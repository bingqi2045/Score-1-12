package org.oagi.srt.gateway.http.api.module_management.service;

import org.oagi.srt.data.AppUser;
import org.oagi.srt.gateway.http.api.DataAccessForbiddenException;
import org.oagi.srt.gateway.http.api.common.data.PageResponse;
import org.oagi.srt.gateway.http.api.module_management.data.ModuleSet;
import org.oagi.srt.gateway.http.api.module_management.data.ModuleSetListRequest;
import org.oagi.srt.gateway.http.api.module_management.data.ModuleSetModule;
import org.oagi.srt.gateway.http.api.module_management.data.ModuleSetModuleListRequest;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.repo.component.module.ModuleSetReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
@Transactional(readOnly = true)
public class ModuleSetService {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ModuleSetReadRepository readRepository;

    public PageResponse<ModuleSet> getModuleSetList(User user, ModuleSetListRequest request) {
        AppUser requester = sessionService.getAppUser(user);
        if (!requester.isDeveloper()) {
            throw new DataAccessForbiddenException(user);
        }
        return readRepository.fetch(requester, request);
    }

    public ModuleSet getModuleSet(User user, BigInteger moduleSetId) {
        AppUser requester = sessionService.getAppUser(user);
        if (!requester.isDeveloper()) {
            throw new DataAccessForbiddenException(user);
        }
        return readRepository.getModuleSet(moduleSetId);
    }

    public PageResponse<ModuleSetModule> getModuleSetModuleList(User user, ModuleSetModuleListRequest request) {
        AppUser requester = sessionService.getAppUser(user);
        if (!requester.isDeveloper()) {
            throw new DataAccessForbiddenException(user);
        }
        return readRepository.fetch(requester, request);
    }
}
