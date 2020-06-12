package org.oagi.srt.repo.component.bbie_sc;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.AppUser;
import org.oagi.srt.entity.jooq.tables.records.BbieScRecord;
import org.oagi.srt.entity.jooq.tables.records.DtScRecord;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.repo.component.bdt_sc_pri_restri.AvailableBdtScPriRestri;
import org.oagi.srt.repo.component.bdt_sc_pri_restri.BdtScPriRestriReadRepository;
import org.oagi.srt.repo.component.dt_sc.DtScReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.BBIE;
import static org.oagi.srt.entity.jooq.Tables.BBIE_SC;

@Repository
public class BbieScWriteRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private DtScReadRepository dtScReadRepository;

    @Autowired
    private BdtScPriRestriReadRepository bdtScPriRestriReadRepository;

    @Autowired
    private BbieScReadRepository bbieScReadRepository;

    public BbieScNode.BbieSc upsertBbieSc(UpsertBbieScRequest request) {
        BbieScNode.BbieSc bbieSc = request.getBbieSc();
        ULong topLevelAbieId = ULong.valueOf(request.getTopLevelAbieId());
        String hashPath = bbieSc.getHashPath();
        BbieScRecord bbieScRecord = dslContext.selectFrom(BBIE_SC)
                .where(and(
                        BBIE_SC.OWNER_TOP_LEVEL_ABIE_ID.eq(topLevelAbieId),
                        BBIE_SC.HASH_PATH.eq(hashPath)
                ))
                .fetchOptional().orElse(null);

        AppUser user = sessionService.getAppUser(request.getUser());
        ULong requesterId = ULong.valueOf(user.getAppUserId());

        if (bbieScRecord == null) {
            bbieScRecord = new BbieScRecord();
            bbieScRecord.setGuid(SrtGuid.randomGuid());
            bbieScRecord.setBasedDtScManifestId(ULong.valueOf(bbieSc.getBasedDtScManifestId()));
            bbieScRecord.setHashPath(hashPath);
            bbieScRecord.setBbieId(dslContext.select(BBIE.BBIE_ID)
                    .from(BBIE)
                    .where(and(
                            BBIE.OWNER_TOP_LEVEL_ABIE_ID.eq(topLevelAbieId),
                            BBIE.HASH_PATH.eq(bbieSc.getBbieHashPath())
                    ))
                    .fetchOneInto(ULong.class));

            bbieScRecord.setIsUsed((byte) (bbieSc.isUsed() ? 1 : 0));
            bbieScRecord.setDefinition(bbieSc.getDefinition());
            if (bbieSc.isEmptyCardinality()) {
                DtScRecord dtScRecord = dtScReadRepository.getDtScByManifestId(bbieSc.getBasedDtScManifestId());
                if (dtScRecord == null) {
                    throw new IllegalArgumentException();
                }

                bbieScRecord.setCardinalityMin(dtScRecord.getCardinalityMin());
                bbieScRecord.setCardinalityMax(dtScRecord.getCardinalityMax());
            } else {
                bbieScRecord.setCardinalityMin(bbieSc.getCardinalityMin());
                bbieScRecord.setCardinalityMax(bbieSc.getCardinalityMax());
            }
            bbieScRecord.setExample(bbieSc.getExample());
            bbieScRecord.setRemark(bbieSc.getRemark());

            if (!StringUtils.isEmpty(bbieSc.getDefaultValue())) {
                bbieScRecord.setDefaultValue(bbieSc.getDefaultValue());
                bbieScRecord.setFixedValue(null);
            } else if (!StringUtils.isEmpty(bbieSc.getFixedValue())) {
                bbieScRecord.setDefaultValue(null);
                bbieScRecord.setFixedValue(bbieSc.getFixedValue());
            }

            if (bbieSc.isEmptyPrimitive()) {
                List<AvailableBdtScPriRestri> bdtScPriRestriList =
                        bdtScPriRestriReadRepository.availableBdtScPriRestriListByBdtScManifestId(bbieSc.getBasedDtScManifestId());
                bdtScPriRestriList = bdtScPriRestriList.stream().filter(e -> e.isDefault())
                        .collect(Collectors.toList());
                if (bdtScPriRestriList.size() != 1) {
                    throw new IllegalArgumentException();
                }

                bbieScRecord.setDtScPriRestriId(ULong.valueOf(bdtScPriRestriList.get(0).getBdtScPriRestriId()));
            } else {
                if (bbieSc.getBdtScPriRestriId() != null) {
                    bbieScRecord.setDtScPriRestriId(ULong.valueOf(bbieSc.getBdtScPriRestriId()));
                    bbieScRecord.setCodeListId(null);
                    bbieScRecord.setAgencyIdListId(null);
                } else if (bbieSc.getCodeListId() != null) {
                    bbieScRecord.setDtScPriRestriId(null);
                    bbieScRecord.setCodeListId(ULong.valueOf(bbieSc.getCodeListId()));
                    bbieScRecord.setAgencyIdListId(null);
                } else if (bbieSc.getAgencyIdListId() != null) {
                    bbieScRecord.setDtScPriRestriId(null);
                    bbieScRecord.setCodeListId(null);
                    bbieScRecord.setAgencyIdListId(ULong.valueOf(bbieSc.getAgencyIdListId()));
                }
            }

            bbieScRecord.setOwnerTopLevelAbieId(topLevelAbieId);

            bbieScRecord.setCreatedBy(requesterId);
            bbieScRecord.setLastUpdatedBy(requesterId);
            bbieScRecord.setCreationTimestamp(request.getLocalDateTime());
            bbieScRecord.setLastUpdateTimestamp(request.getLocalDateTime());

            bbieScRecord.setBbieScId(
                    dslContext.insertInto(BBIE_SC)
                            .set(bbieScRecord)
                            .returning(BBIE_SC.BBIE_SC_ID)
                            .fetchOne().getBbieScId()
            );
        } else {
            bbieScRecord.setIsUsed((byte) (bbieSc.isUsed() ? 1 : 0));
            bbieScRecord.setDefinition(bbieSc.getDefinition());
            if (bbieSc.isEmptyCardinality()) {
                throw new IllegalArgumentException();
            }
            bbieScRecord.setCardinalityMin(bbieSc.getCardinalityMin());
            bbieScRecord.setCardinalityMax(bbieSc.getCardinalityMax());
            bbieScRecord.setExample(bbieSc.getExample());
            bbieScRecord.setRemark(bbieSc.getRemark());

            if (!StringUtils.isEmpty(bbieSc.getDefaultValue())) {
                bbieScRecord.setDefaultValue(bbieSc.getDefaultValue());
                bbieScRecord.setFixedValue(null);
            } else if (!StringUtils.isEmpty(bbieSc.getFixedValue())) {
                bbieScRecord.setDefaultValue(null);
                bbieScRecord.setFixedValue(bbieSc.getFixedValue());
            }

            if (bbieSc.isEmptyPrimitive()) {
                throw new IllegalArgumentException();
            }
            if (bbieSc.getBdtScPriRestriId() != null) {
                bbieScRecord.setDtScPriRestriId(ULong.valueOf(bbieSc.getBdtScPriRestriId()));
                bbieScRecord.setCodeListId(null);
                bbieScRecord.setAgencyIdListId(null);
            } else if (bbieSc.getCodeListId() != null) {
                bbieScRecord.setDtScPriRestriId(null);
                bbieScRecord.setCodeListId(ULong.valueOf(bbieSc.getCodeListId()));
                bbieScRecord.setAgencyIdListId(null);
            } else if (bbieSc.getAgencyIdListId() != null) {
                bbieScRecord.setDtScPriRestriId(null);
                bbieScRecord.setCodeListId(null);
                bbieScRecord.setAgencyIdListId(ULong.valueOf(bbieSc.getAgencyIdListId()));
            }

            bbieScRecord.setLastUpdatedBy(requesterId);
            bbieScRecord.setLastUpdateTimestamp(request.getLocalDateTime());

            bbieScRecord.update(
                    BBIE_SC.IS_USED,
                    BBIE_SC.DEFINITION,
                    BBIE_SC.CARDINALITY_MIN,
                    BBIE_SC.CARDINALITY_MAX,
                    BBIE_SC.EXAMPLE,
                    BBIE_SC.REMARK,
                    BBIE_SC.DEFAULT_VALUE,
                    BBIE_SC.FIXED_VALUE,
                    BBIE_SC.DT_SC_PRI_RESTRI_ID,
                    BBIE_SC.CODE_LIST_ID,
                    BBIE_SC.AGENCY_ID_LIST_ID,
                    BBIE_SC.LAST_UPDATED_BY,
                    BBIE_SC.LAST_UPDATE_TIMESTAMP
            );
        }

        return bbieScReadRepository.getBbieSc(request.getTopLevelAbieId(), hashPath);
    }
}