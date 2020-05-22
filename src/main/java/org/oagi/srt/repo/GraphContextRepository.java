package org.oagi.srt.repo;

import org.jooq.DSLContext;
import org.oagi.srt.entity.jooq.tables.records.AccManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.AsccpManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.BccpManifestRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public class GraphContextRepository {

    @Autowired
    private DSLContext dslContext;

    public GraphContext buildGraphContext(BigInteger releaseId) {
        return new GraphContext(dslContext, releaseId);
    }

    public GraphContext buildGraphContext(AccManifestRecord accManifest) {
        return buildGraphContext(accManifest.getReleaseId().toBigInteger());
    }

    public GraphContext buildGraphContext(AsccpManifestRecord asccpManifest) {
        return buildGraphContext(asccpManifest.getReleaseId().toBigInteger());
    }

    public GraphContext buildGraphContext(BccpManifestRecord bccpManifest) {
        return buildGraphContext(bccpManifest.getReleaseId().toBigInteger());
    }
}
