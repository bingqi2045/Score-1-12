package org.oagi.srt.gateway.http.api.revision_management.service;

import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.api.revision_management.data.Revision;
import org.oagi.srt.gateway.http.api.revision_management.data.RevisionListRequest;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.repo.RevisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RevisionService {

    @Autowired
    private RevisionRepository repository;

    public PageResponse<Revision> getRevisionByReference(RevisionListRequest request) {
        return repository.getRevisionByReference(request);
    }
}
