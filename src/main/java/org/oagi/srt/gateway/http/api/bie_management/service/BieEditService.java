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
import org.oagi.srt.gateway.http.api.cc_management.service.ExtensionPathHandler;
import org.oagi.srt.gateway.http.api.cc_management.service.ExtensionService;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.repository.TopLevelAbieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.gateway.http.api.cc_management.data.CcState.Editing;
import static org.oagi.srt.gateway.http.api.cc_management.data.CcState.Published;

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
    public BccForBie getBcc(User user, long bccId) {
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
        return createAbieExtension(user, roleOfAccId, releaseId);
    }

    @Transactional
    public long createGlobalAbieExtension(User user, BieEditAsbiepNode extension) {
        long releaseId = extension.getReleaseId();
        long roleOfAccId = dslContext.select(Tables.ACC.ACC_ID)
                .from(Tables.ACC)
                .where(and(
                        Tables.ACC.OBJECT_CLASS_TERM.eq("All Extension"),
                        Tables.ACC.REVISION_NUM.eq(0)))
                .fetchOneInto(Long.class);
        return createAbieExtension(user, roleOfAccId, releaseId);
    }

    private long createAbieExtension(User user, long roleOfAccId, long releaseId) {
        BieEditAcc eAcc = bieRepository.getAccByCurrentAccId(roleOfAccId, releaseId);
        ACC ueAcc = extensionService.getExistsUserExtension(roleOfAccId, releaseId);
        if (ueAcc != null) {
            CcState ueAccState = CcState.valueOf(ueAcc.getState());
            if (ueAccState == Editing) {
                boolean isSameBetweenRequesterAndOwner = sessionService.userId(user) == ueAcc.getOwnerUserId();
                if (!isSameBetweenRequesterAndOwner) {
                    throw new IllegalArgumentException("The component is currently edited by another user.");
                }
            }

            if (ueAccState != Published) {
                return ueAcc.getAccId();
            }
        }

        long ueAccId = extensionService.appendUserExtension(eAcc, ueAcc, releaseId, user);
        return ueAccId;
    }

    public List<BieUserExtRevision> getBieUserExtRevisions(User user, long topLevelAbieId) {
        long userId = sessionService.userId(user);
        return dslContext.select(
                Tables.BIE_USER_EXT_REVISION.BIE_USER_EXT_REVISION_ID,
                Tables.BIE_USER_EXT_REVISION.EXT_ABIE_ID,
                Tables.BIE_USER_EXT_REVISION.EXT_ACC_ID,
                Tables.BIE_USER_EXT_REVISION.USER_EXT_ACC_ID)
                .from(Tables.BIE_USER_EXT_REVISION)
                .join(Tables.TOP_LEVEL_ABIE)
                .on(Tables.BIE_USER_EXT_REVISION.TOP_LEVEL_ABIE_ID.eq(Tables.TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID))
                .where(and(
                        Tables.BIE_USER_EXT_REVISION.TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)),
                        Tables.BIE_USER_EXT_REVISION.REVISED_INDICATOR.eq((byte) 0),
                        Tables.TOP_LEVEL_ABIE.OWNER_USER_ID.eq(ULong.valueOf(userId))
                )).fetchInto(BieUserExtRevision.class);
    }

    @Transactional
    public void discardBieUserExtRevisions(User user, long topLevelAbieId) {
        long userId = sessionService.userId(user);
        dslContext.deleteFrom(Tables.BIE_USER_EXT_REVISION)
                .where(Tables.BIE_USER_EXT_REVISION.TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)))
                .execute();
    }

    public Collection<String> getExtensionPath(long bieUserExtRevisionId) {
        BieUserExtRevision bieUserExtRevision = dslContext.select(
                Tables.BIE_USER_EXT_REVISION.EXT_ABIE_ID,
                Tables.BIE_USER_EXT_REVISION.EXT_ACC_ID,
                Tables.BIE_USER_EXT_REVISION.USER_EXT_ACC_ID,
                Tables.BIE_USER_EXT_REVISION.TOP_LEVEL_ABIE_ID)
                .from(Tables.BIE_USER_EXT_REVISION)
                .where(Tables.BIE_USER_EXT_REVISION.BIE_USER_EXT_REVISION_ID.eq(ULong.valueOf(bieUserExtRevisionId)))
                .fetchOptionalInto(BieUserExtRevision.class).orElse(null);

        if (bieUserExtRevision == null) {
            return Collections.emptyList();
        }

        long releaseId = dslContext.select(Tables.TOP_LEVEL_ABIE.RELEASE_ID)
                .from(Tables.TOP_LEVEL_ABIE)
                .where(Tables.TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(bieUserExtRevision.getTopLevelAbieId())))
                .fetchOneInto(Long.class);

        ExtensionPathHandler extensionPathHandler =
                applicationContext.getBean(ExtensionPathHandler.class, releaseId);

        long accId = dslContext.select(Tables.ACC.CURRENT_ACC_ID)
                .from(Tables.ACC)
                .join(Tables.ABIE).on(Tables.ACC.ACC_ID.eq(Tables.ABIE.BASED_ACC_ID))
                .join(Tables.TOP_LEVEL_ABIE).on(Tables.ABIE.ABIE_ID.eq(Tables.TOP_LEVEL_ABIE.ABIE_ID))
                .where(Tables.TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(bieUserExtRevision.getTopLevelAbieId())))
                .fetchOneInto(Long.class);

        return extensionPathHandler.getPath(accId, bieUserExtRevision.getExtAccId());
    }
}
