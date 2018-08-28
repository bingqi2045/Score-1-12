package org.oagi.srt.gateway.http.api.bie_management.service.generate_expression;

import org.oagi.srt.data.*;
import org.oagi.srt.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Scope(SCOPE_PROTOTYPE)
public class GenerationContext {

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

    // Prepared Datas
    private Map<Long, BdtPriRestri> findBdtPriRestriByBdtIdAndDefaultIsTrueMap;

    public BdtPriRestri findBdtPriRestriByBdtIdAndDefaultIsTrue(long bdtId) {
        return findBdtPriRestriByBdtIdAndDefaultIsTrueMap.get(bdtId);
    }

    private Map<Long, BdtPriRestri> findBdtPriRestriMap;

    public BdtPriRestri findBdtPriRestri(long bdtPriRestriId) {
        return findBdtPriRestriMap.get(bdtPriRestriId);
    }

    private Map<Long, CdtAwdPriXpsTypeMap> findCdtAwdPriXpsTypeMapMap;

    public CdtAwdPriXpsTypeMap findCdtAwdPriXpsTypeMap(long cdtAwdPriXpsTypeMapId) {
        return findCdtAwdPriXpsTypeMapMap.get(cdtAwdPriXpsTypeMapId);
    }

    private Map<Long, BdtScPriRestri> findBdtScPriRestriByBdtIdAndDefaultIsTrueMap;

    public BdtScPriRestri findBdtScPriRestriByBdtScIdAndDefaultIsTrue(long bdtScId) {
        return findBdtScPriRestriByBdtIdAndDefaultIsTrueMap.get(bdtScId);
    }

    private Map<Long, BdtScPriRestri> findBdtScPriRestriMap;

    public BdtScPriRestri findBdtScPriRestri(long bdtScPriRestriId) {
        return findBdtScPriRestriMap.get(bdtScPriRestriId);
    }

    private Map<Long, CdtScAwdPriXpsTypeMap> findCdtScAwdPriXpsTypeMapMap;

    public CdtScAwdPriXpsTypeMap findCdtScAwdPriXpsTypeMap(long cdtScAwdPriXpsTypeMapId) {
        return findCdtScAwdPriXpsTypeMapMap.get(cdtScAwdPriXpsTypeMapId);
    }

    private Map<Long, Xbt> findXbtMap;

    public Xbt findXbt(long xbtId) {
        return findXbtMap.get(xbtId);
    }

    private Map<Long, CodeList> findCodeListMap;

    public CodeList findCodeList(long codeListId) {
        return findCodeListMap.get(codeListId);
    }

    private Map<Long, List<CodeListValue>> findCodeListValueByCodeListIdAndUsedIndicatorIsTrueMap;

    public List<CodeListValue> findCodeListValueByCodeListIdAndUsedIndicatorIsTrue(long codeListId) {
        return findCodeListValueByCodeListIdAndUsedIndicatorIsTrueMap.containsKey(codeListId) ?
                findCodeListValueByCodeListIdAndUsedIndicatorIsTrueMap.get(codeListId) :
                Collections.emptyList();
    }

    private Map<Long, ACC> findACCMap;

    public ACC findACC(long accId) {
        return findACCMap.get(accId);
    }

    private Map<Long, BCC> findBCCMap;

    public BCC findBCC(long bccId) {
        return findBCCMap.get(bccId);
    }

    private Map<Long, BCCP> findBCCPMap;

    public BCCP findBCCP(long bccpId) {
        return findBCCPMap.get(bccpId);
    }

    private Map<Long, ASCC> findASCCMap;

    public ASCC findASCC(long asccId) {
        return findASCCMap.get(asccId);
    }

    private Map<Long, ASCCP> findASCCPMap;

    public ASCCP findASCCP(long asccpId) {
        return findASCCPMap.get(asccpId);
    }

    private Map<Long, DT> findDTMap;

    public DT findDT(long dtId) {
        return findDTMap.get(dtId);
    }

    private Map<Long, DTSC> findDtScMap;

    public DTSC findDtSc(long dtScId) {
        return findDtScMap.get(dtScId);
    }

    private Map<Long, AgencyIdList> findAgencyIdListMap;

    public AgencyIdList findAgencyIdList(long agencyIdListId) {
        return findAgencyIdListMap.get(agencyIdListId);
    }

    private Map<Long, AgencyIdListValue> findAgencyIdListValueMap;
    private Map<Long, List<AgencyIdListValue>> findAgencyIdListValueByOwnerListIdMap;

    public AgencyIdListValue findAgencyIdListValue(long agencyIdListValueId) {
        return findAgencyIdListValueMap.get(agencyIdListValueId);
    }

    public List<AgencyIdListValue> findAgencyIdListValueByOwnerListId(long ownerListId) {
        return findAgencyIdListValueByOwnerListIdMap.containsKey(ownerListId) ?
                findAgencyIdListValueByOwnerListIdMap.get(ownerListId) :
                Collections.emptyList();
    }

    private Map<Long, ABIE> findAbieMap;

    public ABIE findAbie(long abieId) {
        return findAbieMap.get(abieId);
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
        return findASBIEPMap.get(asbiepId);
    }

    private Map<Long, ASBIEP> findAsbiepByRoleOfAbieIdMap;

    public ASBIEP findAsbiepByRoleOfAbieId(long roleOfAbieId) {
        return findAsbiepByRoleOfAbieIdMap.get(roleOfAbieId);
    }

    private Map<Long, BBIEP> findBBIEPMap;

    public BBIEP findBBIEP(long bbiepId) {
        return findBBIEPMap.get(bbiepId);
    }

    private Map<Long, String> findUserNameMap;

    public String findUserName(long userId) {
        return findUserNameMap.get(userId);
    }

    private Map<Long, String> findReleaseNumberMap;

    public String findReleaseNumber(long releaseId) {
        return findReleaseNumberMap.get(releaseId);
    }

    public ACC queryBasedACC(ABIE abie) {
        long basedAccId = abie.getBasedAccId();
        return findACC(basedAccId);
    }

    // Get only Child BIEs whose is_used flag is true
    public List<BIE> queryChildBIEs(ABIE abie) {
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
        long bbieId = bbie.getBbieId();
        return findBbieScByBbieIdAndUsedIsTrue(bbieId);
    }

    public ASBIEP receiveASBIEP(ABIE abie) {
        return findAsbiepByRoleOfAbieId(abie.getAbieId());
    }

    public DT queryBDT(BBIE bbie) {
        BCC bcc = findBCC(bbie.getBasedBccId());
        BCCP bccp = findBCCP(bcc.getToBccpId());
        return queryBDT(bccp);
    }

    public DT queryBDT(BCCP bccp) {
        DT bdt = findDT(bccp.getBdtId());
        return bdt;
    }

    public ASCCP queryBasedASCCP(ASBIEP asbiep) {
        ASCCP asccp = findASCCP(asbiep.getBasedAsccpId());
        return asccp;
    }

    public ASCC queryBasedASCC(ASBIE asbie) {
        ASCC ascc = findASCC(asbie.getBasedAsccId());
        return ascc;
    }

    public ABIE queryTargetABIE(ASBIEP asbiep) {
        ABIE abie = findAbie(asbiep.getRoleOfAbieId());
        return abie;
    }

    public ACC queryTargetACC(ASBIEP asbiep) {
        ABIE abie = findAbie(asbiep.getRoleOfAbieId());

        ACC acc = findACC(abie.getBasedAccId());
        return acc;
    }

    public ABIE queryTargetABIE2(ASBIEP asbiep) {
        ABIE abie = findAbie(asbiep.getRoleOfAbieId());
        return abie;
    }

    public BCC queryBasedBCC(BBIE bbie) {
        BCC bcc = findBCC(bbie.getBasedBccId());
        return bcc;
    }

    public BCCP queryToBCCP(BCC bcc) {
        return findBCCP(bcc.getToBccpId());
    }

    public CodeList getCodeList(BBIESC bbieSc) {
        CodeList codeList = findCodeList(bbieSc.getCodeListId());
        if (codeList != null) {
            return codeList;
        }

        BdtScPriRestri bdtScPriRestri =
                findBdtScPriRestri(bbieSc.getDtScPriRestriId());
        if (bdtScPriRestri != null) {
            return findCodeList(bdtScPriRestri.getCodeListId());
        } else {
            DTSC gDTSC = findDtSc(bbieSc.getDtScId());
            BdtScPriRestri bBDTSCPrimitiveRestriction =
                    findBdtScPriRestriByBdtScIdAndDefaultIsTrue(gDTSC.getDtScId());
            if (bBDTSCPrimitiveRestriction != null) {
                codeList = findCodeList(bBDTSCPrimitiveRestriction.getCodeListId());
            }
        }

        return codeList;
    }

    public AgencyIdList getAgencyIdList(BBIE bbie) {
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
            bdtScPriRestri = findBdtScPriRestriByBdtScIdAndDefaultIsTrue(gDTSC.getDtScId());
            if (bdtScPriRestri != null) {
                agencyIdList = findAgencyIdList(bdtScPriRestri.getAgencyIdListId());
            }
        }
        return agencyIdList;
    }

    public List<CodeListValue> getCodeListValues(CodeList codeList) {
        return findCodeListValueByCodeListIdAndUsedIndicatorIsTrue(codeList.getCodeListId());
    }

    public ASBIEP queryAssocToASBIEP(ASBIE asbie) {
        ASBIEP asbiepVO = findASBIEP(asbie.getToAsbiepId());
        return asbiepVO;
    }

    public DT queryAssocBDT(BBIE bbie) {
        BCC bcc = findBCC(bbie.getBasedBccId());
        BCCP bccp = findBCCP(bcc.getToBccpId());
        return queryBDT(bccp);
    }

    public BusinessContext findBusinessContext(TopLevelAbie topLevelAbie) {
        long bizCtxId = abieRepository.findById(topLevelAbie.getAbieId()).getBizCtxId();
        return businessContextRepository.findById(bizCtxId);
    }

    public List<ContextSchemeValue> findContextSchemeValue(BusinessContext businessContext) {
        List<BusinessContextValue> businessContextValues =
                businessContextValueRepository.findByBizCtxId(businessContext.getBizCtxId());

        return businessContextValues.stream()
                .map(e -> contextSchemeValueRepository.findById(e.getCtxSchemeValueId()))
                .collect(Collectors.toList());
    }

    private Map<Long, ContextScheme> findContextSchemeMap;

    public ContextScheme findContextScheme(long ctxSchemeId) {
        return findContextSchemeMap.get(ctxSchemeId);
    }

    private Map<Long, ContextCategory> findContextCategoryMap;

    public ContextCategory findContextCategory(long ctxCategoryId) {
        return findContextCategoryMap.get(ctxCategoryId);
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
