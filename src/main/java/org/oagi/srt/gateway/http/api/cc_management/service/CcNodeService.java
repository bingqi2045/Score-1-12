package org.oagi.srt.gateway.http.api.cc_management.service;

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
                manifestRepository.getAccReleaseManifestById(manifestId);
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
                manifestRepository.getAccReleaseManifestById(manifestId);

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
                manifestRepository.getAsccpReleaseManifestById(manifestId);

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
                manifestRepository.getBccpReleaseManifestById(manifestId);

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
            CcAsccpNodeDetail updatedAsccpNodeDetail =
                    repository.updateAsccp(user, detail.getAsccp(), detail.getAsccp().getManifestId());
            updatedAsccpNodeDetails.add(updatedAsccpNodeDetail);
        }
        return updatedAsccpNodeDetails;
    }

    @Transactional
    public List<CcBccpNodeDetail> updateBccp(User user, List<CcBccpNodeDetail> bccpNodeDetails) {
        List<CcBccpNodeDetail> updatedBccpNodeDetails = new ArrayList<>();
        for (CcBccpNodeDetail detail : bccpNodeDetails) {
            CcBccpNodeDetail updatedBccpNodeDetail =
                    repository.updateBccp(user, detail.getBccp(), detail.getBccp().getManifestId());
            updatedBccpNodeDetails.add(updatedBccpNodeDetail);
        }
        return updatedBccpNodeDetails;
    }

    @Transactional
    public void appendAscc(User user, long accManifestId, long asccManifestId) {
        repository.appendAscc(user, accManifestId, asccManifestId);
    }

    @Transactional
    public void appendBcc(User user, long accManifestId, long bccManifestId) {
        repository.appendBcc(user, accManifestId, bccManifestId);
    }

    @Transactional
    public void updateAsccpManifest(User user, long asccpManifestId, long accManifestId) {
        repository.updateAsccpManifest(user, asccpManifestId, accManifestId);
    }

    @Transactional
    public void updateBccpManifest(User user, long bccpManifestId, long bdtManifestId) {
        repository.updateBccpManifest(user, bccpManifestId, bdtManifestId);
    }

    @Transactional
    public CcAsccpNodeDetail updateAsccpState(User user, long asccpManifestId, String state) {
        CcState ccState = getStateCode(state);
        return repository.updateAsccpState(user, asccpManifestId, ccState);
    }

    @Transactional
    public CcAccNodeDetail updateAccState(User user, long accManifestId, String state) {
        CcState ccState = getStateCode(state);
        return repository.updateAccState(user, accManifestId, ccState);
    }


    @Transactional
    public CcBccpNodeDetail updateBccpState(User user, long bccpManifestId, String state) {
        CcState ccState = getStateCode(state);
        return repository.updateBccpState(user, bccpManifestId, ccState);
    }

    @Transactional
    public CcAccNode updateAccBasedId(User user, long accManifestId, Long basedAccManifestId) {
        return repository.updateAccBasedId(user, accManifestId, basedAccManifestId);
    }


    private CcState getStateCode(String state) {
        if(CcState.Editing.name().equals(state)) {
            return CcState.Editing;
        }
        else if(CcState.Candidate.name().equals(state)) {
            return CcState.Candidate;
        }
        else if(CcState.Published.name().equals(state)) {
            return CcState.Published;
        }
        return null;
    }
}

