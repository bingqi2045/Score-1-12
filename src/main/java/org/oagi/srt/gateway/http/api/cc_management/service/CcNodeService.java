package org.oagi.srt.gateway.http.api.cc_management.service;

import org.jooq.types.ULong;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.gateway.http.api.cc_management.data.*;
import org.oagi.srt.gateway.http.api.cc_management.data.node.*;
import org.oagi.srt.gateway.http.api.cc_management.repository.CcNodeRepository;
import org.oagi.srt.gateway.http.api.cc_management.repository.ManifestRepository;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.redis.event.EventHandler;
import org.oagi.srt.repo.CoreComponentRepository;
import org.oagi.srt.repo.RevisionRepository;
import org.oagi.srt.repo.component.acc.*;
import org.oagi.srt.repo.component.ascc.*;
import org.oagi.srt.repo.component.asccp.*;
import org.oagi.srt.repo.component.bcc.*;
import org.oagi.srt.repo.component.bccp.*;
import org.oagi.srt.repository.ReleaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.oagi.srt.gateway.http.api.cc_management.data.CcType.*;

@Service
@Transactional(readOnly = true)
public class CcNodeService extends EventHandler {

    @Autowired
    private CcNodeRepository repository;

    @Autowired
    private CoreComponentRepository ccRepository;

    @Autowired
    private AccCUDRepository accCUDRepository;

    @Autowired
    private AsccpCUDRepository asccpCUDRepository;

    @Autowired
    private BccpCUDRepository bccpCUDRepository;

    @Autowired
    private AsccCUDRepository asccCUDRepository;

    @Autowired
    private BccCUDRepository bccCUDRepository;

    @Autowired
    private RevisionRepository revisionRepository;

    @Autowired
    private ManifestRepository manifestRepository;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ReleaseRepository releaseRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public CcAccNode getAccNode(User user, BigInteger manifestId) {
        return repository.getAccNodeByAccManifestId(user, manifestId);
    }

    public CcAsccpNode getAsccpNode(User user, BigInteger manifestId) {
        return repository.getAsccpNodeByAsccpManifestId(user, manifestId);
    }

    public CcBccpNode getBccpNode(User user, BigInteger manifestId) {
        return repository.getBccpNodeByBccpManifestId(user, manifestId);
    }

    @Transactional
    public void deleteAcc(User user, BigInteger manifestId) {
        DeleteAccRepositoryRequest repositoryRequest =
                new DeleteAccRepositoryRequest(user, manifestId);

        DeleteAccRepositoryResponse repositoryResponse =
                accCUDRepository.deleteAcc(repositoryRequest);

        fireEvent(new DeletedAccEvent());
    }

    @Transactional
    public void deleteAsccp(User user, BigInteger manifestId) {
        DeleteAsccpRepositoryRequest repositoryRequest =
                new DeleteAsccpRepositoryRequest(user, manifestId);

        DeleteAsccpRepositoryResponse repositoryResponse =
                asccpCUDRepository.deleteAsccp(repositoryRequest);

        fireEvent(new DeletedAsccpEvent());
    }

    @Transactional
    public void deleteBccp(User user, BigInteger manifestId) {
        DeleteBccpRepositoryRequest repositoryRequest =
                new DeleteBccpRepositoryRequest(user, manifestId);

        DeleteBccpRepositoryResponse repositoryResponse =
                bccpCUDRepository.deleteBccp(repositoryRequest);

        fireEvent(new DeletedBccpEvent());
    }

    @Transactional
    public void deleteAscc(User user, BigInteger asccManifestId) {
        DeleteAsccRepositoryRequest request =
                new DeleteAsccRepositoryRequest(user, asccManifestId);

        asccCUDRepository.deleteAscc(request);

        fireEvent(new DeletedAsccEvent());
    }

    @Transactional
    public void deleteBcc(User user, BigInteger bccManifestId) {
        DeleteBccRepositoryRequest request =
                new DeleteBccRepositoryRequest(user, bccManifestId);

        bccCUDRepository.deleteBcc(request);

        fireEvent(new DeletedBccEvent());
    }

    public CcAccNodeDetail getAccNodeDetail(User user, CcAccNode accNode) {
        return repository.getAccNodeDetail(user, accNode);
    }

    public CcAsccpNodeDetail getAsccpNodeDetail(User user, CcAsccpNode asccpNode) {
        return repository.getAsccpNodeDetail(user, asccpNode);
    }

    public CcBccpNodeDetail getBccpNodeDetail(User user, CcBccpNode bccpNode) {
        return repository.getBccpNodeDetail(user, bccpNode);
    }

    public CcBdtScNodeDetail getBdtScNodeDetail(User user, CcBdtScNode bdtScNode) {
        return repository.getBdtScNodeDetail(user, bdtScNode);
    }

    public CcAsccpNodeDetail.Asccp getAsccp(BigInteger asccpId) {
        return repository.getAsccp(asccpId);
    }

    @Transactional
    public BigInteger createAcc(User user, CcAccCreateRequest request) {
        CreateAccRepositoryRequest repositoryRequest =
                new CreateAccRepositoryRequest(user, request.getReleaseId());

        CreateAccRepositoryResponse repositoryResponse =
                accCUDRepository.createAcc(repositoryRequest);

        fireEvent(new CreatedAccEvent());

        return repositoryResponse.getAccManifestId();
    }

    @Transactional
    public BigInteger createAsccp(User user, CcAsccpCreateRequest request) {
        CreateAsccpRepositoryRequest repositoryRequest =
                new CreateAsccpRepositoryRequest(user,
                        request.getRoleOfAccManifestId(), request.getReleaseId());

        CreateAsccpRepositoryResponse repositoryResponse =
                asccpCUDRepository.createAsccp(repositoryRequest);

        fireEvent(new CreatedAsccpEvent());

        return repositoryResponse.getAsccpManifestId();
    }

    @Transactional
    public BigInteger createBccp(User user, CcBccpCreateRequest request) {
        CreateBccpRepositoryRequest repositoryRequest =
                new CreateBccpRepositoryRequest(user,
                        request.getBdtManifestId(), request.getReleaseId());

        CreateBccpRepositoryResponse repositoryResponse =
                bccpCUDRepository.createBccp(repositoryRequest);

        fireEvent(new CreatedBccpEvent());

        return repositoryResponse.getBccpManifestId();
    }

    @Transactional
    public CcUpdateResponse updateCcDetails(User user, CcUpdateRequest ccUpdateRequest) {
        CcUpdateResponse ccUpdateResponse = new CcUpdateResponse();

        ccUpdateResponse.setAccNodeResults(
                updateAccDetail(user, ccUpdateRequest.getAccNodeDetails()));
        ccUpdateResponse.setAsccpNodeResults(
                updateAsccp(user, ccUpdateRequest.getAsccpNodeDetails()));
        ccUpdateResponse.setBccpNodeResults(
                updateBccpDetail(user, ccUpdateRequest.getBccpNodeDetails()));

        return ccUpdateResponse;
    }

    @Transactional
    public List<CcAccNodeDetail> updateAccDetail(User user, List<CcAccNodeDetail> ccAccNodeDetails) {
        LocalDateTime timestamp = LocalDateTime.now();
        List<CcAccNodeDetail> updatedAccNodeDetails = new ArrayList<>();
        for (CcAccNodeDetail detail : ccAccNodeDetails) {
            CcAccNode ccAccNode = updateAccDetail(user, timestamp, detail);
            updatedAccNodeDetails.add(getAccNodeDetail(user, ccAccNode));
        }
        return updatedAccNodeDetails;
    }

    @Transactional
    public List<CcAsccpNodeDetail> updateAsccp(User user, List<CcAsccpNodeDetail> asccpNodeDetails) {
        LocalDateTime timestamp = LocalDateTime.now();
        List<CcAsccpNodeDetail> updatedAsccpNodeDetails = new ArrayList<>();
        for (CcAsccpNodeDetail detail : asccpNodeDetails) {
            if (detail.getAscc() != null) {
                updateAsccDetail(user, timestamp, detail.getAscc());
            } else {
                updateAsccpDetail(user, timestamp, detail.getAsccp());
            }
            CcAsccpNode ccAsccpNode = getAsccpNode(user, detail.getAsccp().getManifestId());
            updatedAsccpNodeDetails.add(getAsccpNodeDetail(user, ccAsccpNode));
        }
        return updatedAsccpNodeDetails;
    }

    @Transactional
    public List<CcBccpNodeDetail> updateBccpDetail(User user, List<CcBccpNodeDetail> bccpNodeDetails) {
        LocalDateTime timestamp = LocalDateTime.now();
        List<CcBccpNodeDetail> updatedBccpNodeDetails = new ArrayList<>();
        for (CcBccpNodeDetail detail : bccpNodeDetails) {
            if(detail.getBcc() != null) {
                updateBccDetail(user, timestamp, detail.getBcc());
            } else {
                updateBccpDetail(user, timestamp, detail.getBccp());
            }
            CcBccpNode ccBccpNode = getBccpNode(user, detail.getBccp().getManifestId());
            updatedBccpNodeDetails.add(getBccpNodeDetail(user, ccBccpNode));
        }
        return updatedBccpNodeDetails;
    }

    private CcAccNode updateAccDetail(User user, LocalDateTime timestamp, CcAccNodeDetail detail) {
        UpdateAccPropertiesRepositoryRequest request =
                new UpdateAccPropertiesRepositoryRequest(user, timestamp, detail.getManifestId());

        request.setObjectClassTerm(detail.getObjectClassTerm());
        request.setDefinition(detail.getDefinition());
        request.setDefinitionSource(detail.getDefinitionSource());
        request.setComponentType(OagisComponentType.valueOf(detail.getOagisComponentType()));
        request.setAbstract(detail.isAbstracted());
        request.setDeprecated(detail.isDeprecated());
        request.setNamespaceId(detail.getNamespaceId());

        UpdateAccPropertiesRepositoryResponse response =
                accCUDRepository.updateAccProperties(request);

        fireEvent(new UpdatedAccPropertiesEvent());

        return repository.getAccNodeByAccManifestId(user, response.getAccManifestId());
    }

    public CcAsccpNode updateAsccpDetail(User user, LocalDateTime timestamp, CcAsccpNodeDetail.Asccp detail) {
        UpdateAsccpPropertiesRepositoryRequest request =
                new UpdateAsccpPropertiesRepositoryRequest(user, timestamp, detail.getManifestId());

        request.setPropertyTerm(detail.getPropertyTerm());
        request.setDefinition(detail.getDefinition());
        request.setDefinitionSource(detail.getDefinitionSource());
        request.setReusable(detail.isReusable());
        request.setDeprecated(detail.isDeprecated());
        request.setNillable(detail.isNillable());
        request.setNamespaceId(detail.getNamespaceId());

        UpdateAsccpPropertiesRepositoryResponse response =
                asccpCUDRepository.updateAsccpProperties(request);

        fireEvent(new UpdatedAsccpPropertiesEvent());

        return repository.getAsccpNodeByAsccpManifestId(user, response.getAsccpManifestId());
    }

    private void updateAsccDetail(User user, LocalDateTime timestamp, CcAsccpNodeDetail.Ascc detail) {
        UpdateAsccPropertiesRepositoryRequest request =
                new UpdateAsccPropertiesRepositoryRequest(user, timestamp, detail.getManifestId());

        request.setCardinalityMin(detail.getCardinalityMin());
        request.setCardinalityMax(detail.getCardinalityMax());
        request.setDefinition(detail.getDefinition());
        request.setDefinitionSource(detail.getDefinitionSource());
        request.setDeprecated(detail.isDeprecated());

        asccCUDRepository.updateAsccProperties(request);

        fireEvent(new UpdatedAsccPropertiesEvent());
    }

    private void updateBccDetail(User user, LocalDateTime timestamp, CcBccpNodeDetail.Bcc detail) {
        UpdateBccPropertiesRepositoryRequest request =
                new UpdateBccPropertiesRepositoryRequest(user, timestamp, detail.getManifestId());

        request.setCardinalityMin(detail.getCardinalityMin());
        request.setCardinalityMax(detail.getCardinalityMax());
        request.setDefinition(detail.getDefinition());
        request.setDefinitionSource(detail.getDefinitionSource());
        request.setEntityType(detail.getEntityType());
        request.setDeprecated(detail.isDeprecated());
        request.setNillable(detail.isNillable());

        if (detail.getDefaultValue() != null) {
            request.setDefaultValue(detail.getDefaultValue());
            request.setFixedValue(null);
        } else if (detail.getFixedValue() != null) {
            request.setDefaultValue(null);
            request.setFixedValue(detail.getFixedValue());
        } else {
            request.setDefaultValue(null);
            request.setFixedValue(null);
        }

        bccCUDRepository.updateBccProperties(request);

        fireEvent(new UpdatedBccPropertiesEvent());
    }

    private CcBccpNode updateBccpDetail(User user, LocalDateTime timestamp, CcBccpNodeDetail.Bccp detail) {
        UpdateBccpPropertiesRepositoryRequest request =
                new UpdateBccpPropertiesRepositoryRequest(user, timestamp, detail.getManifestId());

        request.setPropertyTerm(detail.getPropertyTerm());
        request.setDefaultValue(detail.getDefaultValue());
        request.setFixedValue(detail.getFixedValue());
        request.setDefinition(detail.getDefinition());
        request.setDefinitionSource(detail.getDefinitionSource());
        request.setDeprecated(detail.isDeprecated());
        request.setNillable(detail.isNillable());
        request.setNamespaceId(detail.getNamespaceId());

        UpdateBccpPropertiesRepositoryResponse response =
                bccpCUDRepository.updateBccpProperties(request);

        fireEvent(new UpdatedBccpPropertiesEvent());

        return repository.getBccpNodeByBccpManifestId(user, response.getBccpManifestId());
    }

    @Transactional
    public long appendAsccp(User user, BigInteger releaseId, BigInteger accManifestId, BigInteger asccpManifestId) {
        LocalDateTime timestamp = LocalDateTime.now();
        CreateAsccRepositoryRequest request =
                new CreateAsccRepositoryRequest(user, timestamp, releaseId, accManifestId, asccpManifestId);

        CreateAsccRepositoryResponse response = asccCUDRepository.createAscc(request);
        fireEvent(new CreatedAsccEvent());
        return response.getAsccManifestId().longValue();
    }

    @Transactional
    public long appendBccp(User user, BigInteger releaseId, BigInteger accManifestId, BigInteger bccpManifestId) {
        LocalDateTime timestamp = LocalDateTime.now();
        CreateBccRepositoryRequest request =
                new CreateBccRepositoryRequest(user, timestamp, releaseId, accManifestId, bccpManifestId);

        CreateBccRepositoryResponse response = bccCUDRepository.createBcc(request);
        fireEvent(new CreatedBccEvent());
        return response.getBccManifestId().longValue();
    }

    @Transactional
    public BigInteger updateAccBasedAcc(User user, BigInteger accManifestId, BigInteger basedAccManifestId) {
        UpdateAccBasedAccRepositoryRequest repositoryRequest =
                new UpdateAccBasedAccRepositoryRequest(user, accManifestId, basedAccManifestId);

        UpdateAccBasedAccRepositoryResponse repositoryResponse =
                accCUDRepository.updateAccBasedAcc(repositoryRequest);

        fireEvent(new UpdatedAccBasedAccEvent());

        return repositoryResponse.getAccManifestId();
    }

    @Transactional
    public BigInteger updateAsccpRoleOfAcc(User user, BigInteger asccpManifestId, BigInteger roleOfAccManifestId) {
        UpdateAsccpRoleOfAccRepositoryRequest repositoryRequest =
                new UpdateAsccpRoleOfAccRepositoryRequest(user, asccpManifestId, roleOfAccManifestId);

        UpdateAsccpRoleOfAccRepositoryResponse repositoryResponse =
                asccpCUDRepository.updateAsccpBdt(repositoryRequest);

        fireEvent(new UpdatedAsccpRoleOfAccEvent());

        return repositoryResponse.getAsccpManifestId();
    }

    @Transactional
    public BigInteger updateBccpBdt(User user, BigInteger bccpManifestId, BigInteger bdtManifestId) {
        UpdateBccpBdtRepositoryRequest repositoryRequest =
                new UpdateBccpBdtRepositoryRequest(user, bccpManifestId, bdtManifestId);

        UpdateBccpBdtRepositoryResponse repositoryResponse =
                bccpCUDRepository.updateBccpBdt(repositoryRequest);

        fireEvent(new UpdatedBccpBdtEvent());

        return repositoryResponse.getBccpManifestId();
    }

    @Transactional
    public BigInteger updateAccState(User user, BigInteger accManifestId, String state) {
        UpdateAccStateRepositoryRequest repositoryRequest =
                new UpdateAccStateRepositoryRequest(user, accManifestId, CcState.valueOf(state));

        UpdateAccStateRepositoryResponse repositoryResponse =
                accCUDRepository.updateAccState(repositoryRequest);

        fireEvent(new UpdatedAccStateEvent());

        return repositoryResponse.getAccManifestId();
    }

    @Transactional
    public BigInteger updateAsccpState(User user, BigInteger asccpManifestId, String state) {
        UpdateAsccpStateRepositoryRequest repositoryRequest =
                new UpdateAsccpStateRepositoryRequest(user, asccpManifestId, CcState.valueOf(state));

        UpdateAsccpStateRepositoryResponse repositoryResponse =
                asccpCUDRepository.updateAsccpState(repositoryRequest);

        fireEvent(new UpdatedAsccpStateEvent());

        return repositoryResponse.getAsccpManifestId();
    }

    @Transactional
    public BigInteger updateBccpState(User user, BigInteger bccpManifestId, String state) {
        UpdateBccpStateRepositoryRequest repositoryRequest =
                new UpdateBccpStateRepositoryRequest(user, bccpManifestId, CcState.valueOf(state));

        UpdateBccpStateRepositoryResponse repositoryResponse =
                bccpCUDRepository.updateBccpState(repositoryRequest);

        fireEvent(new UpdatedBccpStateEvent());

        return repositoryResponse.getBccpManifestId();
    }

    @Transactional
    public BigInteger makeNewRevisionForAcc(User user, BigInteger accManifestId) {
        ReviseAccRepositoryRequest repositoryRequest =
                new ReviseAccRepositoryRequest(user, accManifestId);

        ReviseAccRepositoryResponse repositoryResponse =
                accCUDRepository.reviseAcc(repositoryRequest);

        fireEvent(new RevisedAccEvent());

        return repositoryResponse.getAccManifestId();
    }

    @Transactional
    public BigInteger makeNewRevisionForAsccp(User user, BigInteger asccpManifestId) {
        ReviseAsccpRepositoryRequest repositoryRequest =
                new ReviseAsccpRepositoryRequest(user, asccpManifestId);

        ReviseAsccpRepositoryResponse repositoryResponse =
                asccpCUDRepository.reviseAsccp(repositoryRequest);

        fireEvent(new RevisedAsccpEvent());

        return repositoryResponse.getAsccpManifestId();
    }

    @Transactional
    public BigInteger makeNewRevisionForBccp(User user, BigInteger bccpManifestId) {
        ReviseBccpRepositoryRequest repositoryRequest =
                new ReviseBccpRepositoryRequest(user, bccpManifestId);

        ReviseBccpRepositoryResponse repositoryResponse =
                bccpCUDRepository.reviseBccp(repositoryRequest);

        fireEvent(new RevisedBccpEvent());

        return repositoryResponse.getBccpManifestId();
    }

    @Transactional
    public CcAccNode discardAccBasedId(User user, BigInteger accManifestId) {

        LocalDateTime timestamp = LocalDateTime.now();
        ULong userId = ULong.valueOf(sessionService.userId(user));

        AccManifestRecord accManifestRecord = ccRepository.getAccManifestByManifestId(ULong.valueOf(accManifestId));
        AccRecord accRecord = ccRepository.getAccById(accManifestRecord.getAccId());

        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(userId)
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Modified)
                .setReference("acc" + accManifestRecord.getAccManifestId())
                .setPrevRevisionId(accManifestRecord.getRevisionId())
                .execute();

        ULong accId = ccRepository.updateAccArguments(accRecord)
                .setLastUpdatedBy(userId)
                .setLastUpdateTimestamp(timestamp)
                .setBasedAccId(null)
                .setPrevAccId(accRecord.getAccId())
                .execute();

        ccRepository.updateAccManifestArguments(accManifestRecord)
                .setAccId(accId)
                .setBasedAccManifestId(null)
                .execute();

        updateAccChain(userId, accManifestRecord.getAccManifestId(), timestamp, revisionId);

        return getAccNode(user, accManifestId);
    }

    private CcState getStateCode(String state) {
        if (CcState.WIP.name().equals(state)) {
            return CcState.WIP;
        } else if (CcState.Draft.name().equals(state)) {
            return CcState.Draft;
        } else if (CcState.Candidate.name().equals(state)) {
            return CcState.Candidate;
        } else if (CcState.QA.name().equals(state)) {
            return CcState.QA;
        } else if (CcState.Production.name().equals(state)) {
            return CcState.Production;
        } else if (CcState.ReleaseDraft.name().equals(state)) {
            return CcState.ReleaseDraft;
        } else if (CcState.Published.name().equals(state)) {
            return CcState.Published;
        }
        return null;
    }

    private void decreaseSeqKeyGreaterThan(BigInteger userId, ULong accManifestId, int seqKey, LocalDateTime timestamp, ULong revisionId) {
        if (seqKey == 0) {
            return;
        }

        List<AsccManifestRecord> asccManifestRecords =
                ccRepository.getAsccManifestByFromAccManifestId(accManifestId);

        for (AsccManifestRecord asccManifestRecord : asccManifestRecords) {
            AsccRecord asccRecord = ccRepository.getAsccById(asccManifestRecord.getAsccId());
            if (asccRecord.getSeqKey() <= seqKey || asccRecord.getState().equals(CcState.Deleted.name())) {
                continue;
            }

            ULong asccId = ccRepository.updateAsccArguments(asccRecord)
                    .setLastUpdatedBy(ULong.valueOf(userId))
                    .setLastUpdateTimestamp(timestamp)
                    .setSeqKey(asccRecord.getSeqKey() - 1)
                    .execute();

            ccRepository.updateAsccManifestArguments(asccManifestRecord)
                    .setAsccId(asccId)
                    .execute();
        }

        List<BccManifestRecord> bccManifestRecords =
                manifestRepository.getBccManifestByFromAccManifestId(accManifestId);
        for (BccManifestRecord bccManifestRecord : bccManifestRecords) {
            BccRecord bccRecord = ccRepository.getBccById(bccManifestRecord.getBccId());
            if (bccRecord.getSeqKey() <= seqKey || bccRecord.getState().equals(CcState.Deleted.name())) {
                continue;
            }

            ULong bccId = ccRepository.updateBccArguments(bccRecord)
                    .setLastUpdatedBy(ULong.valueOf(userId))
                    .setLastUpdateTimestamp(timestamp)
                    .setSeqKey(bccRecord.getSeqKey() - 1)
                    .execute();

            ccRepository.updateBccManifestArguments(bccManifestRecord)
                    .setBccId(bccId)
                    .execute();
        }
    }

    public CcRevisionResponse getAccNodeRevision(User user, BigInteger manifestId) {
        CcAccNode accNode = getAccNode(user, manifestId);
        BigInteger lastPublishedCcId = getLastPublishedCcId(accNode.getAccId(), CcType.ACC);
        CcRevisionResponse ccRevisionResponse = new CcRevisionResponse();
        if (lastPublishedCcId != null) {
            AccRecord accRecord = ccRepository.getAccById(ULong.valueOf(lastPublishedCcId));
            ccRevisionResponse.setCcId(accRecord.getAccId().longValue());
            ccRevisionResponse.setType(ACC.toString());
            ccRevisionResponse.setIsDeprecated(accRecord.getIsDeprecated() == 1);
            ccRevisionResponse.setIsAbstract(accRecord.getIsAbstract() == 1);
            ccRevisionResponse.setName(accRecord.getObjectClassTerm());
            ccRevisionResponse.setHasBaseCc(accRecord.getBasedAccId() != null);
            List<AsccManifestRecord> asccManifestRecordList
                    = ccRepository.getAsccManifestByFromAccManifestId(ULong.valueOf(manifestId));
            List<String> associationKeys = new ArrayList<>();
            for (AsccManifestRecord asccManifestRecord : asccManifestRecordList) {
                BigInteger lastAsccId = getLastPublishedCcId(asccManifestRecord.getAsccId().toBigInteger(), CcType.ASCC);
                if (lastAsccId != null) {
                    associationKeys.add(CcType.ASCCP.toString().toLowerCase() + asccManifestRecord.getToAsccpManifestId());
                }
            }
            List<BccManifestRecord> bccManifestRecordList
                    = ccRepository.getBccManifestByFromAccManifestId(ULong.valueOf(manifestId));
            for (BccManifestRecord bccManifestRecord : bccManifestRecordList) {
                BigInteger lastBccId = getLastPublishedCcId(bccManifestRecord.getBccId().toBigInteger(), CcType.BCC);
                if (lastBccId != null) {
                    associationKeys.add(CcType.BCCP.toString().toLowerCase() + bccManifestRecord.getToBccpManifestId());
                }
            }
            ccRevisionResponse.setAssociationKeys(associationKeys);
        }
        return ccRevisionResponse;
    }

    public CcRevisionResponse getBccpNodeRevision(User user, BigInteger manifestId) {
        CcBccpNode bccpNode = getBccpNode(user, manifestId);
        BigInteger lastPublishedCcId = getLastPublishedCcId(bccpNode.getBccpId(), BCCP);
        CcRevisionResponse ccRevisionResponse = new CcRevisionResponse();
        if (lastPublishedCcId != null) {
            BccpRecord bccpRecord = ccRepository.getBccpById(ULong.valueOf(lastPublishedCcId));
            ccRevisionResponse.setCcId(bccpRecord.getBccpId().longValue());
            ccRevisionResponse.setType(BCCP.toString());
            ccRevisionResponse.setIsDeprecated(bccpRecord.getIsDeprecated() == 1);
            ccRevisionResponse.setIsNillable(bccpRecord.getIsNillable() == 1);
            ccRevisionResponse.setName(bccpRecord.getPropertyTerm());
            ccRevisionResponse.setFixedValue(bccpRecord.getFixedValue());
            ccRevisionResponse.setHasBaseCc(bccpRecord.getBdtId() != null);
        }
        return ccRevisionResponse;
    }

    public CcRevisionResponse getAsccpNodeRevision(User user, BigInteger manifestId) {
        CcAsccpNode asccpNode = getAsccpNode(user, manifestId);
        BigInteger lastPublishedCcId = getLastPublishedCcId(asccpNode.getAsccpId(), ASCCP);
        CcRevisionResponse ccRevisionResponse = new CcRevisionResponse();
        if (lastPublishedCcId != null) {
            AsccpRecord asccpRecord = ccRepository.getAsccpById(ULong.valueOf(lastPublishedCcId));
            ccRevisionResponse.setCcId(asccpRecord.getAsccpId().longValue());
            ccRevisionResponse.setType(ASCCP.toString());
            ccRevisionResponse.setIsDeprecated(asccpRecord.getIsDeprecated() == 1);
            ccRevisionResponse.setIsNillable(asccpRecord.getIsNillable() == 1);
            ccRevisionResponse.setName(asccpRecord.getPropertyTerm());
            ccRevisionResponse.setHasBaseCc(asccpRecord.getRoleOfAccId() != null);
        }
        return ccRevisionResponse;
    }

    private BigInteger getLastPublishedCcId(BigInteger ccId, CcType type) {
        if (ccId == null) {
            return null;
        }
        switch (type) {
            case ACC:
                AccRecord accRecord = ccRepository.getAccById(ULong.valueOf(ccId));
                if (accRecord.getState().equals(CcState.Published.name())) {
                    return ccId;
                }
                if (accRecord.getPrevAccId() == null) {
                    return null;
                }
                return getLastPublishedCcId(accRecord.getPrevAccId().toBigInteger(), CcType.ACC);
            case ASCC:
                AsccRecord asccRecord = ccRepository.getAsccById(ULong.valueOf(ccId));
                if (asccRecord.getState().equals(CcState.Published.name())) {
                    return ccId;
                }
                if (asccRecord.getPrevAsccId() == null) {
                    return null;
                }
                return getLastPublishedCcId(asccRecord.getPrevAsccId().toBigInteger(), CcType.ASCC);
            case BCC:
                BccRecord bccRecord = ccRepository.getBccById(ULong.valueOf(ccId));
                if (bccRecord.getState().equals(CcState.Published.name())) {
                    return ccId;
                }
                if (bccRecord.getPrevBccId() == null) {
                    return null;
                }
                return getLastPublishedCcId(bccRecord.getPrevBccId().toBigInteger(), CcType.BCC);
            case ASCCP:
                AsccpRecord asccpRecord = ccRepository.getAsccpById(ULong.valueOf(ccId));
                if (asccpRecord.getState().equals(CcState.Published.name())) {
                    return ccId;
                }
                if (asccpRecord.getPrevAsccpId() == null) {
                    return null;
                }
                return getLastPublishedCcId(asccpRecord.getPrevAsccpId().toBigInteger(), CcType.ASCCP);
            case BCCP:
                BccpRecord bccpRecord = ccRepository.getBccpById(ULong.valueOf(ccId));
                if (bccpRecord.getState().equals(CcState.Published.name())) {
                    return ccId;
                }
                if (bccpRecord.getPrevBccpId() == null) {
                    return null;
                }
                return getLastPublishedCcId(bccpRecord.getPrevBccpId().toBigInteger(), CcType.BCCP);

            case BDT:
                return null;

            case BDT_SC:
                return null;

            case XBT:
                return null;

            default:
                return null;

        }
    }

    private void updateAccChain(ULong userId, ULong accManifestId, LocalDateTime timestamp, ULong revisionId) {
        AccManifestRecord accManifestRecord = ccRepository.getAccManifestByManifestId(accManifestId);
        AccRecord accRecord = ccRepository.getAccById(accManifestRecord.getAccId());
        updateAsccByFromAcc(userId, accManifestRecord, accRecord, timestamp, revisionId);
        updateBccByFromAcc(userId, accManifestRecord, accRecord, timestamp, revisionId);
        updateAsccpByRoleOfAcc(userId, accManifestRecord, accRecord, timestamp, revisionId);
        updateAccByBasedAcc(userId, accManifestRecord, accRecord, timestamp, revisionId);
    }

    private void updateAsccByFromAcc(ULong userId, AccManifestRecord accManifest, AccRecord acc, LocalDateTime timestamp, ULong revisionId) {
        List<AsccManifestRecord> asccManifestRecordList = ccRepository.getAsccManifestByFromAccManifestId(accManifest.getAccManifestId());
        for (AsccManifestRecord asccManifest : asccManifestRecordList) {
            AsccRecord asccRecord = ccRepository.getAsccById(asccManifest.getAsccId());
            if (asccRecord.getState().equals(CcState.Deleted.name())) {
                continue;
            }
            AsccpManifestRecord asccpManifestRecord = ccRepository.getAsccpManifestByManifestId(asccManifest.getToAsccpManifestId());
            AsccpRecord asccpRecord = ccRepository.getAsccpById(asccpManifestRecord.getAsccpId());
            ULong asccId = ccRepository.updateAsccArguments(asccRecord)
                    .setDen(acc.getObjectClassTerm() + ". " + asccpRecord.getPropertyTerm())
                    .setFromAccId(acc.getAccId())
                    .setLastUpdatedBy(userId)
                    .setLastUpdateTimestamp(timestamp)
                    .setState(CcState.valueOf(acc.getState()))
                    .setPrevAsccId(asccRecord.getAsccId())
                    .execute();
            ccRepository.updateAsccManifestArguments(asccManifest)
                    .setAsccId(asccId)
                    .execute();
        }
    }

    private void updateAsccByToAsccp(ULong userId, ULong asccpManifestId, LocalDateTime timestamp, ULong revisionId) {
        AsccpManifestRecord asccpManifestRecord = ccRepository.getAsccpManifestByManifestId(asccpManifestId);
        AsccpRecord asccpRecord = ccRepository.getAsccpById(asccpManifestRecord.getAsccpId());
        List<AsccManifestRecord> asccManifestRecordList = ccRepository.getAsccManifestByToAsccpManifestId(asccpManifestId);
        for (AsccManifestRecord asccManifest : asccManifestRecordList) {
            AsccRecord asccRecord = ccRepository.getAsccById(asccManifest.getAsccId());
            if (asccRecord.getState().equals(CcState.Deleted.name())) {
                continue;
            }
            AccManifestRecord AccManifestRecord = ccRepository.getAccManifestByManifestId(asccManifest.getFromAccManifestId());
            AccRecord accRecord = ccRepository.getAccById(AccManifestRecord.getAccId());
            ULong asccId = ccRepository.updateAsccArguments(asccRecord)
                    .setDen(accRecord.getObjectClassTerm() + ". " + asccpRecord.getPropertyTerm())
                    .setToAsccpId(asccpRecord.getAsccpId())
                    .setLastUpdatedBy(userId)
                    .setLastUpdateTimestamp(timestamp)
                    .setPrevAsccId(asccRecord.getAsccId())
                    .execute();
            ccRepository.updateAsccManifestArguments(asccManifest)
                    .setAsccId(asccId)
                    .execute();
        }
    }

    private void updateBccByFromAcc(ULong userId, AccManifestRecord accManifest, AccRecord acc, LocalDateTime timestamp, ULong revisionId) {
        List<BccManifestRecord> bccManifestRecordList = ccRepository.getBccManifestByFromAccManifestId(accManifest.getAccManifestId());
        for (BccManifestRecord bccManifest : bccManifestRecordList) {
            BccRecord bccRecord = ccRepository.getBccById(bccManifest.getBccId());
            if (bccRecord.getState().equals(CcState.Deleted.name())) {
                continue;
            }
            BccpManifestRecord bccpManifestRecord = ccRepository.getBccpManifestByManifestId(bccManifest.getToBccpManifestId());
            BccpRecord bccpRecord = ccRepository.getBccpById(bccpManifestRecord.getBccpId());
            ULong bccId = ccRepository.updateBccArguments(bccRecord)
                    .setDen(acc.getObjectClassTerm() + ". " + bccpRecord.getPropertyTerm())
                    .setFromAccId(acc.getAccId())
                    .setLastUpdatedBy(userId)
                    .setLastUpdateTimestamp(timestamp)
                    .setState(CcState.valueOf(acc.getState()))
                    .setPrevBccId(bccRecord.getBccId())
                    .execute();
            ccRepository.updateBccManifestArguments(bccManifest)
                    .setBccId(bccId)
                    .execute();
        }
    }

    private void updateBccByToBccp(ULong userId, ULong bccpManifestId, LocalDateTime timestamp, ULong revisionId) {
        BccpManifestRecord bccpManifestRecord = ccRepository.getBccpManifestByManifestId(bccpManifestId);
        BccpRecord bccpRecord = ccRepository.getBccpById(bccpManifestRecord.getBccpId());
        List<BccManifestRecord> bccManifestRecordList = ccRepository.getBccManifestByToBccpManifestId(bccpManifestId);
        for (BccManifestRecord bccManifest : bccManifestRecordList) {
            BccRecord bccRecord = ccRepository.getBccById(bccManifest.getBccId());
            if (bccRecord.getState().equals(CcState.Deleted.name())) {
                continue;
            }
            AccManifestRecord AccManifestRecord = ccRepository.getAccManifestByManifestId(bccManifest.getFromAccManifestId());
            AccRecord accRecord = ccRepository.getAccById(AccManifestRecord.getAccId());
            ULong bccId = ccRepository.updateBccArguments(bccRecord)
                    .setDen(accRecord.getObjectClassTerm() + ". " + bccpRecord.getPropertyTerm())
                    .setToBccpId(bccpRecord.getBccpId())
                    .setLastUpdatedBy(userId)
                    .setLastUpdateTimestamp(timestamp)
                    .setPrevBccId(bccRecord.getBccId())
                    .execute();
            ccRepository.updateBccManifestArguments(bccManifest)
                    .setBccId(bccId)
                    .execute();
        }
    }

    private void updateAsccpByRoleOfAcc(ULong userId, AccManifestRecord accManifestRecord, AccRecord accRecord, LocalDateTime timestamp, ULong revisionId) {
        List<AsccpManifestRecord> asccpManifestRecordList = ccRepository.getAsccpManifestByRolOfAccManifestId(accManifestRecord.getAccManifestId());
        for (AsccpManifestRecord asccpManifest : asccpManifestRecordList) {
            AsccpRecord asccpRecord = ccRepository.getAsccpById(asccpManifest.getAsccpId());
            if (asccpRecord.getState().equals(CcState.Deleted.name())) {
                continue;
            }
            ULong asccpId = ccRepository.updateAsccpArguments(asccpRecord)
                    .setDen(asccpRecord.getPropertyTerm() + ". " + accRecord.getObjectClassTerm())
                    .setRoleOfAccId(accRecord.getAccId())
                    .setLastUpdatedBy(userId)
                    .setLastUpdateTimestamp(timestamp)
                    .setPrevAsccpId(asccpRecord.getAsccpId())
                    .execute();
            ccRepository.updateAsccpManifestArguments(asccpManifest)
                    .setAsccpId(asccpId)
                    .execute();
            asccpManifest.setAsccpId(asccpId);

            updateAsccByToAsccp(userId, asccpManifest.getAsccpManifestId(), timestamp, revisionId);
        }
    }

    private void updateAccByBasedAcc(ULong userId, AccManifestRecord basedAccManifestRecord, AccRecord basedAccRecord, LocalDateTime timestamp, ULong revisionId) {
        List<AccManifestRecord> accManifestRecordList = ccRepository.getAccManifestByBasedAccManifestId(basedAccManifestRecord.getAccManifestId());
        for (AccManifestRecord accManifestRecord : accManifestRecordList) {
            AccRecord accRecord = ccRepository.getAccById(accManifestRecord.getAccId());
            if (accRecord.getState().equals(CcState.Deleted.name())) {
                continue;
            }
            ULong accId = ccRepository.updateAccArguments(accRecord)
                    .setBasedAccId(basedAccRecord.getAccId())
                    .setLastUpdatedBy(userId)
                    .setLastUpdateTimestamp(timestamp)
                    .setPrevAccId(accRecord.getAccId())
                    .execute();
            ccRepository.updateAccManifestArguments(accManifestRecord)
                    .setAccId(accId)
                    .execute();
            AccRecord updatedAcc = ccRepository.getAccById(accId);
            accManifestRecord.setAccId(accId);

            updateAsccByFromAcc(userId, accManifestRecord, updatedAcc, timestamp, revisionId);
        }
    }

    public ULong updateAccOwnerUserId(ULong accManifestId, ULong ownerUserId, ULong requesterUserId, LocalDateTime timestamp) {

        AccManifestRecord accManifestRecord = ccRepository.getAccManifestByManifestId(accManifestId);
        AccRecord accRecord = ccRepository.getAccById(accManifestRecord.getAccId());

        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(requesterUserId)
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Modified)
                .setReference("acc" + accManifestRecord.getAccManifestId())
                .setPrevRevisionId(accManifestRecord.getRevisionId())
                .execute();

        ULong accId = ccRepository.updateAccArguments(accRecord)
                .setLastUpdateTimestamp(timestamp)
                .setLastUpdatedBy(requesterUserId)
                .setOwnerUserId(ownerUserId)
                .execute();

        ccRepository.updateAccManifestArguments(accManifestRecord)
                .setAccId(accId)
                .execute();

        updateAccChain(requesterUserId, accManifestId, timestamp, revisionId);

        return accId;
    }

    public void updateAsccpOwnerUserId(ULong accManifestId, ULong ownerUserId, ULong requesterUserId, LocalDateTime timestamp) {
        AsccpManifestRecord asccpManifestRecord = ccRepository.getAsccpManifestByManifestId(accManifestId);
        AsccpRecord asccpRecord = ccRepository.getAsccpById(asccpManifestRecord.getAsccpId());

        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(requesterUserId)
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Modified)
                .setReference("asccp" + asccpManifestRecord.getAsccpManifestId())
                .setPrevRevisionId(asccpManifestRecord.getRevisionId())
                .execute();

        ULong asccpId = ccRepository.updateAsccpArguments(asccpRecord)
                .setOwnerUserId(ownerUserId)
                .setLastUpdatedBy(requesterUserId)
                .setLastUpdateTimestamp(timestamp)
                .execute();

        ccRepository.updateAsccpManifestArguments(asccpManifestRecord)
                .setAsccpId(asccpId)
                .execute();

        updateAsccByToAsccp(requesterUserId, asccpManifestRecord.getAsccpManifestId(), timestamp, revisionId);
    }

    public void updateBccpOwnerUserId(ULong accManifestId, ULong ownerUserId, ULong requesterUserId, LocalDateTime timestamp) {
        BccpManifestRecord bccpManifestRecord = ccRepository.getBccpManifestByManifestId(accManifestId);
        BccpRecord bccpRecord = ccRepository.getBccpById(bccpManifestRecord.getBccpId());

        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(requesterUserId)
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Modified)
                .setReference("bccp" + bccpManifestRecord.getBccpManifestId())
                .setPrevRevisionId(bccpManifestRecord.getRevisionId())
                .execute();

        ULong bccpId = ccRepository.updateBccpArguments(bccpRecord)
                .setOwnerUserId(ownerUserId)
                .setLastUpdatedBy(requesterUserId)
                .setLastUpdateTimestamp(timestamp)
                .execute();

        ccRepository.updateBccpManifestArguments(bccpManifestRecord)
                .setBccpId(bccpId)
                .execute();

        updateBccByToBccp(requesterUserId, bccpManifestRecord.getBccpManifestId(), timestamp, revisionId);
    }
}

