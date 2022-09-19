package org.oagi.score.repo.api.agency.model;

import org.oagi.score.repo.api.base.Auditable;
import org.oagi.score.repo.api.corecomponent.model.CoreComponent;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.io.Serializable;
import java.math.BigInteger;

public class AgencyIdListValue extends Auditable implements CoreComponent, Serializable {

    private String agencyIdListValueManifestId;

    private String basedAgencyIdListValueManifestId;

    private String agencyIdListValueId;

    private String basedAgencyIdListValueId;

    private String guid;

    private String value;

    private String name;

    private String definition;

    private String definitionSource;

    private String ownerAgencyIdListId;

    private ScoreUser owner;

    private boolean deprecated;

    private boolean used;

    private String prevAgencyIdListValueId;

    private String nextAgencyIdListValueId;

    @Override
    public String getId() {
        return agencyIdListValueId;
    }

    public String getAgencyIdListValueManifestId() {
        return agencyIdListValueManifestId;
    }

    public void setAgencyIdListValueManifestId(String agencyIdListValueManifestId) {
        this.agencyIdListValueManifestId = agencyIdListValueManifestId;
    }

    public String getBasedAgencyIdListValueManifestId() {
        return basedAgencyIdListValueManifestId;
    }

    public void setBasedAgencyIdListValueManifestId(String basedAgencyIdListValueManifestId) {
        this.basedAgencyIdListValueManifestId = basedAgencyIdListValueManifestId;
    }

    public String getAgencyIdListValueId() {
        return agencyIdListValueId;
    }

    public void setAgencyIdListValueId(String agencyIdListValueId) {
        this.agencyIdListValueId = agencyIdListValueId;
    }

    public String getBasedAgencyIdListValueId() {
        return basedAgencyIdListValueId;
    }

    public void setBasedAgencyIdListValueId(String basedAgencyIdListValueId) {
        this.basedAgencyIdListValueId = basedAgencyIdListValueId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getDefinitionSource() {
        return definitionSource;
    }

    public void setDefinitionSource(String definitionSource) {
        this.definitionSource = definitionSource;
    }

    public String getOwnerAgencyIdListId() {
        return ownerAgencyIdListId;
    }

    public void setOwnerAgencyIdListId(String ownerAgencyIdListId) {
        this.ownerAgencyIdListId = ownerAgencyIdListId;
    }

    public ScoreUser getOwner() {
        return owner;
    }

    public void setOwner(ScoreUser owner) {
        this.owner = owner;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String getPrevAgencyIdListValueId() {
        return prevAgencyIdListValueId;
    }

    public void setPrevAgencyIdListValueId(String prevAgencyIdListValueId) {
        this.prevAgencyIdListValueId = prevAgencyIdListValueId;
    }

    public String getNextAgencyIdListValueId() {
        return nextAgencyIdListValueId;
    }

    public void setNextAgencyIdListValueId(String nextAgencyIdListValueId) {
        this.nextAgencyIdListValueId = nextAgencyIdListValueId;
    }
}
