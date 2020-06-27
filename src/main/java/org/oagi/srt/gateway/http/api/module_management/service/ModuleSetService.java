package org.oagi.srt.gateway.http.api.module_management.service;

import org.oagi.srt.data.AppUser;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.api.module_management.data.ModuleSet;
import org.oagi.srt.gateway.http.api.module_management.data.ModuleSetListRequest;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.repo.component.module.ModuleSetReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ModuleSetService {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ModuleSetReadRepository readRepository;

    public PageResponse<ModuleSet> getModuleSetList(User user, ModuleSetListRequest request) {
        AppUser requester = sessionService.getAppUser(user);
        return readRepository.fetch(requester, request);
    }
}
