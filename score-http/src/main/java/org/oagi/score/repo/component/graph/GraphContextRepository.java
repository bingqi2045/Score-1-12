package org.oagi.score.repo.component.graph;

import org.jooq.DSLContext;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GraphContextRepository {

    @Autowired
    private DSLContext dslContext;

    public CoreComponentGraphContext buildGraphContext(String releaseId) {
        return new CoreComponentGraphContext(dslContext, releaseId);
    }

    public CoreComponentGraphContext buildGraphContext(AccManifestRecord accManifest) {
        return buildGraphContext(accManifest.getReleaseId());
    }

    public CoreComponentGraphContext buildGraphContext(AsccpManifestRecord asccpManifest) {
        return buildGraphContext(asccpManifest.getReleaseId());
    }

    public CoreComponentGraphContext buildGraphContext(BccpManifestRecord bccpManifest) {
        return buildGraphContext(bccpManifest.getReleaseId());
    }

    public CoreComponentGraphContext buildGraphContext(DtManifestRecord dtManifest) {
        return buildGraphContext(dtManifest.getReleaseId());
    }

    public CodeListGraphContext buildGraphContext(CodeListManifestRecord codeListManifest) {
        return new CodeListGraphContext(dslContext, codeListManifest.getReleaseId());
    }
}
