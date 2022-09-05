package org.oagi.score.export.impl;

import org.jooq.types.ULong;
import org.oagi.score.export.ExportContext;
import org.oagi.score.export.model.*;
import org.oagi.score.provider.ImportedDataProvider;
import org.oagi.score.repo.api.corecomponent.model.EntityType;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.*;
import org.oagi.score.repository.ModuleRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import static org.oagi.score.common.ScoreConstants.ANY_ASCCP_DEN;

public class StandaloneExportContextBuilder {

    private ModuleRepository moduleRepository;

    private ImportedDataProvider importedDataProvider;

    public StandaloneExportContextBuilder(ModuleRepository moduleRepository,
                                          ImportedDataProvider importedDataProvider) {
        this.moduleRepository = moduleRepository;
        this.importedDataProvider = importedDataProvider;
    }

    public ExportContext build(String moduleSetReleaseId,
                               String asccpManifestId) {
        DefaultExportContext context = new DefaultExportContext();
        ScoreModule scoreModule = moduleRepository.findByModuleSetReleaseIdAndAsccpManifestId(
                moduleSetReleaseId, asccpManifestId);
        SchemaModule schemaModule = new SchemaModule(scoreModule);
        context.addSchemaModule(schemaModule);

        addASCCP(schemaModule, asccpManifestId);

        return context;
    }

    private void addASCCP(SchemaModule schemaModule, String asccpManifestId) {
        AsccpManifestRecord asccpManifest =
                importedDataProvider.findASCCPManifest(asccpManifestId);
        AsccpRecord asccp = importedDataProvider.findASCCP(asccpManifest.getAsccpId());
        if (asccp.getDen().equals(ANY_ASCCP_DEN)) {
            return;
        }

        if (asccp.getReusableIndicator() != (byte) 0) {
            if (!schemaModule.addASCCP(ASCCP.newInstance(asccp, asccpManifest, importedDataProvider))) {
                return;
            }
        }
        addACC(schemaModule, asccpManifest.getRoleOfAccManifestId());
    }

    private void addBCCP(SchemaModule schemaModule, BccManifestRecord bccManifest) {
        BccRecord bcc = importedDataProvider.findBCC(bccManifest.getBccId());
        BccpManifestRecord bccpManifest =
                importedDataProvider.findBCCPManifest(bccManifest.getToBccpManifestId());

        if (EntityType.Element == EntityType.valueOf(bcc.getEntityType())) {
            BccpRecord bccp = importedDataProvider.findBCCP(bccpManifest.getBccpId());
            DtRecord bdt = importedDataProvider.findDT(bccp.getBdtId());
            if (!schemaModule.addBCCP(new BCCP(bccp, bdt))) {
                return;
            }
        }

        addBDT(schemaModule, bccpManifest.getBdtManifestId());
    }

    private void addACC(SchemaModule schemaModule, String accManifestId) {
        AccManifestRecord accManifest =
                importedDataProvider.findACCManifest(accManifestId);

        if (accManifest.getBasedAccManifestId() != null) {
            addACC(schemaModule, accManifest.getBasedAccManifestId());
        }

        AccRecord acc = importedDataProvider.findACC(accManifest.getAccId());
        if (acc.getDen().equals("Any Structured Content. Details")) {
            return;
        }
        ModuleCCID moduleCCID = importedDataProvider.findModuleAcc(acc.getAccId());
        if (moduleCCID == null) {
            return;
        }

        if (!schemaModule.addACC(ACC.newInstance(acc, accManifest, importedDataProvider))) {
            return;
        }

        importedDataProvider.findASCCManifestByFromAccManifestId(accManifestId).forEach(asccManifest -> {
            addASCCP(schemaModule, asccManifest.getToAsccpManifestId());
        });

        importedDataProvider.findBCCManifestByFromAccManifestId(accManifestId).forEach(bccManifest -> {
            addBCCP(schemaModule, bccManifest);
        });
    }

    private void addBDT(SchemaModule schemaModule, String bdtManifestId) {
        DtManifestRecord dtManifestRecord = importedDataProvider.findDtManifestByDtManifestId(bdtManifestId);
        if (dtManifestRecord.getBasedDtManifestId() != null) {
            addBDT(schemaModule, dtManifestRecord.getBasedDtManifestId());
        }
        DtRecord bdt = importedDataProvider.findDT(dtManifestRecord.getDtId());

        DtRecord baseDataType = importedDataProvider.findDT(bdt.getBasedDtId());
        if (baseDataType == null) {
            return;
        }
        List<DtScRecord> dtScList =
                importedDataProvider.findDtScByOwnerDtId(bdt.getDtId()).stream()
                        .filter(e -> e.getCardinalityMax() > 0).collect(Collectors.toList());

        String dtModulePath = moduleRepository.getModulePathByDtManifestId(
                schemaModule.getModuleSetReleaseId(),
                bdtManifestId);

        boolean isDefaultBDT = (dtModulePath != null) && dtModulePath.contains("BusinessDataType_1");
        BDTSimple bdtSimple;
        if (dtScList.isEmpty()) {
            String bdtId = bdt.getDtId();
            List<BdtPriRestriRecord> bdtPriRestriList =
                    importedDataProvider.findBdtPriRestriListByDtId(bdtId);
            List<CdtAwdPriXpsTypeMapRecord> cdtAwdPriXpsTypeMapList =
                    importedDataProvider.findCdtAwdPriXpsTypeMapListByDtId(bdtId);
            if (!cdtAwdPriXpsTypeMapList.isEmpty()) {
                List<XbtRecord> xbtList = cdtAwdPriXpsTypeMapList.stream()
                        .map(e -> importedDataProvider.findXbt(e.getXbtId()))
                        .collect(Collectors.toList());
                bdtSimple = new BDTSimpleType(
                        bdt, baseDataType, isDefaultBDT,
                        bdtPriRestriList, xbtList, importedDataProvider);
            } else {
                bdtSimple = new BDTSimpleType(
                        bdt, baseDataType, isDefaultBDT, importedDataProvider);
            }
        } else {
            bdtSimple = new BDTSimpleContent(bdt, baseDataType, isDefaultBDT, dtScList, importedDataProvider);
            dtScList.forEach(dtScRecord -> {
                List<BdtScPriRestriRecord> bdtScPriRestriList =
                        importedDataProvider.findBdtScPriRestriListByDtScId(dtScRecord.getDtScId());

                for (BdtScPriRestriRecord bdtScPriRestri : bdtScPriRestriList) {
                    if (bdtScPriRestri.getCdtScAwdPriXpsTypeMapId() == null) {
                        continue;
                    }
                    CdtScAwdPriXpsTypeMapRecord cdtScAwdPriXpsTypeMap =
                            importedDataProvider.findCdtScAwdPriXpsTypeMap(bdtScPriRestri.getCdtScAwdPriXpsTypeMapId());
                    XbtRecord xbt = importedDataProvider.findXbt(cdtScAwdPriXpsTypeMap.getXbtId());
                    addXBTSimpleType(schemaModule, xbt);
                }

                List<BdtScPriRestriRecord> codeListBdtScPriRestri =
                        bdtScPriRestriList.stream()
                                .filter(e -> e.getCodeListId() != null)
                                .collect(Collectors.toList());
                if (codeListBdtScPriRestri.size() > 1) {
                    throw new IllegalStateException();
                }

                if (codeListBdtScPriRestri.isEmpty()) {
                    List<BdtScPriRestriRecord> agencyIdBdtScPriRestri =
                            bdtScPriRestriList.stream()
                                    .filter(e -> e.getAgencyIdListId() != null)
                                    .collect(Collectors.toList());
                    if (agencyIdBdtScPriRestri.size() > 1) {
                        throw new IllegalStateException();
                    }

                    if (agencyIdBdtScPriRestri.isEmpty()) {
                        List<BdtScPriRestriRecord> defaultBdtScPriRestri =
                                bdtScPriRestriList.stream()
                                        .filter(e -> e.getIsDefault() == 1)
                                        .collect(Collectors.toList());
                        if (defaultBdtScPriRestri.isEmpty() || defaultBdtScPriRestri.size() > 1) {
                            throw new IllegalStateException();
                        }
                    } else {
                        AgencyIdListRecord agencyIdList = importedDataProvider.findAgencyIdList(agencyIdBdtScPriRestri.get(0).getAgencyIdListId());
                        List<AgencyIdListValueRecord> agencyIdListValues = importedDataProvider.findAgencyIdListValueByOwnerListId(agencyIdList.getAgencyIdListId());
                        schemaModule.addAgencyId(new AgencyId(agencyIdList, agencyIdListValues));
                    }
                } else {
                    CodeListRecord codeList = importedDataProvider.findCodeList(codeListBdtScPriRestri.get(0).getCodeListId());
                    addCodeList(schemaModule, codeList);
                }
            });
        }

        schemaModule.addBDTSimple(bdtSimple);

        List<BdtPriRestriRecord> bdtPriRestriList = importedDataProvider.findBdtPriRestriListByDtId(bdt.getDtId());
        for (BdtPriRestriRecord bdtPriRestri : bdtPriRestriList) {
            if (bdtPriRestri.getCdtAwdPriXpsTypeMapId() == null) {
                continue;
            }
            CdtAwdPriXpsTypeMapRecord cdtAwdPriXpsTypeMap =
                    importedDataProvider.findCdtAwdPriXpsTypeMapById(bdtPriRestri.getCdtAwdPriXpsTypeMapId());
            XbtRecord xbt = importedDataProvider.findXbt(cdtAwdPriXpsTypeMap.getXbtId());
            addXBTSimpleType(schemaModule, xbt);
        }

        List<BdtPriRestriRecord> codeListBdtPriRestri =
                bdtPriRestriList.stream()
                        .filter(e -> e.getCodeListId() != null)
                        .collect(Collectors.toList());
        if (codeListBdtPriRestri.size() > 1) {
            throw new IllegalStateException();
        }
        if (codeListBdtPriRestri.isEmpty()) {
            List<BdtPriRestriRecord> agencyIdBdtPriRestri =
                    codeListBdtPriRestri.stream()
                            .filter(e -> e.getAgencyIdListId() != null)
                            .collect(Collectors.toList());
            if (agencyIdBdtPriRestri.size() > 1) {
                throw new IllegalStateException();
            }

            if (agencyIdBdtPriRestri.isEmpty()) {
                List<BdtPriRestriRecord> defaultBdtPriRestri =
                        bdtPriRestriList.stream()
                                .filter(e -> e.getIsDefault() == 1)
                                .collect(Collectors.toList());
                if (defaultBdtPriRestri.isEmpty() || defaultBdtPriRestri.size() > 1) {
                    throw new IllegalStateException();
                }
            } else {
                AgencyIdListRecord agencyIdList = importedDataProvider.findAgencyIdList(agencyIdBdtPriRestri.get(0).getAgencyIdListId());
                List<AgencyIdListValueRecord> agencyIdListValues = importedDataProvider.findAgencyIdListValueByOwnerListId(agencyIdList.getAgencyIdListId());
                schemaModule.addAgencyId(new AgencyId(agencyIdList, agencyIdListValues));
            }
        } else {
            CodeListRecord codeList = importedDataProvider.findCodeList(codeListBdtPriRestri.get(0).getCodeListId());
            addCodeList(schemaModule, codeList);
        }
    }

    private void addXBTSimpleType(SchemaModule schemaModule, XbtRecord xbt) {
        if (xbt.getBuiltinType().startsWith("xsd")) {
            return;
        }
        XbtRecord baseXbt = importedDataProvider.findXbt(xbt.getSubtypeOfXbtId());
        if (baseXbt != null) {
            addXBTSimpleType(schemaModule, baseXbt);
        }
        schemaModule.addXBTSimpleType(new XBTSimpleType(xbt, baseXbt));
    }

    private void addCodeList(SchemaModule schemaModule, CodeListRecord codeList) {
        CodeListRecord baseCodeList = importedDataProvider.findCodeList(codeList.getBasedCodeListId());
        if (baseCodeList != null) {
            addCodeList(schemaModule, baseCodeList);
        }

        SchemaCodeList schemaCodeList = new SchemaCodeList();
        schemaCodeList.setGuid(codeList.getGuid());
        schemaCodeList.setName(codeList.getName());
        schemaCodeList.setEnumTypeGuid(codeList.getEnumTypeGuid());

        for (CodeListValueRecord codeListValue : importedDataProvider.findCodeListValueByCodeListId(codeList.getCodeListId())) {
            schemaCodeList.addValue(codeListValue.getValue());
        }

        schemaModule.addCodeList(schemaCodeList);
    }

}
