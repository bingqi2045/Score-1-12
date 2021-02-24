package org.oagi.score.repo.api.module;

import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.module.model.*;
import org.oagi.score.repo.api.module.model.Module;

import java.math.BigInteger;
import java.util.List;

public interface ModuleReadRepository {

    GetModuleResponse getModule(
            GetModuleRequest request) throws ScoreDataAccessException;

    GetModuleListResponse getModuleList(
            GetModuleListRequest request) throws ScoreDataAccessException;

    List<Module> getAllModules(GetModuleListRequest request) throws ScoreDataAccessException;

    List<ModuleDir> getAllModuleDirs(GetModuleListRequest request) throws ScoreDataAccessException;

    GetModuleElementResponse getModuleElements(
            GetModuleElementRequest request) throws ScoreDataAccessException;

}
