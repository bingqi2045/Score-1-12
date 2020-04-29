package org.oagi.srt.gateway.http.api.bie_management.service.edit_tree;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record4;
import org.jooq.types.ULong;
import org.oagi.srt.data.BieState;
import org.oagi.srt.data.OagisComponentType;
import org.oagi.srt.data.SeqKeySupportable;
import org.oagi.srt.data.TopLevelAbie;
import org.oagi.srt.entity.jooq.tables.records.*;
import org.oagi.srt.gateway.http.api.DataAccessForbiddenException;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.*;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree.*;
import org.oagi.srt.gateway.http.api.bie_management.service.BieRepository;
import org.oagi.srt.gateway.http.api.cc_management.repository.CcNodeRepository;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.repo.BusinessInformationEntityRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.userdetails.User;
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
import static org.oagi.srt.entity.jooq.Tables.*;
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
    private RedissonClient redissonClient;

    private boolean initialized;
    private User user;
    private TopLevelAbie topLevelAbie;
    private BieState state;
    private boolean forceBieUpdate;

    public void initialize(User user, TopLevelAbie topLevelAbie) {
        this.user = user;
        this.topLevelAbie = topLevelAbie;

        this.state = BieState.valueOf(topLevelAbie.getState());
        this.forceBieUpdate = true;
        switch (this.state) {
            case WIP:
                if (sessionService.userId(user) != topLevelAbie.getOwnerUserId()) {
                    throw new DataAccessForbiddenException("'" + user.getUsername() +
                            "' doesn't have an access privilege.");
                }
                break;
        }

        this.initialized = true;
    }

    private boolean isForceBieUpdate() {
        return forceBieUpdate;
    }

    public BieEditAbieNode getRootNode(long topLevelAbieId) {
        BieEditAbieNode rootNode = dslContext.select(
                TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID,
                TOP_LEVEL_ABIE.RELEASE_ID,
                TOP_LEVEL_ABIE.STATE.as("top_level_abie_state"),
                TOP_LEVEL_ABIE.OWNER_USER_ID,
                ASCCP.GUID,
                ASCCP.PROPERTY_TERM.as("name"),
                ASBIEP.ASBIEP_ID,
                ASBIEP.BASED_ASCCP_MANIFEST_ID.as("asccp_manifest_id"),
                ABIE.ABIE_ID,
                ABIE.BASED_ACC_MANIFEST_ID.as("acc_manifest_id"),
                inline("abie").as("type"),
                inline(true).as("used"))
                .from(TOP_LEVEL_ABIE)
                .join(ABIE).on(ABIE.ABIE_ID.eq(TOP_LEVEL_ABIE.ABIE_ID))
                .join(ASBIEP).on(ASBIEP.ROLE_OF_ABIE_ID.eq(ABIE.ABIE_ID))
                .join(ASCCP_MANIFEST).on(ASBIEP.BASED_ASCCP_MANIFEST_ID.eq(ASCCP_MANIFEST.ASCCP_MANIFEST_ID))
                .join(ASCCP).on(ASCCP_MANIFEST.ASCCP_ID.eq(ASCCP.ASCCP_ID))
                .where(TOP_LEVEL_ABIE.TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(topLevelAbieId)))
                .fetchOneInto(BieEditAbieNode.class);
        rootNode.setHasChild(hasChild(rootNode));

        return rootNode;
    }

    // have to check @hakju
    private boolean hasChild(BieEditAbieNode abieNode) {
        long fromAccId;
        long topLevelAbieId = abieNode.getTopLevelAbieId();
        long releaseId = abieNode.getReleaseId();
        BieEditAcc acc = null;
        if (topLevelAbieId > 0L) {
            fromAccId = repository.getAccIdByTopLevelAbieId(topLevelAbieId, releaseId);
        } else {
            acc = repository.getAcc(abieNode.getAccId(), releaseId);
            fromAccId = acc.getAccId();
        }

        if (repository.getAsccListByFromAccId(fromAccId, releaseId).size() > 0) {
            return true;
        }
        if (repository.getBccListByFromAccId(fromAccId, releaseId).size() > 0) {
            return true;
        }

        if (acc == null) {
            acc = repository.getAccByAccId(fromAccId, releaseId);
        }
        if (acc != null && acc.getBasedAccId() != null) {
            BieEditAbieNode basedAbieNode = new BieEditAbieNode();
            basedAbieNode.setReleaseId(releaseId);

            acc = repository.getAccByAccId(acc.getBasedAccId(), releaseId);
            basedAbieNode.setAccId(acc.getAccId());
            return hasChild(basedAbieNode);
        }

        return false;
    }

    @Override
    public List<BieEditNode> getDescendants(BieEditNode node, boolean hideUnused) {
        /*
         * If this profile BIE is in Editing state, descendants of given node will create during this process,
         * and this must be thread-safe.
         */
        RLock lock = null;
        String lockName = getClass().getSimpleName() + ".getDescendants(" +
                node.getType() + ", " + topLevelAbie.getTopLevelAbieId() + ")";
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
        Map<Long, BieEditAsbie> asbieMap;
        Map<Long, BieEditBbie> bbieMap;

        long accId;
        long asbiepId = abieNode.getAsbiepId();
        long abieId = repository.getAbieByAsbiepId(asbiepId).getAbieId();
        asbieMap = repository.getAsbieListByFromAbieId(abieId, abieNode).stream()
                .collect(toMap(BieEditAsbie::getBasedAsccId, Function.identity()));
        bbieMap = repository.getBbieListByFromAbieId(abieId, abieNode).stream()
                .collect(toMap(BieEditBbie::getBasedBccId, Function.identity()));

        accId = repository.getRoleOfAccIdByAsbiepId(asbiepId);

        List<BieEditNode> children = getChildren(asbieMap, bbieMap, abieId, accId, abieNode, hideUnused);
        return children;
    }

    private List<BieEditNode> getDescendants(BieEditAsbiepNode asbiepNode, boolean hideUnused) {
        Map<Long, BieEditAsbie> asbieMap;
        Map<Long, BieEditBbie> bbieMap;

        long accId = asbiepNode.getAccId();
        long abieId = asbiepNode.getAbieId();

        if (abieId == 0L && isForceBieUpdate()) {
            BieEditAcc acc = repository.getAcc(asbiepNode.getAccId(), asbiepNode.getReleaseId());
            AccManifestRecord accManifest = ccNodeRepository.getAccManifestByAcc(asbiepNode.getAccId(), asbiepNode.getReleaseId());

            abieId = bieRepository.insertAbie()
                    .setUserId(sessionService.userId(user))
                    .setTopLevelAbieId(asbiepNode.getTopLevelAbieId())
                    .setAccManifestId(accManifest.getAccManifestId())
                    .execute().longValue();
        }

        if (abieId > 0L) {
            asbieMap = repository.getAsbieListByFromAbieId(abieId, asbiepNode).stream()
                    .collect(toMap(BieEditAsbie::getBasedAsccId, Function.identity()));
            bbieMap = repository.getBbieListByFromAbieId(abieId, asbiepNode).stream()
                    .collect(toMap(BieEditBbie::getBasedBccId, Function.identity()));
        } else {
            asbieMap = Collections.emptyMap();
            bbieMap = Collections.emptyMap();
        }

        List<BieEditNode> children = getChildren(asbieMap, bbieMap, abieId, accId, asbiepNode, hideUnused);
        return children;
    }

    private List<BieEditNode> getChildren(
            Map<Long, BieEditAsbie> asbieMap,
            Map<Long, BieEditBbie> bbieMap,
            long fromAbieId, long accId,
            BieEditNode node, boolean hideUnused) {
        List<BieEditNode> children = new ArrayList();

        List<SeqKeySupportable> assocList = getAssociationsByAccId(accId, node.getReleaseId());
        int seqKey = 1;
        for (SeqKeySupportable assoc : assocList) {
            if (assoc instanceof BieEditAscc) {
                BieEditAscc ascc = (BieEditAscc) assoc;
                BieEditAsbie asbie = asbieMap.get(ascc.getAsccId());
                BieEditAsbiepNode asbiepNode = createAsbiepNode(fromAbieId, seqKey++, asbie, ascc);
                if (asbiepNode == null) {
                    seqKey--;
                    continue;
                }

                OagisComponentType oagisComponentType = ccNodeRepository.getOagisComponentTypeByAccId(asbiepNode.getAccId());
                if (oagisComponentType.isGroup()) {
                    children.addAll(getDescendants(asbiepNode, hideUnused));
                } else {
                    if (hideUnused && (asbie == null || asbie.getAsbieId() == 0L || !asbie.isUsed())) {
                        seqKey--;
                        continue;
                    }

                    children.add(asbiepNode);
                }
            } else {
                BieEditBcc bcc = (BieEditBcc) assoc;
                BieEditBbie bbie = bbieMap.get(bcc.getBccId());
                if (hideUnused && (bbie == null || bbie.getBbieId() == 0L || !bbie.isUsed())) {
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

    private List<SeqKeySupportable> getAssociationsByAccId(long accId, long releaseId) {
        Stack<BieEditAcc> accStack = getAccStack(accId, releaseId);

        List<BieEditBcc> attributeBccList = new ArrayList();
        List<SeqKeySupportable> assocList = new ArrayList();

        while (!accStack.isEmpty()) {
            BieEditAcc acc = accStack.pop();

            long fromAccId = acc.getAccId();
            List<BieEditAscc> asccList = repository.getAsccListByFromAccId(fromAccId, releaseId, true);
            List<BieEditBcc> bccList = repository.getBccListByFromAccId(fromAccId, releaseId, true);

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
                            repository.getOagisComponentTypeOfAccByAsccpId(((BieEditAscc) assoc).getToAsccpManifestId(), releaseId);
                    if (roleOfAccType.isGroup()) {
                        long roleOfAccId = repository.getRoleOfAccIdByAsccpId(((BieEditAscc) assoc).getToAsccpManifestId());

                        assocList.addAll(
                                getAssociationsByAccId(roleOfAccId, releaseId)
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

    private Stack<BieEditAcc> getAccStack(long accId, long releaseId) {
        Stack<BieEditAcc> accStack = new Stack();
        BieEditAcc acc = repository.getAccByAccId(accId, releaseId);
        /*
         * Issue #708
         * If the UEG's state is not 'Published', its children couldn't get it by the logic above.
         */
        if (acc == null) {
            return accStack;
        }
        accStack.push(acc);

        while (acc.getBasedAccId() != null) {
            acc = repository.getAccByAccId(acc.getBasedAccId(), releaseId);
            accStack.push(acc);
        }

        return accStack;
    }

    private BieEditAsbiepNode createAsbiepNode(long fromAbieId, int seqKey,
                                               BieEditAsbie asbie, BieEditAscc ascc) {
        BieEditAsbiepNode asbiepNode = new BieEditAsbiepNode();

        long topLevelAbieId = topLevelAbie.getTopLevelAbieId();
        long releaseId = topLevelAbie.getReleaseId();

        asbiepNode.setTopLevelAbieId(topLevelAbieId);
        asbiepNode.setReleaseId(releaseId);
        asbiepNode.setType("asbiep");
        asbiepNode.setGuid(ascc.getGuid());
        asbiepNode.setAsccId(ascc.getAsccId());

        BieEditAsccp asccp = repository.getAsccpByAsccpManifestId(ascc.getToAsccpManifestId());

        asbiepNode.setAsccpId(asccp.getAsccpId());

        if (StringUtils.isEmpty(asbiepNode.getName())) {
            asbiepNode.setName(asccp.getPropertyTerm());
        }

        BieEditAcc acc;
        if (asbiepNode.getAccId() == 0L) {
            acc = repository.getAccByAccManifestId(asccp.getRoleOfAccManifestId());
            if (acc == null) {
                return null;
            }
            asbiepNode.setAccId(acc.getAccId());
        } else {
            acc = repository.getAcc(asbiepNode.getAccId(), releaseId);
        }

        if (asbie == null && isForceBieUpdate()) {
            long abieId = bieRepository.insertAbie()
                    .setUserId(sessionService.userId(user))
                    .setTopLevelAbieId(asbiepNode.getTopLevelAbieId())
                    .setAccManifestId(acc.getAccId())
                    .execute().longValue();

            AsbiepRecord asbiepRecord = repository.createAsbiep(user, asccp.getAsccpId(), abieId, topLevelAbieId);
            long asbiepId = asbiepRecord.getAsbiepId().longValue();
            AsbieRecord asbieRecord = repository.createAsbie(user, fromAbieId, asbiepId, ascc.getAsccManifestId(),
                    seqKey, topLevelAbieId);

            asbie = new BieEditAsbie();
            asbie.setAsbieId(asbieRecord.getAsbieId().longValue());
            asbie.setBasedAsccId(ascc.getAsccId());
            asbie.setFromAbieId(fromAbieId);
            asbie.setToAsbiepId(asbiepId);
            asbie.setUsed((asbieRecord.getIsUsed() == 1) ? true : false);
            asbie.setCardinalityMin(asbieRecord.getCardinalityMin());
            asbie.setCardinalityMax(asbieRecord.getCardinalityMax());
        }

        if (asbie != null) {
            asbiepNode.setAsbieId(asbie.getAsbieId());
            asbiepNode.setAsbiepId(asbie.getToAsbiepId());

            BieEditAbie abie = repository.getAbieByAsbiepId(asbie.getToAsbiepId());
            asbiepNode.setAbieId(abie.getAbieId());
            asbiepNode.setAccId(abie.getBasedAccId());

            asbiepNode.setName(repository.getAsccpPropertyTermByAsbiepId(asbie.getToAsbiepId()));
            asbiepNode.setUsed(asbie.isUsed());
            asbiepNode.setRequired(ascc.getCardinalityMin() > 0);
        }

        asbiepNode.setHasChild(hasChild(asbiepNode));

        return asbiepNode;
    }

    private BieEditBbiepNode createBbiepNode(long fromAbieId, int seqKey,
                                             BieEditBbie bbie, BieEditBcc bcc,
                                             boolean hideUnused) {
        BieEditBbiepNode bbiepNode = new BieEditBbiepNode();

        long topLevelAbieId = topLevelAbie.getTopLevelAbieId();
        long releaseId = topLevelAbie.getReleaseId();

        bbiepNode.setTopLevelAbieId(topLevelAbieId);
        bbiepNode.setReleaseId(releaseId);
        bbiepNode.setType("bbiep");
        bbiepNode.setGuid(bcc.getGuid());
        bbiepNode.setAttribute(bcc.isAttribute());

        bbiepNode.setBccId(bcc.getBccId());
        BieEditBccp bccp = repository.getBccpByBccpManifestId(bcc.getToBccpManifestId());
        bbiepNode.setBccpId(bccp.getBccpId());
        bbiepNode.setBdtId(bccp.getBdtManifestId());

        if (StringUtils.isEmpty(bbiepNode.getName())) {
            bbiepNode.setName(bccp.getPropertyTerm());
        }

        if (bbie == null && isForceBieUpdate()) {
            BbiepRecord bbiepRecord = repository.createBbiep(user, bcc.getToBccpManifestId(), topLevelAbieId);
            long bbiepId = bbiepRecord.getBbiepId().longValue();
            BbieRecord bbieRecord = repository.createBbie(user, fromAbieId, bbiepId,
                    bcc.getBccManifestId(), bccp.getBdtManifestId(), seqKey, topLevelAbieId);
            long bbieId = bbieRecord.getBbieId().longValue();

            bbie = new BieEditBbie();
            bbie.setBasedBccId(bcc.getBccId());
            bbie.setBbieId(bbieId);
            bbie.setFromAbieId(fromAbieId);
            bbie.setToBbiepId(bbiepId);
            bbie.setUsed((bbieRecord.getIsUsed() == 1) ? true : false);
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
        abieNode.setAccId(asbiepNode.getAccId());

        return hasChild(abieNode);
    }

    public boolean hasChild(BieEditBbiepNode bbiepNode, boolean hideUnused) {
        if (hideUnused) {
            return repository.getCountBbieScByBbieIdAndIsUsedAndOwnerTopLevelAbieId(
                    bbiepNode.getBbieId(), true, bbiepNode.getTopLevelAbieId()) > 0;

        } else {
            BieEditBccp bccp = repository.getBccp(bbiepNode.getBccpId(), bbiepNode.getReleaseId());
            return repository.getCountDtScByOwnerDtId(bccp.getBdtManifestId()) > 0;
        }
    }

    private List<BieEditNode> getDescendants(BieEditBbiepNode bbiepNode, boolean hideUnused) {
        long bbiepId = bbiepNode.getBbiepId();
        long topLevelAbieId = bbiepNode.getTopLevelAbieId();
        BieEditBccp bccp;
        if (bbiepId > 0L) {
            BieEditBbiep bbiep = repository.getBbiep(bbiepId, topLevelAbieId);
            bccp = repository.getBccp(bbiep.getBasedBccpId(), bbiepNode.getReleaseId());
        } else {
            if (hideUnused) {
                return Collections.emptyList();
            }

            bccp = repository.getBccp(bbiepNode.getBccpId(), bbiepNode.getReleaseId());
        }

        List<BieEditNode> children = new ArrayList();
        List<BieEditBdtSc> bdtScList = repository.getBdtScListByOwnerDtId(bccp.getBdtManifestId());
        long bbieId = bbiepNode.getBbieId();
        for (BieEditBdtSc bdtSc : bdtScList) {
            BieEditBbieScNode bbieScNode = new BieEditBbieScNode();

            bbieScNode.setTopLevelAbieId(topLevelAbieId);
            bbieScNode.setReleaseId(bbiepNode.getReleaseId());
            bbieScNode.setType("bbie_sc");
            bbieScNode.setGuid(bdtSc.getGuid());
            bbieScNode.setName(bdtSc.getName());

            long dtScManifestId = bdtSc.getDtScManifestId();
            if (bbieId > 0L) {
                BieEditBbieSc bbieSc = repository.getBbieScIdByBbieIdAndDtScId(bbieId, dtScManifestId, topLevelAbieId);
                if (bbieSc == null) {
                    if (isForceBieUpdate()) {
                        long bbieScId = repository.createBbieSc(user, bbieId, dtScManifestId, topLevelAbieId);
                        bbieSc = new BieEditBbieSc();
                        bbieSc.setBbieScId(bbieScId);
                    }
                }

                if (hideUnused && (bbieSc == null || bbieSc.getBbieScId() == 0L || !bbieSc.isUsed())) {
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
                ABIE.VERSION,
                ABIE.STATUS,
                ABIE.REMARK,
                ABIE.BIZ_TERM,
                ABIE.DEFINITION)
                .from(ABIE)
                .where(ABIE.ABIE_ID.eq(ULong.valueOf(abieNode.getAbieId())))
                .fetchOneInto(BieEditAbieNodeDetail.class);

        return detail.append(abieNode);
    }

    private BieEditAsbiepNodeDetail getDetail(BieEditAsbiepNode asbiepNode) {

        BieEditAsbiepNodeDetail detail;
        if (asbiepNode.getAsbieId() > 0L) {
            detail = dslContext.select(
                    ASBIE.CARDINALITY_MIN.as("bie_cardinality_min"),
                    ASBIE.CARDINALITY_MAX.as("bie_cardinality_max"),
                    ASBIE.IS_USED.as("used"),
                    ASBIE.IS_NILLABLE.as("bie_nillable"),
                    ASBIE.DEFINITION.as("context_definition")
            ).from(ASBIE)
                    .where(ASBIE.ASBIE_ID.eq(ULong.valueOf(asbiepNode.getAsbieId())))
                    .fetchOneInto(BieEditAsbiepNodeDetail.class);
        } else {
            detail = dslContext.select(
                    ASCC.CARDINALITY_MIN.as("bie_cardinality_min"),
                    ASCC.CARDINALITY_MAX.as("bie_cardinality_max"))
                    .from(ASCC)
                    .where(ASCC.ASCC_ID.eq(ULong.valueOf(asbiepNode.getAsccId())))
                    .fetchOneInto(BieEditAsbiepNodeDetail.class);
        }

        if (asbiepNode.getAsbiepId() > 0L) {
            detail.setBizTerm(dslContext.select(
                    ASBIEP.BIZ_TERM).from(ASBIEP)
                    .where(ASBIEP.ASBIEP_ID.eq(ULong.valueOf(asbiepNode.getAsbiepId())))
                    .fetchOneInto(String.class));

            detail.setRemark(dslContext.select(
                    ASBIEP.REMARK).from(ASBIEP)
                    .where(ASBIEP.ASBIEP_ID.eq(ULong.valueOf(asbiepNode.getAsbiepId())))
                    .fetchOneInto(String.class));
        }

        if (asbiepNode.getAsccId() > 0L) {
            Record2<Integer, Integer> res = dslContext.select(
                    ASCC.CARDINALITY_MIN,
                    ASCC.CARDINALITY_MAX)
                    .from(ASCC)
                    .where(ASCC.ASCC_ID.eq(ULong.valueOf(asbiepNode.getAsccId())))
                    .fetchOne();

            detail.setCcCardinalityMin(res.get(ASCC.CARDINALITY_MIN));
            detail.setCcCardinalityMax(res.get(ASCC.CARDINALITY_MAX));
            detail.setRequired(detail.getCcCardinalityMin() > 0);
        }

        if (asbiepNode.getAsccpId() > 0L) {
            Record1<Byte> ccNillable = dslContext.select(
                    ASCCP.IS_NILLABLE)
                    .from(ASCCP)
                    .where(ASCCP.ASCCP_ID.eq(ULong.valueOf(asbiepNode.getAsccpId())))
                    .fetchOne();

            detail.setCcNillable(ccNillable.get(ASCCP.IS_NILLABLE) == 1);
        }

        detail.setAssociationDefinition(dslContext.select(
                ASCC.DEFINITION).from(ASCC)
                .where(ASCC.ASCC_ID.eq(ULong.valueOf(asbiepNode.getAsccId())))
                .fetchOneInto(String.class));

        detail.setComponentDefinition(dslContext.select(
                ASCCP.DEFINITION).from(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(ULong.valueOf(asbiepNode.getAsccpId())))
                .fetchOneInto(String.class));

        detail.setTypeDefinition(dslContext.select(
                ACC.DEFINITION).from(ACC)
                .where(ACC.ACC_ID.eq(ULong.valueOf(asbiepNode.getAccId())))
                .fetchOneInto(String.class));

        return detail.append(asbiepNode);
    }

    private BieEditBbiepNodeDetail getDetail(BieEditBbiepNode bbiepNode) {
        BieEditBbiepNodeDetail detail;

        if (bbiepNode.getBbieId() > 0L) {
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
                    .where(BBIE.BBIE_ID.eq(ULong.valueOf(bbiepNode.getBbieId())))
                    .fetchOneInto(BieEditBbiepNodeDetail.class);

            /* Issue #762 */
            if (detail.getAgencyIdListId() != null && detail.getAgencyIdListId() > 0L) {
                detail.setBdtPriRestriId(null);
                detail.setCodeListId(null);
            } else if (detail.getCodeListId() != null && detail.getCodeListId() > 0L) {
                detail.setBdtPriRestriId(null);
                detail.setAgencyIdListId(null);
            } else if (detail.getBdtPriRestriId() != null && detail.getBdtPriRestriId() > 0L) {
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
                    .where(BCC.BCC_ID.eq(ULong.valueOf(bbiepNode.getBccId())))
                    .fetchOneInto(BieEditBbiepNodeDetail.class);
        }

        if (bbiepNode.getBbiepId() > 0L) {
            Record4<String, String, ULong, String> rs =
                    dslContext.select(BBIEP.BIZ_TERM, BBIEP.REMARK, BCCP.BDT_ID, DT.DEN)
                            .from(BBIEP)
                            .join(BCCP_MANIFEST).on(BBIEP.BASED_BCCP_MANIFEST_ID.eq(BCCP_MANIFEST.BCCP_MANIFEST_ID))
                            .join(BCCP).on(BCCP_MANIFEST.BCCP_ID.eq(BCCP.BCCP_ID))
                            .join(DT).on(BCCP.BDT_ID.eq(DT.DT_ID))
                            .where(BBIEP.BBIEP_ID.eq(ULong.valueOf(bbiepNode.getBbiepId())))
                            .fetchOne();

            detail.setBizTerm(rs.getValue(BBIEP.BIZ_TERM));
            detail.setRemark(rs.getValue(BBIEP.REMARK));
            detail.setBdtId(rs.getValue(BCCP.BDT_ID).longValue());
            detail.setBdtDen(rs.getValue(DT.DEN).replaceAll("_ ", " "));
        } else {
            Record2<String, ULong> rs = dslContext.select(
                    DT.DEN,
                    BCCP.BDT_ID).from(BCCP)
                    .join(DT).on(BCCP.BDT_ID.eq(DT.DT_ID))
                    .where(BCCP.BCCP_ID.eq(ULong.valueOf(bbiepNode.getBbiepId()))).fetchOne();
            detail.setBdtDen(rs.getValue(DT.DEN));
            detail.setBdtId(rs.getValue(BCCP.BDT_ID).longValue());
        }

        if (bbiepNode.getBbieId() == 0L) {
            long defaultBdtPriRestriId = repository.getDefaultBdtPriRestriIdByBdtId(detail.getBdtId());
            detail.setBdtPriRestriId(defaultBdtPriRestriId);
        }

        if (bbiepNode.getBccId() > 0L) {
            BccRecord bccRecord = dslContext.selectFrom(BCC)
                    .where(BCC.BCC_ID.eq(ULong.valueOf(bbiepNode.getBccId()))).fetchOne();
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
                .where(BCC.BCC_ID.eq(ULong.valueOf(bbiepNode.getBccId())))
                .fetchOneInto(String.class)
        );

        detail.setComponentDefinition(dslContext.select(
                BCCP.DEFINITION).from(BCCP)
                .where(BCCP.BCCP_ID.eq(ULong.valueOf(bbiepNode.getBccpId())))
                .fetchOneInto(String.class)
        );

        return detail.append(bbiepNode);
    }

    private BieEditBdtPriRestri getBdtPriRestri(BieEditBbiepNode bbiepNode) {
        long bdtId = bbiepNode.getBdtId();

        List<BieEditXbt> bieEditXbtList = dslContext.select(
                BDT_PRI_RESTRI.BDT_PRI_RESTRI_ID.as("pri_restri_id"),
                BDT_PRI_RESTRI.IS_DEFAULT, XBT.XBT_ID, XBT.NAME.as("xbt_name"))
                .from(BDT_PRI_RESTRI)
                .join(CDT_AWD_PRI_XPS_TYPE_MAP).on(BDT_PRI_RESTRI.CDT_AWD_PRI_XPS_TYPE_MAP_ID.eq(CDT_AWD_PRI_XPS_TYPE_MAP.CDT_AWD_PRI_XPS_TYPE_MAP_ID))
                .join(XBT).on(CDT_AWD_PRI_XPS_TYPE_MAP.XBT_ID.eq(XBT.XBT_ID))
                .where(BDT_PRI_RESTRI.BDT_ID.eq(ULong.valueOf(bdtId)))
                .fetchInto(BieEditXbt.class);

        List<BieEditCodeList> bieEditCodeLists = dslContext.select(
                CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID, CODE_LIST_MANIFEST.BASED_CODE_LIST_MANIFEST_ID,
                BDT_PRI_RESTRI.IS_DEFAULT, CODE_LIST.NAME.as("code_list_name"))
                .from(BDT_PRI_RESTRI)
                .join(CODE_LIST_MANIFEST).on(BDT_PRI_RESTRI.CODE_LIST_ID.eq(CODE_LIST_MANIFEST.CODE_LIST_ID))
                .join(CODE_LIST).on(CODE_LIST_MANIFEST.CODE_LIST_ID.eq(CODE_LIST.CODE_LIST_ID))
                .where(BDT_PRI_RESTRI.BDT_ID.eq(ULong.valueOf(bdtId)))
                .fetchInto(BieEditCodeList.class);

        List<BieEditAgencyIdList> bieEditAgencyIdLists = dslContext.select(
                AGENCY_ID_LIST.AGENCY_ID_LIST_ID, BDT_PRI_RESTRI.IS_DEFAULT, AGENCY_ID_LIST.NAME.as("agency_id_list_name"))
                .from(BDT_PRI_RESTRI)
                .join(AGENCY_ID_LIST).on(BDT_PRI_RESTRI.AGENCY_ID_LIST_ID.eq(AGENCY_ID_LIST.AGENCY_ID_LIST_ID))
                .where(BDT_PRI_RESTRI.BDT_ID.eq(ULong.valueOf(bdtId)))
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
                List<BieEditCodeList> basedCodeLists2 = getCodeListsByBasedCodeList(bieEditCodeLists.get(0).getCodeListManifestId());
                basedCodeLists2.clear();
                for (int i = 0; i < bieEditCodeLists.size(); i++) {
                    basedCodeLists2.addAll(getCodeListsByBasedCodeList(bieEditCodeLists.get(i).getCodeListManifestId()));
                }
                bieEditCodeLists.addAll(0, basedCodeLists);
                bieEditCodeLists.addAll(0, basedCodeLists2);
                basedCodeLists2.clear();
                for (int i = 0; i < bieEditCodeLists.size(); i++) {
                    basedCodeLists2.addAll(getCodeListsByBasedCodeList(bieEditCodeLists.get(i).getCodeListManifestId()));
                }
                bieEditCodeLists.addAll(basedCodeLists2);
                Set<BieEditCodeList> set = new HashSet<BieEditCodeList>(bieEditCodeLists);
                bieEditCodeLists.clear();
                bieEditCodeLists.addAll(set); // remove dupplicate elements
            }
        }

        BieEditBdtPriRestri bdtPriRestri = new BieEditBdtPriRestri();

        bieEditXbtList.sort(Comparator.comparingLong(BieEditXbt::getPriRestriId));
        bdtPriRestri.setXbtList(bieEditXbtList);

        bieEditCodeLists.sort(Comparator.comparingLong(BieEditCodeList::getCodeListManifestId));
        bdtPriRestri.setCodeLists(bieEditCodeLists);

        bieEditAgencyIdLists.sort(Comparator.comparingLong(BieEditAgencyIdList::getAgencyIdListId));
        bdtPriRestri.setAgencyIdLists(bieEditAgencyIdLists);

        return bdtPriRestri;
    }

    private BieEditBbieScNodeDetail getDetail(BieEditBbieScNode bbieScNode) {
        BieEditBbieScNodeDetail detail;
        if (bbieScNode.getBbieScId() > 0L) {
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
                    .where(BBIE_SC.BBIE_SC_ID.eq(ULong.valueOf(bbieScNode.getBbieScId())))
                    .fetchOneInto(BieEditBbieScNodeDetail.class);

            /* Issue #762 */
            if (detail.getAgencyIdListId() != null && detail.getAgencyIdListId() > 0L) {
                detail.setDtScPriRestriId(null);
                detail.setCodeListId(null);
            } else if (detail.getCodeListId() != null && detail.getCodeListId() > 0L) {
                detail.setDtScPriRestriId(null);
                detail.setAgencyIdListId(null);
            } else if (detail.getDtScPriRestriId() != null && detail.getDtScPriRestriId() > 0L) {
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
                    .where(DT_SC.DT_SC_ID.eq(ULong.valueOf(bbieScNode.getDtScManifestId())))
                    .fetchOneInto(BieEditBbieScNodeDetail.class);
        }

        if (bbieScNode.getBbieScId() == 0L) {
            long defaultDtScPriRestriId = repository.getDefaultDtScPriRestriIdByDtScId(bbieScNode.getDtScManifestId());
            detail.setDtScPriRestriId(defaultDtScPriRestriId);
        }

        if (bbieScNode.getDtScManifestId() > 0L) {
            DtScRecord dtScRecord = dslContext.select(DT_SC.fields())
                    .from(DT_SC)
                    .join(DT_SC_MANIFEST).on(DT_SC.DT_SC_ID.eq(DT_SC_MANIFEST.DT_SC_ID))
                    .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(ULong.valueOf(bbieScNode.getDtScManifestId())))
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
                        .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(ULong.valueOf(bbieScNode.getDtScManifestId())))
                        .fetchOneInto(String.class)
        );
        return detail.append(bbieScNode);
    }

    private BieEditBdtScPriRestri getBdtScPriRestri(BieEditBbieScNode bbieScNode) {
        long dtScManifestId = bbieScNode.getDtScManifestId();

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
                .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(ULong.valueOf(dtScManifestId)))
                .fetchInto(BieEditXbt.class);

        List<BieEditCodeList> bieEditCodeLists = dslContext.select(
                CODE_LIST_MANIFEST.CODE_LIST_MANIFEST_ID,
                CODE_LIST_MANIFEST.BASED_CODE_LIST_MANIFEST_ID,
                BDT_SC_PRI_RESTRI.IS_DEFAULT,
                CODE_LIST.NAME.as("code_list_name"))
                .from(BDT_SC_PRI_RESTRI)
                .join(DT_SC_MANIFEST).on(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(DT_SC_MANIFEST.DT_SC_ID))
                .join(CODE_LIST).on(BDT_SC_PRI_RESTRI.CODE_LIST_ID.eq(CODE_LIST.CODE_LIST_ID))
                .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(ULong.valueOf(dtScManifestId)))
                .fetchInto(BieEditCodeList.class);

        List<BieEditAgencyIdList> bieEditAgencyIdLists = dslContext.select(
                AGENCY_ID_LIST.AGENCY_ID_LIST_ID,
                BDT_SC_PRI_RESTRI.IS_DEFAULT,
                AGENCY_ID_LIST.NAME.as("agency_id_list_name"))
                .from(BDT_SC_PRI_RESTRI)
                .join(DT_SC_MANIFEST).on(BDT_SC_PRI_RESTRI.BDT_SC_ID.eq(DT_SC_MANIFEST.DT_SC_ID))
                .join(AGENCY_ID_LIST).on(
                        BDT_SC_PRI_RESTRI.AGENCY_ID_LIST_ID.eq(AGENCY_ID_LIST.AGENCY_ID_LIST_ID))
                .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(ULong.valueOf(dtScManifestId)))
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

    private List<BieEditCodeList> getBieEditCodeListByBasedCodeListIds(List<Long> basedCodeListIds) {
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
                        CODE_LIST.CODE_LIST_ID.in(
                                basedCodeListIds.stream()
                                        .map(e -> ULong.valueOf(e))
                                        .collect(Collectors.toList())),
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

    private List<BieEditCodeList> getCodeListsByBasedCodeList(Long basedCodeList) {
        if (basedCodeList == null) {
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
                        CODE_LIST_MANIFEST.BASED_CODE_LIST_MANIFEST_ID.eq(ULong.valueOf(basedCodeList)),
                        CODE_LIST.STATE.eq("Published")))
                .fetchInto(BieEditCodeList.class);

        bieEditCodeLists.addAll(bieEditCodeListsByBasedCodeListId);

        for (BieEditCodeList bieEditCodeList : bieEditCodeListsByBasedCodeListId) {
            bieEditCodeLists.addAll(getCodeListsByBasedCodeList(bieEditCodeList.getCodeListManifestId()));
        }

        return bieEditCodeLists;
    }

    @Override
    public void updateState(BieState state) {
        repository.updateState(topLevelAbie.getTopLevelAbieId(), state);
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
                .set(ABIE.VERSION, emptyToNull(abieNodeDetail.getVersion()))
                .set(ABIE.STATUS, emptyToNull(abieNodeDetail.getStatus()))
                .set(ABIE.REMARK, emptyToNull(abieNodeDetail.getRemark()))
                .set(ABIE.BIZ_TERM, emptyToNull(abieNodeDetail.getBizTerm()))
                .set(ABIE.DEFINITION, emptyToNull(abieNodeDetail.getDefinition()))
                .set(ABIE.LAST_UPDATED_BY, ULong.valueOf(sessionService.userId(user)))
                .set(ABIE.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(ABIE.ABIE_ID.eq(ULong.valueOf(abieNodeDetail.getAbieId())))
                .execute();
    }

    private void updateDetail(BieEditAsbiepNodeDetail asbiepNodeDetail) {
        if (asbiepNodeDetail.getBieCardinalityMin() != null) {
            dslContext.update(ASBIE)
                    .set(ASBIE.CARDINALITY_MIN, asbiepNodeDetail.getBieCardinalityMin())
                    .where(ASBIE.ASBIE_ID.eq(ULong.valueOf(asbiepNodeDetail.getAsbieId())))
                    .execute();
        }
        if (asbiepNodeDetail.getBieCardinalityMax() != null) {
            dslContext.update(ASBIE)
                    .set(ASBIE.CARDINALITY_MAX, asbiepNodeDetail.getBieCardinalityMax())
                    .where(ASBIE.ASBIE_ID.eq(ULong.valueOf(asbiepNodeDetail.getAsbieId())))
                    .execute();
        }
        Record1<Byte> rs = dslContext.select(ASCCP.IS_NILLABLE).from(ASCCP)
                .where(ASCCP.ASCCP_ID.eq(ULong.valueOf(asbiepNodeDetail.getAsccpId()))).fetchOne();

        if (rs.getValue(ASCCP.IS_NILLABLE) != 1 && asbiepNodeDetail.getBieNillable() != null) {
            dslContext.update(ASBIE)
                    .set(ASBIE.IS_NILLABLE, (byte) (asbiepNodeDetail.getBieNillable() ? 1 : 0))
                    .where(ASBIE.ASBIE_ID.eq(ULong.valueOf(asbiepNodeDetail.getAsbieId())))
                    .execute();
        }

        long userId = sessionService.userId(user);
        LocalDateTime timestamp = LocalDateTime.now();

        dslContext.update(ASBIE)
                .set(ASBIE.IS_USED, (byte) (asbiepNodeDetail.isUsed() ? 1 : 0))
                .set(ASBIE.DEFINITION, emptyToNull(asbiepNodeDetail.getContextDefinition()))
                .set(ASBIE.LAST_UPDATED_BY, ULong.valueOf(userId))
                .set(ASBIE.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(ASBIE.ASBIE_ID.eq(ULong.valueOf(asbiepNodeDetail.getAsbieId())))
                .execute();

        dslContext.update(ASBIEP)
                .set(ASBIEP.BIZ_TERM, emptyToNull(asbiepNodeDetail.getBizTerm()))
                .set(ASBIEP.REMARK, emptyToNull(asbiepNodeDetail.getRemark()))
                .set(ASBIEP.LAST_UPDATED_BY, ULong.valueOf(userId))
                .set(ASBIEP.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(ASBIEP.ASBIEP_ID.eq(ULong.valueOf(asbiepNodeDetail.getAsbiepId())))
                .execute();
    }

    private void updateDetail(BieEditBbiepNodeDetail bbiepNodeDetail) {
        if (bbiepNodeDetail.getBieCardinalityMin() != null) {
            dslContext.update(BBIE)
                    .set(BBIE.CARDINALITY_MIN, bbiepNodeDetail.getBieCardinalityMin())
                    .where(BBIE.BBIE_ID.eq(ULong.valueOf(bbiepNodeDetail.getBbieId()))).execute();
        }
        if (bbiepNodeDetail.getBieCardinalityMax() != null) {
            dslContext.update(BBIE)
                    .set(BBIE.CARDINALITY_MAX, bbiepNodeDetail.getBieCardinalityMax())
                    .where(BBIE.BBIE_ID.eq(ULong.valueOf(bbiepNodeDetail.getBbieId()))).execute();
        }

        Long bdtPriRestriId = bbiepNodeDetail.getBdtPriRestriId();
        Long codeListId = bbiepNodeDetail.getCodeListId();
        Long agencyIdListId = bbiepNodeDetail.getAgencyIdListId();

        if (agencyIdListId != null) {
            dslContext.update(BBIE)
                    .set(BBIE.AGENCY_ID_LIST_ID, ULong.valueOf(agencyIdListId))
                    .setNull(BBIE.CODE_LIST_ID)
                    .setNull(BBIE.BDT_PRI_RESTRI_ID)
                    .where(BBIE.BBIE_ID.eq(ULong.valueOf(bbiepNodeDetail.getBbieId()))).execute();
        } else if (codeListId != null) {
            dslContext.update(BBIE)
                    .setNull(BBIE.AGENCY_ID_LIST_ID)
                    .set(BBIE.CODE_LIST_ID, ULong.valueOf(codeListId))
                    .setNull(BBIE.BDT_PRI_RESTRI_ID)
                    .where(BBIE.BBIE_ID.eq(ULong.valueOf(bbiepNodeDetail.getBbieId()))).execute();
        } else if (bdtPriRestriId != null) {
            dslContext.update(BBIE)
                    .setNull(BBIE.AGENCY_ID_LIST_ID)
                    .setNull(BBIE.CODE_LIST_ID)
                    .set(BBIE.BDT_PRI_RESTRI_ID, ULong.valueOf(bdtPriRestriId))
                    .where(BBIE.BBIE_ID.eq(ULong.valueOf(bbiepNodeDetail.getBbieId()))).execute();
        }

        BccRecord bccRecord = dslContext.selectFrom(BCC)
                .where(BCC.BCC_ID.eq(ULong.valueOf(bbiepNodeDetail.getBccId()))).fetchOne();

        if (bccRecord.getIsNillable() != 1 && bbiepNodeDetail.getBieNillable() != null) {
            dslContext.update(BBIE)
                    .set(BBIE.IS_NILLABLE, (byte) (bbiepNodeDetail.getBieNillable() ? 1 : 0))
                    .where(BBIE.BBIE_ID.eq(ULong.valueOf(bbiepNodeDetail.getBbieId()))).execute();
        }

        Record2<String, String> bccp = dslContext.select(BCCP.DEFAULT_VALUE, BCCP.FIXED_VALUE)
                .from(BCCP).where(BCCP.BCCP_ID.eq(ULong.valueOf(bbiepNodeDetail.getBccpId()))).fetchOne();

        if (bccRecord.getDefaultValue() == null &&
                bccRecord.getFixedValue() == null &&
                bccp.getValue(BCCP.DEFAULT_VALUE) == null &&
                bccp.getValue(BCCP.FIXED_VALUE) == null) {

            dslContext.update(BBIE)
                    .set(BBIE.FIXED_VALUE, emptyToNull(bbiepNodeDetail.getBieFixedValue()))
                    .set(BBIE.DEFAULT_VALUE, emptyToNull(bbiepNodeDetail.getBieDefaultValue()))
                    .where(BBIE.BBIE_ID.eq(ULong.valueOf(bbiepNodeDetail.getBbieId()))).execute();
        }

        dslContext.update(BBIE)
                .set(BBIE.IS_USED, (byte) (bbiepNodeDetail.isUsed() ? 1 : 0))
                .set(BBIE.DEFINITION, emptyToNull(bbiepNodeDetail.getContextDefinition()))
                .set(BBIE.EXAMPLE, emptyToNull(bbiepNodeDetail.getExample()))
                .where(BBIE.BBIE_ID.eq(ULong.valueOf(bbiepNodeDetail.getBbieId()))).execute();

        long userId = sessionService.userId(user);
        LocalDateTime timestamp = LocalDateTime.now();

        dslContext.update(BBIEP)
                .set(BBIEP.BIZ_TERM, emptyToNull(bbiepNodeDetail.getBizTerm()))
                .set(BBIEP.REMARK, emptyToNull(bbiepNodeDetail.getRemark()))
                .set(BBIEP.LAST_UPDATED_BY, ULong.valueOf(userId))
                .set(BBIEP.LAST_UPDATE_TIMESTAMP, timestamp)
                .where(BBIEP.BBIEP_ID.eq(ULong.valueOf(bbiepNodeDetail.getBbiepId())))
                .execute();
    }

    private void updateDetail(BieEditBbieScNodeDetail bbieScNodeDetail) {
        if (bbieScNodeDetail.getBieCardinalityMin() != null) {
            dslContext.update(BBIE_SC)
                    .set(BBIE_SC.CARDINALITY_MIN, bbieScNodeDetail.getBieCardinalityMin())
                    .where(BBIE_SC.BBIE_SC_ID.eq(ULong.valueOf(bbieScNodeDetail.getBbieScId()))).execute();
        }

        if (bbieScNodeDetail.getBieCardinalityMax() != null) {
            dslContext.update(BBIE_SC)
                    .set(BBIE_SC.CARDINALITY_MAX, bbieScNodeDetail.getBieCardinalityMax())
                    .where(BBIE_SC.BBIE_SC_ID.eq(ULong.valueOf(bbieScNodeDetail.getBbieScId()))).execute();
        }

        Long dtScPriRestriId = bbieScNodeDetail.getDtScPriRestriId();
        Long codeListId = bbieScNodeDetail.getCodeListId();
        Long agencyIdListId = bbieScNodeDetail.getAgencyIdListId();

        if (agencyIdListId != null) {
            dslContext.update(BBIE_SC)
                    .setNull(BBIE_SC.DT_SC_PRI_RESTRI_ID)
                    .setNull(BBIE_SC.CODE_LIST_ID)
                    .set(BBIE_SC.AGENCY_ID_LIST_ID, ULong.valueOf(agencyIdListId))
                    .where(BBIE_SC.BBIE_SC_ID.eq(ULong.valueOf(bbieScNodeDetail.getBbieScId()))).execute();
        } else if (codeListId != null) {
            dslContext.update(BBIE_SC)
                    .setNull(BBIE_SC.DT_SC_PRI_RESTRI_ID)
                    .set(BBIE_SC.CODE_LIST_ID, ULong.valueOf(codeListId))
                    .setNull(BBIE_SC.AGENCY_ID_LIST_ID)
                    .where(BBIE_SC.BBIE_SC_ID.eq(ULong.valueOf(bbieScNodeDetail.getBbieScId()))).execute();
        } else if (dtScPriRestriId != null) {
            dslContext.update(BBIE_SC)
                    .set(BBIE_SC.DT_SC_PRI_RESTRI_ID, ULong.valueOf(dtScPriRestriId))
                    .setNull(BBIE_SC.CODE_LIST_ID)
                    .setNull(BBIE_SC.AGENCY_ID_LIST_ID)
                    .where(BBIE_SC.BBIE_SC_ID.eq(ULong.valueOf(bbieScNodeDetail.getBbieScId()))).execute();
        }

        DtScRecord dtScRecord = dslContext.select(DT_SC.fields())
                .from(DT_SC)
                .join(DT_SC_MANIFEST).on(DT_SC.DT_SC_ID.eq(DT_SC_MANIFEST.DT_SC_ID))
                .where(DT_SC_MANIFEST.DT_SC_MANIFEST_ID.eq(ULong.valueOf(bbieScNodeDetail.getDtScManifestId())))
                .fetchOneInto(DtScRecord.class);

        if (dtScRecord.getDefaultValue() == null && dtScRecord.getFixedValue() == null) {
            dslContext.update(BBIE_SC)
                    .set(BBIE_SC.DEFAULT_VALUE, emptyToNull(bbieScNodeDetail.getBieDefaultValue()))
                    .set(BBIE_SC.FIXED_VALUE, emptyToNull(bbieScNodeDetail.getBieFixedValue()))
                    .where(BBIE_SC.BBIE_SC_ID.eq(ULong.valueOf(bbieScNodeDetail.getBbieScId())))
                    .execute();
        }

        dslContext.update(BBIE_SC)
                .set(BBIE_SC.IS_USED, (byte) (bbieScNodeDetail.isUsed() ? 1 : 0))
                .set(BBIE_SC.DEFINITION, emptyToNull(bbieScNodeDetail.getContextDefinition()))
                .set(BBIE_SC.BIZ_TERM, emptyToNull(bbieScNodeDetail.getBizTerm()))
                .set(BBIE_SC.REMARK, emptyToNull(bbieScNodeDetail.getRemark()))
                .set(BBIE_SC.EXAMPLE, emptyToNull(bbieScNodeDetail.getExample()))
                .where(BBIE_SC.BBIE_SC_ID.eq(ULong.valueOf(bbieScNodeDetail.getBbieScId())))
                .execute();
    }

    private String emptyToNull(String str) {
        if (str != null) {
            str = str.trim();
        }
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        return str;
    }
}
