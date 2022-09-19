package org.oagi.score.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;

@Data
public class BieEditBbieSc {

    private String bbieScId;
    private String bbieId;
    private String dtScManifestId;
    private boolean used;

}
