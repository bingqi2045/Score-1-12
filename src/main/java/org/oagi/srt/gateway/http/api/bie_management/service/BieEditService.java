package org.oagi.srt.gateway.http.api.bie_management.service;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.ACC;
import org.oagi.srt.data.BieState;
import org.oagi.srt.data.TopLevelAbie;
import org.oagi.srt.entity.jooq.Tables;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.*;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree.BieEditAbieNode;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree.BieEditAsbiepNode;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree.BieEditNodeDetail;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree.BieEditRef;
import org.oagi.srt.gateway.http.api.bie_management.service.edit_tree.BieEditTreeController;
import org.oagi.srt.gateway.http.api.bie_management.service.edit_tree.DefaultBieEditTreeController;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.service.ExtensionService;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.repo.component.abie.AbieNode;
import org.oagi.srt.repo.component.abie.AbieReadRepository;
import org.oagi.srt.repo.component.abie.AbieWriteRepository;
import org.oagi.srt.repo.component.abie.UpsertAbieRequest;
import org.oagi.srt.repo.component.agency_id_list.AgencyIdListReadRepository;
import org.oagi.srt.repo.component.agency_id_list.AvailableAgencyIdList;
import org.oagi.srt.repo.component.asbie.AsbieNode;
import org.oagi.srt.repo.component.asbie.AsbieReadRepository;
import org.oagi.srt.repo.component.asbie.AsbieWriteRepository;
import org.oagi.srt.repo.component.asbie.UpsertAsbieRequest;
import org.oagi.srt.repo.component.asbiep.AsbiepNode;
import org.oagi.srt.repo.component.asbiep.AsbiepReadRepository;
import org.oagi.srt.repo.component.asbiep.AsbiepWriteRepository;
import org.oagi.srt.repo.component.asbiep.UpsertAsbiepRequest;
import org.oagi.srt.repo.component.bbie.BbieNode;
import org.oagi.srt.repo.component.bbie.BbieReadRepository;
import org.oagi.srt.repo.component.bbie.BbieWriteRepository;
import org.oagi.srt.repo.component.bbie.UpsertBbieRequest;
import org.oagi.srt.repo.component.bbie_sc.BbieScNode;
import org.oagi.srt.repo.component.bbie_sc.BbieScReadRepository;
import org.oagi.srt.repo.component.bbie_sc.BbieScWriteRepository;
import org.oagi.srt.repo.component.bbie_sc.UpsertBbieScRequest;
import org.oagi.srt.repo.component.bbiep.BbiepNode;
import org.oagi.srt.repo.component.bbiep.BbiepReadRepository;
import org.oagi.srt.repo.component.bbiep.BbiepWriteRepository;
import org.oagi.srt.repo.component.bbiep.UpsertBbiepRequest;
import org.oagi.srt.repo.component.bdt_pri_restri.AvailableBdtPriRestri;
import org.oagi.srt.repo.component.bdt_pri_restri.BdtPriRestriReadRepository;
import org.oagi.srt.repo.component.bdt_sc_pri_restri.AvailableBdtScPriRestri;
import org.oagi.srt.repo.component.bdt_sc_pri_restri.BdtScPriRestriReadRepository;
import org.oagi.srt.repo.component.code_list.AvailableCodeList;
import org.oagi.srt.repo.component.code_list.CodeListReadRepository;
import org.oagi.srt.repo.component.dt.BdtNode;
import org.oagi.srt.repo.component.dt.DtReadRepository;
import org.oagi.srt.repository.TopLevelAbieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;

@Service
@Transactional(readOnly = true)
public class BieEditService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TopLevelAbieRepository topLevelAbieRepository;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private BieRepository bieRepository;

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private ExtensionService extensionService;

    @Autowired
    private SessionService sessionService;

    private BieEditTreeController getTreeController(User user, BieEditNode node) {
        return getTreeController(user, node.getTopLevelAbieId());
    }

    private BieEditTreeController getTreeController(User user, BigInteger topLevelAbieId) {
        DefaultBieEditTreeController bieEditTreeController =
                applicationContext.getBean(DefaultBieEditTreeController.class);

        TopLevelAbie topLevelAbie = topLevelAbieRepository.findById(topLevelAbieId);
        bieEditTreeController.initialize(user, topLevelAbie);

        return bieEditTreeController;
    }

    @Transactional
    public BieEditAbieNode getRootNode(User user, BigInteger topLevelAbieId) {
        BieEditTreeController treeController = getTreeController(user, topLevelAbieId);
        return treeController.getRootNode(topLevelAbieId);
    }

    @Transactional
    public BccForBie getBcc(User user, BigInteger bccId) {
        return bieRepository.getBcc(bccId);
    }

    @Transactional
    public List<BieEditNode> getDescendants(User user, BieEditNode node, boolean hideUnused) {
        BieEditTreeController treeController = getTreeController(user, node);
        return treeController.getDescendants(node, hideUnused);
    }

    @Transactional
    public BieEditNodeDetail getDetail(User user, BieEditNode node) {
        BieEditTreeController treeController = getTreeController(user, node);
        return treeController.getDetail(node);
    }

    @Transactional
    public void updateState(User user, BigInteger topLevelAbieId, BieState state) {
        BieEditTreeController treeController = getTreeController(user, topLevelAbieId);
        treeController.updateState(state);
    }

    @Autowired
    private AbieWriteRepository abieWriteRepository;

    @Autowired
    private AsbieWriteRepository asbieWriteRepository;

    @Autowired
    private BbieWriteRepository bbieWriteRepository;

    @Autowired
    private AsbiepWriteRepository asbiepWriteRepository;

    @Autowired
    private BbiepWriteRepository bbiepWriteRepository;

    @Autowired
    private BbieScWriteRepository bbieScWriteRepository;

    @Transactional
    public BieEditUpdateDetailResponse updateDetails(User user, BieEditUpdateDetailRequest request) {
        BieEditUpdateDetailResponse response = new BieEditUpdateDetailResponse();
        LocalDateTime timestamp = LocalDateTime.now();

        response.setAbieDetailMap(
                request.getAbieDetails().stream()
                        .map(abie ->
                                abieWriteRepository.upsertAbie(new UpsertAbieRequest(
                                        user, timestamp, request.getTopLevelAbieId(), abie))
                        )
                        .collect(Collectors.toMap(e -> e.getHashPath(), Function.identity()))
        );

        response.setAsbiepDetailMap(
                request.getAsbiepDetails().stream()
                        .map(asbiep ->
                                asbiepWriteRepository.upsertAsbiep(new UpsertAsbiepRequest(
                                        user, timestamp, request.getTopLevelAbieId(), asbiep))
                        )
                        .collect(Collectors.toMap(e -> e.getHashPath(), Function.identity()))
        );

        response.setBbiepDetailMap(
                request.getBbiepDetails().stream()
                        .map(bbiep ->
                                bbiepWriteRepository.upsertBbiep(new UpsertBbiepRequest(
                                        user, timestamp, request.getTopLevelAbieId(), bbiep
                                ))
                        )
                        .collect(Collectors.toMap(e -> e.getHashPath(), Function.identity()))
        );

        response.setAsbieDetailMap(
                request.getAsbieDetails().stream()
                        .map(asbie ->
                                asbieWriteRepository.upsertAsbie(new UpsertAsbieRequest(
                                        user, timestamp, request.getTopLevelAbieId(), asbie
                                ))
                        )
                        .collect(Collectors.toMap(e -> e.getHashPath(), Function.identity()))
        );

        response.setBbieDetailMap(
                request.getBbieDetails().stream()
                        .map(bbie ->
                                bbieWriteRepository.upsertBbie(new UpsertBbieRequest(
                                        user, timestamp, request.getTopLevelAbieId(), bbie
                                ))
                        )
                        .collect(Collectors.toMap(e -> e.getHashPath(), Function.identity()))
        );

        response.setBbieScDetailMap(
                request.getBbieScDetails().stream()
                        .map(bbieSc ->
                                bbieScWriteRepository.upsertBbieSc(new UpsertBbieScRequest(
                                        user, timestamp, request.getTopLevelAbieId(), bbieSc
                                ))
                        )
                        .collect(Collectors.toMap(e -> e.getHashPath(), Function.identity()))
        );

        return response;
    }

    @Transactional
    public CreateExtensionResponse createLocalAbieExtension(User user, BieEditAsbiepNode extension) {
        BigInteger asccpManifestId = extension.getAsccpManifestId();
        BigInteger releaseId = extension.getReleaseId();
        BigInteger roleOfAccManifestId = bieRepository.getRoleOfAccManifestIdByAsccpManifestId(asccpManifestId);

        CreateExtensionResponse response = new CreateExtensionResponse();

        ACC ueAcc = extensionService.getExistsUserExtension(roleOfAccManifestId);

        response.setCanEdit(false);
        response.setCanView(false);

        if (ueAcc != null) {
            ACC latestUeAcc = ueAcc;
            if (ueAcc.getState() == CcState.Candidate) {
                response.setCanEdit(true);
                response.setCanView(true);
            } else if (ueAcc.getState() == CcState.Draft) {
                response.setCanView(true);
            }
            boolean isSameBetweenRequesterAndOwner = sessionService.userId(user).equals(latestUeAcc.getOwnerUserId());
            if (isSameBetweenRequesterAndOwner) {
                response.setCanEdit(true);
                response.setCanView(true);
            }
        } else {
            response.setCanEdit(true);
            response.setCanView(true);
        }

        response.setExtensionId(createAbieExtension(user, roleOfAccManifestId, releaseId));
        return response;
    }

    @Transactional
    public CreateExtensionResponse createGlobalAbieExtension(User user, BieEditAsbiepNode extension) {
        BigInteger releaseId = extension.getReleaseId();
        BigInteger roleOfAccManifestId = dslContext.select(Tables.ACC_MANIFEST.ACC_MANIFEST_ID)
                .from(Tables.ACC_MANIFEST)
                .join(Tables.ACC).on(Tables.ACC_MANIFEST.ACC_ID.eq(Tables.ACC.ACC_ID))
                .where(and(
                        Tables.ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                        Tables.ACC.OBJECT_CLASS_TERM.eq("All Extension")
                ))
                .fetchOneInto(BigInteger.class);

        CreateExtensionResponse response = new CreateExtensionResponse();
        ACC ueAcc = extensionService.getExistsUserExtension(roleOfAccManifestId);

        response.setCanEdit(false);
        response.setCanView(false);

        if (ueAcc != null) {
            ACC latestUeAcc = ueAcc;
            if (ueAcc.getState() == CcState.Candidate) {
                response.setCanEdit(true);
                response.setCanView(true);
            } else if (ueAcc.getState() == CcState.Draft) {
                response.setCanView(true);
            }
            boolean isSameBetweenRequesterAndOwner = sessionService.userId(user).equals(latestUeAcc.getOwnerUserId());
            if (isSameBetweenRequesterAndOwner) {
                response.setCanEdit(true);
                response.setCanView(true);
            }
        } else {
            response.setCanEdit(true);
            response.setCanView(true);
        }
        response.setExtensionId(createAbieExtension(user, roleOfAccManifestId, releaseId));
        return response;
    }

    private BigInteger createAbieExtension(User user, BigInteger roleOfAccManifestId, BigInteger releaseId) {
        BieEditAcc eAcc = bieRepository.getAccByAccManifestId(roleOfAccManifestId);
        ACC ueAcc = extensionService.getExistsUserExtension(roleOfAccManifestId);

        BigInteger manifestId = extensionService.appendUserExtension(eAcc, ueAcc, releaseId, user);
        return manifestId;
    }

    @Transactional
    public void updateTopLevelAbieLastUpdated(User user, BigInteger topLevelAbieId) {
        topLevelAbieRepository.updateTopLevelAbieLastUpdated(sessionService.userId(user), topLevelAbieId);
    }

    @Autowired
    private AbieReadRepository abieReadRepository;

    public AbieNode getAbieDetail(User user, BigInteger topLevelAbieId,
                                  BigInteger accManifestId, String hashPath) {
        return abieReadRepository.getAbieNode(topLevelAbieId, accManifestId, hashPath);
    }

    @Autowired
    private AsbieReadRepository asbieReadRepository;

    public AsbieNode getAsbieDetail(User user, BigInteger topLevelAbieId,
                                    BigInteger asccManifestId, String hashPath) {
        return asbieReadRepository.getAsbieNode(topLevelAbieId, asccManifestId, hashPath);
    }

    @Autowired
    private BbieReadRepository bbieReadRepository;

    public BbieNode getBbieDetail(User user, BigInteger topLevelAbieId,
                                  BigInteger bccManifestId, String hashPath) {
        return bbieReadRepository.getBbieNode(topLevelAbieId, bccManifestId, hashPath);
    }

    @Autowired
    private AsbiepReadRepository asbiepReadRepository;

    public AsbiepNode getAsbiepDetail(User user, BigInteger topLevelAbieId,
                                      BigInteger asccpManifestId, String hashPath) {
        return asbiepReadRepository.getAsbiepNode(topLevelAbieId, asccpManifestId, hashPath);
    }

    @Autowired
    private BbiepReadRepository bbiepReadRepository;

    public BbiepNode getBbiepDetail(User user, BigInteger topLevelAbieId,
                                    BigInteger bccpManifestId, String hashPath) {
        return bbiepReadRepository.getBbiepNode(topLevelAbieId, bccpManifestId, hashPath);
    }

    @Autowired
    private BbieScReadRepository bbieScReadRepository;

    public BbieScNode getBbieScDetail(User user, BigInteger topLevelAbieId,
                                      BigInteger dtScManifestId, String hashPath) {
        return bbieScReadRepository.getBbieScNode(topLevelAbieId, dtScManifestId, hashPath);
    }

    @Autowired
    private DtReadRepository bdtReadRepository;

    public BdtNode getBdtDetail(User user, BigInteger topLevelAbieId,
                                BigInteger dtManifestId) {
        return bdtReadRepository.getBdtNode(topLevelAbieId, dtManifestId);
    }

    public Map<String, BieEditUsed> getBieUsedList(User user, BigInteger topLevelAbieId) {
        List<BieEditUsed> usedList = new ArrayList();
        usedList.addAll(asbieReadRepository.getUsedAsbieList(topLevelAbieId));
        usedList.addAll(bbieReadRepository.getUsedBbieList(topLevelAbieId));
        usedList.addAll(bbieScReadRepository.getUsedBbieScList(topLevelAbieId));
        return usedList.stream().collect(Collectors.toMap(BieEditUsed::getHashPath, Function.identity()));
    }

    public List<BieEditRef> getBieRefList(User user, BigInteger topLevelAbieId) {
        return asbiepReadRepository.getBieRefList(topLevelAbieId);
    }

    // begins supporting dynamic primitive type lists

    @Autowired
    private BdtPriRestriReadRepository bdtPriRestriReadRepository;

    public List<AvailableBdtPriRestri> availableBdtPriRestriListByBccpManifestId(
            User user, BigInteger topLevelAbieId, BigInteger bccpManifestId) {
        return bdtPriRestriReadRepository.availableBdtPriRestriListByBccpManifestId(bccpManifestId);
    }

    @Autowired
    private BdtScPriRestriReadRepository bdtScPriRestriReadRepository;

    public List<AvailableBdtScPriRestri> availableBdtScPriRestriListByBdtScManifestId(
            User user, BigInteger topLevelAbieId, BigInteger bdtScManifestId) {
        return bdtScPriRestriReadRepository.availableBdtScPriRestriListByBdtScManifestId(bdtScManifestId);
    }

    @Autowired
    private CodeListReadRepository codeListReadRepository;

    public List<AvailableCodeList> availableCodeListListByBccpManifestId(
            User user, BigInteger topLevelAbieId, BigInteger bccpManifestId) {
        return codeListReadRepository.availableCodeListByBccpManifestId(bccpManifestId);
    }

    public List<AvailableCodeList> availableCodeListListByBdtScManifestId(
            User user, BigInteger topLevelAbieId, BigInteger bdtScManifestId) {
        return codeListReadRepository.availableCodeListByBdtScManifestId(bdtScManifestId);
    }

    @Autowired
    private AgencyIdListReadRepository agencyIdListReadRepository;

    public List<AvailableAgencyIdList> availableAgencyIdListListByBccpManifestId(
            User user, BigInteger topLevelAbieId, BigInteger bccpManifestId) {
        return agencyIdListReadRepository.availableAgencyIdListByBccpManifestId(bccpManifestId);
    }

    public List<AvailableAgencyIdList> availableAgencyIdListListByBdtScManifestId(
            User user, BigInteger topLevelAbieId, BigInteger bdtScManifestId) {
        return agencyIdListReadRepository.availableAgencyIdListByBdtScManifestId(bdtScManifestId);
    }

    // ends supporting dynamic primitive type lists

    @Transactional
    public void overrideBIE(User user, OverrideBIERequest request) {
        LocalDateTime timestamp = LocalDateTime.now();

        AsbiepNode.Asbiep asbiep = asbiepReadRepository.getAsbiep(
                request.getTopLevelAbieId(), request.getAsbiepHashPath());
        if (asbiep.getRefTopLevelAbieId() != null &&
                asbiep.getRefTopLevelAbieId().equals(request.getOverrideTopLevelAbieId())) {
            return;
        }

        AbieNode.Abie abie = abieReadRepository.getAbieByTopLevelAbieId(request.getOverrideTopLevelAbieId());
        asbiep = asbiepReadRepository.getAsbiepByTopLevelAbieId(
                request.getOverrideTopLevelAbieId());
        asbiep.setHashPath(request.getAsbiepHashPath());
        asbiep.setRoleOfAbieHashPath(abie.getHashPath());

        UpsertAsbiepRequest upsertAsbiepRequest =
                new UpsertAsbiepRequest(user, timestamp, request.getTopLevelAbieId(), asbiep);
        upsertAsbiepRequest.setRefTopLevelAbieId(request.getOverrideTopLevelAbieId());
        asbiepWriteRepository.upsertAsbiep(upsertAsbiepRequest);
    }
}
