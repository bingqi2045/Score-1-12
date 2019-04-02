package org.oagi.srt.gateway.http.api.bie_management.service.generate_expression;

import org.oagi.srt.data.*;
import org.oagi.srt.repository.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class GenerationContext implements InitializingBean {

    private TopLevelAbie topLevelAbie;

    @Autowired
    private AgencyIdListRepository agencyIdListRepository;

    @Autowired
    private AgencyIdListValueRepository agencyIdListValueRepository;

    @Autowired
    private CodeListRepository codeListRepository;

    @Autowired
    private CodeListValueRepository codeListValueRepository;

    @Autowired
    private XbtRepository xbtRepository;

    @Autowired
    private CdtAwdPriXpsTypeMapRepository cdtAwdPriXpsTypeMapRepository;

    @Autowired
    private CdtScAwdPriXpsTypeMapRepository cdtScAwdPriXpsTypeMapRepository;

    @Autowired
    private DTRepository dataTypeRepository;

    @Autowired
    private DTSCRepository dtScRepository;

    @Autowired
    private BdtPriRestriRepository bdtPriRestriRepository;

    @Autowired
    private BdtScPriRestriRepository bdtScPriRestriRepository;


    @Autowired
    private ACCRepository accRepository;

    @Autowired
    private ASCCPRepository asccpRepository;

    @Autowired
    private BCCPRepository bccpRepository;

    @Autowired
    private ASCCRepository asccRepository;

    @Autowired
    private BCCRepository bccRepository;


    @Autowired
    private ABIERepository abieRepository;

    @Autowired
    private ASBIEPRepository asbiepRepository;

    @Autowired
    private BBIEPRepository bbiepRepository;

    @Autowired
    private ASBIERepository asbieRepository;

    @Autowired
    private BBIERepository bbieRepository;

    @Autowired
    private BBIESCRepository bbieScRepository;


    @Autowired
    private BusinessContextRepository businessContextRepository;

    @Autowired
    private BusinessContextValueRepository businessContextValueRepository;

    @Autowired
    private ContextSchemeRepository contextSchemeRepository;

    @Autowired
    private ContextSchemeValueRepository contextSchemeValueRepository;

    @Autowired
    private ContextCategoryRepository contextCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReleaseRepository releaseRepository;

    public GenerationContext(TopLevelAbie topLevelAbie) {
        this.topLevelAbie = topLevelAbie;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (topLevelAbie == null) {
            throw new IllegalStateException("'topLevelAbie' parameter must not be null.");
        }

        init(topLevelAbie);
    }

    private void init(TopLevelAbie topLevelAbie) {
        List<BdtPriRestri> bdtPriRestriList = bdtPriRestriRepository.findAll();
        findBdtPriRestriByBdtIdAndDefaultIsTrueMap = bdtPriRestriList.stream()
                .filter(e -> e.isDefault())
                .collect(Collectors.toMap(e -> e.getBdtId(), Function.identity()));
        findBdtPriRestriMap = bdtPriRestriList.stream()
                .collect(Collectors.toMap(e -> e.getBdtPriRestriId(), Function.identity()));

        List<BdtScPriRestri> bdtScPriRestriList = bdtScPriRestriRepository.findAll();
        findBdtScPriRestriByBdtIdAndDefaultIsTrueMap = bdtScPriRestriList.stream()
                .filter(e -> e.isDefault())
                .collect(Collectors.toMap(e -> e.getBdtScId(), Function.identity()));
        findBdtScPriRestriMap = bdtScPriRestriList.stream()
                .collect(Collectors.toMap(e -> e.getBdtScPriRestriId(), Function.identity()));

        List<CdtAwdPriXpsTypeMap> cdtAwdPriXpsTypeMapList = cdtAwdPriXpsTypeMapRepository.findAll();
        findCdtAwdPriXpsTypeMapMap = cdtAwdPriXpsTypeMapList.stream()
                .collect(Collectors.toMap(e -> e.getCdtAwdPriXpsTypeMapId(), Function.identity()));

        List<CdtScAwdPriXpsTypeMap> cdtScAwdPriXpsTypeMapList = cdtScAwdPriXpsTypeMapRepository.findAll();
        findCdtScAwdPriXpsTypeMapMap = cdtScAwdPriXpsTypeMapList.stream()
                .collect(Collectors.toMap(e -> e.getCdtScAwdPriXpsTypeMapId(), Function.identity()));

        List<Xbt> xbtList = xbtRepository.findAll();
        findXbtMap = xbtList.stream()
                .collect(Collectors.toMap(e -> e.getXbtId(), Function.identity()));

        List<CodeList> codeLists = codeListRepository.findAll();
        findCodeListMap = codeLists.stream()
                .collect(Collectors.toMap(e -> e.getCodeListId(), Function.identity()));

        List<CodeListValue> codeListValues = codeListValueRepository.findAll();
        findCodeListValueByCodeListIdAndUsedIndicatorIsTrueMap = codeListValues.stream()
                .filter(e -> e.isUsedIndicator())
                .collect(Collectors.groupingBy(e -> e.getCodeListId()));

        List<ACC> accList = accRepository.findAll();
        findACCMap = accList.stream()
                .collect(Collectors.toMap(e -> e.getAccId(), Function.identity()));

        List<BCC> bccList = bccRepository.findAll();
        findBCCMap = bccList.stream()
                .collect(Collectors.toMap(e -> e.getBccId(), Function.identity()));

        List<BCCP> bccpList = bccpRepository.findAll();
        findBCCPMap = bccpList.stream()
                .collect(Collectors.toMap(e -> e.getBccpId(), Function.identity()));

        List<ASCC> asccList = asccRepository.findAll();
        findASCCMap = asccList.stream()
                .collect(Collectors.toMap(e -> e.getAsccId(), Function.identity()));

        List<ASCCP> asccpList = asccpRepository.findAll();
        findASCCPMap = asccpList.stream()
                .collect(Collectors.toMap(e -> e.getAsccpId(), Function.identity()));

        List<DT> dataTypeList = dataTypeRepository.findAll();
        findDTMap = dataTypeList.stream()
                .collect(Collectors.toMap(e -> e.getDtId(), Function.identity()));

        List<DTSC> dtScList = dtScRepository.findAll();
        findDtScMap = dtScList.stream()
                .collect(Collectors.toMap(e -> e.getDtScId(), Function.identity()));

        List<AgencyIdList> agencyIdLists = agencyIdListRepository.findAll();
        findAgencyIdListMap = agencyIdLists.stream()
                .collect(Collectors.toMap(e -> e.getAgencyIdListId(), Function.identity()));

        List<AgencyIdListValue> agencyIdListValues = agencyIdListValueRepository.findAll();
        findAgencyIdListValueMap = agencyIdListValues.stream()
                .collect(Collectors.toMap(e -> e.getAgencyIdListValueId(), Function.identity()));
        findAgencyIdListValueByOwnerListIdMap = agencyIdListValues.stream()
                .collect(Collectors.groupingBy(e -> e.getOwnerListId()));

        List<ABIE> abieList = abieRepository.findByOwnerTopLevelAbieId(topLevelAbie.getTopLevelAbieId());
        findAbieMap = abieList.stream()
                .collect(Collectors.toMap(e -> e.getAbieId(), Function.identity()));

        List<BBIE> bbieList =
                bbieRepository.findByOwnerTopLevelAbieIdAndUsed(topLevelAbie.getTopLevelAbieId(), true);
        findBbieByFromAbieIdAndUsedIsTrueMap = bbieList.stream()
                .filter(e -> e.isUsed())
                .collect(Collectors.groupingBy(e -> e.getFromAbieId()));

        List<BBIESC> bbieScList =
                bbieScRepository.findByOwnerTopLevelAbieIdAndUsed(topLevelAbie.getTopLevelAbieId(), true);
        findBbieScByBbieIdAndUsedIsTrueMap = bbieScList.stream()
                .filter(e -> e.isUsed())
                .collect(Collectors.groupingBy(e -> e.getBbieId()));

        List<ASBIE> asbieList =
                asbieRepository.findByOwnerTopLevelAbieIdAndUsed(topLevelAbie.getTopLevelAbieId(), true);
        findAsbieByFromAbieIdAndUsedIsTrueMap = asbieList.stream()
                .filter(e -> e.isUsed())
                .collect(Collectors.groupingBy(e -> e.getFromAbieId()));

        List<ASBIEP> asbiepList =
                asbiepRepository.findByOwnerTopLevelAbieId(topLevelAbie.getTopLevelAbieId());
        findASBIEPMap = asbiepList.stream()
                .collect(Collectors.toMap(e -> e.getAsbiepId(), Function.identity()));
        findAsbiepByRoleOfAbieIdMap = asbiepList.stream()
                .collect(Collectors.toMap(e -> e.getRoleOfAbieId(), Function.identity()));

        List<BBIEP> bbiepList =
                bbiepRepository.findByOwnerTopLevelAbieId(topLevelAbie.getTopLevelAbieId());
        findBBIEPMap = bbiepList.stream()
                .collect(Collectors.toMap(e -> e.getBbiepId(), Function.identity()));

        findUserNameMap = userRepository.getUsernameMap();
        findReleaseNumberMap = releaseRepository.findAll().stream()
                .collect(Collectors.toMap(e -> e.getReleaseId(), e -> e.getReleaseNum()));

        findContextSchemeMap = contextSchemeRepository.findAll().stream()
                .collect(Collectors.toMap(e -> e.getCtxSchemeId(), Function.identity()));
        findContextCategoryMap = contextCategoryRepository.findAll().stream()
                .collect(Collectors.toMap(e -> e.getCtxCategoryId(), Function.identity()));
    }

    public TopLevelAbie getTopLevelAbie() {
        return topLevelAbie;
    }

    // Prepared Datas
    private Map<Long, BdtPriRestri> findBdtPriRestriByBdtIdAndDefaultIsTrueMap;

    public BdtPriRestri findBdtPriRestriByBdtIdAndDefaultIsTrue(long bdtId) {
        return (bdtId > 0L) ? findBdtPriRestriByBdtIdAndDefaultIsTrueMap.get(bdtId) : null;
    }

    private Map<Long, BdtPriRestri> findBdtPriRestriMap;

    public BdtPriRestri findBdtPriRestri(Long bdtPriRestriId) {
        return (bdtPriRestriId != null && bdtPriRestriId > 0L) ? findBdtPriRestriMap.get(bdtPriRestriId) : null;
    }

    private Map<Long, CdtAwdPriXpsTypeMap> findCdtAwdPriXpsTypeMapMap;

    public CdtAwdPriXpsTypeMap findCdtAwdPriXpsTypeMap(Long cdtAwdPriXpsTypeMapId) {
        return (cdtAwdPriXpsTypeMapId != null && cdtAwdPriXpsTypeMapId > 0L) ?
                findCdtAwdPriXpsTypeMapMap.get(cdtAwdPriXpsTypeMapId) : null;
    }

    private Map<Long, BdtScPriRestri> findBdtScPriRestriByBdtIdAndDefaultIsTrueMap;

    public BdtScPriRestri findBdtScPriRestriByBdtScIdAndDefaultIsTrue(long bdtScId) {
        return (bdtScId > 0L) ? findBdtScPriRestriByBdtIdAndDefaultIsTrueMap.get(bdtScId) : null;
    }

    private Map<Long, BdtScPriRestri> findBdtScPriRestriMap;

    public BdtScPriRestri findBdtScPriRestri(Long bdtScPriRestriId) {
        return (bdtScPriRestriId != null && bdtScPriRestriId > 0L) ? findBdtScPriRestriMap.get(bdtScPriRestriId) : null;
    }

    private Map<Long, CdtScAwdPriXpsTypeMap> findCdtScAwdPriXpsTypeMapMap;

    public CdtScAwdPriXpsTypeMap findCdtScAwdPriXpsTypeMap(Long cdtScAwdPriXpsTypeMapId) {
        return (cdtScAwdPriXpsTypeMapId != null && cdtScAwdPriXpsTypeMapId > 0L) ?
                findCdtScAwdPriXpsTypeMapMap.get(cdtScAwdPriXpsTypeMapId) : null;
    }

    private Map<Long, Xbt> findXbtMap;

    public Xbt findXbt(long xbtId) {
        return (xbtId > 0L) ? findXbtMap.get(xbtId) : null;
    }

    private Map<Long, CodeList> findCodeListMap;

    public CodeList findCodeList(Long codeListId) {
        return (codeListId != null && codeListId > 0L) ? findCodeListMap.get(codeListId) : null;
    }

    private Map<Long, List<CodeListValue>> findCodeListValueByCodeListIdAndUsedIndicatorIsTrueMap;

    public List<CodeListValue> findCodeListValueByCodeListIdAndUsedIndicatorIsTrue(long codeListId) {
        return findCodeListValueByCodeListIdAndUsedIndicatorIsTrueMap.containsKey(codeListId) ?
                findCodeListValueByCodeListIdAndUsedIndicatorIsTrueMap.get(codeListId) :
                Collections.emptyList();
    }

    private Map<Long, ACC> findACCMap;

    public ACC findACC(long accId) {
        return (accId > 0L) ? findACCMap.get(accId) : null;
    }

    private Map<Long, BCC> findBCCMap;

    public BCC findBCC(long bccId) {
        return (bccId > 0L) ? findBCCMap.get(bccId) : null;
    }

    private Map<Long, BCCP> findBCCPMap;

    public BCCP findBCCP(long bccpId) {
        return (bccpId > 0L) ? findBCCPMap.get(bccpId) : null;
    }

    private Map<Long, ASCC> findASCCMap;

    public ASCC findASCC(long asccId) {
        return (asccId > 0L) ? findASCCMap.get(asccId) : null;
    }

    private Map<Long, ASCCP> findASCCPMap;

    public ASCCP findASCCP(long asccpId) {
        return (asccpId > 0L) ? findASCCPMap.get(asccpId) : null;
    }

    private Map<Long, DT> findDTMap;

    public DT findDT(long dtId) {
        return (dtId > 0L) ? findDTMap.get(dtId) : null;
    }

    private Map<Long, DTSC> findDtScMap;

    public DTSC findDtSc(long dtScId) {
        return (dtScId > 0L) ? findDtScMap.get(dtScId) : null;
    }

    private Map<Long, AgencyIdList> findAgencyIdListMap;

    public AgencyIdList findAgencyIdList(Long agencyIdListId) {
        return (agencyIdListId != null && agencyIdListId > 0L) ? findAgencyIdListMap.get(agencyIdListId) : null;
    }

    private Map<Long, AgencyIdListValue> findAgencyIdListValueMap;
    private Map<Long, List<AgencyIdListValue>> findAgencyIdListValueByOwnerListIdMap;

    public AgencyIdListValue findAgencyIdListValue(Long agencyIdListValueId) {
        return (agencyIdListValueId != null && agencyIdListValueId > 0L) ? findAgencyIdListValueMap.get(agencyIdListValueId) : null;
    }

    public List<AgencyIdListValue> findAgencyIdListValueByOwnerListId(long ownerListId) {
        return findAgencyIdListValueByOwnerListIdMap.containsKey(ownerListId) ?
                findAgencyIdListValueByOwnerListIdMap.get(ownerListId) :
                Collections.emptyList();
    }

    private Map<Long, ABIE> findAbieMap;

    public ABIE findAbie(long abieId) {
        return (abieId > 0L) ? findAbieMap.get(abieId) : null;
    }

    private Map<Long, List<BBIE>> findBbieByFromAbieIdAndUsedIsTrueMap;

    public List<BBIE> findBbieByFromAbieIdAndUsedIsTrue(long fromAbieId) {
        return findBbieByFromAbieIdAndUsedIsTrueMap.containsKey(fromAbieId) ?
                findBbieByFromAbieIdAndUsedIsTrueMap.get(fromAbieId) :
                Collections.emptyList();
    }

    private Map<Long, List<BBIESC>>
            findBbieScByBbieIdAndUsedIsTrueMap;

    public List<BBIESC> findBbieScByBbieIdAndUsedIsTrue(long bbieId) {
        return findBbieScByBbieIdAndUsedIsTrueMap.containsKey(bbieId) ?
                findBbieScByBbieIdAndUsedIsTrueMap.get(bbieId) :
                Collections.emptyList();
    }

    private Map<Long, List<ASBIE>> findAsbieByFromAbieIdAndUsedIsTrueMap;

    public List<ASBIE> findAsbieByFromAbieIdAndUsedIsTrue(long fromAbieId) {
        return findAsbieByFromAbieIdAndUsedIsTrueMap.containsKey(fromAbieId) ?
                findAsbieByFromAbieIdAndUsedIsTrueMap.get(fromAbieId) :
                Collections.emptyList();
    }

    private Map<Long, ASBIEP> findASBIEPMap;

    public ASBIEP findASBIEP(long asbiepId) {
        return (asbiepId > 0L) ? findASBIEPMap.get(asbiepId) : null;
    }

    private Map<Long, ASBIEP> findAsbiepByRoleOfAbieIdMap;

    public ASBIEP findAsbiepByRoleOfAbieId(long roleOfAbieId) {
        return (roleOfAbieId > 0L) ? findAsbiepByRoleOfAbieIdMap.get(roleOfAbieId) : null;
    }

    private Map<Long, BBIEP> findBBIEPMap;

    public BBIEP findBBIEP(long bbiepId) {
        return (bbiepId > 0L) ? findBBIEPMap.get(bbiepId) : null;
    }

    private Map<Long, String> findUserNameMap;

    public String findUserName(long userId) {
        return (userId > 0L) ? findUserNameMap.get(userId) : null;
    }

    private Map<Long, String> findReleaseNumberMap;

    public String findReleaseNumber(long releaseId) {
        return (releaseId > 0L) ? findReleaseNumberMap.get(releaseId) : null;
    }

    public ACC queryBasedACC(ABIE abie) {
        return (abie != null) ? findACC(abie.getBasedAccId()) : null;
    }

    // Get only Child BIEs whose is_used flag is true
    public List<BIE> queryChildBIEs(ABIE abie) {
        if (abie == null) {
            return Collections.emptyList();
        }
        List<BIE> result;
        Map<BIE, Double> sequence = new HashMap();
        ValueComparator bvc = new ValueComparator(sequence);
        Map<BIE, Double> ordered_sequence = new TreeMap(bvc);

        List<ASBIE> asbievo = findAsbieByFromAbieIdAndUsedIsTrue(abie.getAbieId());
        List<BBIE> bbievo = findBbieByFromAbieIdAndUsedIsTrue(abie.getAbieId());

        for (BBIE aBBIE : bbievo) {
            if (aBBIE.getCardinalityMax() != 0) //modify
                sequence.put(aBBIE, aBBIE.getSeqKey());
        }

        for (ASBIE aASBIE : asbievo) {
            if (aASBIE.getCardinalityMax() != 0)
                sequence.put(aASBIE, aASBIE.getSeqKey());
        }

        ordered_sequence.putAll(sequence);
        Set set = ordered_sequence.entrySet();
        Iterator i = set.iterator();
        result = new ArrayList();
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            result.add((BIE) me.getKey());
        }

        return result;
    }

    class ValueComparator implements Comparator<BIE> {

        Map<BIE, Double> base;

        public ValueComparator(Map<BIE, Double> base) {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with equals.
        public int compare(BIE a, BIE b) {
            if (base.get(a) <= base.get(b)) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }
    }

    // Get only SCs whose is_used is true.
    public List<BBIESC> queryBBIESCs(BBIE bbie) {
        return (bbie != null) ? findBbieScByBbieIdAndUsedIsTrue(bbie.getBbieId()) : Collections.emptyList();
    }

    public ASBIEP receiveASBIEP(ABIE abie) {
        return (abie != null) ? findAsbiepByRoleOfAbieId(abie.getAbieId()) : null;
    }

    public DT queryBDT(BBIE bbie) {
        BCC bcc = (bbie != null) ? findBCC(bbie.getBasedBccId()) : null;
        BCCP bccp = (bcc != null) ? findBCCP(bcc.getToBccpId()) : null;
        return (bccp != null) ? queryBDT(bccp) : null;
    }

    public DT queryBDT(BCCP bccp) {
        return (bccp != null) ? findDT(bccp.getBdtId()) : null;
    }

    public ASCCP queryBasedASCCP(ASBIEP asbiep) {
        return (asbiep != null) ? findASCCP(asbiep.getBasedAsccpId()) : null;
    }

    public ASCC queryBasedASCC(ASBIE asbie) {
        return (asbie != null) ? findASCC(asbie.getBasedAsccId()) : null;
    }

    public ABIE queryTargetABIE(ASBIEP asbiep) {
        return (asbiep != null) ? findAbie(asbiep.getRoleOfAbieId()) : null;
    }

    public ACC queryTargetACC(ASBIEP asbiep) {
        ABIE abie = (asbiep != null) ? findAbie(asbiep.getRoleOfAbieId()) : null;
        return (abie != null) ? findACC(abie.getBasedAccId()) : null;
    }

    public ABIE queryTargetABIE2(ASBIEP asbiep) {
        return (asbiep != null) ? findAbie(asbiep.getRoleOfAbieId()) : null;
    }

    public BCC queryBasedBCC(BBIE bbie) {
        return (bbie != null) ? findBCC(bbie.getBasedBccId()) : null;
    }

    public BCCP queryToBCCP(BCC bcc) {
        return (bcc != null) ? findBCCP(bcc.getToBccpId()) : null;
    }

    public CodeList getCodeList(BBIESC bbieSc) {
        if (bbieSc == null) {
            return null;
        }

        CodeList codeList = findCodeList(bbieSc.getCodeListId());
        if (codeList != null) {
            return codeList;
        }

        BdtScPriRestri bdtScPriRestri = findBdtScPriRestri(bbieSc.getDtScPriRestriId());
        if (bdtScPriRestri != null) {
            return findCodeList(bdtScPriRestri.getCodeListId());
        } else {
            DTSC gDTSC = findDtSc(bbieSc.getDtScId());
            BdtScPriRestri bBDTSCPrimitiveRestriction =
                    (gDTSC != null) ? findBdtScPriRestriByBdtScIdAndDefaultIsTrue(gDTSC.getDtScId()) : null;
            if (bBDTSCPrimitiveRestriction != null) {
                codeList = findCodeList(bBDTSCPrimitiveRestriction.getCodeListId());
            }
        }

        return codeList;
    }

    public AgencyIdList getAgencyIdList(BBIE bbie) {
        if (bbie == null) {
            return null;
        }
        AgencyIdList agencyIdList = findAgencyIdList(bbie.getAgencyIdListId());
        if (agencyIdList != null) {
            return agencyIdList;
        }

        BdtPriRestri bdtPriRestri =
                findBdtPriRestri(bbie.getBdtPriRestriId());
        if (bdtPriRestri != null) {
            agencyIdList = findAgencyIdList(bdtPriRestri.getAgencyIdListId());
        }

        if (agencyIdList == null) {
            DT bdt = queryAssocBDT(bbie);
            bdtPriRestri = findBdtPriRestriByBdtIdAndDefaultIsTrue(bdt.getDtId());
            if (bdtPriRestri != null) {
                agencyIdList = findAgencyIdList(bdtPriRestri.getAgencyIdListId());
            }
        }
        return agencyIdList;
    }

    public AgencyIdList getAgencyIdList(BBIESC bbieSc) {
        if (bbieSc == null) {
            return null;
        }
        AgencyIdList agencyIdList = findAgencyIdList(bbieSc.getAgencyIdListId());
        if (agencyIdList != null) {
            return agencyIdList;
        }

        BdtScPriRestri bdtScPriRestri =
                findBdtScPriRestri(bbieSc.getDtScPriRestriId());
        if (bdtScPriRestri != null) {
            agencyIdList = findAgencyIdList(bdtScPriRestri.getAgencyIdListId());
        }

        if (agencyIdList == null) {
            DTSC gDTSC = findDtSc(bbieSc.getDtScId());
            bdtScPriRestri = (gDTSC != null) ? findBdtScPriRestriByBdtScIdAndDefaultIsTrue(gDTSC.getDtScId()) : null;
            if (bdtScPriRestri != null) {
                agencyIdList = findAgencyIdList(bdtScPriRestri.getAgencyIdListId());
            }
        }
        return agencyIdList;
    }

    public List<CodeListValue> getCodeListValues(CodeList codeList) {
        return (codeList != null) ?
                findCodeListValueByCodeListIdAndUsedIndicatorIsTrue(codeList.getCodeListId()) :
                Collections.emptyList();
    }

    public ASBIEP queryAssocToASBIEP(ASBIE asbie) {
        return (asbie != null) ? findASBIEP(asbie.getToAsbiepId()) : null;
    }

    public DT queryAssocBDT(BBIE bbie) {
        BCC bcc = (bbie != null) ? findBCC(bbie.getBasedBccId()) : null;
        BCCP bccp = (bcc != null) ? findBCCP(bcc.getToBccpId()) : null;
        return queryBDT(bccp);
    }

    public BusinessContext findBusinessContext(TopLevelAbie topLevelAbie) {
        long bizCtxId = (topLevelAbie != null) ? abieRepository.findById(topLevelAbie.getAbieId()).getBizCtxId() : 0L;
        return businessContextRepository.findById(bizCtxId);
    }

    public List<ContextSchemeValue> findContextSchemeValue(BusinessContext businessContext) {
        List<BusinessContextValue> businessContextValues = (businessContext != null) ?
                businessContextValueRepository.findByBizCtxId(businessContext.getBizCtxId()) :
                Collections.emptyList();

        return businessContextValues.stream()
                .map(e -> contextSchemeValueRepository.findById(e.getCtxSchemeValueId()))
                .collect(Collectors.toList());
    }

    private Map<Long, ContextScheme> findContextSchemeMap;

    public ContextScheme findContextScheme(long ctxSchemeId) {
        return (ctxSchemeId > 0L) ? findContextSchemeMap.get(ctxSchemeId) : null;
    }

    private Map<Long, ContextCategory> findContextCategoryMap;

    public ContextCategory findContextCategory(long ctxCategoryId) {
        return (ctxCategoryId > 0L) ? findContextCategoryMap.get(ctxCategoryId) : null;
    }

    public AgencyIdList findAgencyIdList(ContextScheme contextScheme) {
        String schemeAgencyId = contextScheme.getSchemeAgencyId();
        if (StringUtils.isEmpty(schemeAgencyId)) {
            return null;
        }

        for (AgencyIdList agencyIdList : findAgencyIdListMap.values()) {
            if (schemeAgencyId.equals(agencyIdList.getListId())) {
                return agencyIdList;
            }
        }

        return null;
    }

}
