package org.oagi.score.repo.component.bbie_sc;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.service.common.data.AppUser;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.gateway.http.helper.ScoreGuid;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.BbieScRecord;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.DtScRecord;
import org.oagi.score.repo.component.bdt_sc_pri_restri.AvailableBdtScPriRestri;
import org.oagi.score.repo.component.bdt_sc_pri_restri.BdtScPriRestriReadRepository;
import org.oagi.score.repo.component.dt_sc.DtScReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.oagi.score.gateway.http.helper.Utility.emptyToNull;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.BBIE;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.BBIE_SC;

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
        String topLevelAsbiepId = request.getTopLevelAsbiepId();
        String hashPath = bbieSc.getHashPath();
        BbieScRecord bbieScRecord = dslContext.selectFrom(BBIE_SC)
                .where(and(
                        BBIE_SC.OWNER_TOP_LEVEL_ASBIEP_ID.eq(topLevelAsbiepId),
                        BBIE_SC.HASH_PATH.eq(hashPath)
                ))
                .fetchOptional().orElse(null);

        AppUser user = sessionService.getAppUserByUsername(request.getUser());
        String requesterId = user.getAppUserId();

        if (bbieScRecord == null) {
            bbieScRecord = new BbieScRecord();
            bbieScRecord.setBbieScId(UUID.randomUUID().toString());
            bbieScRecord.setGuid(ScoreGuid.randomGuid());
            bbieScRecord.setBasedDtScManifestId(bbieSc.getBasedDtScManifestId());
            bbieScRecord.setPath(bbieSc.getPath());
            bbieScRecord.setHashPath(hashPath);
            bbieScRecord.setBbieId(dslContext.select(BBIE.BBIE_ID)
                    .from(BBIE)
                    .where(and(
                            BBIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(topLevelAsbiepId),
                            BBIE.HASH_PATH.eq(bbieSc.getBbieHashPath())
                    ))
                    .fetchOneInto(String.class));

            if (bbieSc.getUsed() != null){
                bbieScRecord.setIsUsed((byte) (bbieSc.getUsed() ? 1 : 0));
            }
            
            bbieScRecord.setDefinition(bbieSc.getDefinition());
            DtScRecord dtScRecord = dtScReadRepository.getDtScByManifestId(bbieSc.getBasedDtScManifestId());
            if (dtScRecord == null) {
                throw new IllegalArgumentException();
            }
            if (bbieSc.getCardinalityMin() == null) {
                bbieScRecord.setCardinalityMin(dtScRecord.getCardinalityMin());
            } else {
                bbieScRecord.setCardinalityMin(bbieSc.getCardinalityMin());
            }

            if (bbieSc.getCardinalityMax() == null) {
                bbieScRecord.setCardinalityMax(dtScRecord.getCardinalityMax());
            } else {
                bbieScRecord.setCardinalityMax(bbieSc.getCardinalityMax());
            }
            if (bbieScRecord.getCardinalityMax() > 0 && bbieScRecord.getCardinalityMin() > bbieScRecord.getCardinalityMax()) {
                throw new IllegalArgumentException("Cardinality is not valid.");
            }

            if (bbieSc.getMinLength() != null) {
                bbieScRecord.setFacetMinLength(ULong.valueOf(bbieSc.getMinLength()));
            } else {
                bbieScRecord.setFacetMinLength(null);
            }
            if (bbieSc.getMaxLength() != null) {
                bbieScRecord.setFacetMaxLength(ULong.valueOf(bbieSc.getMaxLength()));
            } else {
                bbieScRecord.setFacetMaxLength(null);
            }
            if (bbieScRecord.getFacetMinLength() != null && bbieScRecord.getFacetMaxLength() != null) {
                if (bbieScRecord.getFacetMinLength().intValue() < 0) {
                    throw new IllegalArgumentException("Minimum Length must be greater than or equals to 0.");
                }
                if (bbieScRecord.getFacetMinLength().compareTo(bbieScRecord.getFacetMaxLength()) > 0) {
                    throw new IllegalArgumentException("Minimum Length must be less than equals to Maximum Length.");
                }
            }
            if (StringUtils.hasLength(bbieSc.getPattern())) {
                bbieScRecord.setFacetPattern(bbieSc.getPattern());
            } else {
                bbieScRecord.setFacetPattern(null);
            }

            bbieScRecord.setBizTerm(bbieSc.getBizTerm());
            bbieScRecord.setExample(bbieSc.getExample());
            bbieScRecord.setRemark(bbieSc.getRemark());

            if (StringUtils.hasLength(bbieSc.getDefaultValue())) {
                bbieScRecord.setDefaultValue(bbieSc.getDefaultValue());
                bbieScRecord.setFixedValue(null);
            } else if (StringUtils.hasLength(bbieSc.getFixedValue())) {
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

                bbieScRecord.setDtScPriRestriId(bdtScPriRestriList.get(0).getBdtScPriRestriId());
            } else {
                if (bbieSc.getBdtScPriRestriId() != null) {
                    bbieScRecord.setDtScPriRestriId(bbieSc.getBdtScPriRestriId());
                    bbieScRecord.setCodeListId(null);
                    bbieScRecord.setAgencyIdListId(null);
                } else if (bbieSc.getCodeListId() != null) {
                    bbieScRecord.setDtScPriRestriId(null);
                    bbieScRecord.setCodeListId(bbieSc.getCodeListId());
                    bbieScRecord.setAgencyIdListId(null);
                } else if (bbieSc.getAgencyIdListId() != null) {
                    bbieScRecord.setDtScPriRestriId(null);
                    bbieScRecord.setCodeListId(null);
                    bbieScRecord.setAgencyIdListId(bbieSc.getAgencyIdListId());
                }
            }

            bbieScRecord.setOwnerTopLevelAsbiepId(topLevelAsbiepId);

            bbieScRecord.setCreatedBy(requesterId);
            bbieScRecord.setLastUpdatedBy(requesterId);
            bbieScRecord.setCreationTimestamp(request.getLocalDateTime());
            bbieScRecord.setLastUpdateTimestamp(request.getLocalDateTime());

            dslContext.insertInto(BBIE_SC)
                    .set(bbieScRecord)
                    .execute();
        } else {
            if (bbieSc.getUsed() != null) {
                bbieScRecord.setIsUsed((byte) (bbieSc.getUsed() ? 1 : 0));
            }

            if (bbieSc.getDefinition() != null) {
                bbieScRecord.setDefinition(emptyToNull(bbieSc.getDefinition()));
            }

            if (bbieSc.getCardinalityMin() != null) {
                bbieScRecord.setCardinalityMin(bbieSc.getCardinalityMin());
            }

            if (bbieSc.getCardinalityMax() != null) {
                bbieScRecord.setCardinalityMax(bbieSc.getCardinalityMax());
            }

            if (bbieScRecord.getCardinalityMax() > 0 && bbieScRecord.getCardinalityMin() > bbieScRecord.getCardinalityMax()) {
                throw new IllegalArgumentException("Cardinality is not valid.");
            }

            if (bbieSc.getMinLength() != null) {
                bbieScRecord.setFacetMinLength(ULong.valueOf(bbieSc.getMinLength()));
            } else {
                bbieScRecord.setFacetMinLength(null);
            }
            if (bbieSc.getMaxLength() != null) {
                bbieScRecord.setFacetMaxLength(ULong.valueOf(bbieSc.getMaxLength()));
            } else {
                bbieScRecord.setFacetMaxLength(null);
            }
            if (bbieScRecord.getFacetMinLength() != null && bbieScRecord.getFacetMaxLength() != null) {
                if (bbieScRecord.getFacetMinLength().intValue() < 0) {
                    throw new IllegalArgumentException("Minimum Length must be greater than or equals to 0.");
                }
                if (bbieScRecord.getFacetMinLength().compareTo(bbieScRecord.getFacetMaxLength()) > 0) {
                    throw new IllegalArgumentException("Minimum Length must be less than equals to Maximum Length.");
                }
            }
            if (StringUtils.hasLength(bbieSc.getPattern())) {
                bbieScRecord.setFacetPattern(bbieSc.getPattern());
            } else {
                bbieScRecord.setFacetPattern(null);
            }

            if (bbieSc.getBizTerm() != null) {
                bbieScRecord.setBizTerm(emptyToNull(bbieSc.getBizTerm()));
            }

            if (bbieSc.getExample() != null) {
                bbieScRecord.setExample(emptyToNull(bbieSc.getExample()));
            }

            if (bbieSc.getRemark() != null) {
                bbieScRecord.setRemark(emptyToNull(bbieSc.getRemark()));
            }

            if (StringUtils.hasLength(bbieSc.getDefaultValue())) {
                bbieScRecord.setDefaultValue(bbieSc.getDefaultValue());
                bbieScRecord.setFixedValue(null);
            } else if (StringUtils.hasLength(bbieSc.getFixedValue())) {
                bbieScRecord.setDefaultValue(null);
                bbieScRecord.setFixedValue(bbieSc.getFixedValue());
            }

            if (!bbieSc.isEmptyPrimitive()) {
                if (bbieSc.getBdtScPriRestriId() != null) {
                    bbieScRecord.setDtScPriRestriId(bbieSc.getBdtScPriRestriId());
                    bbieScRecord.setCodeListId(null);
                    bbieScRecord.setAgencyIdListId(null);
                } else if (bbieSc.getCodeListId() != null) {
                    bbieScRecord.setDtScPriRestriId(null);
                    bbieScRecord.setCodeListId(bbieSc.getCodeListId());
                    bbieScRecord.setAgencyIdListId(null);
                } else if (bbieSc.getAgencyIdListId() != null) {
                    bbieScRecord.setDtScPriRestriId(null);
                    bbieScRecord.setCodeListId(null);
                    bbieScRecord.setAgencyIdListId(bbieSc.getAgencyIdListId());
                }
            }

            if (bbieScRecord.changed()) {
                bbieScRecord.setLastUpdatedBy(requesterId);
                bbieScRecord.setLastUpdateTimestamp(request.getLocalDateTime());

                bbieScRecord.update(
                        BBIE_SC.IS_USED,
                        BBIE_SC.DEFINITION,
                        BBIE_SC.CARDINALITY_MIN,
                        BBIE_SC.CARDINALITY_MAX,
                        BBIE_SC.FACET_MIN_LENGTH,
                        BBIE_SC.FACET_MAX_LENGTH,
                        BBIE_SC.FACET_PATTERN,
                        BBIE_SC.BIZ_TERM,
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
        }

        return bbieScReadRepository.getBbieSc(request.getTopLevelAsbiepId(), hashPath);
    }
}
