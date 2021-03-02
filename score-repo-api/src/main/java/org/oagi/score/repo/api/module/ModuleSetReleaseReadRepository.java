package org.oagi.score.repo.api.module;

import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.module.model.*;

import java.math.BigInteger;
import java.util.List;

public interface ModuleSetReleaseReadRepository {

    GetModuleSetReleaseResponse getModuleSetRelease(
            GetModuleSetReleaseRequest request) throws ScoreDataAccessException;

    GetModuleSetReleaseListResponse getModuleSetReleaseList(
            GetModuleSetReleaseListRequest request) throws ScoreDataAccessException;

    List<AssignableNode> getAssignableACCByModuleSetReleaseId(
            GetAssignableCCListRequest request) throws ScoreDataAccessException;

    List<AssignableNode> getAssignableASCCPByModuleSetReleaseId(
            GetAssignableCCListRequest request) throws ScoreDataAccessException;

    List<AssignableNode> getAssignableBCCPByModuleSetReleaseId(
            GetAssignableCCListRequest request) throws ScoreDataAccessException;

    List<AssignableNode> getAssignableDTByModuleSetReleaseId(
            GetAssignableCCListRequest request) throws ScoreDataAccessException;

    List<AssignableNode> getAssignableCodeListByModuleSetReleaseId(
            GetAssignableCCListRequest request) throws ScoreDataAccessException;
}
