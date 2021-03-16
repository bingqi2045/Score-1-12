package org.oagi.score.repo.api.module;

import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.module.model.*;
import org.oagi.score.repo.api.module.model.Module;

import java.math.BigInteger;
import java.util.List;

public interface ModuleReadRepository {

    GetModuleListResponse getModuleList(
            GetModuleListRequest request) throws ScoreDataAccessException;

}
