package org.oagi.score.repo.component.bcc;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.gateway.http.api.cc_management.data.CcList;
import org.oagi.score.gateway.http.api.cc_management.data.CcRefactorRequest;
import org.oagi.score.gateway.http.api.cc_management.data.CcRefactorValidationResponse;
import org.oagi.score.gateway.http.api.cc_management.data.CcType;
import org.oagi.score.repo.api.impl.jooq.entity.Tables;
import org.oagi.score.repo.api.impl.jooq.entity.tables.Bcc;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BccManifest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AccManifestRecord;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AccRecord;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.BccManifestRecord;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.BccRecord;
import org.oagi.score.service.common.data.AppUser;
import org.oagi.score.service.common.data.CcState;
import org.oagi.score.service.common.data.OagisComponentType;
import org.oagi.score.service.corecomponent.seqkey.MoveTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.stripToNull;
import static org.jooq.impl.DSL.inline;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.BBIE;
import static org.oagi.score.repo.api.impl.jooq.entity.tables.Acc.ACC;
import static org.oagi.score.repo.api.impl.jooq.entity.tables.AccManifest.ACC_MANIFEST;
import static org.oagi.score.repo.api.impl.jooq.entity.tables.Bcc.BCC;
import static org.oagi.score.repo.api.impl.jooq.entity.tables.BccManifest.BCC_MANIFEST;

@Repository
public class BccReadRepository {

    @Autowired
    private DSLContext dslContext;

    public BccRecord getBccByManifestId(BigInteger bccManifestId) {
        return dslContext.select(BCC.fields())
                .from(BCC)
                .join(BCC_MANIFEST).on(BCC.BCC_ID.eq(BCC_MANIFEST.BCC_ID))
                .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(ULong.valueOf(bccManifestId)))
                .fetchOptionalInto(BccRecord.class).orElse(null);
    }

    public BccManifestRecord getBccManifestByManifestId(BigInteger bccManifestId) {
        return dslContext.select(BCC_MANIFEST.fields())
                .from(BCC_MANIFEST)
                .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(ULong.valueOf(bccManifestId)))
                .fetchOptionalInto(BccManifestRecord.class).orElse(null);
    }

    public CcRefactorValidationResponse validateBccRefactoring(AppUser user, BigInteger bccManifestId, BigInteger accManifestId) {

        BccManifestRecord bccManifestRecord = dslContext.selectFrom(BccManifest.BCC_MANIFEST)
                .where(BccManifest.BCC_MANIFEST.BCC_MANIFEST_ID.eq(
                        ULong.valueOf(bccManifestId)
                ))
                .fetchOne();

        BccRecord bccRecord = dslContext.selectFrom(Bcc.BCC)
                .where(Bcc.BCC.BCC_ID.eq(bccManifestRecord.getBccId()))
                .fetchOne();

        AccManifestRecord prevAccManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(bccManifestRecord.getFromAccManifestId()))
                .fetchOne();

        AccRecord prevAccRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(prevAccManifestRecord.getAccId()))
                .fetchOne();

        AccManifestRecord accManifestRecord = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(ULong.valueOf(accManifestId)))
                .fetchOne();

        AccRecord accRecord = dslContext.selectFrom(ACC)
                .where(ACC.ACC_ID.eq(accManifestRecord.getAccId()))
                .fetchOne();

        String bccpDen = dslContext.select(BCCP.DEN)
                .from(BCCP_MANIFEST)
                .join(BCCP).on(BCCP_MANIFEST.BCCP_ID.eq(BCCP.BCCP_ID))
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(bccManifestRecord.getToBccpManifestId()))
                .fetchOneInto(String.class);

        if (!CcState.WIP.equals(CcState.valueOf(accRecord.getState()))) {
            throw new IllegalArgumentException("Only the core component in 'WIP' state can be refactored.");
        }

        if (!accRecord.getOwnerUserId().equals(ULong.valueOf(user.getAppUserId()))) {
            throw new IllegalArgumentException("It only allows to modify the core component by the owner.");
        }

        int usedBieCount = dslContext.selectCount().from(BBIE)
                .where(BBIE.BASED_BCC_MANIFEST_ID.eq(bccManifestRecord.getBccManifestId())).fetchOne(0, int.class);

        if (usedBieCount > 0) {
            throw new IllegalArgumentException("This association used in " + usedBieCount + " BIE(s). Can not be refactored.");
        }

        List<BccManifestRecord> blockerList = getBlockerList(bccManifestRecord, ULong.valueOf(accManifestId));

        CcRefactorValidationResponse response = new CcRefactorValidationResponse();
        response.setType(CcType.BCC.toString());
        response.setManifestId(bccManifestId);

        List<ULong> blockerAccManifestIdList = blockerList.stream().map(BccManifestRecord::getFromAccManifestId).collect(Collectors.toList());

        List<CcList> blockerAccList = dslContext.select(
                inline("ACC").as("type"),
                Tables.ACC_MANIFEST.ACC_MANIFEST_ID.as("manifest_id"),
                Tables.ACC.ACC_ID.as("id"),
                Tables.ACC.GUID,
                Tables.ACC.DEN,
                Tables.ACC.DEFINITION,
                Tables.ACC.DEFINITION_SOURCE,
                Tables.ACC.OBJECT_CLASS_TERM.as("term"),
                Tables.ACC.OAGIS_COMPONENT_TYPE.as("oagis_component_type"),
                Tables.ACC.STATE,
                Tables.ACC.IS_DEPRECATED,
                Tables.ACC.LAST_UPDATE_TIMESTAMP,
                APP_USER.as("appUserOwner").LOGIN_ID.as("owner"),
                APP_USER.as("appUserOwner").IS_DEVELOPER.as("owned_by_developer"),
                APP_USER.as("appUserUpdater").LOGIN_ID.as("last_update_user"),
                LOG.REVISION_NUM,
                LOG.REVISION_TRACKING_NUM,
                RELEASE.RELEASE_NUM)
                .from(Tables.ACC)
                .join(Tables.ACC_MANIFEST)
                .on(Tables.ACC.ACC_ID.eq(Tables.ACC_MANIFEST.ACC_ID).and(Tables.ACC_MANIFEST.RELEASE_ID.eq(bccManifestRecord.getReleaseId())))
                .join(LOG)
                .on(Tables.ACC_MANIFEST.LOG_ID.eq(LOG.LOG_ID))
                .join(RELEASE)
                .on(Tables.ACC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(APP_USER.as("appUserOwner"))
                .on(Tables.ACC.OWNER_USER_ID.eq(APP_USER.as("appUserOwner").APP_USER_ID))
                .join(APP_USER.as("appUserUpdater"))
                .on(Tables.ACC.LAST_UPDATED_BY.eq(APP_USER.as("appUserUpdater").APP_USER_ID))
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.in(blockerAccManifestIdList))
                .fetchStream().map(row -> {
                    CcList ccList = new CcList();
                    ccList.setType(CcType.valueOf(row.getValue("type", String.class)));
                    ccList.setManifestId(row.getValue("manifest_id", ULong.class).toBigInteger());
                    ccList.setId(row.getValue("id", ULong.class).toBigInteger());
                    ccList.setGuid(row.getValue("guid", String.class));
                    ccList.setDen(row.getValue("den", String.class));
                    ccList.setDefinition(stripToNull(row.getValue("definition", String.class)));
                    ccList.setDefinitionSource(stripToNull(row.getValue("definition_source", String.class)));
                    ccList.setName(row.getValue("term", String.class));
                    Integer componentType = row.getValue("oagis_component_type", Integer.class);
                    if (componentType != null) {
                        ccList.setOagisComponentType(OagisComponentType.valueOf(componentType));
                    }
                    ccList.setState(CcState.valueOf(row.getValue("state", String.class)));
                    ccList.setDeprecated(row.getValue("is_deprecated", Byte.class) == 1);
                    ccList.setLastUpdateTimestamp(Date.from(row.getValue("last_update_timestamp", LocalDateTime.class)
                            .atZone(ZoneId.systemDefault()).toInstant()));
                    ccList.setOwner((String) row.getValue("owner"));
                    ccList.setOwnedByDeveloper(row.getValue("owned_by_developer", Byte.class) == 1);
                    ccList.setLastUpdateUser((String) row.getValue("last_update_user"));
                    ccList.setRevision(row.getValue(LOG.REVISION_NUM).toString());
                    ccList.setReleaseNum(row.getValue(RELEASE.RELEASE_NUM));
                    return ccList;
                }).collect(Collectors.toList());

        response.setBlockers(blockerAccList);


        return response;
    }

    public List<BccManifestRecord> getBlockerList(BccManifestRecord bccManifestRecord, ULong targetAccManifestId) {
        List<AccManifestRecord> accList = dslContext.selectFrom(ACC_MANIFEST)
                .where(ACC_MANIFEST.RELEASE_ID.eq(bccManifestRecord.getReleaseId())).fetch();

        Map<ULong, AccManifestRecord> accMap = accList.stream().collect(Collectors.toMap(AccManifestRecord::getAccManifestId, Function.identity()));
        Map<ULong, List<AccManifestRecord>> baseAccMap = accList.stream().filter(e -> e.getBasedAccManifestId() != null)
                .collect(Collectors.groupingBy(AccManifestRecord::getBasedAccManifestId));

        List<BccManifestRecord> bccList = dslContext.selectFrom(BccManifest.BCC_MANIFEST)
                .where(BccManifest.BCC_MANIFEST.RELEASE_ID.eq(bccManifestRecord.getReleaseId())).fetch();

        Map<ULong, List<BccManifestRecord>> fromAccBccMap = bccList.stream()
                .collect(Collectors.groupingBy(BccManifestRecord::getFromAccManifestId));

        List<ULong> accManifestList = new ArrayList<>();

        AccManifestRecord targetAccManifestRecord = accMap.get(targetAccManifestId);

        accManifestList.add(targetAccManifestId);

        while (targetAccManifestRecord.getBasedAccManifestId() != null) {
            accManifestList.add(targetAccManifestRecord.getBasedAccManifestId());
            targetAccManifestRecord = accMap.get(targetAccManifestRecord.getBasedAccManifestId());
        }

        Set<ULong> result = new HashSet<>();

        for (ULong cur : accManifestList) {
            result.addAll(getBaseAccManifestId(cur, baseAccMap));
        }

        Set<BccManifestRecord> bccResult = new HashSet<>();

        for (ULong acc : result) {
            bccResult.addAll(
                    fromAccBccMap.getOrDefault(acc, Collections.emptyList())
                            .stream()
                            .filter(bcc -> bcc.getToBccpManifestId().equals(bccManifestRecord.getToBccpManifestId())
                                    && !bcc.getBccManifestId().equals(bccManifestRecord.getBccManifestId()))
                            .collect(Collectors.toList()));
        }

        return new ArrayList<>(bccResult);
    }

    private List<ULong> getBaseAccManifestId(ULong accManifestId, Map<ULong, List<AccManifestRecord>> baseAccMap) {
        List<ULong> result = new ArrayList<>();
        result.add(accManifestId);
        if (baseAccMap.containsKey(accManifestId)) {
            baseAccMap.get(accManifestId).forEach(e -> {
                result.addAll(getBaseAccManifestId(e.getAccManifestId(), baseAccMap));
            });
        }
        return result;
    }
}
