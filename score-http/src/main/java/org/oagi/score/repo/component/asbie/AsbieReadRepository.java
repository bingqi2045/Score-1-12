package org.oagi.score.repo.component.asbie;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AsbieRecord;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AsccRecord;
import org.oagi.score.gateway.http.api.bie_management.data.bie_edit.BieEditUsed;
import org.oagi.score.gateway.http.api.bie_management.data.bie_edit.tree.BieEditRef;
import org.oagi.score.gateway.http.api.cc_management.data.CcState;
import org.oagi.score.repo.component.ascc.AsccReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;

@Repository
public class AsbieReadRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private AsccReadRepository asccReadRepository;

    private AsbieRecord getAsbieByTopLevelAbieIdAndHashPath(BigInteger topLevelAbieId, String hashPath) {
        return dslContext.selectFrom(ASBIE)
                .where(and(
                        ASBIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(ULong.valueOf(topLevelAbieId)),
                        ASBIE.HASH_PATH.eq(hashPath)
                ))
                .fetchOptional().orElse(null);
    }

    public AsbieNode getAsbieNode(BigInteger topLevelAbieId, BigInteger asccManifestId, String hashPath) {
        AsccRecord asccRecord = asccReadRepository.getAsccByManifestId(asccManifestId);
        if (asccRecord == null) {
            return null;
        }

        AsbieNode asbieNode = new AsbieNode();

        AsbieNode.Ascc ascc = asbieNode.getAscc();
        ascc.setAsccManifestId(asccManifestId);
        ascc.setGuid(asccRecord.getGuid());
        ascc.setCardinalityMin(asccRecord.getCardinalityMin());
        ascc.setCardinalityMax(asccRecord.getCardinalityMax());
        ascc.setDen(asccRecord.getDen());
        ascc.setDefinition(asccRecord.getDefinition());
        ascc.setState(CcState.valueOf(asccRecord.getState()));

        AsbieNode.Asbie asbie = getAsbie(topLevelAbieId, hashPath);
        asbieNode.setAsbie(asbie);

        if (asbie.getAsbieId() == null) {
            asbie.setBasedAsccManifestId(ascc.getAsccManifestId());
            asbie.setCardinalityMin(ascc.getCardinalityMin());
            asbie.setCardinalityMax(ascc.getCardinalityMax());
        }

        return asbieNode;
    }

    public AsbieNode.Asbie getAsbie(BigInteger topLevelAbieId, String hashPath) {
        AsbieNode.Asbie asbie = new AsbieNode.Asbie();
        asbie.setHashPath(hashPath);

        AsbieRecord asbieRecord = getAsbieByTopLevelAbieIdAndHashPath(topLevelAbieId, hashPath);
        if (asbieRecord != null) {
            asbie.setAsbieId(asbieRecord.getAsbieId().toBigInteger());
            asbie.setGuid(asbieRecord.getGuid());
            asbie.setFromAbieHashPath(dslContext.select(ABIE.HASH_PATH)
                    .from(ABIE)
                    .where(and(
                            ABIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(ULong.valueOf(topLevelAbieId)),
                            ABIE.ABIE_ID.eq(asbieRecord.getFromAbieId())
                    ))
                    .fetchOneInto(String.class));
            asbie.setToAsbiepHashPath(dslContext.select(ASBIEP.HASH_PATH)
                    .from(ASBIEP)
                    .where(and(
                            ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID.eq(ULong.valueOf(topLevelAbieId)),
                            ASBIEP.ASBIEP_ID.eq(asbieRecord.getToAsbiepId())
                    ))
                    .fetchOneInto(String.class));
            asbie.setBasedAsccManifestId(asbieRecord.getBasedAsccManifestId().toBigInteger());
            asbie.setUsed(asbieRecord.getIsUsed() == 1 ? true : false);
            asbie.setCardinalityMin(asbieRecord.getCardinalityMin());
            asbie.setCardinalityMax(asbieRecord.getCardinalityMax());
            asbie.setNillable(asbieRecord.getIsNillable() == 1 ? true : false);
            asbie.setRemark(asbieRecord.getRemark());
            asbie.setDefinition(asbieRecord.getDefinition());
        }

        return asbie;
    }

    public List<BieEditUsed> getUsedAsbieList(BigInteger topLevelAbieId) {
        return dslContext.select(ASBIE.HASH_PATH)
                .from(ASBIE)
                .join(ASBIEP).on(ASBIE.TO_ASBIEP_ID.eq(ASBIEP.ASBIEP_ID))
                .where(and(
                        ASBIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(ULong.valueOf(topLevelAbieId)),
                        ASBIE.IS_USED.eq((byte) 1)
                ))
                .fetchStream().map(record -> {
                    BieEditUsed bieEditUsed = new BieEditUsed();
                    bieEditUsed.setType("ASBIE");
                    bieEditUsed.setHashPath(record.get(ASBIE.HASH_PATH));
                    bieEditUsed.setTopLevelAsbiepId(topLevelAbieId);
                    bieEditUsed.setUsed(true);
                    return bieEditUsed;
                }).collect(Collectors.toList());
    }

    public List<BieEditRef> getBieRefList(BigInteger topLevelAsbiepId) {
        if (topLevelAsbiepId == null || topLevelAsbiepId.longValue() <= 0L) {
            return Collections.emptyList();
        }

        List<BieEditRef> bieEditRefList = new ArrayList();
        List<BieEditRef> refTopLevelAbieIdList = getRefTopLevelAsbiepIdList(topLevelAsbiepId);
        bieEditRefList.addAll(refTopLevelAbieIdList);

        if (!bieEditRefList.isEmpty()) {
            refTopLevelAbieIdList.stream().map(e -> e.getRefTopLevelAsbiepId()).distinct().forEach(refTopLevelAbieId -> {
                bieEditRefList.addAll(getBieRefList(refTopLevelAbieId));
            });
        }
        return bieEditRefList;
    }

    private List<BieEditRef> getRefTopLevelAsbiepIdList(BigInteger topLevelAsbiepId) {
        return dslContext.select(
                ASBIE.HASH_PATH,
                ASBIE.OWNER_TOP_LEVEL_ASBIEP_ID.as("top_level_asbiep_id"),
                ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID.as("ref_top_level_asbiep_id"))
                .from(ASBIE)
                .join(ASBIEP).on(
                        and(ASBIE.TO_ASBIEP_ID.eq(ASBIEP.ASBIEP_ID),
                            ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID.notEqual(ASBIE.OWNER_TOP_LEVEL_ASBIEP_ID)))
                .where(ASBIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(ULong.valueOf(topLevelAsbiepId)))
                .fetchInto(BieEditRef.class);
    }
}
