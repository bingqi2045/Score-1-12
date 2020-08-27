package org.oagi.score.repo.component.bbiep;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.data.AppUser;
import org.oagi.score.repo.entity.jooq.tables.records.BbiepRecord;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.gateway.http.helper.SrtGuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static org.jooq.impl.DSL.and;
import static org.oagi.score.repo.entity.jooq.Tables.BBIEP;
import static org.oagi.score.gateway.http.helper.Utility.emptyToNull;

@Repository
public class BbiepWriteRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private BbiepReadRepository readRepository;

    public BbiepNode.Bbiep upsertBbiep(UpsertBbiepRequest request) {
        BbiepNode.Bbiep bbiep = request.getBbiep();
        ULong topLevelAsbiepId = ULong.valueOf(request.getTopLevelAsbiepId());
        String hashPath = bbiep.getHashPath();
        BbiepRecord bbiepRecord = dslContext.selectFrom(BBIEP)
                .where(and(
                        BBIEP.OWNER_TOP_LEVEL_ASBIEP_ID.eq(topLevelAsbiepId),
                        BBIEP.HASH_PATH.eq(hashPath)
                ))
                .fetchOptional().orElse(null);

        AppUser user = sessionService.getAppUser(request.getUser());
        ULong requesterId = ULong.valueOf(user.getAppUserId());

        if (bbiepRecord == null) {
            bbiepRecord = new BbiepRecord();
            bbiepRecord.setGuid(SrtGuid.randomGuid());
            bbiepRecord.setBasedBccpManifestId(ULong.valueOf(bbiep.getBasedBccpManifestId()));
            bbiepRecord.setPath(bbiep.getPath());
            bbiepRecord.setHashPath(hashPath);

            bbiepRecord.setDefinition(bbiep.getDefinition());
            bbiepRecord.setRemark(bbiep.getRemark());
            bbiepRecord.setBizTerm(bbiep.getBizTerm());

            bbiepRecord.setOwnerTopLevelAsbiepId(topLevelAsbiepId);

            bbiepRecord.setCreatedBy(requesterId);
            bbiepRecord.setLastUpdatedBy(requesterId);
            bbiepRecord.setCreationTimestamp(request.getLocalDateTime());
            bbiepRecord.setLastUpdateTimestamp(request.getLocalDateTime());

            bbiepRecord.setBbiepId(
                    dslContext.insertInto(BBIEP)
                            .set(bbiepRecord)
                            .returning(BBIEP.BBIEP_ID)
                            .fetchOne().getBbiepId()
            );
        } else {
            if (bbiep.getDefinition() != null) {
                bbiepRecord.setDefinition(emptyToNull(bbiep.getDefinition()));
            }

            if (bbiep.getRemark() != null) {
                bbiepRecord.setRemark(emptyToNull(bbiep.getRemark()));
            }

            if (bbiep.getBizTerm() != null) {
                bbiepRecord.setBizTerm(emptyToNull(bbiep.getBizTerm()));
            }

            if (bbiepRecord.changed()) {
                bbiepRecord.setLastUpdatedBy(requesterId);
                bbiepRecord.setLastUpdateTimestamp(request.getLocalDateTime());

                bbiepRecord.update(
                        BBIEP.DEFINITION,
                        BBIEP.REMARK,
                        BBIEP.BIZ_TERM,
                        BBIEP.LAST_UPDATED_BY,
                        BBIEP.LAST_UPDATE_TIMESTAMP
                );
            }
        }

        return readRepository.getBbiep(request.getTopLevelAsbiepId(), hashPath);
    }
}
