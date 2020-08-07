package org.oagi.srt.gateway.http.api.bie_management.service;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.types.ULong;
import org.oagi.srt.data.ACC;
import org.oagi.srt.data.AppUser;
import org.oagi.srt.data.BieState;
import org.oagi.srt.data.TopLevelAsbiep;
import org.oagi.srt.entity.jooq.Tables;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.gateway.http.api.bie_management.data.TopLevelAsbiepRequest;
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
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.redis.event.EventListenerContainer;
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
import org.oagi.srt.repo.component.top_level_asbiep.TopLevelAsbiepWriteRepository;
import org.oagi.srt.repo.component.top_level_asbiep.UpdateTopLevelAsbiepRequest;
import org.oagi.srt.repository.TopLevelAsbiepRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.jooq.impl.DSL.and;
import static org.oagi.srt.entity.jooq.Tables.*;

@Service
@Transactional(readOnly = true)
public class BieEditService implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TopLevelAsbiepRepository topLevelAsbiepRepository;

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

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private EventListenerContainer eventListenerContainer;

    @Autowired
    private TopLevelAsbiepWriteRepository topLevelAsbiepWriteRepository;

    private String PURGE_BIE_EVENT_NAME = "purgeBieEvent";

    @Override
    public void afterPropertiesSet() throws Exception {
        eventListenerContainer.addMessageListener(this,
                "onPurgeBieEventReceived",
                new ChannelTopic(PURGE_BIE_EVENT_NAME));
    }

    private BieEditTreeController getTreeController(User user, BieEditNode node) {
        return getTreeController(user, node.getTopLevelAsbiepId(), node.isDerived(), node.isLocked());
    }

    private BieEditTreeController getTreeController(User user, BigInteger topLevelAsbiepId) {
        return getTreeController(user, topLevelAsbiepId, false, false);
    }

    private BieEditTreeController getTreeController(User user, BigInteger topLevelAsbiepId,
                                                    boolean isDerived, boolean isLocked) {
        DefaultBieEditTreeController bieEditTreeController =
                applicationContext.getBean(DefaultBieEditTreeController.class);

        TopLevelAsbiep topLevelAsbiep = topLevelAsbiepRepository.findById(topLevelAsbiepId);
        bieEditTreeController.initialize(user, topLevelAsbiep);
        if (isDerived || isLocked) {
            // bieEditTreeController.setForceBieUpdate(false);
        }

        return bieEditTreeController;
    }

    @Transactional
    public BieEditAbieNode getRootNode(User user, BigInteger topLevelAsbiepId) {
        BieEditTreeController treeController = getTreeController(user, topLevelAsbiepId);
        return treeController.getRootNode(topLevelAsbiepId);
    }

    @Transactional
    public BieEditAbieNode updateRootNode(User user, TopLevelAsbiepRequest request) {
        UpdateTopLevelAsbiepRequest updateTopLevelAsbiepRequest = new UpdateTopLevelAsbiepRequest(
                user, LocalDateTime.now(), request.getTopLevelAsbiepId(), request.getStatus(), request.getVersion());
        topLevelAsbiepWriteRepository.updateTopLevelAsbiep(updateTopLevelAsbiepRequest);
        BieEditTreeController treeController = getTreeController(user, request.getTopLevelAsbiepId());
        return treeController.getRootNode(request.getTopLevelAsbiepId());
    }

    @Transactional
    public BccForBie getBcc(User user, BigInteger bccId) {
        return bieRepository.getBcc(bccId);
    }

    @Transactional
    public List<BieEditNode> getDescendants(User user, BieEditNode node, boolean hideUnused) {
        BieEditTreeController treeController = getTreeController(user, node);
        return treeController.getDescendants(user, node, hideUnused).stream()
                .map(e -> {
                    if (node.isDerived() || node.isLocked()) {
                        e.setLocked(true);
                    }
                    return e;
                }).collect(Collectors.toList());
    }

    @Transactional
    public BieEditNodeDetail getDetail(User user, BieEditNode node) {
        BieEditTreeController treeController = getTreeController(user, node);
        return treeController.getDetail(node);
    }

    @Transactional
    public void updateState(User user, BigInteger topLevelAsbiepId, BieState state) {
        BieEditTreeController treeController = getTreeController(user, topLevelAsbiepId);
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
                                        user, timestamp, request.getTopLevelAsbiepId(), abie))
                        )
                        .collect(Collectors.toMap(e -> e.getHashPath(), Function.identity()))
        );

        response.setAsbiepDetailMap(
                request.getAsbiepDetails().stream()
                        .map(asbiep ->
                                asbiepWriteRepository.upsertAsbiep(new UpsertAsbiepRequest(
                                        user, timestamp, request.getTopLevelAsbiepId(), asbiep))
                        )
                        .collect(Collectors.toMap(e -> e.getHashPath(), Function.identity()))
        );

        response.setBbiepDetailMap(
                request.getBbiepDetails().stream()
                        .map(bbiep ->
                                bbiepWriteRepository.upsertBbiep(new UpsertBbiepRequest(
                                        user, timestamp, request.getTopLevelAsbiepId(), bbiep
                                ))
                        )
                        .collect(Collectors.toMap(e -> e.getHashPath(), Function.identity()))
        );

        response.setAsbieDetailMap(
                request.getAsbieDetails().stream()
                        .map(asbie ->
                                asbieWriteRepository.upsertAsbie(new UpsertAsbieRequest(
                                        user, timestamp, request.getTopLevelAsbiepId(), asbie
                                ))
                        )
                        .collect(Collectors.toMap(e -> e.getHashPath(), Function.identity()))
        );

        response.setBbieDetailMap(
                request.getBbieDetails().stream()
                        .map(bbie ->
                                bbieWriteRepository.upsertBbie(new UpsertBbieRequest(
                                        user, timestamp, request.getTopLevelAsbiepId(), bbie
                                ))
                        )
                        .collect(Collectors.toMap(e -> e.getHashPath(), Function.identity()))
        );

        response.setBbieScDetailMap(
                request.getBbieScDetails().stream()
                        .map(bbieSc ->
                                bbieScWriteRepository.upsertBbieSc(new UpsertBbieScRequest(
                                        user, timestamp, request.getTopLevelAsbiepId(), bbieSc
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
    public void updateTopLevelAbieLastUpdated(User user, BigInteger topLevelAsbiepId) {
        topLevelAsbiepRepository.updateTopLevelAbieLastUpdated(sessionService.userId(user), topLevelAsbiepId);
    }

    @Autowired
    private AbieReadRepository abieReadRepository;

    public AbieNode getAbieDetail(User user, BigInteger topLevelAsbiepId,
                                  BigInteger accManifestId, String hashPath) {
        return abieReadRepository.getAbieNode(topLevelAsbiepId, accManifestId, hashPath);
    }

    @Autowired
    private AsbieReadRepository asbieReadRepository;

    public AsbieNode getAsbieDetail(User user, BigInteger topLevelAsbiepId,
                                    BigInteger asccManifestId, String hashPath) {
        return asbieReadRepository.getAsbieNode(topLevelAsbiepId, asccManifestId, hashPath);
    }

    @Autowired
    private BbieReadRepository bbieReadRepository;

    public BbieNode getBbieDetail(User user, BigInteger topLevelAsbiepId,
                                  BigInteger bccManifestId, String hashPath) {
        return bbieReadRepository.getBbieNode(topLevelAsbiepId, bccManifestId, hashPath);
    }

    @Autowired
    private AsbiepReadRepository asbiepReadRepository;

    public AsbiepNode getAsbiepDetail(User user, BigInteger topLevelAsbiepId,
                                      BigInteger asccpManifestId, String hashPath) {
        return asbiepReadRepository.getAsbiepNode(topLevelAsbiepId, asccpManifestId, hashPath);
    }

    @Autowired
    private BbiepReadRepository bbiepReadRepository;

    public BbiepNode getBbiepDetail(User user, BigInteger topLevelAsbiepId,
                                    BigInteger bccpManifestId, String hashPath) {
        return bbiepReadRepository.getBbiepNode(topLevelAsbiepId, bccpManifestId, hashPath);
    }

    @Autowired
    private BbieScReadRepository bbieScReadRepository;

    public BbieScNode getBbieScDetail(User user, BigInteger topLevelAsbiepId,
                                      BigInteger dtScManifestId, String hashPath) {
        return bbieScReadRepository.getBbieScNode(topLevelAsbiepId, dtScManifestId, hashPath);
    }

    @Autowired
    private DtReadRepository bdtReadRepository;

    public BdtNode getBdtDetail(User user, BigInteger topLevelAsbiepId,
                                BigInteger dtManifestId) {
        return bdtReadRepository.getBdtNode(topLevelAsbiepId, dtManifestId);
    }

    public List<BieEditUsed> getBieUsedList(User user, BigInteger topLevelAsbiepId) {
        List<BieEditUsed> usedList = new ArrayList();

        asbieReadRepository.getBieRefList(topLevelAsbiepId).stream()
                .filter(e -> e.getRefTopLevelAsbiepId() != null)
                .map(BieEditRef::getRefTopLevelAsbiepId)
                .distinct()
                .forEach(refTopLevelAbieId -> {
                    usedList.addAll(getBieUsedList(user, refTopLevelAbieId));
                });

        usedList.addAll(asbieReadRepository.getUsedAsbieList(topLevelAsbiepId));
        usedList.addAll(bbieReadRepository.getUsedBbieList(topLevelAsbiepId));
        usedList.addAll(bbieScReadRepository.getUsedBbieScList(topLevelAsbiepId));

        return usedList;
    }

    public List<BieEditRef> getBieRefList(User user, BigInteger topLevelAsbiepId) {
        return asbieReadRepository.getBieRefList(topLevelAsbiepId);
    }

    // begins supporting dynamic primitive type lists

    @Autowired
    private BdtPriRestriReadRepository bdtPriRestriReadRepository;

    public List<AvailableBdtPriRestri> availableBdtPriRestriListByBccpManifestId(
            User user, BigInteger topLevelAsbiepId, BigInteger bccpManifestId) {
        return bdtPriRestriReadRepository.availableBdtPriRestriListByBccpManifestId(bccpManifestId);
    }

    @Autowired
    private BdtScPriRestriReadRepository bdtScPriRestriReadRepository;

    public List<AvailableBdtScPriRestri> availableBdtScPriRestriListByBdtScManifestId(
            User user, BigInteger topLevelAsbiepId, BigInteger bdtScManifestId) {
        return bdtScPriRestriReadRepository.availableBdtScPriRestriListByBdtScManifestId(bdtScManifestId);
    }

    @Autowired
    private CodeListReadRepository codeListReadRepository;

    public List<AvailableCodeList> availableCodeListListByBccpManifestId(
            User user, BigInteger topLevelAsbiepId, BigInteger bccpManifestId) {
        return codeListReadRepository.availableCodeListByBccpManifestId(bccpManifestId);
    }

    public List<AvailableCodeList> availableCodeListListByBdtScManifestId(
            User user, BigInteger topLevelAsbiepId, BigInteger bdtScManifestId) {
        return codeListReadRepository.availableCodeListByBdtScManifestId(bdtScManifestId);
    }

    @Autowired
    private AgencyIdListReadRepository agencyIdListReadRepository;

    public List<AvailableAgencyIdList> availableAgencyIdListListByBccpManifestId(
            User user, BigInteger topLevelAsbiepId, BigInteger bccpManifestId) {
        return agencyIdListReadRepository.availableAgencyIdListByBccpManifestId(bccpManifestId);
    }

    public List<AvailableAgencyIdList> availableAgencyIdListListByBdtScManifestId(
            User user, BigInteger topLevelAsbiepId, BigInteger bdtScManifestId) {
        return agencyIdListReadRepository.availableAgencyIdListByBdtScManifestId(bdtScManifestId);
    }

    // ends supporting dynamic primitive type lists

    @Transactional
    public void reuseBIE(User user, ReuseBIERequest request) {
        AppUser requester = sessionService.getAppUser(user);

        TopLevelAsbiepRecord topLevelAsbiepRecord = dslContext.selectFrom(TOP_LEVEL_ASBIEP)
                .where(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.eq(ULong.valueOf(request.getTopLevelAsbiepId())))
                .fetchOne();

        AppUserRecord bieOwnerRecord = dslContext.selectFrom(APP_USER)
                .where(APP_USER.APP_USER_ID.eq(topLevelAsbiepRecord.getOwnerUserId()))
                .fetchOne();

        if (requester.isDeveloper() && bieOwnerRecord.getIsDeveloper() == 0) {
            throw new IllegalArgumentException("Developer does not allow to reuse end user's BIE.");
        }

        if (!topLevelAsbiepRecord.getOwnerUserId().toBigInteger().equals(requester.getAppUserId())) {
            throw new IllegalArgumentException("Requester is not an owner of the target BIE.");
        }
        if (BieState.valueOf(topLevelAsbiepRecord.getState()) != BieState.WIP) {
            throw new IllegalArgumentException("Target BIE cannot edit.");
        }

        AsbieRecord asbieRecord = dslContext.selectFrom(ASBIE)
                .where(and(
                        ASBIE.HASH_PATH.eq(request.getAsbieHashPath()),
                        ASBIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(topLevelAsbiepRecord.getTopLevelAsbiepId())
                ))
                .fetchOne();
        ULong prevToAsbiepId = asbieRecord.getToAsbiepId();

        ULong ownerTopLevelAsbiepOfToAsbiep =
                dslContext.select(ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID)
                        .from(ASBIEP)
                        .where(ASBIEP.ASBIEP_ID.eq(asbieRecord.getToAsbiepId()))
                        .fetchOneInto(ULong.class);

        boolean isReused = !asbieRecord.getOwnerTopLevelAsbiepId().equals(ownerTopLevelAsbiepOfToAsbiep);
        if (isReused) {
            throw new IllegalArgumentException("Target BIE already has reused BIE.");
        }

        ULong reuseAsbiepId = dslContext.select(TOP_LEVEL_ASBIEP.ASBIEP_ID)
                .from(TOP_LEVEL_ASBIEP)
                .where(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.eq(ULong.valueOf(request.getReuseTopLevelAsbiepId())))
                .fetchOneInto(ULong.class);

        asbieRecord.setToAsbiepId(reuseAsbiepId);
        asbieRecord.setLastUpdatedBy(ULong.valueOf(requester.getAppUserId()));
        asbieRecord.setLastUpdateTimestamp(LocalDateTime.now());
        asbieRecord.update(
                ASBIE.TO_ASBIEP_ID,
                ASBIE.LAST_UPDATED_BY,
                ASBIE.LAST_UPDATE_TIMESTAMP);

        // Delete orphan ASBIEP record.
        dslContext.deleteFrom(ASBIEP)
                .where(ASBIEP.ASBIEP_ID.eq(prevToAsbiepId))
                .execute();

        PurgeBieEvent event = new PurgeBieEvent(
                asbieRecord.getOwnerTopLevelAsbiepId().toBigInteger());
        /*
         * Message Publishing
         */
        redisTemplate.convertAndSend(PURGE_BIE_EVENT_NAME, event);
    }

    /**
     * This method is invoked by 'purgeBieEvent' channel subscriber.
     *
     * @param purgeBieEvent
     */
    @Transactional
    public void onPurgeBieEventReceived(PurgeBieEvent purgeBieEvent) {
        ULong topLevelAsbiepId = ULong.valueOf(purgeBieEvent.getTopLevelAsbiepId());

        while (true) {
            List<ULong> unreferencedAbieList = dslContext.select(ABIE.ABIE_ID)
                    .from(ABIE)
                    .leftJoin(ASBIEP).on(and(
                            ABIE.ABIE_ID.eq(ASBIEP.ROLE_OF_ABIE_ID),
                            ABIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID)
                    ))
                    .leftJoin(TOP_LEVEL_ASBIEP).on(ASBIEP.ASBIEP_ID.eq(TOP_LEVEL_ASBIEP.ASBIEP_ID))
                    .where(and(
                            ABIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(topLevelAsbiepId),
                            TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.isNull(),
                            ASBIEP.ASBIEP_ID.isNull()
                    ))
                    .fetchInto(ULong.class);

            if (unreferencedAbieList.isEmpty()) {
                break;
            }

            List<Record2<ULong, ULong>> unreferencedAsbieList = dslContext.select(ASBIE.ASBIE_ID, ASBIE.TO_ASBIEP_ID)
                    .from(ASBIE)
                    .where(and(
                            ASBIE.FROM_ABIE_ID.in(unreferencedAbieList),
                            ASBIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(topLevelAsbiepId)
                    ))
                    .fetch();

            if (!unreferencedAsbieList.isEmpty()) {
                dslContext.deleteFrom(ASBIE)
                        .where(ASBIE.ASBIE_ID.in(
                                unreferencedAsbieList.stream()
                                        .map(e -> e.get(ASBIE.ASBIE_ID)).collect(Collectors.toList())
                        ))
                        .execute();

                dslContext.deleteFrom(ASBIEP)
                        .where(and(
                                ASBIEP.ASBIEP_ID.in(unreferencedAsbieList.stream()
                                        .map(e -> e.get(ASBIE.TO_ASBIEP_ID)).collect(Collectors.toList())),
                                ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID.eq(topLevelAsbiepId)
                        ))
                        .execute();
            }

            List<Record2<ULong, ULong>> unreferencedBbieList = dslContext.select(BBIE.BBIE_ID, BBIE.TO_BBIEP_ID)
                    .from(BBIE)
                    .where(and(
                            BBIE.FROM_ABIE_ID.in(unreferencedAbieList),
                            BBIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(topLevelAsbiepId)
                    ))
                    .fetch();

            if (!unreferencedBbieList.isEmpty()) {
                dslContext.deleteFrom(BBIE_SC)
                        .where(and(
                                BBIE_SC.BBIE_ID.in(unreferencedBbieList.stream()
                                        .map(e -> e.get(BBIE.BBIE_ID)).collect(Collectors.toList())),
                                BBIE_SC.OWNER_TOP_LEVEL_ASBIEP_ID.eq(topLevelAsbiepId)
                        ))
                        .execute();

                dslContext.deleteFrom(BBIE)
                        .where(BBIE.BBIE_ID.in(
                                unreferencedBbieList.stream()
                                        .map(e -> e.get(BBIE.BBIE_ID)).collect(Collectors.toList())
                        ))
                        .execute();

                dslContext.deleteFrom(BBIEP)
                        .where(and(
                                BBIEP.BBIEP_ID.in(unreferencedBbieList.stream()
                                        .map(e -> e.get(BBIE.TO_BBIEP_ID)).collect(Collectors.toList())),
                                BBIEP.OWNER_TOP_LEVEL_ASBIEP_ID.eq(topLevelAsbiepId)
                        ))
                        .execute();
            }

            dslContext.deleteFrom(ABIE)
                    .where(ABIE.ABIE_ID.in(unreferencedAbieList))
                    .execute();
        }
    }

    @Transactional
    public void removeReusedBIE(User user, RemoveReusedBIERequest request) {
        AppUser requester = sessionService.getAppUser(user);

        AsbieRecord asbieRecord = dslContext.selectFrom(ASBIE)
                .where(and(ASBIE.HASH_PATH.eq(request.getAsbieHashPath()),
                        ASBIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(ULong.valueOf(request.getTopLevelAsbiepId()))))
                .fetchOne();

        if (asbieRecord == null) {
            throw new IllegalArgumentException("Can not fount target BIE.");
        }

        TopLevelAsbiepRecord topLevelAsbiepRecord = dslContext.selectFrom(TOP_LEVEL_ASBIEP)
                .where(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.eq(asbieRecord.getOwnerTopLevelAsbiepId()))
                .fetchOne();

        AppUserRecord bieOwnerRecord = dslContext.selectFrom(APP_USER)
                .where(APP_USER.APP_USER_ID.eq(topLevelAsbiepRecord.getOwnerUserId()))
                .fetchOne();

        if (requester.isDeveloper() && bieOwnerRecord.getIsDeveloper() == 0) {
            throw new IllegalArgumentException("Developer does not allow to remove the end user's reused BIE.");
        }

        if (!topLevelAsbiepRecord.getOwnerUserId().toBigInteger().equals(requester.getAppUserId())) {
            throw new IllegalArgumentException("Requester is not an owner of the target BIE.");
        }
        if (BieState.valueOf(topLevelAsbiepRecord.getState()) != BieState.WIP) {
            throw new IllegalArgumentException("Target BIE cannot edit.");
        }

        AsbiepRecord asbiepRecord = dslContext.selectFrom(ASBIEP)
                .where(ASBIEP.ASBIEP_ID.eq(asbieRecord.getToAsbiepId()))
                .fetchOne();

        boolean isReused = !asbiepRecord.getOwnerTopLevelAsbiepId().equals(asbieRecord.getOwnerTopLevelAsbiepId());
        if (!isReused) {
            throw new IllegalArgumentException("Target BIE does not have reused BIE.");
        }

        dslContext.deleteFrom(ASBIE).where(ASBIE.ASBIE_ID.eq(asbieRecord.getAsbieId())).execute();
    }
}
