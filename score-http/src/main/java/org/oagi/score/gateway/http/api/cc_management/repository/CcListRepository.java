package org.oagi.score.gateway.http.api.cc_management.repository;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.data.DTType;
import org.oagi.score.data.OagisComponentType;
import org.oagi.score.data.Release;
import org.oagi.score.gateway.http.api.cc_management.data.*;
import org.oagi.score.gateway.http.helper.filter.ContainsFilterBuilder;
import org.oagi.score.repo.api.impl.jooq.entity.tables.AppUser;
import org.oagi.score.repo.component.release.ReleaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.*;
import static org.oagi.score.data.OagisComponentType.*;
import static org.oagi.score.gateway.http.api.module_management.data.Module.MODULE_SEPARATOR;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;

@Repository
public class CcListRepository {

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private ReleaseRepository releaseRepository;

    private BigInteger getManifestIdByObjectClassTermAndReleaseId(String objectClassTerm, BigInteger releaseId) {
        return dslContext.select(ACC_MANIFEST.ACC_MANIFEST_ID)
                .from(ACC_MANIFEST)
                .join(ACC).on(ACC_MANIFEST.ACC_ID.eq(ACC.ACC_ID))
                .where(and(
                        ACC.OBJECT_CLASS_TERM.eq(objectClassTerm),
                        ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))
                ))
                .fetchOptionalInto(BigInteger.class).orElse(BigInteger.ZERO);
    }

    private List<BigInteger> getManifestIdsByBasedAccManifestIdAndReleaseId(
            List<BigInteger> basedManifestIds, BigInteger releaseId) {
        return dslContext.select(ACC_MANIFEST.ACC_MANIFEST_ID)
                .from(ACC_MANIFEST)
                .where(and(
                        ACC_MANIFEST.BASED_ACC_MANIFEST_ID.in(basedManifestIds),
                        ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId))
                ))
                .fetchInto(BigInteger.class);
    }

    public List<CcList> getAccList(CcListRequest request) {
        if (!request.getTypes().isAcc()) {
            return Collections.emptyList();
        }

        Release release = releaseRepository.findById(request.getReleaseId());
        AppUser appUserOwner = APP_USER.as("owner");
        AppUser appUserUpdater = APP_USER.as("updater");

        List<Condition> conditions = new ArrayList();
        conditions.add(ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())));
        if ("Working".equals(release.getReleaseNum())) {
            conditions.add(ACC.OAGIS_COMPONENT_TYPE.notEqual(OagisComponentType.UserExtensionGroup.getValue()));
        }
        if (request.getDeprecated() != null) {
            conditions.add(ACC.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (request.getStates() != null) {
            conditions.add(ACC.STATE.in(
                    request.getStates().stream().map(CcState::name).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            conditions.add(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (!request.getExcludes().isEmpty()) {
            conditions.add(ACC_MANIFEST.ACC_MANIFEST_ID.notIn(request.getExcludes()));
        }
        if (!StringUtils.isEmpty(request.getDen())) {
            conditions.addAll(ContainsFilterBuilder.contains(request.getDen(), ACC.DEN));
        }
        if (!StringUtils.isEmpty(request.getDefinition())) {
            conditions.addAll(ContainsFilterBuilder.contains(request.getDefinition(), ACC.DEFINITION));
        }
        if (!StringUtils.isEmpty(request.getModule())) {
            conditions.add(concat(MODULE_DIR.PATH, inline("/"), MODULE.NAME).containsIgnoreCase(request.getModule()));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(ACC.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime()).toLocalDateTime()));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(ACC.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime()).toLocalDateTime()));
        }
        if (request.getComponentTypes() != null && !request.getComponentTypes().isEmpty()) {

            List<OagisComponentType> componentTypes = Arrays.asList(request.getComponentTypes().split(","))
                    .stream()
                    .map(e -> OagisComponentType.valueOf(Integer.parseInt(e)))
                    .collect(Collectors.toList());

            List<OagisComponentType> usualComponentTypes = componentTypes.stream()
                    .filter(e -> !Arrays.asList(BOD, Verb, Noun).contains(e))
                    .collect(Collectors.toList());

            if (!usualComponentTypes.isEmpty()) {
                conditions.add(ACC.OAGIS_COMPONENT_TYPE.in(usualComponentTypes.stream()
                        .map(e -> e.getValue()).collect(Collectors.toList())));
            }

            List<OagisComponentType> unusualComponentTypes = componentTypes.stream()
                    .filter(e -> Arrays.asList(BOD, Verb, Noun).contains(e))
                    .collect(Collectors.toList());

            if (!unusualComponentTypes.isEmpty()) {
                for (OagisComponentType unusualComponentType : unusualComponentTypes) {
                    switch (unusualComponentType) {
                        case BOD:
                            BigInteger bodBasedAccManifestId = getManifestIdByObjectClassTermAndReleaseId(
                                    "Business Object Document", request.getReleaseId());
                            conditions.add(ACC_MANIFEST.BASED_ACC_MANIFEST_ID.eq(ULong.valueOf(bodBasedAccManifestId)));
                            break;

                        case Verb:
                            BigInteger verbAccManifestId = getManifestIdByObjectClassTermAndReleaseId(
                                    "Verb", request.getReleaseId());

                            Set<BigInteger> verbManifestIds = new HashSet();
                            verbManifestIds.add(verbAccManifestId);

                            List<BigInteger> basedAccManifestIds = new ArrayList();
                            basedAccManifestIds.add(verbAccManifestId);

                            while (!basedAccManifestIds.isEmpty()) {
                                basedAccManifestIds = getManifestIdsByBasedAccManifestIdAndReleaseId(
                                        basedAccManifestIds, request.getReleaseId());
                                verbManifestIds.addAll(basedAccManifestIds);
                            }

                            conditions.add(ACC_MANIFEST.ACC_MANIFEST_ID.in(verbManifestIds.stream()
                                    .map(e -> ULong.valueOf(e)).collect(Collectors.toList())));

                            break;

                        case Noun:
                            // TODO:

                            break;
                    }
                }
            }
        }

        if (request.getFindUsages() != null) {
            CcId findUsages = request.getFindUsages();
            Set<ULong> usages = new HashSet();
            switch (CcType.valueOf(findUsages.getType())) {
                case ACC:
                    usages.addAll(
                            dslContext.selectDistinct(ACC_MANIFEST.ACC_MANIFEST_ID)
                                    .from(ACC_MANIFEST)
                                    .where(and(
                                            ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())),
                                            ACC_MANIFEST.BASED_ACC_MANIFEST_ID.eq(ULong.valueOf(findUsages.getManifestId()))
                                    ))
                                    .fetchInto(ULong.class)
                    );
                    break;

                case ASCCP:
                    usages.addAll(
                            dslContext.selectDistinct(ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID)
                                    .from(ASCCP_MANIFEST)
                                    .where(and(
                                            ASCCP_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())),
                                            ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(ULong.valueOf(findUsages.getManifestId()))
                                    ))
                                    .fetchInto(ULong.class)
                    );

                    usages.addAll(
                            dslContext.selectDistinct(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID)
                                    .from(ASCC_MANIFEST)
                                    .where(and(
                                            ASCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())),
                                            ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID.eq(ULong.valueOf(findUsages.getManifestId()))
                                    ))
                                    .fetchInto(ULong.class)
                    );
                    break;

                case BCCP:
                    usages.addAll(
                            dslContext.selectDistinct(BCC_MANIFEST.FROM_ACC_MANIFEST_ID)
                                    .from(BCC_MANIFEST)
                                    .where(and(
                                            BCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())),
                                            BCC_MANIFEST.TO_BCCP_MANIFEST_ID.eq(ULong.valueOf(findUsages.getManifestId()))
                                    ))
                                    .fetchInto(ULong.class)
                    );
                    break;
            }
            if (!usages.isEmpty()) {
                conditions.add(ACC_MANIFEST.ACC_MANIFEST_ID.in(usages));
            }
        }

        return dslContext.select(
                ACC_MANIFEST.ACC_MANIFEST_ID,
                ACC.ACC_ID,
                ACC.GUID,
                ACC.DEN,
                ACC.DEFINITION,
                ACC.DEFINITION_SOURCE,
                MODULE.NAME,
                MODULE_DIR.PATH,
                ACC.OAGIS_COMPONENT_TYPE,
                ACC.STATE,
                ACC.IS_DEPRECATED,
                ACC.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM,
                RELEASE.RELEASE_NUM)
                .from(ACC)
                .join(ACC_MANIFEST)
                .on(ACC.ACC_ID.eq(ACC_MANIFEST.ACC_ID).and(ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(REVISION)
                .on(ACC_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .join(RELEASE)
                .on(ACC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(ACC.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(ACC.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .leftJoin(MODULE_ACC_MANIFEST)
                .on(ACC_MANIFEST.ACC_MANIFEST_ID.eq(MODULE_ACC_MANIFEST.ACC_MANIFEST_ID))
                .leftJoin(MODULE_SET_ASSIGNMENT)
                .on(MODULE_ACC_MANIFEST.MODULE_SET_ASSIGNMENT_ID.eq(MODULE_SET_ASSIGNMENT.MODULE_SET_ASSIGNMENT_ID))
                .leftJoin(MODULE)
                .on(MODULE_SET_ASSIGNMENT.MODULE_ID.eq(MODULE.MODULE_ID))
                .leftJoin(MODULE_DIR)
                .on(MODULE.MODULE_DIR_ID.eq(MODULE_DIR.MODULE_DIR_ID))
                .where(conditions)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType(CcType.ACC);
                    ccList.setManifestId(row.getValue(ACC_MANIFEST.ACC_MANIFEST_ID).toBigInteger());
                    ccList.setId(row.getValue(ACC.ACC_ID).toBigInteger());
                    ccList.setGuid(row.getValue(ACC.GUID));
                    ccList.setDen(row.getValue(ACC.DEN));
                    ccList.setDefinition(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(ACC.DEFINITION)));
                    ccList.setDefinitionSource(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(ACC.DEFINITION_SOURCE)));
                    if (!StringUtils.isEmpty(row.getValue(MODULE.NAME))) {
                        ccList.setModule(row.getValue(MODULE_DIR.PATH) + MODULE_SEPARATOR + row.getValue(MODULE.NAME));
                    }
                    ccList.setOagisComponentType(OagisComponentType.valueOf(row.getValue(ACC.OAGIS_COMPONENT_TYPE)));
                    ccList.setState(CcState.valueOf(row.getValue(ACC.STATE)));
                    ccList.setDeprecated(row.getValue(ACC.IS_DEPRECATED) == 1);
                    ccList.setLastUpdateTimestamp(Date.from(row.getValue(ACC.LAST_UPDATE_TIMESTAMP).atZone(ZoneId.systemDefault()).toInstant()));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(REVISION.REVISION_NUM).toString());
                    ccList.setReleaseNum(row.getValue(RELEASE.RELEASE_NUM));
                    return ccList;
                });
    }

    public List<CcList> getAsccList(CcListRequest request) {
        if (!request.getTypes().isAscc()) {
            return Collections.emptyList();
        }

        Release release = releaseRepository.findById(request.getReleaseId());
        AppUser appUserOwner = APP_USER.as("owner");
        AppUser appUserUpdater = APP_USER.as("updater");

        List<Condition> conditions = new ArrayList();
        conditions.add(ASCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())));
        conditions.add(ASCC.DEN.notContains("User Extension Group"));

        if (request.getDeprecated() != null) {
            conditions.add(ASCC.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (request.getStates() != null) {
            conditions.add(ASCC.STATE.in(
                    request.getStates().stream().map(CcState::name).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            conditions.add(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (!request.getExcludes().isEmpty()) {
            conditions.add(ASCC_MANIFEST.ASCC_MANIFEST_ID.notIn(request.getExcludes()));
        }
        if (!StringUtils.isEmpty(request.getDen())) {
            conditions.addAll(ContainsFilterBuilder.contains(request.getDen(), ASCC.DEN));
        }
        if (!StringUtils.isEmpty(request.getDefinition())) {
            conditions.addAll(ContainsFilterBuilder.contains(request.getDefinition(), ASCC.DEFINITION));
        }
        if (!StringUtils.isEmpty(request.getModule())) {
            conditions.add(concat(MODULE_DIR.PATH, inline(MODULE_SEPARATOR), MODULE.NAME).containsIgnoreCase(request.getModule()));
        }

        if (request.getUpdateStartDate() != null) {
            conditions.add(ASCC.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime()).toLocalDateTime()));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(ASCC.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime()).toLocalDateTime()));
        }

        if (request.getFindUsages() != null) {
            CcId findUsages = request.getFindUsages();
            Set<ULong> usages = new HashSet();
            switch (CcType.valueOf(findUsages.getType())) {
                case ACC:
                    usages.addAll(
                            dslContext.selectDistinct(ASCC_MANIFEST.ASCC_MANIFEST_ID)
                                    .from(ASCC_MANIFEST)
                                    .where(and(
                                            ASCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())),
                                            ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ULong.valueOf(findUsages.getManifestId()))
                                    ))
                                    .fetchInto(ULong.class)
                    );
                    break;

                case ASCCP:
                    usages.addAll(
                            dslContext.selectDistinct(ASCC_MANIFEST.ASCC_MANIFEST_ID)
                                    .from(ASCC_MANIFEST)
                                    .where(and(
                                            ASCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())),
                                            ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID.eq(ULong.valueOf(findUsages.getManifestId()))
                                    ))
                                    .fetchInto(ULong.class)
                    );
                    break;
            }
            if (!usages.isEmpty()) {
                conditions.add(ASCC_MANIFEST.ASCC_MANIFEST_ID.in(usages));
            }
        }

        return dslContext.select(
                ASCC_MANIFEST.ASCC_MANIFEST_ID,
                ASCC.ASCC_ID,
                ASCC.GUID,
                ASCC.DEN,
                ASCC.DEFINITION,
                ASCC.DEFINITION_SOURCE,
                ASCC.STATE,
                ASCC.IS_DEPRECATED,
                ASCC.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                MODULE.NAME,
                MODULE_DIR.PATH,
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM,
                RELEASE.RELEASE_NUM)
                .from(ASCC)
                .join(ASCC_MANIFEST)
                .on(ASCC.ASCC_ID.eq(ASCC_MANIFEST.ASCC_ID).and(ASCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(ACC_MANIFEST)
                .on(and(
                        ASCC_MANIFEST.RELEASE_ID.eq(ACC_MANIFEST.RELEASE_ID),
                        ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID)
                ))
                .join(REVISION)
                .on(ACC_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .join(RELEASE)
                .on(ASCC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(ASCC.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(ASCC.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .leftJoin(MODULE_ACC_MANIFEST)
                .on(ACC_MANIFEST.ACC_MANIFEST_ID.eq(MODULE_ACC_MANIFEST.ACC_MANIFEST_ID))
                .leftJoin(MODULE_SET_ASSIGNMENT)
                .on(MODULE_ACC_MANIFEST.MODULE_SET_ASSIGNMENT_ID.eq(MODULE_SET_ASSIGNMENT.MODULE_SET_ASSIGNMENT_ID))
                .leftJoin(MODULE)
                .on(MODULE_SET_ASSIGNMENT.MODULE_ID.eq(MODULE.MODULE_ID))
                .leftJoin(MODULE_DIR)
                .on(MODULE.MODULE_DIR_ID.eq(MODULE_DIR.MODULE_DIR_ID))
                .where(conditions)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType(CcType.ASCC);
                    ccList.setManifestId(row.getValue(ASCC_MANIFEST.ASCC_MANIFEST_ID).toBigInteger());
                    ccList.setId(row.getValue(ASCC.ASCC_ID).toBigInteger());
                    ccList.setGuid(row.getValue(ASCC.GUID));
                    ccList.setDen(row.getValue(ASCC.DEN));
                    ccList.setDefinition(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(ASCC.DEFINITION)));
                    ccList.setDefinitionSource(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(ASCC.DEFINITION_SOURCE)));
                    ccList.setState(CcState.valueOf(row.getValue(ASCC.STATE)));
                    ccList.setDeprecated(row.getValue(ASCC.IS_DEPRECATED) == 1);
                    if (!StringUtils.isEmpty(row.getValue(MODULE.NAME))) {
                        ccList.setModule(row.getValue(MODULE_DIR.PATH) + MODULE_SEPARATOR + row.getValue(MODULE.NAME));
                    }
                    ccList.setLastUpdateTimestamp(Date.from(row.getValue(ASCC.LAST_UPDATE_TIMESTAMP).atZone(ZoneId.systemDefault()).toInstant()));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(REVISION.REVISION_NUM).toString());
                    ccList.setReleaseNum(row.getValue(RELEASE.RELEASE_NUM));
                    return ccList;
                });
    }

    public List<CcList> getBccList(CcListRequest request) {
        if (!request.getTypes().isBcc()) {
            return Collections.emptyList();
        }

        Release release = releaseRepository.findById(request.getReleaseId());
        AppUser appUserOwner = APP_USER.as("owner");
        AppUser appUserUpdater = APP_USER.as("updater");

        List<Condition> conditions = new ArrayList();
        conditions.add(BCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())));
        conditions.add(BCC.DEN.notContains("User Extension Group"));

        if (request.getDeprecated() != null) {
            conditions.add(BCC.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (request.getStates() != null) {
            conditions.add(BCC.STATE.in(
                    request.getStates().stream().map(CcState::name).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            conditions.add(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (!request.getExcludes().isEmpty()) {
            conditions.add(BCC_MANIFEST.BCC_MANIFEST_ID.notIn(request.getExcludes()));
        }
        if (!StringUtils.isEmpty(request.getDen())) {
            conditions.addAll(ContainsFilterBuilder.contains(request.getDen(), BCC.DEN));
        }
        if (!StringUtils.isEmpty(request.getDefinition())) {
            conditions.addAll(ContainsFilterBuilder.contains(request.getDefinition(), BCC.DEFINITION));
        }
        if (!StringUtils.isEmpty(request.getModule())) {
            conditions.add(concat(MODULE_DIR.PATH, inline(MODULE_SEPARATOR), MODULE.NAME).containsIgnoreCase(request.getModule()));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(BCC.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime()).toLocalDateTime()));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(BCC.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime()).toLocalDateTime()));
        }

        if (request.getFindUsages() != null) {
            CcId findUsages = request.getFindUsages();
            Set<ULong> usages = new HashSet();
            switch (CcType.valueOf(findUsages.getType())) {
                case ACC:
                    usages.addAll(
                            dslContext.selectDistinct(BCC_MANIFEST.BCC_MANIFEST_ID)
                                    .from(BCC_MANIFEST)
                                    .where(and(
                                            BCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())),
                                            BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ULong.valueOf(findUsages.getManifestId()))
                                    ))
                                    .fetchInto(ULong.class)
                    );
                    break;

                case BCCP:
                    usages.addAll(
                            dslContext.selectDistinct(BCC_MANIFEST.BCC_MANIFEST_ID)
                                    .from(BCC_MANIFEST)
                                    .where(and(
                                            BCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())),
                                            BCC_MANIFEST.TO_BCCP_MANIFEST_ID.eq(ULong.valueOf(findUsages.getManifestId()))
                                    ))
                                    .fetchInto(ULong.class)
                    );
                    break;
            }
            if (!usages.isEmpty()) {
                conditions.add(BCC_MANIFEST.BCC_MANIFEST_ID.in(usages));
            }
        }

        return dslContext.select(
                BCC_MANIFEST.BCC_MANIFEST_ID,
                BCC.BCC_ID,
                BCC.GUID,
                BCC.DEN,
                BCC.DEFINITION,
                BCC.DEFINITION_SOURCE,
                BCC.STATE,
                BCC.IS_DEPRECATED,
                MODULE.NAME,
                MODULE_DIR.PATH,
                BCC.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM,
                RELEASE.RELEASE_NUM)
                .from(BCC)
                .join(BCC_MANIFEST)
                .on(BCC.BCC_ID.eq(BCC_MANIFEST.BCC_ID).and(BCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(ACC_MANIFEST)
                .on(and(
                        BCC_MANIFEST.RELEASE_ID.eq(ACC_MANIFEST.RELEASE_ID),
                        BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID)
                ))
                .join(REVISION)
                .on(ACC_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .join(RELEASE)
                .on(BCC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(BCC.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(BCC.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .leftJoin(MODULE_ACC_MANIFEST)
                .on(ACC_MANIFEST.ACC_MANIFEST_ID.eq(MODULE_ACC_MANIFEST.ACC_MANIFEST_ID))
                .leftJoin(MODULE_SET_ASSIGNMENT)
                .on(MODULE_ACC_MANIFEST.MODULE_SET_ASSIGNMENT_ID.eq(MODULE_SET_ASSIGNMENT.MODULE_SET_ASSIGNMENT_ID))
                .leftJoin(MODULE)
                .on(MODULE_SET_ASSIGNMENT.MODULE_ID.eq(MODULE.MODULE_ID))
                .leftJoin(MODULE_DIR)
                .on(MODULE.MODULE_DIR_ID.eq(MODULE_DIR.MODULE_DIR_ID))
                .where(conditions)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType(CcType.BCC);
                    ccList.setManifestId(row.getValue(BCC_MANIFEST.BCC_MANIFEST_ID).toBigInteger());
                    ccList.setId(row.getValue(BCC.BCC_ID).toBigInteger());
                    ccList.setGuid(row.getValue(BCC.GUID));
                    ccList.setDen(row.getValue(BCC.DEN));
                    ccList.setDefinition(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(BCC.DEFINITION)));
                    ccList.setDefinitionSource(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(BCC.DEFINITION_SOURCE)));
                    ccList.setState(CcState.valueOf(row.getValue(BCC.STATE)));
                    ccList.setDeprecated(row.getValue(BCC.IS_DEPRECATED) == 1);
                    ccList.setLastUpdateTimestamp(Date.from(row.getValue(BCC.LAST_UPDATE_TIMESTAMP).atZone(ZoneId.systemDefault()).toInstant()));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    if (!StringUtils.isEmpty(row.getValue(MODULE.NAME))) {
                        ccList.setModule(row.getValue(MODULE_DIR.PATH) + MODULE_SEPARATOR + row.getValue(MODULE.NAME));
                    }
                    ccList.setRevision(row.getValue(REVISION.REVISION_NUM).toString());
                    ccList.setReleaseNum(row.getValue(RELEASE.RELEASE_NUM));
                    return ccList;
                });
    }

    public List<CcList> getAsccpList(CcListRequest request) {
        if (!request.getTypes().isAsccp()) {
            return Collections.emptyList();
        }

        Release release = releaseRepository.findById(request.getReleaseId());
        AppUser appUserOwner = APP_USER.as("owner");
        AppUser appUserUpdater = APP_USER.as("updater");

        List<Condition> conditions = new ArrayList();
        conditions.add(ASCCP_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())));
        conditions.add(ASCCP.DEN.notContains("User Extension Group"));

        if (request.getDeprecated() != null) {
            conditions.add(ASCCP.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (request.getStates() != null) {
            conditions.add(ASCCP.STATE.in(
                    request.getStates().stream().map(CcState::name).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            conditions.add(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (!request.getExcludes().isEmpty()) {
            conditions.add(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.notIn(request.getExcludes()));
        }
        if (!StringUtils.isEmpty(request.getDen())) {
            conditions.addAll(ContainsFilterBuilder.contains(request.getDen(), ASCCP.DEN));
        }
        if (!StringUtils.isEmpty(request.getDefinition())) {
            conditions.addAll(ContainsFilterBuilder.contains(request.getDefinition(), ASCCP.DEFINITION));
        }
        if (!StringUtils.isEmpty(request.getModule())) {
            conditions.add(concat(MODULE_DIR.PATH, inline(MODULE_SEPARATOR), MODULE.NAME).containsIgnoreCase(request.getModule()));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(ASCCP.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime()).toLocalDateTime()));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(ASCCP.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime()).toLocalDateTime()));
        }

        if (request.getAsccpTypes().size() != 0) {
            conditions.add(ASCCP.TYPE.in(request.getAsccpTypes()));
        }

        if (request.getFindUsages() != null) {
            CcId findUsages = request.getFindUsages();
            Set<ULong> usages = new HashSet();
            switch (CcType.valueOf(findUsages.getType())) {
                case ACC:
                    usages.addAll(
                            dslContext.selectDistinct(ASCCP_MANIFEST.ASCCP_MANIFEST_ID)
                                    .from(ASCCP_MANIFEST)
                                    .where(and(
                                            ASCCP_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())),
                                            ASCCP_MANIFEST.ROLE_OF_ACC_MANIFEST_ID.eq(ULong.valueOf(findUsages.getManifestId()))
                                    ))
                                    .fetchInto(ULong.class)
                    );

                    usages.addAll(
                            dslContext.selectDistinct(ASCC_MANIFEST.TO_ASCCP_MANIFEST_ID)
                                    .from(ASCC_MANIFEST)
                                    .join(ACC_MANIFEST).on(ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                                    .where(and(
                                            ASCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())),
                                            ASCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ULong.valueOf(findUsages.getManifestId()))
                                    ))
                                    .fetchInto(ULong.class)
                    );
                    break;
            }
            if (!usages.isEmpty()) {
                conditions.add(ACC_MANIFEST.ACC_MANIFEST_ID.in(usages));
            }
        }

        return dslContext.select(
                ASCCP_MANIFEST.ASCCP_MANIFEST_ID,
                ASCCP.ASCCP_ID,
                ASCCP.GUID,
                ASCCP.PROPERTY_TERM.as("propertyTerm"),
                ASCCP.DEN,
                ASCCP.DEFINITION,
                ASCCP.DEFINITION_SOURCE,
                MODULE.NAME,
                MODULE_DIR.PATH,
                ASCCP.STATE,
                ASCCP.IS_DEPRECATED,
                ASCCP.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM,
                RELEASE.RELEASE_NUM)
                .from(ASCCP)
                .join(ASCCP_MANIFEST)
                .on(ASCCP.ASCCP_ID.eq(ASCCP_MANIFEST.ASCCP_ID).and(ASCCP_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(REVISION)
                .on(ASCCP_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .join(RELEASE)
                .on(ASCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(ASCCP.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(ASCCP.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .leftJoin(MODULE_ASCCP_MANIFEST)
                .on(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(MODULE_ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                .leftJoin(MODULE_SET_ASSIGNMENT)
                .on(MODULE_ASCCP_MANIFEST.MODULE_SET_ASSIGNMENT_ID.eq(MODULE_SET_ASSIGNMENT.MODULE_SET_ASSIGNMENT_ID))
                .leftJoin(MODULE)
                .on(MODULE_SET_ASSIGNMENT.MODULE_ID.eq(MODULE.MODULE_ID))
                .leftJoin(MODULE_DIR)
                .on(MODULE.MODULE_DIR_ID.eq(MODULE_DIR.MODULE_DIR_ID))
                .where(conditions)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType(CcType.ASCCP);
                    ccList.setManifestId(row.getValue(ASCCP_MANIFEST.ASCCP_MANIFEST_ID).toBigInteger());
                    ccList.setId(row.getValue(ASCCP.ASCCP_ID).toBigInteger());
                    ccList.setGuid(row.getValue(ASCCP.GUID));
                    ccList.setName((String) row.getValue("propertyTerm"));
                    ccList.setDen(row.getValue(ASCCP.DEN));
                    ccList.setDefinition(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(ASCCP.DEFINITION)));
                    ccList.setDefinitionSource(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(ASCCP.DEFINITION_SOURCE)));
                    if (!StringUtils.isEmpty(row.getValue(MODULE.NAME))) {
                        ccList.setModule(row.getValue(MODULE_DIR.PATH) + MODULE_SEPARATOR + row.getValue(MODULE.NAME));
                    }
                    ccList.setState(CcState.valueOf(row.getValue(ASCCP.STATE)));
                    ccList.setDeprecated(row.getValue(ASCCP.IS_DEPRECATED) == 1);
                    ccList.setLastUpdateTimestamp(Date.from(row.getValue(ASCCP.LAST_UPDATE_TIMESTAMP).atZone(ZoneId.systemDefault()).toInstant()));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(REVISION.REVISION_NUM).toString());
                    ccList.setReleaseNum(row.getValue(RELEASE.RELEASE_NUM));
                    return ccList;
                });
    }

    public List<CcList> getBccpList(CcListRequest request) {
        if (!request.getTypes().isBccp()) {
            return Collections.emptyList();
        }

        Release release = releaseRepository.findById(request.getReleaseId());
        AppUser appUserOwner = APP_USER.as("owner");
        AppUser appUserUpdater = APP_USER.as("updater");

        List<Condition> conditions = new ArrayList();
        conditions.add(BCCP_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())));
        conditions.add(BCCP.DEN.notContains("User Extension Group"));
        if (request.getDeprecated() != null) {
            conditions.add(BCCP.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (request.getStates() != null) {
            conditions.add(BCCP.STATE.in(
                    request.getStates().stream().map(CcState::name).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            conditions.add(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (!request.getExcludes().isEmpty()) {
            conditions.add(BCCP_MANIFEST.BCCP_MANIFEST_ID.notIn(request.getExcludes()));
        }
        if (!StringUtils.isEmpty(request.getDen())) {
            conditions.addAll(ContainsFilterBuilder.contains(request.getDen(), BCCP.DEN));
        }
        if (!StringUtils.isEmpty(request.getDefinition())) {
            conditions.addAll(ContainsFilterBuilder.contains(request.getDefinition(), BCCP.DEFINITION));
        }
        if (!StringUtils.isEmpty(request.getModule())) {
            conditions.add(concat(MODULE_DIR.PATH, inline(MODULE_SEPARATOR), MODULE.NAME).containsIgnoreCase(request.getModule()));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(BCCP.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime()).toLocalDateTime()));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(BCCP.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime()).toLocalDateTime()));
        }

        if (request.getFindUsages() != null) {
            CcId findUsages = request.getFindUsages();
            Set<ULong> usages = new HashSet();
            switch (CcType.valueOf(findUsages.getType())) {
                case ACC:
                    usages.addAll(
                            dslContext.selectDistinct(BCC_MANIFEST.TO_BCCP_MANIFEST_ID)
                                    .from(BCC_MANIFEST)
                                    .join(ACC_MANIFEST).on(BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ACC_MANIFEST.ACC_MANIFEST_ID))
                                    .where(and(
                                            BCC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())),
                                            BCC_MANIFEST.FROM_ACC_MANIFEST_ID.eq(ULong.valueOf(findUsages.getManifestId()))
                                    ))
                                    .fetchInto(ULong.class)
                    );
                    break;
            }
            if (!usages.isEmpty()) {
                conditions.add(ACC_MANIFEST.ACC_MANIFEST_ID.in(usages));
            }
        }

        return dslContext.select(
                BCCP_MANIFEST.BCCP_MANIFEST_ID,
                BCCP.BCCP_ID,
                BCCP.GUID,
                BCCP.DEN,
                BCCP.DEFINITION,
                BCCP.DEFINITION_SOURCE,
                MODULE.NAME,
                MODULE_DIR.PATH,
                BCCP.STATE,
                BCCP.IS_DEPRECATED,
                BCCP.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM,
                RELEASE.RELEASE_NUM)
                .from(BCCP)
                .join(BCCP_MANIFEST)
                .on(BCCP.BCCP_ID.eq(BCCP_MANIFEST.BCCP_ID).and(BCCP_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(REVISION)
                .on(BCCP_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .join(RELEASE)
                .on(BCCP_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(BCCP.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(BCCP.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .leftJoin(MODULE_BCCP_MANIFEST)
                .on(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(MODULE_BCCP_MANIFEST.BCCP_MANIFEST_ID))
                .leftJoin(MODULE_SET_ASSIGNMENT)
                .on(MODULE_BCCP_MANIFEST.MODULE_SET_ASSIGNMENT_ID.eq(MODULE_SET_ASSIGNMENT.MODULE_SET_ASSIGNMENT_ID))
                .leftJoin(MODULE)
                .on(MODULE_SET_ASSIGNMENT.MODULE_ID.eq(MODULE.MODULE_ID))
                .leftJoin(MODULE_DIR)
                .on(MODULE.MODULE_DIR_ID.eq(MODULE_DIR.MODULE_DIR_ID))
                .where(conditions)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType(CcType.BCCP);
                    ccList.setManifestId(row.getValue(BCCP_MANIFEST.BCCP_MANIFEST_ID).toBigInteger());
                    ccList.setId(row.getValue(BCCP.BCCP_ID).toBigInteger());
                    ccList.setGuid(row.getValue(BCCP.GUID));
                    ccList.setDen(row.getValue(BCCP.DEN));
                    ccList.setDefinition(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(BCCP.DEFINITION)));
                    ccList.setDefinitionSource(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(BCCP.DEFINITION_SOURCE)));
                    if (!StringUtils.isEmpty(row.getValue(MODULE.NAME))) {
                        ccList.setModule(row.getValue(MODULE_DIR.PATH) + MODULE_SEPARATOR + row.getValue(MODULE.NAME));
                    }
                    ccList.setState(CcState.valueOf(row.getValue(BCCP.STATE)));
                    ccList.setDeprecated(row.getValue(BCCP.IS_DEPRECATED) == 1);
                    ccList.setLastUpdateTimestamp(Date.from(row.getValue(BCCP.LAST_UPDATE_TIMESTAMP).atZone(ZoneId.systemDefault()).toInstant()));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(REVISION.REVISION_NUM).toString());
                    ccList.setReleaseNum(row.getValue(RELEASE.RELEASE_NUM));
                    return ccList;
                });
    }

    public List<CcList> getBdtList(CcListRequest request) {
        if (!request.getTypes().isBdt()) {
            return Collections.emptyList();
        }

        Release release = releaseRepository.findById(request.getReleaseId());
        AppUser appUserOwner = APP_USER.as("owner");
        AppUser appUserUpdater = APP_USER.as("updater");

        List<Condition> conditions = new ArrayList();
        conditions.add(DT_MANIFEST.RELEASE_ID.eq(ULong.valueOf(request.getReleaseId())));
        if (request.getDeprecated() != null) {
            conditions.add(DT.IS_DEPRECATED.eq((byte) (request.getDeprecated() ? 1 : 0)));
        }
        if (request.getStates() != null) {
            conditions.add(DT.STATE.in(
                    request.getStates().stream().map(CcState::name).collect(Collectors.toList())));
        }
        if (!request.getOwnerLoginIds().isEmpty()) {
            conditions.add(appUserOwner.LOGIN_ID.in(request.getOwnerLoginIds()));
        }
        if (!request.getUpdaterLoginIds().isEmpty()) {
            conditions.add(appUserUpdater.LOGIN_ID.in(request.getUpdaterLoginIds()));
        }
        if (!request.getExcludes().isEmpty()) {
            conditions.add(DT.DT_ID.notIn(request.getExcludes()));
        }
        if (!StringUtils.isEmpty(request.getDen())) {
            conditions.addAll(ContainsFilterBuilder.contains(request.getDen(), DT.DEN));
        }
        if (!StringUtils.isEmpty(request.getDefinition())) {
            conditions.addAll(ContainsFilterBuilder.contains(request.getDefinition(), DT.DEFINITION));
        }
        if (!StringUtils.isEmpty(request.getModule())) {
            conditions.add(concat(MODULE_DIR.PATH, inline(MODULE_SEPARATOR), MODULE.NAME).containsIgnoreCase(request.getModule()));
        }
        if (request.getDtTypes().size() > 0) {
            conditions.add(DT.TYPE.in(request.getDtTypes()));
        } else {
            conditions.add(DT.TYPE.notEqual(DTType.Core.toString()));
        }
        if (request.getUpdateStartDate() != null) {
            conditions.add(DT.LAST_UPDATE_TIMESTAMP.greaterThan(new Timestamp(request.getUpdateStartDate().getTime()).toLocalDateTime()));
        }
        if (request.getUpdateEndDate() != null) {
            conditions.add(DT.LAST_UPDATE_TIMESTAMP.lessThan(new Timestamp(request.getUpdateEndDate().getTime()).toLocalDateTime()));
        }

        return dslContext.select(
                DT_MANIFEST.DT_MANIFEST_ID,
                DT.DT_ID,
                DT.GUID,
                DT.DEN,
                DT.TYPE,
                DT.DEFINITION,
                DT.DEFINITION_SOURCE,
                MODULE.NAME,
                MODULE_DIR.PATH,
                DT.STATE,
                DT.IS_DEPRECATED,
                DT.LAST_UPDATE_TIMESTAMP,
                appUserOwner.LOGIN_ID.as("owner"),
                appUserUpdater.LOGIN_ID.as("last_update_user"),
                REVISION.REVISION_NUM,
                REVISION.REVISION_TRACKING_NUM,
                RELEASE.RELEASE_NUM)
                .from(DT)
                .join(DT_MANIFEST)
                .on(DT.DT_ID.eq(DT_MANIFEST.DT_ID).and(DT_MANIFEST.RELEASE_ID.eq(ULong.valueOf(release.getReleaseId()))))
                .join(REVISION)
                .on(DT_MANIFEST.REVISION_ID.eq(REVISION.REVISION_ID))
                .join(RELEASE)
                .on(DT_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(appUserOwner)
                .on(DT.OWNER_USER_ID.eq(appUserOwner.APP_USER_ID))
                .join(appUserUpdater)
                .on(DT.LAST_UPDATED_BY.eq(appUserUpdater.APP_USER_ID))
                .leftJoin(MODULE_DT_MANIFEST)
                .on(DT_MANIFEST.DT_MANIFEST_ID.eq(MODULE_DT_MANIFEST.DT_MANIFEST_ID))
                .leftJoin(MODULE_SET_ASSIGNMENT)
                .on(MODULE_DT_MANIFEST.MODULE_SET_ASSIGNMENT_ID.eq(MODULE_SET_ASSIGNMENT.MODULE_SET_ASSIGNMENT_ID))
                .leftJoin(MODULE)
                .on(MODULE_SET_ASSIGNMENT.MODULE_ID.eq(MODULE.MODULE_ID))
                .leftJoin(MODULE_DIR)
                .on(MODULE.MODULE_DIR_ID.eq(MODULE_DIR.MODULE_DIR_ID))
                .where(conditions)
                .fetch().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType(CcType.BDT);
                    ccList.setManifestId(row.getValue(DT_MANIFEST.DT_MANIFEST_ID).toBigInteger());
                    ccList.setId(row.getValue(DT.DT_ID).toBigInteger());
                    ccList.setGuid(row.getValue(DT.GUID));
                    String den = row.getValue(DT.DEN);
                    ccList.setDtType(row.getValue(DT.TYPE));
                    if (!StringUtils.isEmpty(den)) {
                        ccList.setDen(den.replaceAll("_", " ").replaceAll("  ", " "));
                    }
                    ccList.setDefinition(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(DT.DEFINITION)));
                    ccList.setDefinitionSource(org.apache.commons.lang3.StringUtils.stripToNull(row.getValue(DT.DEFINITION_SOURCE)));
                    if (!StringUtils.isEmpty(row.getValue(MODULE.NAME))) {
                        ccList.setModule(row.getValue(MODULE_DIR.PATH) + MODULE_SEPARATOR + row.getValue(MODULE.NAME));
                    }
                    ccList.setState(CcState.valueOf(row.getValue(DT.STATE)));
                    ccList.setDeprecated(row.getValue(DT.IS_DEPRECATED) == 1);
                    ccList.setLastUpdateTimestamp(Date.from(row.getValue(DT.LAST_UPDATE_TIMESTAMP).atZone(ZoneId.systemDefault()).toInstant()));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(REVISION.REVISION_NUM).toString());
                    ccList.setReleaseNum(row.getValue(RELEASE.RELEASE_NUM));
                    return ccList;
                });
    }
}
