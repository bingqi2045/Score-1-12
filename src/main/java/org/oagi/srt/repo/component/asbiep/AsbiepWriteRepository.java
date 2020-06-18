package org.oagi.srt.repo.component.asbiep;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.AppUser;
import org.oagi.srt.entity.jooq.tables.records.AsbiepRecord;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.ABIE;
import static org.oagi.srt.entity.jooq.Tables.ASBIEP;

@Repository
public class AsbiepWriteRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private AsbiepReadRepository readRepository;

    public AsbiepNode.Asbiep upsertAsbiep(UpsertAsbiepRequest request) {
        AsbiepNode.Asbiep asbiep = request.getAsbiep();
        ULong topLevelAbieId = ULong.valueOf(request.getTopLevelAbieId());
        String hashPath = asbiep.getHashPath();
        AsbiepRecord asbiepRecord = dslContext.selectFrom(ASBIEP)
                .where(and(
                        ASBIEP.OWNER_TOP_LEVEL_ABIE_ID.eq(topLevelAbieId),
                        ASBIEP.HASH_PATH.eq(hashPath)
                ))
                .fetchOptional().orElse(null);

        AppUser user = sessionService.getAppUser(request.getUser());
        ULong requesterId = ULong.valueOf(user.getAppUserId());

        if (asbiepRecord == null) {
            asbiepRecord = new AsbiepRecord();
            asbiepRecord.setGuid(SrtGuid.randomGuid());
            asbiepRecord.setBasedAsccpManifestId(ULong.valueOf(asbiep.getBasedAsccpManifestId()));
            asbiepRecord.setHashPath(hashPath);
            asbiepRecord.setRoleOfAbieId(dslContext.select(ABIE.ABIE_ID)
                    .from(ABIE)
                    .where(and(
                            ABIE.OWNER_TOP_LEVEL_ABIE_ID.eq(topLevelAbieId),
                            ABIE.HASH_PATH.eq(asbiep.getRoleOfAbieHashPath())
                    ))
                    .fetchOneInto(ULong.class));

            asbiepRecord.setDefinition(asbiep.getDefinition());
            asbiepRecord.setRemark(asbiep.getRemark());
            asbiepRecord.setBizTerm(asbiep.getBizTerm());

            asbiepRecord.setOwnerTopLevelAbieId(topLevelAbieId);
            if (request.getRefTopLevelAbieId() != null) {
                asbiepRecord.setRefTopLevelAbieId(ULong.valueOf(request.getRefTopLevelAbieId()));
            }

            asbiepRecord.setCreatedBy(requesterId);
            asbiepRecord.setLastUpdatedBy(requesterId);
            asbiepRecord.setCreationTimestamp(request.getLocalDateTime());
            asbiepRecord.setLastUpdateTimestamp(request.getLocalDateTime());

            asbiepRecord.setAsbiepId(
                    dslContext.insertInto(ASBIEP)
                            .set(asbiepRecord)
                            .returning(ASBIEP.ASBIEP_ID)
                            .fetchOne().getAsbiepId()
            );
        } else {
            asbiepRecord.setDefinition(asbiep.getDefinition());
            asbiepRecord.setRemark(asbiep.getRemark());
            asbiepRecord.setBizTerm(asbiep.getBizTerm());

            if (request.getRefTopLevelAbieId() != null) {
                asbiepRecord.setRefTopLevelAbieId(ULong.valueOf(request.getRefTopLevelAbieId()));
            }

            asbiepRecord.setLastUpdatedBy(requesterId);
            asbiepRecord.setLastUpdateTimestamp(request.getLocalDateTime());

            asbiepRecord.update(
                    ASBIEP.DEFINITION,
                    ASBIEP.REMARK,
                    ASBIEP.BIZ_TERM,
                    ASBIEP.REF_TOP_LEVEL_ABIE_ID,
                    ASBIEP.LAST_UPDATED_BY,
                    ASBIEP.LAST_UPDATE_TIMESTAMP
            );
        }

        return readRepository.getAsbiep(request.getTopLevelAbieId(), hashPath);
    }
    
}
