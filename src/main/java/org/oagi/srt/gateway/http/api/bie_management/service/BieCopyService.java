package org.oagi.srt.gateway.http.api.bie_management.service;

import lombok.Data;
import org.jooq.DSLContext;
import org.jooq.types.ULong;
import org.oagi.srt.data.BieState;
import org.oagi.srt.data.TopLevelAbie;
import org.oagi.srt.gateway.http.api.bie_management.data.BieCopyRequest;
import org.oagi.srt.gateway.http.configuration.security.SessionService;
import org.oagi.srt.gateway.http.event.BieCopyRequestEvent;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.redis.event.EventListenerContainer;
import org.oagi.srt.repo.BusinessInformationEntityRepository;
import org.oagi.srt.repository.TopLevelAbieRepository;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static org.oagi.srt.data.BieState.Initiating;
import static org.oagi.srt.entity.jooq.Tables.*;

@Service
@Transactional
public class BieCopyService implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private BieRepository repository;

    @Autowired
    private TopLevelAbieRepository topLevelAbieRepository;

    @Autowired
    private BusinessInformationEntityRepository bieRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisMessageListenerContainer messageListenerContainer;

    @Autowired
    private EventListenerContainer eventListenerContainer;

    private String INTERESTED_EVENT_NAME = "bieCopyRequestEvent";

    @Override
    public void afterPropertiesSet() throws Exception {
        eventListenerContainer.addMessageListener(this, "onEventReceived",
                new ChannelTopic(INTERESTED_EVENT_NAME));
    }

    @Transactional
    public void copyBie(User user, BieCopyRequest request) {
        BigInteger sourceTopLevelAbieId = request.getTopLevelAbieId();
        List<BigInteger> bizCtxIds = request.getBizCtxIds();
        BigInteger userId = sessionService.userId(user);
        long millis = System.currentTimeMillis();

        TopLevelAbie sourceTopLevelAbie = topLevelAbieRepository.findById(sourceTopLevelAbieId);
        ULong copiedTopLevelAbieId = bieRepository.insertTopLevelAbie()
                .setReleaseId(sourceTopLevelAbie.getReleaseId())
                .setBieState(Initiating)
                .setUserId(userId)
                .setTimestamp(millis)
                .execute();

        BieCopyRequestEvent bieCopyRequestEvent = new BieCopyRequestEvent(
                sourceTopLevelAbieId, copiedTopLevelAbieId.toBigInteger(), bizCtxIds, userId
        );

        /*
         * Message Publishing
         */
        redisTemplate.convertAndSend(INTERESTED_EVENT_NAME, bieCopyRequestEvent);
    }


    /**
     * This method is invoked by 'bieCopyRequestEvent' channel subscriber.
     *
     * @param bieCopyRequestEvent
     */
    @Transactional
    public void onEventReceived(BieCopyRequestEvent bieCopyRequestEvent) {
        RLock lock = redissonClient.getLock("BieCopyRequestEvent:" + bieCopyRequestEvent.hashCode());
        if (!lock.tryLock()) {
            return;
        }
        try {
            logger.debug("Received BieCopyRequestEvent: " + bieCopyRequestEvent);

            BieCopyContext copyContext = new BieCopyContext(bieCopyRequestEvent);
            copyContext.execute();
        } finally {
            lock.unlock();
        }
    }

    private List<BieCopyAbie> getAbieByOwnerTopLevelAbieId(BigInteger ownerTopLevelAbieId) {
        return dslContext.select(
                ABIE.ABIE_ID,
                ABIE.GUID,
                ABIE.BASED_ACC_MANIFEST_ID,
                ABIE.DEFINITION,
                ABIE.CLIENT_ID,
                ABIE.VERSION,
                ABIE.STATUS,
                ABIE.REMARK,
                ABIE.BIZ_TERM)
                .from(ABIE)
                .where(ABIE.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(ownerTopLevelAbieId)))
                .fetchInto(BieCopyAbie.class);
    }

    private List<BieCopyAsbie> getAsbieByOwnerTopLevelAbieId(BigInteger ownerTopLevelAbieId) {
        return dslContext.select(
                ASBIE.ASBIE_ID,
                ASBIE.GUID,
                ASBIE.FROM_ABIE_ID,
                ASBIE.TO_ASBIEP_ID,
                ASBIE.BASED_ASCC_MANIFEST_ID,
                ASBIE.DEFINITION,
                ASBIE.CARDINALITY_MIN,
                ASBIE.CARDINALITY_MAX,
                ASBIE.IS_NILLABLE.as("nillable"),
                ASBIE.REMARK,
                ASBIE.SEQ_KEY,
                ASBIE.IS_USED.as("used"))
                .from(ASBIE)
                .where(ASBIE.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(ownerTopLevelAbieId)))
                .fetchInto(BieCopyAsbie.class);
    }

    private List<BieCopyBbie> getBbieByOwnerTopLevelAbieId(BigInteger ownerTopLevelAbieId) {
        return dslContext.select(
                BBIE.BBIE_ID,
                BBIE.GUID,
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
                BBIE.IS_USED.as("used"))
                .from(BBIE)
                .where(BBIE.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(ownerTopLevelAbieId)))
                .fetchInto(BieCopyBbie.class);
    }

    private List<BieCopyAsbiep> getAsbiepByOwnerTopLevelAbieId(BigInteger ownerTopLevelAbieId) {
        return dslContext.select(
                ASBIEP.ASBIEP_ID,
                ASBIEP.GUID,
                ASBIEP.BASED_ASCCP_MANIFEST_ID,
                ASBIEP.ROLE_OF_ABIE_ID,
                ASBIEP.DEFINITION,
                ASBIEP.REMARK,
                ASBIEP.BIZ_TERM)
                .from(ASBIEP)
                .where(ASBIEP.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(ownerTopLevelAbieId)))
                .fetchInto(BieCopyAsbiep.class);
    }

    private List<BieCopyBbiep> getBbiepByOwnerTopLevelAbieId(BigInteger ownerTopLevelAbieId) {
        return dslContext.select(
                BBIEP.BBIEP_ID,
                BBIEP.GUID,
                BBIEP.BASED_BCCP_MANIFEST_ID,
                BBIEP.DEFINITION,
                BBIEP.REMARK,
                BBIEP.BIZ_TERM)
                .from(BBIEP)
                .where(BBIEP.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(ownerTopLevelAbieId)))
                .fetchInto(BieCopyBbiep.class);
    }

    private List<BieCopyBbieSc> getBbieScByOwnerTopLevelAbieId(BigInteger ownerTopLevelAbieId) {
        return dslContext.select(
                BBIE_SC.BBIE_SC_ID,
                BBIE_SC.GUID,
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
                BBIE_SC.IS_USED.as("used"))
                .from(BBIE_SC)
                .where(BBIE_SC.OWNER_TOP_LEVEL_ABIE_ID.eq(ULong.valueOf(ownerTopLevelAbieId)))
                .fetchInto(BieCopyBbieSc.class);
    }

    @Data
    public static class BieCopyAbie {

        private BigInteger abieId;
        private String guid;
        private BigInteger basedAccManifestId;
        private String definition;
        private Long clientId;
        private String version;
        private String status;
        private String remark;
        private String bizTerm;

    }

    @Data
    public static class BieCopyAsbie {

        private BigInteger asbieId;
        private String guid;
        private BigInteger fromAbieId;
        private BigInteger toAsbiepId;
        private BigInteger basedAsccManifestId;
        private String definition;
        private int cardinalityMin;
        private int cardinalityMax;
        private boolean nillable;
        private String remark;
        private double seqKey;
        private boolean used;

    }

    @Data
    public static class BieCopyBbie {

        private BigInteger bbieId;
        private String guid;
        private BigInteger basedBccManifestId;
        private BigInteger fromAbieId;
        private BigInteger toBbiepId;
        private Long bdtPriRestriId;
        private Long codeListId;
        private Long agencyIdListId;
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
    public static class BieCopyAsbiep {

        private BigInteger asbiepId;
        private String guid;
        private BigInteger basedAsccpManifestId;
        private BigInteger roleOfAbieId;
        private String definition;
        private String remark;
        private String bizTerm;

    }

    @Data
    public static class BieCopyBbiep {

        private BigInteger bbiepId;
        private String guid;
        private BigInteger basedBccpManifestId;
        private String definition;
        private String remark;
        private String bizTerm;

    }

    @Data
    public static class BieCopyBbieSc {

        private BigInteger bbieScId;
        private String guid;
        private BigInteger bbieId;
        private BigInteger basedDtScManifestId;
        private Long dtScPriRestriId;
        private Long codeListId;
        private Long agencyIdListId;
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

    private class BieCopyContext {

        private TopLevelAbie sourceTopLevelAbie;
        private TopLevelAbie copiedTopLevelAbie;
        private List<BigInteger> bizCtxIds;
        private BigInteger userId;

        private Timestamp timestamp;


        private List<BieCopyAbie> abieList;

        private List<BieCopyAsbiep> asbiepList;
        private Map<BigInteger, List<BieCopyAsbiep>> roleOfAbieToAsbiepMap;

        private List<BieCopyBbiep> bbiepList;

        private List<BieCopyAsbie> asbieList;
        private Map<BigInteger, List<BieCopyAsbie>> fromAbieToAsbieMap;
        private Map<BigInteger, List<BieCopyAsbie>> toAsbiepToAsbieMap;

        private List<BieCopyBbie> bbieList;
        private Map<BigInteger, List<BieCopyBbie>> fromAbieToBbieMap;
        private Map<BigInteger, List<BieCopyBbie>> toBbiepToBbieMap;

        private List<BieCopyBbieSc> bbieScList;
        private Map<BigInteger, List<BieCopyBbieSc>> bbieToBbieScMap;

        public BieCopyContext(BieCopyRequestEvent bieCopyRequestEvent) {
            BigInteger sourceTopLevelAbieId = bieCopyRequestEvent.getSourceTopLevelAbieId();
            sourceTopLevelAbie = topLevelAbieRepository.findById(sourceTopLevelAbieId);

            BigInteger copiedTopLevelAbieId = bieCopyRequestEvent.getCopiedTopLevelAbieId();
            copiedTopLevelAbie = topLevelAbieRepository.findById(copiedTopLevelAbieId);

            bizCtxIds = bieCopyRequestEvent.getBizCtxIds();
            userId = bieCopyRequestEvent.getUserId();

            abieList = getAbieByOwnerTopLevelAbieId(sourceTopLevelAbieId);

            asbiepList = getAsbiepByOwnerTopLevelAbieId(sourceTopLevelAbieId);
            roleOfAbieToAsbiepMap = asbiepList.stream().collect(groupingBy(BieCopyAsbiep::getRoleOfAbieId));

            bbiepList = getBbiepByOwnerTopLevelAbieId(sourceTopLevelAbieId);

            asbieList = getAsbieByOwnerTopLevelAbieId(sourceTopLevelAbieId);
            fromAbieToAsbieMap = asbieList.stream().collect(groupingBy(BieCopyAsbie::getFromAbieId));
            toAsbiepToAsbieMap = asbieList.stream().collect(groupingBy(BieCopyAsbie::getToAsbiepId));

            bbieList = getBbieByOwnerTopLevelAbieId(sourceTopLevelAbieId);
            fromAbieToBbieMap = bbieList.stream().collect(groupingBy(BieCopyBbie::getFromAbieId));
            toBbiepToBbieMap = bbieList.stream().collect(groupingBy(BieCopyBbie::getToBbiepId));

            bbieScList = getBbieScByOwnerTopLevelAbieId(sourceTopLevelAbieId);
            bbieToBbieScMap = bbieScList.stream().collect(groupingBy(BieCopyBbieSc::getBbieId));
        }

        public void execute() {
            timestamp = new Timestamp(System.currentTimeMillis());
            logger.debug("Begin copying from " + sourceTopLevelAbie.getTopLevelAbieId() +
                    " to " + copiedTopLevelAbie.getTopLevelAbieId());

            bieRepository.insertBizCtxAssignments()
                    .setTopLevelAbieId(copiedTopLevelAbie.getTopLevelAbieId())
                    .setBizCtxIds(bizCtxIds)
                    .execute();

            for (BieCopyAbie abie : abieList) {
                BigInteger previousAbieId = abie.getAbieId();
                BigInteger nextAbieId = insertAbie(abie);

                fireChangeEvent("abie", previousAbieId, nextAbieId);
            }

            bieRepository.updateTopLevelAbie()
                    .setAbieId(copiedTopLevelAbie.getAbieId())
                    .setTopLevelAbieId(copiedTopLevelAbie.getTopLevelAbieId())
                    .execute();

            for (BieCopyAsbiep asbiep : asbiepList) {
                BigInteger previousAsbiepId = asbiep.getAsbiepId();
                BigInteger nextAsbiepId = insertAsbiep(asbiep);

                fireChangeEvent("asbiep", previousAsbiepId, nextAsbiepId);
            }

            for (BieCopyBbiep bbiep : bbiepList) {
                BigInteger previousBbiepId = bbiep.getBbiepId();
                BigInteger nextBbiepId = insertBbiep(bbiep);

                fireChangeEvent("bbiep", previousBbiepId, nextBbiepId);
            }

            for (BieCopyAsbie asbie : asbieList) {
                BigInteger previousAsbieId = asbie.getAsbieId();
                BigInteger nextAsbieId = insertAsbie(asbie);

                fireChangeEvent("asbie", previousAsbieId, nextAsbieId);
            }

            for (BieCopyBbie bbie : bbieList) {
                BigInteger previousBbieId = bbie.getBbieId();
                BigInteger nextBbieId = insertBbie(bbie);

                fireChangeEvent("bbie", previousBbieId, nextBbieId);
            }

            for (BieCopyBbieSc bbieSc : bbieScList) {
                BigInteger previousBbieScId = bbieSc.getBbieId();
                BigInteger nextBbieScId = insertBbieSc(bbieSc);

                fireChangeEvent("bbie_sc", previousBbieScId, nextBbieScId);
            }

            repository.updateState(copiedTopLevelAbie.getTopLevelAbieId(), BieState.WIP);

            logger.debug("End copying from " + sourceTopLevelAbie.getTopLevelAbieId() +
                    " to " + copiedTopLevelAbie.getTopLevelAbieId());
        }


        private void fireChangeEvent(String type, BigInteger previousVal, BigInteger nextVal) {
            switch (type) {
                case "abie":
                    if (sourceTopLevelAbie.getAbieId().equals(previousVal)) {
                        copiedTopLevelAbie.setAbieId(nextVal);
                    }

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


        private BigInteger insertAbie(BieCopyAbie abie) {

            return dslContext.insertInto(ABIE)
                    .set(ABIE.GUID, SrtGuid.randomGuid())
                    .set(ABIE.BASED_ACC_MANIFEST_ID, ULong.valueOf(abie.getBasedAccManifestId()))
                    .set(ABIE.DEFINITION, abie.getDefinition())
                    .set(ABIE.CREATED_BY, ULong.valueOf(userId))
                    .set(ABIE.LAST_UPDATED_BY, ULong.valueOf(userId))
                    .set(ABIE.CREATION_TIMESTAMP, timestamp.toLocalDateTime())
                    .set(ABIE.LAST_UPDATE_TIMESTAMP, timestamp.toLocalDateTime())
                    .set(ABIE.STATE, BieState.Initiating.getValue())
                    .set(ABIE.CLIENT_ID, (abie.getClientId() != null) ? ULong.valueOf(abie.getClientId()) : null)
                    .set(ABIE.VERSION, abie.getVersion())
                    .set(ABIE.STATUS, abie.getStatus())
                    .set(ABIE.REMARK, abie.getRemark())
                    .set(ABIE.BIZ_TERM, abie.getBizTerm())
                    .set(ABIE.OWNER_TOP_LEVEL_ABIE_ID, ULong.valueOf(copiedTopLevelAbie.getTopLevelAbieId()))
                    .returning(ABIE.ABIE_ID).fetchOne().getValue(ABIE.ABIE_ID).toBigInteger();
        }

        private BigInteger insertAsbiep(BieCopyAsbiep asbiep) {

            return dslContext.insertInto(ASBIEP)
                    .set(ASBIEP.GUID, SrtGuid.randomGuid())
                    .set(ASBIEP.BASED_ASCCP_MANIFEST_ID, ULong.valueOf(asbiep.getBasedAsccpManifestId()))
                    .set(ASBIEP.ROLE_OF_ABIE_ID, ULong.valueOf(asbiep.getRoleOfAbieId()))
                    .set(ASBIEP.DEFINITION, asbiep.getDefinition())
                    .set(ASBIEP.REMARK, asbiep.getRemark())
                    .set(ASBIEP.BIZ_TERM, asbiep.getBizTerm())
                    .set(ASBIEP.CREATED_BY, ULong.valueOf(userId))
                    .set(ASBIEP.LAST_UPDATED_BY, ULong.valueOf(userId))
                    .set(ASBIEP.CREATION_TIMESTAMP, timestamp.toLocalDateTime())
                    .set(ASBIEP.LAST_UPDATE_TIMESTAMP, timestamp.toLocalDateTime())
                    .set(ASBIEP.OWNER_TOP_LEVEL_ABIE_ID, ULong.valueOf(copiedTopLevelAbie.getTopLevelAbieId()))
                    .returning(ASBIEP.ASBIEP_ID).fetchOne().getValue(ASBIEP.ASBIEP_ID).toBigInteger();
        }

        private BigInteger insertBbiep(BieCopyBbiep bbiep) {

            return dslContext.insertInto(BBIEP)
                    .set(BBIEP.GUID, SrtGuid.randomGuid())
                    .set(BBIEP.BASED_BCCP_MANIFEST_ID, ULong.valueOf(bbiep.getBasedBccpManifestId()))
                    .set(BBIEP.DEFINITION, bbiep.getDefinition())
                    .set(BBIEP.REMARK, bbiep.getRemark())
                    .set(BBIEP.BIZ_TERM, bbiep.getBizTerm())
                    .set(BBIEP.CREATED_BY, ULong.valueOf(userId))
                    .set(BBIEP.LAST_UPDATED_BY, ULong.valueOf(userId))
                    .set(BBIEP.CREATION_TIMESTAMP, timestamp.toLocalDateTime())
                    .set(BBIEP.LAST_UPDATE_TIMESTAMP, timestamp.toLocalDateTime())
                    .set(BBIEP.OWNER_TOP_LEVEL_ABIE_ID, ULong.valueOf(copiedTopLevelAbie.getTopLevelAbieId()))
                    .returning(BBIEP.BBIEP_ID).fetchOne().getValue(BBIEP.BBIEP_ID).toBigInteger();
        }

        private BigInteger insertAsbie(BieCopyAsbie asbie) {

            return dslContext.insertInto(ASBIE)
                    .set(ASBIE.GUID, SrtGuid.randomGuid())
                    .set(ASBIE.FROM_ABIE_ID, ULong.valueOf(asbie.getFromAbieId()))
                    .set(ASBIE.TO_ASBIEP_ID, ULong.valueOf(asbie.getToAsbiepId()))
                    .set(ASBIE.BASED_ASCC_MANIFEST_ID, ULong.valueOf(asbie.getBasedAsccManifestId()))
                    .set(ASBIE.DEFINITION, asbie.getDefinition())
                    .set(ASBIE.REMARK, asbie.getRemark())
                    .set(ASBIE.CARDINALITY_MIN, asbie.getCardinalityMin())
                    .set(ASBIE.CARDINALITY_MAX, asbie.getCardinalityMax())
                    .set(ASBIE.IS_NILLABLE, (byte) ((asbie.isNillable()) ? 1 : 0))
                    .set(ASBIE.IS_USED, (byte) ((asbie.isUsed()) ? 1 : 0))
                    .set(ASBIE.SEQ_KEY, BigDecimal.valueOf(asbie.getSeqKey()))
                    .set(ASBIE.CREATED_BY, ULong.valueOf(userId))
                    .set(ASBIE.LAST_UPDATED_BY, ULong.valueOf(userId))
                    .set(ASBIE.CREATION_TIMESTAMP, timestamp.toLocalDateTime())
                    .set(ASBIE.LAST_UPDATE_TIMESTAMP, timestamp.toLocalDateTime())
                    .set(ASBIE.OWNER_TOP_LEVEL_ABIE_ID, ULong.valueOf(copiedTopLevelAbie.getTopLevelAbieId()))
                    .returning(ASBIE.ASBIE_ID).fetchOne().getValue(ASBIE.ASBIE_ID).toBigInteger();
        }

        private BigInteger insertBbie(BieCopyBbie bbie) {

            return dslContext.insertInto(BBIE)
                    .set(BBIE.GUID, SrtGuid.randomGuid())
                    .set(BBIE.FROM_ABIE_ID, ULong.valueOf(bbie.getFromAbieId()))
                    .set(BBIE.TO_BBIEP_ID, ULong.valueOf(bbie.getToBbiepId()))
                    .set(BBIE.BASED_BCC_MANIFEST_ID, ULong.valueOf(bbie.getBasedBccManifestId()))
                    .set(BBIE.BDT_PRI_RESTRI_ID, (bbie.getBdtPriRestriId() != null) ? ULong.valueOf(bbie.getBdtPriRestriId()) : null)
                    .set(BBIE.CODE_LIST_ID, (bbie.getCodeListId() != null) ? ULong.valueOf(bbie.getCodeListId()) : null)
                    .set(BBIE.AGENCY_ID_LIST_ID, (bbie.getAgencyIdListId() != null) ? ULong.valueOf(bbie.getAgencyIdListId()) : null)
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
                    .set(BBIE.CREATED_BY, ULong.valueOf(userId))
                    .set(BBIE.LAST_UPDATED_BY, ULong.valueOf(userId))
                    .set(BBIE.CREATION_TIMESTAMP, timestamp.toLocalDateTime())
                    .set(BBIE.LAST_UPDATE_TIMESTAMP, timestamp.toLocalDateTime())
                    .set(BBIE.OWNER_TOP_LEVEL_ABIE_ID, ULong.valueOf(copiedTopLevelAbie.getTopLevelAbieId()))
                    .returning(BBIE.BBIE_ID).fetchOne().getValue(BBIE.BBIE_ID).toBigInteger();
        }

        private BigInteger insertBbieSc(BieCopyBbieSc bbieSc) {

            return dslContext.insertInto(BBIE_SC)
                    .set(BBIE_SC.GUID, SrtGuid.randomGuid())
                    .set(BBIE_SC.BBIE_ID, ULong.valueOf(bbieSc.getBbieId()))
                    .set(BBIE_SC.BASED_DT_SC_MANIFEST_ID, ULong.valueOf(bbieSc.getBasedDtScManifestId()))
                    .set(BBIE_SC.DT_SC_PRI_RESTRI_ID, (bbieSc.getDtScPriRestriId() != null) ? ULong.valueOf(bbieSc.getDtScPriRestriId()) : null)
                    .set(BBIE_SC.CODE_LIST_ID, (bbieSc.getCodeListId() != null) ? ULong.valueOf(bbieSc.getCodeListId()) : null)
                    .set(BBIE_SC.AGENCY_ID_LIST_ID, (bbieSc.getAgencyIdListId() != null) ? ULong.valueOf(bbieSc.getAgencyIdListId()) : null)
                    .set(BBIE_SC.DEFAULT_VALUE, bbieSc.getDefaultValue())
                    .set(BBIE_SC.FIXED_VALUE, bbieSc.getFixedValue())
                    .set(BBIE_SC.DEFINITION, bbieSc.getDefinition())
                    .set(BBIE_SC.EXAMPLE, bbieSc.getExample())
                    .set(BBIE_SC.REMARK, bbieSc.getRemark())
                    .set(BBIE_SC.BIZ_TERM, bbieSc.getBizTerm())
                    .set(BBIE_SC.CARDINALITY_MIN, bbieSc.getCardinalityMin())
                    .set(BBIE_SC.CARDINALITY_MAX, bbieSc.getCardinalityMax())
                    .set(BBIE_SC.IS_USED, (byte) ((bbieSc.isUsed()) ? 1 : 0))
                    .set(BBIE_SC.OWNER_TOP_LEVEL_ABIE_ID, ULong.valueOf(copiedTopLevelAbie.getTopLevelAbieId()))
                    .returning(BBIE_SC.BBIE_SC_ID).fetchOne().getValue(BBIE_SC.BBIE_SC_ID).toBigInteger();
        }
    }

}
