package org.oagi.score.repo.api.agency.model;

import org.oagi.score.repo.api.user.model.ScoreUser;

import java.util.ArrayList;
import java.util.List;

public class ModifyAgencyIdListValuesRepositoryRequest {

    private String agencyIdListManifestId;
    private String state;
    private List<AgencyIdListValue> agencyIdListValueList = new ArrayList();

    public void setAgencyIdListManifestId(String agencyIdListManifestId) {
        this.agencyIdListManifestId = agencyIdListManifestId;
    }

    public ScoreUser getRequester() {
        return requester;
    }

    public void setRequester(ScoreUser requester) {
        this.requester = requester;
    }

    private ScoreUser requester;

    public static class AgencyIdListValue {

        private String agencyIdListValueManifestId;
        private String value;
        private String name;
        private String definition;

        private String definitionSource;
        private boolean deprecated;

        public String getAgencyIdListValueManifestId() {
            return agencyIdListValueManifestId;
        }

        public void setAgencyIdListValueManifestId(String agencyIdListValueManifestId) {
            this.agencyIdListValueManifestId = agencyIdListValueManifestId;
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

        public boolean isDeprecated() {
            return deprecated;
        }

        public void setDeprecated(boolean deprecated) {
            this.deprecated = deprecated;
        }
    }

    public String getAgencyIdListManifestId() {
        return agencyIdListManifestId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void addAgencyIdListValue(AgencyIdListValue agencyIdListValue) {
        this.agencyIdListValueList.add(agencyIdListValue);
    }

    public void setAgencyIdListValueList(List<AgencyIdListValue> agencyIdListValueList) {
        this.agencyIdListValueList = agencyIdListValueList;
    }

    public List<AgencyIdListValue> getAgencyIdListValueList() {
        return agencyIdListValueList;
    }
}
