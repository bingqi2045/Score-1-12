package org.oagi.srt.repo;

import org.jooq.types.ULong;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oagi.srt.entity.jooq.tables.records.AsccpManifestRecord;
import org.oagi.srt.gateway.http.ScoreHttpApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = ScoreHttpApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
public class CoreComponentRepositoryTest {

    @Autowired
    private CoreComponentRepository ccRepository;

    @Test
    public void getAsccpManifestByManifestIdTestWithValidManifestId() {
        ULong manifestId = ULong.valueOf(1L);

        AsccpManifestRecord asccpManifest =
                ccRepository.getAsccpManifestByManifestId(manifestId);

        assertNotNull(asccpManifest);
        assertEquals(manifestId, asccpManifest.getAsccpManifestId());
    }

    @Test
    public void getAsccpManifestByManifestIdTestWithInvalidManifestId() {
        ULong manifestId = ULong.valueOf(0L);

        AsccpManifestRecord asccpManifest =
                ccRepository.getAsccpManifestByManifestId(manifestId);

        assertNull(asccpManifest);
    }
}
