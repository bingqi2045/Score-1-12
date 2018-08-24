package org.oagi.srt.gateway.http.api.cc_management.data;

import lombok.Data;

import java.util.Date;

@Data
public class CcList {

    private String type;
    private long id;
    private String guid;
    private String den;
    private String definition;
    private String definitionSource;
    private String owner;
    private CcState state;
    private String revision;
    private boolean deprecated;
    private Long currentId;
    private String lastUpdateUser;
    private Date lastUpdateTimestamp;

}
