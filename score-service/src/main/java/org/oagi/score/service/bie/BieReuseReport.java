package org.oagi.score.service.bie;

import lombok.Data;

import java.math.BigInteger;

@Data
public class BieReuseReport {
    private String reusingTopLevelAsbiepId;
    private String reusingState;
    private String reusingPropertyTerm;
    private String reusingGuid;
    private String reusingOwner;
    private String reusingVersion;
    private String reusingStatus;
    private String path;
    private String displayPath;

    private String reusedTopLevelAsbiepId;
    private String reusedState;
    private String reusedPropertyTerm;
    private String reusedGuid;
    private String reusedOwner;
    private String reusedVersion;
    private String reusedStatus;

    private String releaseNum;
}
