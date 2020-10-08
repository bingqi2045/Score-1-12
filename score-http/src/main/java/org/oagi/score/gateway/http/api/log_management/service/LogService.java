package org.oagi.score.gateway.http.api.log_management.service;

import org.oagi.score.gateway.http.api.common.data.PageResponse;
import org.oagi.score.gateway.http.api.log_management.data.Log;
import org.oagi.score.gateway.http.api.log_management.data.LogListRequest;
import org.oagi.score.repo.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
@Transactional(readOnly = true)
public class LogService {

    @Autowired
    private LogRepository repository;

    public PageResponse<Log> getLogByReference(LogListRequest request) {
        return repository.getLogByReference(request);
    }

    public String getSnapshotById(AuthenticatedPrincipal user, BigInteger logId) {
        return repository.getSnapshotById(user, logId);
    }
}
