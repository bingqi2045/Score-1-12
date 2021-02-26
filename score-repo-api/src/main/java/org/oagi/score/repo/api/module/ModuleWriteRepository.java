package org.oagi.score.repo.api.module;

import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.module.model.*;

import java.math.BigInteger;

public interface ModuleWriteRepository {

    CreateModuleResponse createModule(
            CreateModuleRequest request) throws ScoreDataAccessException;

    UpdateModuleResponse updateModule(
            UpdateModuleRequest request) throws ScoreDataAccessException;

    DeleteModuleResponse deleteModule(
            DeleteModuleRequest request) throws ScoreDataAccessException;

    CreateModuleDirResponse createModuleDir(
            CreateModuleDirRequest request) throws ScoreDataAccessException;

    UpdateModuleDirResponse updateModuleDir(
            UpdateModuleDirRequest request) throws ScoreDataAccessException;

    DeleteModuleDirResponse deleteModuleDir(
            DeleteModuleDirRequest request) throws ScoreDataAccessException;

    void copyModuleDir(CopyModuleDirRequest request) throws ScoreDataAccessException;

    void copyModule(CopyModuleRequest request) throws ScoreDataAccessException;
}
