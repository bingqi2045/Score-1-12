package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit;

import lombok.Data;
import org.oagi.srt.data.BIE;

import java.math.BigInteger;

@Data
public class BieEditNode {

    private BigInteger topLevelAbieId;
    private BigInteger releaseId;
    private BigInteger ccManifestId;

    private String type;
    private String guid;
    private String name;
    private boolean used;
    private boolean required;
    private boolean hasChild;

}
