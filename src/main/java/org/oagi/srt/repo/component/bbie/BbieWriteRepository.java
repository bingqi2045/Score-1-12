package org.oagi.srt.repo.component.bbie;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.AppUser;
import org.oagi.srt.entity.jooq.tables.records.BbieRecord;
import org.oagi.srt.entity.jooq.tables.records.BccRecord;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.repo.component.bcc.BccReadRepository;
import org.oagi.srt.repo.component.bdt_pri_restri.AvailableBdtPriRestri;
import org.oagi.srt.repo.component.bdt_pri_restri.BdtPriRestriReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;

@Repository
public class BbieWriteRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private BccReadRepository bccReadRepository;

    @Autowired
    private BdtPriRestriReadRepository bdtPriRestriReadRepository;

    @Autowired
    private BbieReadRepository bbieReadRepository;

    public BbieNode.Bbie upsertBbie(UpsertBbieRequest request) {
        BbieNode.Bbie bbie = request.getBbie();
        ULong topLevelAbieId = ULong.valueOf(request.getTopLevelAbieId());
        String hashPath = bbie.getHashPath();
        BbieRecord bbieRecord = dslContext.selectFrom(BBIE)
                .where(and(
                        BBIE.OWNER_TOP_LEVEL_ABIE_ID.eq(topLevelAbieId),
                        BBIE.HASH_PATH.eq(hashPath)
                ))
                .fetchOptional().orElse(null);

        AppUser user = sessionService.getAppUser(request.getUser());
        ULong requesterId = ULong.valueOf(user.getAppUserId());

        if (bbieRecord == null) {
            bbieRecord = new BbieRecord();
            bbieRecord.setGuid(SrtGuid.randomGuid());
            bbieRecord.setBasedBccManifestId(ULong.valueOf(bbie.getBasedBccManifestId()));
            bbieRecord.setHashPath(hashPath);
            bbieRecord.setFromAbieId(dslContext.select(ABIE.ABIE_ID)
                    .from(ABIE)
                    .where(and(
                            ABIE.OWNER_TOP_LEVEL_ABIE_ID.eq(topLevelAbieId),
                            ABIE.HASH_PATH.eq(bbie.getFromAbieHashPath())
                    ))
                    .fetchOneInto(ULong.class));
            bbieRecord.setToBbiepId(dslContext.select(BBIEP.BBIEP_ID)
                    .from(BBIEP)
                    .where(and(
                            BBIEP.OWNER_TOP_LEVEL_ABIE_ID.eq(topLevelAbieId),
                            BBIEP.HASH_PATH.eq(bbie.getToBbiepHashPath())
                    ))
                    .fetchOneInto(ULong.class));
            bbieRecord.setSeqKey(BigDecimal.valueOf(bbie.getSeqKey().longValue()));

            bbieRecord.setIsUsed((byte) (bbie.isUsed() ? 1 : 0));
            bbieRecord.setIsNillable((byte) (bbie.isNillable() ? 1 : 0));
            bbieRecord.setDefinition(bbie.getDefinition());
            if (bbie.isEmptyCardinality()) {
                BccRecord bccRecord = bccReadRepository.getBccByManifestId(bbie.getBasedBccManifestId());
                if (bccRecord == null) {
                    throw new IllegalArgumentException();
                }

                bbieRecord.setCardinalityMin(bccRecord.getCardinalityMin());
                bbieRecord.setCardinalityMax(bccRecord.getCardinalityMax());
            } else {
                bbieRecord.setCardinalityMin(bbie.getCardinalityMin());
                bbieRecord.setCardinalityMax(bbie.getCardinalityMax());
            }
            bbieRecord.setExample(bbie.getExample());
            bbieRecord.setRemark(bbie.getRemark());

            if (!StringUtils.isEmpty(bbie.getDefaultValue())) {
                bbieRecord.setDefaultValue(bbie.getDefaultValue());
                bbieRecord.setFixedValue(null);
            } else if (!StringUtils.isEmpty(bbie.getFixedValue())) {
                bbieRecord.setDefaultValue(null);
                bbieRecord.setFixedValue(bbie.getFixedValue());
            }

            if (bbie.isEmptyPrimitive()) {
                List<AvailableBdtPriRestri> bdtPriRestriList =
                        bdtPriRestriReadRepository.availableBdtPriRestriListByBccManifestId(bbie.getBasedBccManifestId());
                bdtPriRestriList = bdtPriRestriList.stream().filter(e -> e.isDefault())
                        .collect(Collectors.toList());
                if (bdtPriRestriList.size() != 1) {
                    throw new IllegalArgumentException();
                }

                bbieRecord.setBdtPriRestriId(ULong.valueOf(bdtPriRestriList.get(0).getBdtPriRestriId()));
            } else {
                if (bbie.getBdtPriRestriId() != null) {
                    bbieRecord.setBdtPriRestriId(ULong.valueOf(bbie.getBdtPriRestriId()));
                    bbieRecord.setCodeListId(null);
                    bbieRecord.setAgencyIdListId(null);
                } else if (bbie.getCodeListId() != null) {
                    bbieRecord.setBdtPriRestriId(null);
                    bbieRecord.setCodeListId(ULong.valueOf(bbie.getCodeListId()));
                    bbieRecord.setAgencyIdListId(null);
                } else if (bbie.getAgencyIdListId() != null) {
                    bbieRecord.setBdtPriRestriId(null);
                    bbieRecord.setCodeListId(null);
                    bbieRecord.setAgencyIdListId(ULong.valueOf(bbie.getAgencyIdListId()));
                }
            }

            bbieRecord.setOwnerTopLevelAbieId(topLevelAbieId);

            bbieRecord.setCreatedBy(requesterId);
            bbieRecord.setLastUpdatedBy(requesterId);
            bbieRecord.setCreationTimestamp(request.getLocalDateTime());
            bbieRecord.setLastUpdateTimestamp(request.getLocalDateTime());

            bbieRecord.setBbieId(
                    dslContext.insertInto(BBIE)
                            .set(bbieRecord)
                            .returning(BBIE.BBIE_ID)
                            .fetchOne().getBbieId()
            );
        } else {
            bbieRecord.setSeqKey(BigDecimal.valueOf(bbie.getSeqKey().longValue()));
            bbieRecord.setIsUsed((byte) (bbie.isUsed() ? 1 : 0));
            bbieRecord.setIsNillable((byte) (bbie.isNillable() ? 1 : 0));
            bbieRecord.setDefinition(bbie.getDefinition());
            if (!bbie.isEmptyCardinality()) {
                bbieRecord.setCardinalityMin(bbie.getCardinalityMin());
                bbieRecord.setCardinalityMax(bbie.getCardinalityMax());
            }
            bbieRecord.setExample(bbie.getExample());
            bbieRecord.setRemark(bbie.getRemark());

            if (!StringUtils.isEmpty(bbie.getDefaultValue())) {
                bbieRecord.setDefaultValue(bbie.getDefaultValue());
                bbieRecord.setFixedValue(null);
            } else if (!StringUtils.isEmpty(bbie.getFixedValue())) {
                bbieRecord.setDefaultValue(null);
                bbieRecord.setFixedValue(bbie.getFixedValue());
            }

            if (!bbie.isEmptyPrimitive()) {
                if (bbie.getBdtPriRestriId() != null) {
                    bbieRecord.setBdtPriRestriId(ULong.valueOf(bbie.getBdtPriRestriId()));
                    bbieRecord.setCodeListId(null);
                    bbieRecord.setAgencyIdListId(null);
                } else if (bbie.getCodeListId() != null) {
                    bbieRecord.setBdtPriRestriId(null);
                    bbieRecord.setCodeListId(ULong.valueOf(bbie.getCodeListId()));
                    bbieRecord.setAgencyIdListId(null);
                } else if (bbie.getAgencyIdListId() != null) {
                    bbieRecord.setBdtPriRestriId(null);
                    bbieRecord.setCodeListId(null);
                    bbieRecord.setAgencyIdListId(ULong.valueOf(bbie.getAgencyIdListId()));
                }
            }

            bbieRecord.setLastUpdatedBy(requesterId);
            bbieRecord.setLastUpdateTimestamp(request.getLocalDateTime());

            bbieRecord.update(
                    BBIE.SEQ_KEY,
                    BBIE.IS_USED,
                    BBIE.IS_NILLABLE,
                    BBIE.DEFINITION,
                    BBIE.CARDINALITY_MIN,
                    BBIE.CARDINALITY_MAX,
                    BBIE.EXAMPLE,
                    BBIE.REMARK,
                    BBIE.DEFAULT_VALUE,
                    BBIE.FIXED_VALUE,
                    BBIE.BDT_PRI_RESTRI_ID,
                    BBIE.CODE_LIST_ID,
                    BBIE.AGENCY_ID_LIST_ID,
                    BBIE.LAST_UPDATED_BY,
                    BBIE.LAST_UPDATE_TIMESTAMP
            );
        }

        return bbieReadRepository.getBbie(request.getTopLevelAbieId(), hashPath);
    }

}
