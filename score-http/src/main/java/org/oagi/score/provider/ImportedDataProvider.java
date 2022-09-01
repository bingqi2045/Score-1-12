package org.oagi.score.provider;

import org.jooq.types.ULong;
import org.oagi.score.export.model.BlobContent;
import org.oagi.score.export.model.ModuleCCID;
import org.oagi.score.export.model.ModuleXbtID;
import org.oagi.score.repo.api.impl.jooq.entity.tables.DtManifest;
import org.oagi.score.repository.CcRepository;
import org.oagi.score.repo.api.impl.jooq.entity.tables.BccManifest;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ImportedDataProvider {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private CcRepository ccRepository;
    private String moduleSetReleaseId;

    public ImportedDataProvider(CcRepository ccRepository, String moduleSetReleaseId) {
        this.ccRepository = ccRepository;
        this.moduleSetReleaseId = moduleSetReleaseId;
        this.init();
    }

    public void init() {
        long s = System.currentTimeMillis();

        findAgencyIdListList = ccRepository.findAllAgencyIdList(moduleSetReleaseId);
        findModuleAgencyIdListManifestMap = ccRepository.findAllModuleAgencyIdListManifest(moduleSetReleaseId)
                .stream().collect(Collectors.toMap(ModuleCCID::getCcId, Function.identity()));
        findAgencyIdListMap = findAgencyIdListList.stream()
                .collect(Collectors.toMap(AgencyIdListRecord::getAgencyIdListId, Function.identity()));

        findAgencyIdListValueByOwnerListIdMap = ccRepository.findAllAgencyIdListValue(moduleSetReleaseId).stream()
                .collect(Collectors.groupingBy(AgencyIdListValueRecord::getOwnerListId));

        findCodeListList = ccRepository.findAllCodeList(moduleSetReleaseId);
        findCodeListMap = findCodeListList.stream()
                .collect(Collectors.toMap(CodeListRecord::getCodeListId, Function.identity()));

        findCodeListValueByCodeListIdMap = ccRepository.findAllCodeListValue(moduleSetReleaseId).stream()
                .collect(Collectors.groupingBy(CodeListValueRecord::getCodeListId));

        findDtManifestList = ccRepository.findAllDtManifest(moduleSetReleaseId);
        findDtManifestMap = findDtManifestList.stream()
                .collect(Collectors.toMap(DtManifestRecord::getDtManifestId, Function.identity()));

        findDtList = ccRepository.findAllDt(moduleSetReleaseId);
        findDtMap = findDtList.stream()
                .collect(Collectors.toMap(DtRecord::getDtId, Function.identity()));

        findDtScByOwnerDtIdMap = ccRepository.findAllDtSc(moduleSetReleaseId).stream()
                .collect(Collectors.groupingBy(DtScRecord::getOwnerDtId));

        List<BdtPriRestriRecord> bdtPriRestriList = ccRepository.findAllBdtPriRestri(moduleSetReleaseId);
        findBdtPriRestriListByDtIdMap = bdtPriRestriList.stream()
                .collect(Collectors.groupingBy(BdtPriRestriRecord::getBdtId));

        cdtAwdPriXpsTypeMapMap = ccRepository.findAllCdtAwdPriXpsTypeMap(moduleSetReleaseId).stream()
                .collect(Collectors.toMap(CdtAwdPriXpsTypeMapRecord::getCdtAwdPriXpsTypeMapId, Function.identity()));

        findBdtScPriRestriListByDtScIdMap = ccRepository.findAllBdtScPriRestri(moduleSetReleaseId).stream()
                .collect(Collectors.groupingBy(BdtScPriRestriRecord::getBdtScId));

        findCdtScAwdPriXpsTypeMapMap = ccRepository.findAllCdtScAwdPriXpsTypeMap(moduleSetReleaseId).stream()
                .collect(Collectors.toMap(CdtScAwdPriXpsTypeMapRecord::getCdtScAwdPriXpsTypeMapId, Function.identity()));

        findCdtScAwdPriMap = ccRepository.findAllCdtScAwdPri(moduleSetReleaseId).stream()
                .collect(Collectors.toMap(CdtScAwdPriRecord::getCdtScAwdPriId, Function.identity()));

        List<CdtAwdPriRecord> cdtAwdPriList = ccRepository.findAllCdtAwdPri(moduleSetReleaseId);
        findCdtAwdPriMap = cdtAwdPriList.stream()
                .collect(Collectors.toMap(CdtAwdPriRecord::getCdtAwdPriId, Function.identity()));

        List<CdtPriRecord> cdtPriList = ccRepository.findAllCdtPri();
        findCdtPriMap = cdtPriList.stream()
                .collect(Collectors.toMap(CdtPriRecord::getCdtPriId, Function.identity()));

        findXbtList = ccRepository.findAllXbt(moduleSetReleaseId);
        findXbtMap = findXbtList.stream()
                .collect(Collectors.toMap(XbtRecord::getXbtId, Function.identity()));

        findACCList = ccRepository.findAllAcc(moduleSetReleaseId);
        findAccMap = findACCList.stream()
                .collect(Collectors.toMap(AccRecord::getAccId, Function.identity()));

        findACCManifestList = ccRepository.findAllAccManifest(moduleSetReleaseId);
        findAccManifestMap = findACCManifestList.stream()
                .collect(Collectors.toMap(AccManifestRecord::getAccManifestId, Function.identity()));

        findASCCPList = ccRepository.findAllAsccp(moduleSetReleaseId);
        findAsccpMap = findASCCPList.stream()
                .collect(Collectors.toMap(AsccpRecord::getAsccpId, Function.identity()));
        findAsccpByGuidMap = findASCCPList.stream()
                .collect(Collectors.toMap(AsccpRecord::getGuid, Function.identity()));

        findASCCPManifestList = ccRepository.findAllAsccpManifest(moduleSetReleaseId);
        findAsccpManifestMap = findASCCPManifestList.stream()
                .collect(Collectors.toMap(AsccpManifestRecord::getAsccpManifestId, Function.identity()));

        findBCCPManifestList = ccRepository.findAllBccpManifest(moduleSetReleaseId);
        findBccpManifestMap = findBCCPManifestList.stream()
                .collect(Collectors.toMap(BccpManifestRecord::getBccpManifestId, Function.identity()));
        
        findBCCPList = ccRepository.findAllBccp(moduleSetReleaseId);
        findBccpMap = findBCCPList.stream()
                .collect(Collectors.toMap(BccpRecord::getBccpId, Function.identity()));

        findACCManifestList = ccRepository.findAllAccManifest(moduleSetReleaseId);
        findAccManifestMap = findACCManifestList.stream()
                .collect(Collectors.toMap(AccManifestRecord::getAccManifestId, Function.identity()));

        List<BccRecord> bccList = ccRepository.findAllBcc(moduleSetReleaseId);

        findBCCByToBccpIdMap = bccList.stream()
                .collect(Collectors.groupingBy(BccRecord::getToBccpId));
        findBccByFromAccIdMap = bccList.stream()
                .collect(Collectors.groupingBy(BccRecord::getFromAccId));
        findBccMap = bccList.stream()
                .collect(Collectors.toMap(BccRecord::getBccId, Function.identity()));

        findBccManifestMap = ccRepository.findAllBccManifest(moduleSetReleaseId).stream()
                .collect(Collectors.toMap(BccManifestRecord::getBccManifestId, Function.identity()));
        findBccManifestByAccManifestIdMap = findBccManifestMap.values().stream()
                .collect(Collectors.groupingBy(BccManifestRecord::getFromAccManifestId));

        findAsccManifestMap = ccRepository.findAllAsccManifest(moduleSetReleaseId).stream()
                .collect(Collectors.toMap(AsccManifestRecord::getAsccManifestId, Function.identity()));
        findAsccManifestByAccManifestIdMap = findAsccManifestMap.values().stream()
                .collect(Collectors.groupingBy(AsccManifestRecord::getFromAccManifestId));

        List<AsccRecord> asccList = ccRepository.findAllAscc(moduleSetReleaseId);
        findAsccByFromAccIdMap = asccList.stream()
                .collect(Collectors.groupingBy(AsccRecord::getFromAccId));

        findAsccMap = asccList.stream()
                .collect(Collectors.toMap(AsccRecord::getAsccId, Function.identity()));

        findModuleCodeListManifestMap = ccRepository.findAllModuleCodeListManifest(moduleSetReleaseId)
                .stream().collect(Collectors.toMap(ModuleCCID::getCcId, Function.identity()));

        findModuleAccManifestMap = ccRepository.findAllModuleAccManifest(moduleSetReleaseId)
                .stream().collect(Collectors.toMap(ModuleCCID::getCcId, Function.identity()));

        findModuleAsccpManifestMap = ccRepository.findAllModuleAsccpManifest(moduleSetReleaseId)
                .stream().collect(Collectors.toMap(ModuleCCID::getCcId, Function.identity()));

        findModuleBccpManifestMap = ccRepository.findAllModuleBccpManifest(moduleSetReleaseId)
                .stream().collect(Collectors.toMap(ModuleCCID::getCcId, Function.identity()));

        findModuleDtManifestMap = ccRepository.findAllModuleDtManifest(moduleSetReleaseId)
                .stream().collect(Collectors.toMap(ModuleCCID::getCcId, Function.identity()));

        findModuleXbtManifestMap = ccRepository.findAllModuleXbtManifest(moduleSetReleaseId)
                .stream().collect(Collectors.toMap(ModuleXbtID::getXbtId, Function.identity()));

        findBlobContentList = ccRepository.findAllBlobContent(moduleSetReleaseId);

        findModuleBlobContentManifestMap = ccRepository.findAllModuleBlobContentManifest(moduleSetReleaseId)
                .stream().collect(Collectors.toMap(ModuleCCID::getCcId, Function.identity()));

        findSeqKeyList = ccRepository.findAllSeqKeyRecord();

        findSeqKeyMap = findSeqKeyList.stream()
                .collect(Collectors.groupingBy(SeqKeyRecord::getFromAccManifestId));


        logger.info("Ready for " + getClass().getSimpleName() + " in " + (System.currentTimeMillis() - s) / 1000d + " seconds");
    }

    private List<SeqKeyRecord> findSeqKeyList;
    private Map<ULong, List<SeqKeyRecord>> findSeqKeyMap;

    public List<SeqKeyRecord> getSeqKeys(ULong accManifestId) {
        return findSeqKeyMap.containsKey(accManifestId) ? findSeqKeyMap.get(accManifestId) : Collections.emptyList();
    }

    private List<AgencyIdListRecord> findAgencyIdListList;
    private Map<ULong, ModuleCCID> findModuleAgencyIdListManifestMap;
    private Map<ULong, ModuleCCID> findModuleCodeListManifestMap;
    private Map<ULong, ModuleCCID> findModuleAccManifestMap;
    private Map<ULong, ModuleCCID> findModuleDtManifestMap;
    private Map<ULong, ModuleCCID> findModuleAsccpManifestMap;
    private Map<ULong, ModuleCCID> findModuleBccpManifestMap;
    private Map<String, ModuleXbtID> findModuleXbtManifestMap;
    private Map<ULong, ModuleCCID> findModuleBlobContentManifestMap;

    
    public List<AgencyIdListRecord> findAgencyIdList() {
        return Collections.unmodifiableList(findAgencyIdListList);
    }

    private Map<ULong, AgencyIdListRecord> findAgencyIdListMap;

    
    public AgencyIdListRecord findAgencyIdList(ULong agencyIdListId) {
        AgencyIdListRecord a = findAgencyIdListMap.get(agencyIdListId);
        if (a == null) {
            throw new IllegalStateException();
        }
        return findAgencyIdListMap.get(agencyIdListId);
    }

    private Map<ULong, List<AgencyIdListValueRecord>> findAgencyIdListValueByOwnerListIdMap;

    
    public List<AgencyIdListValueRecord> findAgencyIdListValueByOwnerListId(ULong ownerListId) {
        return findAgencyIdListValueByOwnerListIdMap.containsKey(ownerListId) ? findAgencyIdListValueByOwnerListIdMap.get(ownerListId) : Collections.emptyList();
    }

    private List<CodeListRecord> findCodeListList;

    
    public List<CodeListRecord> findCodeList() {
        return Collections.unmodifiableList(findCodeListList);
    }

    private Map<ULong, CodeListRecord> findCodeListMap;

    
    public CodeListRecord findCodeList(ULong codeListId) {
        return findCodeListMap.get(codeListId);
    }

    private Map<ULong, List<CodeListValueRecord>> findCodeListValueByCodeListIdMap;

    
    public List<CodeListValueRecord> findCodeListValueByCodeListId(ULong codeListId) {
        return (findCodeListValueByCodeListIdMap.containsKey(codeListId)) ? findCodeListValueByCodeListIdMap.get(codeListId) : Collections.emptyList();
    }

    private List<DtManifestRecord> findDtManifestList;
    private Map<ULong, DtManifestRecord> findDtManifestMap;

    public List<DtManifestRecord> findDtManifest() {
        return Collections.unmodifiableList(this.findDtManifestList);
    }

    public DtManifestRecord findDtManifestByDtManifestId(ULong dtManifestId) {
        return this.findDtManifestMap.get(dtManifestId);
    }

    private List<DtRecord> findDtList;

    
    public List<DtRecord> findDT() {
        return Collections.unmodifiableList(findDtList);
    }

    private Map<ULong, DtRecord> findDtMap;

    
    public DtRecord findDT(ULong dtId) {
        return findDtMap.get(dtId);
    }

    private Map<ULong, List<DtScRecord>> findDtScByOwnerDtIdMap;

    
    public List<DtScRecord> findDtScByOwnerDtId(ULong ownerDtId) {
        return (findDtScByOwnerDtIdMap.containsKey(ownerDtId)) ? findDtScByOwnerDtIdMap.get(ownerDtId) : Collections.emptyList();
    }

    private Map<ULong, List<BdtPriRestriRecord>> findBdtPriRestriListByDtIdMap;

    
    public List<BdtPriRestriRecord> findBdtPriRestriListByDtId(ULong dtId) {
        return (findBdtPriRestriListByDtIdMap.containsKey(dtId)) ? findBdtPriRestriListByDtIdMap.get(dtId) : Collections.emptyList();
    }

    private Map<String, CdtAwdPriXpsTypeMapRecord> cdtAwdPriXpsTypeMapMap;

    
    public CdtAwdPriXpsTypeMapRecord findCdtAwdPriXpsTypeMapById(String cdtAwdPriXpsTypeMapId) {
        return cdtAwdPriXpsTypeMapMap.get(cdtAwdPriXpsTypeMapId);
    }

    
    public List<CdtAwdPriXpsTypeMapRecord> findCdtAwdPriXpsTypeMapListByDtId(ULong dtId) {
        List<BdtPriRestriRecord> bdtPriRestriList = findBdtPriRestriListByDtId(dtId);
        List<CdtAwdPriXpsTypeMapRecord> cdtAwdPriXpsTypeMapList = bdtPriRestriList.stream()
                .filter(e -> e.getCdtAwdPriXpsTypeMapId() != null)
                .map(e -> cdtAwdPriXpsTypeMapMap.get(e.getCdtAwdPriXpsTypeMapId()))
                .collect(Collectors.toList());
        return (cdtAwdPriXpsTypeMapList != null) ? cdtAwdPriXpsTypeMapList : Collections.emptyList();
    }

    private Map<ULong, List<BdtScPriRestriRecord>> findBdtScPriRestriListByDtScIdMap;

    
    public List<BdtScPriRestriRecord> findBdtScPriRestriListByDtScId(ULong dtScId) {
        return (findBdtScPriRestriListByDtScIdMap.containsKey(dtScId)) ? findBdtScPriRestriListByDtScIdMap.get(dtScId) : Collections.emptyList();
    }

    private Map<String, CdtScAwdPriXpsTypeMapRecord> findCdtScAwdPriXpsTypeMapMap;

    
    public CdtScAwdPriXpsTypeMapRecord findCdtScAwdPriXpsTypeMap(String cdtScAwdPriXpsTypeMapId) {
        return findCdtScAwdPriXpsTypeMapMap.get(cdtScAwdPriXpsTypeMapId);
    }

    private Map<String, CdtScAwdPriRecord> findCdtScAwdPriMap;

    
    public CdtScAwdPriRecord findCdtScAwdPri(String cdtScAwdPriId) {
        return findCdtScAwdPriMap.get(cdtScAwdPriId);
    }

    private List<XbtRecord> findXbtList;

    
    public List<XbtRecord> findXbt() {
        return findXbtList;
    }

    private Map<String, CdtAwdPriRecord> findCdtAwdPriMap;

    
    public CdtAwdPriRecord findCdtAwdPri(String cdtAwdPriId) {
        return findCdtAwdPriMap.get(cdtAwdPriId);
    }

    private Map<String, CdtPriRecord> findCdtPriMap;

    public CdtPriRecord findCdtPri(String cdtPriId) {
        return findCdtPriMap.get(cdtPriId);
    }

    private Map<String, XbtRecord> findXbtMap;

    
    public XbtRecord findXbt(String xbtId) {
        XbtRecord xbt = findXbtMap.get(xbtId);
        return xbt;
    }

    private List<AccRecord> findACCList;

    
    public List<AccRecord> findACC() {
        return Collections.unmodifiableList(findACCList);
    }

    
    public List<AccManifestRecord> findACCManifest() {
        return Collections.unmodifiableList(findACCManifestList);
    }

    private Map<ULong, AccRecord> findAccMap;

    
    public AccRecord findACC(ULong accId) {
        return findAccMap.get(accId);
    }

    private List<AccManifestRecord> findACCManifestList;

    private Map<ULong, AccManifestRecord> findAccManifestMap;

    
    public AccManifestRecord findACCManifest(ULong accManifestId) {
        return findAccManifestMap.get(accManifestId);
    }

    
    public List<AsccpManifestRecord> findASCCPManifest() {
        return Collections.unmodifiableList(findASCCPManifestList);
    }

    private List<AsccpManifestRecord> findASCCPManifestList;

    private Map<ULong, AsccpManifestRecord> findAsccpManifestMap;

    
    public AsccpManifestRecord findASCCPManifest(ULong asccpManifestId) {
        return findAsccpManifestMap.get(asccpManifestId);
    }

    
    public List<BccpManifestRecord> findBCCPManifest() {
        return Collections.unmodifiableList(findBCCPManifestList);
    }

    private List<BccpManifestRecord> findBCCPManifestList;

    private Map<ULong, BccpManifestRecord> findBccpManifestMap;

    
    public BccpManifestRecord findBCCPManifest(ULong bccpManifestId) {
        if (bccpManifestId == null) {
            return null;
        }
        return findBccpManifestMap.get(bccpManifestId);
    }

    private List<AsccpRecord> findASCCPList;

    
    public List<AsccpRecord> findASCCP() {
        return Collections.unmodifiableList(findASCCPList);
    }

    private Map<ULong, AsccpRecord> findAsccpMap;

    
    public AsccpRecord findASCCP(ULong asccpId) {
        return findAsccpMap.get(asccpId);
    }

    private Map<String, AsccpRecord> findAsccpByGuidMap;

    
    public AsccpRecord findASCCPByGuid(String guid) {
        return findAsccpByGuidMap.get(guid);
    }

    private List<BccpRecord> findBCCPList;

    
    public List<BccpRecord> findBCCP() {
        return Collections.unmodifiableList(findBCCPList);
    }

    private Map<ULong, BccpRecord> findBccpMap;

    
    public BccpRecord findBCCP(ULong bccpId) {
        return findBccpMap.get(bccpId);
    }

    private Map<ULong, List<BccRecord>> findBCCByToBccpIdMap;

    
    public List<BccRecord> findBCCByToBccpId(ULong toBccpId) {
        return (findBCCByToBccpIdMap.containsKey(toBccpId)) ? findBCCByToBccpIdMap.get(toBccpId) : Collections.emptyList();
    }

    private Map<ULong, List<BccRecord>> findBccByFromAccIdMap;

    
    public List<BccRecord> findBCCByFromAccId(ULong fromAccId) {
        return (findBccByFromAccIdMap.containsKey(fromAccId)) ? findBccByFromAccIdMap.get(fromAccId) : Collections.emptyList();
    }

    private Map<ULong, AsccRecord> findAsccMap;

    
    public AsccRecord findASCC(ULong asccId) {
        return findAsccMap.get(asccId);
    }

    private Map<ULong, BccRecord> findBccMap;

    
    public BccRecord findBCC(ULong bccId) {
        return findBccMap.get(bccId);
    }

    private Map<ULong, AsccManifestRecord> findAsccManifestMap;
    private Map<ULong, List<AsccManifestRecord>> findAsccManifestByAccManifestIdMap;

    
    public AsccManifestRecord findASCCManifest(ULong asccId) {
        return findAsccManifestMap.get(asccId);
    }

    public List<AsccManifestRecord> findASCCManifestByFromAccManifestId(ULong fromAccManifestId) {
        if (!findAsccManifestByAccManifestIdMap.containsKey(fromAccManifestId)) {
            return Collections.emptyList();
        }
        return findAsccManifestByAccManifestIdMap.get(fromAccManifestId);
    }

    private Map<ULong, BccManifestRecord> findBccManifestMap;
    private Map<ULong, List<BccManifestRecord>> findBccManifestByAccManifestIdMap;

    
    public BccManifestRecord findBCCManifest(ULong bccId) {
        return findBccManifestMap.get(bccId);
    }

    public List<BccManifestRecord> findBCCManifestByFromAccManifestId(ULong fromAccManifestId) {
        if (!findBccManifestByAccManifestIdMap.containsKey(fromAccManifestId)) {
            return Collections.emptyList();
        }
        return findBccManifestByAccManifestIdMap.get(fromAccManifestId);
    }

    private Map<ULong, List<AsccRecord>> findAsccByFromAccIdMap;

    
    public List<AsccRecord> findASCCByFromAccId(ULong fromAccId) {
        return (findAsccByFromAccIdMap.containsKey(fromAccId)) ? findAsccByFromAccIdMap.get(fromAccId) : Collections.emptyList();
    }

    
    public ModuleCCID findModuleAgencyIdList(ULong agencyIdListId) {
        return findModuleAgencyIdListManifestMap.get(agencyIdListId);
    }

    
    public ModuleCCID findModuleCodeList(ULong codeListId) {
        return findModuleCodeListManifestMap.get(codeListId);
    }

    
    public ModuleCCID findModuleAcc(ULong accId) {
        return findModuleAccManifestMap.get(accId);
    }

    
    public ModuleCCID findModuleAsccp(ULong asccpId) {
        return findModuleAsccpManifestMap.get(asccpId);
    }

    
    public ModuleCCID findModuleBccp(ULong bccpId) {
        return findModuleBccpManifestMap.get(bccpId);
    }

    
    public ModuleCCID findModuleDt(ULong dtId) {
        return findModuleDtManifestMap.get(dtId);
    }

    
    public ModuleXbtID findModuleXbt(String xbtId) {
        return findModuleXbtManifestMap.get(xbtId);
    }

    private List<BlobContentRecord> findBlobContentList;

    
    public List<BlobContentRecord> findBlobContent() {
        return findBlobContentList;
    }

    
    public ModuleCCID findModuleBlobContent(ULong blobContentId) {
        return findModuleBlobContentManifestMap.get(blobContentId);
    }
}
