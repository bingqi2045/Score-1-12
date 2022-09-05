package org.oagi.score.repo.component.code_list;

import org.oagi.score.data.RepositoryRequest;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpdateCodeListPropertiesRepositoryRequest extends RepositoryRequest {

    private final String codeListManifestId;

    private String codeListName;
    private String agencyIdListValueManifestId;
    private String versionId;
    private String listId;
    private String namespaceId;
    private String definition;
    private String definitionSource;
    private String remark;
    private boolean extensible;
    private boolean deprecated;

    public UpdateCodeListPropertiesRepositoryRequest(AuthenticatedPrincipal user,
                                                     String codeListManifestId) {
        super(user);
        this.codeListManifestId = codeListManifestId;
    }

    public UpdateCodeListPropertiesRepositoryRequest(AuthenticatedPrincipal user,
                                                     LocalDateTime localDateTime,
                                                     String codeListManifestId) {
        super(user, localDateTime);
        this.codeListManifestId = codeListManifestId;
    }

    public String getCodeListManifestId() {
        return codeListManifestId;
    }

    public String getCodeListName() {
        return codeListName;
    }

    public void setCodeListName(String codeListName) {
        this.codeListName = codeListName;
    }

    public String getAgencyIdListValueManifestId() {
        return agencyIdListValueManifestId;
    }

    public void setAgencyIdListValueManifestId(String agencyIdListValueManifestId) {
        this.agencyIdListValueManifestId = agencyIdListValueManifestId;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getNamespaceId() {
        return namespaceId;
    }

    public void setNamespaceId(String namespaceId) {
        this.namespaceId = namespaceId;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isExtensible() {
        return extensible;
    }

    public void setExtensible(boolean extensible) {
        this.extensible = extensible;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public void addCodeListValueRequest() {

    }
}
