package org.oagi.score.gateway.http.api.context_management.data;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
public class BusinessContext implements Serializable {

    private BigInteger bizCtxId;
    private String guid;
    private String name;
    private Date lastUpdateTimestamp;
    private String lastUpdateUser;
    private List<BusinessContextValue> bizCtxValues = Collections.emptyList();
    private boolean used;

}
