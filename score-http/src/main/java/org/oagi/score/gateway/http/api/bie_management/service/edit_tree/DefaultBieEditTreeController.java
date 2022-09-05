package org.oagi.score.gateway.http.api.bie_management.service.edit_tree;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record4;
import org.oagi.score.data.SeqKeySupportable;
import org.oagi.score.data.TopLevelAsbiep;
import org.oagi.score.gateway.http.api.bie_management.data.bie_edit.*;
import org.oagi.score.gateway.http.api.bie_management.data.bie_edit.tree.*;
import org.oagi.score.gateway.http.api.bie_management.service.BieRepository;
import org.oagi.score.gateway.http.api.cc_management.repository.CcNodeRepository;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.repo.BusinessInformationEntityRepository;
import org.oagi.score.repo.api.ScoreRepositoryFactory;
import org.oagi.score.repo.api.bie.BieReadRepository;
import org.oagi.score.repo.api.bie.model.BieState;
import org.oagi.score.repo.api.bie.model.GetReuseBieListRequest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;
import org.oagi.score.service.common.data.AccessPrivilege;
import org.oagi.score.service.common.data.OagisComponentType;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.jooq.impl.DSL.and;
import static org.jooq.impl.DSL.inline;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
@Transactional
public class DefaultBieEditTreeController implements BieEditTreeController {

    private static final String DEFAULT_TEXT_CONTENT_TYPE = "json";

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private BieRepository repository;

    @Autowired
    private CcNodeRepository ccNodeRepository;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private BusinessInformationEntityRepository bieRepository;

    @Autowired
    private ScoreRepositoryFactory scoreRepositoryFactory;

    @Autowired
    private RedissonClient redissonClient;

    private boolean initialized;
    private AuthenticatedPrincipal user;
    private TopLevelAsbiep topLevelAsbiep;
    private AccessPrivilege accessPrivilege;
    private BieState state;
    private boolean forceBieUpdate;

    public void initialize(AuthenticatedPrincipal user, TopLevelAsbiep topLevelAsbiep) {
        this.user = user;
        this.topLevelAsbiep = topLevelAsbiep;

        this.state = topLevelAsbiep.getState();
        this.forceBieUpdate = true;

        String userId = sessionService.userId(user);
        accessPrivilege = AccessPrivilege.Prohibited;
        switch (this.state) {
            case Initiating:
                accessPrivilege = AccessPrivilege.Unprepared;
                break;

            case WIP:
                if (topLevelAsbiep.getOwnerUserId().equals(userId)) {
                    accessPrivilege = AccessPrivilege.CanEdit;
                } else {
                    accessPrivilege = AccessPrivilege.CanView;
                }
                break;

            case QA:
                if (topLevelAsbiep.getOwnerUserId().equals(userId)) {
                    accessPrivilege = AccessPrivilege.CanMove;
                } else {
                    accessPrivilege = AccessPrivilege.CanView;
                }

                break;

            case Production:
                accessPrivilege = AccessPrivilege.CanView;
                break;
        }

        this.initialized = true;
    }

    public boolean hasReuseBie(AuthenticatedPrincipal user, String topLevelAsbiepId) {
        BieReadRepository bieReadRepository = scoreRepositoryFactory.createBieReadRepository();
        return !bieReadRepository.getReuseBieList(new GetReuseBieListRequest(sessionService.asScoreUser(user))
                .withTopLevelAsbiepId(topLevelAsbiepId, true))
                .getTopLevelAsbiepList().isEmpty();
    }

    private boolean isForceBieUpdate() {
        return forceBieUpdate;
    }

    public BieEditAbieNode getRootNode(String topLevelAsbiepId) {
        BieEditAbieNode rootNode = dslContext.select(
                TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID,
                TOP_LEVEL_ASBIEP.RELEASE_ID,
                TOP_LEVEL_ASBIEP.STATE.as("top_level_asbiep_state"),
                TOP_LEVEL_ASBIEP.OWNER_USER_ID,
                TOP_LEVEL_ASBIEP.VERSION,
                TOP_LEVEL_ASBIEP.STATUS,
                RELEASE.RELEASE_NUM,
                APP_USER.LOGIN_ID,
                ASCCP.GUID,
                ASCCP.PROPERTY_TERM.as("name"),
                ASBIEP.ASBIEP_ID,
                ASBIEP.BASED_ASCCP_MANIFEST_ID.as("asccp_manifest_id"),
                ABIE.ABIE_ID,
                ABIE.BASED_ACC_MANIFEST_ID.as("acc_manifest_id"),
                inline("abie").as("type"),
                inline(true).as("used"))
                .from(TOP_LEVEL_ASBIEP)
                .join(ASBIEP).on(and(
                        TOP_LEVEL_ASBIEP.ASBIEP_ID.eq(ASBIEP.ASBIEP_ID),
                        ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID.eq(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID)
                ))
                .join(ABIE).on(and(
                        ASBIEP.ROLE_OF_ABIE_ID.eq(ABIE.ABIE_ID),
                        ABIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID)
                ))

                .join(ASCCP_MANIFEST).on(ASBIEP.BASED_ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                .join(RELEASE).on(TOP_LEVEL_ASBIEP.RELEASE_ID.eq(RELEASE.RELEASE_ID))
                .join(APP_USER).on(TOP_LEVEL_ASBIEP.OWNER_USER_ID.eq(APP_USER.APP_USER_ID))
                .where(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.eq(topLevelAsbiepId))
                .fetchOneInto(BieEditAbieNode.class);
        rootNode.setHasChild(hasChild(rootNode));
        rootNode.setAccess(accessPrivilege);

        return rootNode;
    }

    // have to check @hakju
    private boolean hasChild(BieEditAbieNode abieNode) {
        String fromAccManifestId;
        String topLevelAsbiepId = abieNode.getTopLevelAsbiepId();
        String releaseId = abieNode.getReleaseId();
        BieEditAcc acc = null;
        if (topLevelAsbiepId != null) {
            fromAccManifestId = repository.getAccManifestIdByTopLevelAsbiepId(topLevelAsbiepId, releaseId);
        } else {
            acc = repository.getAcc(abieNode.getAccManifestId(), releaseId);
            fromAccManifestId = acc.getAccManifestId();
        }

        if (repository.getAsccListByFromAccManifestId(fromAccManifestId).size() > 0) {
            return true;
        }
        if (repository.getBccListByFromAccManifestId(fromAccManifestId).size() > 0) {
            return true;
        }

        if (acc == null) {
            acc = repository.getAccByAccManifestId(fromAccManifestId);
        }
        if (acc != null && acc.getBasedAccManifestId() != null) {
            BieEditAbieNode basedAbieNode = new BieEditAbieNode();
            basedAbieNode.setReleaseId(releaseId);

            acc = repository.getAccByAccManifestId(acc.getBasedAccManifestId());
            basedAbieNode.setAccManifestId(acc.getAccManifestId());
            return hasChild(basedAbieNode);
        }

        return false;
    }

    @Override
    public List<BieEditNode> getDescendants(AuthenticatedPrincipal user, BieEditNode node, boolean hideUnused) {
        /*
         * If this profile BIE is in Editing state, descendants of given node will create during this process,
         * and this must be thread-safe.
         */
        RLock lock = null;
        String lockName = getClass().getSimpleName() + ".getDescendants(" +
                node.getType() + ", " + topLevelAsbiep.getTopLevelAsbiepId() + ")";
        lock = redissonClient.getLock(lockName);

        try {
            if (lock != null) {
                try {
                    boolean locked = lock.tryLock(10, TimeUnit.SECONDS);
                    if (!locked) {
                        throw new IllegalStateException("Lock is held by another thread/process.");
                    }
                } catch (InterruptedException e) {
                    throw new IllegalStateException("Lock acquisition is cancelled by interrupt.");
                }
            }

            switch (node.getType()) {
                case "abie":
                    return getDescendants((BieEditAbieNode) node, hideUnused);
                case "asbiep":
                    return getDescendants((BieEditAsbiepNode) node, hideUnused);
                case "bbiep":
                    return getDescendants((BieEditBbiepNode) node, hideUnused);
            }
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }

        return Collections.emptyList();
    }

    private List<BieEditNode> getDescendants(BieEditAbieNode abieNode, boolean hideUnused) {
        Map<String, BieEditAsbie> asbieMap;
        Map<String, BieEditBbie> bbieMap;

        String asbiepId = abieNode.getAsbiepId();
        String abieId = repository.getAbieByAsbiepId(asbiepId).getAbieId();
        asbieMap = repository.getAsbieListByFromAbieId(abieId, abieNode).stream()
                .collect(toMap(BieEditAsbie::getBasedAsccManifestId, Function.identity()));
        bbieMap = repository.getBbieListByFromAbieId(abieId, abieNode).stream()
                .collect(toMap(BieEditBbie::getBasedBccManifestId, Function.identity()));

        String accManifestId = repository.getRoleOfAccManifestIdByAsbiepId(asbiepId);

        List<BieEditNode> children = getChildren(asbieMap, bbieMap, abieId, accManifestId, abieNode, hideUnused);
        return children;
    }

    private List<BieEditNode> getDescendants(BieEditAsbiepNode asbiepNode, boolean hideUnused) {
        Map<String, BieEditAsbie> asbieMap;
        Map<String, BieEditBbie> bbieMap;

        String accManifestId = asbiepNode.getAccManifestId();
        String abieId = asbiepNode.getAbieId();

        if (abieId == null && isForceBieUpdate()) {
            BieEditAcc acc = repository.getAcc(asbiepNode.getAccManifestId(), asbiepNode.getReleaseId());
            AccManifestRecord accManifest = ccNodeRepository.getAccManifestByAccManifestId(asbiepNode.getAccManifestId());

            abieId = bieRepository.insertAbie()
                    .setUserId(sessionService.userId(user))
                    .setTopLevelAsbiepId(asbiepNode.getTopLevelAsbiepId())
                    .setAccManifestId(accManifest.getAccManifestId())
                    .execute();
        }

        if (abieId != null) {
            asbieMap = repository.getAsbieListByFromAbieId(abieId, asbiepNode).stream()
                    .collect(toMap(BieEditAsbie::getBasedAsccManifestId, Function.identity()));
            bbieMap = repository.getBbieListByFromAbieId(abieId, asbiepNode).stream()
                    .collect(toMap(BieEditBbie::getBasedBccManifestId, Function.identity()));
        } else {
            asbieMap = Collections.emptyMap();
            bbieMap = Collections.emptyMap();
        }

        List<BieEditNode> children = getChildren(asbieMap, bbieMap, abieId, accManifestId, asbiepNode, hideUnused);
        return children;
    }

    private List<BieEditNode> getChildren(
            Map<String, BieEditAsbie> asbieManifestMap,
            Map<String, BieEditBbie> bbieManifestMap,
            String fromAbieId, String accManifestId,
            BieEditNode node, boolean hideUnused) {
        List<BieEditNode> children = new ArrayList();

        List<SeqKeySupportable> assocList = getAssociationsByAccManifestId(accManifestId);
        int seqKey = 1;
        for (SeqKeySupportable assoc : assocList) {
            if (assoc instanceof BieEditAscc) {
                BieEditAscc ascc = (BieEditAscc) assoc;
                BieEditAsbie asbie = asbieManifestMap.get(ascc.getAsccManifestId());
                BieEditAsbiepNode asbiepNode = createAsbiepNode(fromAbieId, seqKey++, asbie, ascc);
                if (asbiepNode == null) {
                    seqKey--;
                    continue;
                }

                OagisComponentType oagisComponentType = ccNodeRepository.getOagisComponentTypeByAccManifestId(asbiepNode.getAccManifestId());
                if (oagisComponentType.isGroup()) {
                    children.addAll(getDescendants(asbiepNode, hideUnused));
                } else {
                    if (hideUnused && (asbie == null || !StringUtils.hasLength(asbie.getAsbieId()) || !asbie.isUsed())) {
                        seqKey--;
                        continue;
                    }

                    children.add(asbiepNode);
                }
            } else {
                BieEditBcc bcc = (BieEditBcc) assoc;
                BieEditBbie bbie = bbieManifestMap.get(bcc.getBccManifestId());
                if (hideUnused && (bbie == null || !StringUtils.hasLength(bbie.getBbieId()) || !bbie.isUsed())) {
                    continue;
                }
                BieEditBbiepNode bbiepNode;
                if (bcc.isAttribute()) {
                    bbiepNode = createBbiepNode(fromAbieId, 0, bbie, bcc, hideUnused);
                } else {
                    bbiepNode = createBbiepNode(fromAbieId, seqKey++, bbie, bcc, hideUnused);
                }
                children.add(bbiepNode);
            }
        }

        return children;
    }

    private List<SeqKeySupportable> getAssociationsByAccManifestId(String accManifestId) {
        Stack<BieEditAcc> accStack = getAccStack(accManifestId);

        List<BieEditBcc> attributeBccList = new ArrayList();
        List<SeqKeySupportable> assocList = new ArrayList();

        while (!accStack.isEmpty()) {
            BieEditAcc acc = accStack.pop();

            String fromAccManifestId = acc.getAccManifestId();
            List<BieEditAscc> asccList = repository.getAsccListByFromAccManifestId(fromAccManifestId, true);
            List<BieEditBcc> bccList = repository.getBccListByFromAccManifestId(fromAccManifestId, true);

            attributeBccList.addAll(
                    bccList.stream().filter(e -> e.isAttribute()).collect(Collectors.toList()));

            List<SeqKeySupportable> tmpAssocList = new ArrayList();
            tmpAssocList.addAll(asccList);
            tmpAssocList.addAll(bccList.stream().filter(e -> !e.isAttribute()).collect(Collectors.toList()));
            tmpAssocList = tmpAssocList.stream()
                    .sorted(Comparator.comparingInt(SeqKeySupportable::getSeqKey))
                    .collect(Collectors.toList());

            for (SeqKeySupportable assoc : tmpAssocList) {
                if (assoc instanceof BieEditAscc) {
                    OagisComponentType roleOfAccType =
                            repository.getOagisComponentTypeOfAccByAsccpManifestId(((BieEditAscc) assoc).getToAsccpManifestId());
                    if (roleOfAccType.isGroup()) {
                        String roleOfAccManifestId = repository.getRoleOfAccManifestIdByAsccpManifestId(
                                ((BieEditAscc) assoc).getToAsccpManifestId());

                        assocList.addAll(
                                getAssociationsByAccManifestId(roleOfAccManifestId)
                        );
                    } else {
                        assocList.add(assoc);
                    }
                } else {
                    assocList.add(assoc);
                }
            }
        }

        assocList.addAll(0, attributeBccList);
        return assocList;
    }

    private Stack<BieEditAcc> getAccStack(String accManifestId) {
        Stack<BieEditAcc> accStack = new Stack();
        BieEditAcc acc = repository.getAccByAccManifestId(accManifestId);
        /*
         * Issue #708
         * If the UEG's state is not 'Published', its children couldn't get it by the logic above.
         */
        if (acc == null) {
            return accStack;
        }
        accStack.push(acc);

        while (acc.getBasedAccManifestId() != null) {
            acc = repository.getAccByAccManifestId(acc.getBasedAccManifestId());
            accStack.push(acc);
        }

        return accStack;
    }

    private BieEditAsbiepNode createAsbiepNode(String fromAbieId, int seqKey,
                                               BieEditAsbie asbie, BieEditAscc ascc) {
        BieEditAsbiepNode asbiepNode = new BieEditAsbiepNode();

        String topLevelAsbiepId = topLevelAsbiep.getTopLevelAsbiepId();
        String releaseId = topLevelAsbiep.getReleaseId();

        asbiepNode.setTopLevelAsbiepId(topLevelAsbiepId);
        asbiepNode.setReleaseId(releaseId);
        asbiepNode.setType("asbiep");
        asbiepNode.setGuid(ascc.getGuid());
        asbiepNode.setAsccManifestId(ascc.getAsccManifestId());

        BieEditAsccp asccp = repository.getAsccpByAsccpManifestId(ascc.getToAsccpManifestId());
        asbiepNode.setAsccpManifestId(asccp.getAsccpManifestId());

        if (!StringUtils.hasLength(asbiepNode.getName())) {
            asbiepNode.setName(asccp.getPropertyTerm());
        }

        BieEditAcc acc;
        if ((asbiepNode.getAccManifestId() == null) || !StringUtils.hasLength(asbiepNode.getAccManifestId())) {
            acc = repository.getAccByAccManifestId(asccp.getRoleOfAccManifestId());
            if (acc == null) {
                return null;
            }
            asbiepNode.setAccManifestId(acc.getAccManifestId());
        } else {
            acc = repository.getAcc(asbiepNode.getAccManifestId(), releaseId);
        }

        if (asbie == null && isForceBieUpdate()) {
            String abieId = bieRepository.insertAbie()
                    .setUserId(sessionService.userId(user))
                    .setTopLevelAsbiepId(asbiepNode.getTopLevelAsbiepId())
                    .setAccManifestId(acc.getAccManifestId())
                    .execute();

            AsbiepRecord asbiepRecord = repository.createAsbiep(user, asccp.getAsccpManifestId(), abieId, topLevelAsbiepId);
            String asbiepId = asbiepRecord.getAsbiepId();
            AsbieRecord asbieRecord = repository.createAsbie(user, fromAbieId, asbiepId, ascc.getAsccManifestId(),
                    seqKey, topLevelAsbiepId);

            asbie = new BieEditAsbie();
            asbie.setAsbieId(asbieRecord.getAsbieId());
            asbie.setBasedAsccManifestId(ascc.getAsccManifestId());
            asbie.setFromAbieId(fromAbieId);
            asbie.setToAsbiepId(asbiepId);
            asbie.setUsed(asbieRecord.getIsUsed() == 1);
            asbie.setCardinalityMin(asbieRecord.getCardinalityMin());
            asbie.setCardinalityMax(asbieRecord.getCardinalityMax());
        }

        if (asbie != null) {
            asbiepNode.setAsbieId(asbie.getAsbieId());
            asbiepNode.setAsbiepId(asbie.getToAsbiepId());

            BieEditAbie abie = repository.getAbieByAsbiepId(asbie.getToAsbiepId());
            asbiepNode.setAbieId(abie.getAbieId());
            asbiepNode.setAccManifestId(abie.getBasedAccManifestId());

            asbiepNode.setName(repository.getAsccpPropertyTermByAsbiepId(asbie.getToAsbiepId()));
            asbiepNode.setUsed(asbie.isUsed());
            asbiepNode.setRequired(ascc.getCardinalityMin() > 0);
        }

        asbiepNode.setHasChild(hasChild(asbiepNode));

        return asbiepNode;
    }

    private BieEditBbiepNode createBbiepNode(String fromAbieId, int seqKey,
                                             BieEditBbie bbie, BieEditBcc bcc,
                                             boolean hideUnused) {
        BieEditBbiepNode bbiepNode = new BieEditBbiepNode();

        String topLevelAsbiepId = topLevelAsbiep.getTopLevelAsbiepId();
        String releaseId = topLevelAsbiep.getReleaseId();

        bbiepNode.setTopLevelAsbiepId(topLevelAsbiepId);
        bbiepNode.setReleaseId(releaseId);
        bbiepNode.setType("bbiep");
        bbiepNode.setGuid(bcc.getGuid());
        bbiepNode.setAttribute(bcc.isAttribute());

        bbiepNode.setBccManifestId(bcc.getBccManifestId());
        BieEditBccp bccp = repository.getBccpByBccpManifestId(bcc.getToBccpManifestId());
        bbiepNode.setBccpManifestId(bccp.getBccpManifestId());
        bbiepNode.setBdtManifestId(bccp.getBdtManifestId());

        if (!StringUtils.hasLength(bbiepNode.getName())) {
            bbiepNode.setName(bccp.getPropertyTerm());
        }

        if (bbie == null && isForceBieUpdate()) {
            BbiepRecord bbiepRecord = repository.createBbiep(user, bcc.getToBccpManifestId(), topLevelAsbiepId);
            String bbiepId = bbiepRecord.getBbiepId();
            BbieRecord bbieRecord = repository.createBbie(user, fromAbieId, bbiepId,
                    bcc.getBccManifestId(), bccp.getBdtManifestId(), seqKey, topLevelAsbiepId);
            String bbieId = bbieRecord.getBbieId();

            bbie = new BieEditBbie();
            bbie.setBasedBccManifestId(bcc.getBccManifestId());
            bbie.setBbieId(bbieId);
            bbie.setFromAbieId(fromAbieId);
            bbie.setToBbiepId(bbiepId);
            bbie.setUsed(bbieRecord.getIsUsed() == 1);
            bbie.setCardinalityMin(bbieRecord.getCardinalityMin());
            bbie.setCardinalityMax(bbieRecord.getCardinalityMax());
        }

        if (bbie != null) {
            bbiepNode.setBbieId(bbie.getBbieId());
            bbiepNode.setBbiepId(bbie.getToBbiepId());

            bbiepNode.setName(repository.getBccpPropertyTermByBbiepId(bbie.getToBbiepId()));
            bbiepNode.setUsed(bbie.isUsed());
            bbiepNode.setRequired(bcc.getCardinalityMin() > 0);
        }

        bbiepNode.setHasChild(hasChild(bbiepNode, hideUnused));

        return bbiepNode;
    }

    public boolean hasChild(BieEditAsbiepNode asbiepNode) {
        BieEditAbieNode abieNode = new BieEditAbieNode();
        abieNode.setReleaseId(asbiepNode.getReleaseId());
        abieNode.setAccManifestId(asbiepNode.getAccManifestId());

        return hasChild(abieNode);
    }

    public boolean hasChild(BieEditBbiepNode bbiepNode, boolean hideUnused) {
        if (hideUnused) {
            return repository.getCountBbieScByBbieIdAndIsUsedAndOwnerTopLevelAsbiepId(
                    bbiepNode.getBbieId(), true, bbiepNode.getTopLevelAsbiepId()) > 0;

        } else {
            BieEditBccp bccp = repository.getBccp(bbiepNode.getBccpManifestId(), bbiepNode.getReleaseId());
            return repository.getCountDtScByOwnerDtId(bccp.getBdtManifestId()) > 0;
        }
    }

    private List<BieEditNode> getDescendants(BieEditBbiepNode bbiepNode, boolean hideUnused) {
        String bbiepId = bbiepNode.getBbiepId();
        String topLevelAsbiepId = bbiepNode.getTopLevelAsbiepId();
        BieEditBccp bccp;
        if (StringUtils.hasLength(bbiepId)) {
            BieEditBbiep bbiep = repository.getBbiep(bbiepId, topLevelAsbiepId);
            bccp = repository.getBccp(bbiep.getBasedBccpId(), bbiepNode.getReleaseId());
        } else {
            if (hideUnused) {
                return Collections.emptyList();
            }

            bccp = repository.getBccp(bbiepNode.getBccpManifestId(), bbiepNode.getReleaseId());
        }

        List<BieEditNode> children = new ArrayList();
        List<BieEditBdtSc> bdtScList = repository.getBdtScListByOwnerDtId(bccp.getBdtManifestId());
        String bbieId = bbiepNode.getBbieId();
        for (BieEditBdtSc bdtSc : bdtScList) {
            BieEditBbieScNode bbieScNode = new BieEditBbieScNode();

            bbieScNode.setTopLevelAsbiepId(topLevelAsbiepId);
            bbieScNode.setReleaseId(bbiepNode.getReleaseId());
            bbieScNode.setType("bbie_sc");
            bbieScNode.setGuid(bdtSc.getGuid());
            bbieScNode.setName(bdtSc.getName());

            String dtScManifestId = bdtSc.getDtScManifestId();
            if (StringUtils.hasLength(bbieId)) {
                BieEditBbieSc bbieSc = repository.getBbieScIdByBbieIdAndDtScId(bbieId, dtScManifestId, topLevelAsbiepId);
                if (bbieSc == null) {
                    if (isForceBieUpdate()) {
                        String bbieScId = repository.createBbieSc(user, bbieId, dtScManifestId, topLevelAsbiepId);
                        bbieSc = new BieEditBbieSc();
                        bbieSc.setBbieScId(bbieScId);
                    }
                }

                if (hideUnused && (bbieSc == null || !StringUtils.hasLength(bbieSc.getBbieScId()) || !bbieSc.isUsed())) {
                    continue;
                }
                bbieScNode.setBbieScId(bbieSc.getBbieScId());
                bbieScNode.setUsed(bbieSc.isUsed());
            }

            bbieScNode.setDtScManifestId(dtScManifestId);

            children.add(bbieScNode);
        }

        return children;
    }

    @Override
    public BieEditNodeDetail getDetail(BieEditNode node) {
        switch (node.getType()) {
            case "abie":
                return getDetail((BieEditAbieNode) node);
            case "asbiep":
                return getDetail((BieEditAsbiepNode) node);
            case "bbiep":
                return getDetail((BieEditBbiepNode) node);
            case "bbie_sc":
                return getDetail((BieEditBbieScNode) node);
            default:
                throw new IllegalStateException();
        }
    }

    private BieEditNodeDetail getDetail(BieEditAbieNode abieNode) {
        BieEditAbieNodeDetail detail = dslContext.select(
                ABIE.REMARK,
                ABIE.BIZ_TERM,
                ABIE.DEFINITION)
                .from(ABIE)
                .where(ABIE.ABIE_ID.eq(abieNode.getAbieId()))
                .fetchOneInto(BieEditAbieNodeDetail.class);

        return detail.append(abieNode);
    }

    private BieEditAsbiepNodeDetail getDetail(BieEditAsbiepNode asbiepNode) {

        BieEditAsbiepNodeDetail detail;
        if (StringUtils.hasLength(asbiepNode.getAsbieId())) {
            detail = dslContext.select(
                    ASBIE.CARDINALITY_MIN.as("bie_cardinality_min"),
                    ASBIE.CARDINALITY_MAX.as("bie_cardinality_max"),
                    ASBIE.IS_USED.as("used"),
                    ASBIE.IS_NILLABLE.as("bie_nillable"),
                    ASBIE.DEFINITION.as("context_definition")
            ).from(ASBIE)
                    .where(ASBIE.ASBIE_ID.eq(asbiepNode.getAsbieId()))
                    .fetchOneInto(BieEditAsbiepNodeDetail.class);
        } else {
            detail = dslContext.select(
                    ASCC.CARDINALITY_MIN.as("bie_cardinality_min"),
                    ASCC.CARDINALITY_MAX.as("bie_cardinality_max"))
                    .from(ASCC)
                    .join(ASCC_MANIFEST).on(ASCC.ASCC_ID.eq(ASCC_MANIFEST.ASCC_ID))
                    .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(asbiepNode.getAsccManifestId()))
                    .fetchOneInto(BieEditAsbiepNodeDetail.class);
        }

        if (StringUtils.hasLength(asbiepNode.getAsbiepId())) {
            detail.setAsbiepBizTerm(dslContext.select(
                    ASBIEP.BIZ_TERM).from(ASBIEP)
                    .where(ASBIEP.ASBIEP_ID.eq(asbiepNode.getAsbiepId()))
                    .fetchOneInto(String.class));

            detail.setAsbiepRemark(dslContext.select(
                    ASBIEP.REMARK).from(ASBIEP)
                    .where(ASBIEP.ASBIEP_ID.eq(asbiepNode.getAsbiepId()))
                    .fetchOneInto(String.class));
        }

        if (StringUtils.hasLength(asbiepNode.getAsccManifestId())) {
            Record2<Integer, Integer> res = dslContext.select(
                    ASCC.CARDINALITY_MIN,
                    ASCC.CARDINALITY_MAX)
                    .from(ASCC)
                    .join(ASCC_MANIFEST).on(ASCC.ASCC_ID.eq(ASCC_MANIFEST.ASCC_ID))
                    .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(asbiepNode.getAsccManifestId()))
                    .fetchOne();

            detail.setCcCardinalityMin(res.get(ASCC.CARDINALITY_MIN));
            detail.setCcCardinalityMax(res.get(ASCC.CARDINALITY_MAX));
            detail.setRequired(detail.getCcCardinalityMin() > 0);
        }

        if (StringUtils.hasLength(asbiepNode.getAsccpManifestId())) {
            Record1<Byte> ccNillable = dslContext.select(
                    ASCCP.IS_NILLABLE)
                    .from(ASCCP)
                    .join(ASCCP_MANIFEST).on(ASCCP.ASCCP_ID.eq(ASCCP_MANIFEST.ASCCP_ID))
                    .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(asbiepNode.getAsccpManifestId()))
                    .fetchOne();

            detail.setCcNillable(ccNillable.get(ASCCP.IS_NILLABLE) == 1);
        }

        detail.setAsccDefinition(dslContext.select(
                ASCC.DEFINITION).from(ASCC)
                .join(ASCC_MANIFEST).on(ASCC.ASCC_ID.eq(ASCC_MANIFEST.ASCC_ID))
                .where(ASCC_MANIFEST.ASCC_MANIFEST_ID.eq(asbiepNode.getAsccManifestId()))
                .fetchOneInto(String.class));

        detail.setAsccpDefinition(dslContext.select(
                ASCCP.DEFINITION).from(ASCCP)
                .join(ASCCP_MANIFEST).on(ASCCP.ASCCP_ID.eq(ASCCP_MANIFEST.ASCCP_ID))
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(asbiepNode.getAsccpManifestId()))
                .fetchOneInto(String.class));

        detail.setAccDefinition(dslContext.select(
                ACC.DEFINITION).from(ACC)
                .join(ACC_MANIFEST).on(ACC.ACC_ID.eq(ACC_MANIFEST.ACC_ID))
                .where(ACC_MANIFEST.ACC_MANIFEST_ID.eq(asbiepNode.getAccManifestId()))
                .fetchOneInto(String.class));

        return detail.append(asbiepNode);
    }

    private BieEditBbiepNodeDetail getDetail(BieEditBbiepNode bbiepNode) {
        BieEditBbiepNodeDetail detail;

        if (StringUtils.hasLength(bbiepNode.getBbieId())) {
            detail = dslContext.select(
                    BBIE.CARDINALITY_MIN.as("bie_cardinality_min"),
                    BBIE.CARDINALITY_MAX.as("bie_cardinality_max"),
                    BBIE.IS_USED.as("used"),
                    BBIE.BDT_PRI_RESTRI_ID,
                    BBIE.CODE_LIST_ID,
                    BBIE.AGENCY_ID_LIST_ID,
                    BBIE.DEFAULT_VALUE.as("bie_default_value"),
                    BBIE.FIXED_VALUE.as("bie_fixed_value"),
                    BBIE.IS_NILLABLE.as("bie_nillable"),
                    BBIE.DEFINITION.as("context_definition"),
                    BBIE.EXAMPLE
            ).from(BBIE)
                    .where(BBIE.BBIE_ID.eq(bbiepNode.getBbieId()))
                    .fetchOneInto(BieEditBbiepNodeDetail.class);

            /* Issue #762 */
            if (detail.getAgencyIdListId() != null) {
                detail.setBdtPriRestriId(null);
                detail.setCodeListId(null);
            } else if (detail.getCodeListId() != null) {
                detail.setBdtPriRestriId(null);
                detail.setAgencyIdListId(null);
            } else if (detail.getBdtPriRestriId() != null) {
                detail.setCodeListId(null);
                detail.setAgencyIdListId(null);
            }
        } else {
            detail = dslContext.select(
                    BCC.CARDINALITY_MIN.as("bie_cardinality_min"),
                    BCC.CARDINALITY_MAX.as("bie_cardinality_max"),
                    BCC.DEFAULT_VALUE.as("bie_default_value"),
                    BCC.FIXED_VALUE.as("bie_fixed_value"),
                    BCC.IS_NILLABLE.as("bie_nillable"))
                    .from(BCC)
                    .join(BCC_MANIFEST).on(BCC.BCC_ID.eq(BCC_MANIFEST.BCC_ID))
                    .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(bbiepNode.getBccManifestId()))
                    .fetchOneInto(BieEditBbiepNodeDetail.class);
        }

        if (StringUtils.hasLength(bbiepNode.getBbiepId())) {
            Record4<String, String, String, String> rs =
                    dslContext.select(BBIEP.BIZ_TERM, BBIEP.REMARK, BCCP.BDT_ID, DT.DEN)
                            .from(BBIEP)
                            .join(BCCP_MANIFEST).on(BBIEP.BASED_BCCP_MANIFEST_ID.eq(BCCP_MANIFEST.BCCP_MANIFEST_ID))
                            .join(BCCP).on(BCCP_MANIFEST.BCCP_ID.eq(BCCP.BCCP_ID))
                            .join(DT).on(BCCP.BDT_ID.eq(DT.DT_ID))
                            .where(BBIEP.BBIEP_ID.eq(bbiepNode.getBbiepId()))
                            .fetchOne();

            detail.setBizTerm(rs.getValue(BBIEP.BIZ_TERM));
            detail.setRemark(rs.getValue(BBIEP.REMARK));
            detail.setBdtId(rs.getValue(BCCP.BDT_ID));
            detail.setBdtDen(rs.getValue(DT.DEN).replaceAll("_ ", " "));
        } else {
            Record2<String, String> rs = dslContext.select(
                    DT.DEN,
                    BCCP.BDT_ID).from(BCCP)
                    .join(DT).on(BCCP.BDT_ID.eq(DT.DT_ID))
                    .join(BCCP_MANIFEST).on(BCCP.BCCP_ID.eq(BCCP_MANIFEST.BCCP_ID))
                    .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(bbiepNode.getBccpManifestId())).fetchOne();
            detail.setBdtDen(rs.getValue(DT.DEN));
            detail.setBdtId(rs.getValue(BCCP.BDT_ID));
        }

        if (!StringUtils.hasLength(bbiepNode.getBbieId())) {
            String defaultBdtPriRestriId = repository.getDefaultBdtPriRestriIdByBdtId(detail.getBdtManifestId());
            detail.setBdtPriRestriId(defaultBdtPriRestriId);
        }

        if (StringUtils.hasLength(bbiepNode.getBccManifestId())) {
            BccRecord bccRecord = dslContext.select(BCC.fields())
                    .from(BCC)
                    .join(BCC_MANIFEST).on(BCC.BCC_ID.eq(BCC_MANIFEST.BCC_ID))
                    .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(bbiepNode.getBccManifestId())).fetchOneInto(BccRecord.class);
            detail.setCcCardinalityMin(bccRecord.getCardinalityMin());
            detail.setCcCardinalityMax(bccRecord.getCardinalityMax());
            detail.setCcDefaultValue(bccRecord.getDefaultValue());
            detail.setCcFixedValue(bccRecord.getFixedValue());
            detail.setCcNillable(bccRecord.getIsNillable() == 1);
            detail.setRequired(detail.getCcCardinalityMin() > 0);
        }

        BieEditBdtPriRestri bdtPriRestri = getBdtPriRestri(bbiepNode);
        detail.setXbtList(bdtPriRestri.getXbtList());
        detail.setCodeLists(bdtPriRestri.getCodeLists());
        detail.setAgencyIdLists(bdtPriRestri.getAgencyIdLists());
        detail.setAssociationDefinition(dslContext.select(
                BCC.DEFINITION).from(BCC)
                .join(BCC_MANIFEST).on(BCC.BCC_ID.eq(BCC_MANIFEST.BCC_ID))
                .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(bbiepNode.getBccManifestId()))
                .fetchOneInto(String.class)
        );

        detail.setComponentDefinition(dslContext.select(
                BCCP.DEFINITION).from(BCCP)
                .join(BCCP_MANIFEST).on(BCCP.BCCP_ID.eq(BCCP_MANIFEST.BCCP_ID))
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(bbiepNode.getBccpManifestId()))
                .fetchOneInto(String.class)
        );

        return detail.append(bbiepNode);
    }

    private BieEditBdtPriRestri getBdtPriRestri(BieEditBbiepNode bbiepNode) {
        String bdtManifestId = bbiepNode.getBdtManifestId();

        List<BieEditXbt> bieEditXbtList = dslContext.select(
                BDT_PRI_RESTRI.BDT_PRI_RESTRI_ID.as("pri_restri_id"),
                BDT_PRI_RESTRI.IS_DEFAULT, XBT.XBT_ID, XBT.NAME.as("xbt_name"))
                .from(BDT_PRI_RESTRI)
                .join(DT_MANIFEST).on(BDT_PRI_RESTRI.BDT_ID.eq(DT_MANIFEST.DT_ID))
                .join(CDT_AWD_PRI_XPS_TYPE_MAP).on(BDT_PRI_RESTRI.CDT_AWD_PRI_XPS_TYPE_MAP_ID.eq(CDT_AWD_PRI_XPS_TYPE_MAP.CDT_AWD_PRI_XPS_TYPE_MAP_ID))
                .join(XBT).on(CDT_AWD_PRI_XPS_TYPE_MAP.XBT_ID.eq(XBT.XBT_ID))
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(bdtManifestId))
                .fetchInto(BieEditXbt.class);

        List<BieEditCodeList> bieEditCodeLists = dslContext.select(
                CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID, CODE_LIST_MANIFEST.BASED_CODE_LIST_MANIFEST_ID,
                CODE_LIST.CODE_LIST_ID, CODE_LIST.BASED_CODE_LIST_ID,
                BDT_PRI_RESTRI.IS_DEFAULT, CODE_LIST.NAME.as("code_list_name"))
                .from(BDT_PRI_RESTRI)
                .join(DT_MANIFEST).on(BDT_PRI_RESTRI.BDT_ID.eq(DT_MANIFEST.DT_ID))
                .join(CODE_LIST_MANIFEST).on(BDT_PRI_RESTRI.CODE_LIST_ID.eq(CODE_LIST_MANIFEST.CODE_LIST_ID))
                .join(CODE_LIST).on(CODE_LIST_MANIFEST.CODE_LIST_ID.eq(CODE_LIST.CODE_LIST_ID))
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(bdtManifestId))
                .fetchInto(BieEditCodeList.class);

        List<BieEditAgencyIdList> bieEditAgencyIdLists = dslContext.select(
                AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_MANIFEST_ID, AGENCY_ID_LIST_MANIFEST.BASED_AGENCY_ID_LIST_MANIFEST_ID,
                AGENCY_ID_LIST.AGENCY_ID_LIST_ID, AGENCY_ID_LIST.BASED_AGENCY_ID_LIST_ID,
                BDT_PRI_RESTRI.IS_DEFAULT, AGENCY_ID_LIST.NAME.as("agency_id_list_name"))
                .from(BDT_PRI_RESTRI)
                .join(DT_MANIFEST).on(BDT_PRI_RESTRI.BDT_ID.eq(DT_MANIFEST.DT_ID))
                .join(AGENCY_ID_LIST_MANIFEST).on(BDT_PRI_RESTRI.AGENCY_ID_LIST_ID.eq(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_ID))
                .join(AGENCY_ID_LIST).on(AGENCY_ID_LIST_MANIFEST.AGENCY_ID_LIST_ID.eq(AGENCY_ID_LIST.AGENCY_ID_LIST_ID))
                .where(DT_MANIFEST.DT_MANIFEST_ID.eq(bdtManifestId))
                .fetchInto(BieEditAgencyIdList.class);

        if (bieEditCodeLists.isEmpty() && bieEditAgencyIdLists.isEmpty()) {
            bieEditCodeLists = getAllCodeLists();
            bieEditAgencyIdLists = getAllAgencyIdLists();
        } else {
            if (!bieEditCodeLists.isEmpty()) {
                List<BieEditCodeList> basedCodeLists = getBieEditCodeListByBasedCodeListIds(
                        bieEditCodeLists.stream().filter(e -> e.getBasedCodeListManifestId() != null)
                                .map(e -> e.getBasedCodeListManifestId()).collect(Collectors.toList())
                );
                List<BieEditCodeList> basedCodeLists2 = getCodeListsByBasedCodeListManifestId(bieEditCodeLists.get(0).getCodeListManifestId());
                basedCodeLists2.clear();
                for (int i = 0; i < bieEditCodeLists.size(); i++) {
                    basedCodeLists2.addAll(getCodeListsByBasedCodeListManifestId(bieEditCodeLists.get(i).getCodeListManifestId()));
                }
                bieEditCodeLists.addAll(0, basedCodeLists);
                bieEditCodeLists.addAll(0, basedCodeLists2);
                basedCodeLists2.clear();
                for (int i = 0; i < bieEditCodeLists.size(); i++) {
                    basedCodeLists2.addAll(getCodeListsByBasedCodeListManifestId(bieEditCodeLists.get(i).getCodeListManifestId()));
                }
                bieEditCodeLists.addAll(basedCodeLists2);
                Set<BieEditCodeList> set = new HashSet<BieEditCodeList>(bieEditCodeLists);
                bieEditCodeLists.clear();
                bieEditCodeLists.addAll(set); // remove dupplicate elements
            }
        }

        BieEditBdtPriRestri bdtPriRestri = new BieEditBdtPriRestri();

        bieEditXbtList.sort(Comparator.comparing(BieEditXbt::getXbtName, String::compareTo));
        bdtPriRestri.setXbtList(bieEditXbtList);

        bieEditCodeLists.sort(Comparator.comparing(BieEditCodeList::getCodeListName, String::compareTo));
        bdtPriRestri.setCodeLists(bieEditCodeLists);

        bieEditAgencyIdLists.sort(Comparator.comparing(BieEditAgencyIdList::getAgencyIdListName, String::compareTo));
        bdtPriRestri.setAgencyIdLists(bieEditAgencyIdLists);

        return bdtPriRestri;
    }

    private BieEditBbieScNodeDetail getDetail(BieEditBbieScNode bbieScNode) {
        BieEditBbieScNodeDetail detail;
        if (StringUtils.hasLength(bbieScNode.getBbieScId())) {
            detail = dslContext.select(
                    BBIE_SC.CARDINALITY_MIN.as("bie_cardinality_min"),
                    BBIE_SC.CARDINALITY_MAX.as("bie_cardinality_max"),
                    BBIE_SC.IS_USED.as("used"),
                    BBIE_SC.DT_SC_PRI_RESTRI_ID,
                    BBIE_SC.CODE_LIST_ID,
                    BBIE_SC.AGENCY_ID_LIST_ID,
                    BBIE_SC.DEFAULT_VALUE.as("bie_default_value"),
                    BBIE_SC.FIXED_VALUE.as("bie_fixed_value"),
                    BBIE_SC.BIZ_TERM,
                    BBIE_SC.REMARK,
                    BBIE_SC.DEFINITION.as("context_definition"),
                    BBIE_SC.EXAMPLE
            )
                    .from(BBIE_SC)
                    .where(BBIE_SC.BBIE_SC_ID.eq(bbieScNode.getBbieScId()))
                    .fetchOneInto(BieEditBbieScNodeDetail.class);

            /* Issue #762 */
            if (detail.getAgencyIdListId() != null) {
                detail.setDtScPriRestriId(null);
                detail.setCodeListId(null);
            } else if (detail.getCodeListId() != null) {
                detail.setDtScPriRestriId(null);
                detail.setAgencyIdListId(null);
            } else if (detail.getDtScPriRestriId() != null) {
                detail.setCodeListId(null);
                detail.setAgencyIdListId(null);
            }
        } else {
            detail = dslContext.select(
                    DT_SC.CARDINALITY_MIN.as("bie_cardinality_min"),
                    DT_SC.CARDINALITY_MAX.as("bie_cardinality_max"),
                    DT_SC.DEFAULT_VALUE.as("bie_default_value"),
                    DT_SC.FIXED_VALUE.as("bie_fixed_value")
            )
                    .from(DT_SC)
                    .join(DT_SC_MANIFEST).on(DT_SC.DT_SC_ID.eq(DT_SC_MANIFEST.DT_SC_ID))
                    .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(bbieScNode.getDtScManifestId()))
                    .fetchOneInto(BieEditBbieScNodeDetail.class);
        }

        if (!StringUtils.hasLength(bbieScNode.getBbieScId())) {
            String defaultDtScPriRestriId = repository.getDefaultDtScPriRestriIdByDtScManifestId(bbieScNode.getDtScManifestId());
            detail.setDtScPriRestriId(defaultDtScPriRestriId);
        }

        if (StringUtils.hasLength(bbieScNode.getDtScManifestId())) {
            DtScRecord dtScRecord = dslContext.select(DT_SC.fields())
                    .from(DT_SC)
                    .join(DT_SC_MANIFEST).on(DT_SC.DT_SC_ID.eq(DT_SC_MANIFEST.DT_SC_ID))
                    .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(bbieScNode.getDtScManifestId()))
                    .fetchOneInto(DtScRecord.class);

            detail.setCcCardinalityMin(dtScRecord.getCardinalityMin());
            detail.setCcCardinalityMax(dtScRecord.getCardinalityMax());
            detail.setCcDefaultValue(dtScRecord.getDefaultValue());
            detail.setCcFixedValue(dtScRecord.getFixedValue());
        }

        BieEditBdtScPriRestri bdtScPriRestri = getBdtScPriRestri(bbieScNode);
        detail.setXbtList(bdtScPriRestri.getXbtList());
        detail.setCodeLists(bdtScPriRestri.getCodeLists());
        detail.setAgencyIdLists(bdtScPriRestri.getAgencyIdLists());
        detail.setComponentDefinition(
                dslContext.select(DT_SC.DEFINITION)
                        .from(DT_SC)
                        .join(DT_SC_MANIFEST).on(DT_SC.DT_SC_ID.eq(DT_SC_MANIFEST.DT_SC_ID))
                        .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(bbieScNode.getDtScManifestId()))
                        .fetchOneInto(String.class)
        );
        return detail.append(bbieScNode);
    }

    private BieEditBdtScPriRestri getBdtScPriRestri(BieEditBbieScNode bbieScNode) {
        String dtScManifestId = bbieScNode.getDtScManifestId();

        List<BieEditXbt> bieEditXbtList = dslContext.select(
                BDT_SC_PRI_RESTRI.BDT_SC_PRI_RESTRI_ID.as("pri_restri_id"),
                BDT_SC_PRI_RESTRI.IS_DEFAULT,
                XBT.XBT_ID,
                XBT.NAME.as("xbt_name"))
                .from(BDT_SC_PRI_RESTRI)
                .join(DT_SC_MANIFEST).on(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(DT_SC_MANIFEST.DT_SC_ID))
                .join(CDT_SC_AWD_PRI_XPS_TYPE_MAP)
                .on(BDT_SC_PRI_RESTRI.CDT_SC_AWD_PRI_XPS_TYPE_MAP_ID
                        .eq(CDT_SC_AWD_PRI_XPS_TYPE_MAP.CDT_SC_AWD_PRI_XPS_TYPE_MAP_ID))
                .join(XBT).on(CDT_SC_AWD_PRI_XPS_TYPE_MAP.XBT_ID.eq(XBT.XBT_ID))
                .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(dtScManifestId))
                .fetchInto(BieEditXbt.class);

        List<BieEditCodeList> bieEditCodeLists = dslContext.select(
                CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID,
                CODE_LIST_MANIFEST.BASED_CODE_LIST_MANIFEST_ID,
                BDT_SC_PRI_RESTRI.IS_DEFAULT,
                CODE_LIST.NAME.as("code_list_name"))
                .from(BDT_SC_PRI_RESTRI)
                .join(DT_SC_MANIFEST).on(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(DT_SC_MANIFEST.DT_SC_ID))
                .join(CODE_LIST).on(BDT_SC_PRI_RESTRI.CODE_LIST_ID.eq(CODE_LIST.CODE_LIST_ID))
                .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(dtScManifestId))
                .fetchInto(BieEditCodeList.class);

        List<BieEditAgencyIdList> bieEditAgencyIdLists = dslContext.select(
                AGENCY_ID_LIST.AGENCY_ID_LIST_ID,
                BDT_SC_PRI_RESTRI.IS_DEFAULT,
                AGENCY_ID_LIST.NAME.as("agency_id_list_name"))
                .from(BDT_SC_PRI_RESTRI)
                .join(DT_SC_MANIFEST).on(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(DT_SC_MANIFEST.DT_SC_ID))
                .join(AGENCY_ID_LIST).on(
                        BDT_SC_PRI_RESTRI.AGENCY_ID_LIST_ID.eq(AGENCY_ID_LIST.AGENCY_ID_LIST_ID))
                .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(dtScManifestId))
                .fetchInto(BieEditAgencyIdList.class);

        if (bieEditCodeLists.isEmpty() && bieEditAgencyIdLists.isEmpty()) {
            bieEditCodeLists = getAllCodeLists();
            bieEditAgencyIdLists = getAllAgencyIdLists();
        } else {
            if (!bieEditCodeLists.isEmpty()) {
                List<BieEditCodeList> basedCodeLists = getBieEditCodeListByBasedCodeListIds(
                        bieEditCodeLists.stream().filter(e -> e.getBasedCodeListManifestId() != null)
                                .map(e -> e.getBasedCodeListManifestId()).collect(Collectors.toList())
                );
                bieEditCodeLists.addAll(0, basedCodeLists);
            }
        }

        BieEditBdtScPriRestri bdtScPriRestri = new BieEditBdtScPriRestri();

        bdtScPriRestri.setXbtList(bieEditXbtList);
        bdtScPriRestri.setCodeLists(bieEditCodeLists);
        bdtScPriRestri.setAgencyIdLists(bieEditAgencyIdLists);

        return bdtScPriRestri;
    }

    private List<BieEditCodeList> getAllCodeLists() {
        return dslContext.select(
                CODE_LIST.CODE_LIST_ID,
                CODE_LIST.NAME.as("code_list_name"),
                CODE_LIST.STATE).from(CODE_LIST)
                .where(CODE_LIST.STATE.eq("Published"))
                .fetchInto(BieEditCodeList.class);
    }

    private List<BieEditAgencyIdList> getAllAgencyIdLists() {
        return dslContext.select(
                AGENCY_ID_LIST.AGENCY_ID_LIST_ID,
                AGENCY_ID_LIST.NAME.as("agency_id_list_name"))
                .from(AGENCY_ID_LIST)
                .fetchInto(BieEditAgencyIdList.class);
    }

    private List<BieEditCodeList> getBieEditCodeListByBasedCodeListIds(List<String> basedCodeListIds) {
        if (basedCodeListIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<BieEditCodeList> bieEditCodeLists = dslContext.select(
                CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID,
                CODE_LIST_MANIFEST.BASED_CODE_LIST_MANIFEST_ID,
                CODE_LIST.NAME.as("code_list_name"))
                .from(CODE_LIST_MANIFEST)
                .join(CODE_LIST).on(CODE_LIST_MANIFEST.CODE_LIST_ID.eq(CODE_LIST.CODE_LIST_ID))
                .where(and(
                        CODE_LIST.CODE_LIST_ID.in(basedCodeListIds),
                        CODE_LIST.STATE.eq("Published")))
                .fetchInto(BieEditCodeList.class);

        List<BieEditCodeList> basedCodeLists =
                getBieEditCodeListByBasedCodeListIds(
                        bieEditCodeLists.stream().filter(e -> e.getBasedCodeListManifestId() != null)
                                .map(e -> e.getBasedCodeListManifestId()).collect(Collectors.toList())
                );

        bieEditCodeLists.addAll(0, basedCodeLists);
        return bieEditCodeLists;
    }

    private List<BieEditCodeList> getCodeListsByBasedCodeListManifestId(String basedCodeListManifestId) {
        if (basedCodeListManifestId == null) {
            return Collections.emptyList();
        }

        List<BieEditCodeList> bieEditCodeLists = new ArrayList();
        List<BieEditCodeList> bieEditCodeListsByBasedCodeListId = dslContext.select(
                CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID,
                CODE_LIST_MANIFEST.BASED_CODE_LIST_MANIFEST_ID,
                CODE_LIST.NAME.as("code_list_name"))
                .from(CODE_LIST_MANIFEST)
                .join(CODE_LIST).on(CODE_LIST_MANIFEST.CODE_LIST_ID.eq(CODE_LIST.CODE_LIST_ID))
                .where(and(
                        CODE_LIST_MANIFEST.BASED_CODE_LIST_MANIFEST_ID.eq(basedCodeListManifestId),
                        CODE_LIST.STATE.eq("Published")))
                .fetchInto(BieEditCodeList.class);

        bieEditCodeLists.addAll(bieEditCodeListsByBasedCodeListId);

        for (BieEditCodeList bieEditCodeList : bieEditCodeListsByBasedCodeListId) {
            bieEditCodeLists.addAll(getCodeListsByBasedCodeListManifestId(bieEditCodeList.getCodeListManifestId()));
        }

        return bieEditCodeLists;
    }

    @Override
    public void updateState(BieState state) {
        repository.updateState(topLevelAsbiep.getTopLevelAsbiepId(), state);
    }

    @Override
    public boolean updateDetail(BieEditNodeDetail detail) {
        if (detail instanceof BieEditAbieNodeDetail) {
            updateDetail((BieEditAbieNodeDetail) detail);
            return true;
        } else if (detail instanceof BieEditAsbiepNodeDetail) {
            updateDetail((BieEditAsbiepNodeDetail) detail);
            return true;
        } else if (detail instanceof BieEditBbiepNodeDetail) {
            updateDetail((BieEditBbiepNodeDetail) detail);
            return true;
        } else if (detail instanceof BieEditBbieScNodeDetail) {
            updateDetail((BieEditBbieScNodeDetail) detail);
            return true;
        } else {
            return false;
        }
    }

    private void updateDetail(BieEditAbieNodeDetail abieNodeDetail) {
        LocalDateTime timestamp = LocalDateTime.now();
        dslContext.update(ABIE)
                .set(ABIE.REMARK, emptyToNull(abieNodeDetail.getAsbiepRemark()))
                .set(ABIE.BIZ_TERM, emptyToNull(abieNodeDetail.getAsbiepBizTerm()))
                .set(ABIE.DEFINITION, emptyToNull(abieNodeDetail.getAsccpDefinition()))
                .set(ABIE.LAST_UPDATED_BY, sessionService.userId(user))
                .set(ABIE.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(ABIE.ABIE_ID.eq(abieNodeDetail.getAbieId()))
                .execute();
    }

    private void updateDetail(BieEditAsbiepNodeDetail asbiepNodeDetail) {
        if (asbiepNodeDetail.getBieCardinalityMin() != null) {
            dslContext.update(ASBIE)
                    .set(ASBIE.CARDINALITY_MIN, asbiepNodeDetail.getBieCardinalityMin())
                    .where(ASBIE.ASBIE_ID.eq(asbiepNodeDetail.getAsbieId()))
                    .execute();
        }
        if (asbiepNodeDetail.getBieCardinalityMax() != null) {
            dslContext.update(ASBIE)
                    .set(ASBIE.CARDINALITY_MAX, asbiepNodeDetail.getBieCardinalityMax())
                    .where(ASBIE.ASBIE_ID.eq(asbiepNodeDetail.getAsbieId()))
                    .execute();
        }
        Record1<Byte> rs = dslContext.select(ASCCP.IS_NILLABLE).from(ASCCP)
                .join(ASCCP_MANIFEST).on(ASCCP.ASCCP_ID.eq(ASCCP_MANIFEST.ASCCP_ID))
                .where(ASCCP_MANIFEST.ASCCP_MANIFEST_ID.eq(asbiepNodeDetail.getAsccpManifestId())).fetchOne();

        if (rs.getValue(ASCCP.IS_NILLABLE) != 1 && asbiepNodeDetail.getBieNillable() != null) {
            dslContext.update(ASBIE)
                    .set(ASBIE.IS_NILLABLE, (byte) (asbiepNodeDetail.getBieNillable() ? 1 : 0))
                    .where(ASBIE.ASBIE_ID.eq(asbiepNodeDetail.getAsbieId()))
                    .execute();
        }

        String userId = sessionService.userId(user);
        LocalDateTime timestamp = LocalDateTime.now();

        dslContext.update(ASBIE)
                .set(ASBIE.IS_USED, (byte) (asbiepNodeDetail.isUsed() ? 1 : 0))
                .set(ASBIE.DEFINITION, emptyToNull(asbiepNodeDetail.getAsbiepDefinition()))
                .set(ASBIE.LAST_UPDATED_BY, userId)
                .set(ASBIE.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(ASBIE.ASBIE_ID.eq(asbiepNodeDetail.getAsbieId()))
                .execute();

        dslContext.update(ASBIEP)
                .set(ASBIEP.BIZ_TERM, emptyToNull(asbiepNodeDetail.getAsbiepBizTerm()))
                .set(ASBIEP.REMARK, emptyToNull(asbiepNodeDetail.getAsbiepRemark()))
                .set(ASBIEP.LAST_UPDATED_BY, userId)
                .set(ASBIEP.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(ASBIEP.ASBIEP_ID.eq(asbiepNodeDetail.getAsbiepId()))
                .execute();
    }

    private void updateDetail(BieEditBbiepNodeDetail bbiepNodeDetail) {
        if (bbiepNodeDetail.getBieCardinalityMin() != null) {
            dslContext.update(BBIE)
                    .set(BBIE.CARDINALITY_MIN, bbiepNodeDetail.getBieCardinalityMin())
                    .where(BBIE.BBIE_ID.eq(bbiepNodeDetail.getBbieId())).execute();
        }
        if (bbiepNodeDetail.getBieCardinalityMax() != null) {
            dslContext.update(BBIE)
                    .set(BBIE.CARDINALITY_MAX, bbiepNodeDetail.getBieCardinalityMax())
                    .where(BBIE.BBIE_ID.eq(bbiepNodeDetail.getBbieId())).execute();
        }

        String bdtPriRestriId = bbiepNodeDetail.getBdtPriRestriId();
        String codeListId = bbiepNodeDetail.getCodeListId();
        String agencyIdListId = bbiepNodeDetail.getAgencyIdListId();

        if (agencyIdListId != null) {
            dslContext.update(BBIE)
                    .set(BBIE.AGENCY_ID_LIST_ID, agencyIdListId)
                    .setNull(BBIE.CODE_LIST_ID)
                    .setNull(BBIE.BDT_PRI_RESTRI_ID)
                    .where(BBIE.BBIE_ID.eq(bbiepNodeDetail.getBbieId())).execute();
        } else if (codeListId != null) {
            dslContext.update(BBIE)
                    .setNull(BBIE.AGENCY_ID_LIST_ID)
                    .set(BBIE.CODE_LIST_ID, codeListId)
                    .setNull(BBIE.BDT_PRI_RESTRI_ID)
                    .where(BBIE.BBIE_ID.eq(bbiepNodeDetail.getBbieId())).execute();
        } else if (bdtPriRestriId != null) {
            dslContext.update(BBIE)
                    .setNull(BBIE.AGENCY_ID_LIST_ID)
                    .setNull(BBIE.CODE_LIST_ID)
                    .set(BBIE.BDT_PRI_RESTRI_ID, bdtPriRestriId)
                    .where(BBIE.BBIE_ID.eq(bbiepNodeDetail.getBbieId())).execute();
        }

        BccRecord bccRecord = dslContext.select(BCC.fields()).from(BCC)
                .join(BCC_MANIFEST).on(BCC.BCC_ID.eq(BCC_MANIFEST.BCC_ID))
                .where(BCC_MANIFEST.BCC_MANIFEST_ID.eq(bbiepNodeDetail.getBccManifestId())).fetchOneInto(BccRecord.class);

        if (bccRecord.getIsNillable() != 1 && bbiepNodeDetail.getBieNillable() != null) {
            dslContext.update(BBIE)
                    .set(BBIE.IS_NILLABLE, (byte) (bbiepNodeDetail.getBieNillable() ? 1 : 0))
                    .where(BBIE.BBIE_ID.eq(bbiepNodeDetail.getBbieId())).execute();
        }

        Record2<String, String> bccp = dslContext.select(BCCP.DEFAULT_VALUE, BCCP.FIXED_VALUE)
                .from(BCCP)
                .join(BCCP_MANIFEST).on(BCCP.BCCP_ID.eq(BCCP_MANIFEST.BCCP_ID))
                .where(BCCP_MANIFEST.BCCP_MANIFEST_ID.eq(bbiepNodeDetail.getBccpManifestId())).fetchOne();

        if (bccRecord.getDefaultValue() == null &&
                bccRecord.getFixedValue() == null &&
                bccp.getValue(BCCP.DEFAULT_VALUE) == null &&
                bccp.getValue(BCCP.FIXED_VALUE) == null) {

            dslContext.update(BBIE)
                    .set(BBIE.FIXED_VALUE, emptyToNull(bbiepNodeDetail.getBieFixedValue()))
                    .set(BBIE.DEFAULT_VALUE, emptyToNull(bbiepNodeDetail.getBieDefaultValue()))
                    .where(BBIE.BBIE_ID.eq(bbiepNodeDetail.getBbieId())).execute();
        }

        dslContext.update(BBIE)
                .set(BBIE.IS_USED, (byte) (bbiepNodeDetail.isUsed() ? 1 : 0))
                .set(BBIE.DEFINITION, emptyToNull(bbiepNodeDetail.getContextDefinition()))
                .set(BBIE.EXAMPLE, emptyToNull(bbiepNodeDetail.getExample()))
                .where(BBIE.BBIE_ID.eq(bbiepNodeDetail.getBbieId())).execute();

        String userId = sessionService.userId(user);
        LocalDateTime timestamp = LocalDateTime.now();

        dslContext.update(BBIEP)
                .set(BBIEP.BIZ_TERM, emptyToNull(bbiepNodeDetail.getBizTerm()))
                .set(BBIEP.REMARK, emptyToNull(bbiepNodeDetail.getRemark()))
                .set(BBIEP.LAST_UPDATED_BY, userId)
                .set(BBIEP.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(BBIEP.BBIEP_ID.eq(bbiepNodeDetail.getBbiepId()))
                .execute();
    }

    private void updateDetail(BieEditBbieScNodeDetail bbieScNodeDetail) {
        if (bbieScNodeDetail.getBieCardinalityMin() != null) {
            dslContext.update(BBIE_SC)
                    .set(BBIE_SC.CARDINALITY_MIN, bbieScNodeDetail.getBieCardinalityMin())
                    .where(BBIE_SC.BBIE_SC_ID.eq(bbieScNodeDetail.getBbieScId())).execute();
        }

        if (bbieScNodeDetail.getBieCardinalityMax() != null) {
            dslContext.update(BBIE_SC)
                    .set(BBIE_SC.CARDINALITY_MAX, bbieScNodeDetail.getBieCardinalityMax())
                    .where(BBIE_SC.BBIE_SC_ID.eq(bbieScNodeDetail.getBbieScId())).execute();
        }

        String dtScPriRestriId = bbieScNodeDetail.getDtScPriRestriId();
        String codeListId = bbieScNodeDetail.getCodeListId();
        String agencyIdListId = bbieScNodeDetail.getAgencyIdListId();

        if (agencyIdListId != null) {
            dslContext.update(BBIE_SC)
                    .setNull(BBIE_SC.DT_SC_PRI_RESTRI_ID)
                    .setNull(BBIE_SC.CODE_LIST_ID)
                    .set(BBIE_SC.AGENCY_ID_LIST_ID, agencyIdListId)
                    .where(BBIE_SC.BBIE_SC_ID.eq(bbieScNodeDetail.getBbieScId())).execute();
        } else if (codeListId != null) {
            dslContext.update(BBIE_SC)
                    .setNull(BBIE_SC.DT_SC_PRI_RESTRI_ID)
                    .set(BBIE_SC.CODE_LIST_ID, codeListId)
                    .setNull(BBIE_SC.AGENCY_ID_LIST_ID)
                    .where(BBIE_SC.BBIE_SC_ID.eq(bbieScNodeDetail.getBbieScId())).execute();
        } else if (dtScPriRestriId != null) {
            dslContext.update(BBIE_SC)
                    .set(BBIE_SC.DT_SC_PRI_RESTRI_ID, dtScPriRestriId)
                    .setNull(BBIE_SC.CODE_LIST_ID)
                    .setNull(BBIE_SC.AGENCY_ID_LIST_ID)
                    .where(BBIE_SC.BBIE_SC_ID.eq(bbieScNodeDetail.getBbieScId())).execute();
        }

        DtScRecord dtScRecord = dslContext.select(DT_SC.fields())
                .from(DT_SC)
                .join(DT_SC_MANIFEST).on(DT_SC.DT_SC_ID.eq(DT_SC_MANIFEST.DT_SC_ID))
                .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(bbieScNodeDetail.getDtScManifestId()))
                .fetchOneInto(DtScRecord.class);

        if (dtScRecord.getDefaultValue() == null && dtScRecord.getFixedValue() == null) {
            dslContext.update(BBIE_SC)
                    .set(BBIE_SC.DEFAULT_VALUE, emptyToNull(bbieScNodeDetail.getBieDefaultValue()))
                    .set(BBIE_SC.FIXED_VALUE, emptyToNull(bbieScNodeDetail.getBieFixedValue()))
                    .where(BBIE_SC.BBIE_SC_ID.eq(bbieScNodeDetail.getBbieScId()))
                    .execute();
        }

        dslContext.update(BBIE_SC)
                .set(BBIE_SC.IS_USED, (byte) (bbieScNodeDetail.isUsed() ? 1 : 0))
                .set(BBIE_SC.DEFINITION, emptyToNull(bbieScNodeDetail.getContextDefinition()))
                .set(BBIE_SC.BIZ_TERM, emptyToNull(bbieScNodeDetail.getBizTerm()))
                .set(BBIE_SC.REMARK, emptyToNull(bbieScNodeDetail.getRemark()))
                .set(BBIE_SC.EXAMPLE, emptyToNull(bbieScNodeDetail.getExample()))
                .where(BBIE_SC.BBIE_SC_ID.eq(bbieScNodeDetail.getBbieScId()))
                .execute();
    }

    private String emptyToNull(String str) {
        if (str != null) {
            str = str.trim();
        }
        if (!StringUtils.hasLength(str)) {
            return null;
        }
        return str;
    }
}
