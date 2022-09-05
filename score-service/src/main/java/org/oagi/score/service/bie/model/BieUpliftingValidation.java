package org.oagi.score.service.bie.model;

import lombok.Data;

@Data
public class BieUpliftingValidation {
    private String bieType;
    private String bieId;
    private boolean isValid;
    private String message;
}
