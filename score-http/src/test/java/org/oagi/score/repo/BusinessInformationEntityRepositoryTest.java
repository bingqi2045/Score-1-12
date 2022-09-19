package org.oagi.score.repo;

import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.oagi.score.gateway.http.ScoreHttpApplication;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AbieRecord;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AsbiepRecord;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.TopLevelAsbiepRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;

@SpringBootTest(
        classes = ScoreHttpApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
public class BusinessInformationEntityRepositoryTest {

    @Autowired
    private BusinessInformationEntityRepository bieRepository;

    @Autowired
    private DSLContext dslContext;

    /*
     * prerequisites
     */
    private String userId = "c720c6cf-43ef-44f6-8552-fab526c572c2";
    private String releaseId = "4dfea06e-9ee1-435e-a025-aad37e164ae0";
    private LocalDateTime timestamp = LocalDateTime.now();

    @Test
    public void insertTopLevelAsbiepTest() {
        String topLevelAsbiepId = insertTopLevelAsbiep();

        TopLevelAsbiepRecord topLevelAsbiep = dslContext.selectFrom(TOP_LEVEL_ASBIEP)
                .where(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.eq(topLevelAsbiepId))
                .fetchOptional().orElse(null);

        assertNotNull(topLevelAsbiep);
        assertEquals(userId, topLevelAsbiep.getOwnerUserId());
        assertEquals(timestamp, topLevelAsbiep.getLastUpdateTimestamp());
        assertEquals(releaseId, topLevelAsbiep.getReleaseId());
    }

    private String insertTopLevelAsbiep() {
        return bieRepository.insertTopLevelAsbiep()
                .setUserId(userId)
                .setReleaseId(releaseId)
                .setTimestamp(timestamp)
                .execute();
    }

    @Test
    public void insertAbieTest() {
        // tested by #insertTopLevelAsbiepTest
        String topLevelAsbiepId = insertTopLevelAsbiep();

        String roleOfAccManifestId = "7155a590-01f0-47de-b680-453481a64350";
        String abieId = insertAbie(topLevelAsbiepId, roleOfAccManifestId);

        AbieRecord abie = dslContext.selectFrom(ABIE)
                .where(ABIE.ABIE_ID.eq(abieId))
                .fetchOptional().orElse(null);

        assertNotNull(abie);
        assertEquals(userId, abie.getCreatedBy());
        assertEquals(timestamp, abie.getCreationTimestamp());
        assertEquals(topLevelAsbiepId, abie.getOwnerTopLevelAsbiepId());
        assertEquals(roleOfAccManifestId, abie.getBasedAccManifestId());
    }

    private String insertAbie(String topLevelAsbiepId, String roleOfAccManifestId) {
        return bieRepository.insertAbie()
                .setUserId(userId)
                .setTopLevelAsbiepId(topLevelAsbiepId)
                .setAccManifestId(roleOfAccManifestId)
                .setTimestamp(timestamp)
                .execute();
    }

    private String insertAbie(String topLevelAsbiepId) {
        return insertAbie(topLevelAsbiepId, "7155a590-01f0-47de-b680-453481a64350");
    }

    @Test
    public void insertAsbiepTest() {
        // tested by #insertTopLevelAsbiepTest
        String topLevelAsbiepId = insertTopLevelAsbiep();
        // tested by #insertAbieTest
        String abieId = insertAbie(topLevelAsbiepId);

        String asccpManifestId = "1086bd04-d289-4aef-8e00-8fa9fe04f412";
        String asbiepId = bieRepository.insertAsbiep()
                .setAsccpManifestId(asccpManifestId)
                .setRoleOfAbieId(abieId)
                .setTopLevelAsbiepId(topLevelAsbiepId)
                .setUserId(userId)
                .setTimestamp(timestamp)
                .execute();

        AsbiepRecord asbiep = dslContext.selectFrom(ASBIEP)
                .where(ASBIEP.ASBIEP_ID.eq(asbiepId))
                .fetchOptional().orElse(null);

        assertNotNull(asbiep);
        assertEquals(userId, asbiep.getCreatedBy());
        assertEquals(timestamp, asbiep.getCreationTimestamp());
        assertEquals(topLevelAsbiepId, asbiep.getOwnerTopLevelAsbiepId());
        assertEquals(abieId, asbiep.getRoleOfAbieId());
        assertEquals(asccpManifestId, asbiep.getBasedAsccpManifestId());
    }
}
