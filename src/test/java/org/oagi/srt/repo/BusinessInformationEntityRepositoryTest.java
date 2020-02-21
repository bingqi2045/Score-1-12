package org.oagi.srt.repo;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oagi.srt.entity.jooq.tables.records.AbieRecord;
import org.oagi.srt.entity.jooq.tables.records.AsbiepRecord;
import org.oagi.srt.entity.jooq.tables.records.TopLevelAbieRecord;
import org.oagi.srt.gateway.http.ScoreHttpApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.oagi.srt.entity.jooq.Tables.*;

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
    private long millis = System.currentTimeMillis();

    @Test
    public void insertTopLevelAbieTest() {
        ULong topLevelAbieId = insertTopLevelAbie();

        TopLevelAbieRecord topLevelAbie = dslContext.selectFrom(TOP_LEVEL_ABIE)
                .where(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.eq(topLevelAbieId))
                .fetchOptional().orElse(null);

        assertNotNull(topLevelAbie);
        assertEquals(userId, topLevelAbie.getOwnerUserId());
        assertEquals(millis, topLevelAbie.getLastUpdateTimestamp().getTime());
        assertEquals(releaseId, topLevelAbie.getReleaseId());
    }

    private ULong insertTopLevelAbie() {
        return bieRepository.insertTopLevelAbie()
                .setUserId(userId)
                .setReleaseId(releaseId)
                .setTimestamp(millis)
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
        assertEquals(millis, abie.getCreationTimestamp().getTime());
        assertEquals(topLevelAbieId, abie.getOwnerTopLevelAbieId());
        assertEquals(roleOfAccManifestId, abie.getBasedAccManifestId());
    }

    private ULong insertAbie(ULong topLevelAbieId, ULong roleOfAccManifestId) {
        return bieRepository.insertAbie()
                .setUserId(userId)
                .setTopLevelAbieId(topLevelAbieId)
                .setAccManifestId(roleOfAccManifestId)
                .setTimestamp(millis)
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
                .setTopLevelAbieId(topLevelAbieId)
                .setUserId(userId)
                .setTimestamp(millis)
                .execute();

        AsbiepRecord asbiep = dslContext.selectFrom(ASBIEP)
                .where(ASBIEP.ASBIEP_ID.eq(asbiepId))
                .fetchOptional().orElse(null);

        assertNotNull(asbiep);
        assertEquals(userId, asbiep.getCreatedBy());
        assertEquals(millis, asbiep.getCreationTimestamp().getTime());
        assertEquals(topLevelAbieId, asbiep.getOwnerTopLevelAbieId());
        assertEquals(abieId, asbiep.getRoleOfAbieId());
        assertEquals(asccpManifestId, asbiep.getBasedAsccpManifestId());
    }
}
