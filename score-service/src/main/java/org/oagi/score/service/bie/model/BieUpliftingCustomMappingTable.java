package org.oagi.score.service.bie.model;

import lombok.Data;
import org.oagi.score.repo.api.corecomponent.model.AsccManifest;
import org.oagi.score.repo.api.corecomponent.model.AsccpManifest;
import org.oagi.score.repo.api.corecomponent.model.BccManifest;
import org.oagi.score.service.corecomponent.CcDocument;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.oagi.score.repo.api.impl.utils.StringUtils.hasLength;

public class BieUpliftingCustomMappingTable {

    @Data
    public class BieUpliftingMapping {
        private String type;
        private BigInteger bieId;
        private BigInteger sourceManifestId;
        private String sourcePath;
        private BigInteger targetManifestId;
        private String targetPath;
    }

    private Map<String, BieUpliftingMapping> targetAsccMappingMap;
    private Map<String, BigInteger> targetAsccpManifestIdBySourcePathMap;
    private Map<String, BigInteger> targetAccManifestIdBySourcePathMap;
    private Map<String, BieUpliftingMapping> targetBccMappingMap;
    private Map<String, BigInteger> targetBccpManifestIdBySourcePathMap;
    private Map<String, BieUpliftingMapping> targetDtScMappingMap;

    public BieUpliftingCustomMappingTable(CcDocument sourceCcDocument,
                                          CcDocument targetCcDocument,
                                          List<BieUpliftingMapping> mappingList) {
        if (mappingList == null) {
            throw new IllegalArgumentException();
        }

        targetAsccMappingMap = mappingList.stream()
                .filter(e -> hasLength(e.getSourcePath()))
                .filter(e -> getLastTag(e.getTargetPath()).contains("ASCC"))
                .collect(Collectors.toMap(BieUpliftingMapping::getSourcePath, Function.identity()));

        targetAsccpManifestIdBySourcePathMap = targetAsccMappingMap.values().stream()
                .collect(Collectors.toMap(e -> {
                    BigInteger sourceAsccManifestId = extractManifestId(getLastTag(e.getSourcePath()));
                    AsccManifest sourceAsccManifest = sourceCcDocument.getAsccManifest(sourceAsccManifestId);
                    return e.getSourcePath() + ">" + "ASCCP-" + sourceAsccManifest.getToAsccpManifestId();
                }, e -> {
                    BigInteger targetAsccManifestId = extractManifestId(getLastTag(e.getTargetPath()));
                    AsccManifest targetAsccManifest = targetCcDocument.getAsccManifest(targetAsccManifestId);
                    return targetAsccManifest.getToAsccpManifestId();
                }));

        targetAccManifestIdBySourcePathMap = targetAsccMappingMap.values().stream()
                .collect(Collectors.toMap(e -> {
                    BigInteger sourceAsccManifestId = extractManifestId(getLastTag(e.getSourcePath()));
                    AsccManifest sourceAsccManifest = sourceCcDocument.getAsccManifest(sourceAsccManifestId);
                    AsccpManifest sourceAsccpManifest = sourceCcDocument.getAsccpManifest(sourceAsccManifest.getToAsccpManifestId());
                    return e.getSourcePath() + ">" + "ASCCP-" + sourceAsccManifest.getToAsccpManifestId() +
                            ">" + "ACC-" + sourceAsccpManifest.getRoleOfAccManifestId();
                }, e -> {
                    BigInteger targetAsccManifestId = extractManifestId(getLastTag(e.getTargetPath()));
                    AsccManifest targetAsccManifest = targetCcDocument.getAsccManifest(targetAsccManifestId);
                    AsccpManifest targetAsccpManifest = targetCcDocument.getAsccpManifest(targetAsccManifest.getToAsccpManifestId());
                    return targetAsccpManifest.getRoleOfAccManifestId();
                }));

        targetBccMappingMap = mappingList.stream()
                .filter(e -> hasLength(e.getSourcePath()))
                .filter(e -> getLastTag(e.getTargetPath()).contains("BCC"))
                .collect(Collectors.toMap(BieUpliftingMapping::getSourcePath, Function.identity()));

        targetBccpManifestIdBySourcePathMap = targetBccMappingMap.values().stream()
                .collect(Collectors.toMap(e -> {
                    BigInteger sourceBccManifestId = extractManifestId(getLastTag(e.getSourcePath()));
                    BccManifest sourceBccManifest = sourceCcDocument.getBccManifest(sourceBccManifestId);
                    return e.getSourcePath() + ">" + "BCCP-" + sourceBccManifest.getToBccpManifestId();
                }, e -> {
                    BigInteger targetBccManifestId = extractManifestId(getLastTag(e.getTargetPath()));
                    BccManifest targetBccManifest = targetCcDocument.getBccManifest(targetBccManifestId);
                    return targetBccManifest.getToBccpManifestId();
                }));

        targetDtScMappingMap = mappingList.stream()
                .filter(e -> hasLength(e.getSourcePath()))
                .filter(e -> getLastTag(e.getTargetPath()).contains("BDT_SC"))
                .collect(Collectors.toMap(BieUpliftingMapping::getSourcePath, Function.identity()));
    }

    private String getLastTag(String path) {
        if (path == null) {
            return null;
        }
        String[] tags = path.split(">");
        return tags[tags.length - 1];
    }

    private BigInteger extractManifestId(String tag) {
        return new BigInteger(tag.substring(tag.indexOf('-') + 1));
    }

    public BigInteger getTargetAccManifestIdBySourcePath(String sourcePath) {
        return targetAccManifestIdBySourcePathMap.get(sourcePath);
    }

    public BigInteger getTargetAsccpManifestIdBySourcePath(String sourcePath) {
        return targetAsccpManifestIdBySourcePathMap.get(sourcePath);
    }

    public BigInteger getTargetBccpManifestIdBySourcePath(String sourcePath) {
        return targetBccpManifestIdBySourcePathMap.get(sourcePath);
    }

    public BieUpliftingMapping getTargetAsccMappingBySourcePath(String sourcePath) {
        return targetAsccMappingMap.get(sourcePath);
    }

    public BieUpliftingMapping getTargetBccMappingBySourcePath(String sourcePath) {
        return targetBccMappingMap.get(sourcePath);
    }

    public BieUpliftingMapping getTargetDtScMappingBySourcePath(String sourcePath) {
        return targetDtScMappingMap.get(sourcePath);
    }

}
