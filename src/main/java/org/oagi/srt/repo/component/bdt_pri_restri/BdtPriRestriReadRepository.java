package org.oagi.srt.repo.component.bdt_pri_restri;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

import static org.oagi.srt.entity.jooq.Tables.*;

@Repository
public class BdtPriRestriReadRepository {

    @Autowired
    private DSLContext dslContext;

    public List<AvailableBdtPriRestri> availableBdtPriRestriListByBccpManifestId(BigInteger bccpManifestId) {
        return dslContext.select(
                BDT_PRI_RESTRI.BDT_PRI_RESTRI_ID,
                BDT_PRI_RESTRI.IS_DEFAULT, XBT.XBT_ID, XBT.NAME.as("xbt_name"))
                .from(BDT_PRI_RESTRI)
                .join(CDT_AWD_PRI_XPS_TYPE_MAP)
                .on(BDT_PRI_RESTRI.CDT_AWD_PRI_XPS_TYPE_MAP_ID.eq(CDT_AWD_PRI_XPS_TYPE_MAP.CDT_AWD_PRI_XPS_TYPE_MAP_ID))
                .join(XBT).on(CDT_AWD_PRI_XPS_TYPE_MAP.XBT_ID.eq(XBT.XBT_ID))
                .join(BCCP)
                .on(BDT_PRI_RESTRI.BDT_ID.eq(BCCP.BDT_ID))
                .join(BCCP_MANIFEST)
                .on(BCCP.BCCP_ID.eq(BCCP_MANIFEST.BCCP_ID))
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(ULong.valueOf(bccpManifestId)))
                .fetchInto(AvailableBdtPriRestri.class);
    }

}
