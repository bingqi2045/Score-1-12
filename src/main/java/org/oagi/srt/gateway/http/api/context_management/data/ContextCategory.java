package org.oagi.srt.gateway.http.api.context_management.data;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

@Data
public class ContextCategory implements Serializable {

    private BigInteger ctxCategoryId;
    private String guid;
    private String name;
    private String description;
    private Date lastUpdateTimestamp;
    private String lastUpdateUser;

    private boolean used;

}
