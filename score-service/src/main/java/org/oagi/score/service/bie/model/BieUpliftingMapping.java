package org.oagi.score.service.bie.model;

import lombok.Data;

@Data
public class BieUpliftingMapping {

    public final static BieUpliftingMapping NULL_INSTANCE = new BieUpliftingMapping();

    private String bieType;
    private String bieId;
    private String sourceManifestId;
    private String sourcePath;
    private String targetManifestId;
    private String targetPath;
    private String refTopLevelAsbiepId;

}
