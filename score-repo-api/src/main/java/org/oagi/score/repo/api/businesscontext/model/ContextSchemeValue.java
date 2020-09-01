package org.oagi.score.repo.api.businesscontext.model;

import java.math.BigInteger;

public class ContextSchemeValue {

    private BigInteger contextSchemeValueId;

    private String guid;

    private String value;

    private String meaning;

    public ContextSchemeValue() {
    }

    public ContextSchemeValue(BigInteger contextSchemeValueId, String guid, String value) {
        this(contextSchemeValueId, guid, value, null);
    }

    public ContextSchemeValue(BigInteger contextSchemeValueId, String guid, String value, String meaning) {
        this.contextSchemeValueId = contextSchemeValueId;
        this.guid = guid;
        this.value = value;
        this.meaning = meaning;
    }

    public BigInteger getContextSchemeValueId() {
        return contextSchemeValueId;
    }

    public void setContextSchemeValueId(BigInteger contextSchemeValueId) {
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
}
