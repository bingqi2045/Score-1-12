package org.oagi.score.repo.domain;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import org.jooq.types.ULong;
import org.oagi.score.data.BCCEntityType;
import org.oagi.score.data.DTType;
import org.oagi.score.data.OagisComponentType;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class LogSerializer {

    private final Gson gson;

    @Autowired
    private LogSnapshotResolver resolver;

    public LogSerializer() {
        this.gson = new Gson();
    }

    @SneakyThrows(JsonIOException.class)
    public String serialize(AccRecord accRecord,
                            List<AsccRecord> asccRecords, List<BccRecord> bccRecords,
                            List<SeqKeyRecord> seqKeyRecords) {
        Map<String, Object> properties = new HashMap();

        properties.put("component", "acc");
        properties.put("guid", accRecord.getGuid());
        properties.put("objectClassTerm", accRecord.getObjectClassTerm());
        properties.put("definition", accRecord.getDefinition());
        properties.put("definitionSource", accRecord.getDefinitionSource());
        properties.put("objectClassQualifier", accRecord.getObjectClassQualifier());
        properties.put("componentType", OagisComponentType.valueOf(accRecord.getOagisComponentType()).name());
        properties.put("state", accRecord.getState());
        properties.put("deprecated", (byte) 1 == accRecord.getIsDeprecated());
        properties.put("abstract", (byte) 1 == accRecord.getIsAbstract());

        properties.put("ownerUserId", (accRecord.getOwnerUserId() != null) ? accRecord.getOwnerUserId() : null);
        properties.put("basedAccId", (accRecord.getBasedAccId() != null) ? accRecord.getBasedAccId() : null);
        properties.put("namespaceId", (accRecord.getNamespaceId() != null) ? accRecord.getNamespaceId() : null);

        List<Map<String, Object>> associations = new ArrayList();
        properties.put("associations", associations);

        for (AssocRecord assocRecord : sort(asccRecords, bccRecords, seqKeyRecords)) {
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

    private List<AssocRecord> sort(List<AsccRecord> asccRecords, List<BccRecord> bccRecords,
                                   List<SeqKeyRecord> seqKeyRecords) {

        Map<ULong, AsccRecord> asccRecordMap = asccRecords.stream().collect(
                Collectors.toMap(AsccRecord::getAsccId, Function.identity()));
        Map<ULong, BccRecord> bccRecordMap = bccRecords.stream().collect(
                Collectors.toMap(BccRecord::getBccId, Function.identity()));

        List<AssocRecord> sortedRecords = new ArrayList();
        if (!seqKeyRecords.isEmpty()) {
            Map<ULong, SeqKeyRecord> seqKeyRecordMap = seqKeyRecords.stream().collect(
                    Collectors.toMap(SeqKeyRecord::getSeqKeyId, Function.identity()));
            SeqKeyRecord node = seqKeyRecords.stream().filter(e -> e.getPrevSeqKeyId() == null).findAny().get();
            while (node != null) {
                node.getCcId();
                switch (node.getType()) {
                    case ascc:
                        sortedRecords.add(new AssocRecord(asccRecordMap.get(node.getCcId())));
                        break;
                    case bcc:
                        sortedRecords.add(new AssocRecord(bccRecordMap.get(node.getCcId())));
                        break;
                }
                node = seqKeyRecordMap.get(node.getNextSeqKeyId());
            }
        }

        return sortedRecords;
    }

    private class AssocRecord {

        private final int seqKey;
        private final LocalDateTime timestamp;
        private final Object delegate;

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
        properties.put("deprecated", (byte) 1 == asccRecord.getIsDeprecated());
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
        properties.put("deprecated", (byte) 1 == bccRecord.getIsDeprecated());
        properties.put("nillable", (byte) 1 == bccRecord.getIsNillable());
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
        properties.put("reusable", (byte) 1 == asccpRecord.getReusableIndicator());
        properties.put("deprecated", (byte) 1 == asccpRecord.getIsDeprecated());
        properties.put("nillable", (byte) 1 == asccpRecord.getIsNillable());

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
        properties.put("deprecated", (byte) 1 == bccpRecord.getIsDeprecated());
        properties.put("nillable", (byte) 1 == bccpRecord.getIsNillable());

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
                    resolver.getDtDen(ULong.valueOf(bdtId))
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
        properties.put("deprecated", (byte) 1 == dtRecord.getIsDeprecated());

        properties.put("ownerUserId", (dtRecord.getOwnerUserId() != null) ? dtRecord.getOwnerUserId().toBigInteger() : null);
        properties.put("basedDtId", (dtRecord.getBasedDtId() != null) ? dtRecord.getBasedDtId().toBigInteger() : null);
        properties.put("namespaceId", (dtRecord.getNamespaceId() != null) ? dtRecord.getNamespaceId() : null);

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

    private void dtAddProperties(JsonObject properties) {
        if (properties.has("ownerUserId")) {
            BigInteger ownerUserId = properties.get("ownerUserId").getAsJsonObject().get("value").getAsBigInteger();
            properties.addProperty("ownerUserLoginId",
                    resolver.getUserLoginId(ULong.valueOf(ownerUserId))
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
        properties.put("deprecated", (byte) 1 == codeListRecord.getIsDeprecated());
        properties.put("extensible", (byte) 1 == codeListRecord.getExtensibleIndicator());

        properties.put("ownerUserId", (codeListRecord.getOwnerUserId() != null) ? codeListRecord.getOwnerUserId().toBigInteger() : null);
        properties.put("basedCodeListId", (codeListRecord.getBasedCodeListId() != null) ? codeListRecord.getBasedCodeListId().toBigInteger() : null);
        properties.put("namespaceId", (codeListRecord.getNamespaceId() != null) ? codeListRecord.getNamespaceId() : null);

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
        properties.put("deprecated", (byte) 1 == codeListValueRecord.getIsDeprecated());
        properties.put("used", (byte) 1 == codeListValueRecord.getUsedIndicator());
        properties.put("locked", (byte) 1 == codeListValueRecord.getLockedIndicator());
        properties.put("extension", (byte) 1 == codeListValueRecord.getExtensionIndicator());

        return properties;
    }

    private void codeListAddProperties(JsonObject properties) {
        if (properties.has("ownerUserId")) {
            BigInteger ownerUserId = properties.get("ownerUserId").getAsJsonObject().get("value").getAsBigInteger();
            properties.addProperty("ownerUserLoginId",
                    resolver.getUserLoginId(ULong.valueOf(ownerUserId))
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
    public String serialize(AgencyIdListRecord agencyIdListRecord,
                            List<AgencyIdListValueRecord> agencyIdListValueRecords) {
        Map<String, Object> properties = new HashMap();

        properties.put("component", "agencyIdList");
        properties.put("guid", agencyIdListRecord.getGuid());
        properties.put("enumTypeGuid", agencyIdListRecord.getEnumTypeGuid());
        properties.put("name", agencyIdListRecord.getName());
        properties.put("listId", agencyIdListRecord.getListId());
        properties.put("versionId", agencyIdListRecord.getVersionId());
        properties.put("definition", agencyIdListRecord.getDefinition());
        properties.put("state", agencyIdListRecord.getState());
        properties.put("deprecated", (byte) 1 == agencyIdListRecord.getIsDeprecated());

        properties.put("agencyIdListValueId", (agencyIdListRecord.getAgencyIdListValueId() != null) ? agencyIdListRecord.getAgencyIdListValueId().toBigInteger() : null);
        properties.put("ownerUserId", (agencyIdListRecord.getOwnerUserId() != null) ? agencyIdListRecord.getOwnerUserId().toBigInteger() : null);
        properties.put("basedAgencyIdListId", (agencyIdListRecord.getBasedAgencyIdListId() != null) ? agencyIdListRecord.getBasedAgencyIdListId().toBigInteger() : null);
        properties.put("namespaceId", (agencyIdListRecord.getNamespaceId() != null) ? agencyIdListRecord.getNamespaceId() : null);

        List<Map<String, Object>> values = new ArrayList();
        properties.put("values", values);

        for (AgencyIdListValueRecord agencyIdListValueRecord : agencyIdListValueRecords) {
            values.add(serialize(agencyIdListValueRecord));
        }

        return gson.toJson(properties, HashMap.class);
    }

    @SneakyThrows(JsonIOException.class)
    private Map<String, Object> serialize(AgencyIdListValueRecord agencyIdListValueRecord) {
        Map<String, Object> properties = new HashMap();

        properties.put("component", "agencyIdListValue");
        properties.put("guid", agencyIdListValueRecord.getGuid());
        properties.put("value", agencyIdListValueRecord.getValue());
        properties.put("name", agencyIdListValueRecord.getName());
        properties.put("definition", agencyIdListValueRecord.getDefinition());
        properties.put("deprecated", (byte) 1 == agencyIdListValueRecord.getIsDeprecated());

        return properties;
    }

    private void agencyIdListAddProperties(JsonObject properties) {
        if (properties.has("ownerUserId")) {
            BigInteger ownerUserId = properties.get("ownerUserId").getAsJsonObject().get("value").getAsBigInteger();
            properties.addProperty("ownerUserLoginId",
                    resolver.getUserLoginId(ULong.valueOf(ownerUserId))
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
        properties.put("deprecated", (byte) 1 == xbtRecord.getIsDeprecated());

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

    public ULong getSnapshotId(JsonElement obj) {
        if (obj != null && !obj.isJsonNull()) {
            return obj.getAsJsonObject().isJsonNull() ? null :
                    ULong.valueOf(obj.getAsJsonObject().get("value").getAsBigInteger());
        }
        return null;
    }

    public String getSnapshotString(JsonElement obj) {
        if (obj != null && !obj.isJsonNull()) {
            return obj.getAsString().isEmpty() ? "" : obj.getAsString();
        }
        return "";
    }

    public Byte getSnapshotByte(JsonElement obj) {
        if (obj != null && !obj.isJsonNull()) {
            return obj.getAsBoolean() ? (byte) 1 : 0;
        }
        return 0;
    }

}
