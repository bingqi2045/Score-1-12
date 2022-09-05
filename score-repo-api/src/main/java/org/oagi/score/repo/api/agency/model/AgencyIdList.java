package org.oagi.score.repo.api.agency.model;

import org.oagi.score.repo.api.base.Auditable;
import org.oagi.score.repo.api.corecomponent.model.CcState;
import org.oagi.score.repo.api.corecomponent.model.CoreComponent;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

public class AgencyIdList extends Auditable implements CoreComponent, Serializable {

    private String releaseId;

    private String releaseNum;

    private String releaseState;

    private String revisionNum;

    public String modulePath;

    private String agencyIdListManifestId;

    private String agencyIdListId;

    private String guid;

    private String enumTypeGuid;

    private String name;

    private String listId;

    private String agencyIdListValueId;

    private String agencyIdListValueManifestId;

    private String agencyIdListValueName;

    private String basedAgencyIdListManifestId;

    private String basedAgencyIdListName;

    private String basedAgencyIdListId;

    private String versionId;

    private String definition;

    private String definitionSource;

    private String remark;

    private String namespaceId;

    private ScoreUser owner;

    private CcState state;

    private String access;

    private boolean deprecated;

    private String prevAgencyIdListId;

    private String nextAgencyIdListId;

    private AgencyIdList prev;

    private List<AgencyIdListValue> values;

    public String getAgencyIdListManifestId() {
        return agencyIdListManifestId;
    }

    public void setAgencyIdListManifestId(String agencyIdListManifestId) {
        this.agencyIdListManifestId = agencyIdListManifestId;
    }

    public String getReleaseNum() {
        return releaseNum;
    }

    public void setReleaseNum(String releaseNum) {
        this.releaseNum = releaseNum;
    }

    public String getReleaseState() {
        return releaseState;
    }

    public void setReleaseState(String releaseState) {
        this.releaseState = releaseState;
    }

    public String getReleaseId() {
        return releaseId;
    }

    public void setReleaseId(String releaseId) {
        this.releaseId = releaseId;
    }

    public String getRevisionNum() {
        return revisionNum;
    }

    public String getModulePath() {
        return modulePath;
    }

    public void setModulePath(String modulePath) {
        this.modulePath = modulePath;
    }

    public void setRevisionNum(String revisionNum) {
        this.revisionNum = revisionNum;
    }

    public String getAgencyIdListValueManifestId() {
        return agencyIdListValueManifestId;
    }

    public void setAgencyIdListValueManifestId(String agencyIdListValueManifestId) {
        this.agencyIdListValueManifestId = agencyIdListValueManifestId;
    }

    public String getDefinitionSource() {
        return definitionSource;
    }

    public void setDefinitionSource(String definitionSource) {
        this.definitionSource = definitionSource;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public AgencyIdList getPrev() {
        return prev;
    }

    public void setPrev(AgencyIdList prev) {
        this.prev = prev;
    }

    public List<AgencyIdListValue> getValues() {
        return values;
    }

    public void setValues(List<AgencyIdListValue> values) {
        this.values = values;
    }

    public String getAgencyIdListId() {
        return agencyIdListId;
    }

    public void setAgencyIdListId(String agencyIdListId) {
        this.agencyIdListId = agencyIdListId;
    }

    @Override
    public String getGuid() {
        return guid;
    }

    @Override
    public String getId() {
        return agencyIdListId;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getEnumTypeGuid() {
        return enumTypeGuid;
    }

    public void setEnumTypeGuid(String enumTypeGuid) {
        this.enumTypeGuid = enumTypeGuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getAgencyIdListValueId() {
        return agencyIdListValueId;
    }

    public void setAgencyIdListValueId(String agencyIdListValueId) {
        this.agencyIdListValueId = agencyIdListValueId;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getNamespaceId() {
        return namespaceId;
    }

    public void setNamespaceId(String namespaceId) {
        this.namespaceId = namespaceId;
    }

    public ScoreUser getOwner() {
        return owner;
    }

    public void setOwner(ScoreUser owner) {
        this.owner = owner;
    }

    public CcState getState() {
        return state;
    }

    public void setState(CcState state) {
        this.state = state;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public String getPrevAgencyIdListId() {
        return prevAgencyIdListId;
    }

    public void setPrevAgencyIdListId(String prevAgencyIdListId) {
        this.prevAgencyIdListId = prevAgencyIdListId;
    }

    public String getNextAgencyIdListId() {
        return nextAgencyIdListId;
    }

    public void setNextAgencyIdListId(String nextAgencyIdListId) {
        this.nextAgencyIdListId = nextAgencyIdListId;
    }

    public String getAgencyIdListValueName() {
        return agencyIdListValueName;
    }

    public void setAgencyIdListValueName(String agencyIdListValueName) {
        this.agencyIdListValueName = agencyIdListValueName;
    }

    public String getBasedAgencyIdListManifestId() {
        return basedAgencyIdListManifestId;
    }

    public void setBasedAgencyIdListManifestId(String basedAgencyIdListManifestId) {
        this.basedAgencyIdListManifestId = basedAgencyIdListManifestId;
    }

    public String getBasedAgencyIdListName() {
        return basedAgencyIdListName;
    }

    public void setBasedAgencyIdListName(String basedAgencyIdListName) {
        this.basedAgencyIdListName = basedAgencyIdListName;
    }

    public String getBasedAgencyIdListId() {
        return basedAgencyIdListId;
    }

    public void setBasedAgencyIdListId(String basedAgencyIdListId) {
        this.basedAgencyIdListId = basedAgencyIdListId;
    }

}
