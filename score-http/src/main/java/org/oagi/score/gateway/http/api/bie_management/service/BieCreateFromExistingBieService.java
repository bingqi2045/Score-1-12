package org.oagi.score.gateway.http.api.bie_management.service;

import lombok.Data;
import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.score.data.TopLevelAsbiep;
import org.oagi.score.gateway.http.api.bie_management.data.BieCreateRequest;
import org.oagi.score.gateway.http.api.bie_management.data.bie_edit.CreateBieFromExistingBieRequest;
import org.oagi.score.gateway.http.api.cc_management.data.CcType;
import org.oagi.score.gateway.http.configuration.security.SessionService;
import org.oagi.score.gateway.http.event.BieCreateFromExistingBieRequestEvent;
import org.oagi.score.gateway.http.helper.ScoreGuid;
import org.oagi.score.gateway.http.helper.Utility;
import org.oagi.score.redis.event.EventListenerContainer;
import org.oagi.score.repo.api.bie.model.BieState;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AppUserRecord;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.TopLevelAsbiepRecord;
import org.oagi.score.repository.TopLevelAsbiepRepository;
import org.oagi.score.service.common.data.AppUser;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.jooq.impl.DSL.and;
import static org.oagi.score.repo.api.bie.model.BieState.Initiating;
import static org.oagi.score.repo.api.impl.jooq.entity.Tables.*;

@Service
@Transactional
public class BieCreateFromExistingBieService implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private BieRepository repository;

    @Autowired
    private TopLevelAsbiepRepository topLevelAsbiepRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private EventListenerContainer eventListenerContainer;

    @Autowired
    private BieService bieService;

    private final String INTERESTED_EVENT_NAME = "bieCreateFromExistingBieRequestEvent";

    @Override
    public void afterPropertiesSet() throws Exception {
        eventListenerContainer.addMessageListener(this, "onEventReceived",
                new ChannelTopic(INTERESTED_EVENT_NAME));
    }

    @Transactional
    public void createBieFromExistingBie(AuthenticatedPrincipal user, CreateBieFromExistingBieRequest request) {
        AppUser requester = sessionService.getAppUserByUsername(user);

        String topLevelAsbiepId = request.getTopLevelAsbiepId();
        String asbiepId = dslContext.select(ASBIE.TO_ASBIEP_ID)
                .from(ASBIE)
                .where(
                        and(ASBIE.HASH_PATH.eq(request.getAsbieHashPath()),
                            ASBIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(topLevelAsbiepId)))
                .fetchOneInto(String.class);

        TopLevelAsbiepRecord topLevelAsbiepRecord = dslContext.selectFrom(TOP_LEVEL_ASBIEP)
                .where(TOP_LEVEL_ASBIEP.TOP_LEVEL_ASBIEP_ID.eq(topLevelAsbiepId))
                .fetchOne();

        AppUserRecord bieOwnerRecord = dslContext.selectFrom(APP_USER)
                .where(APP_USER.APP_USER_ID.eq(topLevelAsbiepRecord.getOwnerUserId()))
                .fetchOne();

        if (requester.isDeveloper() && bieOwnerRecord.getIsDeveloper() == 0) {
            throw new IllegalArgumentException("Developer does not allow to create new BIE along with the end user's BIE.");
        }

        TopLevelAsbiep sourceTopLevelAsbiep = topLevelAsbiepRepository.findById(topLevelAsbiepId);

        if (asbiepId != null) {
            String copiedTopLevelAsbiepId =
                    repository.createTopLevelAsbiep(requester.getAppUserId(), sourceTopLevelAsbiep.getReleaseId(), Initiating);
            BieCreateFromExistingBieRequestEvent event = new BieCreateFromExistingBieRequestEvent(
                    topLevelAsbiepId, copiedTopLevelAsbiepId, asbiepId,
                    Collections.emptyList(), requester.getAppUserId()
            );
            /*
             * Message Publishing
             */
            redisTemplate.convertAndSend(INTERESTED_EVENT_NAME, event);
        } else {
            // Create empty BIE.
            if (request.getAsccpManifestId() == null) {
                throw new IllegalArgumentException("Unable to find data to create BIE.");
            }
            BieCreateRequest bieRequest = new BieCreateRequest();
            bieRequest.setAsccpManifestId(request.getAsccpManifestId());
            List<String> bizCtxIds = dslContext.select(BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID)
                    .from(BIZ_CTX_ASSIGNMENT)
                    .where(BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ASBIEP_ID.eq(topLevelAsbiepRecord.getTopLevelAsbiepId()))
                    .fetchInto(String.class);
            bieRequest.setBizCtxIds(bizCtxIds);
            bieService.createBie(user, bieRequest);
        }
    }


    /**
     * This method is invoked by 'bieCreateFromExistingBieRequestEvent' channel subscriber.
     *
     * @param event
     */
    @Transactional
    public void onEventReceived(BieCreateFromExistingBieRequestEvent event) {
        RLock lock = redissonClient.getLock("BieCreateFromExistingBieRequestEvent:" + event.hashCode());
        if (!lock.tryLock()) {
            return;
        }
        try {
            logger.debug("Received BieCreateFromExistingBieRequestEvent: " + event);

            BieCreateFromExistingBieContext context = new BieCreateFromExistingBieContext(event);
            context.execute();
        } finally {
            lock.unlock();
        }
    }

    private List<BieCreateFromExistingBieAbie> getAbieByOwnerTopLevelAsbiepId(String ownerTopLevelAsbiepId) {
        return dslContext.select(
                ABIE.ABIE_ID,
                ABIE.GUID,
                ABIE.PATH,
                ABIE.BASED_ACC_MANIFEST_ID
        ).from(ABIE)
                .where(ABIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(ownerTopLevelAsbiepId))
                .fetchInto(BieCreateFromExistingBieAbie.class);
    }

    private List<BieCreateFromExistingBieAsbie> getAsbieByOwnerTopLevelAsbiepId(String ownerTopLevelAsbiepId) {
        return dslContext.select(
                ASBIE.ASBIE_ID,
                ASBIE.GUID,
                ASBIE.PATH,
                ASBIE.FROM_ABIE_ID,
                ASBIE.TO_ASBIEP_ID,
                ASBIE.BASED_ASCC_MANIFEST_ID,
                ASBIE.DEFINITION,
                ASBIE.CARDINALITY_MIN,
                ASBIE.CARDINALITY_MAX,
                ASBIE.IS_NILLABLE.as("nillable"),
                ASBIE.REMARK,
                ASBIE.SEQ_KEY,
                ASBIE.IS_USED.as("used")).from(ASBIE)
                .where(ASBIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(ownerTopLevelAsbiepId))
                .fetchInto(BieCreateFromExistingBieAsbie.class);
    }

    private List<BieCreateFromExistingBieBbie> getBbieByOwnerTopLevelAsbiepId(String ownerTopLevelAsbiepId) {
        return dslContext.select(
                BBIE.BBIE_ID,
                BBIE.GUID,
                BBIE.PATH,
                BBIE.BASED_BCC_MANIFEST_ID,
                BBIE.FROM_ABIE_ID,
                BBIE.TO_BBIEP_ID,
                BBIE.BDT_PRI_RESTRI_ID,
                BBIE.CODE_LIST_ID,
                BBIE.AGENCY_ID_LIST_ID,
                BBIE.CARDINALITY_MIN,
                BBIE.CARDINALITY_MAX,
                BBIE.DEFAULT_VALUE,
                BBIE.IS_NILLABLE.as("nillable"),
                BBIE.FIXED_VALUE,
                BBIE.IS_NULL.as("nill"),
                BBIE.DEFINITION,
                BBIE.EXAMPLE,
                BBIE.REMARK,
                BBIE.SEQ_KEY,
                BBIE.IS_USED.as("used")).from(BBIE)
                .where(BBIE.OWNER_TOP_LEVEL_ASBIEP_ID.eq(ownerTopLevelAsbiepId))
                .fetchInto(BieCreateFromExistingBieBbie.class);
    }

    private List<BieCreateFromExistingBieAsbiep> getAsbiepByOwnerTopLevelAsbiepId(String ownerTopLevelAsbiepId) {
        return dslContext.select(
                ASBIEP.ASBIEP_ID,
                ASBIEP.GUID,
                ASBIEP.PATH,
                ASBIEP.BASED_ASCCP_MANIFEST_ID,
                ASBIEP.ROLE_OF_ABIE_ID,
                ASBIEP.DEFINITION,
                ASBIEP.REMARK,
                ASBIEP.BIZ_TERM).from(ASBIEP)
                .where(ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID.eq(ownerTopLevelAsbiepId))
                .fetchInto(BieCreateFromExistingBieAsbiep.class);
    }

    private List<BieCreateFromExistingBieBbiep> getBbiepByOwnerTopLevelAsbiepId(String ownerTopLevelAsbiepId) {
        return dslContext.select(
                BBIEP.BBIEP_ID,
                BBIEP.GUID,
                BBIEP.PATH,
                BBIEP.BASED_BCCP_MANIFEST_ID,
                BBIEP.DEFINITION,
                BBIEP.REMARK,
                BBIEP.BIZ_TERM).from(BBIEP)
                .where(BBIEP.OWNER_TOP_LEVEL_ASBIEP_ID.eq(ownerTopLevelAsbiepId))
                .fetchInto(BieCreateFromExistingBieBbiep.class);
    }

    private List<BieCreateFromExistingBieBbieSc> getBbieScByOwnerTopLevelAsbiepId(String ownerTopLevelAsbiepId) {
        return dslContext.select(
                BBIE_SC.BBIE_SC_ID,
                BBIE_SC.GUID,
                BBIE_SC.PATH,
                BBIE_SC.BBIE_ID,
                BBIE_SC.BASED_DT_SC_MANIFEST_ID,
                BBIE_SC.DT_SC_PRI_RESTRI_ID,
                BBIE_SC.CODE_LIST_ID,
                BBIE_SC.AGENCY_ID_LIST_ID,
                BBIE_SC.CARDINALITY_MIN,
                BBIE_SC.CARDINALITY_MAX,
                BBIE_SC.DEFAULT_VALUE,
                BBIE_SC.FIXED_VALUE,
                BBIE_SC.DEFINITION,
                BBIE_SC.EXAMPLE,
                BBIE_SC.REMARK,
                BBIE_SC.BIZ_TERM,
                BBIE_SC.IS_USED.as("used")).from(BBIE_SC)
                .where(BBIE_SC.OWNER_TOP_LEVEL_ASBIEP_ID.eq(ownerTopLevelAsbiepId))
                .fetchInto(BieCreateFromExistingBieBbieSc.class);
    }

    @Data
    public static class BieCreateFromExistingBieAbie {

        private String abieId;
        private String guid;
        private String path;
        private String basedAccManifestId;

    }

    @Data
    public static class BieCreateFromExistingBieAsbie {

        private String asbieId;
        private String guid;
        private String path;
        private String fromAbieId;
        private String toAsbiepId;
        private String basedAsccManifestId;
        private String definition;
        private int cardinalityMin;
        private int cardinalityMax;
        private boolean nillable;
        private String remark;
        private double seqKey;
        private boolean used;

    }

    @Data
    public static class BieCreateFromExistingBieBbie {

        private String bbieId;
        private String guid;
        private String path;
        private String basedBccManifestId;
        private String fromAbieId;
        private String toBbiepId;
        private String bdtPriRestriId;
        private String codeListId;
        private String agencyIdListId;
        private int cardinalityMin;
        private int cardinalityMax;
        private String defaultValue;
        private boolean nillable;
        private String fixedValue;
        private boolean nill;
        private String definition;
        private String example;
        private String remark;
        private double seqKey;
        private boolean used;

    }

    @Data
    public static class BieCreateFromExistingBieAsbiep {

        private String asbiepId;
        private String guid;
        private String path;
        private String basedAsccpManifestId;
        private String roleOfAbieId;
        private String definition;
        private String remark;
        private String bizTerm;

    }

    @Data
    public static class BieCreateFromExistingBieBbiep {

        private String bbiepId;
        private String guid;
        private String path;
        private String basedBccpManifestId;
        private String definition;
        private String remark;
        private String bizTerm;

    }

    @Data
    public static class BieCreateFromExistingBieBbieSc {

        private String bbieScId;
        private String guid;
        private String path;
        private String bbieId;
        private String basedDtScManifestId;
        private String dtScPriRestriId;
        private String codeListId;
        private String agencyIdListId;
        private int cardinalityMin;
        private int cardinalityMax;
        private String defaultValue;
        private String fixedValue;
        private String definition;
        private String example;
        private String remark;
        private String bizTerm;
        private boolean used;

    }

    private class BieCreateFromExistingBieContext {

        private final TopLevelAsbiep sourceTopLevelAsbiep;
        private final TopLevelAsbiep targetTopLevelAsbiep;
        private List<String> bizCtxIds;
        private final String userId;
        private final String sourceAsccpKey;

        private LocalDateTime timestamp;


        private List<BieCreateFromExistingBieAbie> abieList;

        private List<BieCreateFromExistingBieAsbiep> asbiepList;
        private final Map<String, List<BieCreateFromExistingBieAsbiep>> roleOfAbieToAsbiepMap;

        private List<BieCreateFromExistingBieBbiep> bbiepList;

        private List<BieCreateFromExistingBieAsbie> asbieList;
        private final Map<String, List<BieCreateFromExistingBieAsbie>> fromAbieToAsbieMap;
        private final Map<String, List<BieCreateFromExistingBieAsbie>> toAsbiepToAsbieMap;

        private List<BieCreateFromExistingBieBbie> bbieList;
        private final Map<String, List<BieCreateFromExistingBieBbie>> fromAbieToBbieMap;
        private final Map<String, List<BieCreateFromExistingBieBbie>> toBbiepToBbieMap;

        private List<BieCreateFromExistingBieBbieSc> bbieScList;
        private final Map<String, List<BieCreateFromExistingBieBbieSc>> bbieToBbieScMap;

        public BieCreateFromExistingBieContext(BieCreateFromExistingBieRequestEvent event) {
            sourceAsccpKey = CcType.ASCCP.name() + "-" + dslContext.select(ASBIEP.BASED_ASCCP_MANIFEST_ID)
                    .from(ASBIEP)
                    .where(ASBIEP.ASBIEP_ID.eq(event.getAsbiepId()))
                    .fetchOneInto(ULong.class).toBigInteger().toString();
            String sourceTopLevelAsbiepId = event.getSourceTopLevelAsbiepId();
            sourceTopLevelAsbiep = topLevelAsbiepRepository.findById(sourceTopLevelAsbiepId);

            String targetTopLevelAsbiepId = event.getTargetTopLevelAsbiepId();
            targetTopLevelAsbiep = topLevelAsbiepRepository.findById(targetTopLevelAsbiepId);

            bizCtxIds = event.getBizCtxIds();
            if (bizCtxIds == null || bizCtxIds.isEmpty()) {
                bizCtxIds = dslContext.select(BIZ_CTX_ASSIGNMENT.BIZ_CTX_ID)
                        .from(BIZ_CTX_ASSIGNMENT)
                        .where(BIZ_CTX_ASSIGNMENT.TOP_LEVEL_ASBIEP_ID.eq(sourceTopLevelAsbiepId))
                        .fetchInto(String.class);
            }
            userId = event.getUserId();

            abieList = getAbieByOwnerTopLevelAsbiepId(sourceTopLevelAsbiepId);

            asbiepList = getAsbiepByOwnerTopLevelAsbiepId(sourceTopLevelAsbiepId);
            roleOfAbieToAsbiepMap = asbiepList.stream().collect(groupingBy(BieCreateFromExistingBieAsbiep::getRoleOfAbieId));

            asbieList = getAsbieByOwnerTopLevelAsbiepId(sourceTopLevelAsbiepId);
            fromAbieToAsbieMap = asbieList.stream().collect(groupingBy(BieCreateFromExistingBieAsbie::getFromAbieId));
            toAsbiepToAsbieMap = asbieList.stream().collect(groupingBy(BieCreateFromExistingBieAsbie::getToAsbiepId));

            bbiepList = getBbiepByOwnerTopLevelAsbiepId(sourceTopLevelAsbiepId);

            bbieList = getBbieByOwnerTopLevelAsbiepId(sourceTopLevelAsbiepId);
            fromAbieToBbieMap = bbieList.stream().collect(groupingBy(BieCreateFromExistingBieBbie::getFromAbieId));
            toBbiepToBbieMap = bbieList.stream().collect(groupingBy(BieCreateFromExistingBieBbie::getToBbiepId));

            bbieScList = getBbieScByOwnerTopLevelAsbiepId(sourceTopLevelAsbiepId);
            bbieToBbieScMap = bbieScList.stream().collect(groupingBy(BieCreateFromExistingBieBbieSc::getBbieId));

            arrange(event);
        }

        public void execute() {
            timestamp = LocalDateTime.now();
            logger.debug("Begin creating BIE from existing BIE with the source " + sourceTopLevelAsbiep.getTopLevelAsbiepId() +
                    " and the target " + targetTopLevelAsbiep.getTopLevelAsbiepId());

            repository.createBizCtxAssignments(
                    targetTopLevelAsbiep.getTopLevelAsbiepId(),
                    bizCtxIds);

            for (BieCreateFromExistingBieAbie abie : abieList) {
                String previousAbieId = abie.getAbieId();
                String nextAbieId = insertAbie(abie);

                fireChangeEvent("abie", previousAbieId, nextAbieId);
            }

            for (BieCreateFromExistingBieAsbiep asbiep : asbiepList) {
                String previousAsbiepId = asbiep.getAsbiepId();
                String nextAsbiepId = insertAsbiep(asbiep);

                fireChangeEvent("asbiep", previousAsbiepId, nextAsbiepId);
            }

            repository.updateAsbiepIdOnTopLevelAsbiep(
                    targetTopLevelAsbiep.getAsbiepId(),
                    targetTopLevelAsbiep.getTopLevelAsbiepId());

            for (BieCreateFromExistingBieBbiep bbiep : bbiepList) {
                String previousBbiepId = bbiep.getBbiepId();
                String nextBbiepId = insertBbiep(bbiep);

                fireChangeEvent("bbiep", previousBbiepId, nextBbiepId);
            }

            for (BieCreateFromExistingBieAsbie asbie : asbieList) {
                String previousAsbieId = asbie.getAsbieId();
                String nextAsbieId = insertAsbie(asbie);

                fireChangeEvent("asbie", previousAsbieId, nextAsbieId);
            }

            for (BieCreateFromExistingBieBbie bbie : bbieList) {
                String previousBbieId = bbie.getBbieId();
                String nextBbieId = insertBbie(bbie);

                fireChangeEvent("bbie", previousBbieId, nextBbieId);
            }

            for (BieCreateFromExistingBieBbieSc bbieSc : bbieScList) {
                String previousBbieScId = bbieSc.getBbieId();
                String nextBbieScId = insertBbieSc(bbieSc);

                fireChangeEvent("bbie_sc", previousBbieScId, nextBbieScId);
            }

            repository.updateState(targetTopLevelAsbiep.getTopLevelAsbiepId(), BieState.WIP);

            logger.debug("End create BIE from " + sourceTopLevelAsbiep.getTopLevelAsbiepId() +
                    " to " + targetTopLevelAsbiep.getTopLevelAsbiepId());
        }

        private void arrange(BieCreateFromExistingBieRequestEvent event) {
            List<BieCreateFromExistingBieAbie> abieList = new ArrayList();
            List<BieCreateFromExistingBieAsbiep> asbiepList = new ArrayList();
            List<BieCreateFromExistingBieBbiep> bbiepList = new ArrayList();
            List<BieCreateFromExistingBieAsbie> asbieList = new ArrayList();
            List<BieCreateFromExistingBieBbie> bbieList = new ArrayList();
            List<BieCreateFromExistingBieBbieSc> bbieScList = new ArrayList();

            Map<String, BieCreateFromExistingBieAsbiep> asbiepMap = this.asbiepList.stream()
                    .collect(Collectors.toMap(BieCreateFromExistingBieAsbiep::getAsbiepId, Function.identity()));

            Map<String, BieCreateFromExistingBieAbie> abieMap = this.abieList.stream()
                    .collect(Collectors.toMap(BieCreateFromExistingBieAbie::getAbieId, Function.identity()));

            Map<String, BieCreateFromExistingBieBbiep> bbiepMap = this.bbiepList.stream()
                    .collect(Collectors.toMap(BieCreateFromExistingBieBbiep::getBbiepId, Function.identity()));

            Queue<BieCreateFromExistingBieAsbiep> asbiepQueue = new LinkedList<>();
            BieCreateFromExistingBieAsbiep topLevelAsbiep = asbiepMap.get(event.getAsbiepId());
            asbiepQueue.add(topLevelAsbiep);

            while (!asbiepQueue.isEmpty()) {
                BieCreateFromExistingBieAsbiep currentAsbiep = asbiepQueue.poll();
                asbiepList.add(currentAsbiep);
                BieCreateFromExistingBieAbie roleOfAbie = abieMap.get(currentAsbiep.getRoleOfAbieId());
                abieList.add(roleOfAbie);

                List<BieCreateFromExistingBieAsbie> asbies = this.asbieList.stream()
                        .filter(e -> e.getFromAbieId().equals(roleOfAbie.getAbieId())).collect(Collectors.toList());

                asbieList.addAll(asbies);
                asbies.forEach(e -> {
                    if (asbiepMap.get(e.getToAsbiepId()) != null) {
                        asbiepQueue.add(asbiepMap.get(e.getToAsbiepId()));
                    }
                });

                List<BieCreateFromExistingBieBbie> bbies = this.bbieList.stream()
                        .filter(e -> e.getFromAbieId().equals(roleOfAbie.getAbieId())).collect(Collectors.toList());
                bbieList.addAll(bbies);
                bbies.forEach(e -> {
                    bbiepList.add(bbiepMap.get(e.getToBbiepId()));
                    bbieScList.addAll(this.bbieScList.stream()
                            .filter(sc -> sc.getBbieId().equals(e.getBbieId()))
                            .collect(Collectors.toList()));
                });
            }

            this.abieList = abieList;
            this.asbieList = asbieList;
            this.bbieList = bbieList;
            this.asbiepList = asbiepList;
            this.bbiepList = bbiepList;
            this.bbieScList = bbieScList;

            targetTopLevelAsbiep.setAsbiepId(topLevelAsbiep.getAsbiepId());
        }


        private void fireChangeEvent(String type, String previousVal, String nextVal) {
            switch (type) {
                case "abie":
                    roleOfAbieToAsbiepMap.getOrDefault(previousVal, Collections.emptyList()).stream().forEach(asbiep -> {
                        asbiep.setRoleOfAbieId(nextVal);
                    });
                    fromAbieToAsbieMap.getOrDefault(previousVal, Collections.emptyList()).stream().forEach(asbie -> {
                        asbie.setFromAbieId(nextVal);
                    });
                    fromAbieToBbieMap.getOrDefault(previousVal, Collections.emptyList()).stream().forEach(bbie -> {
                        bbie.setFromAbieId(nextVal);
                    });

                    break;

                case "asbiep":
                    if (previousVal.equals(targetTopLevelAsbiep.getAsbiepId())) {
                        targetTopLevelAsbiep.setAsbiepId(nextVal);
                    }

                    toAsbiepToAsbieMap.getOrDefault(previousVal, Collections.emptyList()).stream().forEach(asbie -> {
                        asbie.setToAsbiepId(nextVal);
                    });

                    break;

                case "bbiep":
                    toBbiepToBbieMap.getOrDefault(previousVal, Collections.emptyList()).stream().forEach(bbie -> {
                        bbie.setToBbiepId(nextVal);
                    });

                    break;

                case "bbie":
                    bbieToBbieScMap.getOrDefault(previousVal, Collections.emptyList()).stream().forEach(bbieSc -> {
                        bbieSc.setBbieId(nextVal);
                    });

                    break;
            }
        }

        private String insertAbie(BieCreateFromExistingBieAbie abie) {

            String abieId = UUID.randomUUID().toString();
            dslContext.insertInto(ABIE)
                    .set(ABIE.ABIE_ID, abieId)
                    .set(ABIE.GUID, ScoreGuid.randomGuid())
                    .set(ABIE.PATH, getPath(abie.getPath()))
                    .set(ABIE.HASH_PATH, getHashPath(abie.getPath()))
                    .set(ABIE.BASED_ACC_MANIFEST_ID, abie.getBasedAccManifestId())
                    .set(ABIE.CREATED_BY, userId)
                    .set(ABIE.LAST_UPDATED_BY, userId)
                    .set(ABIE.CREATION_TIMESTAMP, timestamp)
                    .set(ABIE.LAST_UPDATE_TIMESTAMP, timestamp)
                    // .set(ABIE.STATE, BieState.Initiating.getValue())
                    .set(ABIE.OWNER_TOP_LEVEL_ASBIEP_ID, targetTopLevelAsbiep.getTopLevelAsbiepId())
                    .execute();
            return abieId;
        }

        private String insertAsbiep(BieCreateFromExistingBieAsbiep asbiep) {

            String asbiepId = UUID.randomUUID().toString();
            dslContext.insertInto(ASBIEP)
                    .set(ASBIEP.ASBIEP_ID, asbiepId)
                    .set(ASBIEP.GUID, ScoreGuid.randomGuid())
                    .set(ASBIEP.PATH, getPath(asbiep.getPath()))
                    .set(ASBIEP.HASH_PATH, getHashPath(asbiep.getPath()))
                    .set(ASBIEP.BASED_ASCCP_MANIFEST_ID, asbiep.getBasedAsccpManifestId())
                    .set(ASBIEP.ROLE_OF_ABIE_ID, asbiep.getRoleOfAbieId())
                    .set(ASBIEP.DEFINITION, asbiep.getDefinition())
                    .set(ASBIEP.REMARK, asbiep.getRemark())
                    .set(ASBIEP.BIZ_TERM, asbiep.getBizTerm())
                    .set(ASBIEP.CREATED_BY, userId)
                    .set(ASBIEP.LAST_UPDATED_BY, userId)
                    .set(ASBIEP.CREATION_TIMESTAMP, timestamp)
                    .set(ASBIEP.LAST_UPDATE_TIMESTAMP, timestamp)
                    .set(ASBIEP.OWNER_TOP_LEVEL_ASBIEP_ID, targetTopLevelAsbiep.getTopLevelAsbiepId())
                    .execute();
            return asbiepId;
        }

        private String insertBbiep(BieCreateFromExistingBieBbiep bbiep) {

            String bbiepId = UUID.randomUUID().toString();
            dslContext.insertInto(BBIEP)
                    .set(BBIEP.BBIEP_ID, bbiepId)
                    .set(BBIEP.GUID, ScoreGuid.randomGuid())
                    .set(BBIEP.PATH, getPath(bbiep.getPath()))
                    .set(BBIEP.HASH_PATH, getHashPath(bbiep.getPath()))
                    .set(BBIEP.BASED_BCCP_MANIFEST_ID, bbiep.getBasedBccpManifestId())
                    .set(BBIEP.DEFINITION, bbiep.getDefinition())
                    .set(BBIEP.REMARK, bbiep.getRemark())
                    .set(BBIEP.BIZ_TERM, bbiep.getBizTerm())
                    .set(BBIEP.CREATED_BY, userId)
                    .set(BBIEP.LAST_UPDATED_BY, userId)
                    .set(BBIEP.CREATION_TIMESTAMP, timestamp)
                    .set(BBIEP.LAST_UPDATE_TIMESTAMP, timestamp)
                    .set(BBIEP.OWNER_TOP_LEVEL_ASBIEP_ID, targetTopLevelAsbiep.getTopLevelAsbiepId())
                    .execute();
            return bbiepId;
        }

        private String insertAsbie(BieCreateFromExistingBieAsbie asbie) {

            String asbieId = UUID.randomUUID().toString();
            dslContext.insertInto(ASBIE)
                    .set(ASBIE.ASBIE_ID, asbieId)
                    .set(ASBIE.GUID, ScoreGuid.randomGuid())
                    .set(ASBIE.PATH, getPath(asbie.getPath()))
                    .set(ASBIE.HASH_PATH, getHashPath(asbie.getPath()))
                    .set(ASBIE.FROM_ABIE_ID, asbie.getFromAbieId())
                    .set(ASBIE.TO_ASBIEP_ID, asbie.getToAsbiepId())
                    .set(ASBIE.BASED_ASCC_MANIFEST_ID, asbie.getBasedAsccManifestId())
                    .set(ASBIE.DEFINITION, asbie.getDefinition())
                    .set(ASBIE.REMARK, asbie.getRemark())
                    .set(ASBIE.CARDINALITY_MIN, asbie.getCardinalityMin())
                    .set(ASBIE.CARDINALITY_MAX, asbie.getCardinalityMax())
                    .set(ASBIE.IS_NILLABLE, (byte) ((asbie.isNillable()) ? 1 : 0))
                    .set(ASBIE.IS_USED, (byte) ((asbie.isUsed()) ? 1 : 0))
                    .set(ASBIE.SEQ_KEY, BigDecimal.valueOf(asbie.getSeqKey()))
                    .set(ASBIE.CREATED_BY, userId)
                    .set(ASBIE.LAST_UPDATED_BY, userId)
                    .set(ASBIE.CREATION_TIMESTAMP, timestamp)
                    .set(ASBIE.LAST_UPDATE_TIMESTAMP, timestamp)
                    .set(ASBIE.OWNER_TOP_LEVEL_ASBIEP_ID, targetTopLevelAsbiep.getTopLevelAsbiepId())
                    .execute();
            return asbieId;
        }

        private String insertBbie(BieCreateFromExistingBieBbie bbie) {

            String bbieId = UUID.randomUUID().toString();
            dslContext.insertInto(BBIE)
                    .set(BBIE.BBIE_ID, bbieId)
                    .set(BBIE.GUID, ScoreGuid.randomGuid())
                    .set(BBIE.PATH, getPath(bbie.getPath()))
                    .set(BBIE.HASH_PATH, getHashPath(bbie.getPath()))
                    .set(BBIE.FROM_ABIE_ID, bbie.getFromAbieId())
                    .set(BBIE.TO_BBIEP_ID, bbie.getToBbiepId())
                    .set(BBIE.BASED_BCC_MANIFEST_ID, bbie.getBasedBccManifestId())
                    .set(BBIE.BDT_PRI_RESTRI_ID, (bbie.getBdtPriRestriId() != null) ? bbie.getBdtPriRestriId() : null)
                    .set(BBIE.CODE_LIST_ID, (bbie.getCodeListId() != null) ? bbie.getCodeListId() : null)
                    .set(BBIE.AGENCY_ID_LIST_ID, (bbie.getAgencyIdListId() != null) ? bbie.getAgencyIdListId() : null)
                    .set(BBIE.DEFAULT_VALUE, bbie.getDefaultValue())
                    .set(BBIE.FIXED_VALUE, bbie.getFixedValue())
                    .set(BBIE.DEFINITION, bbie.getDefinition())
                    .set(BBIE.EXAMPLE, bbie.getExample())
                    .set(BBIE.REMARK, bbie.getRemark())
                    .set(BBIE.CARDINALITY_MIN, bbie.getCardinalityMin())
                    .set(BBIE.CARDINALITY_MAX, bbie.getCardinalityMax())
                    .set(BBIE.IS_NILLABLE, (byte) ((bbie.isNillable()) ? 1 : 0))
                    .set(BBIE.IS_NULL, (byte) ((bbie.isNill()) ? 1 : 0))
                    .set(BBIE.SEQ_KEY, BigDecimal.valueOf(bbie.getSeqKey()))
                    .set(BBIE.IS_USED, (byte) ((bbie.isUsed()) ? 1 : 0))
                    .set(BBIE.CREATED_BY, userId)
                    .set(BBIE.LAST_UPDATED_BY, userId)
                    .set(BBIE.CREATION_TIMESTAMP, timestamp)
                    .set(BBIE.LAST_UPDATE_TIMESTAMP, timestamp)
                    .set(BBIE.OWNER_TOP_LEVEL_ASBIEP_ID, targetTopLevelAsbiep.getTopLevelAsbiepId())
                    .execute();
            return bbieId;
        }

        private String insertBbieSc(BieCreateFromExistingBieBbieSc bbieSc) {

            String bbieScId = UUID.randomUUID().toString();
            dslContext.insertInto(BBIE_SC)
                    .set(BBIE_SC.BBIE_SC_ID, bbieScId)
                    .set(BBIE_SC.GUID, ScoreGuid.randomGuid())
                    .set(BBIE_SC.PATH, getPath(bbieSc.getPath()))
                    .set(BBIE_SC.HASH_PATH, getHashPath(bbieSc.getPath()))
                    .set(BBIE_SC.BBIE_ID, bbieSc.getBbieId())
                    .set(BBIE_SC.BASED_DT_SC_MANIFEST_ID, bbieSc.getBasedDtScManifestId())
                    .set(BBIE_SC.DT_SC_PRI_RESTRI_ID, (bbieSc.getDtScPriRestriId() != null) ? bbieSc.getDtScPriRestriId() : null)
                    .set(BBIE_SC.CODE_LIST_ID, (bbieSc.getCodeListId() != null) ? bbieSc.getCodeListId() : null)
                    .set(BBIE_SC.AGENCY_ID_LIST_ID, (bbieSc.getAgencyIdListId() != null) ? bbieSc.getAgencyIdListId() : null)
                    .set(BBIE_SC.DEFAULT_VALUE, bbieSc.getDefaultValue())
                    .set(BBIE_SC.FIXED_VALUE, bbieSc.getFixedValue())
                    .set(BBIE_SC.DEFINITION, bbieSc.getDefinition())
                    .set(BBIE_SC.EXAMPLE, bbieSc.getExample())
                    .set(BBIE_SC.REMARK, bbieSc.getRemark())
                    .set(BBIE_SC.BIZ_TERM, bbieSc.getBizTerm())
                    .set(BBIE_SC.CARDINALITY_MIN, bbieSc.getCardinalityMin())
                    .set(BBIE_SC.CARDINALITY_MAX, bbieSc.getCardinalityMax())
                    .set(BBIE_SC.IS_USED, (byte) ((bbieSc.isUsed()) ? 1 : 0))
                    .set(BBIE_SC.OWNER_TOP_LEVEL_ASBIEP_ID, targetTopLevelAsbiep.getTopLevelAsbiepId())
                    .set(BBIE.CREATED_BY, userId)
                    .set(BBIE.LAST_UPDATED_BY, userId)
                    .set(BBIE.CREATION_TIMESTAMP, timestamp)
                    .set(BBIE.LAST_UPDATE_TIMESTAMP, timestamp)
                    .execute();
            return bbieScId;
        }

        private String getPath(String path) {
            String seperator = sourceAsccpKey + ">";
            String[] tokens = path.split(sourceAsccpKey);
            if (tokens.length < 2) {
                return sourceAsccpKey;
            } else {
                if(path.endsWith(sourceAsccpKey)) {
                    return sourceAsccpKey;
                }
                return seperator + path.split(seperator)[1];
            }
        }

        private String getHashPath(String path) {
            return Utility.sha256(getPath(path));
        }
    }

}