package org.oagi.srt.gateway.http.api.cc_management.service;

import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.data.Release;
import org.oagi.srt.data.RevisionAction;
import org.oagi.srt.entity.jooq.tables.AsccManifest;
import org.oagi.srt.entity.jooq.tables.AsccpManifest;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.gateway.http.api.cc_management.data.*;
import org.oagi.srt.gateway.http.api.cc_management.data.node.*;
import org.oagi.srt.gateway.http.api.cc_management.repository.CcNodeRepository;
import org.oagi.srt.gateway.http.api.cc_management.repository.ManifestRepository;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.repo.CoreComponentRepository;
import org.oagi.srt.repo.RevisionRepository;
import org.oagi.srt.repo.RevisionRepository.InsertRevisionArguments;
import org.oagi.srt.repo.cc_arguments.*;
import org.oagi.srt.repository.ReleaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.oagi.srt.data.BCCEntityType.Element;
import static org.oagi.srt.gateway.http.api.cc_management.data.CcType.*;

@Service
@Transactional(readOnly = true)
public class CcNodeService {

    @Autowired
    private CcNodeRepository repository;

    @Autowired
    private CoreComponentRepository ccRepository;

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

    public CcAccNode getAccNode(User user, long manifestId) {
        AccManifestRecord accManifestRecord =
                manifestRepository.getAccManifestById(ULong.valueOf(manifestId));
        return repository.getAccNodeByAccManifestId(user, accManifestRecord.getAccManifestId());
    }

    public CcAsccpNode getAsccpNode(User user, long manifestId) {
        return repository.getAsccpNodeByAsccpManifestId(user, manifestId);
    }

    public CcBccpNode getBccpNode(User user, long manifestId) {
        return repository.getBccpNodeByBccpManifestId(user, manifestId);
    }

    @Transactional
    public void deleteAcc(User user, long manifestId) {
        ULong userId = ULong.valueOf(sessionService.userId(user));
        LocalDateTime timestamp = LocalDateTime.now();
        AccManifestRecord accManifestRecord = ccRepository.getAccManifestByManifestId(ULong.valueOf(manifestId));
        AccRecord accRecord = ccRepository.getAccById(accManifestRecord.getAccId());
        RevisionRecord revisionRecord = revisionRepository.getRevisionById(accRecord.getRevisionId());
        if (!revisionRecord.getRevisionNum().equals(UInteger.valueOf(1))) {
            throw new IllegalArgumentException("The target ACC can not be Delete.");
        }

        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(userId)
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Deleted)
                .setReference("acc" + accManifestRecord.getAccManifestId())
                .setPrevRevisionId(accRecord.getRevisionId())
                .execute();

        ULong accId = ccRepository.updateAccArguments(accRecord)
                .setState(CcState.Deleted)
                .setRevisionId(revisionId)
                .execute();

        ccRepository.updateAccManifestArguments(accManifestRecord)
                .setAccId(accId)
                .execute();

        updateAccChain(userId, accManifestRecord.getAccManifestId(), timestamp, revisionId);
    }

    @Transactional
    public void deleteAsccp(User user, long manifestId) {
        ULong userId = ULong.valueOf(sessionService.userId(user));
        LocalDateTime timestamp = LocalDateTime.now();
        AsccpManifestRecord asccpManifestRecord = ccRepository.getAsccpManifestByManifestId(ULong.valueOf(manifestId));
        AsccpRecord asccpRecord = ccRepository.getAsccpById(asccpManifestRecord.getAsccpId());
        RevisionRecord revisionRecord = revisionRepository.getRevisionById(asccpRecord.getRevisionId());
        if (!revisionRecord.getRevisionNum().equals(UInteger.valueOf(1))) {
            throw new IllegalArgumentException("The target ASCCP can not be Delete.");
        }

        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(userId)
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Deleted)
                .setReference("asccp" + asccpManifestRecord.getAsccpManifestId())
                .setPrevRevisionId(asccpRecord.getRevisionId())
                .execute();

        ULong asccpId = ccRepository.updateAsccpArguments(asccpRecord)
                .setState(CcState.Deleted)
                .setRevisionId(revisionId)
                .execute();

        ccRepository.updateAsccpManifestArguments(asccpManifestRecord)
                .setAsccpId(asccpId)
                .execute();
        
        updateAsccByToAsccp(userId, asccpManifestRecord.getAsccpManifestId(), timestamp, revisionId);
    }

    @Transactional
    public void deleteBccp(User user, long manifestId) {
        ULong userId = ULong.valueOf(sessionService.userId(user));
        LocalDateTime timestamp = LocalDateTime.now();
        BccpManifestRecord bccpManifestRecord = ccRepository.getBccpManifestByManifestId(ULong.valueOf(manifestId));
        BccpRecord bccpRecord = ccRepository.getBccpById(bccpManifestRecord.getBccpId());
        RevisionRecord revisionRecord = revisionRepository.getRevisionById(bccpRecord.getRevisionId());
        if (!revisionRecord.getRevisionNum().equals(UInteger.valueOf(1))) {
            throw new IllegalArgumentException("The target BCCP can not be Delete.");
        }

        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(userId)
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Deleted)
                .setReference("bccp" + bccpManifestRecord.getBccpManifestId())
                .setPrevRevisionId(bccpRecord.getRevisionId())
                .execute();

        ULong bccpId = ccRepository.updateBccpArguments(bccpRecord)
                .setState(CcState.Deleted)
                .setRevisionId(revisionId)
                .execute();

        ccRepository.updateBccpManifestArguments(bccpManifestRecord)
                .setBccpId(bccpId)
                .execute();

        updateBccByToBccp(userId, bccpManifestRecord.getBccpManifestId(), timestamp, revisionId);
    }

    @Transactional
    public void deleteAscc(User user, long asccManifestId) {
        long userId = sessionService.userId(user);
        LocalDateTime timestamp = LocalDateTime.now();
        AsccManifestRecord asccManifestRecord = ccRepository.getAsccManifestByManifestId(ULong.valueOf(asccManifestId));
        AsccRecord asccRecord = ccRepository.getAsccById(asccManifestRecord.getAsccId());
        AccManifestRecord accManifestRecord = ccRepository.getAccManifestByManifestId(asccManifestRecord.getFromAccManifestId());
        AccRecord accRecord = ccRepository.getAccById(accManifestRecord.getAccId());
        RevisionRecord revisionRecord = revisionRepository.getRevisionById(accRecord.getRevisionId());
        if (!revisionRecord.getRevisionNum().equals(UInteger.valueOf(1))) {
            throw new IllegalArgumentException("The target ASCC can not be Delete.");
        }

        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(ULong.valueOf(userId))
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Modified)
                .setReference("acc" + accManifestRecord.getAccManifestId())
                .setPrevRevisionId(accRecord.getRevisionId())
                .execute();

        ULong accId = ccRepository.updateAccArguments(accRecord)
                .setRevisionId(revisionId)
                .execute();

        ccRepository.updateAccManifestArguments(accManifestRecord)
                .setAccId(accId)
                .execute();

        updateAccChain(ULong.valueOf(userId), accManifestRecord.getAccManifestId(), timestamp, revisionId);

        ULong asccId = ccRepository.updateAsccArguments(asccRecord)
                .setLastUpdateTimestamp(timestamp)
                .setLastUpdatedBy(ULong.valueOf(userId))
                .setState(CcState.Deleted)
                .setRevisionId(revisionId)
                .execute();

        ccRepository.updateAsccManifestArguments(asccManifestRecord)
                .setAsccId(asccId)
                .execute();

        decreaseSeqKeyGreaterThan(userId, asccManifestRecord.getFromAccManifestId(), asccRecord.getSeqKey(), timestamp, revisionId);
    }

    @Transactional
    public void deleteBcc(User user, long bccManifestId) {
        long userId = sessionService.userId(user);
        LocalDateTime timestamp = LocalDateTime.now();
        BccManifestRecord bccManifestRecord = ccRepository.getBccManifestByManifestId(ULong.valueOf(bccManifestId));
        BccRecord bccRecord = ccRepository.getBccById(bccManifestRecord.getBccId());
        AccManifestRecord accManifestRecord = ccRepository.getAccManifestByManifestId(bccManifestRecord.getFromAccManifestId());
        AccRecord accRecord = ccRepository.getAccById(accManifestRecord.getAccId());
        RevisionRecord revisionRecord = revisionRepository.getRevisionById(accRecord.getRevisionId());
        if (!revisionRecord.getRevisionNum().equals(UInteger.valueOf(1))) {
            throw new IllegalArgumentException("The target Bcc can not be Delete.");
        }

        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(ULong.valueOf(userId))
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Modified)
                .setReference("acc" + accManifestRecord.getAccManifestId())
                .setPrevRevisionId(accRecord.getRevisionId())
                .execute();

        ULong accId = ccRepository.updateAccArguments(accRecord)
                .setRevisionId(revisionId)
                .execute();

        ccRepository.updateAccManifestArguments(accManifestRecord)
                .setAccId(accId)
                .execute();

        updateAccChain(ULong.valueOf(userId), accManifestRecord.getAccManifestId(), timestamp, revisionId);

        ULong bccId = ccRepository.updateBccArguments(bccRecord)
                .setLastUpdateTimestamp(timestamp)
                .setLastUpdatedBy(ULong.valueOf(userId))
                .setState(CcState.Deleted)
                .setRevisionId(revisionId)
                .execute();

        ccRepository.updateBccManifestArguments(bccManifestRecord)
                .setBccId(bccId)
                .execute();

        decreaseSeqKeyGreaterThan(userId, bccManifestRecord.getFromAccManifestId(), bccRecord.getSeqKey(), timestamp, revisionId);
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

    public CcAsccpNodeDetail.Asccp getAsccp(long asccpId) {
        return repository.getAsccp(asccpId);
    }

    @Transactional
    public long createAcc(User user, CcAccCreateRequest request) {
        ULong userId = ULong.valueOf(sessionService.userId(user));
        LocalDateTime timestamp = LocalDateTime.now();
        String defaultObjectClassTerm = "Object Class Term";

        String guid = SrtGuid.randomGuid();
        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(userId)
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Added)
                .setReference("Add" + guid)
                .execute();

        ULong accId = ccRepository.insertAccArguments()
                .setGuid(guid)
                .setObjectClassTerm(defaultObjectClassTerm)
                .setDen(defaultObjectClassTerm + ". Details")
                .setOagisComponentType(OagisComponentType.Semantics)
                .setState(CcState.WIP)
                .setRevisionId(revisionId)
                .setDeprecated(false)
                .setAbstract(false)
                .setCreatedBy(userId)
                .setLastUpdatedBy(userId)
                .setOwnerUserId(userId)
                .setCreationTimestamp(timestamp)
                .setLastUpdateTimestamp(timestamp)
                .execute();

        ULong accManifestId = ccRepository.insertAccManifestArguments()
                .setAccId(accId)
                .setReleaseId(ULong.valueOf(request.getReleaseId()))
                .execute();

        revisionRepository.updateRevisionArguments(revisionId)
                .setReference(CcType.ACC.name().toLowerCase() + accManifestId)
                .setAction(CcAction.Created)
                .addContent("ObjectClassTerm", "", defaultObjectClassTerm)
                .addContent("Den", "", defaultObjectClassTerm + ". Details")
                .addContent("OagisComponentType", "", OagisComponentType.Semantics)
                .addContent("Deprecated", "", false)
                .addContent("Abstract", "", false)
                .execute();

        return accManifestId.longValue();
    }

    @Transactional
    public long createAsccp(User user, CcAsccpCreateRequest request) {
        long userId = sessionService.userId(user);
        LocalDateTime timestamp = LocalDateTime.now();
        String defaultPropertyTerm = "Property Term";

        AccManifestRecord accManifestRecord =
                ccRepository.getAccManifestByManifestId(ULong.valueOf(request.getRoleOfAccManifestId()));
        AccRecord accRecord = ccRepository.getAccById(accManifestRecord.getAccId());

        String guid = SrtGuid.randomGuid();
        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(ULong.valueOf(userId))
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Added)
                .setReference("Add" + guid)
                .execute();

        InsertAsccpArguments insertAsccpArguments = ccRepository.insertAsccpArguments()
                .setGuid(guid)
                .setPropertyTerm(defaultPropertyTerm)
                .setRoleOfAccId(accManifestRecord.getAccId())
                .setDen(defaultPropertyTerm + ". " + accRecord.getObjectClassTerm())
                .setState(CcState.WIP)
                .setReuseableIndicator(true)
                .setDeprecated(false)
                .setNillable(false)
                .setNamespaceId(null)
                .setRevisionId(revisionId)
                .setCreatedBy(ULong.valueOf(userId))
                .setLastUpdatedBy(ULong.valueOf(userId))
                .setOwnerUserId(ULong.valueOf(userId))
                .setCreationTimestamp(timestamp)
                .setLastUpdateTimestamp(timestamp);

        ULong asccpId = ccRepository.execute(insertAsccpArguments);

        ULong asccpManifestId = ccRepository.insertAsccpManifest()
                .setAsccpId(asccpId)
                .setRoleOfAccManifestId(accManifestRecord.getAccManifestId())
                .setReleaseId(accManifestRecord.getReleaseId())
                .execute();

        revisionRepository.updateRevisionArguments(revisionId)
                .setReference(CcType.ASCCP.name().toLowerCase() + asccpManifestId)
                .setAction(CcAction.Created)
                .addContent("PropertyTerm", "", defaultPropertyTerm)
                .addContent("Den", "", defaultPropertyTerm + ". " + accRecord.getObjectClassTerm())
                .addContent("RoleOfAcc", "", accRecord.getDen())
                .addContent("Reuseable", "", true)
                .addContent("Nillable", "", false)
                .addContent("Deprecated", "", false)
                .addContent("NamespaceId", "", null)
                .execute();

        return asccpManifestId.longValue();
    }

    @Transactional
    public long createBccp(User user, CcBccpCreateRequest request) {
        ULong userId = ULong.valueOf(sessionService.userId(user));
        LocalDateTime timestamp = LocalDateTime.now();
        String defaultPropertyTerm = "Property Term";

        DtManifestRecord bdtManifest = ccRepository.getBdtManifestByManifestId(
                ULong.valueOf(request.getBdtManifestId()));
        DtRecord bdt = ccRepository.getBdtById(bdtManifest.getDtId());

        String guid = SrtGuid.randomGuid();
        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(userId)
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Added)
                .setReference("Add" + guid)
                .execute();

        ULong bccpId = ccRepository.insertBccpArguments()
                .setGuid(guid)
                .setPropertyTerm(defaultPropertyTerm)
                .setRepresentationTerm(bdt.getDataTypeTerm())
                .setBdtId(bdt.getDtId())
                .setDen(defaultPropertyTerm + ". " + bdt.getDataTypeTerm())
                .setState(CcState.WIP)
                .setDeprecated(false)
                .setNillable(false)
                .setNamespaceId(null)
                .setRevisionId(revisionId)
                .setCreatedBy(userId)
                .setLastUpdatedBy(userId)
                .setOwnerUserId(userId)
                .setCreationTimestamp(timestamp)
                .setLastUpdateTimestamp(timestamp)
                .execute();

        ULong bccpManifestId = ccRepository.insertBccpManifest()
                .setBccpId(bccpId)
                .setBdtManifestId(bdtManifest.getDtManifestId())
                .setReleaseId(ULong.valueOf(request.getReleaseId()))
                .execute();

        revisionRepository.updateRevisionArguments(revisionId)
                .setReference(CcType.BCCP.name().toLowerCase() + bccpManifestId)
                .setAction(CcAction.Created)
                .addContent("PropertyTerm", "", defaultPropertyTerm)
                .addContent("Den", "", defaultPropertyTerm + ". " + bdt.getDen())
                .addContent("Bdt", "", bdt.getDataTypeTerm())
                .addContent("Nillable", "", false)
                .addContent("Deprecated", "", false)
                .addContent("NamespaceId", "", null)
                .execute();

        return bccpManifestId.longValue();
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
            CcAccNodeDetail updatedAccNodeDetail = updateAccDetail(user, timestamp, detail);
            updatedAccNodeDetails.add(updatedAccNodeDetail);

            CcEvent event = new CcEvent();
            event.setAction("UpdateDetail");
            event.addProperty("actor", user.getUsername());
            simpMessagingTemplate.convertAndSend("/topic/acc/" + detail.getManifestId(), event);
        }

        return updatedAccNodeDetails;
    }

    @Transactional
    public List<CcAsccpNodeDetail> updateAsccp(User user, List<CcAsccpNodeDetail> asccpNodeDetails) {
        LocalDateTime timestamp = LocalDateTime.now();
        List<CcAsccpNodeDetail> updatedAsccpNodeDetails = new ArrayList<>();
        for (CcAsccpNodeDetail detail : asccpNodeDetails) {
            CcAsccpNode ccAsccpNode = updateAsccpDetail(user, timestamp, detail.getAsccp());
            updatedAsccpNodeDetails.add(getAsccpNodeDetail(user, ccAsccpNode));
        }
        return updatedAsccpNodeDetails;
    }

    @Transactional
    public List<CcBccpNodeDetail> updateBccpDetail(User user, List<CcBccpNodeDetail> bccpNodeDetails) {
        LocalDateTime timestamp = LocalDateTime.now();

        List<CcBccpNodeDetail> updatedBccpNodeDetails = new ArrayList<>();
        for (CcBccpNodeDetail detail : bccpNodeDetails) {
            CcBccpNode ccBccpNode = updateBccpDetail(user, timestamp, detail.getBccp());
            updatedBccpNodeDetails.add(getBccpNodeDetail(user, ccBccpNode));
        }
        return updatedBccpNodeDetails;
    }

    private CcAccNodeDetail updateAccDetail(User user, LocalDateTime timestamp, CcAccNodeDetail detail) {
        ULong userId = ULong.valueOf(sessionService.userId(user));

        AccManifestRecord accManifestRecord = ccRepository.getAccManifestByManifestId(
                ULong.valueOf(detail.getManifestId()));
        AccRecord accRecord = ccRepository.getAccById(accManifestRecord.getAccId());

        UpdateAccArguments updateAccArguments = ccRepository.updateAccArguments(accRecord);
        InsertRevisionArguments insertRevisionArguments = revisionRepository.insertRevisionArguments();

        if (!accRecord.getObjectClassTerm().equals(detail.getObjectClassTerm())) {
            updateAccArguments.setObjectClassTerm(detail.getObjectClassTerm());
            updateAccArguments.setDen(detail.getObjectClassTerm() + ". Details");
            insertRevisionArguments.addContent("ObjectClassTerm", accRecord.getObjectClassTerm(), updateAccArguments.getObjectClassTerm());
            insertRevisionArguments.addContent("Den", accRecord.getDen(), updateAccArguments.getDen());
        }

        Byte abstracted = (byte) (detail.isAbstracted() ? 1 : 0);
        if (!accRecord.getIsAbstract().equals(abstracted)) {
            updateAccArguments.setAbstract(detail.isAbstracted());
            insertRevisionArguments.addContent("Abstract", accRecord.getIsAbstract() == 1, updateAccArguments.getAbstract());
        }

        Byte deprecated = (byte) (detail.isDeprecated() ? 1 : 0);
        if (!accRecord.getIsDeprecated().equals(deprecated)) {
            updateAccArguments.setDeprecated(detail.isDeprecated());
            insertRevisionArguments.addContent("Deprecated", accRecord.getIsDeprecated() == 1, updateAccArguments.getDeprecated());
        }

        if (!Objects.equals(accRecord.getDefinition(), detail.getDefinition())) {
            updateAccArguments.setDefinition(detail.getDefinition());
            insertRevisionArguments.addContent("Definition", accRecord.getDefinition(), updateAccArguments.getDefinition());
        }

        if (!Objects.equals(accRecord.getDefinitionSource(), detail.getDefinitionSource())) {
            updateAccArguments.setDefinitionSource(detail.getDefinitionSource());
            insertRevisionArguments.addContent("DefinitionSource", accRecord.getDefinitionSource(), updateAccArguments.getDefinitionSource());
        }

        if (!accRecord.getOagisComponentType().equals((int) detail.getOagisComponentType())) {
            insertRevisionArguments.addContent("OagisComponentType",
                    OagisComponentType.valueOf(accRecord.getOagisComponentType()),
                    updateAccArguments.getOagisComponentType());
            updateAccArguments.setOagisComponentType(OagisComponentType.valueOf((int) detail.getOagisComponentType()));
        }

        ULong namespaceId = detail.getNamespaceId() == 0 ? null : ULong.valueOf(detail.getNamespaceId());
        if (!Objects.equals(accRecord.getNamespaceId(), namespaceId)) {
            updateAccArguments.setNamespaceId(ULong.valueOf(detail.getNamespaceId()));
            insertRevisionArguments.addContent("NamespaceId", accRecord.getNamespaceId(), updateAccArguments.getNamespaceId());
        }

        insertRevisionArguments.setAction(CcAction.DetailModified);

        ULong revisionId = insertRevisionArguments
                .setCreatedBy(userId)
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Modified)
                .setReference("acc" + accManifestRecord.getAccManifestId())
                .setPrevRevisionId(accRecord.getRevisionId())
                .execute();

        ULong accId = updateAccArguments.setLastUpdatedBy(userId)
                .setLastUpdateTimestamp(timestamp)
                .setRevisionId(revisionId)
                .setPrevAccId(accRecord.getAccId())
                .execute();

        ccRepository.updateAccManifestArguments(accManifestRecord)
                .setAccId(accId)
                .execute();

        updateAccChain(userId, accManifestRecord.getAccManifestId(), timestamp, revisionId);

        CcAccNode updateAccNode = repository.getAccNodeByAccManifestId(user, accManifestRecord.getAccManifestId());
        return repository.getAccNodeDetail(user, updateAccNode);
    }

    private CcBccpNode updateBccpDetail(User user, LocalDateTime timestamp, CcBccpNodeDetail.Bccp detail) {
        ULong userId = ULong.valueOf(sessionService.userId(user));

        BccpManifestRecord bccpManifestRecord = ccRepository.getBccpManifestByManifestId(
                ULong.valueOf(detail.getManifestId()));
        BccpRecord bccpRecord = ccRepository.getBccpById(bccpManifestRecord.getBccpId());

        UpdateBccpArguments updateBccpArguments = ccRepository.updateBccpArguments(bccpRecord);
        InsertRevisionArguments insertRevisionArguments = revisionRepository.insertRevisionArguments();

        if (!bccpRecord.getPropertyTerm().equals(detail.getPropertyTerm())) {
            DtManifestRecord bdtManifest = ccRepository.getBdtManifestByManifestId(
                    bccpManifestRecord.getBdtManifestId());
            DtRecord bdt = ccRepository.getBdtById(bdtManifest.getDtId());

            updateBccpArguments.setPropertyTerm(detail.getPropertyTerm());
            updateBccpArguments.setRepresentationTerm(bdt.getDataTypeTerm());
            insertRevisionArguments.addContent("PropertyTerm", bccpRecord.getPropertyTerm(), detail.getPropertyTerm());
            insertRevisionArguments.addContent("Den", bccpRecord.getDen(), detail.getPropertyTerm() + ". " + updateBccpArguments.getRepresentationTerm());
        }

        byte nillable = (byte) (detail.isNillable() ? 1 : 0);
        if (!bccpRecord.getIsNillable().equals(nillable)) {
            insertRevisionArguments.addContent("Nillable", bccpRecord.getIsNillable(), nillable);
            updateBccpArguments.setNillable(detail.isNillable());
        }

        byte deprecated = (byte) (detail.isDeprecated() ? 1 : 0);
        if (!bccpRecord.getIsDeprecated().equals(deprecated)) {
            insertRevisionArguments.addContent("Deprecated", bccpRecord.getIsDeprecated(), deprecated);
            updateBccpArguments.setDeprecated(detail.isDeprecated());
        }

        if (detail.getDefaultValue() != null && detail.getDefaultValue().length() > 0) {
            insertRevisionArguments.addContent("DefaultValue", bccpRecord.getDefaultValue(), detail.getDefaultValue());
            insertRevisionArguments.addContent("FixedValue", bccpRecord.getFixedValue(), detail.getFixedValue());
            updateBccpArguments.setFixedValue(null);
        } else if (detail.getFixedValue() != null && detail.getFixedValue().length() > 0) {
            insertRevisionArguments.addContent("DefaultValue", bccpRecord.getDefaultValue(), detail.getDefaultValue());
            insertRevisionArguments.addContent("FixedValue", bccpRecord.getFixedValue(), detail.getFixedValue());
            updateBccpArguments.setFixedValue(detail.getFixedValue());
            updateBccpArguments.setDefaultValue(null);
        } else {
            insertRevisionArguments.addContent("DefaultValue", bccpRecord.getDefaultValue(), detail.getDefaultValue());
            insertRevisionArguments.addContent("FixedValue", bccpRecord.getFixedValue(), detail.getFixedValue());
            updateBccpArguments.setFixedValue(null);
            updateBccpArguments.setDefaultValue(null);
        }

        if (!Objects.equals(bccpRecord.getDefinition(), detail.getDefinition())) {
            insertRevisionArguments.addContent("Definition", bccpRecord.getDefinition(), detail.getDefinition());
            updateBccpArguments.setDefinition(detail.getDefinition());
        }

        if (!Objects.equals(bccpRecord.getDefinitionSource(), detail.getDefinitionSource())) {
            insertRevisionArguments.addContent("DefinitionSource", bccpRecord.getDefinitionSource(), detail.getDefinitionSource());
            updateBccpArguments.setDefinitionSource(detail.getDefinitionSource());
        }

        ULong namespaceId = detail.getNamespaceId() == 0 ? null : ULong.valueOf(detail.getNamespaceId());
        if (!Objects.equals(bccpRecord.getNamespaceId(), namespaceId)) {
            updateBccpArguments.setNamespaceId(ULong.valueOf(detail.getNamespaceId()));
            insertRevisionArguments.addContent("NamespaceId", bccpRecord.getNamespaceId(), updateBccpArguments.getNamespaceId());
        }

        insertRevisionArguments.setAction(CcAction.DetailModified);

        ULong revisionId = insertRevisionArguments
                .setCreatedBy(userId)
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Modified)
                .setReference("bccp" + bccpManifestRecord.getBccpManifestId())
                .setPrevRevisionId(bccpRecord.getRevisionId())
                .execute();

        updateBccpArguments.setLastUpdatedBy(userId)
                .setLastUpdateTimestamp(timestamp)
                .setRevisionId(revisionId)
                .setPrevBccpId(bccpRecord.getBccpId());
        ULong bccpId = ccRepository.execute(updateBccpArguments);

        updateBccByToBccp(userId, bccpManifestRecord.getBccpManifestId(), timestamp, revisionId);

        UpdateBccpManifestArguments updateBccpManifestArguments =
                ccRepository.updateBccpManifestArguments(bccpManifestRecord);
        updateBccpManifestArguments.setBccpId(bccpId);
        ccRepository.execute(updateBccpManifestArguments);

        return repository.getBccpNodeByBccpManifestId(user, detail.getManifestId());
    }

    public CcAsccpNode updateAsccpDetail(User user, LocalDateTime timestamp, CcAsccpNodeDetail.Asccp detail) {
        ULong userId = ULong.valueOf(sessionService.userId(user));

        AsccpManifestRecord asccpManifestRecord =
                ccRepository.getAsccpManifestByManifestId(ULong.valueOf(detail.getManifestId()));
        AsccpRecord asccpRecord = ccRepository.getAsccpById(asccpManifestRecord.getAsccpId());
        AccRecord accRecord = ccRepository.getAccById(asccpRecord.getRoleOfAccId());

        UpdateAsccpArguments updateAsccpArguments = ccRepository.updateAsccpArguments(asccpRecord);
        InsertRevisionArguments insertRevisionArguments = revisionRepository.insertRevisionArguments();

        byte nillable = (byte) (detail.isNillable() ? 1 : 0);
        if (!asccpRecord.getIsNillable().equals(nillable)) {
            updateAsccpArguments.setNillable(detail.isNillable());
            insertRevisionArguments.addContent("Nillable", asccpRecord.getIsNillable() == 1, updateAsccpArguments.getNillable());
        }

        byte deprecated = (byte) (detail.isDeprecated() ? 1 : 0);
        if (!asccpRecord.getIsDeprecated().equals(deprecated)) {
            updateAsccpArguments.setDeprecated(detail.isDeprecated());
            insertRevisionArguments.addContent("Deprecated", asccpRecord.getIsDeprecated() == 1, updateAsccpArguments.getDeprecated());
        }

        if (!Objects.equals(asccpRecord.getDefinition(), detail.getDefinition())) {
            insertRevisionArguments.addContent("Definition", asccpRecord.getDefinition(), detail.getDefinition());
            updateAsccpArguments.setDefinition(detail.getDefinition());
        }

        if (!Objects.equals(asccpRecord.getDefinitionSource(), detail.getDefinitionSource())) {
            insertRevisionArguments.addContent("DefinitionSource", asccpRecord.getDefinitionSource(), detail.getDefinitionSource());
            updateAsccpArguments.setDefinitionSource(detail.getDefinitionSource());
        }

        ULong namespaceId = detail.getNamespaceId() == 0 ? null : ULong.valueOf(detail.getNamespaceId());
        if (!Objects.equals(asccpRecord.getNamespaceId(), namespaceId)) {
            updateAsccpArguments.setNamespaceId(ULong.valueOf(detail.getNamespaceId()));
            insertRevisionArguments.addContent("NamespaceId", asccpRecord.getNamespaceId(), updateAsccpArguments.getNamespaceId());
        }

        if (!asccpRecord.getPropertyTerm().equals(detail.getPropertyTerm())) {
            updateAsccpArguments.setPropertyTerm(detail.getPropertyTerm());
            updateAsccpArguments.setDen(detail.getPropertyTerm() + ". " + accRecord.getObjectClassTerm());
            insertRevisionArguments.addContent("PropertyTerm", asccpRecord.getPropertyTerm(), detail.getPropertyTerm());
            insertRevisionArguments.addContent("Den", asccpRecord.getDen(), updateAsccpArguments.getDen());
        }

        insertRevisionArguments.setAction(CcAction.DetailModified);

        ULong revisionId = insertRevisionArguments
                .setCreatedBy(userId)
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Modified)
                .setReference("asccp" + asccpManifestRecord.getAsccpManifestId())
                .setPrevRevisionId(asccpRecord.getRevisionId())
                .execute();

        updateAsccpArguments
                .setPropertyTerm(detail.getPropertyTerm())
                .setDen(detail.getPropertyTerm() + ". " + accRecord.getObjectClassTerm())
                .setReuseableIndicator(detail.isReusable())
                .setLastUpdatedBy(userId)
                .setLastUpdateTimestamp(timestamp)
                .setRevisionId(revisionId)
                .setPrevAsccpId(asccpRecord.getAsccpId());

        ULong asccpId = ccRepository.execute(updateAsccpArguments);

        if (!asccpId.equals(asccpRecord.getAsccpId())) {
            UpdateAsccpManifestArguments updateAsccpManifestArguments
                    = ccRepository.updateAsccpManifestArguments(asccpManifestRecord)
                    .setAsccpId(asccpId);
            ccRepository.execute(updateAsccpManifestArguments);
            updateAsccByToAsccp(userId, asccpManifestRecord.getAsccpManifestId(),  timestamp, revisionId);
        }

        insertRevisionArguments.setAction(CcAction.DetailModified);

        return repository.getAsccpNodeByAsccpManifestId(user, asccpManifestRecord.getAsccpManifestId().longValue());
    }

    @Transactional
    public long appendAsccp(User user, long accManifestId, long asccpManifestId) {
        long userId = sessionService.userId(user);
        LocalDateTime timestamp = LocalDateTime.now();

        AccManifestRecord accManifest = ccRepository.getAccManifestByManifestId(ULong.valueOf(accManifestId));
        AsccpManifestRecord asccpManifestRecord
                = ccRepository.getAsccpManifestByManifestId(ULong.valueOf(asccpManifestId));

        repository.duplicateAssociationValidate(user, accManifest.getAccManifestId(),
                asccpManifestRecord.getAsccpManifestId(), null);

        AsccpRecord asccpRecord = ccRepository.getAsccpById(asccpManifestRecord.getAsccpId());
        AccRecord accRecord = ccRepository.getAccById(accManifest.getAccId());

        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(ULong.valueOf(userId))
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Modified)
                .setReference("acc" + accManifestId)
                .setPrevRevisionId(accRecord.getRevisionId())
                .execute();

        ULong accId = ccRepository.updateAccArguments(accRecord)
                .setRevisionId(revisionId)
                .execute();

        ccRepository.updateAccManifestArguments(accManifest)
                .setAccId(accId)
                .execute();

        updateAccChain(ULong.valueOf(userId), accManifest.getAccManifestId(), timestamp, revisionId);


        int seqKey = ccRepository.getNextSeqKey(accManifest.getAccManifestId());

        ULong asccId = ccRepository.insertAsccArguments()
                .setGuid(SrtGuid.randomGuid())
                .setCardinalityMin(0)
                .setCardinalityMax(-1)
                .setSeqKey(seqKey)
                .setDeprecated(false)
                .setFromAccId(accRecord.getAccId())
                .setToAsccpId(asccpManifestRecord.getAsccpId())
                .setDen(accRecord.getObjectClassTerm() + ". " + asccpRecord.getPropertyTerm())
                .setCreatedBy(ULong.valueOf(userId))
                .setCreationTimestamp(timestamp)
                .setLastUpdatedBy(ULong.valueOf(userId))
                .setLastUpdateTimestamp(timestamp)
                .setOwnerUserId(ULong.valueOf(userId))
                .setState(CcState.valueOf(accRecord.getState()))
                .setRevisionId(revisionId)
                .execute();

        return ccRepository.insertAsccManifestArguments()
                .setReleaseId(accManifest.getReleaseId())
                .setAsccId(asccId)
                .setFromAccManifestId(accManifest.getAccManifestId())
                .setToAsccpManifestId(asccpManifestRecord.getAsccpManifestId())
                .execute().longValue();
    }

    @Transactional
    public long appendBccp(User user, long accManifestId, long bccpManifestId) {
        long userId = sessionService.userId(user);
        LocalDateTime timestamp = LocalDateTime.now();

        AccManifestRecord accManifest = ccRepository.getAccManifestByManifestId(ULong.valueOf(accManifestId));
        BccpManifestRecord bccpManifestRecord
                = ccRepository.getBccpManifestByManifestId(ULong.valueOf(bccpManifestId));

        repository.duplicateAssociationValidate(user, accManifest.getAccManifestId(),
                null, bccpManifestRecord.getBccpManifestId());

        BccpRecord bccpRecord = ccRepository.getBccpById(bccpManifestRecord.getBccpId());
        AccRecord accRecord = ccRepository.getAccById(accManifest.getAccId());

        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(ULong.valueOf(userId))
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Modified)
                .setReference("acc" + accManifestId)
                .setPrevRevisionId(accRecord.getRevisionId())
                .execute();

        ULong accId = ccRepository.updateAccArguments(accRecord)
                .setRevisionId(revisionId)
                .execute();

        ccRepository.updateAccManifestArguments(accManifest)
                .setAccId(accId)
                .execute();

        updateAccChain(ULong.valueOf(userId), accManifest.getAccManifestId(), timestamp, revisionId);

        int seqKey = ccRepository.getNextSeqKey(accManifest.getAccManifestId());

        ULong bccId = ccRepository.insertBccArguments()
                .setGuid(SrtGuid.randomGuid())
                .setCardinalityMin(0)
                .setCardinalityMax(-1)
                .setSeqKey(seqKey)
                .setDeprecated(false)
                .setNillable(false)
                .setEntityType(Element)
                .setFromAccId(accRecord.getAccId())
                .setToBccpId(bccpManifestRecord.getBccpId())
                .setDen(accRecord.getObjectClassTerm() + ". " + bccpRecord.getPropertyTerm())
                .setCreatedBy(ULong.valueOf(userId))
                .setCreationTimestamp(timestamp)
                .setLastUpdatedBy(ULong.valueOf(userId))
                .setLastUpdateTimestamp(timestamp)
                .setOwnerUserId(ULong.valueOf(userId))
                .setState(CcState.valueOf(accRecord.getState()))
                .setRevisionId(revisionId)
                .execute();

        return ccRepository.insertBccManifestArguments()
                .setReleaseId(accManifest.getReleaseId())
                .setBccId(bccId)
                .setFromAccManifestId(accManifest.getAccManifestId())
                .setToBccpManifestId(bccpManifestRecord.getBccpManifestId())
                .execute().longValue();
    }

    @Transactional
    public CcNode updateAsccpRoleOfAcc(User user, long asccpManifestId, long accManifestId) {
        LocalDateTime timestamp = LocalDateTime.now();
        ULong userId = ULong.valueOf(sessionService.userId(user));

        AsccpManifestRecord asccpManifestRecord =
                ccRepository.getAsccpManifestByManifestId(ULong.valueOf(asccpManifestId));
        AsccpRecord asccpRecord = ccRepository.getAsccpById(asccpManifestRecord.getAsccpId());
        AccManifestRecord accManifestRecord = ccRepository.getAccManifestByManifestId(ULong.valueOf(accManifestId));
        AccRecord accRecord = ccRepository.getAccById(accManifestRecord.getAccId());

        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(userId)
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Modified)
                .setReference("asccp" + asccpManifestRecord.getAsccpManifestId())
                .setPrevRevisionId(asccpRecord.getRevisionId())
                .execute();

        ULong asccpId = ccRepository.updateAsccpArguments(asccpRecord)
                .setLastUpdatedBy(userId)
                .setLastUpdateTimestamp(timestamp)
                .setRevisionId(revisionId)
                .setRoleOfAccId(accRecord.getAccId())
                .setPrevAsccpId(asccpRecord.getAsccpId())
                .execute();

        ccRepository.updateAsccpManifestArguments(asccpManifestRecord)
                .setAsccpId(asccpId)
                .setRoleOfAccManifestId(accManifestRecord.getAccManifestId())
                .execute();

        updateAsccByToAsccp(userId, asccpManifestRecord.getAsccpManifestId(), timestamp, revisionId);

        return repository.getAsccpNodeByAsccpManifestId(user, asccpManifestRecord.getAsccpManifestId().longValue());
    }

    @Transactional
    public CcNode updateBccpBdt(User user, long bccpManifestId, long bdtManifestId) {
        LocalDateTime timestamp = LocalDateTime.now();
        ULong userId = ULong.valueOf(sessionService.userId(user));

        BccpManifestRecord bccpManifestRecord =
                ccRepository.getBccpManifestByManifestId(ULong.valueOf(bccpManifestId));
        BccpRecord bccpRecord = ccRepository.getBccpById(bccpManifestRecord.getBccpId());
        DtManifestRecord dtManifestRecord = ccRepository.getBdtManifestByManifestId(ULong.valueOf(bdtManifestId));
        DtRecord dtRecord = ccRepository.getBdtById(dtManifestRecord.getDtId());

        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(userId)
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Modified)
                .setReference("bccp" + bccpManifestRecord.getBccpManifestId())
                .setPrevRevisionId(bccpRecord.getRevisionId())
                .execute();

        ULong bccpId = ccRepository.updateBccpArguments(bccpRecord)
                .setLastUpdatedBy(userId)
                .setLastUpdateTimestamp(timestamp)
                .setRevisionId(revisionId)
                .setBdtId(dtRecord.getDtId())
                .setPrevBccpId(bccpRecord.getBccpId())
                .execute();

        ccRepository.updateBccpManifestArguments(bccpManifestRecord)
                .setBccpId(bccpId)
                .setBdtManifestId(dtManifestRecord.getDtManifestId())
                .execute();

        updateBccByToBccp(userId, bccpManifestRecord.getBccpManifestId(), timestamp, revisionId);

        return repository.getBccpNodeByBccpManifestId(user, bccpManifestRecord.getBccpManifestId().longValue());
    }

    @Transactional
    public CcAccNode updateAccState(User user, long accManifestId, String state) {
        LocalDateTime timestamp = LocalDateTime.now();
        ULong userId = ULong.valueOf(sessionService.userId(user));
        CcState ccState = getStateCode(state);

        AccManifestRecord accManifestRecord = ccRepository.getAccManifestByManifestId(ULong.valueOf(accManifestId));
        AccRecord accRecord = ccRepository.getAccById(accManifestRecord.getAccId());

        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(userId)
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Modified)
                .setReference("acc" + accManifestRecord.getAccManifestId())
                .setPrevRevisionId(accRecord.getRevisionId())
                .execute();

        ULong accId = ccRepository.updateAccArguments(accRecord)
                .setLastUpdatedBy(userId)
                .setLastUpdateTimestamp(timestamp)
                .setRevisionId(revisionId)
                .setState(ccState)
                .setPrevAccId(accRecord.getAccId())
                .execute();

        ccRepository.updateAccManifestArguments(accManifestRecord)
                .setAccId(accId)
                .execute();

        updateAccChain(userId, accManifestRecord.getAccManifestId(), timestamp, revisionId);

        CcEvent event = new CcEvent();
        event.setAction("ChangeState");
        event.addProperty("State", state);
        event.addProperty("actor", user.getUsername());
        simpMessagingTemplate.convertAndSend("/topic/acc/" + accManifestId, event);

        return getAccNode(user, accManifestId);
    }

    @Transactional
    public CcAsccpNode updateAsccpState(User user, long asccpManifestId, String state) {
        LocalDateTime timestamp = LocalDateTime.now();
        ULong userId = ULong.valueOf(sessionService.userId(user));
        CcState ccState = getStateCode(state);

        AsccpManifestRecord asccpManifestRecord = ccRepository.getAsccpManifestByManifestId(ULong.valueOf(asccpManifestId));
        AsccpRecord asccpRecord = ccRepository.getAsccpById(asccpManifestRecord.getAsccpId());

        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(userId)
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Modified)
                .setReference("asccp" + asccpManifestRecord.getAsccpManifestId())
                .setPrevRevisionId(asccpRecord.getRevisionId())
                .execute();

        ULong asccpId = ccRepository.updateAsccpArguments(asccpRecord)
                .setLastUpdatedBy(userId)
                .setLastUpdateTimestamp(timestamp)
                .setRevisionId(revisionId)
                .setState(ccState)
                .setPrevAsccpId(asccpRecord.getAsccpId())
                .execute();

        ccRepository.updateAsccpManifestArguments(asccpManifestRecord)
                .setAsccpId(asccpId)
                .execute();

        updateAsccByToAsccp(userId, asccpManifestRecord.getAsccpManifestId(), timestamp, revisionId);

        CcEvent event = new CcEvent();
        event.setAction("ChangeState");
        event.addProperty("State", state);
        event.addProperty("actor", user.getUsername());
        simpMessagingTemplate.convertAndSend("/topic/asccp/" + asccpManifestId, event);

        return getAsccpNode(user, asccpManifestId);
    }

    @Transactional
    public CcBccpNode updateBccpState(User user, long bccpManifestId, String state) {
        LocalDateTime timestamp = LocalDateTime.now();
        ULong userId = ULong.valueOf(sessionService.userId(user));
        CcState ccState = getStateCode(state);

        BccpManifestRecord bccpManifestRecord = ccRepository.getBccpManifestByManifestId(ULong.valueOf(bccpManifestId));
        BccpRecord bccpRecord = ccRepository.getBccpById(bccpManifestRecord.getBccpId());

        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(userId)
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Modified)
                .setReference("bccp" + bccpManifestRecord.getBccpManifestId())
                .setPrevRevisionId(bccpRecord.getRevisionId())
                .execute();

        ULong bccpId = ccRepository.updateBccpArguments(bccpRecord)
                .setLastUpdatedBy(userId)
                .setLastUpdateTimestamp(timestamp)
                .setRevisionId(revisionId)
                .setState(ccState)
                .setPrevBccpId(bccpRecord.getBccpId())
                .execute();

        ccRepository.updateBccpManifestArguments(bccpManifestRecord)
                .setBccpId(bccpId)
                .execute();

        updateBccByToBccp(userId, bccpManifestRecord.getBccpManifestId(), timestamp, revisionId);

        CcEvent event = new CcEvent();
        event.setAction("ChangeState");
        event.addProperty("State", state);
        event.addProperty("actor", user.getUsername());
        simpMessagingTemplate.convertAndSend("/topic/bccp/" + bccpManifestId, event);

        return getBccpNode(user, bccpManifestId);
    }

    @Transactional
    public CcAccNode makeNewRevisionForAcc(User user, long accManifestId) {
        LocalDateTime timestamp = LocalDateTime.now();
        ULong userId = ULong.valueOf(sessionService.userId(user));

        AccManifestRecord accManifestRecord = ccRepository.getAccManifestByManifestId(ULong.valueOf(accManifestId));
        AccRecord accRecord = ccRepository.getAccById(accManifestRecord.getAccId());
        if (CcState.valueOf(accRecord.getState()) != CcState.Published) {
            throw new IllegalArgumentException("Creating new revision only allowed for the component in 'Published' state.");
        }

        Release workingRelease = releaseRepository.getWorkingRelease();

        if (accManifestRecord.getReleaseId().longValue() != workingRelease.getReleaseId()) {
            throw new IllegalArgumentException("Creating new revision is not allow for this release");
        }

        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(userId)
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Modified)
                .setReference("acc" + accManifestId)
                .setPrevRevisionId(accRecord.getRevisionId())
                .execute();

        ULong accId = ccRepository.updateAccArguments(accRecord)
                .setLastUpdatedBy(userId)
                .setLastUpdateTimestamp(timestamp)
                .setRevisionId(revisionId)
                .setPrevAccId(accRecord.getAccId())
                .setState(CcState.WIP)
                .execute();

        ccRepository.updateAccManifestArguments(accManifestRecord)
                .setAccId(accId)
                .execute();

        AccRecord updatedAccRecord = ccRepository.getAccById(accId);
        accManifestRecord.setAccId(accId);

        updateAccChain(userId, accManifestRecord.getAccManifestId(), timestamp, revisionId);

        return repository.getAccNodeByAccManifestId(user, accManifestRecord.getAccManifestId());
    }

    @Transactional
    public CcAsccpNode makeNewRevisionForAsccp(User user, long asccpManifestId) {
        LocalDateTime timestamp = LocalDateTime.now();
        ULong userId = ULong.valueOf(sessionService.userId(user));

        AsccpManifestRecord asccpManifestRecord =
                ccRepository.getAsccpManifestByManifestId(ULong.valueOf(asccpManifestId));
        AsccpRecord asccpRecord = ccRepository.getAsccpById(asccpManifestRecord.getAsccpId());

        if (CcState.valueOf(asccpRecord.getState()) != CcState.Published) {
            throw new IllegalArgumentException("Creating new revision only allowed for the component in 'Published' state.");
        }

        Release workingRelease = releaseRepository.getWorkingRelease();

        if (asccpManifestRecord.getReleaseId().longValue() != workingRelease.getReleaseId()) {
            throw new IllegalArgumentException("Creating new revision is not allow for this release");
        }

        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(userId)
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Modified)
                .setReference("asccp" + asccpManifestRecord.getAsccpManifestId())
                .setPrevRevisionId(asccpRecord.getRevisionId())
                .execute();

        ULong asccpId = ccRepository.updateAsccpArguments(asccpRecord)
                .setLastUpdatedBy(userId)
                .setLastUpdateTimestamp(timestamp)
                .setRevisionId(revisionId)
                .setState(CcState.WIP)
                .setPrevAsccpId(asccpRecord.getAsccpId())
                .execute();

        ccRepository.updateAsccpManifestArguments(asccpManifestRecord)
                .setAsccpId(asccpId)
                .execute();

        return repository.getAsccpNodeByAsccpManifestId(user, asccpManifestRecord.getAsccpManifestId().longValue());
    }

    @Transactional
    public CcBccpNode makeNewRevisionForBccp(User user, long bccpManifestId) {
        LocalDateTime timestamp = LocalDateTime.now();
        ULong userId = ULong.valueOf(sessionService.userId(user));

        BccpManifestRecord bccpManifestRecord =
                ccRepository.getBccpManifestByManifestId(ULong.valueOf(bccpManifestId));
        BccpRecord bccpRecord = ccRepository.getBccpById(bccpManifestRecord.getBccpId());
        if (CcState.valueOf(bccpRecord.getState()) != CcState.Published) {
            throw new IllegalArgumentException("Creating new revision only allowed for the component in 'Published' state.");
        }

        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(userId)
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Modified)
                .setReference("bccp" + bccpManifestRecord.getBccpManifestId())
                .setPrevRevisionId(bccpRecord.getRevisionId())
                .execute();


        ULong bccpId = ccRepository.updateBccpArguments(bccpRecord)
                .setLastUpdatedBy(userId)
                .setLastUpdateTimestamp(timestamp)
                .setRevisionId(revisionId)
                .setState(CcState.WIP)
                .setPrevBccpId(bccpRecord.getBccpId())
                .execute();

        Release workingRelease = releaseRepository.getWorkingRelease();
        ULong workingReleaseId = ULong.valueOf(workingRelease.getReleaseId());

        if (bccpManifestRecord.getReleaseId().equals(workingReleaseId)) {
            ccRepository.updateBccpManifestArguments(bccpManifestRecord)
                    .setBccpId(bccpId)
                    .execute();
        } else {
            DtManifestRecord dtManifestRecord = ccRepository.getBdtManifestByManifestId(bccpManifestRecord.getBdtManifestId());
            DtManifestRecord dtWorkingManifestRecord = ccRepository.getBdtManifestByBdtId(dtManifestRecord.getDtId(), workingReleaseId);
            bccpManifestId = ccRepository.insertBccpManifest()
                    .setBdtManifestId(dtWorkingManifestRecord.getDtManifestId())
                    .setBccpId(bccpId)
                    .setReleaseId(workingReleaseId)
                    .execute().longValue();

        }
        return getBccpNode(user, bccpManifestId);
    }

    @Transactional
    public CcAccNode updateAccBasedId(User user, long accManifestId, long basedAccManifestId) {
        if (accManifestId == basedAccManifestId) {
            throw new IllegalArgumentException("Cannot choose itself as a based ACC.");
        }

        LocalDateTime timestamp = LocalDateTime.now();
        ULong userId = ULong.valueOf(sessionService.userId(user));

        AccManifestRecord accManifestRecord = ccRepository.getAccManifestByManifestId(ULong.valueOf(accManifestId));
        AccManifestRecord basedAccManifestRecord = ccRepository.getAccManifestByManifestId(ULong.valueOf(basedAccManifestId));
        AccRecord accRecord = ccRepository.getAccById(accManifestRecord.getAccId());

        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(userId)
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Modified)
                .setReference("acc" + accManifestRecord.getAccManifestId())
                .setPrevRevisionId(accRecord.getRevisionId())
                .execute();

        ULong accId = ccRepository.updateAccArguments(accRecord)
                .setLastUpdatedBy(userId)
                .setLastUpdateTimestamp(timestamp)
                .setRevisionId(revisionId)
                .setBasedAccId(basedAccManifestRecord.getAccId())
                .setPrevAccId(accRecord.getAccId())
                .execute();

        ccRepository.updateAccManifestArguments(accManifestRecord)
                .setAccId(accId)
                .setBasedAccManifestId(basedAccManifestRecord.getAccManifestId())
                .execute();

        updateAccChain(userId, accManifestRecord.getAccManifestId(), timestamp, revisionId);

        return getAccNode(user, accManifestId);
    }

    @Transactional
    public CcAccNode discardAccBasedId(User user, long accManifestId) {

        LocalDateTime timestamp = LocalDateTime.now();
        ULong userId = ULong.valueOf(sessionService.userId(user));

        AccManifestRecord accManifestRecord = ccRepository.getAccManifestByManifestId(ULong.valueOf(accManifestId));
        AccRecord accRecord = ccRepository.getAccById(accManifestRecord.getAccId());

        ULong revisionId = revisionRepository.insertRevisionArguments()
                .setCreatedBy(userId)
                .setCreationTimestamp(timestamp)
                .setRevisionAction(RevisionAction.Modified)
                .setReference("acc" + accManifestRecord.getAccManifestId())
                .setPrevRevisionId(accRecord.getRevisionId())
                .execute();

        ULong accId = ccRepository.updateAccArguments(accRecord)
                .setLastUpdatedBy(userId)
                .setLastUpdateTimestamp(timestamp)
                .setRevisionId(revisionId)
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

    private void decreaseSeqKeyGreaterThan(long userId, ULong accManifestId, int seqKey, LocalDateTime timestamp, ULong revisionId) {
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
                    .setRevisionId(revisionId)
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
                    .setRevisionId(revisionId)
                    .setSeqKey(bccRecord.getSeqKey() - 1)
                    .execute();

            ccRepository.updateBccManifestArguments(bccManifestRecord)
                    .setBccId(bccId)
                    .execute();
        }
    }

    public CcRevisionResponse getAccNoddRevision(User user, long manifestId) {
        CcAccNode accNode = getAccNode(user, manifestId);
        Long lastPublishedCcId = getLastPublishedCcId(accNode.getAccId(), CcType.ACC);
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
                Long lastAsccId = getLastPublishedCcId(asccManifestRecord.getAsccId().longValue(), CcType.ASCC);
                if (lastAsccId != null) {
                    associationKeys.add(CcType.ASCCP.toString().toLowerCase() + asccManifestRecord.getToAsccpManifestId());
                }
            }
            List<BccManifestRecord> bccManifestRecordList
                    = ccRepository.getBccManifestByFromAccManifestId(ULong.valueOf(manifestId));
            for (BccManifestRecord bccManifestRecord : bccManifestRecordList) {
                Long lastBccId = getLastPublishedCcId(bccManifestRecord.getBccId().longValue(), CcType.BCC);
                if (lastBccId != null) {
                    associationKeys.add(CcType.BCCP.toString().toLowerCase() + bccManifestRecord.getToBccpManifestId());
                }
            }
            ccRevisionResponse.setAssociationKeys(associationKeys);
        }
        return ccRevisionResponse;
    }

    public CcRevisionResponse getBccpNoddRevision(User user, long manifestId) {
        CcBccpNode bccpNode = getBccpNode(user, manifestId);
        Long lastPublishedCcId = getLastPublishedCcId(bccpNode.getBccpId(), BCCP);
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

    public CcRevisionResponse getAsccpNoddRevision(User user, long manifestId) {
        CcAsccpNode asccpNode = getAsccpNode(user, manifestId);
        Long lastPublishedCcId = getLastPublishedCcId(asccpNode.getAsccpId(), ASCCP);
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

    private Long getLastPublishedCcId(Long ccId, CcType type) {
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
                return getLastPublishedCcId(accRecord.getPrevAccId().longValue(), CcType.ACC);
            case ASCC:
                AsccRecord asccRecord = ccRepository.getAsccById(ULong.valueOf(ccId));
                if (asccRecord.getState().equals(CcState.Published.name())) {
                    return ccId;
                }
                if (asccRecord.getPrevAsccId() == null) {
                    return null;
                }
                return getLastPublishedCcId(asccRecord.getPrevAsccId().longValue(), CcType.ASCC);
            case BCC:
                BccRecord bccRecord = ccRepository.getBccById(ULong.valueOf(ccId));
                if (bccRecord.getState().equals(CcState.Published.name())) {
                    return ccId;
                }
                if (bccRecord.getPrevBccId() == null) {
                    return null;
                }
                return getLastPublishedCcId(bccRecord.getPrevBccId().longValue(), CcType.BCC);
            case ASCCP:
                AsccpRecord asccpRecord = ccRepository.getAsccpById(ULong.valueOf(ccId));
                if (asccpRecord.getState().equals(CcState.Published.name())) {
                    return ccId;
                }
                if (asccpRecord.getPrevAsccpId() == null) {
                    return null;
                }
                return getLastPublishedCcId(asccpRecord.getPrevAsccpId().longValue(), CcType.ASCCP);
            case BCCP:
                BccpRecord bccpRecord = ccRepository.getBccpById(ULong.valueOf(ccId));
                if (bccpRecord.getState().equals(CcState.Published.name())) {
                    return ccId;
                }
                if (bccpRecord.getPrevBccpId() == null) {
                    return null;
                }
                return getLastPublishedCcId(bccpRecord.getPrevBccpId().longValue(), CcType.BCCP);

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
                    .setRevisionId(revisionId)
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
                    .setRevisionId(revisionId)
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
                    .setRevisionId(revisionId)
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
                    .setRevisionId(revisionId)
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
                    .setRevisionId(revisionId)
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
                    .setRevisionId(revisionId)
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
                .setPrevRevisionId(accRecord.getRevisionId())
                .execute();

        ULong accId = ccRepository.updateAccArguments(accRecord)
                .setRevisionId(revisionId)
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
                .setPrevRevisionId(asccpRecord.getRevisionId())
                .execute();

        ULong asccpId = ccRepository.updateAsccpArguments(asccpRecord)
                .setOwnerUserId(ownerUserId)
                .setLastUpdatedBy(requesterUserId)
                .setLastUpdateTimestamp(timestamp)
                .setRevisionId(revisionId)
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
                .setPrevRevisionId(bccpRecord.getRevisionId())
                .execute();

        ULong bccpId = ccRepository.updateBccpArguments(bccpRecord)
                .setOwnerUserId(ownerUserId)
                .setLastUpdatedBy(requesterUserId)
                .setLastUpdateTimestamp(timestamp)
                .setRevisionId(revisionId)
                .execute();

        ccRepository.updateBccpManifestArguments(bccpManifestRecord)
                .setBccpId(bccpId)
                .execute();

        updateBccByToBccp(requesterUserId, bccpManifestRecord.getBccpManifestId(), timestamp, revisionId);
    }
}

