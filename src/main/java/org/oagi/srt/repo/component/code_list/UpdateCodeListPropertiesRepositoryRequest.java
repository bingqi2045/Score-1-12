package org.oagi.srt.repo.component.code_list;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class UpdateCodeListPropertiesRepositoryRequest extends RepositoryRequest {

    private final BigInteger codeListManifestId;

    private String codeListName;
    private BigInteger agencyId;
    private String versionId;
    private String listId;
    private String definition;
    private String definitionSource;
    private String remark;
    private boolean extensible;
    private boolean deprecated;

    public UpdateCodeListPropertiesRepositoryRequest(User user,
                                                     BigInteger codeListManifestId) {
        super(user);
        this.codeListManifestId = codeListManifestId;
    }

    public UpdateCodeListPropertiesRepositoryRequest(User user,
                                                     LocalDateTime localDateTime,
                                                     BigInteger codeListManifestId) {
        super(user, localDateTime);
        this.codeListManifestId = codeListManifestId;
    }

    public BigInteger getCodeListManifestId() {
        return codeListManifestId;
    }

    public String getCodeListName() {
        return codeListName;
    }

    public void setCodeListName(String codeListName) {
        this.codeListName = codeListName;
    }

    public BigInteger getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(BigInteger agencyId) {
        this.agencyId = agencyId;
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
}