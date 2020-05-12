package org.oagi.srt.repo.domain;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import org.jooq.types.ULong;
import org.oagi.srt.data.BCCEntityType;
import org.oagi.srt.data.DTType;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class RevisionSerializer {

    private Gson gson;

    @Autowired
    private RevisionSnapshotResolver resolver;

    public RevisionSerializer() {
        this.gson = new Gson();
    }

    @SneakyThrows(JsonIOException.class)
    public String serialize(AccRecord accRecord,
                            List<AsccRecord> asccRecords, List<BccRecord> bccRecords) {
        Map<String, Object> properties = new HashMap();

        properties.put("component", "acc");
        properties.put("guid", accRecord.getGuid());
        properties.put("objectClassTerm", accRecord.getObjectClassTerm());
        properties.put("definition", accRecord.getDefinition());
        properties.put("definitionSource", accRecord.getDefinitionSource());
        properties.put("objectClassQualifier", accRecord.getObjectClassQualifier());
        properties.put("componentType", OagisComponentType.valueOf(accRecord.getOagisComponentType()).name());
        properties.put("state", accRecord.getState());
        properties.put("deprecated", ((byte) 1 == accRecord.getIsDeprecated()) ? true : false);
        properties.put("abstract", ((byte) 1 == accRecord.getIsAbstract()) ? true : false);

        properties.put("ownerUserId", (accRecord.getOwnerUserId() != null) ? accRecord.getOwnerUserId() : null);
        properties.put("basedAccId", (accRecord.getBasedAccId() != null) ? accRecord.getBasedAccId() : null);
        properties.put("namespaceId", (accRecord.getNamespaceId() != null) ? accRecord.getNamespaceId() : null);

        List<Map<String, Object>> associations = new ArrayList();
        properties.put("associations", associations);

        for (AssocRecord assocRecord : sort(asccRecords, bccRecords)) {
            Object delegate = assocRecord.getDelegate();
            if (delegate instanceof AsccRecord) {
                associations.add(serialize((AsccRecord) delegate));
            } else {
                associations.add(serialize((BccRecord) delegate));
            }
        }

        return gson.toJson(properties, HashMap.class);
    }

    private void accAddProperties(JsonObject properties) {
        properties.addProperty("den",
                properties.get("objectClassTerm").getAsString() + ". Details"
        );

        if (properties.has("ownerUserId")) {
            BigInteger ownerUserId = properties.get("ownerUserId").getAsJsonObject().get("value").getAsBigInteger();
            properties.addProperty("ownerUserLoginId",
                    resolver.getUserLoginId(ULong.valueOf(ownerUserId))
            );
        }
        if (properties.has("basedAccId")) {
            BigInteger basedAccId = properties.get("basedAccId").getAsJsonObject().get("value").getAsBigInteger();
            properties.addProperty("basedAccObjectClassTerm",
                    resolver.getAccObjectClassTerm(ULong.valueOf(basedAccId))
            );
        }
        if (properties.has("namespaceId")) {
            BigInteger basedAccId = properties.get("namespaceId").getAsJsonObject().get("value").getAsBigInteger();
            properties.addProperty("namespaceUrl",
                    resolver.getNamespaceUrl(ULong.valueOf(basedAccId))
            );
        }

        if (properties.has("associations")) {
            for (JsonElement element : properties.getAsJsonArray("associations")) {
                JsonObject association = element.getAsJsonObject();
                String component = association.get("component").getAsString();
                if ("ascc".equals(component)) {
                    asccAddProperties(properties, association);
                } else if ("bcc".equals(component)) {
                    bccAddProperties(properties, association);
                }
            }
        }
    }

    private List<AssocRecord> sort(List<AsccRecord> asccRecords, List<BccRecord> bccRecords) {
        List<AssocRecord> assocRecords = new ArrayList();
        assocRecords.addAll(
                asccRecords.stream().map(e -> new AssocRecord(e)).collect(Collectors.toList())
        );
        assocRecords.addAll(
                bccRecords.stream().map(e -> new AssocRecord(e)).collect(Collectors.toList())
        );
        Collections.sort(assocRecords, (o1, o2) -> {
            int comp = Integer.compare(o1.getSeqKey(), o2.getSeqKey());
            if (comp == 0) {
                return o1.getTimestamp().compareTo(o2.getTimestamp());
            }
            return comp;
        });
        return assocRecords;
    }

    private class AssocRecord {

        private int seqKey;
        private LocalDateTime timestamp;
        private Object delegate;

        AssocRecord(AsccRecord ascc) {
            this.seqKey = ascc.getSeqKey();
            this.timestamp = ascc.getLastUpdateTimestamp();
            this.delegate = ascc;
        }

        AssocRecord(BccRecord bcc) {
            this.seqKey = bcc.getSeqKey();
            this.timestamp = bcc.getLastUpdateTimestamp();
            this.delegate = bcc;
        }

        public int getSeqKey() {
            return this.seqKey;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public Object getDelegate() {
            return this.delegate;
        }
    }

    @SneakyThrows(JsonIOException.class)
    public Map<String, Object> serialize(AsccRecord asccRecord) {
        Map<String, Object> properties = new HashMap();

        properties.put("component", "ascc");
        properties.put("guid", asccRecord.getGuid());
        properties.put("cardinalityMin", asccRecord.getCardinalityMin());
        properties.put("cardinalityMax", asccRecord.getCardinalityMax());
        properties.put("definition", asccRecord.getDefinition());
        properties.put("definitionSource", asccRecord.getDefinitionSource());
        properties.put("deprecated", ((byte) 1 == asccRecord.getIsDeprecated()) ? true : false);
        properties.put("state", asccRecord.getState());

        properties.put("toAsccpId", (asccRecord.getToAsccpId() != null) ? asccRecord.getToAsccpId().toBigInteger() : null);

        return properties;
    }

    private void asccAddProperties(JsonObject accProperties,
                                   JsonObject properties) {

        properties.addProperty("den",
                accProperties.get("objectClassTerm").getAsString() + ". " +
                        resolver.getAsccpDen(
                                ULong.valueOf(properties.get("toAsccpId").getAsBigInteger())
                        )
        );
        properties.addProperty("toAsccpPropertyTerm",
                resolver.getAsccpPropertyTerm(
                        ULong.valueOf(properties.get("toAsccpId").getAsBigInteger())
                )
        );
    }

    @SneakyThrows(JsonIOException.class)
    public Map<String, Object> serialize(BccRecord bccRecord) {
        Map<String, Object> properties = new HashMap();

        properties.put("component", "bcc");
        properties.put("guid", bccRecord.getGuid());
        properties.put("cardinalityMin", bccRecord.getCardinalityMin());
        properties.put("cardinalityMax", bccRecord.getCardinalityMax());
        properties.put("entityType", BCCEntityType.valueOf(bccRecord.getEntityType()).name());
        properties.put("definition", bccRecord.getDefinition());
        properties.put("definitionSource", bccRecord.getDefinitionSource());
        properties.put("defaultValue", bccRecord.getDefaultValue());
        properties.put("fixedValue", bccRecord.getFixedValue());
        properties.put("deprecated", ((byte) 1 == bccRecord.getIsDeprecated()) ? true : false);
        properties.put("nillable", ((byte) 1 == bccRecord.getIsNillable()) ? true : false);
        properties.put("state", bccRecord.getState());

        properties.put("toBccpId", (bccRecord.getToBccpId() != null) ? bccRecord.getToBccpId().toBigInteger() : null);

        return properties;
    }

    private void bccAddProperties(JsonObject accProperties,
                                  JsonObject properties) {

        properties.addProperty("den",
                accProperties.get("objectClassTerm").getAsString() + ". " +
                        resolver.getBccpDen(
                                ULong.valueOf(properties.get("toBccpId").getAsBigInteger())
                        )
        );
        properties.addProperty("toBccpPropertyTerm",
                resolver.getBccpPropertyTerm(
                        ULong.valueOf(properties.get("toBccpId").getAsBigInteger())
                )
        );
    }

    @SneakyThrows(JsonIOException.class)
    public String serialize(AsccpRecord asccpRecord) {
        Map<String, Object> properties = new HashMap();

        properties.put("component", "asccp");
        properties.put("guid", asccpRecord.getGuid());
        properties.put("propertyTerm", asccpRecord.getPropertyTerm());
        properties.put("definition", asccpRecord.getDefinition());
        properties.put("definitionSource", asccpRecord.getDefinitionSource());
        properties.put("state", asccpRecord.getState());
        properties.put("reusable", ((byte) 1 == asccpRecord.getReusableIndicator()) ? true : false);
        properties.put("deprecated", ((byte) 1 == asccpRecord.getIsDeprecated()) ? true : false);
        properties.put("nillable", ((byte) 1 == asccpRecord.getIsNillable()) ? true : false);

        properties.put("ownerUserId", (asccpRecord.getOwnerUserId() != null) ? asccpRecord.getOwnerUserId() : null);
        properties.put("roleOfAccId", (asccpRecord.getRoleOfAccId() != null) ? asccpRecord.getRoleOfAccId() : null);
        properties.put("namespaceId", (asccpRecord.getNamespaceId() != null) ? asccpRecord.getNamespaceId() : null);

        return gson.toJson(properties, HashMap.class);
    }

    private void asccpAddProperties(JsonObject properties) {
        if (properties.has("ownerUserId")) {
            BigInteger ownerUserId = properties.get("ownerUserId").getAsJsonObject().get("value").getAsBigInteger();
            properties.addProperty("ownerUserLoginId",
                    resolver.getUserLoginId(ULong.valueOf(ownerUserId))
            );
        }
        if (properties.has("roleOfAccId")) {
            BigInteger roleOfAccId = properties.get("roleOfAccId").getAsJsonObject().get("value").getAsBigInteger();
            properties.addProperty("roleOfAccObjectClassTerm",
                    resolver.getAccObjectClassTerm(ULong.valueOf(roleOfAccId))
            );
        }
        if (properties.has("namespaceId")) {
            BigInteger basedAccId = properties.get("namespaceId").getAsJsonObject().get("value").getAsBigInteger();
            properties.addProperty("namespaceUrl",
                    resolver.getNamespaceUrl(ULong.valueOf(basedAccId))
            );
        }

        properties.addProperty("den",
                properties.get("propertyTerm").getAsString() + ". " +
                        properties.get("roleOfAccObjectClassTerm").getAsString()
        );
    }

    @SneakyThrows(JsonIOException.class)
    public String serialize(BccpRecord bccpRecord) {
        Map<String, Object> properties = new HashMap();

        properties.put("component", "bccp");
        properties.put("guid", bccpRecord.getGuid());
        properties.put("propertyTerm", bccpRecord.getPropertyTerm());
        properties.put("representationTerm", bccpRecord.getRepresentationTerm());
        properties.put("definition", bccpRecord.getDefinition());
        properties.put("definitionSource", bccpRecord.getDefinitionSource());
        properties.put("defaultValue", bccpRecord.getDefaultValue());
        properties.put("fixedValue", bccpRecord.getFixedValue());
        properties.put("state", bccpRecord.getState());
        properties.put("deprecated", ((byte) 1 == bccpRecord.getIsDeprecated()) ? true : false);
        properties.put("nillable", ((byte) 1 == bccpRecord.getIsNillable()) ? true : false);

        properties.put("ownerUserId", (bccpRecord.getOwnerUserId() != null) ? bccpRecord.getOwnerUserId() : null);
        properties.put("bdtId", (bccpRecord.getBdtId() != null) ? bccpRecord.getBdtId() : null);
        properties.put("namespaceId", (bccpRecord.getNamespaceId() != null) ? bccpRecord.getNamespaceId() : null);

        return gson.toJson(properties, HashMap.class);
    }

    private void bccpAddProperties(JsonObject properties) {
        properties.addProperty("den",
                properties.get("propertyTerm").getAsString() + ". " +
                        properties.get("representationTerm").getAsString()
        );

        if (properties.has("ownerUserId")) {
            BigInteger ownerUserId = properties.get("ownerUserId").getAsJsonObject().get("value").getAsBigInteger();
            properties.addProperty("ownerUserLoginId",
                    resolver.getUserLoginId(ULong.valueOf(ownerUserId))
            );
        }
        if (properties.has("bdtId")) {
            BigInteger bdtId = properties.get("bdtId").getAsJsonObject().get("value").getAsBigInteger();
            properties.addProperty("bdtDataTypeTerm",
                    resolver.getAccObjectClassTerm(ULong.valueOf(bdtId))
            );
        }
        if (properties.has("namespaceId")) {
            BigInteger basedAccId = properties.get("namespaceId").getAsJsonObject().get("value").getAsBigInteger();
            properties.addProperty("namespaceUrl",
                    resolver.getNamespaceUrl(ULong.valueOf(basedAccId))
            );
        }
    }

    @SneakyThrows(JsonIOException.class)
    public String serialize(DtRecord dtRecord, List<DtScRecord> dtScRecords) {
        Map<String, Object> properties = new HashMap();

        properties.put("component", "dt");
        properties.put("guid", dtRecord.getGuid());
        properties.put("type", DTType.valueOf(dtRecord.getType()).name());
        properties.put("versionNum", dtRecord.getVersionNum());
        properties.put("dataTypeTerm", dtRecord.getDataTypeTerm());
        properties.put("qualifier", dtRecord.getQualifier());
        properties.put("definition", dtRecord.getDefinition());
        properties.put("definitionSource", dtRecord.getDefinitionSource());
        properties.put("contentComponentDefinition", dtRecord.getContentComponentDefinition());
        properties.put("revisionDoc", dtRecord.getRevisionDoc());
        properties.put("state", dtRecord.getState());
        properties.put("deprecated", ((byte) 1 == dtRecord.getIsDeprecated()) ? true : false);

        properties.put("ownerUserId", (dtRecord.getOwnerUserId() != null) ? dtRecord.getOwnerUserId().toBigInteger() : null);
        properties.put("basedDtId", (dtRecord.getBasedDtId() != null) ? dtRecord.getBasedDtId().toBigInteger() : null);

        List<Map<String, Object>> supplementaryComponents = new ArrayList();
        properties.put("supplementaryComponents", supplementaryComponents);

        for (DtScRecord dtScRecord : dtScRecords) {
            supplementaryComponents.add(serialize(dtScRecord));
        }

        return gson.toJson(properties, HashMap.class);
    }

    @SneakyThrows(JsonIOException.class)
    private Map<String, Object> serialize(DtScRecord dtScRecord) {
        Map<String, Object> properties = new HashMap();

        properties.put("component", "dtSc");
        properties.put("guid", dtScRecord.getGuid());
        properties.put("propertyTerm", dtScRecord.getPropertyTerm());
        properties.put("representationTerm", dtScRecord.getRepresentationTerm());
        properties.put("cardinalityMin", dtScRecord.getCardinalityMin());
        properties.put("cardinalityMax", dtScRecord.getCardinalityMax());
        properties.put("defaultValue", dtScRecord.getDefaultValue());
        properties.put("fixedValue", dtScRecord.getFixedValue());
        properties.put("definition", dtScRecord.getDefinition());
        properties.put("definitionSource", dtScRecord.getDefinitionSource());

        properties.put("basedDtScId", (dtScRecord.getBasedDtScId() != null) ? dtScRecord.getBasedDtScId().toBigInteger() : null);

        return properties;
    }

    @SneakyThrows(JsonIOException.class)
    public String serialize(CodeListRecord codeListRecord,
                            List<CodeListValueRecord> codeListValueRecords) {
        Map<String, Object> properties = new HashMap();

        properties.put("component", "codeList");
        properties.put("guid", codeListRecord.getGuid());
        properties.put("name", codeListRecord.getName());
        properties.put("listId", codeListRecord.getListId());
        properties.put("agencyId", codeListRecord.getAgencyId());
        properties.put("versionId", codeListRecord.getVersionId());
        properties.put("remark", codeListRecord.getRemark());
        properties.put("definition", codeListRecord.getDefinition());
        properties.put("definitionSource", codeListRecord.getDefinitionSource());
        properties.put("state", codeListRecord.getState());
        properties.put("deprecated", ((byte) 1 == codeListRecord.getIsDeprecated()) ? true : false);
        properties.put("extensible", ((byte) 1 == codeListRecord.getExtensibleIndicator()) ? true : false);

        properties.put("ownerUserId", (codeListRecord.getOwnerUserId() != null) ? codeListRecord.getOwnerUserId().toBigInteger() : null);

        List<Map<String, Object>> values = new ArrayList();
        properties.put("values", values);

        for (CodeListValueRecord codeListValueRecord : codeListValueRecords) {
            values.add(serialize(codeListValueRecord));
        }

        return gson.toJson(properties, HashMap.class);
    }

    @SneakyThrows(JsonIOException.class)
    private Map<String, Object> serialize(CodeListValueRecord codeListValueRecord) {
        Map<String, Object> properties = new HashMap();

        properties.put("component", "codeListValue");
        properties.put("guid", codeListValueRecord.getGuid());
        properties.put("value", codeListValueRecord.getValue());
        properties.put("name", codeListValueRecord.getName());
        properties.put("definition", codeListValueRecord.getDefinition());
        properties.put("definitionSource", codeListValueRecord.getDefinitionSource());
        properties.put("used", ((byte) 1 == codeListValueRecord.getUsedIndicator()) ? true : false);
        properties.put("locked", ((byte) 1 == codeListValueRecord.getLockedIndicator()) ? true : false);
        properties.put("extension", ((byte) 1 == codeListValueRecord.getExtensionIndicator()) ? true : false);

        return properties;
    }

    @SneakyThrows(JsonIOException.class)
    public String serialize(XbtRecord xbtRecord) {
        Map<String, Object> properties = new HashMap();

        properties.put("component", "xbt");
        properties.put("guid", xbtRecord.getGuid());
        properties.put("name", xbtRecord.getName());
        properties.put("builtInType", xbtRecord.getBuiltinType());
        properties.put("revisionDoc", xbtRecord.getRevisionDoc());
        properties.put("schemaDefinition", xbtRecord.getSchemaDefinition());
        properties.put("jbtDraft05Map", xbtRecord.getJbtDraft05Map());
        properties.put("openapi30Map", xbtRecord.getOpenapi30Map());
        properties.put("state", xbtRecord.getState());
        properties.put("deprecated", ((byte) 1 == xbtRecord.getIsDeprecated()) ? true : false);

        properties.put("ownerUserId", (xbtRecord.getOwnerUserId() != null) ? xbtRecord.getOwnerUserId().toBigInteger() : null);
        properties.put("subTypeOfXbtId", (xbtRecord.getSubtypeOfXbtId() != null) ? xbtRecord.getSubtypeOfXbtId().toBigInteger() : null);

        return gson.toJson(properties, HashMap.class);
    }

    @SneakyThrows()
    public JsonObject deserialize(String snapshot) {
        if (StringUtils.isEmpty(snapshot)) {
            return null;
        }

        JsonObject properties = this.gson.fromJson(snapshot, JsonObject.class);
        String component = properties.get("component").getAsString();
        if (StringUtils.isEmpty(component)) {
            return properties;
        }

        try {
            Method method = this.getClass().getDeclaredMethod(component + "AddProperties", JsonObject.class);
            method.invoke(this, properties);
        } catch (NoSuchMethodException ignore) {
        }

        return properties;
    }

}
