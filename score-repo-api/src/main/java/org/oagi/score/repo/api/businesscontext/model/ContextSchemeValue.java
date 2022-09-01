package org.oagi.score.repo.api.businesscontext.model;

import java.io.Serializable;
import java.math.BigInteger;

public class ContextSchemeValue implements Serializable {

    private String contextSchemeValueId;

    private String guid;

    private String value;

    private String meaning;

    private boolean used;

    private String ownerContextSchemeId;

    public String getContextSchemeValueId() {
        return contextSchemeValueId;
    }

    public void setContextSchemeValueId(String contextSchemeValueId) {
        this.contextSchemeValueId = contextSchemeValueId;
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

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String getOwnerContextSchemeId() {
        return ownerContextSchemeId;
    }

    public void setOwnerContextSchemeId(String ownerContextSchemeId) {
        this.ownerContextSchemeId = ownerContextSchemeId;
    }
}
