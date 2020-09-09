package org.oagi.score.gateway.http.api.cc_management.service;

import org.apache.commons.lang3.tuple.Pair;
import org.jooq.types.ULong;
import org.oagi.score.data.BCCEntityType;
import org.oagi.score.data.OagisComponentType;
import org.oagi.score.data.RevisionAction;
import org.oagi.score.gateway.http.api.cc_management.data.*;
import org.oagi.score.gateway.http.api.cc_management.data.node.*;
import org.oagi.score.gateway.http.api.cc_management.repository.CcNodeRepository;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.redis.event.EventHandler;
import org.oagi.score.repo.CoreComponentRepository;
import org.oagi.score.repo.RevisionRepository;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;
import org.oagi.score.repo.component.acc.*;
import org.oagi.score.repo.component.ascc.*;
import org.oagi.score.repo.component.asccp.*;
import org.oagi.score.repo.component.bcc.*;
import org.oagi.score.repo.component.bccp.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class CcNodeService extends EventHandler {

    @Autowired
    private CcNodeRepository repository;

    @Autowired
    private CoreComponentRepository ccRepository;

    @Autowired
    private AccReadRepository accReadRepository;

    @Autowired
    private AccWriteRepository accWriteRepository;

    @Autowired
    private AsccpWriteRepository asccpWriteRepository;

    @Autowired
    private AsccpReadRepository asccpReadRepository;

    @Autowired
    private BccpWriteRepository bccpWriteRepository;

    @Autowired
    private AsccWriteRepository asccWriteRepository;

    @Autowired
    private AsccReadRepository asccReadRepository;

    @Autowired
    private BccWriteRepository bccWriteRepository;

    @Autowired
    private RevisionRepository revisionRepository;

    @Autowired
    private SessionService sessionService;

    public CcAccNode getAccNode(AuthenticatedPrincipal user, BigInteger manifestId) {
        return repository.getAccNodeByAccManifestId(user, manifestId);
    }

    public CcAsccpNode getAsccpNode(AuthenticatedPrincipal user, BigInteger manifestId) {
        return repository.getAsccpNodeByAsccpManifestId(user, manifestId);
    }

    public CcBccpNode getBccpNode(AuthenticatedPrincipal user, BigInteger manifestId) {
        return repository.getBccpNodeByBccpManifestId(user, manifestId);
    }

    @Transactional
    public void deleteAcc(AuthenticatedPrincipal user, BigInteger manifestId) {
        DeleteAccRepositoryRequest repositoryRequest =
                new DeleteAccRepositoryRequest(user, manifestId);

        DeleteAccRepositoryResponse repositoryResponse =
                accWriteRepository.deleteAcc(repositoryRequest);

        fireEvent(new DeletedAccEvent());
    }

    @Transactional
    public void deleteAsccp(AuthenticatedPrincipal user, BigInteger manifestId) {
        DeleteAsccpRepositoryRequest repositoryRequest =
                new DeleteAsccpRepositoryRequest(user, manifestId);

        DeleteAsccpRepositoryResponse repositoryResponse =
                asccpWriteRepository.deleteAsccp(repositoryRequest);

        fireEvent(new DeletedAsccpEvent());
    }

    @Transactional
    public void deleteBccp(AuthenticatedPrincipal user, BigInteger manifestId) {
        DeleteBccpRepositoryRequest repositoryRequest =
                new DeleteBccpRepositoryRequest(user, manifestId);

        DeleteBccpRepositoryResponse repositoryResponse =
                bccpWriteRepository.deleteBccp(repositoryRequest);

        fireEvent(new DeletedBccpEvent());
    }

    @Transactional
    public void deleteAscc(AuthenticatedPrincipal user, BigInteger asccManifestId) {
        DeleteAsccRepositoryRequest request =
                new DeleteAsccRepositoryRequest(user, asccManifestId);

        AsccManifestRecord asccManifestRecord = asccReadRepository.getAsccManifestById(asccManifestId);
        AsccpRecord asccpRecord
                = asccpReadRepository.getAsccpByManifestId(asccManifestRecord.getToAsccpManifestId().toBigInteger());

        asccWriteRepository.deleteAscc(request);

        if (asccpRecord.getType().equals(CcASCCPType.Extension.name())
            && asccpRecord.getPropertyTerm().equals("Extension")) {
            AsccpManifestRecord asccpManifestRecord
                    = asccpReadRepository.getAsccpManifestById(asccManifestRecord.getToAsccpManifestId().toBigInteger());

            BigInteger asccpManifestId = asccpManifestRecord.getAsccpManifestId().toBigInteger();
            BigInteger accManifestId = asccpManifestRecord.getRoleOfAccManifestId().toBigInteger();

            DeleteAsccpRepositoryRequest deleteAsccpRepositoryRequest
                    = new DeleteAsccpRepositoryRequest(user, asccpManifestId);
            asccpWriteRepository.removeAsccp(deleteAsccpRepositoryRequest);

            DeleteAccRepositoryRequest deleteAccRepositoryRequest
                    = new DeleteAccRepositoryRequest(user, accManifestId);
            accWriteRepository.removeAcc(deleteAccRepositoryRequest);
        }

        fireEvent(new DeletedAsccEvent());
    }

    @Transactional
    public void deleteBcc(AuthenticatedPrincipal user, BigInteger bccManifestId) {
        DeleteBccRepositoryRequest request =
                new DeleteBccRepositoryRequest(user, bccManifestId);

        bccWriteRepository.deleteBcc(request);

        fireEvent(new DeletedBccEvent());
    }

    public CcAccNodeDetail getAccNodeDetail(AuthenticatedPrincipal user, CcAccNode accNode) {
        return repository.getAccNodeDetail(user, accNode);
    }

    public CcAsccpNodeDetail getAsccpNodeDetail(AuthenticatedPrincipal user, CcAsccpNode asccpNode) {
        return repository.getAsccpNodeDetail(user, asccpNode);
    }

    public CcBccpNodeDetail getBccpNodeDetail(AuthenticatedPrincipal user, CcBccpNode bccpNode) {
        return repository.getBccpNodeDetail(user, bccpNode);
    }

    public CcBdtScNodeDetail getBdtScNodeDetail(AuthenticatedPrincipal user, CcBdtScNode bdtScNode) {
        return repository.getBdtScNodeDetail(user, bdtScNode);
    }

    public CcAsccpNodeDetail.Asccp getAsccp(BigInteger asccpId) {
        return repository.getAsccp(asccpId);
    }

    @Transactional
    public BigInteger createAcc(AuthenticatedPrincipal user, CcAccCreateRequest request) {
        CreateAccRepositoryRequest repositoryRequest =
                new CreateAccRepositoryRequest(user, request.getReleaseId());

        CreateAccRepositoryResponse repositoryResponse =
                accWriteRepository.createAcc(repositoryRequest);

        fireEvent(new CreatedAccEvent());

        return repositoryResponse.getAccManifestId();
    }

    @Transactional
    public BigInteger createAsccp(AuthenticatedPrincipal user, CcAsccpCreateRequest request) {
        CreateAsccpRepositoryRequest repositoryRequest =
                new CreateAsccpRepositoryRequest(user,
                        request.getRoleOfAccManifestId(), request.getReleaseId());

        if (request.getAsccpType() != null) {
            repositoryRequest.setInitialType(CcASCCPType.valueOf(request.getAsccpType()));
        }

        CreateAsccpRepositoryResponse repositoryResponse =
                asccpWriteRepository.createAsccp(repositoryRequest);

        fireEvent(new CreatedAsccpEvent());

        return repositoryResponse.getAsccpManifestId();
    }

    @Transactional
    public BigInteger createBccp(AuthenticatedPrincipal user, CcBccpCreateRequest request) {
        CreateBccpRepositoryRequest repositoryRequest =
                new CreateBccpRepositoryRequest(user,
                        request.getBdtManifestId(), request.getReleaseId());

        CreateBccpRepositoryResponse repositoryResponse =
                bccpWriteRepository.createBccp(repositoryRequest);

        fireEvent(new CreatedBccpEvent());

        return repositoryResponse.getBccpManifestId();
    }

    @Transactional
    public BigInteger createAccExtension(AuthenticatedPrincipal user, CcExtensionCreateRequest request) {
        LocalDateTime timestamp = LocalDateTime.now();
        AccManifestRecord accManifestRecord = accReadRepository.getAccManifest(request.getAccManifestId());
        BigInteger releaseId = accManifestRecord.getReleaseId().toBigInteger();
        AccRecord accRecord = accReadRepository.getAccByManifestId(request.getAccManifestId());

        AccManifestRecord allExtension
                = accReadRepository.getAllExtensionAccManifest(releaseId);

        // create extension ACC
        CreateAccRepositoryRequest createAccRepositoryRequest
                = new CreateAccRepositoryRequest(user, timestamp, releaseId);

        createAccRepositoryRequest.setInitialComponentType(OagisComponentType.Extension);
        createAccRepositoryRequest.setInitialType(CcACCType.Extension);
        createAccRepositoryRequest.setInitialObjectClassTerm(accRecord.getObjectClassTerm() + " Extension");
        createAccRepositoryRequest.setBasedAccManifestId(allExtension.getAccManifestId().toBigInteger());
        if (accRecord.getNamespaceId() != null) {
            createAccRepositoryRequest.setNamespaceId(accRecord.getNamespaceId().toBigInteger());
        }

        CreateAccRepositoryResponse createAccRepositoryResponse
                = accWriteRepository.createAcc(createAccRepositoryRequest);

        BigInteger extensionAccManifestId = createAccRepositoryResponse.getAccManifestId();

        // create extension ASCCP
        CreateAsccpRepositoryRequest createAsccpRepositoryRequest
                = new CreateAsccpRepositoryRequest(user, timestamp, extensionAccManifestId, releaseId);

        String extensionAsccpDefintion = "Allows the user of OAGIS to extend the specification in order to " +
                "provide additional information that is not captured in OAGIS.";
        String extensionAsccpDefintionSource = "http://www.openapplications.org/oagis/10/platform/2";
        createAsccpRepositoryRequest.setInitialPropertyTerm("Extension");
        createAsccpRepositoryRequest.setInitialType(CcASCCPType.Extension);
        createAsccpRepositoryRequest.setDefinition(extensionAsccpDefintion);
        createAsccpRepositoryRequest.setDefinitionSoruce(extensionAsccpDefintionSource);
        createAsccpRepositoryRequest.setReusable(false);

        CreateAsccpRepositoryResponse createAsccpRepositoryResponse
                = asccpWriteRepository.createAsccp(createAsccpRepositoryRequest);
        BigInteger extensionAsccpManifestId = createAsccpRepositoryResponse.getAsccpManifestId();

        // create ASCC between extension ACC and extension ASCCP
        CreateAsccRepositoryRequest createAsccRepositoryRequest
                = new CreateAsccRepositoryRequest(user, timestamp, releaseId,
                request.getAccManifestId(), extensionAsccpManifestId);

        asccWriteRepository.createAscc(createAsccRepositoryRequest);

        return request.getAccManifestId();
    }

    private void updateExtensionComponentProperties(AuthenticatedPrincipal user, CcAccNodeDetail accNodeDetail) {
        LocalDateTime timestamp = LocalDateTime.now();
        AccManifestRecord accManifestRecord = accReadRepository.getAccManifest(accNodeDetail.getManifestId());
        AccRecord accRecord = accReadRepository.getAccByManifestId(accManifestRecord.getAccManifestId().toBigInteger());

        AsccpManifestRecord extensionManifestAsccp = asccpReadRepository.getExtensionAsccpManifestByAccManifestId(
                accManifestRecord.getAccManifestId().toBigInteger()
        );
        AsccpRecord extensionAsccp
                = asccpReadRepository.getAsccpByManifestId(extensionManifestAsccp.getAsccpManifestId().toBigInteger());

        AccManifestRecord extensionAcc
                = accReadRepository.getAccManifest(extensionManifestAsccp.getRoleOfAccManifestId().toBigInteger());

        // update extension ACC
        UpdateAccPropertiesRepositoryRequest updateAccPropertiesRepositoryRequest
                = new UpdateAccPropertiesRepositoryRequest(
                        user,
                        timestamp,
                        extensionAcc.getAccManifestId().toBigInteger());

        updateAccPropertiesRepositoryRequest.setObjectClassTerm(accRecord.getObjectClassTerm() + " Extension");
        if (accRecord.getNamespaceId() != null) {
            updateAccPropertiesRepositoryRequest.setNamespaceId(accRecord.getNamespaceId().toBigInteger());
        }
        accWriteRepository.updateAccProperties(updateAccPropertiesRepositoryRequest);

        // update extension ASCCP
        UpdateAsccpPropertiesRepositoryRequest updateAsccpPropertiesRepositoryRequest
                = new UpdateAsccpPropertiesRepositoryRequest(
                user,
                timestamp,
                extensionManifestAsccp.getAsccpManifestId().toBigInteger());

        updateAsccpPropertiesRepositoryRequest.setPropertyTerm(extensionAsccp.getPropertyTerm());
        updateAsccpPropertiesRepositoryRequest.setDefinition(extensionAsccp.getDefinition());
        updateAsccpPropertiesRepositoryRequest.setDefinitionSource(extensionAsccp.getDefinitionSource());

        if (accRecord.getNamespaceId() != null) {
            updateAsccpPropertiesRepositoryRequest.setNamespaceId(accRecord.getNamespaceId().toBigInteger());
        }

        asccpWriteRepository.updateAsccpProperties(updateAsccpPropertiesRepositoryRequest);
    }

    private void updateUserExtensionNamespace(AuthenticatedPrincipal user, CcAccNodeDetail accNodeDetail) {
        LocalDateTime timestamp = LocalDateTime.now();
        AccManifestRecord accManifestRecord = accReadRepository.getAccManifest(accNodeDetail.getManifestId());
        AccRecord accRecord = accReadRepository.getAccByManifestId(accManifestRecord.getAccManifestId().toBigInteger());

        AsccpManifestRecord extensionManifestAsccp = asccpReadRepository.getUserExtensionAsccpManifestByAccManifestId(
                accManifestRecord.getAccManifestId().toBigInteger()
        );

        // update extension ASCCP
        UpdateAsccpPropertiesRepositoryRequest updateAsccpPropertiesRepositoryRequest
                = new UpdateAsccpPropertiesRepositoryRequest(
                user,
                timestamp,
                extensionManifestAsccp.getAsccpManifestId().toBigInteger());

        if (accRecord.getNamespaceId() != null) {
            updateAsccpPropertiesRepositoryRequest.setNamespaceId(accRecord.getNamespaceId().toBigInteger());
        }
        asccpWriteRepository.updateAsccpNamespace(updateAsccpPropertiesRepositoryRequest);
    }

    private void updateExtensionComponentState(AuthenticatedPrincipal user, BigInteger accManifestId, CcState fromState, CcState toState) {
        LocalDateTime timestamp = LocalDateTime.now();
        AccManifestRecord accManifestRecord = accReadRepository.getAccManifest(accManifestId);

        AsccpManifestRecord extensionAsccp = asccpReadRepository.getExtensionAsccpManifestByAccManifestId(
                accManifestRecord.getAccManifestId().toBigInteger()
        );

        AccManifestRecord extensionAcc
                = accReadRepository.getAccManifest(extensionAsccp.getRoleOfAccManifestId().toBigInteger());

        // update extension ACC
        UpdateAccStateRepositoryRequest updateAccStateRepositoryRequest
                = new UpdateAccStateRepositoryRequest(
                user,
                timestamp,
                extensionAcc.getAccManifestId().toBigInteger(),
                fromState,
                toState);

        accWriteRepository.updateAccState(updateAccStateRepositoryRequest);

        // update extension ASCCP
        UpdateAsccpStateRepositoryRequest updateAsccpStateRepositoryRequest
                = new UpdateAsccpStateRepositoryRequest(
                user,
                timestamp,
                extensionAsccp.getAsccpManifestId().toBigInteger(),
                fromState,
                toState);

        asccpWriteRepository.updateAsccpState(updateAsccpStateRepositoryRequest);
    }

    @Transactional
    public CcUpdateResponse updateCcDetails(AuthenticatedPrincipal user, CcUpdateRequest ccUpdateRequest) {
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
    public void updateCcSeq(AuthenticatedPrincipal user,
                            BigInteger accManifestId,
                            Pair<CcId, CcId> itemAfterPair) {

        UpdateSeqKeyRequest request =
                new UpdateSeqKeyRequest(user, accManifestId, itemAfterPair);

        accWriteRepository.moveSeq(request);
    }

    @Transactional
    public List<CcAccNodeDetail> updateAccDetail(AuthenticatedPrincipal user, List<CcAccNodeDetail> ccAccNodeDetails) {
        LocalDateTime timestamp = LocalDateTime.now();
        List<CcAccNodeDetail> updatedAccNodeDetails = new ArrayList<>();
        for (CcAccNodeDetail detail : ccAccNodeDetails) {
            CcAccNode ccAccNode = updateAccDetail(user, timestamp, detail);
            updatedAccNodeDetails.add(getAccNodeDetail(user, ccAccNode));
            if(hasExtensionAssociation(user, ccAccNode.getManifestId())) {
                updateExtensionComponentProperties(user, detail);
            }
            if(isUserExtensionGroup(user, ccAccNode.getManifestId())) {
                updateUserExtensionNamespace(user, detail);
            }
        }
        return updatedAccNodeDetails;
    }

    private boolean hasExtensionAssociation(AuthenticatedPrincipal user, BigInteger accManifestId) {
        AsccpManifestRecord extension
                = asccpReadRepository.getExtensionAsccpManifestByAccManifestId(accManifestId);
        return extension != null;
    }

    private boolean isUserExtensionGroup(AuthenticatedPrincipal user, BigInteger accManifestId) {
        AccRecord acc
                = accReadRepository.getAccByManifestId(accManifestId);
        return acc.getOagisComponentType().equals(OagisComponentType.UserExtensionGroup.getValue());
    }

    @Transactional
    public List<CcAsccpNodeDetail> updateAsccp(AuthenticatedPrincipal user, List<CcAsccpNodeDetail> asccpNodeDetails) {
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
    public List<CcBccpNodeDetail> updateBccpDetail(AuthenticatedPrincipal user, List<CcBccpNodeDetail> bccpNodeDetails) {
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

    private CcAccNode updateAccDetail(AuthenticatedPrincipal user, LocalDateTime timestamp, CcAccNodeDetail detail) {
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
                accWriteRepository.updateAccProperties(request);

        fireEvent(new UpdatedAccPropertiesEvent());

        return repository.getAccNodeByAccManifestId(user, response.getAccManifestId());
    }

    public CcAsccpNode updateAsccpDetail(AuthenticatedPrincipal user, LocalDateTime timestamp, CcAsccpNodeDetail.Asccp detail) {
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
                asccpWriteRepository.updateAsccpProperties(request);

        fireEvent(new UpdatedAsccpPropertiesEvent());

        return repository.getAsccpNodeByAsccpManifestId(user, response.getAsccpManifestId());
    }

    private void updateAsccDetail(AuthenticatedPrincipal user, LocalDateTime timestamp, CcAsccpNodeDetail.Ascc detail) {
        UpdateAsccPropertiesRepositoryRequest request =
                new UpdateAsccPropertiesRepositoryRequest(user, timestamp, detail.getManifestId());

        request.setCardinalityMin(detail.getCardinalityMin());
        request.setCardinalityMax(detail.getCardinalityMax());
        request.setDefinition(detail.getDefinition());
        request.setDefinitionSource(detail.getDefinitionSource());
        request.setDeprecated(detail.isDeprecated());

        asccWriteRepository.updateAsccProperties(request);

        fireEvent(new UpdatedAsccPropertiesEvent());
    }

    private void updateBccDetail(AuthenticatedPrincipal user, LocalDateTime timestamp, CcBccpNodeDetail.Bcc detail) {
        UpdateBccPropertiesRepositoryRequest request =
                new UpdateBccPropertiesRepositoryRequest(user, timestamp, detail.getManifestId());

        request.setCardinalityMin(detail.getCardinalityMin());
        request.setCardinalityMax(detail.getCardinalityMax());
        request.setDefinition(detail.getDefinition());
        request.setDefinitionSource(detail.getDefinitionSource());
        request.setEntityType(BCCEntityType.valueOf(detail.getEntityType()));
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

        bccWriteRepository.updateBccProperties(request);

        fireEvent(new UpdatedBccPropertiesEvent());
    }

    private CcBccpNode updateBccpDetail(AuthenticatedPrincipal user, LocalDateTime timestamp, CcBccpNodeDetail.Bccp detail) {
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
                bccpWriteRepository.updateBccpProperties(request);

        fireEvent(new UpdatedBccpPropertiesEvent());

        return repository.getBccpNodeByBccpManifestId(user, response.getBccpManifestId());
    }

    @Transactional
    public BigInteger appendAsccp(AuthenticatedPrincipal user, BigInteger releaseId,
                                  BigInteger accManifestId, BigInteger asccpManifestId,
                                  int pos) {
        LocalDateTime timestamp = LocalDateTime.now();
        CreateAsccRepositoryRequest request =
                new CreateAsccRepositoryRequest(user, timestamp, releaseId, accManifestId, asccpManifestId);
        request.setPos(pos);

        CreateAsccRepositoryResponse response = asccWriteRepository.createAscc(request);
        fireEvent(new CreatedAsccEvent());
        return response.getAsccManifestId();
    }

    @Transactional
    public BigInteger appendBccp(AuthenticatedPrincipal user, BigInteger releaseId,
                                 BigInteger accManifestId, BigInteger bccpManifestId,
                                 int pos) {
        LocalDateTime timestamp = LocalDateTime.now();
        CreateBccRepositoryRequest request =
                new CreateBccRepositoryRequest(user, timestamp, releaseId, accManifestId, bccpManifestId);
        request.setPos(pos);

        CreateBccRepositoryResponse response = bccWriteRepository.createBcc(request);
        fireEvent(new CreatedBccEvent());
        return response.getBccManifestId();
    }

    @Transactional
    public BigInteger updateAccBasedAcc(AuthenticatedPrincipal user, BigInteger accManifestId, BigInteger basedAccManifestId) {
        UpdateAccBasedAccRepositoryRequest repositoryRequest =
                new UpdateAccBasedAccRepositoryRequest(user, accManifestId, basedAccManifestId);

        UpdateAccBasedAccRepositoryResponse repositoryResponse =
                accWriteRepository.updateAccBasedAcc(repositoryRequest);

        fireEvent(new UpdatedAccBasedAccEvent());

        return repositoryResponse.getAccManifestId();
    }

    @Transactional
    public BigInteger updateAsccpRoleOfAcc(AuthenticatedPrincipal user, BigInteger asccpManifestId, BigInteger roleOfAccManifestId) {
        UpdateAsccpRoleOfAccRepositoryRequest repositoryRequest =
                new UpdateAsccpRoleOfAccRepositoryRequest(user, asccpManifestId, roleOfAccManifestId);

        UpdateAsccpRoleOfAccRepositoryResponse repositoryResponse =
                asccpWriteRepository.updateAsccpBdt(repositoryRequest);

        fireEvent(new UpdatedAsccpRoleOfAccEvent());

        return repositoryResponse.getAsccpManifestId();
    }

    @Transactional
    public BigInteger updateBccpBdt(AuthenticatedPrincipal user, BigInteger bccpManifestId, BigInteger bdtManifestId) {
        UpdateBccpBdtRepositoryRequest repositoryRequest =
                new UpdateBccpBdtRepositoryRequest(user, bccpManifestId, bdtManifestId);

        UpdateBccpBdtRepositoryResponse repositoryResponse =
                bccpWriteRepository.updateBccpBdt(repositoryRequest);

        fireEvent(new UpdatedBccpBdtEvent());

        return repositoryResponse.getBccpManifestId();
    }

    @Transactional
    public BigInteger updateAccState(AuthenticatedPrincipal user, BigInteger accManifestId, CcState toState) {
        CcState fromState = repository.getAccState(accManifestId);
        return updateAccState(user, accManifestId, fromState, toState);
    }

    @Transactional
    public BigInteger updateAccState(AuthenticatedPrincipal user, BigInteger accManifestId, CcState fromState, CcState toState) {
        UpdateAccStateRepositoryRequest repositoryRequest =
                new UpdateAccStateRepositoryRequest(user, accManifestId, fromState, toState);

        UpdateAccStateRepositoryResponse repositoryResponse =
                accWriteRepository.updateAccState(repositoryRequest);

//        if (hasExtensionAssociation(user, accManifestId)) {
//            updateExtensionComponentState(user, accManifestId, fromState, toState);
//        }

        fireEvent(new UpdatedAccStateEvent());

        return repositoryResponse.getAccManifestId();
    }

    @Transactional
    public BigInteger updateAsccpState(AuthenticatedPrincipal user, BigInteger asccpManifestId, CcState toState) {
        CcState fromState = repository.getAsccpState(asccpManifestId);
        return updateAsccpState(user, asccpManifestId, fromState, toState);
    }

    @Transactional
    public BigInteger updateAsccpState(AuthenticatedPrincipal user, BigInteger asccpManifestId, CcState fromState, CcState toState) {
        UpdateAsccpStateRepositoryRequest repositoryRequest =
                new UpdateAsccpStateRepositoryRequest(user, asccpManifestId, fromState, toState);

        UpdateAsccpStateRepositoryResponse repositoryResponse =
                asccpWriteRepository.updateAsccpState(repositoryRequest);

        fireEvent(new UpdatedAsccpStateEvent());

        return repositoryResponse.getAsccpManifestId();
    }

    @Transactional
    public BigInteger updateBccpState(AuthenticatedPrincipal user, BigInteger bccpManifestId, CcState toState) {
        CcState fromState = repository.getBccpState(bccpManifestId);
        return updateBccpState(user, bccpManifestId, fromState, toState);
    }

    @Transactional
    public BigInteger updateBccpState(AuthenticatedPrincipal user, BigInteger bccpManifestId, CcState fromState, CcState toState) {
        UpdateBccpStateRepositoryRequest repositoryRequest =
                new UpdateBccpStateRepositoryRequest(user, bccpManifestId, fromState, toState);

        UpdateBccpStateRepositoryResponse repositoryResponse =
                bccpWriteRepository.updateBccpState(repositoryRequest);

        fireEvent(new UpdatedBccpStateEvent());

        return repositoryResponse.getBccpManifestId();
    }

    @Transactional
    public BigInteger makeNewRevisionForAcc(AuthenticatedPrincipal user, BigInteger accManifestId) {
        ReviseAccRepositoryRequest repositoryRequest =
                new ReviseAccRepositoryRequest(user, accManifestId);

        ReviseAccRepositoryResponse repositoryResponse =
                accWriteRepository.reviseAcc(repositoryRequest);

        fireEvent(new RevisedAccEvent());

        return repositoryResponse.getAccManifestId();
    }

    @Transactional
    public BigInteger makeNewRevisionForAsccp(AuthenticatedPrincipal user, BigInteger asccpManifestId) {
        ReviseAsccpRepositoryRequest repositoryRequest =
                new ReviseAsccpRepositoryRequest(user, asccpManifestId);

        ReviseAsccpRepositoryResponse repositoryResponse =
                asccpWriteRepository.reviseAsccp(repositoryRequest);

        fireEvent(new RevisedAsccpEvent());

        return repositoryResponse.getAsccpManifestId();
    }

    @Transactional
    public BigInteger makeNewRevisionForBccp(AuthenticatedPrincipal user, BigInteger bccpManifestId) {
        ReviseBccpRepositoryRequest repositoryRequest =
                new ReviseBccpRepositoryRequest(user, bccpManifestId);

        ReviseBccpRepositoryResponse repositoryResponse =
                bccpWriteRepository.reviseBccp(repositoryRequest);

        fireEvent(new RevisedBccpEvent());

        return repositoryResponse.getBccpManifestId();
    }

    @Transactional
    public CcAccNode discardAccBasedId(AuthenticatedPrincipal user, BigInteger accManifestId) {

        LocalDateTime timestamp = LocalDateTime.now();
        ULong userId = ULong.valueOf(sessionService.userId(user));

        AccManifestRecord accManifestRecord = ccRepository.getAccManifestByManifestId(ULong.valueOf(accManifestId));
        AccRecord accRecord = ccRepository.getAccById(accManifestRecord.getAccId());

        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(userId)
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Modified)
                .setReference(CcType.ACC.name() + "-" + accManifestRecord.getAccManifestId())
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

    public CcRevisionResponse getAccNodeRevision(AuthenticatedPrincipal user, BigInteger manifestId) {
        CcAccNode accNode = getAccNode(user, manifestId);
        BigInteger lastPublishedCcId = getLastPublishedCcId(accNode.getAccId(), CcType.ACC);
        CcRevisionResponse ccRevisionResponse = new CcRevisionResponse();
        if (lastPublishedCcId != null) {
            AccRecord accRecord = ccRepository.getAccById(ULong.valueOf(lastPublishedCcId));
            ccRevisionResponse.setCcId(accRecord.getAccId().longValue());
            ccRevisionResponse.setType(CcType.ACC.toString());
            ccRevisionResponse.setIsDeprecated(accRecord.getIsDeprecated() == 1);
            ccRevisionResponse.setIsAbstract(accRecord.getIsAbstract() == 1);
            ccRevisionResponse.setName(accRecord.getObjectClassTerm());
            ccRevisionResponse.setHasBaseCc(accRecord.getBasedAccId() != null);
            List<AsccManifestRecord> asccManifestRecordList
                    = ccRepository.getAsccManifestByFromAccManifestId(ULong.valueOf(manifestId));
            Map<String, CcNode> associations = new HashMap<>();
            for (AsccManifestRecord asccManifestRecord : asccManifestRecordList) {
                BigInteger lastAsccId = getLastPublishedCcId(asccManifestRecord.getAsccId().toBigInteger(), CcType.ASCC);
                if (lastAsccId != null) {
                    CcAsccNode ascc = new CcAsccNode();
                    AsccRecord asccRecord = ccRepository.getAsccById(asccManifestRecord.getAsccId());
                    ascc.setAsccId(asccRecord.getAsccId().toBigInteger());
                    ascc.setCardinalityMin(BigInteger.valueOf(asccRecord.getCardinalityMin()));
                    ascc.setCardinalityMax(BigInteger.valueOf(asccRecord.getCardinalityMax()));
                    ascc.setDeprecated(asccRecord.getIsDeprecated() == 1);
                    associations.put(CcType.ASCCP.toString() + "-" + asccManifestRecord.getToAsccpManifestId(), ascc);
                }
            }
            List<BccManifestRecord> bccManifestRecordList
                    = ccRepository.getBccManifestByFromAccManifestId(ULong.valueOf(manifestId));
            for (BccManifestRecord bccManifestRecord : bccManifestRecordList) {
                BigInteger lastBccId = getLastPublishedCcId(bccManifestRecord.getBccId().toBigInteger(), CcType.BCC);
                if (lastBccId != null) {
                    CcBccNode bcc = new CcBccNode();
                    BccRecord bccRecord = ccRepository.getBccById(bccManifestRecord.getBccId());
                    bcc.setBccId(bccRecord.getBccId().toBigInteger());
                    bcc.setCardinalityMin(BigInteger.valueOf(bccRecord.getCardinalityMin()));
                    bcc.setCardinalityMax(BigInteger.valueOf(bccRecord.getCardinalityMax()));
                    bcc.setEntityType(BCCEntityType.valueOf(bccRecord.getEntityType()));
                    bcc.setDeprecated(bccRecord.getIsDeprecated() == 1);
                    associations.put(CcType.BCCP.toString() + "-" + bccManifestRecord.getToBccpManifestId(), bcc);
                }
            }
            ccRevisionResponse.setAssociations(associations);
        }
        return ccRevisionResponse;
    }

    public CcRevisionResponse getBccpNodeRevision(AuthenticatedPrincipal user, BigInteger manifestId) {
        CcBccpNode bccpNode = getBccpNode(user, manifestId);
        BigInteger lastPublishedCcId = getLastPublishedCcId(bccpNode.getBccpId(), CcType.BCCP);
        CcRevisionResponse ccRevisionResponse = new CcRevisionResponse();
        if (lastPublishedCcId != null) {
            BccpRecord bccpRecord = ccRepository.getBccpById(ULong.valueOf(lastPublishedCcId));
            ccRevisionResponse.setCcId(bccpRecord.getBccpId().longValue());
            ccRevisionResponse.setType(CcType.BCCP.toString());
            ccRevisionResponse.setIsDeprecated(bccpRecord.getIsDeprecated() == 1);
            ccRevisionResponse.setIsNillable(bccpRecord.getIsNillable() == 1);
            ccRevisionResponse.setName(bccpRecord.getPropertyTerm());
            ccRevisionResponse.setFixedValue(bccpRecord.getFixedValue());
            ccRevisionResponse.setHasBaseCc(bccpRecord.getBdtId() != null);
        }
        return ccRevisionResponse;
    }

    public CcRevisionResponse getAsccpNodeRevision(AuthenticatedPrincipal user, BigInteger manifestId) {
        CcAsccpNode asccpNode = getAsccpNode(user, manifestId);
        BigInteger lastPublishedCcId = getLastPublishedCcId(asccpNode.getAsccpId(), CcType.ASCCP);
        CcRevisionResponse ccRevisionResponse = new CcRevisionResponse();
        if (lastPublishedCcId != null) {
            AsccpRecord asccpRecord = ccRepository.getAsccpById(ULong.valueOf(lastPublishedCcId));
            ccRevisionResponse.setCcId(asccpRecord.getAsccpId().longValue());
            ccRevisionResponse.setType(CcType.ASCCP.toString());
            ccRevisionResponse.setIsDeprecated(asccpRecord.getIsDeprecated() == 1);
            ccRevisionResponse.setIsNillable(asccpRecord.getIsNillable() == 1);
            ccRevisionResponse.setIsReusable(asccpRecord.getReusableIndicator() == 1);
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
                if (accRecord.getState().equals(CcState.Published.name()) ||
                        accRecord.getState().equals(CcState.Production.name())) {
                    return ccId;
                }
                if (accRecord.getPrevAccId() == null) {
                    return null;
                }
                return getLastPublishedCcId(accRecord.getPrevAccId().toBigInteger(), CcType.ACC);
            case ASCC:
                AsccRecord asccRecord = ccRepository.getAsccById(ULong.valueOf(ccId));
                if (asccRecord.getState().equals(CcState.Published.name()) ||
                        asccRecord.getState().equals(CcState.Production.name())) {
                    return ccId;
                }
                if (asccRecord.getPrevAsccId() == null) {
                    return null;
                }
                return getLastPublishedCcId(asccRecord.getPrevAsccId().toBigInteger(), CcType.ASCC);
            case BCC:
                BccRecord bccRecord = ccRepository.getBccById(ULong.valueOf(ccId));
                if (bccRecord.getState().equals(CcState.Published.name()) ||
                        bccRecord.getState().equals(CcState.Production.name())) {
                    return ccId;
                }
                if (bccRecord.getPrevBccId() == null) {
                    return null;
                }
                return getLastPublishedCcId(bccRecord.getPrevBccId().toBigInteger(), CcType.BCC);
            case ASCCP:
                AsccpRecord asccpRecord = ccRepository.getAsccpById(ULong.valueOf(ccId));
                if (asccpRecord.getState().equals(CcState.Published.name()) ||
                        asccpRecord.getState().equals(CcState.Production.name())) {
                    return ccId;
                }
                if (asccpRecord.getPrevAsccpId() == null) {
                    return null;
                }
                return getLastPublishedCcId(asccpRecord.getPrevAsccpId().toBigInteger(), CcType.ASCCP);
            case BCCP:
                BccpRecord bccpRecord = ccRepository.getBccpById(ULong.valueOf(ccId));
                if (bccpRecord.getState().equals(CcState.Published.name()) ||
                        bccpRecord.getState().equals(CcState.Production.name())) {
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

    public void updateAccOwnerUserId(AuthenticatedPrincipal user, BigInteger accManifestId, BigInteger ownerUserId) {
        UpdateAccOwnerRepositoryRequest request =
                new UpdateAccOwnerRepositoryRequest(user, accManifestId, ownerUserId);
        accWriteRepository.updateAccOwner(request);

        fireEvent(new UpdatedAccOwnerEvent());
    }

    public void updateAsccpOwnerUserId(AuthenticatedPrincipal user, BigInteger asccpManifestId, BigInteger ownerUserId) {
        UpdateAsccpOwnerRepositoryRequest request =
                new UpdateAsccpOwnerRepositoryRequest(user, asccpManifestId, ownerUserId);
        asccpWriteRepository.updateAsccpOwner(request);

        fireEvent(new UpdatedAsccpOwnerEvent());
    }

    public void updateBccpOwnerUserId(AuthenticatedPrincipal user, BigInteger bccpManifestId, BigInteger ownerUserId) {
        UpdateBccpOwnerRepositoryRequest request =
                new UpdateBccpOwnerRepositoryRequest(user, bccpManifestId, ownerUserId);
        bccpWriteRepository.updateBccpOwner(request);

        fireEvent(new UpdatedBccpOwnerEvent());
    }

    @Transactional
    public void discardRevisionBccp(AuthenticatedPrincipal user, BigInteger bccpManifestId) {
        DiscardRevisionBccpRepositoryRequest request
                = new DiscardRevisionBccpRepositoryRequest(user, bccpManifestId);
        bccpWriteRepository.discardRevisionBccp(request);

        fireEvent(new DiscardRevisionBccpEvent());
    }

    @Transactional
    public void discardRevisionAsccp(AuthenticatedPrincipal user, BigInteger asccpManifestId) {
        DiscardRevisionAsccpRepositoryRequest request
                = new DiscardRevisionAsccpRepositoryRequest(user, asccpManifestId);
        asccpWriteRepository.discardRevisionAsccp(request);

        fireEvent(new DiscardRevisionAsccpEvent());
    }

    @Transactional
    public void discardRevisionAcc(AuthenticatedPrincipal user, BigInteger accManifestId) {
        DiscardRevisionAccRepositoryRequest request
                = new DiscardRevisionAccRepositoryRequest(user, accManifestId);
        accWriteRepository.discardRevisionAcc(request);

        fireEvent(new DiscardRevisionAccEvent());
    }

    @Transactional
    public CreateOagisBodResponse createOagisBod(AuthenticatedPrincipal user,
                                                 CreateOagisBodRequest request) {

        CreateOagisBodResponse response = new CreateOagisBodResponse();

        // TODO

        return response;
    }

    @Transactional
    public CreateOagisVerbResponse createBod(AuthenticatedPrincipal user,
                                             CreateOagisVerbRequest request) {

        CreateOagisVerbResponse response = new CreateOagisVerbResponse();

        // TODO

        return response;
    }
}

