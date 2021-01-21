package org.oagi.score.service.bie.model;

import lombok.Data;

import java.math.BigInteger;

@Data
public class BieUpliftingMapping {

    private String bieType;
    private BigInteger bieId;
    private BigInteger sourceManifestId;
    private String sourcePath;
    private BigInteger targetManifestId;
    private String targetPath;
    private BigInteger refTopLevelAsbiepId;

}
