package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree;

import lombok.Data;

import java.math.BigInteger;

@Data
public class BieEditRef {

    private String hashPath;
    private BigInteger refTopLevelAbieId;
}
