package org.oagi.score.repo.api.impl.jooq.businessterm;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.types.ULong;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.businessterm.BusinessTermAssignmentWriteRepository;
import org.oagi.score.repo.api.businessterm.BusinessTermReadRepository;
import org.oagi.score.repo.api.businessterm.model.*;
import org.oagi.score.repo.api.impl.jooq.JooqScoreRepository;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.oagi.score.repo.api.security.AccessControl;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        ULong requesterUserId = ULong.valueOf(requester.getUserId());
        LocalDateTime timestamp = LocalDateTime.now();

        List<BigInteger> assignedBtIds = request.getBiesToAssign().stream().map(bieToAssign -> {
            if(bieToAssign.getBieType().equals("ASBIE")){
                BigInteger asccId = findCcIdByBie(bieToAssign);
                AsccBiztermRecord asccBiztermRecord = new AsccBiztermRecord();
                asccBiztermRecord.setAsccId(ULong.valueOf(asccId));
                asccBiztermRecord.setBusinessTermId(ULong.valueOf(request.getBusinessTermId()));
                asccBiztermRecord.setCreatedBy(requesterUserId);
                asccBiztermRecord.setLastUpdatedBy(requesterUserId);
                asccBiztermRecord.setCreationTimestamp(timestamp);
                asccBiztermRecord.setLastUpdateTimestamp(timestamp);

                ULong asccBiztermRecordId = dslContext().insertInto(ASCC_BIZTERM)
                        .set(asccBiztermRecord)
                        .returning(ASCC_BIZTERM.ASCC_BIZTERM_ID)
                        .fetchOne().getAsccBiztermId();

                AsbieBiztermRecord asbieBiztermRecord = new AsbieBiztermRecord();
                asbieBiztermRecord.setAsbieId(ULong.valueOf(bieToAssign.getBieId()));
                asbieBiztermRecord.setAsccBiztermId(asccBiztermRecordId);
                asbieBiztermRecord.setPrimaryIndicator(request.isPrimary() ? "1" : "0");
                asbieBiztermRecord.setTypeCode(request.getTypeCode());
                asbieBiztermRecord.setCreatedBy(requesterUserId);
                asbieBiztermRecord.setLastUpdatedBy(requesterUserId);
                asbieBiztermRecord.setCreationTimestamp(timestamp);
                asbieBiztermRecord.setLastUpdateTimestamp(timestamp);
                BigInteger asbieBiztermRecordId = dslContext().insertInto(ASBIE_BIZTERM)
                        .set(asbieBiztermRecord)
                        .returning(ASBIE_BIZTERM.ASBIE_BIZTERM_ID)
                        .fetchOne().getAsbieBiztermId().toBigInteger();
                return asbieBiztermRecordId;
            }
            else if (bieToAssign.getBieType().equals("BBIE")){
                BigInteger bccId = findCcIdByBie(bieToAssign);
                BccBiztermRecord bccBiztermRecord = new BccBiztermRecord();
                bccBiztermRecord.setBccId(ULong.valueOf(bccId));
                bccBiztermRecord.setBusinessTermId(ULong.valueOf(request.getBusinessTermId()));
                bccBiztermRecord.setCreatedBy(requesterUserId);
                bccBiztermRecord.setLastUpdatedBy(requesterUserId);
                bccBiztermRecord.setCreationTimestamp(timestamp);
                bccBiztermRecord.setLastUpdateTimestamp(timestamp);

                ULong bccBiztermRecordId = dslContext().insertInto(BCC_BIZTERM)
                        .set(bccBiztermRecord)
                        .returning(BCC_BIZTERM.BCC_BIZTERM_ID)
                        .fetchOne().getBccBiztermId();

                BbieBiztermRecord bbieBiztermRecord = new BbieBiztermRecord();
                bbieBiztermRecord.setBbieId(ULong.valueOf(bieToAssign.getBieId()));
                bbieBiztermRecord.setBccBiztermId(bccBiztermRecordId);
                bbieBiztermRecord.setPrimaryIndicator(request.isPrimary() ? "1" : "0");
                bbieBiztermRecord.setTypeCode(request.getTypeCode());
                bbieBiztermRecord.setCreatedBy(requesterUserId);
                bbieBiztermRecord.setLastUpdatedBy(requesterUserId);
                bbieBiztermRecord.setCreationTimestamp(timestamp);
                bbieBiztermRecord.setLastUpdateTimestamp(timestamp);
                BigInteger bbieBiztermRecordId = dslContext().insertInto(BBIE_BIZTERM)
                        .set(bbieBiztermRecord)
                        .returning(BBIE_BIZTERM.BBIE_BIZTERM_ID)
                        .fetchOne().getBbieBiztermId().toBigInteger();
                return bbieBiztermRecordId;
            }
            else throw new ScoreDataAccessException("Wrong BIE type");
        }).collect(Collectors.toList());

        return new AssignBusinessTermResponse(assignedBtIds);
    }

    private BigInteger findCcIdByBie(BieToAssign bieToAssign) throws ScoreDataAccessException {
        if(bieToAssign.getBieType().equals("ASBIE")){
            return dslContext()
                    .select(ASCC_MANIFEST.ASCC_ID)
                    .from(ASBIE)
                    .leftJoin(ASCC_MANIFEST)
                    .on(ASBIE.BASED_ASCC_MANIFEST_ID.eq(ASCC_MANIFEST.ASCC_MANIFEST_ID))
                    .where(ASBIE.ASBIE_ID.eq(ULong.valueOf(bieToAssign.getBieId())))
                    .fetchOneInto(BigInteger.class);
        }
        else if (bieToAssign.getBieType().equals("BBIE")){
            return dslContext()
                    .select(BCC_MANIFEST.BCC_ID)
                    .from(BBIE)
                    .leftJoin(BCC_MANIFEST)
                    .on(BBIE.BASED_BCC_MANIFEST_ID.eq(BCC_MANIFEST.BCC_MANIFEST_ID))
                    .where(BBIE.BBIE_ID.eq(ULong.valueOf(bieToAssign.getBieId())))
                    .fetchOneInto(BigInteger.class);
        }
        else throw new ScoreDataAccessException("Wrong BIE type");
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public UpdateBusinessTermResponse updateBusinessTermAssignment(
            UpdateBusinessTermRequest request) throws ScoreDataAccessException {

        ScoreUser requester = request.getRequester();
        ULong requesterUserId = ULong.valueOf(requester.getUserId());
        LocalDateTime timestamp = LocalDateTime.now();

        BusinessTermRecord record = dslContext().selectFrom(BUSINESS_TERM)
                .where(BUSINESS_TERM.BUSINESS_TERM_ID.eq(ULong.valueOf(request.getBusinessTermId())))
                .fetchOptional().orElse(null);
        if (record == null) {
            throw new ScoreDataAccessException(new IllegalArgumentException());
        }
        List<Field<?>> changedField = new ArrayList();
        if (!StringUtils.equals(request.getBusinessTerm(), record.getBusinessTerm())) {
            record.setBusinessTerm(request.getBusinessTerm());
            changedField.add(BUSINESS_TERM.BUSINESS_TERM_);
        }
        if (!StringUtils.equals(request.getDefinition(), record.getDefinition())) {
            record.setDefinition(request.getDefinition());
            changedField.add(BUSINESS_TERM.DEFINITION);
        }
        if (!StringUtils.equals(request.getExternalReferenceId(), record.getExternalRefId())) {
            record.setExternalRefId(request.getExternalReferenceId());
            changedField.add(BUSINESS_TERM.EXTERNAL_REF_ID);
        }
        if (!StringUtils.equals(request.getExternalReferenceUri(), record.getExternalRefUri())) {
            record.setExternalRefUri(request.getExternalReferenceUri());
            changedField.add(BUSINESS_TERM.EXTERNAL_REF_URI);
        }
        if (!changedField.isEmpty()) {
            record.setLastUpdatedBy(requesterUserId);
            changedField.add(BUSINESS_TERM.LAST_UPDATED_BY);

            record.setLastUpdateTimestamp(timestamp);
            changedField.add(BUSINESS_TERM.LAST_UPDATE_TIMESTAMP);

            int affectedRows = record.update(changedField);
            if (affectedRows != 1) {
                throw new ScoreDataAccessException(new IllegalStateException());
            }
        }

        return new UpdateBusinessTermResponse(
                record.getBusinessTermId().toBigInteger(),
                !changedField.isEmpty());
    }

    @Override
    @AccessControl(requiredAnyRole = {DEVELOPER, END_USER})
    public DeleteBusinessTermResponse deleteBusinessTermAssignment(
            DeleteBusinessTermRequest request) throws ScoreDataAccessException {

        List<BigInteger> businessTermIdList = request.getBusinessTermIdList();
        if (businessTermIdList == null || businessTermIdList.isEmpty()) {
            return new DeleteBusinessTermResponse(Collections.emptyList());
        }
        dslContext().delete(BUSINESS_TERM)
                .where(
                        businessTermIdList.size() == 1 ?
                                BUSINESS_TERM.BUSINESS_TERM_ID.eq(ULong.valueOf(businessTermIdList.get(0))) :
                                BUSINESS_TERM.BUSINESS_TERM_ID.in(
                                        businessTermIdList.stream().map(e -> ULong.valueOf(e)).collect(Collectors.toList())
                                )
                )
                .execute();

        DeleteBusinessTermResponse response = new DeleteBusinessTermResponse(businessTermIdList);
        return response;
    }
}
