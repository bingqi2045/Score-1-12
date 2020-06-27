package org.oagi.srt.repo.component.abie;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.AppUser;
import org.oagi.srt.entity.jooq.tables.records.AbieRecord;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.ABIE;

@Repository
public class AbieWriteRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private AbieReadRepository readRepository;

    public AbieNode.Abie upsertAbie(UpsertAbieRequest request) {
        AbieNode.Abie abie = request.getAbie();
        ULong topLevelAbieId = ULong.valueOf(request.getTopLevelAbieId());
        String hashPath = abie.getHashPath();
        AbieRecord abieRecord = dslContext.selectFrom(ABIE)
                .where(and(
                        ABIE.OWNER_TOP_LEVEL_ABIE_ID.eq(topLevelAbieId),
                        ABIE.HASH_PATH.eq(hashPath)
                ))
                .fetchOptional().orElse(null);

        AppUser user = sessionService.getAppUser(request.getUser());
        ULong requesterId = ULong.valueOf(user.getAppUserId());

        if (abieRecord == null) {
            abieRecord = new AbieRecord();
            abieRecord.setGuid(SrtGuid.randomGuid());
            abieRecord.setBasedAccManifestId(ULong.valueOf(abie.getBasedAccManifestId()));
            abieRecord.setPath(abie.getPath());
            abieRecord.setHashPath(hashPath);

            abieRecord.setDefinition(abie.getDefinition());
            if (abie.getClientId() != null) {
                abieRecord.setClientId(ULong.valueOf(abie.getClientId()));
            }
            abieRecord.setVersion(abie.getVersion());
            abieRecord.setRemark(abie.getRemark());
            abieRecord.setBizTerm(abie.getBizTerm());
            abieRecord.setStatus(abie.getStatus());

            abieRecord.setOwnerTopLevelAbieId(topLevelAbieId);

            abieRecord.setCreatedBy(requesterId);
            abieRecord.setLastUpdatedBy(requesterId);
            abieRecord.setCreationTimestamp(request.getLocalDateTime());
            abieRecord.setLastUpdateTimestamp(request.getLocalDateTime());

            abieRecord.setAbieId(
                    dslContext.insertInto(ABIE)
                            .set(abieRecord)
                            .returning(ABIE.ABIE_ID)
                            .fetchOne().getAbieId()
            );
        } else {
            abieRecord.setDefinition(abie.getDefinition());
            if (abie.getClientId() != null) {
                abieRecord.setClientId(ULong.valueOf(abie.getClientId()));
            }
            abieRecord.setVersion(abie.getVersion());
            abieRecord.setRemark(abie.getRemark());
            abieRecord.setBizTerm(abie.getBizTerm());
            abieRecord.setStatus(abie.getStatus());

            abieRecord.setLastUpdatedBy(requesterId);
            abieRecord.setLastUpdateTimestamp(request.getLocalDateTime());

            abieRecord.update(
                    ABIE.DEFINITION,
                    ABIE.CLIENT_ID,
                    ABIE.VERSION,
                    ABIE.REMARK,
                    ABIE.BIZ_TERM,
                    ABIE.STATUS,
                    ABIE.LAST_UPDATED_BY,
                    ABIE.LAST_UPDATE_TIMESTAMP
            );
        }

        return readRepository.getAbie(request.getTopLevelAbieId(), hashPath);
    }

}
