package org.oagi.srt.gateway.http.api.cc_management.service;

import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.AccReleaseManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.AsccpReleaseManifestRecord;
import org.oagi.srt.entity.jooq.tables.records.BccpReleaseManifestRecord;
import org.oagi.srt.gateway.http.api.cc_management.data.*;
import org.oagi.srt.gateway.http.api.cc_management.data.node.*;
import org.oagi.srt.gateway.http.api.cc_management.repository.CcNodeRepository;
import org.oagi.srt.gateway.http.api.cc_management.repository.ReleaseManifestRepository;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CcNodeService {

    @Autowired
    private CcNodeRepository repository;

    @Autowired
    private ReleaseManifestRepository manifestRepository;

    @Autowired
    private SessionService sessionService;

    public CcAccNode getAccNode(User user, long manifestId) {
        AccReleaseManifestRecord accReleaseManifestRecord =
                manifestRepository.getAccReleaseManifestById(ULong.valueOf(manifestId));
        return repository.getAccNodeByAccId(user, accReleaseManifestRecord);
    }

    public CcAsccpNode getAsccpNode(User user, long manifestId) {
        return repository.getAsccpNodeByAsccpManifestId(user, manifestId);
    }

    public CcBccpNode getBccpNode(User user, long manifestId) {
        return repository.getBccpNodeByBccpManifestId(user, manifestId);
    }

    @Transactional
    public void deleteAccNode(User user, long manifestId) {
        AccReleaseManifestRecord accReleaseManifestRecord =
                manifestRepository.getAccReleaseManifestById(ULong.valueOf(manifestId));

        boolean used = repository.isAccUsed(accReleaseManifestRecord.getAccId().longValue());
        if (used) {
            throw new IllegalArgumentException("The target ACC is currently in use by another component.");
        }

        manifestRepository.deleteAccReleaseManifestById(manifestId);
        if (!manifestRepository.isAccUsed(accReleaseManifestRecord.getAccId().longValue())) {
            repository.deleteAccRecords(accReleaseManifestRecord.getAccId().longValue());
        }
    }

    @Transactional
    public void deleteAsccpNode(User user, long manifestId) {
        AsccpReleaseManifestRecord asccpReleaseManifestRecord =
                manifestRepository.getAsccpReleaseManifestById(ULong.valueOf(manifestId));

        boolean used = repository.isAsccpUsed(asccpReleaseManifestRecord.getAsccpId().longValue());
        if (used) {
            throw new IllegalArgumentException("The target ASCCP is currently in use by another component.");
        }

        manifestRepository.deleteAsccpReleaseManifestById(manifestId);
        if (!manifestRepository.isAsccpUsed(asccpReleaseManifestRecord.getAsccpId().longValue())) {
            repository.deleteAsccpRecords(asccpReleaseManifestRecord.getAsccpId().longValue());
        }
    }

    @Transactional
    public void deleteBccpNode(User user, long manifestId) {
        BccpReleaseManifestRecord bccpReleaseManifestRecord =
                manifestRepository.getBccpReleaseManifestById(ULong.valueOf(manifestId));

        boolean used = repository.isBccpUsed(bccpReleaseManifestRecord.getBccpId().longValue());
        if (used) {
            throw new IllegalArgumentException("The target BCCP is currently in use by another component.");
        }

        manifestRepository.deleteBccpReleaseManifestById(manifestId);
        if (!manifestRepository.isBccpUsed(bccpReleaseManifestRecord.getBccpId().longValue())) {
            repository.deleteBccpRecords(bccpReleaseManifestRecord.getBccpId().longValue());
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
        long userId = sessionService.userId(user);
        return repository.createAcc(userId, request.getReleaseId());
    }

    @Transactional
    public long createAsccp(User user, CcAsccpCreateRequest request) {
        long userId = sessionService.userId(user);
        return repository.createAsccp(userId, request.getRoleOfAccManifestId());
    }

    @Transactional
    public long createBccp(User user, CcBccpCreateRequest request) {
        long userId = sessionService.userId(user);
        return repository.createBccp(userId, request.getBdtManifestId());
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
        List<CcBccpNodeDetail> updatedBccpNodeDetails = new ArrayList<>();
        for (CcBccpNodeDetail detail : bccpNodeDetails) {
            CcBccpNode ccBccpNode = repository.updateBccp(user, detail.getBccp(), detail.getBccp().getManifestId());
            if (detail.getBcc() != null) {
                long bccId = repository.updateBcc(user, detail.getBcc(), detail.getBcc().getManifestId());
                ccBccpNode.setBccId(bccId);
                ccBccpNode.setBccManifestId(detail.getBcc().getManifestId());
            }
            updatedBccpNodeDetails.add(getBccpNodeDetail(user, ccBccpNode));
        }
        return updatedBccpNodeDetails;
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
    public CcAccNode updateAccState(User user, long accManifestId, String state) {
        CcState ccState = getStateCode(state);
        return repository.updateAccState(user, ULong.valueOf(accManifestId), ccState);
    }

    @Transactional
    public CcAsccpNode updateAsccpState(User user, long asccpManifestId, String state) {
        CcState ccState = getStateCode(state);
        return repository.updateAsccpState(user, asccpManifestId, ccState);
    }

    @Transactional
    public CcBccpNode updateBccpState(User user, long bccpManifestId, String state) {
        CcState ccState = getStateCode(state);
        return repository.updateBccpState(user, bccpManifestId, ccState);
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
        if (CcState.Editing.name().equals(state)) {
            return CcState.Editing;
        } else if (CcState.Candidate.name().equals(state)) {
            return CcState.Candidate;
        } else if (CcState.Published.name().equals(state)) {
            return CcState.Published;
        }
        return null;
    }
}

