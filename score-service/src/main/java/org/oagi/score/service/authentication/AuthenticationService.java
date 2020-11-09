package org.oagi.score.service.authentication;

import org.oagi.score.repo.api.ScoreRepositoryFactory;
import org.oagi.score.repo.api.user.model.GetScoreUserRequest;
import org.oagi.score.repo.api.user.model.ScoreUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthenticationService {

    @Autowired
    private ScoreRepositoryFactory scoreRepositoryFactory;

    public ScoreUser asScoreUser(AuthenticatedPrincipal user) {
        return scoreRepositoryFactory.createScoreUserReadRepository()
                .getScoreUser(new GetScoreUserRequest().withUserName(user.getName()))
                .getUser();
    }

}
