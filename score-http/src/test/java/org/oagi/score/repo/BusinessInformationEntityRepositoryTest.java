package org.oagi.score.repo;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oagi.score.entity.jooq.tables.records.AbieRecord;
import org.oagi.score.entity.jooq.tables.records.AsbiepRecord;
import org.oagi.score.entity.jooq.tables.records.TopLevelAsbiepRecord;
import org.oagi.score.gateway.http.ScoreHttpApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.oagi.score.entity.jooq.Tables.*;

@RunWith(SpringRunner.class)
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
    private ULong userId = ULong.valueOf(1L);
    private ULong releaseId = ULong.valueOf(1L);
    private LocalDateTime timestamp = LocalDateTime.now();

    @Test
    public void insertTopLevelAbieTest() {
        ULong topLevelAbieId = insertTopLevelAbie();

        TopLevelAsbiepRecord topLevelAsbiep = dslContext.selectFrom(TOP_LEVEL_ASBIEP)
                .where(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.eq(topLevelAbieId))
                .fetchOptional().orElse(null);

        assertNotNull(topLevelAsbiep);
        assertEquals(userId, topLevelAsbiep.getOwnerUserId());
        assertEquals(timestamp, topLevelAsbiep.getLastUpdateTimestamp());
        assertEquals(releaseId, topLevelAsbiep.getReleaseId());
    }

    private ULong insertTopLevelAbie() {
        return bieRepository.insertTopLevelAsbiep()
                .setUserId(userId)
                .setReleaseId(releaseId)
                .setTimestamp(timestamp)
                .execute();
    }

    @Test
    public void insertAbieTest() {
        // tested by #insertTopLevelAbieTest
        ULong topLevelAbieId = insertTopLevelAbie();

        ULong roleOfAccManifestId = ULong.valueOf(1L);
        ULong abieId = insertAbie(topLevelAbieId, roleOfAccManifestId);

        AbieRecord abie = dslContext.selectFrom(ABIE)
                .where(ABIE.ABIE_ID.eq(abieId))
                .fetchOptional().orElse(null);

        assertNotNull(abie);
        assertEquals(userId, abie.getCreatedBy());
        assertEquals(timestamp, abie.getCreationTimestamp());
        assertEquals(topLevelAbieId, abie.getOwnerTopLevelAsbiepId());
        assertEquals(roleOfAccManifestId, abie.getBasedAccManifestId());
    }

    private ULong insertAbie(ULong topLevelAbieId, ULong roleOfAccManifestId) {
        return bieRepository.insertAbie()
                .setUserId(userId)
                .setTopLevelAsbiepId(topLevelAbieId)
                .setAccManifestId(roleOfAccManifestId)
                .setTimestamp(timestamp)
                .execute();
    }

    private ULong insertAbie(ULong topLevelAbieId) {
        return insertAbie(topLevelAbieId, ULong.valueOf(1L));
    }

    @Test
    public void insertAsbiepTest() {
        // tested by #insertTopLevelAbieTest
        ULong topLevelAbieId = insertTopLevelAbie();
        // tested by #insertAbieTest
        ULong abieId = insertAbie(topLevelAbieId);

        ULong asccpManifestId = ULong.valueOf(1L);
        ULong asbiepId = bieRepository.insertAsbiep()
                .setAsccpManifestId(asccpManifestId)
                .setRoleOfAbieId(abieId)
                .setTopLevelAsbiepId(topLevelAbieId)
                .setUserId(userId)
                .setTimestamp(timestamp)
                .execute();

        AsbiepRecord asbiep = dslContext.selectFrom(ASBIEP)
                .where(ASBIEP.ASBIEP_ID.eq(asbiepId))
                .fetchOptional().orElse(null);

        assertNotNull(asbiep);
        assertEquals(userId, asbiep.getCreatedBy());
        assertEquals(timestamp, asbiep.getCreationTimestamp());
        assertEquals(topLevelAbieId, asbiep.getOwnerTopLevelAsbiepId());
        assertEquals(abieId, asbiep.getRoleOfAbieId());
        assertEquals(asccpManifestId, asbiep.getBasedAsccpManifestId());
    }
}
