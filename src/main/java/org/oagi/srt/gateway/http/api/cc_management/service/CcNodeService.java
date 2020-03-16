package org.oagi.srt.gateway.http.api.cc_management.service;

import org.jooq.types.ULong;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.gateway.http.api.cc_management.data.*;
import org.oagi.srt.gateway.http.api.cc_management.data.node.*;
import org.oagi.srt.gateway.http.api.cc_management.repository.CcNodeRepository;
import org.oagi.srt.gateway.http.api.cc_management.repository.ManifestRepository;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.repo.CoreComponentRepository;
import org.oagi.srt.repo.cc_arguments.InsertAccArguments;
import org.oagi.srt.repo.cc_arguments.InsertAccManifestArguments;
import org.oagi.srt.repo.cc_arguments.UpdateBccpArguments;
import org.oagi.srt.repo.cc_arguments.UpdateBccpManifestArguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public List<? extends CcNode> getDescendants(User user, CcAccNode accNode) {
        return repository.getDescendants(user, accNode);
    }

    public List<? extends CcNode> getDescendants(User user, CcAsccpNode asccpNode) {
        return repository.getDescendants(user, asccpNode);
    }

    public List<? extends CcNode> getDescendants(User user, CcBccpNode bccpNode) {
        return repository.getDescendants(user, bccpNode);
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

        InsertAccArguments accArguments = ccRepository.insertAccArguments()
                .setGuid(SrtGuid.randomGuid())
                .setObjectClassTerm(defaultObjectClassTerm)
                .setDen(defaultObjectClassTerm + ". Details")
                .setOagisComponentType(OagisComponentType.Semantics)
                .setState(CcState.WIP)
                .setRevisionNum(1)
                .setRevisionTrackingNum(1)
                .setRevisionAction(RevisionAction.Insert)
                .setCreatedBy(userId)
                .setLastUpdatedBy(userId)
                .setOwnerUserId(userId)
                .setCreationTimestamp(timestamp)
                .setLastUpdateTimestamp(timestamp);
        ULong accId = ccRepository.execute(accArguments);

        InsertAccManifestArguments accManifestArguments = ccRepository.insertAccManifestArguments()
                .setAccId(accId)
                .setReleaseId(ULong.valueOf(request.getReleaseId()));
        return ccRepository.execute(accManifestArguments).longValue();
    }

    @Transactional
    public long createAsccp(User user, CcAsccpCreateRequest request) {
        long userId = sessionService.userId(user);
        return repository.createAsccp(userId, request.getRoleOfAccManifestId());
    }

    @Transactional
    public long createBccp(User user, CcBccpCreateRequest request) {
        ULong userId = ULong.valueOf(sessionService.userId(user));
        LocalDateTime timestamp = LocalDateTime.now();
        String defaultPropertyTerm = "Property Term";

        DtManifestRecord bdtManifest = ccRepository.getBdtManifestByManifestId(
                ULong.valueOf(request.getBdtManifestId()));
        DtRecord bdt = ccRepository.getBdtById(bdtManifest.getDtId());

        ULong bccpId = ccRepository.insertBccp()
                .setGuid(SrtGuid.randomGuid())
                .setPropertyTerm(defaultPropertyTerm)
                .setRepresentationTerm(bdt.getDataTypeTerm())
                .setBdtId(bdt.getDtId())
                .setDen(defaultPropertyTerm + ". " + bdt.getDataTypeTerm())
                .setState(CcState.WIP)
                .setDeprecated(false)
                .setNillable(false)
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
                updateAcc(user, ccUpdateRequest.getAccNodeDetails()));
        ccUpdateResponse.setAsccpNodeResults(
                updateAsccp(user, ccUpdateRequest.getAsccpNodeDetails()));
        ccUpdateResponse.setBccpNodeResults(
                updateBccp(user, ccUpdateRequest.getBccpNodeDetails()));

        return ccUpdateResponse;
    }

    @Transactional
    public List<CcAccNodeDetail> updateAcc(User user, List<CcAccNodeDetail> ccAccNodeDetails) {
        List<CcAccNodeDetail> updatedAccNodeDetails = new ArrayList<>();
        for (CcAccNodeDetail detail : ccAccNodeDetails) {
            CcAccNodeDetail updatedAccNodeDetail =
                    repository.updateAcc(user, detail);
            updatedAccNodeDetails.add(updatedAccNodeDetail);
        }
        return updatedAccNodeDetails;
    }

    @Transactional
    public List<CcAsccpNodeDetail> updateAsccp(User user, List<CcAsccpNodeDetail> asccpNodeDetails) {
        List<CcAsccpNodeDetail> updatedAsccpNodeDetails = new ArrayList<>();
        for (CcAsccpNodeDetail detail : asccpNodeDetails) {
            CcAsccpNode ccAsccpNode = repository.updateAsccp(user, detail.getAsccp(), detail.getAsccp().getManifestId());
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
    public List<CcBccpNodeDetail> updateBccp(User user, List<CcBccpNodeDetail> bccpNodeDetails) {
        LocalDateTime timestamp = LocalDateTime.now();

        List<CcBccpNodeDetail> updatedBccpNodeDetails = new ArrayList<>();
        for (CcBccpNodeDetail detail : bccpNodeDetails) {
            CcBccpNode ccBccpNode = updateBccp(user, timestamp, detail.getBccp());

            if (detail.getBcc() != null) {
                long bccId = repository.updateBcc(user, detail.getBcc(), detail.getBcc().getManifestId());
                ccBccpNode.setBccId(bccId);
                ccBccpNode.setBccManifestId(detail.getBcc().getManifestId());
            }

            updatedBccpNodeDetails.add(getBccpNodeDetail(user, ccBccpNode));
        }
        return updatedBccpNodeDetails;
    }

    private CcBccpNode updateBccp(User user, LocalDateTime timestamp, CcBccpNodeDetail.Bccp detail) {
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

        if (!Objects.equals(bccpRecord.getDefaultValue(), detail.getDefaultValue())) {
            updateBccpArguments.setDefaultValue(detail.getDefaultValue());
            updateBccpArguments.setFixedValue(null);
        }

        if (!Objects.equals(bccpRecord.getFixedValue(), detail.getFixedValue())) {
            updateBccpArguments.setFixedValue(detail.getFixedValue());
            updateBccpArguments.setDefaultValue(null);
        }

        if (!Objects.equals(bccpRecord.getDefinition(), detail.getDefinition())) {
            updateBccpArguments.setDefinition(detail.getDefinition());
        }

        if (!Objects.equals(bccpRecord.getDefinitionSource(), detail.getDefinitionSource())) {
            updateBccpArguments.setDefinitionSource(detail.getDefinitionSource());
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

    @Transactional
    public void appendAsccp(User user, long accManifestId, long asccpManifestId) {
        repository.appendAsccp(user, ULong.valueOf(accManifestId), ULong.valueOf(asccpManifestId));
    }

    @Transactional
    public void appendBccp(User user, long accManifestId, long bccpManifestId) {
        repository.appendBccp(user, ULong.valueOf(accManifestId), ULong.valueOf(bccpManifestId));
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
        return repository.updateAccState(user, ULong.valueOf(accManifestId), ccState);
    }

    @Transactional
    public CcAsccpNode updateAsccpState(User user, long asccpManifestId, String state) {
        CcState ccState = getStateCode(state);
        return repository.updateAsccpState(user, ULong.valueOf(asccpManifestId), ccState);
    }

    @Transactional
    public CcBccpNode updateBccpState(User user, long bccpManifestId, String state) {
        CcState ccState = getStateCode(state);
        return repository.updateBccpState(user, ULong.valueOf(bccpManifestId), ccState);
    }

    @Transactional
    public CcAccNode makeNewRevisionForAcc(User user, long accManifestId) {
        return repository.makeNewRevisionForAcc(user, ULong.valueOf(accManifestId));
    }

    @Transactional
    public CcAsccpNode makeNewRevisionForAsccp(User user, long asccpManifestId) {
        return repository.makeNewRevisionForAsccp(user, ULong.valueOf(asccpManifestId));
    }

    @Transactional
    public CcBccpNode makeNewRevisionForBccp(User user, long bccpManifestId) {
        return repository.makeNewRevisionForBccp(user, ULong.valueOf(bccpManifestId));
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
        repository.discardAsccById(user, asccManifestId);
    }

    @Transactional
    public void discardBcc(User user, long bccManifestId) {
        repository.discardBccById(user, bccManifestId);
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
}

