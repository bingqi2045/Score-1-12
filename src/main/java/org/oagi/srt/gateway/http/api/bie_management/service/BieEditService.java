package org.oagi.srt.gateway.http.api.bie_management.service;

import org.oagi.srt.data.ACC;
import org.oagi.srt.data.BieState;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.data.TopLevelAbie;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.*;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree.*;
import org.oagi.srt.gateway.http.api.bie_management.service.edit_tree.BieEditTreeController;
import org.oagi.srt.gateway.http.api.bie_management.service.edit_tree.DefaultBieEditTreeController;
import org.oagi.srt.gateway.http.api.cc_management.service.CcListService;
import org.oagi.srt.repository.TopLevelAbieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    private CcListService ccListService;

    private BieEditTreeController getTreeController(User user, BieEditNode node) {
        return getTreeController(user, node.getTopLevelAbieId());
    }

    private BieEditTreeController getTreeController(User user, long topLevelAbieId) {
        DefaultBieEditTreeController bieEditTreeController =
                applicationContext.getBean(DefaultBieEditTreeController.class);

        TopLevelAbie topLevelAbie = topLevelAbieRepository.findById(topLevelAbieId);
        bieEditTreeController.initialize(user, topLevelAbie);

        return bieEditTreeController;
    }

    @Transactional
    public BieEditAbieNode getRootNode(User user, long topLevelAbieId) {
        BieEditTreeController treeController = getTreeController(user, topLevelAbieId);
        return treeController.getRootNode(topLevelAbieId);
    }

    @Transactional
    public BccForBie getBcc(User user, long bccId){
        return bieRepository.getBcc(bccId);
    }

    @Transactional
    public List<BieEditNode> getDescendants(User user, BieEditNode node) {
        BieEditTreeController treeController = getTreeController(user, node);
        return treeController.getDescendants(node);
    }

    @Transactional
    public BieEditNodeDetail getDetail(User user, BieEditNode node) {
        BieEditTreeController treeController = getTreeController(user, node);
        return treeController.getDetail(node);
    }

    @Transactional
    public void updateState(User user, long topLevelAbieId, BieState state) {
        BieEditTreeController treeController = getTreeController(user, topLevelAbieId);
        treeController.updateState(state);
    }


    @Transactional
    public BieEditUpdateResponse updateDetails(User user, BieEditUpdateRequest request) {
        long topLevelAbieId = request.getTopLevelAbieId();
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

        return response;
    }

    @Transactional
    public long createLocalAbieExtension(User user, BieEditAsbiepNode extension) {
        long asccpId = extension.getAsccpId();
        long releaseId = extension.getReleaseId();

        long roleOfAccId = bieRepository.getRoleOfAccIdByAsccpId(asccpId);
        BieEditAcc eAcc = bieRepository.getAccByCurrentAccId(roleOfAccId, releaseId);
        ACC ueAcc = getExistsUserExtension(roleOfAccId, releaseId);
        if (ueAcc != null) {
            return ueAcc.getCurrentAccId();
        } else {
            long ueAccId = bieRepository.appendLocalUserExtension(eAcc, ueAcc, asccpId, releaseId, user);
            return ueAccId;
        }
    }

    @Transactional
    public long createGlobalAbieExtension(User user, BieEditAsbiepNode extension) {
        return 0L;
    }

    private ACC getExistsUserExtension(long accId, long releaseId) {
        for (BieEditAscc ascc : bieRepository.getAsccListByFromAccId(accId, releaseId)) {
            BieEditAsccp asccp = bieRepository.getAsccpByCurrentAsccpId(ascc.getToAsccpId(), releaseId);

            ACC ueAcc = ccListService.getAccByCurrentAccId(asccp.getRoleOfAccId(), releaseId);
            if (ueAcc.getOagisComponentType() == OagisComponentType.UserExtensionGroup.getValue()) {
                return ueAcc;
            }
        }
        return null;
    }

}
