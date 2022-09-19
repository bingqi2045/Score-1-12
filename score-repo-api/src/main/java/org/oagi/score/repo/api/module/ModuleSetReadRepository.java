package org.oagi.score.repo.api.module;

import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.module.model.Module;
import org.oagi.score.repo.api.module.model.*;

import java.util.List;

public interface ModuleSetReadRepository {

    GetModuleSetResponse getModuleSet(
            GetModuleSetRequest request) throws ScoreDataAccessException;

    GetModuleSetMetadataResponse getModuleSetMetadata(
            GetModuleSetMetadataRequest request) throws ScoreDataAccessException;

    GetModuleSetListResponse getModuleSetList(
            GetModuleSetListRequest request) throws ScoreDataAccessException;

    List<Module> getAllModules(String moduleSetId) throws  ScoreDataAccessException;

    List<Module> getToplevelModules(String moduleSetId) throws  ScoreDataAccessException;
}
