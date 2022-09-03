package org.oagi.score.service.bie.model;

import lombok.Data;

import java.math.BigInteger;

@Data
public class BieUpliftingValidation {
    private String bieType;
    private String bieId;
    private boolean isValid;
    private String message;
}
