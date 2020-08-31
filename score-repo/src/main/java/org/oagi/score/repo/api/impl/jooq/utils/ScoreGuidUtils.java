package org.oagi.score.repo.api.impl.jooq.utils;

import java.util.UUID;

public abstract class ScoreGuidUtils {

    public static String randomGuid() {
        return "oagis-id-" + (UUID.randomUUID().toString().replace("-", ""));
    }
}
