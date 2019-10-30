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
        return repository.getAccNodeByAccId(accReleaseManifestRecord);
    }

    public CcAsccpNode getAsccpNode(User user, long manifestId) {
        return repository.getAsccpNodeByAsccpManifestId(user, manifestId);
    }

    public CcBccpNode getBccpNode(User user, long manifestId) {
        return repository.getBccpNodeByBccpManifestId(manifestId);
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
    public void updateAcc(User user, CcAccNode ccAccNode) {
        repository.updateAcc(user, ccAccNode);
    }

    @Transactional
    public void updateAsccp(User user, CcAsccpNodeDetail.Asccp asccpNodeDetail, long manifestId) {
        repository.updateAsccp(user, asccpNodeDetail, manifestId);
    }

    @Transactional
    public void appendAscc(User user, long accManifestId, long asccManifestId) {
        repository.createAscc(user, accManifestId, asccManifestId);
    }

    @Transactional
    public void discardAscc(User user, long accManifestId, long asccManifestId) {
        // repository method discard specific id
    }
}

