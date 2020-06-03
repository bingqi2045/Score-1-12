package org.oagi.srt.gateway.http.api.bie_management.service;

import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.ACC;
import org.oagi.srt.data.BieState;
import org.oagi.srt.data.TopLevelAbie;
import org.oagi.srt.entity.jooq.Tables;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.*;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree.*;
import org.oagi.srt.gateway.http.api.bie_management.service.edit_tree.BieEditTreeController;
import org.oagi.srt.gateway.http.api.bie_management.service.edit_tree.DefaultBieEditTreeController;
import org.oagi.srt.gateway.http.api.cc_management.data.CcState;
import org.oagi.srt.gateway.http.api.cc_management.service.ExtensionService;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.repo.component.abie.AbieNode;
import org.oagi.srt.repo.component.abie.AbieReadRepository;
import org.oagi.srt.repo.component.asbie.AsbieNode;
import org.oagi.srt.repo.component.asbie.AsbieReadRepository;
import org.oagi.srt.repo.component.asbiep.AsbiepNode;
import org.oagi.srt.repo.component.asbiep.AsbiepReadRepository;
import org.oagi.srt.repo.component.bbie.BbieNode;
import org.oagi.srt.repo.component.bbie.BbieReadRepository;
import org.oagi.srt.repo.component.bbie_sc.BbieScNode;
import org.oagi.srt.repo.component.bbie_sc.BbieScReadRepository;
import org.oagi.srt.repo.component.bbiep.BbiepNode;
import org.oagi.srt.repo.component.bbiep.BbiepReadRepository;
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
import java.util.List;

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


    @Transactional
    public BieEditUpdateResponse updateDetails(User user, BieEditUpdateRequest request) {
        BigInteger topLevelAbieId = request.getTopLevelAbieId();
        BieEditTreeController treeController = getTreeController(user, topLevelAbieId);

        BieEditUpdateResponse response = new BieEditUpdateResponse();
        BieEditAbieNodeDetail abieNodeDetail = request.getAbieNodeDetail();
        if (abieNodeDetail != null) {
            response.setAbieNodeResult(treeController.updateDetail(abieNodeDetail));
        }

        for (BieEditAsbiepNodeDetail asbiepNodeDetail : request.getAsbiepNodeDetails()) {
            response.getAsbiepNodeResults().put(asbiepNodeDetail.getGuid(),
                    treeController.updateDetail(asbiepNodeDetail));
        }

        for (BieEditBbiepNodeDetail bbiepNodeDetail : request.getBbiepNodeDetails()) {
            response.getBbiepNodeResults().put(bbiepNodeDetail.getGuid(),
                    treeController.updateDetail(bbiepNodeDetail));
        }

        for (BieEditBbieScNodeDetail bbieScNodeDetail : request.getBbieScNodeDetails()) {
            response.getBbieScNodeResults().put(bbieScNodeDetail.getGuid(),
                    treeController.updateDetail(bbieScNodeDetail));
        }

        this.updateTopLevelAbieLastUpdated(user, topLevelAbieId);

        return response;
    }

    @Transactional
    public CreateExtensionResponse createLocalAbieExtension(User user, BieEditAsbiepNode extension) {
        BigInteger asccpId = extension.getAsccpManifestId();
        BigInteger releaseId = extension.getReleaseId();
        BigInteger roleOfAccId = bieRepository.getRoleOfAccIdByAsccpId(asccpId);

        CreateExtensionResponse response = new CreateExtensionResponse();

        ACC ueAcc = extensionService.getExistsUserExtension(roleOfAccId, releaseId);

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

        response.setExtensionId(createAbieExtension(user, roleOfAccId, releaseId));
        return response;
    }

    @Transactional
    public CreateExtensionResponse createGlobalAbieExtension(User user, BieEditAsbiepNode extension) {
        BigInteger releaseId = extension.getReleaseId();
        BigInteger roleOfAccId = dslContext.select(Tables.ACC.ACC_ID)
                .from(Tables.ACC)
                .join(Tables.ACC_MANIFEST)
                .on(Tables.ACC_MANIFEST.ACC_ID.eq(Tables.ACC.ACC_ID))
                .where(and(
                        Tables.ACC_MANIFEST.RELEASE_ID.eq(ULong.valueOf(releaseId)),
                        Tables.ACC.OBJECT_CLASS_TERM.eq("All Extension"),
                        Tables.ACC.STATE.eq(CcState.Candidate.name())))
                .fetchOneInto(BigInteger.class);

        CreateExtensionResponse response = new CreateExtensionResponse();
        ACC ueAcc = extensionService.getExistsUserExtension(roleOfAccId, releaseId);

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
        response.setExtensionId(createAbieExtension(user, roleOfAccId, releaseId));
        return response;
    }

    private BigInteger createAbieExtension(User user, BigInteger roleOfAccId, BigInteger releaseId) {
        BieEditAcc eAcc = bieRepository.getAccByAccId(roleOfAccId, releaseId);
        ACC ueAcc = extensionService.getExistsUserExtension(roleOfAccId, releaseId);

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
                                BigInteger dtManifestId, String hashPath) {
        return bdtReadRepository.getBdtNode(topLevelAbieId, dtManifestId, hashPath);
    }
}
