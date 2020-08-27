package org.oagi.score.repo.component.code_list;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.entity.jooq.tables.records.CodeListManifestRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

import static org.jooq.impl.DSL.and;
import static org.oagi.score.entity.jooq.Tables.*;

@Repository
public class CodeListReadRepository {

    @Autowired
    private DSLContext dslContext;

    public CodeListManifestRecord getCodeListManifestByManifestId(BigInteger codeListManifestId) {
        return dslContext.selectFrom(CODE_LIST_MANIFEST)
                .where(CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID.eq(ULong.valueOf(codeListManifestId)))
                .fetchOne();
    }

    public List<AvailableCodeList> availableCodeListByBccpManifestId(BigInteger bccpManifestId) {
        return dslContext.select(
                CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID,
                CODE_LIST_MANIFEST.BASED_CODE_LIST_MANIFEST_ID,
                BDT_PRI_RESTRI.IS_DEFAULT,
                CODE_LIST.CODE_LIST_ID,
                CODE_LIST.NAME.as("code_list_name"))
                .from(BDT_PRI_RESTRI)
                .join(CODE_LIST).on(BDT_PRI_RESTRI.CODE_LIST_ID.eq(CODE_LIST.CODE_LIST_ID))
                .join(BCCP).on(BDT_PRI_RESTRI.BDT_ID.eq(BCCP.BDT_ID))
                .join(BCCP_MANIFEST).on(BCCP.BCCP_ID.eq(BCCP_MANIFEST.BCCP_ID))
                .join(CODE_LIST_MANIFEST).on(and(CODE_LIST.CODE_LIST_ID.eq(CODE_LIST_MANIFEST.CODE_LIST_ID),
                        BCCP_MANIFEST.RELEASE_ID.eq(CODE_LIST_MANIFEST.RELEASE_ID)))
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(ULong.valueOf(bccpManifestId)))
                .fetchInto(AvailableCodeList.class);
    }

    public List<AvailableCodeList> availableCodeListByBdtScManifestId(BigInteger bdtScManifestId) {
        return dslContext.select(
                CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID,
                CODE_LIST_MANIFEST.BASED_CODE_LIST_MANIFEST_ID,
                BDT_SC_PRI_RESTRI.IS_DEFAULT,
                CODE_LIST.NAME.as("code_list_name"))
                .from(BDT_SC_PRI_RESTRI)
                .join(CODE_LIST).on(BDT_SC_PRI_RESTRI.CODE_LIST_ID.eq(CODE_LIST.CODE_LIST_ID))
                .join(DT_SC).on(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(DT_SC.DT_SC_ID))
                .join(DT_SC_MANIFEST).on(DT_SC.DT_SC_ID.eq(DT_SC_MANIFEST.DT_SC_ID))
                .join(CODE_LIST_MANIFEST).on(and(CODE_LIST.CODE_LIST_ID.eq(CODE_LIST_MANIFEST.CODE_LIST_ID),
                        DT_SC_MANIFEST.RELEASE_ID.eq(CODE_LIST_MANIFEST.RELEASE_ID)))
                .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(ULong.valueOf(bdtScManifestId)))
                .fetchInto(AvailableCodeList.class);
    }

}