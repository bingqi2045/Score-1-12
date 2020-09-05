package org.oagi.score.repo.api.impl.jooq.corecomponent;

import org.jooq.types.ULong;
import org.junit.jupiter.api.*;
import org.oagi.score.repo.api.corecomponent.BccEntityType;
import org.oagi.score.repo.api.corecomponent.seqkey.SeqKeyReadRepository;
import org.oagi.score.repo.api.corecomponent.seqkey.model.GetSeqKeyRequest;
import org.oagi.score.repo.api.corecomponent.seqkey.model.GetSeqKeyResponse;
import org.oagi.score.repo.api.corecomponent.seqkey.model.SeqKey;
import org.oagi.score.repo.api.corecomponent.seqkey.model.SeqKeyType;
import org.oagi.score.repo.api.impl.jooq.AbstractJooqScoreRepositoryTest;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.jooq.impl.DSL.and;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;
import static org.oagi.score.repo.api.user.model.ScoreRole.DEVELOPER;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SeqKeyReadRepositoryTest
        extends AbstractJooqScoreRepositoryTest {

    private SeqKeyReadRepository repository;
    private ScoreUser requester;

    @BeforeAll
    void setUp() {
        repository = scoreRepositoryFactory().createSeqKeyReadRepository();
        requester = new ScoreUser(BigInteger.ONE, "oagis", DEVELOPER);
    }

    @Test
    @Order(1)
    public void getSeqKeyOfBusinessObjectDocumentTest() {
        GetSeqKeyRequest request = new GetSeqKeyRequest(requester)
                .withFromAccId(getAccIdByObjectClassTerm("Business Object Document"));
        GetSeqKeyResponse response = repository.getSeqKey(request);
        assertNotNull(response.getSeqKey());

        List<SeqKey> seqKeys = new ArrayList();
        for (SeqKey seqKey : response.getSeqKey()) {
            seqKeys.add(seqKey);
        }

        assertTrue(seqKeys.size() == 5);

        assertEquals(SeqKeyType.BCC, seqKeys.get(0).getSeqKeyType());
        assertEquals("Release Identifier", getBccpPropertyTerm(seqKeys.get(0).getCcId()));
        assertEquals(BccEntityType.Attribute, seqKeys.get(0).getEntityType());

        assertEquals(SeqKeyType.BCC, seqKeys.get(1).getSeqKeyType());
        assertEquals("Version Identifier", getBccpPropertyTerm(seqKeys.get(1).getCcId()));
        assertEquals(BccEntityType.Attribute, seqKeys.get(1).getEntityType());

        assertEquals(SeqKeyType.BCC, seqKeys.get(2).getSeqKeyType());
        assertEquals("System Environment Code", getBccpPropertyTerm(seqKeys.get(2).getCcId()));
        assertEquals(BccEntityType.Attribute, seqKeys.get(2).getEntityType());

        assertEquals(SeqKeyType.BCC, seqKeys.get(3).getSeqKeyType());
        assertEquals("Language Code", getBccpPropertyTerm(seqKeys.get(3).getCcId()));
        assertEquals(BccEntityType.Attribute, seqKeys.get(3).getEntityType());

        assertEquals(SeqKeyType.ASCC, seqKeys.get(4).getSeqKeyType());
        assertEquals("Application Area", getAsccpPropertyTerm(seqKeys.get(4).getCcId()));
        assertEquals(null, seqKeys.get(4).getEntityType());
    }

    private BigInteger getAccIdByObjectClassTerm(String objectClassTerm) {
        return dslContext().select(ACC.ACC_ID)
                .from(ACC)
                .join(ACC_MANIFEST).on(ACC.ACC_ID.eq(ACC_MANIFEST.ACC_ID))
                .join(RELEASE).on(ACC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        ACC.OBJECT_CLASS_TERM.eq(objectClassTerm),
                        RELEASE.RELEASE_NUM.eq("10.6")
                ))
                .fetchOneInto(BigInteger.class);
    }

    private String getAsccpPropertyTerm(BigInteger asccId) {
        return dslContext()
                .select(ASCCP.PROPERTY_TERM)
                .from(ASCCP)
                .join(ASCC).on(ASCCP.ASCCP_ID.eq(ASCC.TO_ASCCP_ID))
                .where(ASCC.ASCC_ID.eq(ULong.valueOf(asccId)))
                .fetchOneInto(String.class);
    }

    private String getBccpPropertyTerm(BigInteger bccId) {
        return dslContext()
                .select(BCCP.PROPERTY_TERM)
                .from(BCCP)
                .join(BCC).on(BCCP.BCCP_ID.eq(BCC.TO_BCCP_ID))
                .where(BCC.BCC_ID.eq(ULong.valueOf(bccId)))
                .fetchOneInto(String.class);
    }

}
