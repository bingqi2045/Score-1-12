package org.oagi.srt.repo;

import lombok.Data;
import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.oagi.srt.entity.jooq.Tables.*;

@Data
public class GraphContext {

    private DSLContext dslContext;

    private Map<ULong, AccManifestRecord> accManifestMap;
    private Map<ULong, AsccpManifestRecord> asccpManifestMap;
    private Map<ULong, BccpManifestRecord> bccpManifestMap;
    private Map<ULong, List<AsccManifestRecord>> asccManifestMap;
    private Map<ULong, List<BccManifestRecord>> bccManifestMap;
    private Map<ULong, DtManifestRecord> dtManifestMap;
    private Map<ULong, List<DtScManifestRecord>> dtScManifestMap;

    public GraphContext(DSLContext dslContext, ULong releaseId) {
        this.dslContext = dslContext;

        accManifestMap =
                dslContext.selectFrom(ACC_MANIFEST)
                        .where(ACC_MANIFEST.RELEASE_ID.eq(releaseId))
                        .fetchStream().collect(Collectors.toMap(AccManifestRecord::getAccManifestId, Function.identity()));
        asccpManifestMap =
                dslContext.selectFrom(ASCCP_MANIFEST)
                        .where(ASCCP_MANIFEST.RELEASE_ID.eq(releaseId))
                        .fetchStream().collect(Collectors.toMap(AsccpManifestRecord::getAsccpManifestId, Function.identity()));
        bccpManifestMap =
                dslContext.selectFrom(BCCP_MANIFEST)
                        .where(BCCP_MANIFEST.RELEASE_ID.eq(releaseId))
                        .fetchStream().collect(Collectors.toMap(BccpManifestRecord::getBccpManifestId, Function.identity()));
        asccManifestMap =
                dslContext.selectFrom(ASCC_MANIFEST)
                        .where(ASCC_MANIFEST.RELEASE_ID.eq(releaseId))
                        .fetchStream().collect(groupingBy(AsccManifestRecord::getFromAccManifestId));
        bccManifestMap =
                dslContext.selectFrom(BCC_MANIFEST)
                        .where(BCC_MANIFEST.RELEASE_ID.eq(releaseId))
                        .fetchStream().collect(groupingBy(BccManifestRecord::getFromAccManifestId));
        dtManifestMap =
                dslContext.selectFrom(DT_MANIFEST)
                        .where(DT_MANIFEST.RELEASE_ID.eq(releaseId))
                        .fetchStream().collect(Collectors.toMap(DtManifestRecord::getDtManifestId, Function.identity()));
        dtScManifestMap =
                dslContext.selectFrom(DT_SC_MANIFEST)
                        .where(DT_SC_MANIFEST.RELEASE_ID.eq(releaseId))
                        .fetchStream().collect(groupingBy(DtScManifestRecord::getOwnerDtManifestId));
    }

    public List<?> findChildren(Object node) {
        if (node instanceof AccManifestRecord) {
            throw new UnsupportedOperationException();
        }
        else if (node instanceof AsccpManifestRecord) {
            throw new UnsupportedOperationException();
        }
        else if (node instanceof BccpManifestRecord) {
            BccpManifestRecord bccpManifest = (BccpManifestRecord) node;
            DtManifestRecord dtManifest = dtManifestMap.get(bccpManifest.getBdtManifestId());
            return (dtManifest != null) ? Arrays.asList(dtManifest) : Collections.emptyList();
        }
        else if (node instanceof DtManifestRecord) {
            DtManifestRecord dtManifest = (DtManifestRecord) node;
            return dtScManifestMap.getOrDefault(dtManifest.getDtManifestId(), Collections.emptyList());
        }
        return Collections.emptyList();
    }
}
