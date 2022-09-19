package org.oagi.score.repo.api.impl.jooq.corecomponent;

import org.junit.jupiter.api.*;
import org.oagi.score.repo.api.corecomponent.model.EntityType;
import org.oagi.score.repo.api.corecomponent.seqkey.SeqKeyWriteRepository;
import org.oagi.score.repo.api.corecomponent.seqkey.model.*;
import org.oagi.score.repo.api.impl.jooq.AbstractJooqScoreRepositoryTest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.BccManifestRecord;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.BccRecord;
import org.oagi.score.repo.api.user.model.ScoreUser;

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

    private String releaseId;

    private String allExtensionAccManifestId;

    private String identifierBccManifestId;
    private String indicatorBccManifestId;
    private String numberBccManifestId;

    @BeforeAll
    void setUp() {
        repository = scoreRepositoryFactory().createSeqKeyWriteRepository();
        requester = new ScoreUser("c720c6cf-43ef-44f6-8552-fab526c572c2", "oagis", DEVELOPER);

        releaseId = getReleaseId("10.6");
        allExtensionAccManifestId = getAccManifestIdByObjectClassTerm("All Extension");

        identifierBccManifestId = createTestBcc("Identifier", "Identifier");
        indicatorBccManifestId = createTestBcc("Indicator", "Indicator");
        numberBccManifestId = createTestBcc("Number", "Number");
    }

    private String createTestBcc(String propertyTerm, String representationTerm) {
        BccRecord bccRecord = new BccRecord();
        bccRecord.setBccId(UUID.randomUUID().toString());
        bccRecord.setGuid(UUID.randomUUID().toString().replace("-", ""));
        bccRecord.setCardinalityMin(0);
        bccRecord.setCardinalityMax(-1);
        bccRecord.setFromAccId(dslContext().select(ACC_MANIFEST.ACC_ID)
                .from(ACC_MANIFEST)
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(allExtensionAccManifestId))
                .fetchOneInto(String.class));
        bccRecord.setToBccpId(dslContext().select(BCCP.BCCP_ID)
                .from(BCCP)
                .where(and(
                        BCCP.PROPERTY_TERM.eq(propertyTerm),
                        BCCP.REPRESENTATION_TERM.eq(representationTerm)
                ))
                .fetchOneInto(String.class));
        bccRecord.setEntityType(EntityType.Element.getValue());
        bccRecord.setDen("All Extension. " + propertyTerm + ". " + representationTerm);
        bccRecord.setIsDeprecated((byte) 0);
        bccRecord.setIsNillable((byte) 0);
        bccRecord.setOwnerUserId(requester.getUserId());
        bccRecord.setCreatedBy(requester.getUserId());
        bccRecord.setLastUpdatedBy(requester.getUserId());
        bccRecord.setCreationTimestamp(LocalDateTime.now());
        bccRecord.setLastUpdateTimestamp(LocalDateTime.now());

        dslContext().insertInto(BCC)
                .set(bccRecord)
                .execute();
        String bccId = bccRecord.getBccId();

        BccManifestRecord bccManifestRecord = new BccManifestRecord();
        bccManifestRecord.setBccManifestId(UUID.randomUUID().toString());
        bccManifestRecord.setFromAccManifestId(allExtensionAccManifestId);
        bccManifestRecord.setToBccpManifestId(dslContext().select(BCCP_MANIFEST.BCCP_MANIFEST_ID)
                .from(BCCP_MANIFEST)
                .where(and(
                        BCCP_MANIFEST.RELEASE_ID.eq(releaseId),
                        BCCP_MANIFEST.BCCP_ID.eq(bccRecord.getToBccpId())
                ))
                .fetchOneInto(String.class));
        bccManifestRecord.setReleaseId(releaseId);
        bccManifestRecord.setBccId(bccId);

        dslContext().insertInto(BCC_MANIFEST)
                .set(bccManifestRecord)
                .execute();
        return bccManifestRecord.getBccManifestId();
    }

    @Test
    @Order(1)
    public void createSeqKeyTest() {
        CreateSeqKeyRequest request = new CreateSeqKeyRequest(requester)
                .withFromAccManifestId(allExtensionAccManifestId)
                .withType(SeqKeyType.BCC)
                .withManifestId(identifierBccManifestId);
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
        Assumptions.assumeTrue(head.getBccManifestId().equals(identifierBccManifestId));

        // create next
        SeqKey next = repository.createSeqKey(
                new CreateSeqKeyRequest(requester)
                        .withFromAccManifestId(allExtensionAccManifestId)
                        .withType(SeqKeyType.BCC)
                        .withManifestId(indicatorBccManifestId))
                .getSeqKey();

        assertEquals(indicatorBccManifestId, next.getBccManifestId());

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
        assertEquals(identifierBccManifestId, head.getBccManifestId());

        List<SeqKey> seqKeys = new ArrayList();
        for (SeqKey seqKey : head) {
            seqKeys.add(seqKey);
        }

        assertTrue(seqKeys.size() == 2);

        assertEquals(identifierBccManifestId, seqKeys.get(0).getBccManifestId());
        assertEquals(EntityType.Element, seqKeys.get(0).getEntityType());

        assertEquals(indicatorBccManifestId, seqKeys.get(1).getBccManifestId());
        assertEquals(EntityType.Element, seqKeys.get(1).getEntityType());
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
        Assumptions.assumeTrue(head.getBccManifestId().equals(identifierBccManifestId));

        Assumptions.assumeTrue(head.getNextSeqKey() != null);
        Assumptions.assumeTrue(head.getNextSeqKey().getBccManifestId().equals(indicatorBccManifestId));

        DeleteSeqKeyRequest request = new DeleteSeqKeyRequest(requester)
                .withSeqKeyId(head.getNextSeqKey().getSeqKeyId());
        DeleteSeqKeyResponse response = repository.deleteSeqKey(request);

        assertEquals(head.getNextSeqKey().getSeqKeyId(), response.getSeqKeyId());
        assertEquals(0, dslContext().selectCount()
                .from(SEQ_KEY)
                .where(SEQ_KEY.SEQ_KEY_ID.eq(head.getNextSeqKey().getSeqKeyId()))
                .fetchOneInto(Integer.class));

        // reload
        head = scoreRepositoryFactory().createSeqKeyReadRepository()
                .getSeqKey(new GetSeqKeyRequest(requester)
                        .withFromAccManifestId(getAccManifestIdByObjectClassTerm("All Extension")))
                .getSeqKey();
        assertEquals(identifierBccManifestId, head.getBccManifestId());

        List<SeqKey> seqKeys = new ArrayList();
        for (SeqKey seqKey : head) {
            seqKeys.add(seqKey);
        }

        assertTrue(seqKeys.size() == 1);
        assertNull(seqKeys.get(0).getPrevSeqKey());
        assertNull(seqKeys.get(0).getNextSeqKey());
    }

    private String getReleaseId(String releaseNum) {
        return dslContext().select(RELEASE.RELEASE_ID)
                .from(RELEASE)
                .where(RELEASE.RELEASE_NUM.eq(releaseNum))
                .fetchOneInto(String.class);
    }

    private String getAccManifestIdByObjectClassTerm(String objectClassTerm) {
        return dslContext().select(ACC_MANIFEST.ACC_MANIFEST_ID)
                .from(ACC)
                .join(ACC_MANIFEST).on(ACC.ACC_ID.eq(ACC_MANIFEST.ACC_ID))
                .join(RELEASE).on(ACC_MANIFEST.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .where(and(
                        ACC.OBJECT_CLASS_TERM.eq(objectClassTerm),
                        RELEASE.RELEASE_NUM.eq("10.6")
                ))
                .fetchOneInto(String.class);
    }

    @AfterAll
    void tearDown() {
        dslContext().update(BCC_MANIFEST)
                .setNull(BCC_MANIFEST.SEQ_KEY_ID)
                .where(BCC_MANIFEST.BCC_MANIFEST_ID.in(Arrays.asList(identifierBccManifestId, indicatorBccManifestId, numberBccManifestId)))
                .execute();

        dslContext().deleteFrom(SEQ_KEY)
                .where(SEQ_KEY.FROM_ACC_MANIFEST_ID.eq(allExtensionAccManifestId))
                .execute();

        List<String> bccIdList = dslContext().select(BCC_MANIFEST.BCC_ID)
                .from(BCC_MANIFEST)
                .where(BCC_MANIFEST.BCC_MANIFEST_ID.in(Arrays.asList(identifierBccManifestId, indicatorBccManifestId, numberBccManifestId)))
                .fetchInto(String.class);

        dslContext().deleteFrom(BCC_MANIFEST)
                .where(BCC_MANIFEST.BCC_MANIFEST_ID.in(Arrays.asList(identifierBccManifestId, indicatorBccManifestId, numberBccManifestId)))
                .execute();

        dslContext().deleteFrom(BCC)
                .where(BCC.BCC_ID.in(bccIdList))
                .execute();
    }

}
