package org.oagi.srt.gateway.http.api.cc_management.data;

import lombok.Data;
import org.oagi.srt.gateway.http.api.common.data.Trackable;

import java.io.Serializable;
import java.util.Date;

@Data
public class CCList extends Trackable implements Serializable {

    private String type;
    private long id;
    private String guid;
    private String den;
    private String owner;
    private Object state;
    private String revision;
    private boolean deprecated;
    private Long currentId;
    private String lastUpdateUser;
    private Date lastUpdateTimestamp;

}
