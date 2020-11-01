package org.oagi.score.service.corecomponent.seqkey;

import org.junit.jupiter.api.*;
import org.oagi.score.repo.api.corecomponent.seqkey.model.GetSeqKeyRequest;
import org.oagi.score.repo.api.corecomponent.seqkey.model.SeqKey;
import org.oagi.score.repo.api.user.model.ScoreUser;
import org.oagi.score.service.AbstractScoreServiceTest;

import java.math.BigInteger;

import static org.jooq.impl.DSL.and;
import static org.junit.jupiter.api.Assertions.*;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;
import static org.oagi.score.repo.api.user.model.ScoreRole.DEVELOPER;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SeqKeyHandlerTest
        extends AbstractScoreServiceTest {

    private ScoreUser requester;

    private BigInteger businessObjectDocumentAccManifestId;
    private SeqKey seqKey;

    @BeforeAll
    void setUp() {
        requester = new ScoreUser(BigInteger.ONE, "oagis", DEVELOPER);

        businessObjectDocumentAccManifestId = getAccManifestIdByObjectClassTerm("Business Object Document");
        seqKey = scoreRepositoryFactory().createSeqKeyReadRepository()
                .getSeqKey(new GetSeqKeyRequest(requester)
                        .withFromAccManifestId(businessObjectDocumentAccManifestId))
                .getSeqKey();
    }

    private BigInteger getAccManifestIdByObjectClassTerm(String objectClassTerm) {
        return dslContext().select(ACC_MANIFEST.ACC_MANIFEST_ID)
                .from(ACC_MANIFEST)
                .join(ACC).on(ACC.ACC_ID.eq(ACC_MANIFEST.ACC_ID))
                .join(RELEASE).on(ACC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        ACC.OBJECT_CLASS_TERM.eq(objectClassTerm),
                        RELEASE.RELEASE_NUM.eq("10.6")
                ))
                .fetchOneInto(BigInteger.class);
    }

    @Test
    @Order(1)
    public void initBccTest() {
        SeqKeyHandler seqKeyHandler = new SeqKeyHandler(scoreRepositoryFactory(), requester);
        seqKeyHandler.initBcc(businessObjectDocumentAccManifestId, seqKey.getSeqKeyId(), seqKey.getBccManifestId());

        assertNull(seqKeyHandler.getHead().getPrevSeqKey());
        assertNull(seqKeyHandler.getTail().getNextSeqKey());
        assertNotNull(seqKeyHandler.getCurrent());
        assertEquals(seqKey.getBccManifestId(), seqKeyHandler.getCurrent().getBccManifestId());
    }

    @AfterAll
    void tearDown() {
    }

}
