package org.oagi.srt.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class DTSC implements Serializable {

    private long dtScId;
    private String guid;
    private String propertyTerm;
    private String representationTerm;
    private String definition;
    private String definitionSource;
    private long ownerDtId;
    private int cardinalityMin;
    private int cardinalityMax;
    private Long basedDtScId;

}
