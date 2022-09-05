package org.oagi.score.gateway.http.api.bie_management.service.generate_expression;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.oagi.score.data.*;
import org.oagi.score.repo.component.asbiep.AsbiepReadRepository;
import org.oagi.score.repo.component.release.ReleaseRepository;
import org.oagi.score.repo.component.top_level_asbiep.TopLevelAsbiepReadRepository;
import org.oagi.score.repository.*;
import org.oagi.score.service.common.data.OagisComponentType;
import org.oagi.score.service.corecomponent.seqkey.SeqKeyHandler;
import org.oagi.score.service.corecomponent.seqkey.SeqKeySupportable;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class GenerationContext implements InitializingBean {

    private final List<TopLevelAsbiep> topLevelAsbieps;

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
    private AsbiepReadRepository asbiepReadRepository;

    @Autowired
    private BBIEPRepository bbiepRepository;

    @Autowired
    private ASBIERepository asbieRepository;

    @Autowired
    private BBIERepository bbieRepository;

    @Autowired
    private BBIESCRepository bbieScRepository;

    @Autowired
    private ASBIEBiztermRepository ASBIEBiztermRepository;

    @Autowired
    private BizCtxRepository bizCtxRepository;

    @Autowired
    private BizCtxValueRepository bizCtxValueRepository;

    @Autowired
    private CtxSchemeRepository ctxSchemeRepository;

    @Autowired
    private CtxSchemeValueRepository ctxSchemeValueRepository;

    @Autowired
    private CtxCategoryRepository ctxCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopLevelAsbiepReadRepository topLevelAsbiepReadRepository;

    @Autowired
    private ASBIEBiztermRepository asbieBiztermRepository;

    @Autowired
    private BBIEBiztermRepository bbieBiztermRepository;

    @Autowired
    private ReleaseRepository releaseRepository;
    // Prepared Datas
    private Map<String, BdtPriRestri> findBdtPriRestriByBdtIdAndDefaultIsTrueMap;
    private Map<String, BdtPriRestri> findBdtPriRestriMap;
    private Map<String, CdtAwdPriXpsTypeMap> findCdtAwdPriXpsTypeMapMap;
    private Map<String, BdtScPriRestri> findBdtScPriRestriByBdtIdAndDefaultIsTrueMap;
    private Map<String, BdtScPriRestri> findBdtScPriRestriMap;
    private Map<String, CdtScAwdPriXpsTypeMap> findCdtScAwdPriXpsTypeMapMap;
    private Map<String, Xbt> findXbtMap;
    private Map<String, CodeList> findCodeListMap;
    private Map<String, List<CodeListValue>> findCodeListValueByCodeListIdAndUsedIndicatorIsTrueMap;
    private Map<String, ACC> findACCMap;
    private Map<String, BCC> findBCCMap;
    private Map<String, BCCP> findBCCPMap;
    private Map<String, List<BCC>> findBCCByFromAccIdMap;
    private Map<String, BCCP> findBccpByBccpIdMap;
    private Map<String, ASCC> findASCCMap;
    private Map<String, List<ASCC>> findASCCByFromAccIdMap;
    private Map<String, ASCCP> findASCCPMap;
    private Map<String, DT> findDTMap;
    private Map<String, DTSC> findDtScMap;
    private Map<String, AgencyIdList> findAgencyIdListMap;
    private Map<String, AgencyIdListValue> findAgencyIdListValueMap;
    private Map<String, List<AgencyIdListValue>> findAgencyIdListValueByOwnerListIdMap;
    private Map<String, ABIE> findAbieMap;
    private Map<String, List<BBIE>> findBbieByFromAbieIdAndUsedIsTrueMap;
    private Map<String, List<BBIESC>>
            findBbieScByBbieIdAndUsedIsTrueMap;
    private Map<String, List<ASBIE>> findAsbieByFromAbieIdMap;
    private Map<String, ASBIEP> findASBIEPMap;
    private Map<String, ASBIEP> findAsbiepByRoleOfAbieIdMap;
    private Map<String, BBIEP> findBBIEPMap;
    private Map<String, String> findUserNameMap;
    private Map<String, List<AsbieBizTerm>> findAsbieBizTermByAsbieIdMap;
    private Map<String, List<BbieBizTerm>> findBbieBizTermByBbieIdMap;

    private Map<String, Release> findReleaseMap;
    private Map<String, ContextScheme> findContextSchemeMap;
    private Map<String, ContextCategory> findContextCategoryMap;

    @Data
    @AllArgsConstructor
    public static class SeqKey implements SeqKeySupportable {
        private String key;
        private String state;
        private BigInteger seqKeyId;
        private BigInteger prevSeqKeyId;
        private BigInteger nextSeqKeyId;
        private String toAsccpManifestId;
    }

    public GenerationContext(TopLevelAsbiep topLevelAsbiep) {
        this(Arrays.asList(topLevelAsbiep));
    }

    public GenerationContext(List<TopLevelAsbiep> topLevelAsbieps) {
        this.topLevelAsbieps = topLevelAsbieps;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (topLevelAsbieps == null) {
            throw new IllegalStateException("'topLevelAsbieps' parameter must not be null.");
        }

        Set<String> releaseIdSet = topLevelAsbieps.stream().map(e -> e.getReleaseId()).collect(Collectors.toSet());
        if (releaseIdSet.size() != 1) {
            throw new UnsupportedOperationException("`releaseId` for all `topLevelAsbieps` parameter must be same.");
        }

        Set<TopLevelAsbiep> topLevelAsbiepSet = new HashSet();
        topLevelAsbiepSet.addAll(topLevelAsbieps);
        topLevelAsbiepSet.addAll(findRefTopLevelAsbieps(topLevelAsbiepSet));

        init(topLevelAsbiepSet.stream().map(e -> e.getTopLevelAsbiepId()).collect(Collectors.toList()), releaseIdSet.iterator().next());
    }

    private Set<TopLevelAsbiep> findRefTopLevelAsbieps(Set<TopLevelAsbiep> topLevelAsbiepSet) {
        Set<TopLevelAsbiep> refTopLevelAsbiepSet = new HashSet();
        refTopLevelAsbiepSet.addAll(
                topLevelAsbiepReadRepository.findRefTopLevelAsbieps(
                        topLevelAsbiepSet.stream().map(e -> e.getTopLevelAsbiepId()).collect(Collectors.toSet())
                )
        );

        if (!refTopLevelAsbiepSet.isEmpty()) {
            refTopLevelAsbiepSet.addAll(findRefTopLevelAsbieps(refTopLevelAsbiepSet));
        }

        return refTopLevelAsbiepSet;
    }

    private void init(Collection<String> topLevelAsbiepIds, String releaseId) {
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
                .filter(e -> e.getReleaseId().equals(releaseId))
                .collect(Collectors.toMap(e -> e.getXbtId(), Function.identity()));

        List<CodeList> codeLists = codeListRepository.findAll();
        findCodeListMap = codeLists.stream()
                .collect(Collectors.toMap(e -> e.getCodeListId(), Function.identity()));

        List<CodeListValue> codeListValues = codeListValueRepository.findAll();
        findCodeListValueByCodeListIdAndUsedIndicatorIsTrueMap = codeListValues.stream()
                .filter(e -> e.isUsedIndicator())
                .collect(Collectors.groupingBy(e -> e.getCodeListId()));

        List<ACC> accList = accRepository.findAllByReleaseId(releaseId);
        findACCMap = accList.stream()
                .collect(Collectors.toMap(e -> e.getAccManifestId(), Function.identity()));

        List<BCC> bccList = bccRepository.findAllByReleaseId(releaseId);
        findBCCMap = bccList.stream()
                .collect(Collectors.toMap(e -> e.getBccManifestId(), Function.identity()));

        List<BCCP> bccpList = bccpRepository.findAllByReleaseId(releaseId);
        findBCCPMap = bccpList.stream()
                .collect(Collectors.toMap(e -> e.getBccpManifestId(), Function.identity()));
        findBccpByBccpIdMap = bccpList.stream()
                .collect(Collectors.toMap(e -> e.getBccpId(), Function.identity()));

        List<ASCC> asccList = asccRepository.findAllByReleaseId(releaseId);
        findASCCMap = asccList.stream()
                .collect(Collectors.toMap(e -> e.getAsccManifestId(), Function.identity()));

        findASCCByFromAccIdMap = asccList.stream().collect(Collectors.groupingBy(e -> e.getFromAccManifestId()));
        findBCCByFromAccIdMap = bccList.stream().collect(Collectors.groupingBy(e -> e.getFromAccManifestId()));

        List<ASCCP> asccpList = asccpRepository.findAllByReleaseId(releaseId);
        findASCCPMap = asccpList.stream()
                .collect(Collectors.toMap(e -> e.getAsccpManifestId(), Function.identity()));

        List<DT> dataTypeList = dataTypeRepository.findAll();
        findDTMap = dataTypeList.stream()
                .filter(e -> e.getReleaseId().equals(releaseId))
                .collect(Collectors.toMap(e -> e.getDtId(), Function.identity()));

        List<DTSC> dtScList = dtScRepository.findAllByReleaseId(releaseId);
        findDtScMap = dtScList.stream()
                .collect(Collectors.toMap(e -> e.getDtScManifestId(), Function.identity()));

        List<AgencyIdList> agencyIdLists = agencyIdListRepository.findAll();
        findAgencyIdListMap = agencyIdLists.stream()
                .collect(Collectors.toMap(e -> e.getAgencyIdListId(), Function.identity()));

        List<AgencyIdListValue> agencyIdListValues = agencyIdListValueRepository.findAll();
        findAgencyIdListValueMap = agencyIdListValues.stream()
                .collect(Collectors.toMap(e -> e.getAgencyIdListValueId(), Function.identity()));
        findAgencyIdListValueByOwnerListIdMap = agencyIdListValues.stream()
                .collect(Collectors.groupingBy(e -> e.getOwnerListId()));

        List<AsbieBizTerm> asbieBizTermList = asbieBiztermRepository.findAll();
        findAsbieBizTermByAsbieIdMap = asbieBizTermList
                .stream().collect(Collectors.groupingBy(e -> e.getAsbieId()));
        List<BbieBizTerm> bbieBizTermList = bbieBiztermRepository.findAll();
        findBbieBizTermByBbieIdMap = bbieBizTermList
                .stream().collect(Collectors.groupingBy(e -> e.getBbieId()));

        List<ABIE> abieList = abieRepository.findByOwnerTopLevelAsbiepIds(topLevelAsbiepIds);
        findAbieMap = abieList.stream()
                .collect(Collectors.toMap(e -> e.getAbieId(), Function.identity()));

        List<BBIE> bbieList =
                bbieRepository.findByOwnerTopLevelAsbiepIdsAndUsed(topLevelAsbiepIds, true);
        findBbieByFromAbieIdAndUsedIsTrueMap = bbieList.stream()
                .filter(e -> e.isUsed())
                .collect(Collectors.groupingBy(e -> e.getFromAbieId()));

        List<BBIESC> bbieScList =
                bbieScRepository.findByOwnerTopLevelAsbiepIdsAndUsed(topLevelAsbiepIds, true);
        findBbieScByBbieIdAndUsedIsTrueMap = bbieScList.stream()
                .filter(e -> e.isUsed())
                .collect(Collectors.groupingBy(e -> e.getBbieId()));

        List<ASBIE> asbieList =
                asbieRepository.findByOwnerTopLevelAsbiepIds(topLevelAsbiepIds);
        findAsbieByFromAbieIdMap = asbieList.stream()
                .collect(Collectors.groupingBy(e -> e.getFromAbieId()));

        List<ASBIEP> asbiepList =
                asbiepRepository.findByOwnerTopLevelAsbiepIds(topLevelAsbiepIds);
        findASBIEPMap = asbiepList.stream()
                .collect(Collectors.toMap(e -> e.getAsbiepId(), Function.identity()));
        findAsbiepByRoleOfAbieIdMap = asbiepList.stream()
                .collect(Collectors.toMap(e -> e.getRoleOfAbieId(), Function.identity()));

        List<BBIEP> bbiepList =
                bbiepRepository.findByOwnerTopLevelAsbiepIds(topLevelAsbiepIds);
        findBBIEPMap = bbiepList.stream()
                .collect(Collectors.toMap(e -> e.getBbiepId(), Function.identity()));

        findUserNameMap = userRepository.getUsernameMap();
        findReleaseMap = releaseRepository.findAll().stream()
                .collect(Collectors.toMap(e -> e.getReleaseId(), Function.identity()));

        findContextSchemeMap = ctxSchemeRepository.findAll().stream()
                .collect(Collectors.toMap(e -> e.getCtxSchemeId(), Function.identity()));
        findContextCategoryMap = ctxCategoryRepository.findAll().stream()
                .collect(Collectors.toMap(e -> e.getCtxCategoryId(), Function.identity()));
    }

    public BdtPriRestri findBdtPriRestriByBdtIdAndDefaultIsTrue(String bdtId) {
        return StringUtils.hasLength(bdtId) ? findBdtPriRestriByBdtIdAndDefaultIsTrueMap.get(bdtId) : null;
    }

    public BdtPriRestri findBdtPriRestri(String bdtPriRestriId) {
        return StringUtils.hasLength(bdtPriRestriId) ? findBdtPriRestriMap.get(bdtPriRestriId) : null;
    }

    public CdtAwdPriXpsTypeMap findCdtAwdPriXpsTypeMap(String cdtAwdPriXpsTypeMapId) {
        return (StringUtils.hasLength(cdtAwdPriXpsTypeMapId)) ?
                findCdtAwdPriXpsTypeMapMap.get(cdtAwdPriXpsTypeMapId) : null;
    }

    public BdtScPriRestri findBdtScPriRestriByBdtScIdAndDefaultIsTrue(String bdtScId) {
        return StringUtils.hasLength(bdtScId) ? findBdtScPriRestriByBdtIdAndDefaultIsTrueMap.get(bdtScId) : null;
    }

    public BdtScPriRestri findBdtScPriRestri(String bdtScPriRestriId) {
        return StringUtils.hasLength(bdtScPriRestriId) ? findBdtScPriRestriMap.get(bdtScPriRestriId) : null;
    }

    public CdtScAwdPriXpsTypeMap findCdtScAwdPriXpsTypeMap(String cdtScAwdPriXpsTypeMapId) {
        return (StringUtils.hasLength(cdtScAwdPriXpsTypeMapId)) ?
                findCdtScAwdPriXpsTypeMapMap.get(cdtScAwdPriXpsTypeMapId) : null;
    }

    public Xbt findXbt(String xbtId) {
        return (StringUtils.hasLength(xbtId)) ? findXbtMap.get(xbtId) : null;
    }

    public CodeList findCodeList(String codeListId) {
        return StringUtils.hasLength(codeListId) ? findCodeListMap.get(codeListId) : null;
    }

    public List<CodeListValue> findCodeListValueByCodeListIdAndUsedIndicatorIsTrue(String codeListId) {
        return findCodeListValueByCodeListIdAndUsedIndicatorIsTrueMap.containsKey(codeListId) ?
                findCodeListValueByCodeListIdAndUsedIndicatorIsTrueMap.get(codeListId) :
                Collections.emptyList();
    }

    public ACC findACC(String accManifestId) {
        return (accManifestId != null && StringUtils.hasLength(accManifestId)) ? findACCMap.get(accManifestId) : null;
    }

    public BCC findBCC(String bccManifestId) {
        return (bccManifestId != null && StringUtils.hasLength(bccManifestId)) ? findBCCMap.get(bccManifestId) : null;
    }

    public BCCP findBCCP(String bccpManifestId) {
        return (bccpManifestId != null && StringUtils.hasLength(bccpManifestId)) ? findBCCPMap.get(bccpManifestId) : null;
    }

    public ASCC findASCC(String asccManifestId) {
        return (asccManifestId != null && StringUtils.hasLength(asccManifestId)) ? findASCCMap.get(asccManifestId) : null;
    }

    public ASCCP findASCCP(String asccpManifestId) {
        return (asccpManifestId != null && StringUtils.hasLength(asccpManifestId)) ? findASCCPMap.get(asccpManifestId) : null;
    }

    public DT findDT(String dtId) {
        return StringUtils.hasLength(dtId) ? findDTMap.get(dtId) : null;
    }

    public DTSC findDtSc(String dtScManifestId) {
        return (dtScManifestId != null && StringUtils.hasLength(dtScManifestId)) ? findDtScMap.get(dtScManifestId) : null;
    }

    public AgencyIdList findAgencyIdList(String agencyIdListId) {
        return StringUtils.hasLength(agencyIdListId) ? findAgencyIdListMap.get(agencyIdListId) : null;
    }

    public AgencyIdListValue findAgencyIdListValue(String agencyIdListValueId) {
        return StringUtils.hasLength(agencyIdListValueId) ? findAgencyIdListValueMap.get(agencyIdListValueId) : null;
    }

    public List<AgencyIdListValue> findAgencyIdListValueByOwnerListId(String ownerListId) {
        return findAgencyIdListValueByOwnerListIdMap.containsKey(ownerListId) ?
                findAgencyIdListValueByOwnerListIdMap.get(ownerListId) :
                Collections.emptyList();
    }

    public ABIE findAbie(String abieId) {
        return StringUtils.hasLength(abieId) ? findAbieMap.get(abieId) : null;
    }

    public List<BBIE> findBbieByFromAbieIdAndUsedIsTrue(String fromAbieId) {
        return findBbieByFromAbieIdAndUsedIsTrueMap.containsKey(fromAbieId) ?
                findBbieByFromAbieIdAndUsedIsTrueMap.get(fromAbieId) :
                Collections.emptyList();
    }

    public List<BBIESC> findBbieScByBbieIdAndUsedIsTrue(String bbieId) {
        return findBbieScByBbieIdAndUsedIsTrueMap.containsKey(bbieId) ?
                findBbieScByBbieIdAndUsedIsTrueMap.get(bbieId) :
                Collections.emptyList();
    }

    public List<ASBIE> findAsbieByFromAbieId(String fromAbieId) {
        return findAsbieByFromAbieIdMap.containsKey(fromAbieId) ?
                findAsbieByFromAbieIdMap.get(fromAbieId) :
                Collections.emptyList();
    }

    public ASBIEP findASBIEP(String asbiepId) {
        return StringUtils.hasLength(asbiepId) ? findASBIEPMap.get(asbiepId) : null;
    }

    public ASBIEP findAsbiepByRoleOfAbieId(String roleOfAbieId) {
        return StringUtils.hasLength(roleOfAbieId) ? findAsbiepByRoleOfAbieIdMap.get(roleOfAbieId) : null;
    }

    public BBIEP findBBIEP(String bbiepId) {
        return StringUtils.hasLength(bbiepId) ? findBBIEPMap.get(bbiepId) : null;
    }

    public List<AsbieBizTerm> findAsbieBizTerm(String asbieId) {
        return StringUtils.hasLength(asbieId) ? findAsbieBizTermByAsbieIdMap.get(asbieId) : Collections.emptyList();
    }

    public List<BbieBizTerm> findBbieBizTerm(String bbieId) {
        return StringUtils.hasLength(bbieId) ? findBbieBizTermByBbieIdMap.get(bbieId) : Collections.emptyList();
    }

    public String findUserName(String userId) {
        return StringUtils.hasLength(userId) ? findUserNameMap.get(userId) : null;
    }

    public Release findRelease(String releaseId) {
        return StringUtils.hasLength(releaseId) ? findReleaseMap.get(releaseId) : null;
    }

    public String findReleaseNumber(String releaseId) {
        Release release = findRelease(releaseId);
        return (release != null) ? release.getReleaseNum() : null;
    }

    public ACC queryBasedACC(ABIE abie) {
        return (abie != null) ? findACC(abie.getBasedAccManifestId()) : null;
    }

    private List<SeqKey> findChildren(String accManifestId) {
        ACC acc = findACCMap.get(accManifestId);
        List<SeqKey> sorted = new ArrayList();
        if (acc == null) {
            throw new IllegalArgumentException();
        }
        if (acc.getBasedAccManifestId() != null) {
            sorted.addAll(findChildren(acc.getBasedAccManifestId()));
        }

        List<SeqKeySupportable> asso = new ArrayList();

        List<ASCC> asccs = findASCCByFromAccIdMap.get(acc.getAccManifestId());
        if (asccs != null) {
            asccs.forEach(e -> asso.add(
                    new SeqKey("ASCC-" + e.getAsccManifestId(), e.getState().name(), e.getSeqKeyId(),
                            e.getPrevSeqKeyId(), e.getNextSeqKeyId(), e.getToAsccpManifestId())));
        }

        List<BCC> bccs = findBCCByFromAccIdMap.get(acc.getAccManifestId());
        if (bccs != null) {
            bccs.forEach(e -> asso.add(
                    new SeqKey("BCC-" + e.getBccManifestId(), e.getState().name(), e.getSeqKeyId(),
                            e.getPrevSeqKeyId(), e.getNextSeqKeyId(), null)
            ));
        }

        SeqKeyHandler.sort(asso).forEach(e -> sorted.add((SeqKey) e));

        return sorted;
    }

    private List<SeqKey> loadGroupAssociations(List<SeqKey> seqKeyList) {
        List<SeqKey> assocs = new ArrayList();
        seqKeyList.forEach(seqKey -> {
            List<SeqKey> groupAsso = getGroupAssociations(seqKey);
            if (groupAsso.size() > 1) {
                assocs.addAll(loadGroupAssociations(groupAsso));
            } else {
                assocs.addAll(groupAsso);
            }
        });
        return assocs;
    }

    private List<SeqKey> getGroupAssociations(SeqKey seqKey) {
        ASCCP asccp = findASCCP(seqKey.getToAsccpManifestId());
        if (asccp != null) {
            ACC acc = findACC(asccp.getRoleOfAccManifestId());
            if (OagisComponentType.valueOf(acc.getOagisComponentType()).isGroup()) {
                return findChildren(acc.getAccManifestId());
            }
        }
        return Collections.singletonList(seqKey);
    }

    private List<BIE> sorted(ABIE abie, List<ASBIE> asbieList, List<BBIE> bbieList) {

        List<BIE> sorted = new ArrayList();
        List<SeqKey> assocs = findChildren(abie.getBasedAccManifestId());

        Map<String, BIE> bieMap = asbieList.stream().collect(Collectors.toMap(e -> "ASCC-" + e.getBasedAsccManifestId(), Function.identity()));
        bieMap.putAll(bbieList.stream().collect(Collectors.toMap(e -> "BCC-" + e.getBasedBccManifestId(), Function.identity())));

        loadGroupAssociations(assocs).forEach(e -> {
            BIE bie = bieMap.get(e.key);
            if (bie != null) {
                sorted.add(bie);
            }
        });
        return sorted;
    }

    // Get only Child BIEs whose is_used flag is true
    public List<BIE> queryChildBIEs(ABIE abie) {
        if (abie == null) {
            return Collections.emptyList();
        }

        List<ASBIE> asbieList = findAsbieByFromAbieId(abie.getAbieId());
        List<BBIE> bbieList = findBbieByFromAbieIdAndUsedIsTrue(abie.getAbieId());

        List<BIE> result = new ArrayList();
        for (BIE bie : sorted(abie, asbieList, bbieList)) {
            if (bie instanceof BBIE) {
                BBIE bbie = (BBIE) bie;
                if (bbie.getCardinalityMax() != 0) {
                    result.add(bbie);
                }
            } else {
                ASBIE asbie = (ASBIE) bie;
                ASBIEP toAsbiep = findASBIEP(asbie.getToAsbiepId());
                ABIE roleOfAbie = findAbie(toAsbiep.getRoleOfAbieId());
                ACC roleOfAcc = findACC(roleOfAbie.getBasedAccManifestId());

                OagisComponentType oagisComponentType = OagisComponentType.valueOf(roleOfAcc.getOagisComponentType());
                if (oagisComponentType.isGroup()) {
                    result.addAll(queryChildBIEs(roleOfAbie));
                } else if (asbie.isUsed() && asbie.getCardinalityMax() != 0) {
                    result.add(asbie);
                }
            }
        }

        return result;
    }

    // Get only SCs whose is_used is true.
    public List<BBIESC> queryBBIESCs(BBIE bbie) {
        return (bbie != null) ? findBbieScByBbieIdAndUsedIsTrue(bbie.getBbieId()) : Collections.emptyList();
    }

    public ASBIEP receiveASBIEP(ABIE abie) {
        return (abie != null) ? findAsbiepByRoleOfAbieId(abie.getAbieId()) : null;
    }

    public DT queryBDT(BBIE bbie) {
        BCC bcc = (bbie != null) ? findBCC(bbie.getBasedBccManifestId()) : null;
        BCCP bccp = (bcc != null) ? queryToBCCP(bcc) : null;
        return (bccp != null) ? queryBDT(bccp) : null;
    }

    public DT queryBDT(BCCP bccp) {
        return (bccp != null) ? findDT(bccp.getBdtId()) : null;
    }

    public ASCCP queryBasedASCCP(ASBIEP asbiep) {
        return (asbiep != null) ? findASCCP(asbiep.getBasedAsccpManifestId()) : null;
    }

    public ASCC queryBasedASCC(ASBIE asbie) {
        return (asbie != null) ? findASCC(asbie.getBasedAsccManifestId()) : null;
    }

    public ABIE queryTargetABIE(ASBIEP asbiep) {
        return (asbiep != null) ? findAbie(asbiep.getRoleOfAbieId()) : null;
    }

    public ACC queryTargetACC(ASBIEP asbiep) {
        ABIE abie = (asbiep != null) ? findAbie(asbiep.getRoleOfAbieId()) : null;
        return (abie != null) ? findACC(abie.getBasedAccManifestId()) : null;
    }

    public ABIE queryTargetABIE2(ASBIEP asbiep) {
        return (asbiep != null) ? findAbie(asbiep.getRoleOfAbieId()) : null;
    }

    public BCC queryBasedBCC(BBIE bbie) {
        return (bbie != null) ? findBCC(bbie.getBasedBccManifestId()) : null;
    }

    public BCCP queryToBCCP(BCC bcc) {
        return (bcc != null) ? findBccpByBccpIdMap.get(bcc.getToBccpId()) : null;
    }

    public List<AsbieBizTerm> queryAsbieBizterm(ASBIE asbie) {
        return (asbie != null) ? findAsbieBizTerm(asbie.getAsbieId()) : Collections.emptyList();
    }

    public List<BbieBizTerm> queryBbieBizterm(BBIE bbie) {
        return (bbie != null) ? findBbieBizTerm(bbie.getBbieId()) : Collections.emptyList();
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
            DTSC gDTSC = findDtSc(bbieSc.getBasedDtScManifestId());
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
            DTSC gDTSC = findDtSc(bbieSc.getBasedDtScManifestId());
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
        BCC bcc = (bbie != null) ? findBCC(bbie.getBasedBccManifestId()) : null;
        BCCP bccp = (bcc != null) ? queryToBCCP(bcc) : null;
        return queryBDT(bccp);
    }

    public BizCtx findBusinessContext(TopLevelAsbiep topLevelAsbiep) {
        String bizCtxId = (topLevelAsbiep != null) ? bizCtxRepository.findByTopLevelAsbiep(topLevelAsbiep).get(0).getBizCtxId() : null;
        //return the first one of the list
        return bizCtxRepository.findById(bizCtxId);
    }

    public List<BizCtx> findBusinessContexts(TopLevelAsbiep topLevelAsbiep) {
        return bizCtxRepository.findByTopLevelAsbiep(topLevelAsbiep);
    }

    public List<ContextSchemeValue> findContextSchemeValue(BizCtx businessContext) {
        List<BusinessContextValue> businessContextValues = (businessContext != null) ?
                bizCtxValueRepository.findByBizCtxId(businessContext.getBizCtxId()) :
                Collections.emptyList();

        return businessContextValues.stream()
                .map(e -> ctxSchemeValueRepository.findById(e.getCtxSchemeValueId()))
                .collect(Collectors.toList());
    }

    public ContextScheme findContextScheme(String ctxSchemeId) {
        return StringUtils.hasLength(ctxSchemeId) ? findContextSchemeMap.get(ctxSchemeId) : null;
    }

    public ContextCategory findContextCategory(String ctxCategoryId) {
        return StringUtils.hasLength(ctxCategoryId) ? findContextCategoryMap.get(ctxCategoryId) : null;
    }

    public AgencyIdList findAgencyIdList(ContextScheme contextScheme) {
        String schemeAgencyId = contextScheme.getSchemeAgencyId();
        if (!StringUtils.hasLength(schemeAgencyId)) {
            return null;
        }

        for (AgencyIdList agencyIdList : findAgencyIdListMap.values()) {
            if (schemeAgencyId.equals(agencyIdList.getListId())) {
                return agencyIdList;
            }
        }

        return null;
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

}
