package org.oagi.srt.gateway.http.api.cc_management.repository;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.types.ULong;
import org.oagi.srt.data.*;
import org.oagi.srt.entity.jooq.Tables;
import org.oagi.srt.entity.jooq.tables.AppUser;
import org.oagi.srt.entity.jooq.tables.records.AccRecord;
import org.oagi.srt.gateway.http.api.cc_management.data.CcList;
import org.oagi.srt.gateway.http.api.cc_management.data.CcListRequest;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.repository.ReleaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.oagi.srt.gateway.http.api.cc_management.helper.CcUtility.getLatestEntity;
import static org.oagi.srt.gateway.http.api.cc_management.helper.CcUtility.getRevision;

@Repository
public class CcListRepository {

    @Autowired
    private CoreComponentRepository coreComponentRepository;

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private ReleaseRepository releaseRepository;


    public List<CcList> getAccList(CcListRequest request) {
        if (!request.getTypes().isAcc()) {
            return Collections.emptyList();
        }
        long releaseId = request.getReleaseId() + 1;
        Release release = releaseRepository.findById(request.getReleaseId());

        Condition whereCondition = DSL.trueCondition();

        if (release.getReleaseNum().equals("Working")){
            whereCondition = whereCondition.and(Tables.ACC.OAGIS_COMPONENT_TYPE.notEqual(OagisComponentType.UserExtensionGroup.getValue()));
        }

        if (request.getDeprecated() != null) {
            whereCondition = whereCondition.and(Tables.ACC.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }

        if (!request.getStates().isEmpty()) {
            whereCondition = whereCondition.and(Tables.ACC.STATE.in(request.getStates()));
        }

        if (!request.getOwnerLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(Tables.ACC.OWNER_USER_ID.in(request.getOwnerLoginIds()));
        }

        if (!request.getOwnerLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(Tables.ACC.OWNER_USER_ID.in(request.getOwnerLoginIds()));
        }

        if (!request.getUpdaterLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(Tables.ACC.OWNER_USER_ID.in(request.getUpdaterLoginIds()));
        }

        if (request.getDen() != null && !request.getDen().isEmpty()){
            whereCondition = whereCondition.and(getDenFilter(Tables.ACC.DEN, request.getDen()));
        }

        if (request.getDefinition() != null && !request.getDefinition().isEmpty()){
            whereCondition = whereCondition.and(DSL.lower(Tables.ACC.DEFINITION).contains(request.getDefinition().trim().toLowerCase()));
        }

        if (request.getModule() != null && !request.getModule().isEmpty()){
            whereCondition = whereCondition.and(DSL.lower(Tables.MODULE.MODULE_).contains(request.getModule().trim().toLowerCase()));
        }

        if (request.getUpdateStartDate() != null){
            whereCondition = whereCondition.and(Tables.ACC.LAST_UPDATE_TIMESTAMP.greaterThan((Timestamp) request.getUpdateStartDate()));
        }

        if (request.getUpdateEndDate() != null){
            whereCondition = whereCondition.and(Tables.ACC.LAST_UPDATE_TIMESTAMP.lessThan((Timestamp) request.getUpdateEndDate()));
        }

        AppUser appUserOwner = Tables.APP_USER.as("owner");
        AppUser appUserUpdater = Tables.APP_USER.as("updater");

        return dslContext.select(Tables.ACC.ACC_ID,
                    Tables.ACC.GUID,
                    Tables.ACC.DEN,
                    Tables.ACC.DEFINITION,
                    Tables.ACC.DEFINITION_SOURCE,
                    Tables.MODULE.MODULE_,
                    Tables.ACC.OAGIS_COMPONENT_TYPE,
                    Tables.ACC.STATE,
                    Tables.ACC.IS_DEPRECATED,
                    Tables.ACC.CURRENT_ACC_ID,
                    Tables.ACC.LAST_UPDATE_TIMESTAMP,
                    appUserOwner.LOGIN_ID.as("owner"),
                    appUserUpdater.LOGIN_ID.as("last_update_user"),
                    Tables.RELEASE.RELEASE_NUM
                )
                .from(Tables.ACC)
                .join(Tables.ACC_RELEASE_MANIFEST)
                .on(Tables.ACC.ACC_ID.eq(Tables.ACC_RELEASE_MANIFEST.ACC_ID).and(Tables.ACC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))))
                .join(Tables.RELEASE)
                .on(Tables.ACC_RELEASE_MANIFEST.RELEASE_ID.eq(Tables.RELEASE.RELEASE_ID))
                .join(Tables.MODULE)
                .on(Tables.ACC.MODULE_ID.eq(Tables.MODULE.MODULE_ID))
                .join(appUserOwner)
                .on(Tables.ACC.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(Tables.ACC.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .where(whereCondition)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType("ACC");
                    ccList.setId(row.getValue(Tables.ACC.ACC_ID).longValue());
                    ccList.setGuid(row.getValue(Tables.ACC.GUID));
                    ccList.setDen(row.getValue(Tables.ACC.DEN));
                    ccList.setDefinition(row.getValue(Tables.ACC.DEFINITION));
                    ccList.setDefinitionSource(row.getValue(Tables.ACC.DEFINITION_SOURCE));
                    ccList.setModule(row.getValue(Tables.MODULE.MODULE_));
                    ccList.setOagisComponentType(OagisComponentType.valueOf(row.getValue(Tables.ACC.OAGIS_COMPONENT_TYPE)));
                    ccList.setState(CcState.valueOf(row.getValue(Tables.ACC.STATE)));
                    ccList.setDeprecated(row.getValue(Tables.ACC.IS_DEPRECATED) == 1);
                    ccList.setCurrentId(row.getValue(Tables.ACC.CURRENT_ACC_ID).longValue());
                    ccList.setLastUpdateTimestamp(row.getValue(Tables.ACC.LAST_UPDATE_TIMESTAMP));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(Tables.RELEASE.RELEASE_NUM));
                    return ccList;
                });
    }

    public List<CcList> getAsccList(CcListRequest request) {
        if (!request.getTypes().isAscc() || !StringUtils.isEmpty(request.getModule())) {
            return Collections.emptyList();
        }

        Map<String, List<ASCC>> asccList = coreComponentRepository.getAsccList()
                .stream().collect(groupingBy(CoreComponent::getGuid));

        long releaseId = request.getReleaseId();
        Map<Long, String> usernameMap = request.getUsernameMap();
        return asccList.entrySet().stream()
                .map(entry -> getLatestEntity(releaseId, entry.getValue()))
                .filter(e -> e != null)
                .filter(e -> releaseId > 0 || !e.getDen().contains("User Extension Group"))
                .filter(e -> !e.getDen().endsWith("User Extension Group"))
                .filter(e -> request.getDeprecated() == null || request.getDeprecated() == e.isDeprecated())
                .filter(e -> request.getStates().isEmpty() || request.getStates().contains(CcState.valueOf(e.getState())))
                .filter(e -> request.getOwnerLoginIds().isEmpty() || request.getOwnerLoginIds().contains(usernameMap.get(e.getOwnerUserId())))
                .filter(e -> request.getUpdaterLoginIds().isEmpty() || request.getOwnerLoginIds().contains(usernameMap.get(e.getLastUpdatedBy())))
                .filter(getDenFilter(request.getDen()))
                .filter(e -> StringUtils.isEmpty(request.getDefinition()) || (!StringUtils.isEmpty(e.getDefinition()) && e.getDefinition().toLowerCase().contains(request.getDefinition().trim().toLowerCase())))
                .filter(e -> {
                    Date start = request.getUpdateStartDate();
                    if (start != null) {
                        if (e.getLastUpdateTimestamp().getTime() < start.getTime()) {
                            return false;
                        }
                    }

                    Date end = request.getUpdateEndDate();
                    if (end != null) {
                        return e.getLastUpdateTimestamp().getTime() <= end.getTime();
                    }

                    return true;
                })
                .map(ascc -> {
                    CcList ccList = new CcList();
                    ccList.setType("ASCC");
                    ccList.setId(ascc.getAsccId());
                    ccList.setGuid(ascc.getGuid());
                    ccList.setDen(ascc.getDen());
                    ccList.setDefinition(ascc.getDefinition());
                    ccList.setDefinitionSource(ascc.getDefinitionSource());
                    ccList.setState(CcState.valueOf(ascc.getState()));
                    ccList.setDeprecated(ascc.isDeprecated());
                    ccList.setCurrentId(ascc.getCurrentAsccId());
                    ccList.setLastUpdateTimestamp(ascc.getLastUpdateTimestamp());
                    ccList.setRevision(getRevision(releaseId, asccList.getOrDefault(ascc.getGuid(), Collections.emptyList())));
                    ccList.setOwner(usernameMap.get(ascc.getOwnerUserId()));
                    ccList.setLastUpdateUser(usernameMap.get(ascc.getLastUpdatedBy()));

                    return ccList;
                })
                .collect(Collectors.toList());
    }

    public List<CcList> getBccList(CcListRequest request) {
        if (!request.getTypes().isBcc() || !StringUtils.isEmpty(request.getModule())) {
            return Collections.emptyList();
        }

        Map<String, List<BCC>> bccList = coreComponentRepository.getBccList()
                .stream().collect(groupingBy(CoreComponent::getGuid));

        long releaseId = request.getReleaseId();
        Map<Long, String> usernameMap = request.getUsernameMap();
        return bccList.entrySet().stream()
                .map(entry -> getLatestEntity(releaseId, entry.getValue()))
                .filter(e -> e != null)
                .filter(e -> releaseId > 0 || !e.getDen().contains("User Extension Group"))
                .filter(e -> request.getDeprecated() == null || request.getDeprecated() == e.isDeprecated())
                .filter(e -> request.getStates().isEmpty() || request.getStates().contains(CcState.valueOf(e.getState())))
                .filter(e -> request.getOwnerLoginIds().isEmpty() || request.getOwnerLoginIds().contains(usernameMap.get(e.getOwnerUserId())))
                .filter(e -> request.getUpdaterLoginIds().isEmpty() || request.getOwnerLoginIds().contains(usernameMap.get(e.getLastUpdatedBy())))
                .filter(getDenFilter(request.getDen()))
                .filter(e -> StringUtils.isEmpty(request.getDefinition()) || (!StringUtils.isEmpty(e.getDefinition()) && e.getDefinition().toLowerCase().contains(request.getDefinition().trim().toLowerCase())))
                .filter(e -> {
                    Date start = request.getUpdateStartDate();
                    if (start != null) {
                        if (e.getLastUpdateTimestamp().getTime() < start.getTime()) {
                            return false;
                        }
                    }

                    Date end = request.getUpdateEndDate();
                    if (end != null) {
                        return e.getLastUpdateTimestamp().getTime() <= end.getTime();
                    }

                    return true;
                })
                .map(bcc -> {
                    CcList ccList = new CcList();
                    ccList.setType("BCC");
                    ccList.setId(bcc.getBccId());
                    ccList.setGuid(bcc.getGuid());
                    ccList.setDen(bcc.getDen());
                    ccList.setDefinition(bcc.getDefinition());
                    ccList.setDefinitionSource(bcc.getDefinitionSource());
                    ccList.setState(CcState.valueOf(bcc.getState()));
                    ccList.setDeprecated(bcc.isDeprecated());
                    ccList.setCurrentId(bcc.getCurrentBccId());
                    ccList.setLastUpdateTimestamp(bcc.getLastUpdateTimestamp());
                    ccList.setRevision(getRevision(releaseId, bccList.getOrDefault(bcc.getGuid(), Collections.emptyList())));
                    ccList.setOwner(usernameMap.get(bcc.getOwnerUserId()));
                    ccList.setLastUpdateUser(usernameMap.get(bcc.getLastUpdatedBy()));

                    return ccList;
                })
                .collect(Collectors.toList());
    }

    public List<CcList> getAsccpList(CcListRequest request) {
        if (!request.getTypes().isAsccp()) {
            return Collections.emptyList();
        }

        Map<String, List<ASCCP>> asccpList = coreComponentRepository.getAsccpList()
                .stream().collect(groupingBy(CoreComponent::getGuid));

        long releaseId = request.getReleaseId();
        Map<Long, String> usernameMap = request.getUsernameMap();
        return asccpList.entrySet().stream()
                .map(entry -> getLatestEntity(releaseId, entry.getValue()))
                .filter(e -> e != null)
                .filter(e -> releaseId > 0 || !e.getDen().contains("User Extension Group"))
                .filter(e -> !e.getDen().endsWith("User Extension Group"))
                .filter(e -> request.getDeprecated() == null || request.getDeprecated() == e.isDeprecated())
                .filter(e -> request.getStates().isEmpty() || request.getStates().contains(CcState.valueOf(e.getState())))
                .filter(e -> request.getOwnerLoginIds().isEmpty() || request.getOwnerLoginIds().contains(usernameMap.get(e.getOwnerUserId())))
                .filter(e -> request.getUpdaterLoginIds().isEmpty() || request.getOwnerLoginIds().contains(usernameMap.get(e.getLastUpdatedBy())))
                .filter(getDenFilter(request.getDen()))
                .filter(e -> StringUtils.isEmpty(request.getDefinition()) || (!StringUtils.isEmpty(e.getDefinition()) && e.getDefinition().toLowerCase().contains(request.getDefinition().trim().toLowerCase())))
                .filter(e -> StringUtils.isEmpty(request.getModule()) || (!StringUtils.isEmpty(e.getModule()) && e.getModule().toLowerCase().contains(request.getModule().trim().toLowerCase())))
                .filter(e -> {
                    Date start = request.getUpdateStartDate();
                    if (start != null) {
                        if (e.getLastUpdateTimestamp().getTime() < start.getTime()) {
                            return false;
                        }
                    }

                    Date end = request.getUpdateEndDate();
                    if (end != null) {
                        return e.getLastUpdateTimestamp().getTime() <= end.getTime();
                    }

                    return true;
                })
                .map(asccp -> {
                    CcList ccList = new CcList();
                    ccList.setType("ASCCP");
                    ccList.setId(asccp.getAsccpId());
                    ccList.setGuid(asccp.getGuid());
                    ccList.setDen(asccp.getDen());
                    ccList.setDefinition(asccp.getDefinition());
                    ccList.setDefinitionSource(asccp.getDefinitionSource());
                    ccList.setModule(asccp.getModule());
                    ccList.setState(CcState.valueOf(asccp.getState()));
                    ccList.setDeprecated(asccp.isDeprecated());
                    ccList.setCurrentId(asccp.getCurrentAsccpId());
                    ccList.setLastUpdateTimestamp(asccp.getLastUpdateTimestamp());
                    ccList.setRevision(getRevision(releaseId, asccpList.getOrDefault(asccp.getGuid(), Collections.emptyList())));
                    ccList.setOwner(usernameMap.get(asccp.getOwnerUserId()));
                    ccList.setLastUpdateUser(usernameMap.get(asccp.getLastUpdatedBy()));

                    return ccList;
                })
                .collect(Collectors.toList());
    }

    public List<CcList> getBccpList(CcListRequest request) {
        if (!request.getTypes().isBccp()) {
            return Collections.emptyList();
        }

        Map<String, List<BCCP>> bccpList = coreComponentRepository.getBccpList()
                .stream().collect(groupingBy(CoreComponent::getGuid));

        long releaseId = request.getReleaseId();
        Map<Long, String> usernameMap = request.getUsernameMap();
        return bccpList.entrySet().stream()
                .map(entry -> getLatestEntity(releaseId, entry.getValue()))
                .filter(item -> item != null)
                .filter(e -> request.getDeprecated() == null || request.getDeprecated() == e.isDeprecated())
                .filter(e -> request.getStates().isEmpty() || request.getStates().contains(CcState.valueOf(e.getState())))
                .filter(e -> request.getOwnerLoginIds().isEmpty() || request.getOwnerLoginIds().contains(usernameMap.get(e.getOwnerUserId())))
                .filter(e -> request.getUpdaterLoginIds().isEmpty() || request.getOwnerLoginIds().contains(usernameMap.get(e.getLastUpdatedBy())))
                .filter(getDenFilter(request.getDen()))
                .filter(e -> StringUtils.isEmpty(request.getDefinition()) || (!StringUtils.isEmpty(e.getDefinition()) && e.getDefinition().toLowerCase().contains(request.getDefinition().trim().toLowerCase())))
                .filter(e -> StringUtils.isEmpty(request.getModule()) || (!StringUtils.isEmpty(e.getModule()) && e.getModule().toLowerCase().contains(request.getModule().trim().toLowerCase())))
                .filter(e -> {
                    Date start = request.getUpdateStartDate();
                    if (start != null) {
                        if (e.getLastUpdateTimestamp().getTime() < start.getTime()) {
                            return false;
                        }
                    }

                    Date end = request.getUpdateEndDate();
                    if (end != null) {
                        return e.getLastUpdateTimestamp().getTime() <= end.getTime();
                    }

                    return true;
                })
                .map(bccp -> {
                    CcList ccList = new CcList();
                    ccList.setType("BCCP");
                    ccList.setId(bccp.getBccpId());
                    ccList.setGuid(bccp.getGuid());
                    ccList.setDen(bccp.getDen());
                    ccList.setDefinition(bccp.getDefinition());
                    ccList.setDefinitionSource(bccp.getDefinitionSource());
                    ccList.setModule(bccp.getModule());
                    ccList.setState(CcState.valueOf(bccp.getState()));
                    ccList.setDeprecated(bccp.isDeprecated());
                    ccList.setCurrentId(bccp.getCurrentBccpId());
                    ccList.setLastUpdateTimestamp(bccp.getLastUpdateTimestamp());
                    ccList.setRevision(getRevision(releaseId, bccpList.getOrDefault(bccp.getGuid(), Collections.emptyList())));
                    ccList.setOwner(usernameMap.get(bccp.getOwnerUserId()));
                    ccList.setLastUpdateUser(usernameMap.get(bccp.getLastUpdatedBy()));

                    return ccList;
                })
                .collect(Collectors.toList());
    }

    public List<CcList> getBdtList(CcListRequest request) {
        if (!request.getTypes().isBdt()) {
            return Collections.emptyList();
        }
        Map<String, List<DT>> bdtList = coreComponentRepository.getBdtList()
                .stream().collect(groupingBy(CoreComponent::getGuid));

        long releaseId = request.getReleaseId();
        Map<Long, String> usernameMap = request.getUsernameMap();

        return bdtList.entrySet().stream()
                .map(entry -> getLatestEntity(releaseId, entry.getValue()))
                .filter(item -> item != null)
                .filter(e -> request.getDeprecated() == null || request.getDeprecated() == e.isDeprecated())
                .filter(e -> request.getStates().isEmpty() || request.getStates().contains(CcState.valueOf(e.getState())))
                .filter(e -> request.getOwnerLoginIds().isEmpty() || request.getOwnerLoginIds().contains(usernameMap.get(e.getOwnerUserId())))
                .filter(e -> request.getUpdaterLoginIds().isEmpty() || request.getOwnerLoginIds().contains(usernameMap.get(e.getLastUpdatedBy())))
                .filter(getDenFilter(request.getDen()))
                .filter(e -> StringUtils.isEmpty(request.getDefinition()) || (!StringUtils.isEmpty(e.getDefinition()) && e.getDefinition().toLowerCase().contains(request.getDefinition().trim().toLowerCase())))
                .filter(e -> {
                    Date start = request.getUpdateStartDate();
                    if (start != null) {
                        if (e.getLastUpdateTimestamp().getTime() < start.getTime()) {
                            return false;
                        }
                    }

                    Date end = request.getUpdateEndDate();
                    if (end != null) {
                        return e.getLastUpdateTimestamp().getTime() <= end.getTime();
                    }

                    return true;
                })
                .map(bdt -> {
                    CcList ccList = new CcList();
                    ccList.setType("BDT");
                    ccList.setId(bdt.getDtId());
                    ccList.setGuid(bdt.getGuid());
                    ccList.setDen(bdt.getDen());
                    ccList.setDefinition(bdt.getDefinition());
                    ccList.setDefinitionSource(bdt.getDefinitionSource());
                    ccList.setState(CcState.valueOf(bdt.getState()));
                    ccList.setDeprecated(bdt.isDeprecated());
                    ccList.setLastUpdateTimestamp(bdt.getLastUpdateTimestamp());
                    ccList.setRevision(getRevision(releaseId, bdtList.getOrDefault(bdt.getGuid(), Collections.emptyList())));
                    ccList.setOwner(usernameMap.get(bdt.getOwnerUserId()));
                    ccList.setLastUpdateUser(usernameMap.get(bdt.getLastUpdatedBy()));
                    return ccList;
                })
                .collect(Collectors.toList());
    }

    private Predicate<CoreComponent> getDenFilter(String filter) {
        if (!StringUtils.isEmpty(filter)) {
            filter = filter.trim();
            List<String> filters = Arrays.asList(filter.toLowerCase().split(" ")).stream()
                    .map(e -> e.replaceAll("[^a-z]", "").trim()).collect(Collectors.toList());

            return coreComponent -> {
                List<String> den = Arrays.asList(coreComponent.getDen().toLowerCase().split(" ")).stream()
                        .map(e -> e.replaceAll("[^a-z]", "").trim()).collect(Collectors.toList());

                for (String partialFilter : filters) {
                    if (!den.contains(partialFilter)) {
                        return false;
                    }
                }

                return true;
            };
        } else {
            return coreComponent -> true;
        }
    }

    private Condition getDenFilter(TableField<AccRecord, String> field, String keyword){
        List<String> filters = Arrays.asList(keyword.toLowerCase().split(" ")).stream()
                .map(e -> e.replaceAll("[^a-z]", "").trim()).collect(Collectors.toList());
        return field.in(filters);
    }
}
