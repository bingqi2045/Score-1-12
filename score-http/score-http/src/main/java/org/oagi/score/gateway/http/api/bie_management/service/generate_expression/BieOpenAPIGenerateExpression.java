package org.oagi.score.gateway.http.api.bie_management.service.generate_expression;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.ImmutableMap;
import org.oagi.score.common.util.OagisComponentType;
import org.oagi.score.data.*;
import org.oagi.score.gateway.http.api.bie_management.data.expression.GenerateExpressionOption;
import org.oagi.score.gateway.http.api.bie_management.data.expression.OpenAPIExpressionFormat;
import org.oagi.score.gateway.http.helper.ScoreGuid;
import org.oagi.score.repository.TopLevelAsbiepRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.oagi.score.gateway.http.api.bie_management.service.generate_expression.Helper.camelCase;
import static org.oagi.score.gateway.http.api.bie_management.service.generate_expression.Helper.convertIdentifierToId;
import static org.oagi.score.gateway.http.helper.ScoreGuid.getGuidWithPrefix;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class BieOpenAPIGenerateExpression implements BieGenerateExpression, InitializingBean {

    private static final String OPEN_API_VERSION = "3.0.3";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private ObjectMapper mapper;
    private ObjectMapper expressionMapper;

    private Map<String, Object> root;
    private GenerateExpressionOption option;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TopLevelAsbiepRepository topLevelAsbiepRepository;

    private GenerationContext generationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        root = null;
    }

    @Override
    public void reset() throws Exception {
        this.afterPropertiesSet();
    }

    @Override
    public GenerationContext generateContext(List<TopLevelAsbiep> topLevelAsbieps, GenerateExpressionOption option) {
        List<TopLevelAsbiep> mergedTopLevelAsbieps = new ArrayList(topLevelAsbieps);

        if (mergedTopLevelAsbieps.size() == 0) {
            throw new IllegalArgumentException("Cannot found BIEs.");
        }
        BigInteger releaseId = mergedTopLevelAsbieps.get(0).getReleaseId();
        /*
         * Issue #587
         */
        if (option.isIncludeMetaHeaderForJsonForOpenAPI30GetTemplate()) {
            TopLevelAsbiep getMetaHeaderTopLevelAsbiep =
                    topLevelAsbiepRepository.findById(option.getMetaHeaderTopLevelAsbiepIdForOpenAPI30GetTemplate());
            if (!releaseId.equals(getMetaHeaderTopLevelAsbiep.getReleaseId())) {
                throw new IllegalArgumentException("Meta Header release does not match.");
            }
            mergedTopLevelAsbieps.add(getMetaHeaderTopLevelAsbiep);
        }
        if (option.isIncludeMetaHeaderForJsonForOpenAPI30PostTemplate()) {
            TopLevelAsbiep postMetaHeaderTopLevelAsbiep =
                    topLevelAsbiepRepository.findById(option.getMetaHeaderTopLevelAsbiepIdForOpenAPI30PostTemplate());
            if (!releaseId.equals(postMetaHeaderTopLevelAsbiep.getReleaseId())) {
                throw new IllegalArgumentException("Meta Header release does not match.");
            }
            mergedTopLevelAsbieps.add(postMetaHeaderTopLevelAsbiep);
        }
        if (option.isIncludePaginationResponseForJsonForOpenAPI30GetTemplate()) {
            TopLevelAsbiep paginationResponseTopLevelAsbiep =
                    topLevelAsbiepRepository.findById(option.getPaginationResponseTopLevelAsbiepIdForOpenAPI30GetTemplate());
            if (!releaseId.equals(paginationResponseTopLevelAsbiep.getReleaseId())) {
                throw new IllegalArgumentException("Pagination Response release does not match.");
            }
            mergedTopLevelAsbieps.add(paginationResponseTopLevelAsbiep);
        }

        return applicationContext.getBean(GenerationContext.class, mergedTopLevelAsbieps);
    }

    @Override
    public void generate(TopLevelAsbiep topLevelAsbiep, GenerationContext generationContext, GenerateExpressionOption option) {
        this.generationContext = generationContext;
        this.option = option;

        OpenAPIExpressionFormat openAPIExpressionFormat;
        if (!StringUtils.hasLength(this.option.getOpenAPIExpressionFormat())) {
            openAPIExpressionFormat = OpenAPIExpressionFormat.YAML;
        } else {
            openAPIExpressionFormat = OpenAPIExpressionFormat.valueOf(this.option.getOpenAPIExpressionFormat());
        }

        switch (openAPIExpressionFormat) {
            case YAML:
                expressionMapper = new ObjectMapper(new YAMLFactory());
                break;
            case JSON:
                expressionMapper = new ObjectMapper();
                break;
        }
        expressionMapper.enable(SerializationFeature.INDENT_OUTPUT);

        generateTopLevelAsbiep(topLevelAsbiep);
    }

    private boolean isFriendly() {
        return this.option.isOpenAPICodeGenerationFriendly();
    }

    private String getPathName(TopLevelAsbiep topLevelAsbiep) {
        // Issue #1308
        StringBuilder pathName = new StringBuilder();
        pathName.append("/");

        BigInteger bizCtxId = option.getBizCtxIds().get(topLevelAsbiep.getTopLevelAsbiepId());
        BizCtx bizCtx = generationContext.findBusinessContexts(topLevelAsbiep).stream()
                .filter(e -> e.getBizCtxId().equals(bizCtxId))
                .findAny().orElse(null);

        String bizCtxName = bizCtx.getName();
        // RESTful Web API Design V2 document
        // [R85] For URI path segments, consisting of more than a single word, a hyphen character "-"
        // SHOULD be used to separate the words.
        String delimiter = "-";
        bizCtxName = bizCtxName.toLowerCase()
                .replaceAll("\\s", delimiter)
                .replaceAll("[^A-Za-z0-9]", delimiter);
        pathName.append(bizCtxName).append("/");

        String version = topLevelAsbiep.getVersion();
        if (StringUtils.hasLength(version)) {
            version = version.toLowerCase()
                    .replaceAll("\\s", "")
                    .replaceAll("[^A-Za-z0-9]", delimiter);
            pathName.append(version).append("/");
        }

        String bieName = getBieName(topLevelAsbiep, s -> convertIdentifierToId(s
                .replaceAll("\\s", delimiter)
                .replaceAll("[^A-Za-z0-9]", delimiter)));
        pathName.append(bieName);

        // RESTful Web API Design V2 document
        // [R87] For the URI path, lower case letters SHOULD be used.
        return pathName.toString().toLowerCase();
    }

    private String getBieName(TopLevelAsbiep topLevelAsbiep) {
        return getBieName(topLevelAsbiep, s -> convertIdentifierToId(camelCase(s)));
    }

    private String getBieName(TopLevelAsbiep topLevelAsbiep, Function<String, String> replacer) {
        ASBIEP asbiep = generationContext.findASBIEP(topLevelAsbiep.getAsbiepId(), topLevelAsbiep);
        ASCCP basedAsccp = generationContext.findASCCP(asbiep.getBasedAsccpManifestId());
        return replacer.apply(basedAsccp.getPropertyTerm());
    }

    private void generateTopLevelAsbiep(TopLevelAsbiep topLevelAsbiep) {
        ASBIEP asbiep = generationContext.findASBIEP(topLevelAsbiep.getAsbiepId(), topLevelAsbiep);
        generationContext.referenceCounter().increase(asbiep);
        try {
            ABIE typeAbie = generationContext.queryTargetABIE(asbiep);
            Release release = generationContext.findRelease(topLevelAsbiep.getReleaseId());

            Map<String, Object> paths;
            Map<String, Object> schemas = new LinkedHashMap();
            if (root == null) {
                root = new LinkedHashMap();
                root.put("openapi", OPEN_API_VERSION);
                root.put("info", ImmutableMap.<String, Object>builder()
                        .put("title", "")
                        .put("description", "")
                        .put("contact", ImmutableMap.<String, Object>builder()
                                .put("name", "")
                                .put("url", "")
                                .put("email", "example@example.org")
                                .build())
                        .put("version", "")
                        .put("x-oagis-release", release.getReleaseNum())
                        .put("x-oagis-release-date", new SimpleDateFormat("yyyy-MM-dd").format(release.getLastUpdateTimestamp()))
                        .put("x-oagis-license", StringUtils.hasLength(release.getReleaseLicense()) ? release.getReleaseLicense() : "")
                        .build());

                paths = new LinkedHashMap();
                root.put("paths", paths);
                root.put("components", ImmutableMap.<String, Object>builder()
                        .put("schemas", schemas)
                        .build()
                );
            } else {
                paths = (Map<String, Object>) root.get("paths");
                schemas = (Map<String, Object>) ((Map<String, Object>) root.get("components")).get("schemas");
            }

            Map<String, Object> path = new LinkedHashMap();
            ASCCP basedAsccp = generationContext.findASCCP(asbiep.getBasedAsccpManifestId());
            String bieName = getBieName(topLevelAsbiep);
            String pathName = getPathName(topLevelAsbiep);
            paths.put(pathName, path);

            path.put("summary", "");
            path.put("description", "");

            if (option.isOpenAPI30GetTemplate()) {
                String schemaName;
                String prefix = "";
                // Issue #1302
                if (option.hasOpenAPI30GetTemplateOptions() || option.hasOpenAPI30PostTemplateOptions()) {
                    prefix = "get-";
                }
                if (isFriendly()) {
                    schemaName = prefix + bieName;
                } else {
                    schemaName = prefix + bieName + "-" + getGuidWithPrefix(typeAbie.getGuid());
                }
                path.put("get", ImmutableMap.<String, Object>builder()
                        .put("summary", "")
                        .put("description", "")
                        .put("tags", Arrays.asList(basedAsccp.getPropertyTerm()))
                        .put("parameters", Arrays.asList(
                                ImmutableMap.<String, Object>builder()
                                        .put("name", "id")
                                        .put("in", "query")
                                        .put("description", "")
                                        .put("required", false)
                                        .put("schema", (isFriendly()) ? ImmutableMap.<String, Object>builder()
                                                .put("type", "integer")
                                                .build() : ImmutableMap.<String, Object>builder()
                                                .put("$ref", "#/components/schemas/integer")
                                                .build())
                                        .build()
                        ))
                        .put("responses", ImmutableMap.<String, Object>builder()
                                .put("200", ImmutableMap.<String, Object>builder()
                                        .put("description", "")
                                        .put("content", ImmutableMap.<String, Object>builder()
                                                .put("application/json", ImmutableMap.<String, Object>builder()
                                                        .put("schema", ImmutableMap.<String, Object>builder()
                                                                .put("$ref", "#/components/schemas/" + schemaName)
                                                                .build())
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .build());

                if (!isFriendly() && !schemas.containsKey("integer")) {
                    schemas.put("integer", ImmutableMap.<String, Object>builder()
                            .put("type", "integer")
                            .build());
                }

                if (!schemas.containsKey(schemaName)) {
                    Map<String, Object> properties = new LinkedHashMap();
                    // Issue #1148
                    properties.put("x-oagis-bie-guid", getGuidWithPrefix(typeAbie.getGuid()));
                    properties.put("x-oagis-bie-date-time", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(typeAbie.getLastUpdateTimestamp()));
                    properties.put("x-oagis-bie-version", StringUtils.hasLength(topLevelAsbiep.getVersion()) ? topLevelAsbiep.getVersion() : "");
                    properties.put("required", new ArrayList());
                    properties.put("additionalProperties", false);

                    fillPropertiesForGetTemplate(properties, schemas, asbiep, typeAbie, generationContext);
                    schemas.put(schemaName, properties);
                }
            }

            if (option.isOpenAPI30PostTemplate()) {
                String schemaName;
                String prefix = "";
                // Issue #1302
                if (option.hasOpenAPI30GetTemplateOptions() || option.hasOpenAPI30PostTemplateOptions()) {
                    prefix = "post-";
                }
                if (isFriendly()) {
                    schemaName = prefix + bieName;
                } else {
                    schemaName = prefix + bieName + "-" + getGuidWithPrefix(typeAbie.getGuid());
                }
                path.put("post", ImmutableMap.<String, Object>builder()
                        .put("summary", "")
                        .put("description", "")
                        .put("tags", Arrays.asList(basedAsccp.getPropertyTerm()))
                        .put("requestBody", ImmutableMap.<String, Object>builder()
                                .put("description", "")
                                .put("content", ImmutableMap.<String, Object>builder()
                                        .put("application/json", ImmutableMap.<String, Object>builder()
                                                .put("schema", ImmutableMap.<String, Object>builder()
                                                        .put("$ref", "#/components/schemas/" + schemaName)
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .put("responses", ImmutableMap.<String, Object>builder()
                                .put("200", ImmutableMap.<String, Object>builder()
                                        .put("description", "")
                                        .put("content", ImmutableMap.<String, Object>builder()
                                                .put("application/json", ImmutableMap.<String, Object>builder()
                                                        .build())
                                                .build())
                                        .build())
                                .build())
                        .build());

                if (!schemas.containsKey(schemaName)) {
                    Map<String, Object> properties = new LinkedHashMap();
                    // Issue #1148
                    properties.put("x-oagis-bie-guid", getGuidWithPrefix(typeAbie.getGuid()));
                    properties.put("x-oagis-bie-date-time", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(typeAbie.getLastUpdateTimestamp()));
                    properties.put("x-oagis-bie-version", StringUtils.hasLength(topLevelAsbiep.getVersion()) ? topLevelAsbiep.getVersion() : "");
                    properties.put("required", new ArrayList());
                    properties.put("additionalProperties", false);

                    fillPropertiesForPostTemplate(properties, schemas, asbiep, typeAbie, generationContext);
                    schemas.put(schemaName, properties);
                }
            }
        } finally {
            generationContext.referenceCounter().decrease(asbiep);
        }
    }

    private void fillPropertiesForGetTemplate(Map<String, Object> parent,
                                              Map<String, Object> schemas,
                                              ASBIEP asbiep, ABIE abie,
                                              GenerationContext generationContext) {
        /*
         * Issue #587
         */
        if (option.isIncludeMetaHeaderForJsonForOpenAPI30GetTemplate()) {
            TopLevelAsbiep metaHeaderTopLevelAsbiep =
                    topLevelAsbiepRepository.findById(option.getMetaHeaderTopLevelAsbiepIdForOpenAPI30GetTemplate());
            fillProperties(parent, schemas, metaHeaderTopLevelAsbiep, generationContext);
        }
        if (option.isIncludePaginationResponseForJsonForOpenAPI30GetTemplate()) {
            TopLevelAsbiep paginationResponseTopLevelAsbiep =
                    topLevelAsbiepRepository.findById(option.getPaginationResponseTopLevelAsbiepIdForOpenAPI30GetTemplate());
            fillProperties(parent, schemas, paginationResponseTopLevelAsbiep, generationContext);
        }

        fillProperties(parent, schemas, asbiep, abie,
                option.isArrayForJsonExpressionForOpenAPI30GetTemplate(), generationContext);
    }

    private void fillPropertiesForPostTemplate(Map<String, Object> parent,
                                               Map<String, Object> schemas,
                                               ASBIEP asbiep, ABIE abie,
                                               GenerationContext generationContext) {
        /*
         * Issue #587
         */
        if (option.isIncludeMetaHeaderForJsonForOpenAPI30PostTemplate()) {
            TopLevelAsbiep metaHeaderTopLevelAsbiep =
                    topLevelAsbiepRepository.findById(option.getMetaHeaderTopLevelAsbiepIdForOpenAPI30PostTemplate());
            fillProperties(parent, schemas, metaHeaderTopLevelAsbiep, generationContext);
        }

        fillProperties(parent, schemas, asbiep, abie,
                option.isArrayForJsonExpressionForOpenAPI30PostTemplate(), generationContext);
    }

    private void fillProperties(Map<String, Object> parent, Map<String, Object> schemas,
                                TopLevelAsbiep topLevelAsbiep,
                                GenerationContext generationContext) {

        ASBIEP asbiep = generationContext.findASBIEP(topLevelAsbiep.getAsbiepId(), topLevelAsbiep);
        ABIE typeAbie = generationContext.queryTargetABIE(asbiep);

        fillProperties(parent, schemas, asbiep, typeAbie, false, generationContext);
    }

    private void fillProperties(Map<String, Object> parent,
                                Map<String, Object> schemas,
                                ASBIE asbie,
                                GenerationContext generationContext) {
        ASBIEP asbiep = generationContext.queryAssocToASBIEP(asbie);
        ABIE typeAbie = generationContext.queryTargetABIE(asbiep);

        ASCC ascc = generationContext.queryBasedASCC(asbie);

        int minVal = asbie.getCardinalityMin();
        int maxVal = asbie.getCardinalityMax();
        // Issue #562
        boolean isArray = (maxVal < 0 || maxVal > 1);
        boolean isNillable = asbie.isNillable();

        ASCCP asccp = generationContext.queryBasedASCCP(asbiep);
        String name = convertIdentifierToId(camelCase(asccp.getPropertyTerm()));
        if (minVal > 0) {
            List<String> parentRequired = (List<String>) parent.get("required");
            if (parentRequired == null) {
                throw new IllegalStateException();
            }
            parentRequired.add(name);
        }

        Map<String, Object> properties = new LinkedHashMap();
        if (!parent.containsKey("properties")) {
            parent.put("properties", new LinkedHashMap<String, Object>());
        }

        if (option.isBieDefinition()) {
            String definition = asbie.getDefinition();
            if (StringUtils.hasLength(definition)) {
                properties.put("description", definition);
            }
        }

        properties.put("type", "object");
        properties.put("required", new ArrayList());
        properties.put("additionalProperties", false);
        properties.put("properties", new LinkedHashMap<String, Object>());

        fillProperties(properties, schemas, typeAbie, generationContext);

        if (properties.containsKey("required") && ((List) properties.get("required")).isEmpty()) {
            properties.remove("required");
        }

        properties = oneOf(allOf(properties), isNillable);

        if (isArray) {
            Map<String, Object> items = new LinkedHashMap();
            items.putAll(properties);

            properties = new LinkedHashMap();

            String description = (String) items.remove("description");
            if (StringUtils.hasLength(description)) {
                properties.put("description", description);
            }
            properties.put("type", "array");
            if (minVal > 0) {
                properties.put("minItems", minVal);
            }
            if (maxVal > 0) {
                properties.put("maxItems", maxVal);
            }
            properties.put("items", items);
        }

        ((Map<String, Object>) parent.get("properties")).put(name, properties);
    }

    private void fillProperties(Map<String, Object> parent,
                                Map<String, Object> schemas,
                                ASBIEP asbiep, ABIE abie,
                                boolean isArray,
                                GenerationContext generationContext) {

        ASCCP asccp = generationContext.queryBasedASCCP(asbiep);
        String name = convertIdentifierToId(camelCase(asccp.getPropertyTerm()));

        List<String> parentRequired = (List<String>) parent.get("required");
        parentRequired.add(name);

        Map<String, Object> properties = new LinkedHashMap();
        if (!parent.containsKey("properties")) {
            parent.put("properties", new LinkedHashMap<String, Object>());
        }

        if (option.isBieDefinition()) {
            String definition = abie.getDefinition();
            if (StringUtils.hasLength(definition)) {
                properties.put("description", definition);
            }
        }

        /*
         * Issue #550
         */
        if (!isArray) {
            properties.put("type", "object");
        }
        properties.put("required", new ArrayList());
        properties.put("additionalProperties", false);

        fillProperties(properties, schemas, abie, generationContext);

        if (properties.containsKey("required") && ((List) properties.get("required")).isEmpty()) {
            properties.remove("required");
        }

        /*
         * Issue #575
         */
        if (isArray) {
            Map<String, Object> items = new LinkedHashMap(properties);
            properties = new LinkedHashMap();

            String description = (String) items.remove("description");
            if (StringUtils.hasLength(description)) {
                properties.put("description", description);
            }
            properties.put("type", "array");
            properties.put("items", items);
        }

        ((Map<String, Object>) parent.get("properties")).put(name, properties);
    }

    private Map<String, Object> toProperties(Xbt xbt) {
        String openapi30Map = xbt.getOpenapi30Map();
        try {
            return mapper.readValue(openapi30Map, LinkedHashMap.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private String fillSchemas(Map<String, Object> schemas,
                               Xbt xbt, FacetRestrictionsAware facetRestri) {
        String guid = (facetRestri instanceof BIE) ? ((BIE) facetRestri).getGuid() : ScoreGuid.randomGuid();
        String name = "type_" + guid;

        Map<String, Object> content = toProperties(xbt);
        if (facetRestri.getMinLength() != null) {
            content.put("minLength", facetRestri.getMinLength().longValue());
        }
        if (facetRestri.getMaxLength() != null) {
            content.put("maxLength", facetRestri.getMaxLength().longValue());
        }
        if (StringUtils.hasLength(facetRestri.getPattern())) {
            content.put("pattern", facetRestri.getPattern());
        }

        schemas.put(name, content);

        return "#/components/schemas/" + name;
    }

    private String fillSchemas(Map<String, Object> schemas,
                               Xbt xbt) {
        String builtInType = xbt.getBuiltinType();
        if (builtInType.startsWith("xsd:")) {
            builtInType = builtInType.substring(4);
        }
        if (!schemas.containsKey(builtInType)) {
            Map<String, Object> content = toProperties(xbt);
            schemas.put(builtInType, content);
        }

        return "#/components/schemas/" + builtInType;
    }

    private String fillSchemas(Map<String, Object> schemas,
                               BBIE bbie, CodeList codeList) {
        BdtPriRestri bdtPriRestri =
                generationContext.findBdtPriRestriByBbieAndDefaultIsTrue(bbie);

        Map<String, Object> properties;
        if (bdtPriRestri.getCodeListManifestId() != null) {
            properties = new LinkedHashMap();
            properties.put("type", "string");
        } else {
            CdtAwdPriXpsTypeMap cdtAwdPriXpsTypeMap =
                    generationContext.findCdtAwdPriXpsTypeMap(bdtPriRestri.getCdtAwdPriXpsTypeMapId());
            Xbt xbt = generationContext.findXbt(cdtAwdPriXpsTypeMap.getXbtId());
            properties = toProperties(xbt);
        }

        return fillSchemas(properties, schemas, codeList);
    }

    private String fillSchemas(Map<String, Object> schemas,
                               BBIESC bbieSc, CodeList codeList) {
        BdtScPriRestri bdtScPriRestri =
                generationContext.findBdtScPriRestriByBbieScAndDefaultIsTrue(bbieSc);

        Map<String, Object> properties;
        if (bdtScPriRestri.getCodeListManifestId() != null) {
            properties = new LinkedHashMap();
            properties.put("type", "string");
        } else {
            CdtScAwdPriXpsTypeMap cdtScAwdPriXpsTypeMap =
                    generationContext.findCdtScAwdPriXpsTypeMap(bdtScPriRestri.getCdtScAwdPriXpsTypeMapId());
            Xbt xbt = generationContext.findXbt(cdtScAwdPriXpsTypeMap.getXbtId());
            properties = toProperties(xbt);
        }

        return fillSchemas(properties, schemas, codeList);
    }

    private String fillSchemas(Map<String, Object> properties,
                               Map<String, Object> schemas,
                               CodeList codeList) {

        AgencyIdListValue agencyIdListValue = generationContext.findAgencyIdListValue(codeList.getAgencyIdListValueId());
        String codeListName = Helper.getCodeListTypeName(codeList, agencyIdListValue);
        /*
         * Issue #589
         */
        codeListName = Stream.of(codeListName.split("_"))
                .map(e -> convertIdentifierToId(camelCase(e))).collect(Collectors.joining("_"));

        if (!schemas.containsKey(codeListName)) {
            List<CodeListValue> codeListValues = generationContext.getCodeListValues(codeList);
            List<String> enumerations = codeListValues.stream().map(e -> e.getValue()).collect(Collectors.toList());
            if (!enumerations.isEmpty()) {
                properties.put("enum", enumerations);
            }

            schemas.put(codeListName, properties);
        }

        return "#/components/schemas/" + codeListName;
    }

    private String fillSchemas(Map<String, Object> schemas,
                               AgencyIdList agencyIdList) {
        AgencyIdListValue agencyIdListValue =
                generationContext.findAgencyIdListValue(agencyIdList.getAgencyIdListValueId());
        String agencyListTypeName = Helper.getAgencyListTypeName(agencyIdList, agencyIdListValue);
        /*
         * Issue #589
         */
        agencyListTypeName = Stream.of(agencyListTypeName.split("_"))
                .map(e -> convertIdentifierToId(camelCase(e))).collect(Collectors.joining("_"));
        if (!schemas.containsKey(agencyListTypeName)) {
            Map<String, Object> properties = new LinkedHashMap();
            properties.put("type", "string");

            List<AgencyIdListValue> agencyIdListValues =
                    generationContext.findAgencyIdListValueByOwnerListId(agencyIdList.getAgencyIdListId());
            List<String> enumerations = agencyIdListValues.stream().map(e -> e.getValue()).collect(Collectors.toList());
            if (!enumerations.isEmpty()) {
                properties.put("enum", enumerations);
            }

            schemas.put(agencyListTypeName, properties);
        }

        return "#/components/schemas/" + agencyListTypeName;
    }

    private void fillProperties(Map<String, Object> parent,
                                Map<String, Object> schemas,
                                ABIE abie,
                                GenerationContext generationContext) {

        List<BIE> children = generationContext.queryChildBIEs(abie);
        ACC acc = generationContext.findACC(abie.getBasedAccManifestId());
        if (OagisComponentType.Choice.getValue() == acc.getOagisComponentType()) {
            List<Object> oneOf = new ArrayList();

            for (BIE bie : children) {
                Map<String, Object> item = new LinkedHashMap();
                item.put("type", "object");
                item.put("required", new ArrayList());
                item.put("additionalProperties", false);
                item.put("properties", new LinkedHashMap<String, Object>());

                fillProperties(item, schemas, bie, generationContext);

                if (item.containsKey("required") && ((List) item.get("required")).isEmpty()) {
                    item.remove("required");
                }
                oneOf.add(item);
            }

            parent.clear();
            parent.put("oneOf", oneOf);
        } else {
            for (BIE bie : children) {
                fillProperties(parent, schemas, bie, generationContext);
            }
        }
    }

    private void fillProperties(Map<String, Object> parent,
                                Map<String, Object> schemas,
                                BIE bie,
                                GenerationContext generationContext) {
        if (bie instanceof BBIE) {
            BBIE bbie = (BBIE) bie;
            fillProperties(parent, schemas, bbie, generationContext);
        } else {
            ASBIE asbie = (ASBIE) bie;
            if (Helper.isAnyProperty(asbie, generationContext)) {
                parent.put("additionalProperties", true);
            } else {
                ASBIEP asbiep = generationContext.queryAssocToASBIEP(asbie);

                generationContext.referenceCounter().increase(asbiep)
                        .ifNotCircularReference(asbiep,
                                () -> fillProperties(parent, schemas, asbie, generationContext))
                        .decrease(asbiep);
            }
        }
    }

    private Object readJsonValue(String textContent) {
        try {
            return mapper.readValue(textContent, Object.class);
        } catch (Exception e) {
            logger.warn("Can't read JSON value from given text: " + textContent, e);
        }
        return null;
    }

    private void fillProperties(Map<String, Object> parent,
                                Map<String, Object> schemas,
                                BBIE bbie,
                                GenerationContext generationContext) {
        BCC bcc = generationContext.queryBasedBCC(bbie);
        BCCP bccp = generationContext.queryToBCCP(bcc);
        DT bdt = generationContext.queryBDT(bccp);

        int minVal = bbie.getCardinalityMin();
        int maxVal = bbie.getCardinalityMax();
        // Issue #562
        boolean isArray = (maxVal < 0 || maxVal > 1);
        boolean isNillable = bbie.isNillable();

        String name = convertIdentifierToId(camelCase(bccp.getPropertyTerm()));

        Map<String, Object> properties = new LinkedHashMap();
        if (!parent.containsKey("properties")) {
            parent.put("properties", new LinkedHashMap<String, Object>());
        }

        if (option.isBieDefinition()) {
            String definition = bbie.getDefinition();
            if (StringUtils.hasLength(definition)) {
                properties.put("description", definition);
            }
        }

        if (minVal > 0) {
            List<String> parentRequired = (List<String>) parent.get("required");
            if (parentRequired == null) {
                throw new IllegalStateException();
            }
            parentRequired.add(name);
        }

        // Issue #700
        if (StringUtils.hasLength(bbie.getFixedValue())) {
            properties.put("enum", Arrays.asList(bbie.getFixedValue()));
        } else if (StringUtils.hasLength(bbie.getDefaultValue())) {
            properties.put("default", bbie.getDefaultValue());
        }

        // Issue #692
        String exampleText = bbie.getExample();
        if (StringUtils.hasLength(exampleText)) {
            properties.put("example", exampleText);
        }

        // Issue #564
        String ref = getReference(schemas, bbie, bdt, generationContext);
        List<BBIESC> bbieScList = generationContext.queryBBIESCs(bbie)
                .stream().filter(e -> e.getCardinalityMax() != 0).collect(Collectors.toList());
        if (bbieScList.isEmpty()) {
            if (ref == null && isFriendly()) {
                Xbt xbt = getXbt(bbie, bdt);
                Map<String, Object> content = toProperties(xbt);
                properties.putAll(content);
            } else {
                properties.put("$ref", ref);
            }
            properties = oneOf(allOf(properties), isNillable);
        } else {
            properties.put("type", "object");
            properties.put("required", new ArrayList());
            properties.put("additionalProperties", false);
            properties.put("properties", new LinkedHashMap<String, Object>());

            Map<String, Object> contentProperties = new LinkedHashMap();
            if (ref == null && isFriendly()) {
                Xbt xbt = getXbt(bbie, bdt);
                Map<String, Object> content = toProperties(xbt);
                contentProperties.putAll(content);
            } else {
                contentProperties.put("$ref", ref);
            }
            for (String key : Arrays.asList("enum", "default", "example")) {
                if (properties.containsKey(key)) {
                    contentProperties.put(key, properties.remove(key));
                }
            }

            ((List<String>) properties.get("required")).add("content");
            ((Map<String, Object>) properties.get("properties"))
                    .put("content", oneOf(allOf(contentProperties), isNillable));

            for (BBIESC bbieSc : bbieScList) {
                fillProperties(properties, schemas, bbieSc, generationContext);
            }
        }

        if (isArray) {
            String description = (String) properties.remove("description");
            Map<String, Object> items = new LinkedHashMap(properties);
            properties = new LinkedHashMap();
            if (StringUtils.hasLength(description)) {
                properties.put("description", description);
            }
            properties.put("type", "array");
            if (minVal > 0) {
                properties.put("minItems", minVal);
            }
            if (maxVal > 0) {
                properties.put("maxItems", maxVal);
            }
            properties.put("items", items);
        }

        ((Map<String, Object>) parent.get("properties")).put(name, properties);
    }

    private Map<String, Object> allOf(Map<String, Object> properties) {
        if (properties.containsKey("$ref") && properties.size() > 1) {
            Map<String, Object> prop = new LinkedHashMap();
            Map<String, Object> refMap = ImmutableMap.<String, Object>builder()
                    .put("$ref", properties.remove("$ref"))
                    .build();
            prop.put("allOf", (properties.isEmpty()) ? Arrays.asList(refMap) : Arrays.asList(refMap, properties));

            return prop;
        }

        return properties;
    }

    private Map<String, Object> oneOf(Map<String, Object> properties,
                                      boolean isNillable) {
        if (isNillable) {
            Map<String, Object> prop = new LinkedHashMap();
            prop.put("oneOf", Arrays.asList(
                    ImmutableMap.builder()
                            .put("nullable", true)
                            .build(),
                    properties
            ));

            return prop;
        }

        return properties;
    }

    private Xbt getXbt(BBIE bbie, DT bdt) {
        if (bbie.getBdtPriRestriId() == null) {
            BdtPriRestri bdtPriRestri =
                    generationContext.findBdtPriRestriByBbieAndDefaultIsTrue(bbie);
            return Helper.getXbt(generationContext, bdtPriRestri);
        } else {
            BdtPriRestri bdtPriRestri =
                    generationContext.findBdtPriRestri(bbie.getBdtPriRestriId());
            return Helper.getXbt(generationContext, bdtPriRestri);
        }
    }

    private String getReference(Map<String, Object> schemas, BBIE bbie, DT bdt,
                                GenerationContext generationContext) {
        CodeList codeList = Helper.getCodeList(generationContext, bbie, bdt);
        String ref;
        if (codeList != null) {
            ref = fillSchemas(schemas, bbie, codeList);
        } else {
            AgencyIdList agencyIdList = generationContext.getAgencyIdList(bbie);
            if (agencyIdList != null) {
                ref = fillSchemas(schemas, agencyIdList);
            } else {
                if (bbie.getMinLength() != null || bbie.getMaxLength() != null || StringUtils.hasLength(bbie.getPattern())) {
                    Xbt xbt = getXbt(bbie, bdt);
                    ref = fillSchemas(schemas, xbt, bbie);
                } else if (!isFriendly()) {
                    Xbt xbt = getXbt(bbie, bdt);
                    ref = fillSchemas(schemas, xbt);
                } else {
                    ref = null;
                }
            }
        }

        return ref;
    }

    private Xbt getXbt(BBIESC bbieSc, DTSC dtSc) {
        if (bbieSc.getDtScPriRestriId() == null) {
            BdtScPriRestri bdtScPriRestri =
                    generationContext.findBdtScPriRestriByBbieScAndDefaultIsTrue(bbieSc);
            CdtScAwdPriXpsTypeMap cdtScAwdPriXpsTypeMap =
                    generationContext.findCdtScAwdPriXpsTypeMap(bdtScPriRestri.getCdtScAwdPriXpsTypeMapId());
            return generationContext.findXbt(cdtScAwdPriXpsTypeMap.getXbtId());
        } else {
            BdtScPriRestri bdtScPriRestri =
                    generationContext.findBdtScPriRestri(bbieSc.getDtScPriRestriId());
            CdtScAwdPriXpsTypeMap cdtScAwdPriXpsTypeMap =
                    generationContext.findCdtScAwdPriXpsTypeMap(bdtScPriRestri.getCdtScAwdPriXpsTypeMapId());
            return generationContext.findXbt(cdtScAwdPriXpsTypeMap.getXbtId());
        }
    }

    private void fillProperties(Map<String, Object> parent,
                                Map<String, Object> schemas,
                                BBIESC bbieSc,
                                GenerationContext generationContext) {
        int minVal = bbieSc.getCardinalityMin();
        int maxVal = bbieSc.getCardinalityMax();
        if (maxVal == 0) {
            return;
        }

        DTSC dtSc = generationContext.findDtSc(bbieSc.getBasedDtScManifestId());
        String name = convertIdentifierToId(toName(dtSc.getPropertyTerm(), dtSc.getRepresentationTerm(), rt -> {
            if ("Text".equals(rt)) {
                return "";
            }
            return rt;
        }, true));
        Map<String, Object> properties = new LinkedHashMap();

        if (option.isBieDefinition()) {
            String definition = bbieSc.getDefinition();
            if (StringUtils.hasLength(definition)) {
                properties.put("description", definition);
            }
        }

        if (minVal > 0) {
            ((List<String>) parent.get("required")).add(name);
        }

        // Issue #596
        if (StringUtils.hasLength(bbieSc.getFixedValue())) {
            properties.put("enum", Arrays.asList(bbieSc.getFixedValue()));
        } else if (StringUtils.hasLength(bbieSc.getDefaultValue())) {
            properties.put("default", bbieSc.getDefaultValue());
        }

        // Issue #692
        String exampleText = bbieSc.getExample();
        if (StringUtils.hasLength(exampleText)) {
            properties.put("example", exampleText);
        }

        CodeList codeList = generationContext.getCodeList(bbieSc);
        String ref;
        if (codeList != null) {
            ref = fillSchemas(schemas, bbieSc, codeList);
        } else {
            AgencyIdList agencyIdList = generationContext.getAgencyIdList(bbieSc);
            if (agencyIdList != null) {
                ref = fillSchemas(schemas, agencyIdList);
            } else {
                if (bbieSc.getMinLength() != null || bbieSc.getMaxLength() != null || StringUtils.hasLength(bbieSc.getPattern())) {
                    Xbt xbt = getXbt(bbieSc, dtSc);
                    ref = fillSchemas(schemas, xbt, bbieSc);
                } else if (!isFriendly()) {
                    Xbt xbt = getXbt(bbieSc, dtSc);
                    ref = fillSchemas(schemas, xbt);
                } else {
                    ref = null;
                }
            }
        }

        if (ref == null && isFriendly()) {
            Xbt xbt = getXbt(bbieSc, dtSc);
            Map<String, Object> content = toProperties(xbt);
            properties.putAll(content);
        } else {
            properties.put("$ref", ref);
        }
        properties = allOf(properties);

        ((Map<String, Object>) parent.get("properties")).put(name, properties);
    }

    private void ensureRoot() {
        if (root == null) {
            throw new IllegalStateException();
        }
    }

    @Override
    public File asFile(String filename) throws IOException {
        ensureRoot();

        File tempFile = File.createTempFile(ScoreGuid.randomGuid(), null);
        String extension = (expressionMapper.getFactory() instanceof YAMLFactory) ? "yml" : "json";

        tempFile = new File(tempFile.getParentFile(), filename + "." + extension);

        expressionMapper.writeValue(tempFile, root);
        logger.info("Open API " + OPEN_API_VERSION + " Schema is generated: " + tempFile);

        return tempFile;
    }
}
