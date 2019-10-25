package org.oagi.srt.gateway.http.api.cc_management.service;

import org.jooq.Record1;
import org.jooq.types.ULong;
import org.oagi.srt.entity.jooq.tables.records.AccRecord;
import org.oagi.srt.entity.jooq.tables.records.AccReleaseManifestRecord;
import org.oagi.srt.gateway.http.api.cc_management.data.CcEditUpdateRequest;
import org.oagi.srt.gateway.http.api.cc_management.data.CcEditUpdateResponse;
import org.oagi.srt.gateway.http.api.cc_management.data.node.*;
import org.oagi.srt.gateway.http.api.cc_management.repository.CcNodeRepository;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.ACC_RELEASE_MANIFEST;

@Service
@Transactional(readOnly = true)
public class CcNodeService {

    @Autowired
    private CcNodeRepository repository;

    @Autowired
    private SessionService sessionService;

    public CcAccNode getAccNode(User user, long manifestId) {
        AccReleaseManifestRecord accReleaseManifestRecord =
                repository.accReleaseManifestRecord(manifestId);
        return repository.getAccNodeByAccId(accReleaseManifestRecord);
    }

    public CcAsccpNode getAsccpNode(User user, long manifestId) {
        return repository.getAsccpNodeByAsccpId(manifestId);
    }

    public CcBccpNode getBccpNode(User user, long manifestId) {
        return repository.getBccpNodeByBccpId(manifestId);
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
    public CcAccNode createAcc(User user) {
        long userId = sessionService.userId(user);
        AccRecord accRecord = repository.createAcc(userId);
        return getAccNode(user, accRecord.getAccId().longValue());
    }

    @Transactional
    public void updateAcc(User user, CcAccNode ccAccNode) {
        repository.updateAcc(user, ccAccNode);
    }

    @Transactional
    public void updateAsccp(User user, CcAsccpNodeDetail.Asccp asccpNodeDetail, long id) {
        repository.updateAsccp(user, asccpNodeDetail, id);
    }

    @Transactional
    public void appendAscc(User user, long accManifestId, long asccManifestId) {
        repository.createAscc(user, accManifestId, asccManifestId);
    }

    @Transactional
    public void discardAscc(User user, long accManifestId, long asccManifestId) {
        // repository method discard specific id
    }

    @Transactional
    public void createAsccp(User user, CcAsccpNode ccAsccpNode) {
        repository.createAsccp(user, ccAsccpNode);
    }

    @Transactional
    public CcEditUpdateResponse updateDetails(User user, CcEditUpdateRequest request) {
        long accId = request.getAccId();

        CcEditUpdateResponse response = new CcEditUpdateResponse();

        return response;
    }

}

