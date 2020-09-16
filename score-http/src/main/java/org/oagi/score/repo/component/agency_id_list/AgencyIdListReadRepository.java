package org.oagi.score.repo.component.agency_id_list;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SelectOnConditionStep;
import org.jooq.types.ULong;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;

@Repository
public class AgencyIdListReadRepository {

    @Autowired
    private DSLContext dslContext;

    public List<AvailableAgencyIdList> availableAgencyIdListByBccpManifestId(BigInteger bccpManifestId) {
        Result<Record2<ULong, ULong>> result = dslContext.selectDistinct(
                AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID,
                BCCP_MANIFEST.RELEASE_ID)
                .from(BCCP_MANIFEST)
                .join(DT_MANIFEST).on(BCCP_MANIFEST.BDT_MANIFEST_ID.eq(DT_MANIFEST.DT_MANIFEST_ID))
                .join(BDT_PRI_RESTRI).on(DT_MANIFEST.DT_ID.eq(BDT_PRI_RESTRI.BDT_ID))
                .leftJoin(AGENCY_ID_LIST_MANIFEST).on(and(
                        BDT_PRI_RESTRI.AGENCY_ID_LIST_ID.eq(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_ID),
                        BCCP_MANIFEST.RELEASE_ID.eq(AGENCY_ID_LIST_MANIFEST.RELEASE_ID)
                ))
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(ULong.valueOf(bccpManifestId)))
                .fetch();

        SelectOnConditionStep step = dslContext.select(
                AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID,
                AGENCY_ID_LIST_MANIFEST.BASED_AGENCY_ID_LIST_MANIFEST_ID,
                BDT_PRI_RESTRI.IS_DEFAULT,
                AGENCY_ID_LIST.AGENCY_ID_LIST_ID,
                AGENCY_ID_LIST.NAME.as("agency_id_list_name"))
                .from(BDT_PRI_RESTRI)
                .join(AGENCY_ID_LIST).on(BDT_PRI_RESTRI.AGENCY_ID_LIST_ID.eq(AGENCY_ID_LIST.AGENCY_ID_LIST_ID))
                .join(AGENCY_ID_LIST_MANIFEST).on(AGENCY_ID_LIST.AGENCY_ID_LIST_ID.eq(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_ID));

        return result.stream().map(e ->
                availableAgencyIdListByAgencyIdListManifestIdOrReleaseId(
                        (e.get(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID) != null) ?
                                e.get(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID).toBigInteger() :
                                null,
                        e.get(AGENCY_ID_LIST_MANIFEST.RELEASE_ID).toBigInteger(), step))
                .flatMap(e -> e.stream())
                .distinct()
                .sorted(Comparator.comparing(AvailableAgencyIdList::getAgencyIdListName))
                .collect(Collectors.toList());
    }

    private List<AvailableAgencyIdList> availableAgencyIdListByAgencyIdListManifestIdOrReleaseId(
            BigInteger agencyIdListManifestId, BigInteger releaseId, SelectOnConditionStep step) {
        if (agencyIdListManifestId == null) {
            return step.where(AGENCY_ID_LIST_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)))
                    .fetchInto(AvailableAgencyIdList.class);
        }

        List<AvailableAgencyIdList> availableAgencyIdLists = step
                .where(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID.eq(ULong.valueOf(agencyIdListManifestId)))
                .fetchInto(AvailableAgencyIdList.class);

        List<AvailableAgencyIdList> mergedAgencyIdLists = new ArrayList();
        mergedAgencyIdLists.addAll(availableAgencyIdLists.stream()
                .filter(e -> e.getBasedAgencyIdListManifestId() != null)
                .distinct()
                .map(e -> availableAgencyIdListByAgencyIdListManifestIdOrReleaseId(
                        e.getBasedAgencyIdListManifestId(), releaseId, step))
                .flatMap(e -> e.stream())
                .collect(Collectors.toList())
        );
        mergedAgencyIdLists.addAll(availableAgencyIdLists.stream()
                .filter(e -> e.getBasedAgencyIdListManifestId() == null)
                .collect(Collectors.toList())
        );
        return mergedAgencyIdLists.stream().distinct().collect(Collectors.toList());
    }

    public List<AvailableAgencyIdList> availableAgencyIdListByBdtScManifestId(BigInteger bdtScManifestId) {
        Result<Record2<ULong, ULong>> result = dslContext.selectDistinct(
                AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID,
                DT_SC_MANIFEST.RELEASE_ID)
                .from(DT_SC_MANIFEST)
                .join(BDT_SC_PRI_RESTRI).on(DT_SC_MANIFEST.DT_SC_ID.eq(BDT_SC_PRI_RESTRI.BDT_SC_ID))
                .leftJoin(AGENCY_ID_LIST_MANIFEST).on(and(
                        BDT_SC_PRI_RESTRI.AGENCY_ID_LIST_ID.eq(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_ID),
                        DT_SC_MANIFEST.RELEASE_ID.eq(AGENCY_ID_LIST_MANIFEST.RELEASE_ID)
                ))
                .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(ULong.valueOf(bdtScManifestId)))
                .fetch();

        SelectOnConditionStep step = dslContext.select(
                AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID,
                AGENCY_ID_LIST_MANIFEST.BASED_AGENCY_ID_LIST_MANIFEST_ID,
                BDT_SC_PRI_RESTRI.IS_DEFAULT,
                AGENCY_ID_LIST.AGENCY_ID_LIST_ID,
                AGENCY_ID_LIST.NAME.as("code_list_name"))
                .from(BDT_SC_PRI_RESTRI)
                .join(AGENCY_ID_LIST).on(BDT_SC_PRI_RESTRI.AGENCY_ID_LIST_ID.eq(AGENCY_ID_LIST.AGENCY_ID_LIST_ID))
                .join(AGENCY_ID_LIST_MANIFEST).on(AGENCY_ID_LIST.AGENCY_ID_LIST_ID.eq(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_ID));

        return result.stream().map(e ->
                availableAgencyIdListByAgencyIdListManifestIdOrReleaseId(
                        (e.get(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID) != null) ?
                                e.get(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID).toBigInteger() :
                                null,
                        e.get(AGENCY_ID_LIST_MANIFEST.RELEASE_ID).toBigInteger(), step))
                .flatMap(e -> e.stream())
                .distinct()
                .sorted(Comparator.comparing(AvailableAgencyIdList::getAgencyIdListName))
                .collect(Collectors.toList());
    }
}
