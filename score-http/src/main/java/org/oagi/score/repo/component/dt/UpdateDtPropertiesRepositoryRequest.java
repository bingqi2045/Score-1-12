package org.oagi.score.repo.component.dt;

import org.oagi.score.data.RepositoryRequest;
import org.oagi.score.gateway.http.api.cc_management.data.node.CcBdtPriRestri;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.time.LocalDateTime;
import java.util.List;

public class UpdateDtPropertiesRepositoryRequest extends RepositoryRequest {

    private final String dtManifestId;

    private String qualifier;
    private String sixDigitId;
    private String contentComponentDefinition;
    private String definition;
    private String definitionSource;
    private boolean deprecated;
    private String namespaceId;
    private List<CcBdtPriRestri> bdtPriRestriList;

    public UpdateDtPropertiesRepositoryRequest(AuthenticatedPrincipal user,
                                               String dtManifestId) {
        super(user);
        this.dtManifestId = dtManifestId;
    }

    public UpdateDtPropertiesRepositoryRequest(AuthenticatedPrincipal user,
                                               LocalDateTime localDateTime,
                                               String dtManifestId) {
        super(user, localDateTime);
        this.dtManifestId = dtManifestId;
    }

    public String getDtManifestId() {
        return dtManifestId;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        if (StringUtils.hasLength(qualifier)) {
            this.qualifier = qualifier;
        }
    }

    public String getSixDigitId() {
        return sixDigitId;
    }

    public void setSixDigitId(String sixDigitId) {
        this.sixDigitId = sixDigitId;
    }

    public String getContentComponentDefinition() {
        return contentComponentDefinition;
    }

    public void setContentComponentDefinition(String contentComponentDefinition) {
        if (StringUtils.hasLength(contentComponentDefinition)) {
            this.contentComponentDefinition = contentComponentDefinition;
        }
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        if (StringUtils.hasLength(definition)) {
            this.definition = definition;
        }
    }

    public String getDefinitionSource() {
        return definitionSource;
    }

    public void setDefinitionSource(String definitionSource) {
        if (StringUtils.hasLength(definitionSource)) {
            this.definitionSource = definitionSource;
        }
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public String getNamespaceId() {
        return namespaceId;
    }

    public void setNamespaceId(String namespaceId) {
        this.namespaceId = namespaceId;
    }

    public List<CcBdtPriRestri> getBdtPriRestriList() {
        return bdtPriRestriList;
    }

    public void setBdtPriRestriList(List<CcBdtPriRestri> bdtPriRestriList) {
        this.bdtPriRestriList = bdtPriRestriList;
    }
}
