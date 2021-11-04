package org.oagi.score.gateway.http.api.cc_management.data.elasticsearch;


import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigInteger;

@Document(indexName = "core_component")
public class CoreComponent {

    @Id
    private String id;

    @Field("manifest_id")
    private BigInteger manifestId;

    @Field("score")
    private Double score;

    @Field("component_id")
    private BigInteger componentId;

    @Field("deprecated")
    private String deprecated;

    @Field("type")
    private String type;

    @Field(name = "last_update_timestamp", type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC-5")
    private String lastUpdateTimestamp;

    @Field("module")
    private String module;

    @Field("revision_num")
    private String revisionNum;

    @Field("definition")
    private String definition;

    @Field("updater")
    private String updater;

    @Field("guid")
    private String guid;

    @Field("definition_source")
    private String definitionSource;

    @Field("owner")
    private String owner;

    @Field("state")
    private String state;

    @Field("den")
    private String den;

    @Field("release_num")
    private String releaseNum;

    @Field("release_id")
    private String releaseId;

    @Field("oagis_component_type")
    private String oagisComponentType;

    @Field("asccp_type")
    private String asccpType;

    @Field("owned_by_developer")
    private String ownedByDeveloper;

    @Field("dt_type")
    private String dtType;

    @Field("name")
    private String name;

    @Field("six_digit_id")
    private String sixDigitId;

    @Field("default_value_domain")
    private String defaultValueDomain;

    public CoreComponent() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigInteger getManifestId() {
        return manifestId;
    }

    public void setManifestId(BigInteger manifestId) {
        this.manifestId = manifestId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public BigInteger getComponentId() {
        return componentId;
    }

    public void setComponentId(BigInteger componentId) {
        this.componentId = componentId;
    }

    public String getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(String deprecated) {
        this.deprecated = deprecated;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLastUpdateTimestamp() {
        return lastUpdateTimestamp;
    }

    public void setLastUpdateTimestamp(String lastUpdateTimestamp) {
        this.lastUpdateTimestamp = lastUpdateTimestamp;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getRevisionNum() {
        return revisionNum;
    }

    public void setRevisionNum(String revisionNum) {
        this.revisionNum = revisionNum;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getDefinitionSource() {
        return definitionSource;
    }

    public void setDefinitionSource(String definitionSource) {
        this.definitionSource = definitionSource;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDen() {
        return den;
    }

    public void setDen(String den) {
        this.den = den;
    }

    public String getReleaseNum() {
        return releaseNum;
    }

    public void setReleaseNum(String releaseNum) {
        this.releaseNum = releaseNum;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getOagisComponentType() {
        return oagisComponentType;
    }

    public void setOagisComponentType(String oagisComponentType) {
        this.oagisComponentType = oagisComponentType;
    }

    public String getAsccpType() {
        return asccpType;
    }

    public void setAsccpType(String asccpType) {
        this.asccpType = asccpType;
    }

    public String getOwnedByDeveloper() {
        return ownedByDeveloper;
    }

    public void setOwnedByDeveloper(String ownedByDeveloper) {
        this.ownedByDeveloper = ownedByDeveloper;
    }

    public String getDtType() {
        return dtType;
    }

    public void setDtType(String dtType) {
        this.dtType = dtType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSixDigitId() {
        return sixDigitId;
    }

    public void setSixDigitId(String sixDigitId) {
        this.sixDigitId = sixDigitId;
    }

    public String getDefaultValueDomain() {
        return defaultValueDomain;
    }

    public void setDefaultValueDomain(String defaultValueDomain) {
        this.defaultValueDomain = defaultValueDomain;
    }
}
