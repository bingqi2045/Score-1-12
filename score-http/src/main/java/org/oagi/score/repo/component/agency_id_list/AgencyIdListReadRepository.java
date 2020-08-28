package org.oagi.score.repo.component.agency_id_list;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;

@Repository
public class AgencyIdListReadRepository {

    @Autowired
    private DSLContext dslContext;

    public List<AvailableAgencyIdList> availableAgencyIdListByBccpManifestId(BigInteger bccpManifestId) {
        return dslContext.select(
                AGENCY_ID_LIST.AGENCY_ID_LIST_ID, BDT_PRI_RESTRI.IS_DEFAULT, AGENCY_ID_LIST.NAME.as("agency_id_list_name"))
                .from(BDT_PRI_RESTRI)
                .join(AGENCY_ID_LIST).on(BDT_PRI_RESTRI.AGENCY_ID_LIST_ID.eq(AGENCY_ID_LIST.AGENCY_ID_LIST_ID))
                .join(BCCP).on(BDT_PRI_RESTRI.BDT_ID.eq(BCCP.BDT_ID))
                .join(BCCP_MANIFEST).on(BCCP.BCCP_ID.eq(BCCP_MANIFEST.BCCP_ID))
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(ULong.valueOf(bccpManifestId)))
                .fetchInto(AvailableAgencyIdList.class);
    }

    public List<AvailableAgencyIdList> availableAgencyIdListByBdtScManifestId(BigInteger bdtScManifestId) {
        return dslContext.select(
                AGENCY_ID_LIST.AGENCY_ID_LIST_ID,
                BDT_SC_PRI_RESTRI.IS_DEFAULT,
                AGENCY_ID_LIST.NAME.as("agency_id_list_name"))
                .from(BDT_SC_PRI_RESTRI)
                .join(AGENCY_ID_LIST).on(BDT_SC_PRI_RESTRI.AGENCY_ID_LIST_ID.eq(AGENCY_ID_LIST.AGENCY_ID_LIST_ID))
                .join(DT_SC).on(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(DT_SC.DT_SC_ID))
                .join(DT_SC_MANIFEST).on(DT_SC.DT_SC_ID.eq(DT_SC_MANIFEST.DT_SC_ID))
                .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(ULong.valueOf(bdtScManifestId)))
                .fetchInto(AvailableAgencyIdList.class);
    }
}
