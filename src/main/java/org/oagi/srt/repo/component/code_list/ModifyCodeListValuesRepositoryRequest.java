package org.oagi.srt.repo.component.code_list;

import org.oagi.srt.data.RepositoryRequest;
import org.springframework.security.core.userdetails.User;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ModifyCodeListValuesRepositoryRequest extends RepositoryRequest {

    private final BigInteger codeListManifestId;
    private String state;
    private List<CodeListValue> codeListValueList = new ArrayList();

    public static class CodeListValue {
        private String value;
        private String name;
        private String definition;
        private String definitionSource;

        private boolean used;
        private boolean locked;
        private boolean extension;

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

        public boolean isUsed() {
            return used;
        }

        public void setUsed(boolean used) {
            this.used = used;
        }

        public boolean isLocked() {
            return locked;
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }

        public boolean isExtension() {
            return extension;
        }

        public void setExtension(boolean extension) {
            this.extension = extension;
        }
    }

    public ModifyCodeListValuesRepositoryRequest(User user,
                                                 BigInteger codeListManifestId) {
        super(user);
        this.codeListManifestId = codeListManifestId;
    }

    public ModifyCodeListValuesRepositoryRequest(User user,
                                                 LocalDateTime localDateTime,
                                                 BigInteger codeListManifestId) {
        super(user, localDateTime);
        this.codeListManifestId = codeListManifestId;
    }

    public BigInteger getCodeListManifestId() {
        return codeListManifestId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void addCodeListValue(CodeListValue codeListValue) {
        this.codeListValueList.add(codeListValue);
    }

    public void setCodeListValueList(List<CodeListValue> codeListValueList) {
        this.codeListValueList = codeListValueList;
    }

    public List<CodeListValue> getCodeListValueList() {
        return codeListValueList;
    }
}
