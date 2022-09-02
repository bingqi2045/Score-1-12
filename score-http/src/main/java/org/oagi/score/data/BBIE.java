package org.oagi.score.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class BBIE implements BIE, FacetRestrictionsAware {

    private BigInteger bbieId = BigInteger.ZERO;
    private String guid;
    private BigInteger basedBccManifestId = BigInteger.ZERO;
    private BigInteger fromAbieId = BigInteger.ZERO;
    private BigInteger toBbiepId = BigInteger.ZERO;
    private BigInteger bdtPriRestriId = BigInteger.ZERO;
    private String codeListId;
    private String agencyIdListId;
    private int cardinalityMin;
    private int cardinalityMax;
    private BigInteger minLength;
    private BigInteger maxLength;
    private String pattern;
    private String defaultValue;
    private boolean nillable;
    private String fixedValue;
    private boolean nill;
    private String definition;
    private String remark;
    private String example;
    private String createdBy;
    private String lastUpdatedBy;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;
    private double seqKey;
    private boolean used;
    private BigInteger ownerTopLevelAsbiepId = BigInteger.ZERO;
}
