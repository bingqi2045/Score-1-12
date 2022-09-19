package org.oagi.score.service.bie.model;

import org.oagi.score.repo.api.corecomponent.model.AsccManifest;
import org.oagi.score.repo.api.corecomponent.model.AsccpManifest;
import org.oagi.score.repo.api.corecomponent.model.BccManifest;
import org.oagi.score.service.corecomponent.CcDocument;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.oagi.score.repo.api.impl.utils.StringUtils.hasLength;

public class BieUpliftingCustomMappingTable {

    private List<BieUpliftingMapping> mappingList;

    private Map<String, BieUpliftingMapping> targetAsccMappingMap;
    private Map<String, BieUpliftingMapping> targetAsccMappingByTargetPathMap;
    private Map<String, String> targetAsccpManifestIdBySourcePathMap;
    private Map<String, String> targetAccManifestIdBySourcePathMap;
    private Map<String, BieUpliftingMapping> targetBccMappingMap;
    private Map<String, String> targetBccpManifestIdBySourcePathMap;
    private Map<String, BieUpliftingMapping> targetDtScMappingMap;

    public BieUpliftingCustomMappingTable(CcDocument sourceCcDocument,
                                          CcDocument targetCcDocument,
                                          List<BieUpliftingMapping> mappingList) {
        if (mappingList == null) {
            throw new IllegalArgumentException();
        }

        this.mappingList = mappingList;

        targetAsccMappingMap = mappingList.stream()
                .filter(e -> hasLength(e.getSourcePath()))
                .filter(e -> getLastTag(e.getSourcePath()).contains("ASCC"))
                .collect(Collectors.toMap(BieUpliftingMapping::getSourcePath, Function.identity(), (a1, a2) -> a2));

        targetAsccMappingByTargetPathMap = mappingList.stream()
                .filter(e -> hasLength(e.getSourcePath()) && hasLength(e.getTargetPath()))
                .filter(e -> getLastTag(e.getTargetPath()).contains("ASCC"))
                .collect(Collectors.toMap(BieUpliftingMapping::getTargetPath, Function.identity(), (a1, a2) -> a2));

        targetAsccpManifestIdBySourcePathMap = targetAsccMappingMap.values().stream()
                .filter(e -> hasLength(e.getSourcePath()) && hasLength(e.getTargetPath()))
                .collect(Collectors.toMap(e -> {
                    String sourceAsccManifestId = extractManifestId(getLastTag(e.getSourcePath()));
                    AsccManifest sourceAsccManifest = sourceCcDocument.getAsccManifest(sourceAsccManifestId);
                    return e.getSourcePath() + ">" + "ASCCP-" + sourceAsccManifest.getToAsccpManifestId();
                }, e -> {
                    String targetAsccManifestId = extractManifestId(getLastTag(e.getTargetPath()));
                    AsccManifest targetAsccManifest = targetCcDocument.getAsccManifest(targetAsccManifestId);
                    return targetAsccManifest.getToAsccpManifestId();
                }, (a1, a2) -> a2));

        targetAccManifestIdBySourcePathMap = targetAsccMappingMap.values().stream()
                .filter(e -> hasLength(e.getSourcePath()) && hasLength(e.getTargetPath()))
                .collect(Collectors.toMap(e -> {
                    String sourceAsccManifestId = extractManifestId(getLastTag(e.getSourcePath()));
                    AsccManifest sourceAsccManifest = sourceCcDocument.getAsccManifest(sourceAsccManifestId);
                    AsccpManifest sourceAsccpManifest = sourceCcDocument.getAsccpManifest(sourceAsccManifest.getToAsccpManifestId());
                    return e.getSourcePath() + ">" + "ASCCP-" + sourceAsccManifest.getToAsccpManifestId() +
                            ">" + "ACC-" + sourceAsccpManifest.getRoleOfAccManifestId();
                }, e -> {
                    String targetAsccManifestId = extractManifestId(getLastTag(e.getTargetPath()));
                    AsccManifest targetAsccManifest = targetCcDocument.getAsccManifest(targetAsccManifestId);
                    AsccpManifest targetAsccpManifest = targetCcDocument.getAsccpManifest(targetAsccManifest.getToAsccpManifestId());
                    return targetAsccpManifest.getRoleOfAccManifestId();
                }, (a1, a2) -> a2));

        targetBccMappingMap = mappingList.stream()
                .filter(e -> hasLength(e.getSourcePath()))
                .filter(e -> getLastTag(e.getSourcePath()).contains("BCC"))
                .collect(Collectors.toMap(BieUpliftingMapping::getSourcePath, Function.identity(), (a1, a2) -> a2));

        targetBccpManifestIdBySourcePathMap = targetBccMappingMap.values().stream()
                .filter(e -> hasLength(e.getSourcePath()) && hasLength(e.getTargetPath()))
                .collect(Collectors.toMap(e -> {
                    String sourceBccManifestId = extractManifestId(getLastTag(e.getSourcePath()));
                    BccManifest sourceBccManifest = sourceCcDocument.getBccManifest(sourceBccManifestId);
                    return e.getSourcePath() + ">" + "BCCP-" + sourceBccManifest.getToBccpManifestId();
                }, e -> {
                    String targetBccManifestId = extractManifestId(getLastTag(e.getTargetPath()));
                    BccManifest targetBccManifest = targetCcDocument.getBccManifest(targetBccManifestId);
                    return targetBccManifest.getToBccpManifestId();
                }, (a1, a2) -> a2));

        targetDtScMappingMap = mappingList.stream()
                .filter(e -> hasLength(e.getSourcePath()))
                .filter(e -> getLastTag(e.getSourcePath()).contains("DT_SC"))
                .collect(Collectors.toMap(BieUpliftingMapping::getSourcePath, Function.identity(), (a1, a2) -> a2));
    }

    public static String getLastTag(String path) {
        if (path == null) {
            return null;
        }
        String[] tags = path.split(">");
        return tags[tags.length - 1];
    }

    public static String extractManifestId(String tag) {
        return tag.substring(tag.indexOf('-') + 1);
    }

    public String getTargetAccManifestIdBySourcePath(String sourcePath) {
        return targetAccManifestIdBySourcePathMap.get(sourcePath);
    }

    public String getTargetAsccpManifestIdBySourcePath(String sourcePath) {
        return targetAsccpManifestIdBySourcePathMap.get(sourcePath);
    }

    public String getTargetBccpManifestIdBySourcePath(String sourcePath) {
        return targetBccpManifestIdBySourcePathMap.get(sourcePath);
    }

    public BieUpliftingMapping getTargetAsccMappingBySourcePath(String sourcePath) {
        return targetAsccMappingMap.get(sourcePath);
    }

    public BieUpliftingMapping getTargetAsccMappingByTargetPath(String targetPath) {
        return targetAsccMappingByTargetPathMap.get(targetPath);
    }

    public BieUpliftingMapping getTargetBccMappingBySourcePath(String sourcePath) {
        return targetBccMappingMap.get(sourcePath);
    }

    public BieUpliftingMapping getTargetDtScMappingBySourcePath(String sourcePath) {
        return targetDtScMappingMap.get(sourcePath);
    }

    public List<BieUpliftingMapping> getMappingList() {
        return this.mappingList;
    }

}
