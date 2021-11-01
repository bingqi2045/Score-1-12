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

    private BigInteger manifest_id;

    private Double score;

    private BigInteger component_id;

    private String deprecated;

    private String type;

    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC-5")
    private String last_update_timestamp;
    private String module;
    private String revision_num;
    private String definition;
    private String updater;
    private String guid;
    private String definition_source;
    private String owner;
    private String state;
    private String den;
    private String release_num;
    private String release_id;
    private String oagis_component_type;
    private String asccp_type;
    private String owned_by_developer;
    private String dt_type;
    private String name;

    public CoreComponent() {
    }

    public BigInteger getManifest_id() {
        return manifest_id;
    }

    public void setManifest_id(BigInteger manifest_id) {
        this.manifest_id = manifest_id;
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

    public String getLast_update_timestamp() {
        return last_update_timestamp;
    }

    public void setLast_update_timestamp(String last_update_timestamp) {
        this.last_update_timestamp = last_update_timestamp;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getRevision_num() {
        return revision_num;
    }

    public void setRevision_num(String revision_num) {
        this.revision_num = revision_num;
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

    public String getDefinition_source() {
        return definition_source;
    }

    public void setDefinition_source(String definition_source) {
        this.definition_source = definition_source;
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

    public String getRelease_num() {
        return release_num;
    }

    public void setRelease_num(String release_num) {
        this.release_num = release_num;
    }

    public String getRelease_id() {
        return release_id;
    }

    public void setRelease_id(String release_id) {
        this.release_id = release_id;
    }

    public String getOagis_component_type() {
        return oagis_component_type;
    }

    public void setOagis_component_type(String oagis_component_type) {
        this.oagis_component_type = oagis_component_type;
    }

    public String getAsccp_type() {
        return asccp_type;
    }

    public void setAsccp_type(String asccp_type) {
        this.asccp_type = asccp_type;
    }

    public String getOwned_by_developer() {
        return owned_by_developer;
    }

    public void setOwned_by_developer(String owned_by_developer) {
        this.owned_by_developer = owned_by_developer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDt_type() {
        return dt_type;
    }

    public void setDt_type(String dt_type) {
        this.dt_type = dt_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getComponent_id() {
        return component_id;
    }

    public void setComponent_id(BigInteger component_id) {
        this.component_id = component_id;
    }
}
