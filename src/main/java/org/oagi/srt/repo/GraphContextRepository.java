package org.oagi.srt.repo;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
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
        throw new UnsupportedOperationException();
    }

    public GraphContext buildGraphContext(AsccpManifestRecord asccpManifest) {
        throw new UnsupportedOperationException();
    }

    public GraphContext buildGraphContext(BccpManifestRecord bccpManifest) {
        return new GraphContext(dslContext, bccpManifest.getReleaseId());
    }
}
