package org.oagi.score.repo.api.impl.jooq.businessterm;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.businessterm.BusinessTermAssignmentWriteRepository;
import org.oagi.score.repo.api.businessterm.model.*;
import org.oagi.score.repo.api.impl.jooq.JooqScoreRepository;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.oagi.score.repo.api.security.AccessControl;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.jooq.impl.DSL.count;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;
import static org.oagi.score.repo.api.user.model.ScoreRole.DEVELOPER;
import static org.oagi.score.repo.api.user.model.ScoreRole.END_USER;

public class JooqBusinessTermAssignmentWriteRepository
        extends JooqScoreRepository
        implements BusinessTermAssignmentWriteRepository {

    public JooqBusinessTermAssignmentWriteRepository(DSLContext dslContext) {
        super(dslContext);
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public AssignBusinessTermResponse assignBusinessTerm(
            AssignBusinessTermRequest request) throws ScoreDataAccessException {

        ScoreUser requester = request.getRequester();
        String requesterUserId = requester.getUserId();
        LocalDateTime timestamp = LocalDateTime.now();

        List<String> assignedBizTermIds = request.getBiesToAssign().stream().map(bieToAssign -> {
            if (bieToAssign.getBieType().equals("ASBIE")) {
                String asccId = findCcIdByBie(bieToAssign);
                AsccBiztermRecord asccBiztermRecord = new AsccBiztermRecord();
                asccBiztermRecord.setAsccId(asccId);
                asccBiztermRecord.setBusinessTermId(request.getBusinessTermId());
                asccBiztermRecord.setCreatedBy(requesterUserId);
                asccBiztermRecord.setLastUpdatedBy(requesterUserId);
                asccBiztermRecord.setCreationTimestamp(timestamp);
                asccBiztermRecord.setLastUpdateTimestamp(timestamp);

                String asccBizTermRecordId;
                asccBizTermRecordId = getAsccBizTermRecordId(asccBiztermRecord.getBusinessTermId(), asccBiztermRecord.getAsccId());
                if (asccBizTermRecordId == null) {
                    asccBizTermRecordId = dslContext().insertInto(ASCC_BIZTERM)
                            .set(asccBiztermRecord)
                            .returning(ASCC_BIZTERM.ASCC_BIZTERM_ID)
                            .fetchOne().getAsccBiztermId();
                }

                if (request.getPrimaryIndicator().equals("1")) {
                    updateOtherBieBiztermToNotPrimary(bieToAssign.getBieId(), bieToAssign.getBieType(),
                            request.getTypeCode(), requesterUserId);
                }
                AsbieBiztermRecord asbieBiztermRecord = new AsbieBiztermRecord();
                asbieBiztermRecord.setAsbieId(bieToAssign.getBieId());
                asbieBiztermRecord.setAsccBiztermId(asccBizTermRecordId);
                asbieBiztermRecord.setPrimaryIndicator(request.getPrimaryIndicator());
                asbieBiztermRecord.setTypeCode(request.getTypeCode());
                asbieBiztermRecord.setCreatedBy(requesterUserId);
                asbieBiztermRecord.setLastUpdatedBy(requesterUserId);
                asbieBiztermRecord.setCreationTimestamp(timestamp);
                asbieBiztermRecord.setLastUpdateTimestamp(timestamp);
                String asbieBizTermRecordId = dslContext().insertInto(ASBIE_BIZTERM)
                        .set(asbieBiztermRecord)
                        .returning(ASBIE_BIZTERM.ASBIE_BIZTERM_ID)
                        .fetchOne().getAsbieBiztermId();
                return asbieBizTermRecordId;
            } else if (bieToAssign.getBieType().equals("BBIE")) {
                String bccId = findCcIdByBie(bieToAssign);
                BccBiztermRecord bccBiztermRecord = new BccBiztermRecord();
                bccBiztermRecord.setBccId(bccId);
                bccBiztermRecord.setBusinessTermId(request.getBusinessTermId());
                bccBiztermRecord.setCreatedBy(requesterUserId);
                bccBiztermRecord.setLastUpdatedBy(requesterUserId);
                bccBiztermRecord.setCreationTimestamp(timestamp);
                bccBiztermRecord.setLastUpdateTimestamp(timestamp);

                String bccBizTermRecordId;
                bccBizTermRecordId = getBccBizTermRecordId(bccBiztermRecord.getBusinessTermId(), bccBiztermRecord.getBccId());
                if (bccBizTermRecordId == null) {
                    bccBizTermRecordId = dslContext().insertInto(BCC_BIZTERM)
                            .set(bccBiztermRecord)
                            .returning(BCC_BIZTERM.BCC_BIZTERM_ID)
                            .fetchOne().getBccBiztermId();
                }

                if (request.getPrimaryIndicator().equals("1")) {
                    updateOtherBieBiztermToNotPrimary(bieToAssign.getBieId(), bieToAssign.getBieType(),
                            request.getTypeCode(), requesterUserId);
                }
                BbieBiztermRecord bbieBizTermRecord = new BbieBiztermRecord();
                bbieBizTermRecord.setBbieId(bieToAssign.getBieId());
                bbieBizTermRecord.setBccBiztermId(bccBizTermRecordId);
                bbieBizTermRecord.setPrimaryIndicator(request.getPrimaryIndicator());
                bbieBizTermRecord.setTypeCode(request.getTypeCode());
                bbieBizTermRecord.setCreatedBy(requesterUserId);
                bbieBizTermRecord.setLastUpdatedBy(requesterUserId);
                bbieBizTermRecord.setCreationTimestamp(timestamp);
                bbieBizTermRecord.setLastUpdateTimestamp(timestamp);
                String bbieBiztermRecordId = dslContext().insertInto(BBIE_BIZTERM)
                        .set(bbieBizTermRecord)
                        .returning(BBIE_BIZTERM.BBIE_BIZTERM_ID)
                        .fetchOne().getBbieBiztermId();
                return bbieBiztermRecordId;
            } else throw new ScoreDataAccessException("Wrong BIE type");
        }).collect(Collectors.toList());

        return new AssignBusinessTermResponse(assignedBizTermIds);
    }

    private String getAsccBizTermRecordId(String businessTermId, String asccId) {
        AsccBiztermRecord asccBiztermRecord = dslContext()
                .selectFrom(ASCC_BIZTERM)
                .where(and(
                        ASCC_BIZTERM.BUSINESS_TERM_ID.eq(businessTermId),
                        ASCC_BIZTERM.ASCC_ID.eq(asccId)
                ))
                .fetchOne();
        return (asccBiztermRecord == null) ? null : asccBiztermRecord.getAsccBiztermId();
    }

    private String getBccBizTermRecordId(String businessTermId, String bccId) {
        BccBiztermRecord bccBiztermRecord = dslContext()
                .selectFrom(BCC_BIZTERM)
                .where(and(
                        BCC_BIZTERM.BUSINESS_TERM_ID.eq(businessTermId),
                        BCC_BIZTERM.BCC_ID.eq(bccId)
                ))
                .fetchOne();
        return (bccBiztermRecord == null) ? null : bccBiztermRecord.getBccBiztermId();
    }

    private String findCcIdByBie(BieToAssign bieToAssign) throws ScoreDataAccessException {
        if (bieToAssign.getBieType().equals("ASBIE")) {
            return dslContext()
                    .select(ASCC_MANIFEST.ASCC_ID)
                    .from(ASBIE)
                    .leftJoin(ASCC_MANIFEST)
                    .on(ASBIE.BASED_ASCC_MANIFEST_ID.eq(ASCC_MANIFEST.ASCC_MANIFEST_ID))
                    .where(ASBIE.ASBIE_ID.eq(bieToAssign.getBieId()))
                    .fetchOneInto(String.class);
        } else if (bieToAssign.getBieType().equals("BBIE")) {
            return dslContext()
                    .select(BCC_MANIFEST.BCC_ID)
                    .from(BBIE)
                    .leftJoin(BCC_MANIFEST)
                    .on(BBIE.BASED_BCC_MANIFEST_ID.eq(BCC_MANIFEST.BCC_MANIFEST_ID))
                    .where(BBIE.BBIE_ID.eq(bieToAssign.getBieId()))
                    .fetchOneInto(String.class);
        } else throw new ScoreDataAccessException("Wrong BIE type");
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public UpdateBusinessTermAssignmentResponse updateBusinessTermAssignment(
            UpdateBusinessTermAssignmentRequest request) throws ScoreDataAccessException {

        ScoreUser requester = request.getRequester();
        String requesterUserId = requester.getUserId();
        LocalDateTime timestamp = LocalDateTime.now();

        if (request.getBieType().equals("ASBIE")) {
            AsbieBiztermRecord record = dslContext().selectFrom(ASBIE_BIZTERM)
                    .where(ASBIE_BIZTERM.ASBIE_BIZTERM_ID.eq(request.getAssignedBizTermId()))
                    .fetchOptional().orElse(null);
            if (record == null) {
                throw new ScoreDataAccessException(new IllegalArgumentException());
            }
            List<Field<?>> changedField = new ArrayList();
            if (!StringUtils.equals(request.getTypeCode(), record.getTypeCode())) {
                record.setTypeCode(request.getTypeCode());
                changedField.add(ASBIE_BIZTERM.TYPE_CODE);
            }
            if (!StringUtils.equals(request.getPrimaryIndicator(), record.getPrimaryIndicator())) {
                record.setPrimaryIndicator(request.getPrimaryIndicator());
                changedField.add(ASBIE_BIZTERM.PRIMARY_INDICATOR);
                if (request.getPrimaryIndicator().equals("1")) {
                    updateOtherBieBiztermToNotPrimary(request.getBieId(), request.getBieType(),
                            request.getTypeCode(), requesterUserId);
                }
            }
            if (!changedField.isEmpty()) {
                record.setLastUpdatedBy(requesterUserId);
                changedField.add(ASBIE_BIZTERM.LAST_UPDATED_BY);

                record.setLastUpdateTimestamp(timestamp);
                changedField.add(ASBIE_BIZTERM.LAST_UPDATE_TIMESTAMP);

                int affectedRows = record.update(changedField);
                if (affectedRows != 1) {
                    throw new ScoreDataAccessException(new IllegalStateException());
                }
            }
            return new UpdateBusinessTermAssignmentResponse(
                    record.getAsbieBiztermId(), "BBIE", !changedField.isEmpty());
        } else if (request.getBieType().equals("BBIE")) {
            BbieBiztermRecord record = dslContext().selectFrom(BBIE_BIZTERM)
                    .where(BBIE_BIZTERM.BBIE_BIZTERM_ID.eq(request.getAssignedBizTermId()))
                    .fetchOptional().orElse(null);
            if (record == null) {
                throw new ScoreDataAccessException(new IllegalArgumentException());
            }
            List<Field<?>> changedField = new ArrayList();
            if (!StringUtils.equals(request.getTypeCode(), record.getTypeCode())) {
                record.setTypeCode(request.getTypeCode());
                changedField.add(BBIE_BIZTERM.TYPE_CODE);
            }
            if (!StringUtils.equals(request.getPrimaryIndicator(), record.getPrimaryIndicator())) {
                record.setPrimaryIndicator(request.getPrimaryIndicator());
                changedField.add(BBIE_BIZTERM.PRIMARY_INDICATOR);
                if (request.getPrimaryIndicator().equals("1")) {
                    updateOtherBieBiztermToNotPrimary(request.getBieId(), request.getBieType(),
                            request.getTypeCode(), requesterUserId);
                }
            }
            if (!changedField.isEmpty()) {
                record.setLastUpdatedBy(requesterUserId);
                changedField.add(BBIE_BIZTERM.LAST_UPDATED_BY);

                record.setLastUpdateTimestamp(timestamp);
                changedField.add(BBIE_BIZTERM.LAST_UPDATE_TIMESTAMP);

                int affectedRows = record.update(changedField);
                if (affectedRows != 1) {
                    throw new ScoreDataAccessException(new IllegalStateException());
                }
            }
            return new UpdateBusinessTermAssignmentResponse(
                    record.getBbieBiztermId(), "BBIE", !changedField.isEmpty());
        } else throw new ScoreDataAccessException("Wrong BIE type");
    }

    private int updateOtherBieBiztermToNotPrimary(String bieId, String bieType, String typeCode, String requesterUserId)
            throws ScoreDataAccessException {
        LocalDateTime timestamp = LocalDateTime.now();

        if (bieType.equals("ASBIE")) {
            return dslContext().update(ASBIE_BIZTERM)
                    .set(ASBIE_BIZTERM.PRIMARY_INDICATOR, "0")
                    .set(ASBIE_BIZTERM.LAST_UPDATED_BY, requesterUserId)
                    .set(ASBIE_BIZTERM.LAST_UPDATE_TIMESTAMP, timestamp)
                    .where(and(ASBIE_BIZTERM.ASBIE_ID.eq(bieId), ASBIE_BIZTERM.TYPE_CODE.eq(typeCode)))
                    .execute();
        } else if (bieType.equals("BBIE")) {
            return dslContext().update(BBIE_BIZTERM)
                    .set(BBIE_BIZTERM.PRIMARY_INDICATOR, "0")
                    .set(BBIE_BIZTERM.LAST_UPDATED_BY, requesterUserId)
                    .set(BBIE_BIZTERM.LAST_UPDATE_TIMESTAMP, timestamp)
                    .where(and(BBIE_BIZTERM.BBIE_ID.eq(bieId), BBIE_BIZTERM.TYPE_CODE.eq(typeCode)))
                    .execute();
        } else throw new ScoreDataAccessException("Wrong BIE type");
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public DeleteAssignedBusinessTermResponse deleteBusinessTermAssignment(
            DeleteAssignedBusinessTermRequest request) throws ScoreDataAccessException {

        List<BieToAssign> assignedBizTermIdList = request.getAssignedBtList();
        if (assignedBizTermIdList == null || assignedBizTermIdList.isEmpty()) {
            return new DeleteAssignedBusinessTermResponse(Collections.emptyList());
        }

        List<String> asbieIds = assignedBizTermIdList.stream().filter(a -> a.getBieType().equals("ASBIE"))
                .map(e -> e.getBieId()).collect(Collectors.toList());
        if (!asbieIds.isEmpty()) {
            List<String> asccBizTermIds = dslContext()
                    .select(ASBIE_BIZTERM.ASCC_BIZTERM_ID)
                    .from(ASBIE_BIZTERM)
                    .where(
                            asbieIds.size() == 1 ?
                                    ASBIE_BIZTERM.ASBIE_BIZTERM_ID.eq(asbieIds.get(0)) :
                                    ASBIE_BIZTERM.ASBIE_BIZTERM_ID.in(asbieIds)
                    ).fetchInto(String.class);
            dslContext()
                    .delete(ASBIE_BIZTERM)
                    .where(
                            asbieIds.size() == 1 ?
                                    ASBIE_BIZTERM.ASBIE_BIZTERM_ID.eq(asbieIds.get(0)) :
                                    ASBIE_BIZTERM.ASBIE_BIZTERM_ID.in(asbieIds)
                    ).execute();

            deleteUnusedAsccBizTerm(asccBizTermIds);
        }

        List<String> bbieIds = assignedBizTermIdList.stream().filter(a -> a.getBieType().equals("BBIE"))
                .map(e -> e.getBieId()).collect(Collectors.toList());
        if (!bbieIds.isEmpty()) {
            List<String> bccBizTermIds = dslContext()
                    .select(BBIE_BIZTERM.BCC_BIZTERM_ID)
                    .from(BBIE_BIZTERM)
                    .where(
                            asbieIds.size() == 1 ?
                                    BBIE_BIZTERM.BBIE_BIZTERM_ID.eq(bbieIds.get(0)) :
                                    BBIE_BIZTERM.BBIE_BIZTERM_ID.in(bbieIds)
                    ).fetchInto(String.class);

            dslContext().delete(BBIE_BIZTERM)
                    .where(
                            asbieIds.size() == 1 ?
                                    BBIE_BIZTERM.BBIE_BIZTERM_ID.eq(bbieIds.get(0)) :
                                    BBIE_BIZTERM.BBIE_BIZTERM_ID.in(bbieIds)
                    )
                    .execute();

            deleteUnusedBccBizTerms(bccBizTermIds);
        }

        DeleteAssignedBusinessTermResponse response = new DeleteAssignedBusinessTermResponse(assignedBizTermIdList);
        return response;
    }

    private void deleteUnusedBccBizTerms(List<String> bccBizTermIds) {
        List<String> bccIdsToKeep = dslContext()
                .select(BBIE_BIZTERM.BCC_BIZTERM_ID)
                .from(BBIE_BIZTERM)
                .where(BBIE_BIZTERM.BCC_BIZTERM_ID.in(bccBizTermIds))
                .groupBy(BBIE_BIZTERM.BCC_BIZTERM_ID)
                .having(count(BBIE_BIZTERM.BBIE_BIZTERM_ID).gt(0))
                .fetchInto(String.class);
        bccBizTermIds.removeIf(id -> bccIdsToKeep.contains(id));
        if (bccBizTermIds.size() != 0) {
            dslContext().delete(BCC_BIZTERM)
                    .where(
                            bccBizTermIds.size() == 1 ?
                                    BCC_BIZTERM.BCC_BIZTERM_ID.eq(bccBizTermIds.get(0)) :
                                    BCC_BIZTERM.BCC_BIZTERM_ID.in(bccBizTermIds)
                    ).execute();
        }
    }

    private void deleteUnusedAsccBizTerm(List<String> asccBizTermIds) {
        List<String> asccIdsToKeep = dslContext()
                .select(ASBIE_BIZTERM.ASCC_BIZTERM_ID)
                .from(ASBIE_BIZTERM)
                .where(ASBIE_BIZTERM.ASCC_BIZTERM_ID.in(asccBizTermIds))
                .groupBy(ASBIE_BIZTERM.ASCC_BIZTERM_ID)
                .having(count(ASBIE_BIZTERM.ASBIE_BIZTERM_ID).gt(0))
                .fetchInto(String.class);
        asccBizTermIds.removeIf(id -> asccIdsToKeep.contains(id));
        if (asccBizTermIds.size() != 0) {
            dslContext().delete(ASCC_BIZTERM)
                    .where(
                            asccBizTermIds.size() == 1 ?
                                    ASCC_BIZTERM.ASCC_BIZTERM_ID.eq(asccBizTermIds.get(0)) :
                                    ASCC_BIZTERM.ASCC_BIZTERM_ID.in(asccBizTermIds)
                    ).execute();
        }
    }
}
