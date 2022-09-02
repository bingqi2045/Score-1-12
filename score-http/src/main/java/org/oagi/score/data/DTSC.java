package org.oagi.score.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DTSC implements Serializable {

    private BigInteger dtScManifestId = BigInteger.ZERO;
    private String dtScId;
    private String guid;
    private String propertyTerm;
    private String representationTerm;
    private String definition;
    private String definitionSource;
    private String ownerDtId;
    private int cardinalityMin;
    private int cardinalityMax;
    private String basedDtScId;
    private int revisionNum;

    public String getDen() {
        return getPropertyTerm() + ". " + getRepresentationTerm();
    }

}
