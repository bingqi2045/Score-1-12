package org.oagi.score.export.model;

import lombok.Data;
import org.jooq.types.ULong;

@Data
public class ScoreModule {

    private String moduleSetReleaseId;
    private ULong moduleSetId;
    private String releaseId;
    private String moduleId;
    private String name;
    private String moduleNamespaceId;
    private String moduleNamespaceUri;
    private String moduleNamespacePrefix;
    private String releaseNamespaceId;
    private String releaseNamespaceUri;
    private String releaseNamespacePrefix;
    private String versionNum;
    private ULong moduleDirId;
    private String path;

    public String getModulePath() {
        return this.path;
    }
}
