package org.oagi.srt.repo;

import org.jooq.DSLContext;
import org.oagi.srt.entity.jooq.tables.records.AccManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.AsccpManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.BccpManifestRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GraphContextRepository {

    @Autowired
    private DSLContext dslContext;

    public GraphContext buildGraphContext(AccManifestRecord accManifest) {
        return new GraphContext(dslContext, accManifest.getReleaseId());
    }

    public GraphContext buildGraphContext(AsccpManifestRecord asccpManifest) {
        return new GraphContext(dslContext, asccpManifest.getReleaseId());
    }

    public GraphContext buildGraphContext(BccpManifestRecord bccpManifest) {
        return new GraphContext(dslContext, bccpManifest.getReleaseId());
    }
}
