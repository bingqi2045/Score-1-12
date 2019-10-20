package org.oagi.srt.gateway.http.api.cc_management.repository;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.types.ULong;
import org.oagi.srt.data.*;
import org.oagi.srt.entity.jooq.Tables;
import org.oagi.srt.entity.jooq.tables.AppUser;
import org.oagi.srt.gateway.http.api.cc_management.data.CcList;
import org.oagi.srt.gateway.http.api.cc_management.data.CcListRequest;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.repository.ReleaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CcListRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private ReleaseRepository releaseRepository;


    public List<CcList> getAccList(CcListRequest request) {
        if (!request.getTypes().isAcc()) {
            return Collections.emptyList();
        }
        Release release = releaseRepository.findById(request.getReleaseId());
        Condition whereCondition = DSL.trueCondition();
        AppUser appUserOwner = Tables.APP_USER.as("owner");
        AppUser appUserUpdater = Tables.APP_USER.as("updater");

        if (release.getReleaseNum().equals("Working")){
            whereCondition = whereCondition.and(Tables.ACC.OAGIS_COMPONENT_TYPE.notEqual(OagisComponentType.UserExtensionGroup.getValue()));
        }
        if (request.getDeprecated() != null) {
            whereCondition = whereCondition.and(Tables.ACC.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            whereCondition = whereCondition.and(Tables.ACC.STATE.in(
                    request.getStates().stream().map(CcState::getValue).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
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
            whereCondition = whereCondition.and(Tables.ACC.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime())));
        }
        if (request.getUpdateEndDate() != null){
            whereCondition = whereCondition.and(Tables.ACC.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime())));
        }

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
                .on(Tables.ACC.ACC_ID.eq(Tables.ACC_RELEASE_MANIFEST.ACC_ID).and(Tables.ACC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
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
        Release release = releaseRepository.findById(request.getReleaseId());
        Condition whereCondition = DSL.trueCondition();
        AppUser appUserOwner = Tables.APP_USER.as("owner");
        AppUser appUserUpdater = Tables.APP_USER.as("updater");

        whereCondition = whereCondition.and(Tables.ASCC.DEN.notLike("%User Extension Group"));

        if (release.getReleaseNum().equals("Working")){
            whereCondition = whereCondition.and(Tables.ASCC.DEN.notContains("User Extension Group"));
        }
        if (request.getDeprecated() != null) {
            whereCondition = whereCondition.and(Tables.ASCC.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            whereCondition = whereCondition.and(Tables.ASCC.STATE.in(
                    request.getStates().stream().map(CcState::getValue).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (request.getDen() != null && !request.getDen().isEmpty()){
            whereCondition = whereCondition.and(getDenFilter(Tables.ASCC.DEN, request.getDen()));
        }
        if (request.getDefinition() != null && !request.getDefinition().isEmpty()){
            whereCondition = whereCondition.and(DSL.lower(Tables.ASCC.DEFINITION).contains(request.getDefinition().trim().toLowerCase()));
        }
        if (request.getUpdateStartDate() != null){
            whereCondition = whereCondition.and(Tables.ASCC.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime())));
        }
        if (request.getUpdateEndDate() != null){
            whereCondition = whereCondition.and(Tables.ASCC.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime())));
        }

        return dslContext.select(Tables.ASCC.ASCC_ID,
                Tables.ASCC.GUID,
                Tables.ASCC.DEN,
                Tables.ASCC.DEFINITION,
                Tables.ASCC.DEFINITION_SOURCE,
                Tables.ASCC.STATE,
                Tables.ASCC.IS_DEPRECATED,
                Tables.ASCC.CURRENT_ASCC_ID,
                Tables.ASCC.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                Tables.RELEASE.RELEASE_NUM)
                .from(Tables.ASCC)
                .join(Tables.ASCC_RELEASE_MANIFEST)
                .on(Tables.ASCC.ASCC_ID.eq(Tables.ASCC_RELEASE_MANIFEST.ASCC_ID).and(Tables.ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(Tables.RELEASE)
                .on(Tables.ASCC_RELEASE_MANIFEST.RELEASE_ID.eq(Tables.RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(Tables.ASCC.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(Tables.ASCC.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .where(whereCondition)
                .fetch().map(ascc -> {
                    CcList ccList = new CcList();
                    ccList.setType("ASCC");
                    ccList.setId(ascc.getValue(Tables.ASCC.ASCC_ID).longValue());
                    ccList.setGuid(ascc.getValue(Tables.ASCC.GUID));
                    ccList.setDen(ascc.getValue(Tables.ASCC.DEN));
                    ccList.setDefinition(ascc.getValue(Tables.ASCC.DEFINITION));
                    ccList.setDefinitionSource(ascc.getValue(Tables.ASCC.DEFINITION_SOURCE));
                    ccList.setState(CcState.valueOf(ascc.getValue(Tables.ASCC.STATE)));
                    ccList.setDeprecated(ascc.getValue(Tables.ASCC.IS_DEPRECATED) == 1);
                    ccList.setCurrentId(ascc.getValue(Tables.ASCC.CURRENT_ASCC_ID).longValue());
                    ccList.setLastUpdateTimestamp(ascc.getValue(Tables.ASCC.LAST_UPDATE_TIMESTAMP));
                    ccList.setOwner((String) ascc.getValue("owner"));
                    ccList.setLastUpdateUser((String) ascc.getValue("last_update_user"));
                    ccList.setRevision(ascc.getValue(Tables.RELEASE.RELEASE_NUM));
                    return ccList;
                });
    }

    public List<CcList> getBccList(CcListRequest request) {
        if (!request.getTypes().isBcc() || !StringUtils.isEmpty(request.getModule())) {
            return Collections.emptyList();
        }

        Release release = releaseRepository.findById(request.getReleaseId());
        Condition whereCondition = DSL.trueCondition();
        AppUser appUserOwner = Tables.APP_USER.as("owner");
        AppUser appUserUpdater = Tables.APP_USER.as("updater");

        if (release.getReleaseNum().equals("Working")){
            whereCondition = whereCondition.and(Tables.BCC.DEN.notContains("User Extension Group"));
        }
        if (request.getDeprecated() != null) {
            whereCondition = whereCondition.and(Tables.BCC.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            whereCondition = whereCondition.and(Tables.BCC.STATE.in(
                    request.getStates().stream().map(CcState::getValue).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (request.getDen() != null && !request.getDen().isEmpty()){
            whereCondition = whereCondition.and(getDenFilter(Tables.BCC.DEN, request.getDen()));
        }
        if (request.getDefinition() != null && !request.getDefinition().isEmpty()){
            whereCondition = whereCondition.and(DSL.lower(Tables.BCC.DEFINITION).contains(request.getDefinition().trim().toLowerCase()));
        }
        if (request.getUpdateStartDate() != null){
            whereCondition = whereCondition.and(Tables.BCC.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime())));
        }
        if (request.getUpdateEndDate() != null){
            whereCondition = whereCondition.and(Tables.BCC.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime())));
        }

        return dslContext.select(Tables.BCC.BCC_ID,
                Tables.BCC.GUID,
                Tables.BCC.DEN,
                Tables.BCC.DEFINITION,
                Tables.BCC.DEFINITION_SOURCE,
                Tables.BCC.STATE,
                Tables.BCC.IS_DEPRECATED,
                Tables.BCC.CURRENT_BCC_ID,
                Tables.BCC.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                Tables.RELEASE.RELEASE_NUM)
                .from(Tables.BCC)
                .join(Tables.BCC_RELEASE_MANIFEST)
                .on(Tables.BCC.BCC_ID.eq(Tables.BCC_RELEASE_MANIFEST.BCC_ID).and(Tables.BCC_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(Tables.RELEASE)
                .on(Tables.BCC_RELEASE_MANIFEST.RELEASE_ID.eq(Tables.RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(Tables.BCC.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(Tables.BCC.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .where(whereCondition)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType("BCC");
                    ccList.setId(row.getValue(Tables.BCC.BCC_ID).longValue());
                    ccList.setGuid(row.getValue(Tables.BCC.GUID));
                    ccList.setDen(row.getValue(Tables.BCC.DEN));
                    ccList.setDefinition(row.getValue(Tables.BCC.DEFINITION));
                    ccList.setDefinitionSource(row.getValue(Tables.BCC.DEFINITION_SOURCE));
                    ccList.setState(CcState.valueOf(row.getValue(Tables.BCC.STATE)));
                    ccList.setDeprecated(row.getValue(Tables.BCC.IS_DEPRECATED) == 1);
                    ccList.setCurrentId(row.getValue(Tables.BCC.CURRENT_BCC_ID).longValue());
                    ccList.setLastUpdateTimestamp(row.getValue(Tables.BCC.LAST_UPDATE_TIMESTAMP));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(Tables.RELEASE.RELEASE_NUM));
                    return ccList;
                });
    }

    public List<CcList> getAsccpList(CcListRequest request) {
        if (!request.getTypes().isAsccp()) {
            return Collections.emptyList();
        }
        Release release = releaseRepository.findById(request.getReleaseId());
        Condition whereCondition = DSL.trueCondition();
        AppUser appUserOwner = Tables.APP_USER.as("owner");
        AppUser appUserUpdater = Tables.APP_USER.as("updater");

        whereCondition = whereCondition.and(Tables.ASCCP.DEN.notLike("%User Extension Group"));

        if (release.getReleaseNum().equals("Working")){
            whereCondition = whereCondition.and(Tables.ASCCP.DEN.notContains("User Extension Group"));
        }
        if (request.getDeprecated() != null) {
            whereCondition = whereCondition.and(Tables.ASCCP.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            whereCondition = whereCondition.and(Tables.ASCCP.STATE.in(
                    request.getStates().stream().map(CcState::getValue).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (request.getDen() != null && !request.getDen().isEmpty()){
            whereCondition = whereCondition.and(getDenFilter(Tables.ASCCP.DEN, request.getDen()));
        }
        if (request.getDefinition() != null && !request.getDefinition().isEmpty()){
            whereCondition = whereCondition.and(DSL.lower(Tables.ASCCP.DEFINITION).contains(request.getDefinition().trim().toLowerCase()));
        }
        if (request.getUpdateStartDate() != null){
            whereCondition = whereCondition.and(Tables.ASCCP.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime())));
        }
        if (request.getUpdateEndDate() != null){
            whereCondition = whereCondition.and(Tables.ASCCP.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime())));
        }

        return dslContext.select(Tables.ASCCP.ASCCP_ID,
                Tables.ASCCP.GUID,
                Tables.ASCCP.DEN,
                Tables.ASCCP.DEFINITION,
                Tables.ASCCP.DEFINITION_SOURCE,
                Tables.ASCCP.STATE,
                Tables.ASCCP.IS_DEPRECATED,
                Tables.ASCCP.CURRENT_ASCCP_ID,
                Tables.ASCCP.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                Tables.RELEASE.RELEASE_NUM)
                .from(Tables.ASCCP)
                .join(Tables.ASCCP_RELEASE_MANIFEST)
                .on(Tables.ASCCP.ASCCP_ID.eq(Tables.ASCCP_RELEASE_MANIFEST.ASCCP_ID).and(Tables.ASCCP_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(Tables.RELEASE)
                .on(Tables.ASCCP_RELEASE_MANIFEST.RELEASE_ID.eq(Tables.RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(Tables.ASCCP.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(Tables.ASCCP.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .where(whereCondition)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType("ASCCP");
                    ccList.setId(row.getValue(Tables.ASCCP.ASCCP_ID).longValue());
                    ccList.setGuid(row.getValue(Tables.ASCCP.GUID));
                    ccList.setDen(row.getValue(Tables.ASCCP.DEN));
                    ccList.setDefinition(row.getValue(Tables.ASCCP.DEFINITION));
                    ccList.setDefinitionSource(row.getValue(Tables.ASCCP.DEFINITION_SOURCE));
                    ccList.setState(CcState.valueOf(row.getValue(Tables.ASCCP.STATE)));
                    ccList.setDeprecated(row.getValue(Tables.ASCCP.IS_DEPRECATED) == 1);
                    ccList.setCurrentId(row.getValue(Tables.ASCCP.CURRENT_ASCCP_ID).longValue());
                    ccList.setLastUpdateTimestamp(row.getValue(Tables.ASCCP.LAST_UPDATE_TIMESTAMP));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(Tables.RELEASE.RELEASE_NUM));
                    return ccList;
                });
    }

    public List<CcList> getBccpList(CcListRequest request) {
        if (!request.getTypes().isBccp()) {
            return Collections.emptyList();
        }
        Release release = releaseRepository.findById(request.getReleaseId());
        Condition whereCondition = DSL.trueCondition();
        AppUser appUserOwner = Tables.APP_USER.as("owner");
        AppUser appUserUpdater = Tables.APP_USER.as("updater");

        if (request.getDeprecated() != null) {
            whereCondition = whereCondition.and(Tables.BCCP.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            whereCondition = whereCondition.and(Tables.BCCP.STATE.in(
                    request.getStates().stream().map(CcState::getValue).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (request.getDen() != null && !request.getDen().isEmpty()) {
            whereCondition = whereCondition.and(getDenFilter(Tables.BCCP.DEN, request.getDen()));
        }
        if (request.getDefinition() != null && !request.getDefinition().isEmpty()) {
            whereCondition = whereCondition.and(DSL.lower(Tables.BCCP.DEFINITION).contains(request.getDefinition().trim().toLowerCase()));
        }
        if (request.getUpdateStartDate() != null) {
            whereCondition = whereCondition.and(Tables.BCCP.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime())));
        }
        if (request.getUpdateEndDate() != null) {
            whereCondition = whereCondition.and(Tables.BCCP.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime())));
        }

        return dslContext.select(Tables.BCCP.BCCP_ID,
                Tables.BCCP.GUID,
                Tables.BCCP.DEN,
                Tables.BCCP.DEFINITION,
                Tables.BCCP.DEFINITION_SOURCE,
                Tables.MODULE.MODULE_,
                Tables.BCCP.STATE,
                Tables.BCCP.IS_DEPRECATED,
                Tables.BCCP.CURRENT_BCCP_ID,
                Tables.BCCP.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                Tables.RELEASE.RELEASE_NUM)
                .from(Tables.BCCP)
                .join(Tables.BCCP_RELEASE_MANIFEST)
                .on(Tables.BCCP.BCCP_ID.eq(Tables.BCCP_RELEASE_MANIFEST.BCCP_ID).and(Tables.BCCP_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(Tables.RELEASE)
                .on(Tables.BCCP_RELEASE_MANIFEST.RELEASE_ID.eq(Tables.RELEASE.RELEASE_ID))
                .join(Tables.MODULE)
                .on(Tables.BCCP.MODULE_ID.eq(Tables.MODULE.MODULE_ID))
                .join(appUserOwner)
                .on(Tables.BCCP.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(Tables.BCCP.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .where(whereCondition)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType("BCCP");
                    ccList.setId(row.getValue(Tables.BCCP.BCCP_ID).longValue());
                    ccList.setGuid(row.getValue(Tables.BCCP.GUID));
                    ccList.setDen(row.getValue(Tables.BCCP.DEN));
                    ccList.setDefinition(row.getValue(Tables.BCCP.DEFINITION));
                    ccList.setDefinitionSource(row.getValue(Tables.BCCP.DEFINITION_SOURCE));
                    ccList.setModule(row.getValue(Tables.MODULE.MODULE_));
                    ccList.setState(CcState.valueOf(row.getValue(Tables.BCCP.STATE)));
                    ccList.setDeprecated(row.getValue(Tables.BCCP.IS_DEPRECATED) == 1);
                    ccList.setCurrentId(row.getValue(Tables.BCCP.CURRENT_BCCP_ID).longValue());
                    ccList.setLastUpdateTimestamp(row.getValue(Tables.BCCP.LAST_UPDATE_TIMESTAMP));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(Tables.RELEASE.RELEASE_NUM));
                    return ccList;
                });
    }

    public List<CcList> getBdtList(CcListRequest request) {
        if (!request.getTypes().isBdt()) {
            return Collections.emptyList();
        }
        Release release = releaseRepository.findById(request.getReleaseId());
        Condition whereCondition = DSL.trueCondition();
        AppUser appUserOwner = Tables.APP_USER.as("owner");
        AppUser appUserUpdater = Tables.APP_USER.as("updater");

        if (request.getDeprecated() != null) {
            whereCondition = whereCondition.and(Tables.DT.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (!request.getStates().isEmpty()) {
            whereCondition = whereCondition.and(Tables.DT.STATE.in(
                    request.getStates().stream().map(CcState::getValue).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            whereCondition = whereCondition.and(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (request.getDen() != null && !request.getDen().isEmpty()) {
            whereCondition = whereCondition.and(getDenFilter(Tables.DT.DEN, request.getDen()));
        }
        if (request.getDefinition() != null && !request.getDefinition().isEmpty()) {
            whereCondition = whereCondition.and(DSL.lower(Tables.DT.DEFINITION).contains(request.getDefinition().trim().toLowerCase()));
        }
        if (request.getUpdateStartDate() != null) {
            whereCondition = whereCondition.and(Tables.DT.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime())));
        }
        if (request.getUpdateEndDate() != null) {
            whereCondition = whereCondition.and(Tables.DT.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime())));
        }

        return dslContext.select(Tables.DT.DT_ID,
                Tables.DT.GUID,
                Tables.DT.DEN,
                Tables.DT.DEFINITION,
                Tables.DT.DEFINITION_SOURCE,
                Tables.DT.STATE,
                Tables.DT.IS_DEPRECATED,
                Tables.DT.CURRENT_BDT_ID,
                Tables.DT.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                Tables.RELEASE.RELEASE_NUM)
                .from(Tables.DT)
                .join(Tables.DT_RELEASE_MANIFEST)
                .on(Tables.DT.DT_ID.eq(Tables.DT_RELEASE_MANIFEST.DT_ID).and(Tables.DT_RELEASE_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(Tables.RELEASE)
                .on(Tables.DT_RELEASE_MANIFEST.RELEASE_ID.eq(Tables.RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(Tables.DT.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(Tables.DT.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .where(whereCondition)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType("BDT");
                    ccList.setId(row.getValue(Tables.DT.DT_ID).longValue());
                    ccList.setGuid(row.getValue(Tables.DT.GUID));
                    ccList.setDen(row.getValue(Tables.DT.DEN));
                    ccList.setDefinition(row.getValue(Tables.DT.DEFINITION));
                    ccList.setDefinitionSource(row.getValue(Tables.DT.DEFINITION_SOURCE));
                    ccList.setState(CcState.valueOf(row.getValue(Tables.DT.STATE)));
                    ccList.setDeprecated(row.getValue(Tables.DT.IS_DEPRECATED) == 1);
                    ccList.setCurrentId(row.getValue(Tables.DT.CURRENT_BDT_ID).longValue());
                    ccList.setLastUpdateTimestamp(row.getValue(Tables.DT.LAST_UPDATE_TIMESTAMP));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(Tables.RELEASE.RELEASE_NUM));
                    return ccList;
                });
    }

    private Condition getDenFilter(TableField<?, String> field, String keyword) {
        Condition denCondition = null;
        List<String> filters = Arrays.asList(keyword.toLowerCase().split(" ")).stream()
                .map(e -> e.replaceAll("[^a-z]", "").trim()).collect(Collectors.toList());
        for (String token : filters) {
            if (denCondition == null){
                denCondition = field.contains(token);
            } else {
                denCondition = denCondition.and(field.contains(token));
            }

        }
        return denCondition;
    }
}
