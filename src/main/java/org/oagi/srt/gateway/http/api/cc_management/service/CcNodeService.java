package org.oagi.srt.gateway.http.api.cc_management.service;

import org.jooq.types.ULong;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.data.Release;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.gateway.http.api.cc_management.data.*;
import org.oagi.srt.gateway.http.api.cc_management.data.node.*;
import org.oagi.srt.gateway.http.api.cc_management.repository.CcNodeRepository;
import org.oagi.srt.gateway.http.api.cc_management.repository.ManifestRepository;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.repo.CoreComponentRepository;
import org.oagi.srt.repo.cc_arguments.*;
import org.oagi.srt.repository.ReleaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.oagi.srt.data.BCCEntityType.Element;
import static org.oagi.srt.gateway.http.api.cc_management.data.CcType.*;

@Service
@Transactional(readOnly = true)
public class CcNodeService {

    @Autowired
    private CcNodeRepository repository;

    @Autowired
    private CoreComponentRepository ccRepository;

    @Autowired
    private ManifestRepository manifestRepository;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ReleaseRepository releaseRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public CcAccNode getAccNode(User user, long manifestId) {
        AccManifestRecord accManifestRecord =
                manifestRepository.getAccManifestById(ULong.valueOf(manifestId));
        return repository.getAccNodeByAccManifestId(user, accManifestRecord.getAccManifestId());
    }

    public CcAsccpNode getAsccpNode(User user, long manifestId) {
        return repository.getAsccpNodeByAsccpManifestId(user, manifestId);
    }

    public CcBccpNode getBccpNode(User user, long manifestId) {
        return repository.getBccpNodeByBccpManifestId(user, manifestId);
    }

    @Transactional
    public void deleteAccNode(User user, long manifestId) {
        AccManifestRecord accManifestRecord =
                manifestRepository.getAccManifestById(ULong.valueOf(manifestId));

        boolean used = repository.isAccManifestUsed(accManifestRecord.getAccManifestId().longValue());
        if (used) {
            throw new IllegalArgumentException("The target ACC is currently in use by another component.");
        }

        manifestRepository.deleteAccManifestById(manifestId);
        if (!manifestRepository.isAccUsed(accManifestRecord.getAccId().longValue())) {
            repository.deleteAccRecords(accManifestRecord);
        }
    }

    @Transactional
    public void deleteAsccpNode(User user, long manifestId) {
        AsccpManifestRecord asccpManifestRecord =
                manifestRepository.getAsccpManifestById(ULong.valueOf(manifestId));

        boolean used = repository.isAsccpManifestUsed(asccpManifestRecord.getAsccpManifestId().longValue());
        if (used) {
            throw new IllegalArgumentException("The target ASCCP is currently in use by another component.");
        }

        manifestRepository.deleteAsccpManifestById(manifestId);
        if (!manifestRepository.isAsccpUsed(asccpManifestRecord.getAsccpId().longValue())) {
            repository.deleteAsccpRecords(asccpManifestRecord.getAsccpId().longValue());
        }
    }

    @Transactional
    public void deleteBccpNode(User user, long manifestId) {
        BccpManifestRecord bccpManifestRecord =
                manifestRepository.getBccpManifestById(ULong.valueOf(manifestId));

        boolean used = repository.isBccpManifestUsed(bccpManifestRecord.getBccpManifestId().longValue());
        if (used) {
            throw new IllegalArgumentException("The target BCCP is currently in use by another component.");
        }

        manifestRepository.deleteBccpManifestById(manifestId);
        if (!manifestRepository.isBccpUsed(bccpManifestRecord.getBccpId().longValue())) {
            repository.deleteBccpRecords(bccpManifestRecord.getBccpId().longValue());
        }
    }

    public CcAccNodeDetail getAccNodeDetail(User user, CcAccNode accNode) {
        return repository.getAccNodeDetail(user, accNode);
    }

    public CcAsccpNodeDetail getAsccpNodeDetail(User user, CcAsccpNode asccpNode) {
        return repository.getAsccpNodeDetail(user, asccpNode);
    }

    public CcBccpNodeDetail getBccpNodeDetail(User user, CcBccpNode bccpNode) {
        return repository.getBccpNodeDetail(user, bccpNode);
    }

    public CcBdtScNodeDetail getBdtScNodeDetail(User user, CcBdtScNode bdtScNode) {
        return repository.getBdtScNodeDetail(user, bdtScNode);
    }

    public CcAsccpNodeDetail.Asccp getAsccp(long asccpId) {
        return repository.getAsccp(asccpId);
    }

    @Transactional
    public long createAcc(User user, CcAccCreateRequest request) {
        ULong userId = ULong.valueOf(sessionService.userId(user));
        LocalDateTime timestamp = LocalDateTime.now();
        String defaultObjectClassTerm = "Object Class Term";

        ULong accId = ccRepository.insertAccArguments()
                .setGuid(SrtGuid.randomGuid())
                .setObjectClassTerm(defaultObjectClassTerm)
                .setDen(defaultObjectClassTerm + ". Details")
                .setOagisComponentType(OagisComponentType.Semantics)
                .setState(CcState.WIP)
                .setRevisionNum(1)
                .setRevisionTrackingNum(1)
                .setRevisionAction(RevisionAction.Insert)
                .setDeprecated(false)
                .setAbstract(false)
                .setCreatedBy(userId)
                .setLastUpdatedBy(userId)
                .setOwnerUserId(userId)
                .setCreationTimestamp(timestamp)
                .setLastUpdateTimestamp(timestamp)
                .execute();

        InsertAccManifestArguments accManifestArguments = ccRepository.insertAccManifestArguments()
                .setAccId(accId)
                .setReleaseId(ULong.valueOf(request.getReleaseId()));
        return ccRepository.execute(accManifestArguments).longValue();
    }

    @Transactional
    public long createAsccp(User user, CcAsccpCreateRequest request) {
        long userId = sessionService.userId(user);
        LocalDateTime timestamp = LocalDateTime.now();
        String defaultPropertyTerm = "Property Term";

        AccManifestRecord accManifestRecord =
                ccRepository.getAccManifestByManifestId(ULong.valueOf(request.getRoleOfAccManifestId()));
        AccRecord accRecord = ccRepository.getAccById(accManifestRecord.getAccId());

        InsertAsccpArguments insertAsccpArguments = ccRepository.insertAsccpArguments()
                .setGuid(SrtGuid.randomGuid())
                .setPropertyTerm(defaultPropertyTerm)
                .setRoleOfAccId(accManifestRecord.getAccId())
                .setDen(defaultPropertyTerm + ". " + accRecord.getObjectClassTerm())
                .setState(CcState.WIP)
                .setReuseableIndicator(true)
                .setDeprecated(false)
                .setNillable(false)
                .setNamespaceId(null)
                .setRevisionNum(1)
                .setRevisionTrackingNum(1)
                .setRevisionAction(RevisionAction.Insert)
                .setCreatedBy(ULong.valueOf(userId))
                .setLastUpdatedBy(ULong.valueOf(userId))
                .setOwnerUserId(ULong.valueOf(userId))
                .setCreationTimestamp(timestamp)
                .setLastUpdateTimestamp(timestamp);

        ULong asccpId = ccRepository.execute(insertAsccpArguments);

        InsertAsccpManifestArguments insertAsccpManifestArguments = ccRepository.insertAsccpManifest()
                .setAsccpId(asccpId)
                .setRoleOfAccManifestId(accManifestRecord.getAccManifestId())
                .setReleaseId(accManifestRecord.getReleaseId());

        return ccRepository.execute(insertAsccpManifestArguments).longValue();
    }

    @Transactional
    public long createBccp(User user, CcBccpCreateRequest request) {
        ULong userId = ULong.valueOf(sessionService.userId(user));
        LocalDateTime timestamp = LocalDateTime.now();
        String defaultPropertyTerm = "Property Term";

        DtManifestRecord bdtManifest = ccRepository.getBdtManifestByManifestId(
                ULong.valueOf(request.getBdtManifestId()));
        DtRecord bdt = ccRepository.getBdtById(bdtManifest.getDtId());

        ULong bccpId = ccRepository.insertBccpArguments()
                .setGuid(SrtGuid.randomGuid())
                .setPropertyTerm(defaultPropertyTerm)
                .setRepresentationTerm(bdt.getDataTypeTerm())
                .setBdtId(bdt.getDtId())
                .setDen(defaultPropertyTerm + ". " + bdt.getDataTypeTerm())
                .setState(CcState.WIP)
                .setDeprecated(false)
                .setNillable(false)
                .setNamespaceId(null)
                .setRevisionNum(1)
                .setRevisionTrackingNum(1)
                .setRevisionAction(RevisionAction.Insert)
                .setCreatedBy(userId)
                .setLastUpdatedBy(userId)
                .setOwnerUserId(userId)
                .setCreationTimestamp(timestamp)
                .setLastUpdateTimestamp(timestamp)
                .execute();

        ULong bccpManifestId = ccRepository.insertBccpManifest()
                .setBccpId(bccpId)
                .setBdtManifestId(bdtManifest.getDtManifestId())
                .setReleaseId(ULong.valueOf(request.getReleaseId()))
                .execute();

        return bccpManifestId.longValue();
    }

    @Transactional
    public CcUpdateResponse updateCcDetails(User user, CcUpdateRequest ccUpdateRequest) {
        CcUpdateResponse ccUpdateResponse = new CcUpdateResponse();

        ccUpdateResponse.setAccNodeResults(
                updateAccDetail(user, ccUpdateRequest.getAccNodeDetails()));
        ccUpdateResponse.setAsccpNodeResults(
                updateAsccp(user, ccUpdateRequest.getAsccpNodeDetails()));
        ccUpdateResponse.setBccpNodeResults(
                updateBccpDetail(user, ccUpdateRequest.getBccpNodeDetails()));

        return ccUpdateResponse;
    }

    @Transactional
    public List<CcAccNodeDetail> updateAccDetail(User user, List<CcAccNodeDetail> ccAccNodeDetails) {
        LocalDateTime timestamp = LocalDateTime.now();
        List<CcAccNodeDetail> updatedAccNodeDetails = new ArrayList<>();

        for (CcAccNodeDetail detail : ccAccNodeDetails) {
            CcAccNodeDetail updatedAccNodeDetail = updateAccDetail(user, timestamp, detail);
            updatedAccNodeDetails.add(updatedAccNodeDetail);

            CcEvent event = new CcEvent();
            event.setAction("UpdateDetail");
            event.addProperty("actor", user.getUsername());
            simpMessagingTemplate.convertAndSend("/topic/acc/" + detail.getManifestId(), event);
        }

        return updatedAccNodeDetails;
    }

    @Transactional
    public List<CcAsccpNodeDetail> updateAsccp(User user, List<CcAsccpNodeDetail> asccpNodeDetails) {
        LocalDateTime timestamp = LocalDateTime.now();
        List<CcAsccpNodeDetail> updatedAsccpNodeDetails = new ArrayList<>();
        for (CcAsccpNodeDetail detail : asccpNodeDetails) {
            CcAsccpNode ccAsccpNode = updateAsccpDetail(user, timestamp, detail.getAsccp());
            if (detail.getAscc() != null) {
                long accId = repository.updateAscc(user, detail.getAscc(), detail.getAscc().getManifestId());
                ccAsccpNode.setAsccId(accId);
                ccAsccpNode.setAsccManifestId(detail.getAscc().getManifestId());
            }
            updatedAsccpNodeDetails.add(getAsccpNodeDetail(user, ccAsccpNode));
        }
        return updatedAsccpNodeDetails;
    }

    @Transactional
    public List<CcBccpNodeDetail> updateBccpDetail(User user, List<CcBccpNodeDetail> bccpNodeDetails) {
        LocalDateTime timestamp = LocalDateTime.now();

        List<CcBccpNodeDetail> updatedBccpNodeDetails = new ArrayList<>();
        for (CcBccpNodeDetail detail : bccpNodeDetails) {
            CcBccpNode ccBccpNode = updateBccpDetail(user, timestamp, detail.getBccp());

            if (detail.getBcc() != null) {
                long bccId = repository.updateBcc(user, detail.getBcc(), detail.getBcc().getManifestId());
                ccBccpNode.setBccId(bccId);
                ccBccpNode.setBccManifestId(detail.getBcc().getManifestId());
            }

            updatedBccpNodeDetails.add(getBccpNodeDetail(user, ccBccpNode));
        }
        return updatedBccpNodeDetails;
    }

    private CcAccNodeDetail updateAccDetail(User user, LocalDateTime timestamp, CcAccNodeDetail detail) {
        ULong userId = ULong.valueOf(sessionService.userId(user));

        AccManifestRecord accManifestRecord = ccRepository.getAccManifestByManifestId(
                ULong.valueOf(detail.getManifestId()));
        AccRecord accRecord = ccRepository.getAccById(accManifestRecord.getAccId());

        UpdateAccArguments updateAccArguments = ccRepository.updateAccArguments(accRecord);

        if (!accRecord.getObjectClassTerm().equals(detail.getObjectClassTerm())) {
            updateAccArguments.setObjectClassTerm(detail.getObjectClassTerm());
            updateAccArguments.setDen(detail.getObjectClassTerm() + ". Details");
        }

        byte abstracted = (byte) (detail.isAbstracted() ? 1 : 0);
        if (!accRecord.getIsAbstract().equals(abstracted)) {
            updateAccArguments.setAbstract(detail.isAbstracted());
        }

        byte deprecated = (byte) (detail.isDeprecated() ? 1 : 0);
        if (!accRecord.getIsDeprecated().equals(deprecated)) {
            updateAccArguments.setDeprecated(detail.isDeprecated());
        }

        if (!Objects.equals(accRecord.getDefinition(), detail.getDefinition())) {
            updateAccArguments.setDefinition(detail.getDefinition());
        }

        if (!Objects.equals(accRecord.getDefinitionSource(), detail.getDefinitionSource())) {
            updateAccArguments.setDefinitionSource(detail.getDefinitionSource());
        }

        if (!Objects.equals(accRecord.getOagisComponentType(), detail.getOagisComponentType())) {
            updateAccArguments.setOagisComponentType(OagisComponentType.valueOf((int) detail.getOagisComponentType()));
        }

        if (!Objects.equals(accRecord.getNamespaceId(), detail.getNamespaceId())) {
            if (detail.getNamespaceId() == 0) {
                updateAccArguments.setNamespaceId(null);
            } else {
                updateAccArguments.setNamespaceId(ULong.valueOf(detail.getNamespaceId()));
            }
        }

        ULong accId = updateAccArguments.setLastUpdatedBy(userId)
                .setLastUpdateTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Update)
                .setRevisionTrackingNum(accRecord.getRevisionTrackingNum() + 1)
                .setPrevAccId(accRecord.getAccId())
                .execute();

        ccRepository.updateAccManifestArguments(accManifestRecord)
                .setAccId(accId)
                .execute();

        updateAccChain(userId, accManifestRecord.getAccManifestId(), timestamp);

        CcAccNode updateAccNode = repository.getAccNodeByAccManifestId(user, accManifestRecord.getAccManifestId());
        return repository.getAccNodeDetail(user, updateAccNode);
    }

    private CcBccpNode updateBccpDetail(User user, LocalDateTime timestamp, CcBccpNodeDetail.Bccp detail) {
        ULong userId = ULong.valueOf(sessionService.userId(user));

        BccpManifestRecord bccpManifestRecord = ccRepository.getBccpManifestByManifestId(
                ULong.valueOf(detail.getManifestId()));
        BccpRecord bccpRecord = ccRepository.getBccpById(bccpManifestRecord.getBccpId());

        UpdateBccpArguments updateBccpArguments = ccRepository.updateBccpArguments(bccpRecord);

        if (!bccpRecord.getPropertyTerm().equals(detail.getPropertyTerm())) {
            DtManifestRecord bdtManifest = ccRepository.getBdtManifestByManifestId(
                    bccpManifestRecord.getBdtManifestId());
            DtRecord bdt = ccRepository.getBdtById(bdtManifest.getDtId());

            updateBccpArguments.setPropertyTerm(detail.getPropertyTerm());
            updateBccpArguments.setRepresentationTerm(bdt.getDataTypeTerm());
        }

        byte nillable = (byte) (detail.isNillable() ? 1 : 0);
        if (!bccpRecord.getIsNillable().equals(nillable)) {
            updateBccpArguments.setNillable(detail.isNillable());
        }

        byte deprecated = (byte) (detail.isDeprecated() ? 1 : 0);
        if (!bccpRecord.getIsDeprecated().equals(deprecated)) {
            updateBccpArguments.setDeprecated(detail.isDeprecated());
        }

        if (detail.getDefaultValue() != null && detail.getDefaultValue().length() > 0) {
            updateBccpArguments.setDefaultValue(detail.getDefaultValue());
            updateBccpArguments.setFixedValue(null);
        }
        else if (detail.getFixedValue() != null && detail.getFixedValue().length() > 0) {
            updateBccpArguments.setFixedValue(detail.getFixedValue());
            updateBccpArguments.setDefaultValue(null);
        } else {
            updateBccpArguments.setFixedValue(null);
            updateBccpArguments.setDefaultValue(null);
        }

        if (!Objects.equals(bccpRecord.getDefinition(), detail.getDefinition())) {
            updateBccpArguments.setDefinition(detail.getDefinition());
        }

        if (!Objects.equals(bccpRecord.getDefinitionSource(), detail.getDefinitionSource())) {
            updateBccpArguments.setDefinitionSource(detail.getDefinitionSource());
        }

        if (detail.getNamespaceId() > 0L) {
            updateBccpArguments.setNamespaceId(ULong.valueOf(detail.getNamespaceId()));
        } else {
            updateBccpArguments.setNamespaceId(null);
        }

        updateBccpArguments.setLastUpdatedBy(userId)
                .setLastUpdateTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Update)
                .setRevisionTrackingNum(bccpRecord.getRevisionTrackingNum() + 1)
                .setPrevBccpId(bccpRecord.getBccpId());
        ULong bccpId = ccRepository.execute(updateBccpArguments);

        repository.updateBccByToBccp(userId.longValue(), bccpManifestRecord.getBccpId().longValue(),
                bccpId.longValue(), bccpManifestRecord.getReleaseId(),
                bccpRecord.getPropertyTerm(), timestamp);

        UpdateBccpManifestArguments updateBccpManifestArguments =
                ccRepository.updateBccpManifestArguments(bccpManifestRecord);
        updateBccpManifestArguments.setBccpId(bccpId);
        ccRepository.execute(updateBccpManifestArguments);

        return repository.getBccpNodeByBccpManifestId(user, detail.getManifestId());
    }

    public CcAsccpNode updateAsccpDetail(User user, LocalDateTime timestamp, CcAsccpNodeDetail.Asccp detail) {
        ULong userId = ULong.valueOf(sessionService.userId(user));

        AsccpManifestRecord asccpManifestRecord =
                ccRepository.getAsccpManifestByManifestId(ULong.valueOf(detail.getManifestId()));
        AsccpRecord asccpRecord = ccRepository.getAsccpById(asccpManifestRecord.getAsccpId());
        AccRecord accRecord = ccRepository.getAccById(asccpRecord.getRoleOfAccId());

        UpdateAsccpArguments updateAsccpArguments = ccRepository.updateAsccpArguments(asccpRecord);

        byte nillable = (byte) (detail.isNillable() ? 1 : 0);
        if (!asccpRecord.getIsNillable().equals(nillable)) {
            updateAsccpArguments.setNillable(detail.isNillable());
        }

        byte deprecated = (byte) (detail.isDeprecated() ? 1 : 0);
        if (!asccpRecord.getIsDeprecated().equals(deprecated)) {
            updateAsccpArguments.setDeprecated(detail.isDeprecated());
        }

        if (!Objects.equals(asccpRecord.getDefinition(), detail.getDefinition())) {
            updateAsccpArguments.setDefinition(detail.getDefinition());
        }

        if (!Objects.equals(asccpRecord.getDefinitionSource(), detail.getDefinitionSource())) {
            updateAsccpArguments.setDefinitionSource(detail.getDefinitionSource());
        }

        if (detail.getNamespaceId() > 0L) {
            updateAsccpArguments.setNamespaceId(ULong.valueOf(detail.getNamespaceId()));
        } else {
            updateAsccpArguments.setNamespaceId(null);
        }
        updateAsccpArguments
            .setPropertyTerm(detail.getPropertyTerm())
            .setDen(detail.getPropertyTerm() + ". " + accRecord.getObjectClassTerm())
            .setReuseableIndicator(detail.isReusable())
            .setLastUpdatedBy(userId)
            .setLastUpdateTimestamp(timestamp)
            .setRevisionAction(RevisionAction.Update)
            .setRevisionTrackingNum(asccpRecord.getRevisionTrackingNum() + 1)
            .setPrevAsccpId(asccpRecord.getAsccpId());

        ULong asccpId = ccRepository.execute(updateAsccpArguments);

        if (!asccpId.equals(asccpRecord.getAsccpId())) {
            UpdateAsccpManifestArguments updateAsccpManifestArguments
                    = ccRepository.updateAsccpManifestArguments(asccpManifestRecord)
                    .setAsccpId(asccpId);
            ccRepository.execute(updateAsccpManifestArguments);
            repository.updateAsccByToAsccp(userId.longValue(), asccpRecord.getAsccpId().longValue(),
                    asccpId.longValue(), asccpManifestRecord.getReleaseId(), detail.getPropertyTerm(), timestamp);
        }

        return repository.getAsccpNodeByAsccpManifestId(user, asccpManifestRecord.getAsccpManifestId().longValue());
    }


    @Transactional
    public long appendAsccp(User user, long accManifestId, long asccpManifestId) {
        long userId = sessionService.userId(user);
        LocalDateTime timestamp = LocalDateTime.now();

        AccManifestRecord accManifest = ccRepository.getAccManifestByManifestId(ULong.valueOf(accManifestId));
        AsccpManifestRecord asccpManifestRecord
                = ccRepository.getAsccpManifestByManifestId(ULong.valueOf(asccpManifestId));

        repository.duplicateAssociationValidate(user, accManifest.getAccManifestId(),
                asccpManifestRecord.getAsccpManifestId(), null);

        AsccpRecord asccpRecord = ccRepository.getAsccpById(asccpManifestRecord.getAsccpId());
        AccRecord accRecord = ccRepository.getAccById(accManifest.getAccId());

        int seqKey = repository.getNextSeqKey(accManifest.getAccId().longValue(), accManifest.getReleaseId().longValue());

        ULong asccId = ccRepository.insertAsccArguments()
                .setGuid(SrtGuid.randomGuid())
                .setCardinalityMin(0)
                .setCardinalityMax(-1)
                .setSeqKey(seqKey)
                .setDeprecated(false)
                .setFromAccId(accRecord.getAccId())
                .setToAsccpId(asccpManifestRecord.getAsccpId())
                .setDen(accRecord.getObjectClassTerm() + ". " + asccpRecord.getPropertyTerm())
                .setCreatedBy(ULong.valueOf(userId))
                .setCreationTimestamp(timestamp)
                .setLastUpdatedBy(ULong.valueOf(userId))
                .setLastUpdateTimestamp(timestamp)
                .setOwnerUserId(ULong.valueOf(userId))
                .setState(CcState.valueOf(accRecord.getState()))
                .setRevisionNum(1)
                .setRevisionTrackingNum(1)
                .setRevisionAction(RevisionAction.Insert)
                .execute();

        return ccRepository.insertAsccManifestArguments()
                .setReleaseId(accManifest.getReleaseId())
                .setAsccId(asccId)
                .setFromAccManifestId(accManifest.getAccManifestId())
                .setToAsccpManifestId(asccpManifestRecord.getAsccpManifestId())
                .execute().longValue();
    }

    @Transactional
    public long appendBccp(User user, long accManifestId, long bccpManifestId) {
        long userId = sessionService.userId(user);
        LocalDateTime timestamp = LocalDateTime.now();

        AccManifestRecord accManifest = ccRepository.getAccManifestByManifestId(ULong.valueOf(accManifestId));
        BccpManifestRecord bccpManifestRecord
                = ccRepository.getBccpManifestByManifestId(ULong.valueOf(bccpManifestId));

        repository.duplicateAssociationValidate(user, accManifest.getAccManifestId(),
                null, bccpManifestRecord.getBccpManifestId());

        BccpRecord bccpRecord = ccRepository.getBccpById(bccpManifestRecord.getBccpId());
        AccRecord accRecord = ccRepository.getAccById(accManifest.getAccId());

        int seqKey = repository.getNextSeqKey(accManifest.getAccId().longValue(), accManifest.getReleaseId().longValue());

        ULong bccId = ccRepository.insertBccArguments()
                .setGuid(SrtGuid.randomGuid())
                .setCardinalityMin(0)
                .setCardinalityMax(-1)
                .setSeqKey(seqKey)
                .setDeprecated(false)
                .setNillable(false)
                .setEntityType(Element)
                .setFromAccId(accRecord.getAccId())
                .setToBccpId(bccpManifestRecord.getBccpId())
                .setDen(accRecord.getObjectClassTerm() + ". " + bccpRecord.getPropertyTerm())
                .setCreatedBy(ULong.valueOf(userId))
                .setCreationTimestamp(timestamp)
                .setLastUpdatedBy(ULong.valueOf(userId))
                .setLastUpdateTimestamp(timestamp)
                .setOwnerUserId(ULong.valueOf(userId))
                .setState(CcState.valueOf(accRecord.getState()))
                .setRevisionNum(1)
                .setRevisionTrackingNum(1)
                .setRevisionAction(RevisionAction.Insert)
                .execute();

        return ccRepository.insertBccManifestArguments()
                .setReleaseId(accManifest.getReleaseId())
                .setBccId(bccId)
                .setFromAccManifestId(accManifest.getAccManifestId())
                .setToBccpManifestId(bccpManifestRecord.getBccpManifestId())
                .execute().longValue();
    }

    @Transactional
    public CcNode updateAsccpRoleOfAcc(User user, long asccpManifestId, long accManifestId) {
        return repository.updateAsccpRoleOfAcc(user, ULong.valueOf(asccpManifestId), ULong.valueOf(accManifestId));
    }

    @Transactional
    public CcNode updateBccpBdt(User user, long bccpManifestId, long bdtManifestId) {
        return repository.updateBccpBdt(user, bccpManifestId, bdtManifestId);
    }

    @Transactional
    public void updateCcStateDelete(User user, List<Long> accManifestIdList, List<Long> asccpManifestIdList,
                                    List<Long> bccpManifestIdList) {
        CcState ccState = CcState.Deleted;
        if (accManifestIdList != null) {
            for (Long accManifestId: accManifestIdList) {
                repository.updateAccState(user, ULong.valueOf(accManifestId), ccState);
            }
        }
        if (asccpManifestIdList != null) {
            for (Long asccpManifestId: asccpManifestIdList) {
                repository.updateAsccpState(user, ULong.valueOf(asccpManifestId), ccState);
            }
        }
        if (bccpManifestIdList != null) {
            for (Long bccpManifestId: bccpManifestIdList) {
                repository.updateBccpState(user, ULong.valueOf(bccpManifestId), ccState);
            }
        }
    }

    @Transactional
    public CcAccNode updateAccState(User user, long accManifestId, String state) {
        CcState ccState = getStateCode(state);
        CcAccNode resp = repository.updateAccState(user, ULong.valueOf(accManifestId), ccState);

        CcEvent event = new CcEvent();
        event.setAction("ChangeState");
        event.addProperty("State", state);
        event.addProperty("actor", user.getUsername());
        simpMessagingTemplate.convertAndSend("/topic/acc/" + accManifestId, event);

        return resp;
    }

    @Transactional
    public CcAsccpNode updateAsccpState(User user, long asccpManifestId, String state) {
        CcState ccState = getStateCode(state);
        CcAsccpNode resp = repository.updateAsccpState(user, ULong.valueOf(asccpManifestId), ccState);

        CcEvent event = new CcEvent();
        event.setAction("Update");
        event.addProperty("State", state);
        simpMessagingTemplate.convertAndSend("/topic/asccp/" + asccpManifestId, event);

        return resp;
    }

    @Transactional
    public CcBccpNode updateBccpState(User user, long bccpManifestId, String state) {
        CcState ccState = getStateCode(state);
        CcBccpNode resp = repository.updateBccpState(user, ULong.valueOf(bccpManifestId), ccState);

        CcEvent event = new CcEvent();
        event.setAction("Update");
        event.addProperty("State", state);
        simpMessagingTemplate.convertAndSend("/topic/bccp/" + bccpManifestId, event);

        return resp;
    }

    @Transactional
    public CcAccNode makeNewRevisionForAcc(User user, long accManifestId) {
        LocalDateTime timestamp = LocalDateTime.now();
        ULong userId = ULong.valueOf(sessionService.userId(user));

        AccManifestRecord accManifestRecord = ccRepository.getAccManifestByManifestId(ULong.valueOf(accManifestId));
        AccRecord accRecord = ccRepository.getAccById(accManifestRecord.getAccId());
        if (CcState.valueOf(accRecord.getState()) != CcState.Published) {
            throw new IllegalArgumentException("Creating new revision only allowed for the component in 'Published' state.");
        }

        Release workingRelease = releaseRepository.getWorkingRelease();

        if (accManifestRecord.getReleaseId().longValue() != workingRelease.getReleaseId()) {
            throw new IllegalArgumentException("Creating new revision is not allow for this release");
        }

        ULong accId = ccRepository.updateAccArguments(accRecord)
                .setLastUpdatedBy(userId)
                .setLastUpdateTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Insert)
                .setRevisionNum(accRecord.getRevisionNum() + 1)
                .setRevisionTrackingNum(1)
                .setPrevAccId(accRecord.getAccId())
                .setState(CcState.WIP)
                .execute();

        ccRepository.updateAccManifestArguments(accManifestRecord)
                .setAccId(accId)
                .execute();

        AccRecord updatedAccRecord = ccRepository.getAccById(accId);
        accManifestRecord.setAccId(accId);

        updateAsccByFromAcc(userId, accManifestRecord, updatedAccRecord, timestamp);
        updateBccByFromAcc(userId, accManifestRecord, updatedAccRecord, timestamp);

        return repository.getAccNodeByAccManifestId(user, accManifestRecord.getAccManifestId());
    }

    @Transactional
    public CcAsccpNode makeNewRevisionForAsccp(User user, long asccpManifestId) {
        LocalDateTime timestamp = LocalDateTime.now();
        ULong userId = ULong.valueOf(sessionService.userId(user));

        AsccpManifestRecord asccpManifestRecord =
                ccRepository.getAsccpManifestByManifestId(ULong.valueOf(asccpManifestId));
        AsccpRecord asccpRecord = ccRepository.getAsccpById(asccpManifestRecord.getAsccpId());

        if (CcState.valueOf(asccpRecord.getState()) != CcState.Published) {
            throw new IllegalArgumentException("Creating new revision only allowed for the component in 'Published' state.");
        }

        Release workingRelease = releaseRepository.getWorkingRelease();

        if (asccpManifestRecord.getReleaseId().longValue() != workingRelease.getReleaseId()) {
            throw new IllegalArgumentException("Creating new revision is not allow for this release");
        }

        ULong asccpId = ccRepository.updateAsccpArguments(asccpRecord)
                .setLastUpdatedBy(userId)
                .setLastUpdateTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Insert)
                .setRevisionNum(asccpRecord.getRevisionNum() + 1)
                .setRevisionTrackingNum(1)
                .setState(CcState.WIP)
                .setPrevAsccpId(asccpRecord.getAsccpId())
                .execute();

        ccRepository.updateAsccpManifestArguments(asccpManifestRecord)
                .setAsccpId(asccpId)
                .execute();

        return repository.getAsccpNodeByAsccpManifestId(user, asccpManifestRecord.getAsccpManifestId().longValue());
    }

    @Transactional
    public CcBccpNode makeNewRevisionForBccp(User user, long bccpManifestId) {
        LocalDateTime timestamp = LocalDateTime.now();
        ULong userId = ULong.valueOf(sessionService.userId(user));

        BccpManifestRecord bccpManifestRecord =
                ccRepository.getBccpManifestByManifestId(ULong.valueOf(bccpManifestId));
        BccpRecord bccpRecord = ccRepository.getBccpById(bccpManifestRecord.getBccpId());
        if (CcState.valueOf(bccpRecord.getState()) != CcState.Published) {
            throw new IllegalArgumentException("Creating new revision only allowed for the component in 'Published' state.");
        }

        ULong bccpId = ccRepository.updateBccpArguments(bccpRecord)
                .setLastUpdatedBy(userId)
                .setLastUpdateTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Insert)
                .setRevisionNum(bccpRecord.getRevisionNum() + 1)
                .setRevisionTrackingNum(1)
                .setState(CcState.WIP)
                .setPrevBccpId(bccpRecord.getBccpId())
                .execute();

        Release workingRelease = releaseRepository.getWorkingRelease();
        ULong workingReleaseId = ULong.valueOf(workingRelease.getReleaseId());

        if (bccpManifestRecord.getReleaseId().equals(workingReleaseId)) {
            ccRepository.updateBccpManifestArguments(bccpManifestRecord)
                    .setBccpId(bccpId)
                    .execute();
        } else {
            DtManifestRecord dtManifestRecord = ccRepository.getBdtManifestByManifestId(bccpManifestRecord.getBdtManifestId());
            DtManifestRecord dtWorkingManifestRecord = ccRepository.getBdtManifestByBdtId(dtManifestRecord.getDtId(), workingReleaseId);
            bccpManifestId = ccRepository.insertBccpManifest()
                    .setBdtManifestId(dtWorkingManifestRecord.getDtManifestId())
                    .setBccpId(bccpId)
                    .setReleaseId(workingReleaseId)
                    .execute().longValue();

        }
        return getBccpNode(user, bccpManifestId);
    }

    @Transactional
    public CcAccNode updateAccBasedId(User user, long accManifestId, long basedAccManifestId) {
        if (accManifestId == basedAccManifestId) {
            throw new IllegalArgumentException("Cannot choose itself as a based ACC.");
        }
        return repository.updateAccBasedId(user, ULong.valueOf(accManifestId), ULong.valueOf(basedAccManifestId));
    }

    @Transactional
    public CcAccNode discardAccBasedId(User user, long accManifestId) {
        return repository.discardAccBasedId(user, ULong.valueOf(accManifestId));
    }

    @Transactional
    public void discardAscc(User user, long asccManifestId) {
        long userId = sessionService.userId(user);
        LocalDateTime timestamp = LocalDateTime.now();
        AsccManifestRecord asccManifestRecord = ccRepository.getAsccManifestByManifestId(ULong.valueOf(asccManifestId));
        AsccRecord asccRecord = ccRepository.getAsccById(asccManifestRecord.getAsccId());

        ULong asccId = ccRepository.updateAsccArguments(asccRecord)
                .setLastUpdateTimestamp(timestamp)
                .setLastUpdatedBy(ULong.valueOf(userId))
                .setRevisionAction(RevisionAction.Delete)
                .setRevisionTrackingNum(asccRecord.getRevisionTrackingNum() + 1)
                .execute();
        
        ccRepository.updateAsccManifestArguments(asccManifestRecord)
                .setAsccId(asccId)
                .execute();
        
        decreaseSeqKeyGreaterThan(userId, asccManifestRecord.getFromAccManifestId(), asccRecord.getSeqKey(), timestamp);
    }

    @Transactional
    public void discardBcc(User user, long bccManifestId) {
        long userId = sessionService.userId(user);
        LocalDateTime timestamp = LocalDateTime.now();
        BccManifestRecord bccManifestRecord = ccRepository.getBccManifestByManifestId(ULong.valueOf(bccManifestId));
        BccRecord bccRecord = ccRepository.getBccById(bccManifestRecord.getBccId());

        ULong bccId = ccRepository.updateBccArguments(bccRecord)
                .setLastUpdateTimestamp(timestamp)
                .setLastUpdatedBy(ULong.valueOf(userId))
                .setRevisionAction(RevisionAction.Delete)
                .setRevisionTrackingNum(bccRecord.getRevisionTrackingNum() + 1)
                .execute();

        ccRepository.updateBccManifestArguments(bccManifestRecord)
                .setBccId(bccId)
                .execute();

        decreaseSeqKeyGreaterThan(userId, bccManifestRecord.getFromAccManifestId(), bccRecord.getSeqKey(), timestamp);
    }


    private CcState getStateCode(String state) {
        if (CcState.WIP.name().equals(state)) {
            return CcState.WIP;
        } else if (CcState.Draft.name().equals(state)) {
            return CcState.Draft;
        } else if (CcState.Candidate.name().equals(state)) {
            return CcState.Candidate;
        } else if (CcState.QA.name().equals(state)) {
            return CcState.QA;
        } else if (CcState.Production.name().equals(state)) {
            return CcState.Production;
        } else if (CcState.ReleaseDraft.name().equals(state)) {
            return CcState.ReleaseDraft;
        } else if (CcState.Published.name().equals(state)) {
            return CcState.Published;
        }
        return null;
    }

    private void decreaseSeqKeyGreaterThan(long userId, ULong accManifestId, int seqKey, LocalDateTime timestamp) {
        if (seqKey == 0) {
            return;
        }

        List<AsccManifestRecord> asccManifestRecords =
                ccRepository.getAsccManifestByFromAccManifestId(accManifestId);

        for (AsccManifestRecord asccManifestRecord : asccManifestRecords) {
            AsccRecord asccRecord = ccRepository.getAsccById(asccManifestRecord.getAsccId());
            if (asccRecord.getSeqKey() <= seqKey) {
                continue;
            }

            ULong asccId = ccRepository.updateAsccArguments(asccRecord)
                    .setLastUpdatedBy(ULong.valueOf(userId))
                    .setLastUpdateTimestamp(timestamp)
                    .setRevisionAction(RevisionAction.Update)
                    .setRevisionTrackingNum(asccRecord.getRevisionTrackingNum() + 1)
                    .setSeqKey(asccRecord.getSeqKey() - 1)
                    .execute();

            ccRepository.updateAsccManifestArguments(asccManifestRecord)
                    .setAsccId(asccId)
                    .execute();
        }

        List<BccManifestRecord> bccManifestRecords =
                manifestRepository.getBccManifestByFromAccManifestId(accManifestId);
        for (BccManifestRecord bccManifestRecord : bccManifestRecords) {
            BccRecord bccRecord = ccRepository.getBccById(bccManifestRecord.getBccId());
            if (bccRecord.getSeqKey() <= seqKey) {
                continue;
            }

            ULong bccId = ccRepository.updateBccArguments(bccRecord)
                    .setLastUpdatedBy(ULong.valueOf(userId))
                    .setLastUpdateTimestamp(timestamp)
                    .setRevisionAction(RevisionAction.Update)
                    .setRevisionTrackingNum(bccRecord.getRevisionTrackingNum() + 1)
                    .setSeqKey(bccRecord.getSeqKey() - 1)
                    .execute();

            ccRepository.updateBccManifestArguments(bccManifestRecord)
                    .setBccId(bccId)
                    .execute();
        }
    }
    
    public CcRevisionResponse getAccNoddRevision(User user, long manifestId) {
        CcAccNode accNode = getAccNode(user, manifestId);
        Long lastPublishedCcId = getLastPublishedCcId(accNode.getAccId(), CcType.ACC);
        CcRevisionResponse ccRevisionResponse = new CcRevisionResponse();
        if (lastPublishedCcId != null) {
            AccRecord accRecord = ccRepository.getAccById(ULong.valueOf(lastPublishedCcId));
            ccRevisionResponse.setCcId(accRecord.getAccId().longValue());
            ccRevisionResponse.setType(ACC.toString());
            ccRevisionResponse.setIsDeprecated(accRecord.getIsDeprecated() == 1);
            ccRevisionResponse.setIsAbstract(accRecord.getIsAbstract() == 1);
            ccRevisionResponse.setName(accRecord.getObjectClassTerm());
            List<AsccManifestRecord> asccManifestRecordList
                    = ccRepository.getAsccManifestByFromAccManifestId(ULong.valueOf(manifestId));
            List<String> associationKeys = new ArrayList<>();
            for (AsccManifestRecord asccManifestRecord: asccManifestRecordList) {
                Long lastAsccId = getLastPublishedCcId(asccManifestRecord.getAsccId().longValue(), CcType.ASCC);
                if (lastAsccId != null) {
                    associationKeys.add(CcType.ASCCP.toString().toLowerCase() + asccManifestRecord.getToAsccpManifestId());
                }
            }
            List<BccManifestRecord> bccManifestRecordList
                    = ccRepository.getBccManifestByFromAccManifestId(ULong.valueOf(manifestId));
            for (BccManifestRecord bccManifestRecord: bccManifestRecordList) {
                Long lastBccId = getLastPublishedCcId(bccManifestRecord.getBccId().longValue(), CcType.BCC);
                if (lastBccId != null) {
                    associationKeys.add(CcType.BCCP.toString().toLowerCase() + bccManifestRecord.getToBccpManifestId());
                }
            }
            ccRevisionResponse.setAssociationKeys(associationKeys);
        }
        return ccRevisionResponse;
    }

    public CcRevisionResponse getBccpNoddRevision(User user, long manifestId) {
        CcBccpNode bccpNode = getBccpNode(user, manifestId);
        Long lastPublishedCcId = getLastPublishedCcId(bccpNode.getBccpId(), BCCP);
        CcRevisionResponse ccRevisionResponse = new CcRevisionResponse();
        if (lastPublishedCcId != null) {
            BccpRecord bccpRecord = ccRepository.getBccpById(ULong.valueOf(lastPublishedCcId));
            ccRevisionResponse.setCcId(bccpRecord.getBccpId().longValue());
            ccRevisionResponse.setType(BCCP.toString());
            ccRevisionResponse.setIsDeprecated(bccpRecord.getIsDeprecated() == 1);
            ccRevisionResponse.setIsNillable(bccpRecord.getIsNillable() == 1);
            ccRevisionResponse.setName(bccpRecord.getPropertyTerm());
            ccRevisionResponse.setFixedValue(bccpRecord.getFixedValue());
        }
        return ccRevisionResponse;
    }

    public CcRevisionResponse getAsccpNoddRevision(User user, long manifestId) {
        CcAsccpNode asccpNode = getAsccpNode(user, manifestId);
        Long lastPublishedCcId = getLastPublishedCcId(asccpNode.getAsccpId(), ASCCP);
        CcRevisionResponse ccRevisionResponse = new CcRevisionResponse();
        if (lastPublishedCcId != null) {
            AsccpRecord asccpRecord = ccRepository.getAsccpById(ULong.valueOf(lastPublishedCcId));
            ccRevisionResponse.setCcId(asccpRecord.getAsccpId().longValue());
            ccRevisionResponse.setType(ASCCP.toString());
            ccRevisionResponse.setIsDeprecated(asccpRecord.getIsDeprecated() == 1);
            ccRevisionResponse.setIsNillable(asccpRecord.getIsNillable() == 1);
            ccRevisionResponse.setName(asccpRecord.getPropertyTerm());
        }
        return ccRevisionResponse;
    }

    private Long getLastPublishedCcId(Long ccId, CcType type) {
        if (ccId == null) {
            return null;
        }
        switch (type) {
            case ACC:
                AccRecord accRecord = ccRepository.getAccById(ULong.valueOf(ccId));
                if (accRecord.getState().equals(CcState.Published.name())) {
                    return ccId;
                }
                if (accRecord.getPrevAccId() == null) {
                    return null;
                }
                return getLastPublishedCcId(accRecord.getPrevAccId().longValue(), CcType.ACC);
            case ASCC:
                AsccRecord asccRecord = ccRepository.getAsccById(ULong.valueOf(ccId));
                if (asccRecord.getState().equals(CcState.Published.name())) {
                    return ccId;
                }
                if (asccRecord.getPrevAsccId() == null) {
                    return null;
                }
                return getLastPublishedCcId(asccRecord.getPrevAsccId().longValue(), CcType.ASCC);
            case BCC:
                BccRecord bccRecord = ccRepository.getBccById(ULong.valueOf(ccId));
                if (bccRecord.getState().equals(CcState.Published.name())) {
                    return ccId;
                }
                if (bccRecord.getPrevBccId() == null) {
                    return null;
                }
                return getLastPublishedCcId(bccRecord.getPrevBccId().longValue(), CcType.BCC);
            case ASCCP:
                AsccpRecord asccpRecord = ccRepository.getAsccpById(ULong.valueOf(ccId));
                if (asccpRecord.getState().equals(CcState.Published.name())) {
                    return ccId;
                }
                if (asccpRecord.getPrevAsccpId() == null) {
                    return null;
                }
                return getLastPublishedCcId(asccpRecord.getPrevAsccpId().longValue(), CcType.ASCCP);
            case BCCP:
                BccpRecord bccpRecord = ccRepository.getBccpById(ULong.valueOf(ccId));
                if (bccpRecord.getState().equals(CcState.Published.name())) {
                    return ccId;
                }
                if (bccpRecord.getPrevBccpId() == null) {
                    return null;
                }
                return getLastPublishedCcId(bccpRecord.getPrevBccpId().longValue(),CcType. BCCP);

            case BDT:
                return null;

            case BDT_SC:
                return null;

            case XBT:
                return null;

            default:
                return null;

        }
    }

    private void updateAccChain(ULong userId, ULong accManifestId, LocalDateTime timestamp) {
        AccManifestRecord accManifestRecord = ccRepository.getAccManifestByManifestId(accManifestId);
        AccRecord accRecord = ccRepository.getAccById(accManifestRecord.getAccId());
        updateAsccByFromAcc(userId, accManifestRecord, accRecord, timestamp);
        updateBccByFromAcc(userId, accManifestRecord, accRecord, timestamp);
        updateAsccpByRoleOfAcc(userId, accManifestRecord, accRecord, timestamp);
        updateAccByBasedAcc(userId, accManifestRecord, accRecord, timestamp);
    }

    private void updateAsccByFromAcc(ULong userId, AccManifestRecord accManifest, AccRecord acc, LocalDateTime timestamp) {
        List<AsccManifestRecord> asccManifestRecordList = ccRepository.getAsccManifestByFromAccManifestId(accManifest.getAccManifestId());
        for (AsccManifestRecord asccManifest: asccManifestRecordList) {
            AsccRecord asccRecord = ccRepository.getAsccById(asccManifest.getAsccId());
            AsccpManifestRecord asccpManifestRecord = ccRepository.getAsccpManifestByManifestId(asccManifest.getToAsccpManifestId());
            AsccpRecord asccpRecord = ccRepository.getAsccpById(asccpManifestRecord.getAsccpId());
            ULong asccId = ccRepository.updateAsccArguments(asccRecord)
                    .setDen(acc.getObjectClassTerm() + ". " + asccpRecord.getPropertyTerm())
                    .setFromAccId(acc.getAccId())
                    .setLastUpdatedBy(userId)
                    .setLastUpdateTimestamp(timestamp)
                    .setState(CcState.valueOf(acc.getState()))
                    .setRevisionAction(RevisionAction.Update)
                    .setRevisionTrackingNum(asccRecord.getRevisionTrackingNum() + 1)
                    .setPrevAsccId(asccRecord.getAsccId())
                    .execute();
            ccRepository.updateAsccManifestArguments(asccManifest)
                    .setAsccId(asccId)
                    .execute();
        }
    }

    private void updateAsccByToAsccp(ULong userId, AsccpManifestRecord asccpManifest, AsccpRecord asccp, LocalDateTime timestamp) {
        List<AsccManifestRecord> asccManifestRecordList = ccRepository.getAsccManifestByToAsccpManifestId(asccpManifest.getAsccpManifestId());
        for (AsccManifestRecord asccManifest: asccManifestRecordList) {
            AsccRecord asccRecord = ccRepository.getAsccById(asccManifest.getAsccId());
            AccManifestRecord AccManifestRecord = ccRepository.getAccManifestByManifestId(asccManifest.getFromAccManifestId());
            AccRecord accRecord = ccRepository.getAccById(AccManifestRecord.getAccId());
            ULong asccId = ccRepository.updateAsccArguments(asccRecord)
                    .setDen(accRecord.getObjectClassTerm() + ". " + asccp.getPropertyTerm())
                    .setToAsccpId(asccp.getAsccpId())
                    .setLastUpdatedBy(userId)
                    .setLastUpdateTimestamp(timestamp)
                    .setState(CcState.valueOf(asccp.getState()))
                    .setRevisionAction(RevisionAction.Update)
                    .setRevisionTrackingNum(asccRecord.getRevisionTrackingNum() + 1)
                    .setPrevAsccId(asccRecord.getAsccId())
                    .execute();
            ccRepository.updateAsccManifestArguments(asccManifest)
                    .setAsccId(asccId)
                    .execute();
        }
    }

    private void updateBccByFromAcc(ULong userId, AccManifestRecord accManifest, AccRecord acc, LocalDateTime timestamp) {
        List<BccManifestRecord> bccManifestRecordList = ccRepository.getBccManifestByFromAccManifestId(accManifest.getAccManifestId());
        for (BccManifestRecord bccManifest: bccManifestRecordList) {
            BccRecord bccRecord = ccRepository.getBccById(bccManifest.getBccId());
            BccpManifestRecord bccpManifestRecord = ccRepository.getBccpManifestByManifestId(bccManifest.getToBccpManifestId());
            BccpRecord bccpRecord = ccRepository.getBccpById(bccpManifestRecord.getBccpId());
            ULong bccId = ccRepository.updateBccArguments(bccRecord)
                    .setDen(acc.getObjectClassTerm() + ". " + bccpRecord.getPropertyTerm())
                    .setFromAccId(acc.getAccId())
                    .setLastUpdatedBy(userId)
                    .setLastUpdateTimestamp(timestamp)
                    .setState(CcState.valueOf(acc.getState()))
                    .setRevisionAction(RevisionAction.Update)
                    .setRevisionTrackingNum(bccRecord.getRevisionTrackingNum() + 1)
                    .setPrevBccId(bccRecord.getBccId())
                    .execute();
            ccRepository.updateBccManifestArguments(bccManifest)
                    .setBccId(bccId)
                    .execute();
        }
    }

    private void updateBccByToBccp(ULong userId, BccpManifestRecord bccpManifest, BccpRecord bccp, LocalDateTime timestamp) {
        List<BccManifestRecord> bccManifestRecordList = ccRepository.getBccManifestByToBccpManifestId(bccpManifest.getBccpManifestId());
        for (BccManifestRecord bccManifest: bccManifestRecordList) {
            BccRecord bccRecord = ccRepository.getBccById(bccManifest.getBccId());
            AccManifestRecord AccManifestRecord = ccRepository.getAccManifestByManifestId(bccManifest.getFromAccManifestId());
            AccRecord accRecord = ccRepository.getAccById(AccManifestRecord.getAccId());
            ULong bccId = ccRepository.updateBccArguments(bccRecord)
                    .setDen(accRecord.getObjectClassTerm() + ". " + bccp.getPropertyTerm())
                    .setToBccpId(bccp.getBccpId())
                    .setLastUpdatedBy(userId)
                    .setLastUpdateTimestamp(timestamp)
                    .setState(CcState.valueOf(bccp.getState()))
                    .setRevisionAction(RevisionAction.Update)
                    .setRevisionTrackingNum(bccRecord.getRevisionTrackingNum() + 1)
                    .setPrevBccId(bccRecord.getBccId())
                    .execute();
            ccRepository.updateBccManifestArguments(bccManifest)
                    .setBccId(bccId)
                    .execute();
        }
    }

    private void updateAsccpByRoleOfAcc(ULong userId, AccManifestRecord accManifestRecord, AccRecord accRecord, LocalDateTime timestamp) {
        List<AsccpManifestRecord> asccpManifestRecordList = ccRepository.getAsccpManifestByRolOfAccManifestId(accManifestRecord.getAccManifestId());
        for (AsccpManifestRecord asccpManifest: asccpManifestRecordList) {
            AsccpRecord asccpRecord = ccRepository.getAsccpById(asccpManifest.getAsccpId());
            ULong asccpId = ccRepository.updateAsccpArguments(asccpRecord)
                    .setDen(asccpRecord.getPropertyTerm() + ". " + accRecord.getObjectClassTerm())
                    .setRoleOfAccId(accRecord.getAccId())
                    .setLastUpdatedBy(userId)
                    .setLastUpdateTimestamp(timestamp)
                    .setRevisionAction(RevisionAction.Update)
                    .setRevisionTrackingNum(asccpRecord.getRevisionTrackingNum() + 1)
                    .setPrevAsccpId(asccpRecord.getAsccpId())
                    .execute();
            ccRepository.updateAsccpManifestArguments(asccpManifest)
                    .setAsccpId(asccpId)
                    .execute();
            asccpManifest.setAsccpId(asccpId);
            AsccpRecord updatedAsccp = ccRepository.getAsccpById(asccpId);

            updateAsccByToAsccp(userId, asccpManifest, updatedAsccp, timestamp);
        }
    }

    private void updateAccByBasedAcc(ULong userId, AccManifestRecord basedAccManifestRecord, AccRecord basedAccRecord, LocalDateTime timestamp) {
        List<AccManifestRecord> accManifestRecordList = ccRepository.getAccManifestByBasedAccManifestId(basedAccManifestRecord.getAccManifestId());
        for (AccManifestRecord accManifestRecord: accManifestRecordList) {
            AccRecord accRecord = ccRepository.getAccById(accManifestRecord.getAccId());
            ULong accId = ccRepository.updateAccArguments(accRecord)
                    .setBasedAccId(basedAccRecord.getAccId())
                    .setLastUpdatedBy(userId)
                    .setLastUpdateTimestamp(timestamp)
                    .setRevisionAction(RevisionAction.Update)
                    .setRevisionTrackingNum(accRecord.getRevisionTrackingNum() + 1)
                    .setPrevAccId(accRecord.getAccId())
                    .execute();
            ccRepository.updateAccManifestArguments(accManifestRecord)
                    .setAccId(accId)
                    .execute();
            AccRecord updatedAcc = ccRepository.getAccById(accId);
            accManifestRecord.setAccId(accId);

            updateAsccByFromAcc(userId, accManifestRecord, updatedAcc, timestamp);
        }
    }

}

