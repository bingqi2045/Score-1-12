package org.oagi.score.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class Release {

    private String releaseId;
    private String guid;
    private String releaseNum;
    private String releaseNote;
    private String releaseLicense;
    private String namespaceId;
    private String createdBy;
    private String lastUpdatedBy;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;
    private String state;

}
