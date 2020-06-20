package org.oagi.srt.repo.component.asbiep;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.AsbiepRecord;
import org.oagi.srt.entity.jooq.tables.records.AsccpRecord;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree.BieEditRef;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.repo.component.asccp.AsccpReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;

@Repository
public class AsbiepReadRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private AsccpReadRepository asccpReadRepository;

    private AsbiepRecord getAsbiepByTopLevelAbieIdAndHashPath(BigInteger topLevelAbieId, String hashPath) {
        return dslContext.selectFrom(ASBIEP)
                .where(and(
                        ASBIEP.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)),
                        ASBIEP.HASH_PATH.eq(hashPath)
                ))
                .fetchOptional().orElse(null);
    }

    private AsbiepRecord getAsbiepByRefTopLevelAbieId(BigInteger topLevelAbieId) {
        return dslContext.select(ASBIEP.fields())
                .from(ASBIEP)
                .join(ABIE).on(ASBIEP.ROLE_OF_ABIE_ID.eq(ABIE.ABIE_ID))
                .join(TOP_LEVEL_ABIE).on(and(
                        ABIE.OWNER_TOP_LEVEL_ABIE_ID.eq(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID),
                        ASBIEP.OWNER_TOP_LEVEL_ABIE_ID.eq(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID),
                        ABIE.ABIE_ID.eq(TOP_LEVEL_ABIE.ABIE_ID)
                ))
                .where(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)))
                .fetchOptionalInto(AsbiepRecord.class).orElse(null);
    }

    public AsbiepNode getAsbiepNode(BigInteger topLevelAbieId, BigInteger asccpManifestId, String hashPath) {
        AsccpRecord asccpRecord = asccpReadRepository.getAsccpByManifestId(asccpManifestId);
        if (asccpRecord == null) {
            return null;
        }

        AsbiepNode asbiepNode = new AsbiepNode();

        AsbiepNode.Asccp asccp = asbiepNode.getAsccp();
        asccp.setAsccpManifestId(asccpManifestId);
        asccp.setGuid(asccpRecord.getGuid());
        asccp.setPropertyTerm(asccpRecord.getPropertyTerm());
        asccp.setDen(asccpRecord.getDen());
        asccp.setDefinition(asccpRecord.getDefinition());
        asccp.setState(CcState.valueOf(asccpRecord.getState()));
        asccp.setNillable(asccpRecord.getIsNillable() == 1 ? true : false);

        AsbiepNode.Asbiep asbiep = getAsbiep(topLevelAbieId, hashPath);
        asbiepNode.setAsbiep(asbiep);

        if (asbiep.getAsbiepId() == null) {
            asbiep.setBasedAsccpManifestId(asccp.getAsccpManifestId());
        }

        return asbiepNode;
    }

    public AsbiepNode.Asbiep getAsbiep(BigInteger topLevelAbieId, String hashPath) {
        AsbiepNode.Asbiep asbiep = new AsbiepNode.Asbiep();
        asbiep.setUsed(true);
        asbiep.setHashPath(hashPath);

        AsbiepRecord asbiepRecord = getAsbiepByTopLevelAbieIdAndHashPath(topLevelAbieId, hashPath);
        if (asbiepRecord != null) {
            BigInteger refTopLevelAbieId = (asbiepRecord.getRefTopLevelAbieId() != null) ?
                    asbiepRecord.getRefTopLevelAbieId().toBigInteger() : null;
            if (refTopLevelAbieId != null) {
                asbiep.setDerived(true);
            }

            asbiep.setAsbiepId(asbiepRecord.getAsbiepId().toBigInteger());
            asbiep.setRoleOfAbieHashPath(dslContext.select(ABIE.HASH_PATH)
                    .from(ABIE)
                    .where(and(
                            ABIE.OWNER_TOP_LEVEL_ABIE_ID.eq(asbiepRecord.getOwnerTopLevelAbieId()),
                            ABIE.ABIE_ID.eq(asbiepRecord.getRoleOfAbieId())
                    ))
                    .fetchOneInto(String.class));
            asbiep.setBasedAsccpManifestId(asbiepRecord.getBasedAsccpManifestId().toBigInteger());
            if (refTopLevelAbieId != null) {
                asbiep.setRefTopLevelAbieId(refTopLevelAbieId);
            }
            asbiep.setGuid(asbiepRecord.getGuid());
            asbiep.setRemark(asbiepRecord.getRemark());
            asbiep.setBizTerm(asbiepRecord.getBizTerm());
            asbiep.setDefinition(asbiepRecord.getDefinition());
        }

        return asbiep;
    }

    public AsbiepNode.Asbiep getAsbiepByTopLevelAbieId(BigInteger topLevelAbieId) {
        String asbiepHashPath = dslContext.select(ASBIEP.HASH_PATH)
                .from(ASBIEP)
                .join(ABIE).on(and(
                        ASBIEP.ROLE_OF_ABIE_ID.eq(ABIE.ABIE_ID),
                        ASBIEP.OWNER_TOP_LEVEL_ABIE_ID.eq(ABIE.OWNER_TOP_LEVEL_ABIE_ID)
                ))
                .join(TOP_LEVEL_ABIE).on(and(
                        ABIE.OWNER_TOP_LEVEL_ABIE_ID.eq(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID),
                        ABIE.ABIE_ID.eq(TOP_LEVEL_ABIE.ABIE_ID)
                ))
                .where(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)))
                .fetchOneInto(String.class);
        return getAsbiep(topLevelAbieId, asbiepHashPath);
    }

    public List<BieEditRef> getBieRefList(BigInteger topLevelAbieId) {
        if (topLevelAbieId == null || topLevelAbieId.longValue() <= 0L) {
            return Collections.emptyList();
        }

        List<BieEditRef> bieEditRefList = new ArrayList();
        List<BieEditRef> refTopLevelAbieIdList = getRefTopLevelAbieIdList(topLevelAbieId);
        bieEditRefList.addAll(refTopLevelAbieIdList);

        if (!bieEditRefList.isEmpty()) {
            refTopLevelAbieIdList.stream().map(e -> e.getRefTopLevelAbieId()).distinct().forEach(refTopLevelAbieId -> {
                bieEditRefList.addAll(getBieRefList(refTopLevelAbieId));
            });
        }
        return bieEditRefList;
    }

    private List<BieEditRef> getRefTopLevelAbieIdList(BigInteger topLevelAbieId) {
        return dslContext.select(
                ASBIEP.HASH_PATH,
                ASBIEP.OWNER_TOP_LEVEL_ABIE_ID.as("top_level_abie_id"),
                ASBIEP.REF_TOP_LEVEL_ABIE_ID)
                .from(ASBIEP)
                .where(and(
                        ASBIEP.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)),
                        ASBIEP.REF_TOP_LEVEL_ABIE_ID.isNotNull()
                ))
                .fetchInto(BieEditRef.class);
    }

}
