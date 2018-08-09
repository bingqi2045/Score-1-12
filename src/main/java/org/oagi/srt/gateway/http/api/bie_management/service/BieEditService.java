package org.oagi.srt.gateway.http.api.bie_management.service;

import org.oagi.srt.gateway.http.api.DataAccessForbiddenException;
import org.oagi.srt.gateway.http.api.bie_management.data.BieState;
import org.oagi.srt.gateway.http.api.bie_management.data.TopLevelAbie;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.BieEditNode;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.BieEditUpdateRequest;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.BieEditUpdateResponse;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree.*;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.helper.SrtJdbcTemplate;
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
    private SrtJdbcTemplate jdbcTemplate;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private BieRepository repository;

    @Autowired
    private ApplicationContext applicationContext;

    private BieEditTreeController getTreeController(User user, BieEditNode node) {
        return getTreeController(user, node.getTopLevelAbieId());
    }

    private BieEditTreeController getTreeController(User user, long topLevelAbieId) {
        DefaultBieEditTreeController bieEditTreeController =
                applicationContext.getBean(DefaultBieEditTreeController.class);

        TopLevelAbie topLevelAbie = repository.getTopLevelAbieById(topLevelAbieId);
        bieEditTreeController.initialize(user, topLevelAbie);

        return bieEditTreeController;
    }

    @Transactional
    public BieEditAbieNode getRootNode(User user, long topLevelAbieId) {
        BieEditTreeController treeController = getTreeController(user, topLevelAbieId);
        return treeController.getRootNode(topLevelAbieId);
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
}
