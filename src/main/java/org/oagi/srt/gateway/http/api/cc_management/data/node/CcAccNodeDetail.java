package org.oagi.srt.gateway.http.api.cc_management.data.node;

import lombok.Data;

@Data
public class CcAccNodeDetail implements CcNodeDetail {
    private String type = "acc";
    private long accId;
    private String guid;
    private String objectClassTerm;
    private String den;
    private int componentType;
    private boolean abstracted;
    private boolean deprecated;
    private String definition;
}
