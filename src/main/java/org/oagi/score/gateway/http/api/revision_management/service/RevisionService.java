package org.oagi.score.gateway.http.api.revision_management.service;

import org.oagi.score.gateway.http.api.common.data.PageResponse;
import org.oagi.score.gateway.http.api.revision_management.data.Revision;
import org.oagi.score.gateway.http.api.revision_management.data.RevisionListRequest;
import org.oagi.score.repo.RevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
@Transactional(readOnly = true)
public class RevisionService {

    @Autowired
    private RevisionRepository repository;

    public PageResponse<Revision> getRevisionByReference(RevisionListRequest request) {
        return repository.getRevisionByReference(request);
    }

    public String getSnapshotById(AuthenticatedPrincipal user, BigInteger revisionId) {
        return repository.getSnapshotById(user, revisionId);
    }
}
