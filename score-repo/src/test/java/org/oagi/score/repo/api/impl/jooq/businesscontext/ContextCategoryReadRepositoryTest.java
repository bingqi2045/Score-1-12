package org.oagi.score.repo.api.impl.jooq.businesscontext;

import org.junit.jupiter.api.*;
import org.oagi.score.repo.api.base.ScoreUser;
import org.oagi.score.repo.api.businesscontext.ContextCategoryReadRepository;
import org.oagi.score.repo.api.businesscontext.model.GetContextCategoryRequest;
import org.oagi.score.repo.api.businesscontext.model.GetContextCategoryResponse;
import org.oagi.score.repo.api.businesscontext.model.ListContextCategoryRequest;
import org.oagi.score.repo.api.businesscontext.model.ListContextCategoryResponse;
import org.oagi.score.repo.api.impl.jooq.AbstractJooqScoreRepositoryTest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.CtxCategoryRecord;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.oagi.score.repo.api.base.ScoreRole.DEVELOPER;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ContextCategoryReadRepositoryTest
        extends AbstractJooqScoreRepositoryTest {

    private ContextCategoryReadRepository repository;
    private ScoreUser requester;
    private CtxCategoryRecord record;

    @BeforeAll
    void setUp() {
        repository = scoreRepositoryFactory().createContextCategoryReadRepository();
        requester = new ScoreUser(BigInteger.ONE, "oagis", DEVELOPER);
    }

    @Test
    @Order(1)
    public void getContextCategoryTestWithoutParameter() {
        GetContextCategoryRequest request = new GetContextCategoryRequest(requester);
        GetContextCategoryResponse response = repository.getContextCategory(request);
        assertNull(response.getContextCategory());
    }

    @Test
    @Order(2)
    public void listContextCategoriesTestWithoutData() {
        ContextCategoryReadRepository repository =
                scoreRepositoryFactory().createContextCategoryReadRepository();
        ListContextCategoryRequest request = new ListContextCategoryRequest(requester);
        ListContextCategoryResponse response = repository.listContextCategories(request);
        assertNotNull(response);
        assertTrue(response.getResults().isEmpty());
    }

}
