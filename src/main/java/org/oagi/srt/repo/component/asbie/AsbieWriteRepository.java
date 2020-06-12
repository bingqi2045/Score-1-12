package org.oagi.srt.repo.component.asbie;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.AppUser;
import org.oagi.srt.entity.jooq.tables.records.AsbieRecord;
import org.oagi.srt.entity.jooq.tables.records.AsccRecord;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.repo.component.ascc.AsccReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;

@Repository
public class AsbieWriteRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private AsccReadRepository asccReadRepository;

    @Autowired
    private AsbieReadRepository asbieReadRepository;

    public AsbieNode.Asbie upsertAsbie(UpsertAsbieRequest request) {
        AsbieNode.Asbie asbie = request.getAsbie();
        ULong topLevelAbieId = ULong.valueOf(request.getTopLevelAbieId());
        String hashPath = asbie.getHashPath();
        AsbieRecord asbieRecord = dslContext.selectFrom(ASBIE)
                .where(and(
                        ASBIE.OWNER_TOP_LEVEL_ABIE_ID.eq(topLevelAbieId),
                        ASBIE.HASH_PATH.eq(hashPath)
                ))
                .fetchOptional().orElse(null);

        AppUser user = sessionService.getAppUser(request.getUser());
        ULong requesterId = ULong.valueOf(user.getAppUserId());

        if (asbieRecord == null) {
            asbieRecord = new AsbieRecord();
            asbieRecord.setGuid(SrtGuid.randomGuid());
            asbieRecord.setBasedAsccManifestId(ULong.valueOf(asbie.getBasedAsccManifestId()));
            asbieRecord.setHashPath(hashPath);
            asbieRecord.setFromAbieId(dslContext.select(ABIE.ABIE_ID)
                    .from(ABIE)
                    .where(and(
                            ABIE.OWNER_TOP_LEVEL_ABIE_ID.eq(topLevelAbieId),
                            ABIE.HASH_PATH.eq(asbie.getFromAbieHashPath())
                    ))
                    .fetchOneInto(ULong.class));
            asbieRecord.setToAsbiepId(dslContext.select(ASBIEP.ASBIEP_ID)
                    .from(ASBIEP)
                    .where(and(
                            ASBIEP.OWNER_TOP_LEVEL_ABIE_ID.eq(topLevelAbieId),
                            ASBIEP.HASH_PATH.eq(asbie.getToAsbiepHashPath())
                    ))
                    .fetchOneInto(ULong.class));
            asbieRecord.setSeqKey(BigDecimal.valueOf(asbie.getSeqKey().longValue()));

            asbieRecord.setIsUsed((byte) (asbie.isUsed() ? 1 : 0));
            asbieRecord.setIsNillable((byte) (asbie.isNillable() ? 1 : 0));
            asbieRecord.setDefinition(asbie.getDefinition());
            if (asbie.isEmptyCardinality()) {
                AsccRecord asccRecord = asccReadRepository.getAsccByManifestId(asbie.getBasedAsccManifestId());
                if (asccRecord == null) {
                    throw new IllegalArgumentException();
                }

                asbieRecord.setCardinalityMin(asccRecord.getCardinalityMin());
                asbieRecord.setCardinalityMax(asccRecord.getCardinalityMax());
            } else {
                asbieRecord.setCardinalityMin(asbie.getCardinalityMin());
                asbieRecord.setCardinalityMax(asbie.getCardinalityMax());
            }
            asbieRecord.setRemark(asbie.getRemark());

            asbieRecord.setOwnerTopLevelAbieId(topLevelAbieId);

            asbieRecord.setCreatedBy(requesterId);
            asbieRecord.setLastUpdatedBy(requesterId);
            asbieRecord.setCreationTimestamp(request.getLocalDateTime());
            asbieRecord.setLastUpdateTimestamp(request.getLocalDateTime());

            asbieRecord.setAsbieId(
                    dslContext.insertInto(ASBIE)
                            .set(asbieRecord)
                            .returning(ASBIE.ASBIE_ID)
                            .fetchOne().getAsbieId()
            );
        } else {
            asbieRecord.setSeqKey(BigDecimal.valueOf(asbie.getSeqKey().longValue()));
            asbieRecord.setIsUsed((byte) (asbie.isUsed() ? 1 : 0));
            asbieRecord.setIsNillable((byte) (asbie.isNillable() ? 1 : 0));
            asbieRecord.setDefinition(asbie.getDefinition());
            if (asbie.isEmptyCardinality()) {
                throw new IllegalArgumentException();
            }
            asbieRecord.setCardinalityMin(asbie.getCardinalityMin());
            asbieRecord.setCardinalityMax(asbie.getCardinalityMax());
            asbieRecord.setRemark(asbie.getRemark());

            asbieRecord.setLastUpdatedBy(requesterId);
            asbieRecord.setLastUpdateTimestamp(request.getLocalDateTime());

            asbieRecord.update(
                    ASBIE.SEQ_KEY,
                    ASBIE.IS_USED,
                    ASBIE.IS_NILLABLE,
                    ASBIE.DEFINITION,
                    ASBIE.CARDINALITY_MIN,
                    ASBIE.CARDINALITY_MAX,
                    ASBIE.REMARK,
                    ASBIE.LAST_UPDATED_BY,
                    ASBIE.LAST_UPDATE_TIMESTAMP
            );
        }

        return asbieReadRepository.getAsbie(request.getTopLevelAbieId(), hashPath);
    }

}
