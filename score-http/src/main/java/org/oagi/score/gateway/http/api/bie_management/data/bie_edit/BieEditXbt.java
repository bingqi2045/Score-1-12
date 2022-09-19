package org.oagi.score.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;

@Data
public class BieEditXbt {

    private String priRestriId;
    private boolean isDefault;
    private String xbtId;
    private String xbtName;

}
