package org.oagi.score.repo.api.impl.jooq.corecomponent;

import org.jooq.types.ULong;
import org.junit.jupiter.api.*;
import org.oagi.score.repo.api.corecomponent.BccEntityType;
import org.oagi.score.repo.api.corecomponent.seqkey.SeqKeyWriteRepository;
import org.oagi.score.repo.api.corecomponent.seqkey.model.*;
import org.oagi.score.repo.api.impl.jooq.AbstractJooqScoreRepositoryTest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.BccRecord;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.jooq.impl.DSL.and;
import static org.junit.jupiter.api.Assertions.*;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;
import static org.oagi.score.repo.api.user.model.ScoreRole.DEVELOPER;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SeqKeyWriteRepositoryTest
        extends AbstractJooqScoreRepositoryTest {

    private SeqKeyWriteRepository repository;
    private ScoreUser requester;

    private BigInteger allExtensionAccId;
    private BigInteger allExtensionAccManifestId;

    private BigInteger identifierBccId;
    private BigInteger indicatorBccId;
    private BigInteger numberBccId;

    @BeforeAll
    void setUp() {
        repository = scoreRepositoryFactory().createSeqKeyWriteRepository();
        requester = new ScoreUser(BigInteger.ONE, "oagis", DEVELOPER);

        allExtensionAccId = getAccManifestIdByObjectClassTerm("All Extension");
        allExtensionAccManifestId = getAccManifestIdByObjectClassTerm("All Extension");

        identifierBccId = createTestBcc("Identifier", "Identifier");
        indicatorBccId = createTestBcc("Indicator", "Indicator");
        numberBccId = createTestBcc("Number", "Number");
    }

    private BigInteger createTestBcc(String propertyTerm, String representationTerm) {
        BccRecord bccRecord = new BccRecord();
        bccRecord.setGuid(UUID.randomUUID().toString().replace("-", ""));
        bccRecord.setCardinalityMin(0);
        bccRecord.setCardinalityMax(-1);
        bccRecord.setFromAccId(ULong.valueOf(allExtensionAccId));
        bccRecord.setToBccpId(dslContext().select(BCCP.BCCP_ID)
                .from(BCCP)
                .where(and(
                        BCCP.PROPERTY_TERM.eq(propertyTerm),
                        BCCP.REPRESENTATION_TERM.eq(representationTerm)
                ))
                .fetchOneInto(ULong.class));
        bccRecord.setEntityType(BccEntityType.Element.getValue());
        bccRecord.setDen("All Extension. " + propertyTerm + ". " + representationTerm);
        bccRecord.setIsDeprecated((byte) 0);
        bccRecord.setIsNillable((byte) 0);
        bccRecord.setOwnerUserId(ULong.valueOf(requester.getUserId()));
        bccRecord.setCreatedBy(ULong.valueOf(requester.getUserId()));
        bccRecord.setLastUpdatedBy(ULong.valueOf(requester.getUserId()));
        bccRecord.setCreationTimestamp(LocalDateTime.now());
        bccRecord.setLastUpdateTimestamp(LocalDateTime.now());

        return dslContext().insertInto(BCC)
                .set(bccRecord)
                .returning(BCC.BCC_ID)
                .fetchOne()
                .getBccId().toBigInteger();
    }

    @Test
    @Order(1)
    public void createSeqKeyTest() {
        CreateSeqKeyRequest request = new CreateSeqKeyRequest(requester)
                .withFromAccManifestId(allExtensionAccId)
                .withType(SeqKeyType.BCC)
                .withManifestId(identifierBccId);
        CreateSeqKeyResponse response = repository.createSeqKey(request);
        assertNotNull(response.getSeqKey());

        List<SeqKey> seqKeys = new ArrayList();
        for (SeqKey seqKey : response.getSeqKey()) {
            seqKeys.add(seqKey);
        }

        assertTrue(seqKeys.size() == 1);

        assertEquals(request.getManifestId(), seqKeys.get(0).getBccManifestId());
    }

    @Test
    @Order(2)
    public void updateSeqKeyTest() {
        // prerequisites
        SeqKey head = scoreRepositoryFactory().createSeqKeyReadRepository()
                .getSeqKey(new GetSeqKeyRequest(requester)
                        .withFromAccManifestId(getAccManifestIdByObjectClassTerm("All Extension")))
                .getSeqKey();

        Assumptions.assumeTrue(head != null);
        Assumptions.assumeTrue(head.getBccManifestId().equals(identifierBccId));

        // create next
        SeqKey next = repository.createSeqKey(
                new CreateSeqKeyRequest(requester)
                        .withFromAccManifestId(allExtensionAccId)
                        .withType(SeqKeyType.BCC)
                        .withManifestId(indicatorBccId))
                .getSeqKey();

        assertEquals(indicatorBccId, next.getBccManifestId());

        head.setNextSeqKey(next);
        next.setPrevSeqKey(head);

        UpdateSeqKeyRequest request = new UpdateSeqKeyRequest(requester)
                .withSeqKey(head);
        UpdateSeqKeyResponse response = repository.updateSeqKey(request);
        assertEquals(head.getSeqKeyId(), response.getSeqKeyId());

        // reload
        head = scoreRepositoryFactory().createSeqKeyReadRepository()
                .getSeqKey(new GetSeqKeyRequest(requester)
                        .withFromAccManifestId(getAccManifestIdByObjectClassTerm("All Extension")))
                .getSeqKey();
        assertEquals(identifierBccId, head.getBccManifestId());

        List<SeqKey> seqKeys = new ArrayList();
        for (SeqKey seqKey : head) {
            seqKeys.add(seqKey);
        }

        assertTrue(seqKeys.size() == 2);

        assertEquals(identifierBccId, seqKeys.get(0).getBccManifestId());
        assertEquals(BccEntityType.Element, seqKeys.get(0).getEntityType());

        assertEquals(indicatorBccId, seqKeys.get(1).getBccManifestId());
        assertEquals(BccEntityType.Element, seqKeys.get(1).getEntityType());
    }

    @Test
    @Order(3)
    public void deleteSeqKeyTest() {
        // prerequisites
        SeqKey head = scoreRepositoryFactory().createSeqKeyReadRepository()
                .getSeqKey(new GetSeqKeyRequest(requester)
                        .withFromAccManifestId(getAccManifestIdByObjectClassTerm("All Extension")))
                .getSeqKey();

        Assumptions.assumeTrue(head != null);
        Assumptions.assumeTrue(head.getBccManifestId().equals(identifierBccId));

        Assumptions.assumeTrue(head.getNextSeqKey() != null);
        Assumptions.assumeTrue(head.getNextSeqKey().getBccManifestId().equals(indicatorBccId));

        dslContext().deleteFrom(BCC)
                .where(BCC.BCC_ID.eq(ULong.valueOf(indicatorBccId)))
                .execute();

        DeleteSeqKeyRequest request = new DeleteSeqKeyRequest(requester)
                .withSeqKeyId(head.getNextSeqKey().getSeqKeyId());
        DeleteSeqKeyResponse response = repository.deleteSeqKey(request);

        assertEquals(head.getNextSeqKey().getSeqKeyId(), response.getSeqKeyId());
        assertEquals(0, dslContext().selectCount()
                .from(SEQ_KEY)
                .where(SEQ_KEY.SEQ_KEY_ID.eq(ULong.valueOf(head.getNextSeqKey().getSeqKeyId())))
                .fetchOneInto(Integer.class));

        // reload
        head = scoreRepositoryFactory().createSeqKeyReadRepository()
                .getSeqKey(new GetSeqKeyRequest(requester)
                        .withFromAccManifestId(getAccManifestIdByObjectClassTerm("All Extension")))
                .getSeqKey();
        assertEquals(identifierBccId, head.getBccManifestId());

        List<SeqKey> seqKeys = new ArrayList();
        for (SeqKey seqKey : head) {
            seqKeys.add(seqKey);
        }

        assertTrue(seqKeys.size() == 1);
        assertNull(seqKeys.get(0).getPrevSeqKey());
        assertNull(seqKeys.get(0).getNextSeqKey());
    }

    private BigInteger getAccManifestIdByObjectClassTerm(String objectClassTerm) {
        return dslContext().select(ACC_MANIFEST.ACC_MANIFEST_ID)
                .from(ACC)
                .join(ACC_MANIFEST).on(ACC.ACC_ID.eq(ACC_MANIFEST.ACC_ID))
                .join(RELEASE).on(ACC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        ACC.OBJECT_CLASS_TERM.eq(objectClassTerm),
                        RELEASE.RELEASE_NUM.eq("10.6")
                ))
                .fetchOneInto(BigInteger.class);
    }

    @AfterAll
    void tearDown() {
        dslContext().deleteFrom(BCC)
                .where(BCC.BCC_ID.in(Arrays.asList(identifierBccId, indicatorBccId, numberBccId)))
                .execute();

        dslContext().deleteFrom(SEQ_KEY)
                .where(SEQ_KEY.FROM_ACC_MANIFEST_ID.eq(ULong.valueOf(allExtensionAccManifestId)))
                .execute();
    }

}
