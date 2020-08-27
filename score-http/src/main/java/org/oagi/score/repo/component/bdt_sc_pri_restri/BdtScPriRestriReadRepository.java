package org.oagi.score.repo.component.bdt_sc_pri_restri;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

import static org.oagi.score.entity.jooq.Tables.*;

@Repository
public class BdtScPriRestriReadRepository {

    @Autowired
    private DSLContext dslContext;

    public List<AvailableBdtScPriRestri> availableBdtScPriRestriListByBdtScManifestId(BigInteger bdtScManifestId) {
        return dslContext.select(
                BDT_SC_PRI_RESTRI.BDT_SC_PRI_RESTRI_ID,
                BDT_SC_PRI_RESTRI.IS_DEFAULT,
                XBT.XBT_ID,
                XBT.NAME.as("xbt_name"))
                .from(BDT_SC_PRI_RESTRI)
                .join(CDT_SC_AWD_PRI_XPS_TYPE_MAP)
                .on(BDT_SC_PRI_RESTRI.CDT_SC_AWD_PRI_XPS_TYPE_MAP_ID
                        .eq(CDT_SC_AWD_PRI_XPS_TYPE_MAP.CDT_SC_AWD_PRI_XPS_TYPE_MAP_ID))
                .join(XBT).on(CDT_SC_AWD_PRI_XPS_TYPE_MAP.XBT_ID.eq(XBT.XBT_ID))
                .join(DT_SC)
                .on(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(DT_SC.DT_SC_ID))
                .join(DT_SC_MANIFEST)
                .on(DT_SC.DT_SC_ID.eq(DT_SC_MANIFEST.DT_SC_ID))
                .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(ULong.valueOf(bdtScManifestId)))
                .fetchInto(AvailableBdtScPriRestri.class);
    }

}
