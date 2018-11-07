package org.oagi.srt.gateway.http.api.bie_management.service.generate_expression;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableMap;
import org.oagi.srt.data.*;
import org.oagi.srt.gateway.http.api.bie_management.data.expression.GenerateExpressionOption;
import org.oagi.srt.gateway.http.helper.SrtGuid;
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
import java.util.*;
import java.util.stream.Collectors;

import static org.oagi.srt.gateway.http.api.bie_management.service.generate_expression.Helper.*;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class BieJSONGenerateExpression implements BieGenerateExpression, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // In schema version draft-04, it used "id" for dereferencing.
    // However, in draft-06, it changes to "$id".
    private static final String ID_KEYWORD = "id";

    private ObjectMapper mapper;
    private Map<String, Object> root;
    private GenerateExpressionOption option;
    
    @Autowired
    private ApplicationContext applicationContext;

    private GenerationContext generationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void generate(TopLevelAbie topLevelAbie, GenerateExpressionOption option) {
        this.option = option;
        this.generationContext = applicationContext.getBean(GenerationContext.class, topLevelAbie);

        generateTopLevelAbie(topLevelAbie);
    }
    
    private void generateTopLevelAbie(TopLevelAbie topLevelAbie) {
        ABIE abie = generationContext.findAbie(topLevelAbie.getAbieId());

        ASBIEP asbiep = generationContext.receiveASBIEP(abie);
        ABIE typeAbie = generationContext.queryTargetABIE(asbiep);

        Map<String, Object> definitions;
        if (root == null) {
            root = new LinkedHashMap();
            root.put("$schema", "http://json-schema.org/draft-04/schema#");
            root.put(ID_KEYWORD, "http://www.openapplications.org/oagis/10");

            root.put("required", new ArrayList());
            root.put("additionalProperties", false);

            Map<String, Object> properties = new LinkedHashMap();
            root.put("properties", properties);
            definitions = new LinkedHashMap();
            root.put("definitions", definitions);
        } else {
            definitions = (Map<String, Object>) root.get("definitions");
        }

        fillProperties(root, definitions, asbiep, typeAbie);
    }

    private String _camelCase(String term) {
        return Arrays.stream(term.split(" ")).filter(e -> !StringUtils.isEmpty(e))
                .map(e -> {
                    if (e.length() > 1) {
                        return Character.toUpperCase(e.charAt(0)) + e.substring(1).toLowerCase();
                    } else {
                        return e.toUpperCase();
                    }
                }).collect(Collectors.joining());
    }

    private String camelCase(String... terms) {
        String term = Arrays.stream(terms).map(e -> _camelCase(e)).collect(Collectors.joining());
        if (StringUtils.isEmpty(term)) {
            throw new IllegalArgumentException();
        }

        return Character.toLowerCase(term.charAt(0)) + term.substring(1);
    }

    private void fillProperties(Map<String, Object> parent,
                                Map<String, Object> definitions,
                                ASBIE asbie) {
        ASBIEP asbiep = generationContext.queryAssocToASBIEP(asbie);
        ABIE typeAbie = generationContext.queryTargetABIE2(asbiep);

        ASCC ascc = generationContext.queryBasedASCC(asbie);

        int minVal = asbie.getCardinalityMin();
        int maxVal = asbie.getCardinalityMax();
        // Issue #562
        boolean isArray = (maxVal < 0 || maxVal > 1);
        boolean isNillable = asbie.isNillable();

        ASCCP asccp = generationContext.queryBasedASCCP(asbiep);
        String name = camelCase(asccp.getPropertyTerm());
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
        ((Map<String, Object>) parent.get("properties")).put(name, properties);

        if (option.isBieDefinition()) {
            String definition = asbie.getDefinition();
            if (!StringUtils.isEmpty(definition)) {
                properties.put("description", definition);
            }
        }

        if (isNillable) {
            properties.put("type", Arrays.asList(isArray ? "array" : "object", "null"));
        } else {
            properties.put("type", isArray ? "array" : "object");
        }

        if (isArray) {
            if (minVal > 0) {
                properties.put("minItems", minVal);
            }
            if (maxVal > 0) {
                properties.put("maxItems", maxVal);
            }

            Map<String, Object> items = new LinkedHashMap();
            properties.put("items", items);
            items.put("type", "object");

            properties = items;
        }

        properties.put("required", new ArrayList());
        properties.put("additionalProperties", false);
        properties.put("properties", new LinkedHashMap<String, Object>());

        fillProperties(properties, definitions, typeAbie);

        if (((List) properties.get("required")).isEmpty()) {
            properties.remove("required");
        }
    }

    private void fillProperties(Map<String, Object> parent,
                                Map<String, Object> definitions,
                                ASBIEP asbiep, ABIE abie) {

        ASCCP asccp = generationContext.queryBasedASCCP(asbiep);
        String name = camelCase(asccp.getPropertyTerm());

        List<String> parentRequired = (List<String>) parent.get("required");
        parentRequired.add(name);

        Map<String, Object> properties = new LinkedHashMap();
        if (!parent.containsKey("properties")) {
            parent.put("properties", new LinkedHashMap<String, Object>());
        }
        ((Map<String, Object>) parent.get("properties")).put(name, properties);

        if (option.isBieDefinition()) {
            String definition = abie.getDefinition();
            if (!StringUtils.isEmpty(definition)) {
                properties.put("description", definition);
            }
        }

        /*
         * Issue #550
         */
        boolean isArray = option.isArrayForJsonExpression();
        properties.put("type", (isArray) ? "array" : "object");

        /*
         * Issue #575
         */
        if (isArray) {
            Map<String, Object> items = new LinkedHashMap();
            properties.put("items", items);
            items.put("type", "object");

            properties = items;
        }

        properties.put("required", new ArrayList());
        properties.put("additionalProperties", false);

        fillProperties(properties, definitions, abie);

        if (((List) properties.get("required")).isEmpty()) {
            properties.remove("required");
        }
    }

    private Map<String, Object> toProperties(Xbt xbt) {
        String jbtDraft05Map = xbt.getJbtDraft05Map();
        try {
            return mapper.readValue(jbtDraft05Map, LinkedHashMap.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private String fillDefinitions(Map<String, Object> definitions,
                                   Xbt xbt) {
        String builtInType = xbt.getBuiltinType();
        if (builtInType.startsWith("xsd:")) {
            builtInType = builtInType.substring(4);
        }
        if (!definitions.containsKey(builtInType)) {
            Map<String, Object> content = toProperties(xbt);
            definitions.put(builtInType, content);
        }

        return "#/definitions/" + builtInType;
    }

    private String fillDefinitions(Map<String, Object> definitions,
                                   BBIE bbie, CodeList codeList) {
        DT bdt = generationContext.queryAssocBDT(bbie);
        BdtPriRestri bdtPriRestri =
                generationContext.findBdtPriRestriByBdtIdAndDefaultIsTrue(bdt.getDtId());

        Map<String, Object> properties;
        if (bdtPriRestri.getCodeListId() != 0) {
            properties = new LinkedHashMap();
            properties.put("type", "string");
        } else {
            CdtAwdPriXpsTypeMap cdtAwdPriXpsTypeMap =
                    generationContext.findCdtAwdPriXpsTypeMap(bdtPriRestri.getCdtAwdPriXpsTypeMapId());
            Xbt xbt = generationContext.findXbt(cdtAwdPriXpsTypeMap.getXbtId());
            properties = toProperties(xbt);
        }

        return fillDefinitions(properties, definitions, codeList);
    }

    private String fillDefinitions(Map<String, Object> definitions,
                                   BBIESC bbieSc, CodeList codeList) {
        DTSC dtSc = generationContext.findDtSc(bbieSc.getDtScId());
        BdtScPriRestri bdtScPriRestri =
                generationContext.findBdtScPriRestriByBdtScIdAndDefaultIsTrue(dtSc.getDtScId());

        Map<String, Object> properties;
        if (bdtScPriRestri.getCodeListId() != 0) {
            properties = new LinkedHashMap();
            properties.put("type", "string");
        } else {
            CdtScAwdPriXpsTypeMap cdtScAwdPriXpsTypeMap =
                    generationContext.findCdtScAwdPriXpsTypeMap(bdtScPriRestri.getCdtScAwdPriXpsTypeMapId());
            Xbt xbt = generationContext.findXbt(cdtScAwdPriXpsTypeMap.getXbtId());
            properties = toProperties(xbt);
        }

        return fillDefinitions(properties, definitions, codeList);
    }

    private String fillDefinitions(Map<String, Object> properties,
                                   Map<String, Object> definitions,
                                   CodeList codeList) {
        String codeListName = camelCase(getCodeListTypeName(codeList));
        if (!definitions.containsKey(codeListName)) {
            List<CodeListValue> codeListValues = generationContext.getCodeListValues(codeList);
            List<String> enumerations = codeListValues.stream().map(e -> e.getValue()).collect(Collectors.toList());
            properties.put("enum", enumerations);

            definitions.put(codeListName, properties);
        }

        return "#/definitions/" + codeListName;
    }

    private String fillDefinitions(Map<String, Object> definitions,
                                   AgencyIdList agencyIdList) {
        AgencyIdListValue agencyIdListValue =
                generationContext.findAgencyIdListValue(agencyIdList.getAgencyIdListValueId());
        String agencyListTypeName = camelCase(getAgencyListTypeName(agencyIdList, agencyIdListValue));
        if (!definitions.containsKey(agencyListTypeName)) {
            Map<String, Object> properties = new LinkedHashMap();
            properties.put("type", "string");

            List<AgencyIdListValue> agencyIdListValues =
                    generationContext.findAgencyIdListValueByOwnerListId(agencyIdList.getAgencyIdListId());
            List<String> enumerations = agencyIdListValues.stream().map(e -> e.getValue()).collect(Collectors.toList());
            properties.put("enum", enumerations);

            definitions.put(agencyListTypeName, properties);
        }

        return "#/definitions/" + agencyListTypeName;
    }

    private void fillProperties(Map<String, Object> parent,
                                Map<String, Object> definitions,
                                ABIE abie) {

        List<BIE> children = generationContext.queryChildBIEs(abie);
        for (BIE bie : children) {
            if (bie instanceof BBIE) {
                BBIE bbie = (BBIE) bie;
                fillProperties(parent, definitions, bbie);
            } else {
                ASBIE asbie = (ASBIE) bie;
                if (isAnyProperty(asbie, generationContext)) {
                    parent.put("additionalProperties", true);
                } else {
                    fillProperties(parent, definitions, asbie);
                }
            }
        }
    }

    private void fillProperties(Map<String, Object> parent,
                                Map<String, Object> definitions,
                                BBIE bbie) {
        BCC bcc = generationContext.queryBasedBCC(bbie);
        BCCP bccp = generationContext.queryToBCCP(bcc);
        DT bdt = generationContext.queryBDT(bccp);

        int minVal = bbie.getCardinalityMin();
        int maxVal = bbie.getCardinalityMax();
        // Issue #562
        boolean isArray = (maxVal < 0 || maxVal > 1);

        /*
         * When a bbie is based on a bcc, whose entity type is 'attribute',
         * XML schema generation shouldn't generate the nillable="true",
         * even if the user specified the bbie to be nillable.
         */
        boolean isNillable;
        if (bcc.getEntityType() == BCCEntityType.Attribute.getValue()) {
            isNillable = false;
        } else {
            isNillable = bbie.isNillable();
        }

        String name = camelCase(bccp.getPropertyTerm());

        Map<String, Object> properties = new LinkedHashMap();
        if (!parent.containsKey("properties")) {
            parent.put("properties", new LinkedHashMap<String, Object>());
        }
        ((Map<String, Object>) parent.get("properties")).put(name, properties);

        if (minVal > 0) {
            List<String> parentRequired = (List<String>) parent.get("required");
            if (parentRequired == null) {
                throw new IllegalStateException();
            }
            parentRequired.add(name);
        }

        if (option.isBieDefinition()) {
            String definition = bbie.getDefinition();
            if (!StringUtils.isEmpty(definition)) {
                properties.put("description", definition);
            }
        }

        if (isNillable) {
            properties.put("type", Arrays.asList(isArray ? "array" : "object", "null"));
        } else {
            properties.put("type", isArray ? "array" : "object");
        }

        if (isArray) {
            if (minVal > 0) {
                properties.put("minItems", minVal);
            }
            if (maxVal > 0) {
                properties.put("maxItems", maxVal);
            }

            properties.put("additionalItems", false);

            Map<String, Object> items = new LinkedHashMap();
            properties.put("items", items);
            items.put("type", "object");

            properties = items;
        }

        properties.put("required", new ArrayList());
        properties.put("additionalProperties", false);
        properties.put("properties", new LinkedHashMap<String, Object>());

        // Issue #564
        String ref = getReference(definitions, bbie, bdt);
        List<BBIESC> bbieScList = generationContext.queryBBIESCs(bbie)
                .stream().filter(e -> e.getCardinalityMax() != 0).collect(Collectors.toList());
        if (bbieScList.isEmpty()) {
            properties.remove("type");
            properties.put("$ref", ref);
        } else {
            properties.put("required", new ArrayList());
            properties.put("additionalProperties", false);
            properties.put("properties", new LinkedHashMap<String, Object>());

            ((List<String>) properties.get("required")).add("content");
            ((Map<String, Object>) properties.get("properties"))
                    .put("content", ImmutableMap.<String, Object>builder()
                            .put("$ref", ref)
                            .build());

            for (BBIESC bbieSc : bbieScList) {
                fillProperties(properties, definitions, bbieSc);
            }
        }
    }

    private String getReference(Map<String, Object> definitions, BBIE bbie, DT bdt) {
        CodeList codeList = getCodeList(generationContext, bbie, bdt);
        String ref;
        if (codeList != null) {
            ref = fillDefinitions(definitions, bbie, codeList);
        } else {
            AgencyIdList agencyIdList = generationContext.getAgencyIdList(bbie);
            if (agencyIdList != null) {
                ref = fillDefinitions(definitions, agencyIdList);
            } else {
                Xbt xbt;
                if (bbie.getBdtPriRestriId() == 0) {
                    BdtPriRestri bdtPriRestri =
                            generationContext.findBdtPriRestriByBdtIdAndDefaultIsTrue(bdt.getDtId());
                    xbt = getXbt(generationContext, bdtPriRestri);
                } else {
                    BdtPriRestri bdtPriRestri =
                            generationContext.findBdtPriRestri(bbie.getBdtPriRestriId());
                    xbt = getXbt(generationContext, bdtPriRestri);
                }

                ref = fillDefinitions(definitions, xbt);
            }
        }

        return ref;
    }

    private void fillProperties(Map<String, Object> parent,
                                Map<String, Object> definitions,
                                BBIESC bbieSc) {
        int minVal = bbieSc.getCardinalityMin();
        int maxVal = bbieSc.getCardinalityMax();
        if (maxVal == 0) {
            return;
        }

        DTSC dtSc = generationContext.findDtSc(bbieSc.getDtScId());
        String name = camelCase(dtSc.getPropertyTerm(), dtSc.getRepresentationTerm());
        Map<String, Object> properties = new LinkedHashMap();
        ((Map<String, Object>) parent.get("properties")).put(name, properties);

        if (minVal > 0) {
            ((List<String>) parent.get("required")).add(name);
        }

        if (option.isBieDefinition()) {
            String definition = bbieSc.getDefinition();
            if (!StringUtils.isEmpty(definition)) {
                properties.put("description", definition);
            }
        }

        CodeList codeList = generationContext.getCodeList(bbieSc);
        String ref;
        if (codeList != null) {
            ref = fillDefinitions(definitions, bbieSc, codeList);
        } else {
            AgencyIdList agencyIdList = generationContext.getAgencyIdList(bbieSc);
            if (agencyIdList != null) {
                ref = fillDefinitions(definitions, agencyIdList);
            } else {
                Xbt xbt;
                if (bbieSc.getDtScPriRestriId() == 0L) {
                    BdtScPriRestri bdtScPriRestri =
                            generationContext.findBdtScPriRestriByBdtScIdAndDefaultIsTrue(dtSc.getDtScId());
                    CdtScAwdPriXpsTypeMap cdtScAwdPriXpsTypeMap =
                            generationContext.findCdtScAwdPriXpsTypeMap(bdtScPriRestri.getCdtScAwdPriXpsTypeMapId());
                    xbt = generationContext.findXbt(cdtScAwdPriXpsTypeMap.getXbtId());
                } else {
                    BdtScPriRestri bdtScPriRestri =
                            generationContext.findBdtScPriRestri(bbieSc.getDtScPriRestriId());
                    CdtScAwdPriXpsTypeMap cdtScAwdPriXpsTypeMap =
                            generationContext.findCdtScAwdPriXpsTypeMap(bdtScPriRestri.getCdtScAwdPriXpsTypeMapId());
                    xbt = generationContext.findXbt(cdtScAwdPriXpsTypeMap.getXbtId());
                }

                ref = fillDefinitions(definitions, xbt);
            }
        }

        properties.put("$ref", ref);
    }

    @Override
    public File asFile(String filename) throws IOException {
        ensureRoot();

        File tempFile = File.createTempFile(SrtGuid.randomGuid(), null);
        tempFile = new File(tempFile.getParentFile(), filename + ".json");

        mapper.writeValue(tempFile, root);
        logger.info("JSON Schema is generated: " + tempFile);

        return tempFile;
    }

    private void ensureRoot() {
        if (root == null) {
            throw new IllegalStateException();
        }

        // The "required" property for the root element of schema should has only one child.
        if (((List<String>) root.get("required")).size() > 1) {
            root.remove("required");
        }

        //
        Map<String, Object> properties = (Map<String, Object>) root.get("properties");
        for (String key : properties.keySet()) {
            Map<String, Object> copied = new LinkedHashMap();
            copied.put(ID_KEYWORD, "#" + key);
            copied.putAll(((Map<String, Object>) properties.get(key)));
            properties.put(key, copied);
        }
    }
}
