package org.oagi.score.repo.api.impl.jooq.businesscontext;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.oagi.score.repo.api.businesscontext.ContextCategoryWriteRepository;
import org.oagi.score.repo.api.businesscontext.model.*;
import org.oagi.score.repo.api.impl.jooq.AbstractJooqScoreRepositoryTest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.CtxCategoryRecord;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.CTX_CATEGORY;
import static org.oagi.score.repo.api.user.model.ScoreRole.DEVELOPER;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ContextCategoryWriteRepositoryTest
        extends AbstractJooqScoreRepositoryTest {

    private ContextCategoryWriteRepository repository;
    private ScoreUser requester;
    private CtxCategoryRecord record;

    @BeforeAll
    void setUp() {
        repository = scoreRepositoryFactory().createContextCategoryWriteRepository();
        requester = new ScoreUser("c720c6cf-43ef-44f6-8552-fab526c572c2", "oagis", DEVELOPER);
    }

    @Test
    @Order(1)
    public void createContextCategoryTest() {
        CreateContextCategoryRequest request = new CreateContextCategoryRequest(requester);
        request.setName(RandomStringUtils.random(45, true, true));
        request.setDescription(RandomStringUtils.random(1000, true, true));
        LocalDateTime requestTime = LocalDateTime.now();

        CreateContextCategoryResponse response = repository.createContextCategory(request);
        assertNotNull(response);
        assertNotNull(response.getContextCategoryId());

        record = dslContext().selectFrom(CTX_CATEGORY)
                .where(CTX_CATEGORY.CTX_CATEGORY_ID.eq(response.getContextCategoryId()))
                .fetchOptional().orElse(null);

        assertNotNull(record);
        assertEquals(request.getName(), record.getName());
        assertEquals(request.getDescription(), record.getDescription());
        assertEquals(requester.getUserId(), record.getCreatedBy());
        assertEquals(requester.getUserId(), record.getLastUpdatedBy());
        assertEquals(record.getCreationTimestamp(), record.getLastUpdateTimestamp());
        assertTrue(record.getCreationTimestamp().compareTo(requestTime) > 0);
    }

    @Test
    @Order(2)
    public void updateContextCategoryTest() {
        Assumptions.assumeTrue(record != null);

        UpdateContextCategoryRequest request = new UpdateContextCategoryRequest(requester);
        request.setContextCategoryId(record.getCtxCategoryId());
        request.setName(RandomStringUtils.random(45, true, true));
        request.setDescription(RandomStringUtils.random(1000, true, true));
        LocalDateTime requestTime = LocalDateTime.now();

        UpdateContextCategoryResponse response = repository.updateContextCategory(request);
        assertNotNull(response);
        assertNotNull(response.getContextCategoryId());
        assertEquals(request.getContextCategoryId(), response.getContextCategoryId());

        if (response.isChanged()) {
            record = dslContext().selectFrom(CTX_CATEGORY)
                    .where(CTX_CATEGORY.CTX_CATEGORY_ID.eq(response.getContextCategoryId()))
                    .fetchOptional().orElse(null);

            assertNotNull(record);
            assertEquals(request.getName(), record.getName());
            assertEquals(request.getDescription(), record.getDescription());
            assertEquals(requester.getUserId(), record.getCreatedBy());
            assertEquals(requester.getUserId(), record.getLastUpdatedBy());
            assertNotEquals(record.getCreationTimestamp(), record.getLastUpdateTimestamp());
            assertTrue(record.getLastUpdateTimestamp().compareTo(requestTime) > 0);
        }
    }

    @Test
    @Order(3)
    public void deleteContextCategoryTest() {
        Assumptions.assumeTrue(record != null);

        DeleteContextCategoryRequest request = new DeleteContextCategoryRequest(requester);
        request.setContextCategoryId(record.getCtxCategoryId());

        DeleteContextCategoryResponse response = repository.deleteContextCategory(request);
        assertNotNull(response);
        assertNotNull(response.getContextCategoryIdList());
        assertTrue(response.contains(request.getContextCategoryIdList().get(0)));

        record = dslContext().selectFrom(CTX_CATEGORY)
                .where(CTX_CATEGORY.CTX_CATEGORY_ID.eq(response.getContextCategoryIdList().get(0)))
                .fetchOptional().orElse(null);

        assertNull(record);
    }

    private CreateContextCategoryResponse createContextCategory() {
        CreateContextCategoryRequest request = new CreateContextCategoryRequest(requester);
        request.setName(RandomStringUtils.random(45, true, true));
        request.setDescription(RandomStringUtils.random(1000, true, true));
        CreateContextCategoryResponse response = repository.createContextCategory(request);
        return response;
    }

    @AfterAll
    void tearDown() {
        dslContext().deleteFrom(CTX_CATEGORY).execute();
    }

}
