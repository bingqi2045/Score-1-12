package org.oagi.score.gateway.http.api.release_management.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.*;

@Data
public class MigrationMetadata {

    private String version = "1.0";
    private String maintainer;
    private String maintainerEmail;
    private String description;

    private BigInteger delimiterId = BigInteger.valueOf(1000000);

    private BigInteger targetReleaseId = BigInteger.ZERO;
    private Map<BigInteger, String> releaseIdNumMap = new HashMap();

    private BigInteger maxAccId = BigInteger.ZERO;
    private BigInteger maxAccManifestId = BigInteger.ZERO;

    private BigInteger maxAsccId = BigInteger.ZERO;
    private BigInteger maxAsccManifestId = BigInteger.ZERO;

    private BigInteger maxBccId = BigInteger.ZERO;
    private BigInteger maxBccManifestId = BigInteger.ZERO;

    private BigInteger maxAsccpId = BigInteger.ZERO;
    private BigInteger maxAsccpManifestId = BigInteger.ZERO;

    private BigInteger maxBccpId = BigInteger.ZERO;
    private BigInteger maxBccpManifestId = BigInteger.ZERO;

    private BigInteger maxDtId = BigInteger.ZERO;
    private BigInteger maxDtManifestId = BigInteger.ZERO;

    private BigInteger maxDtScId = BigInteger.ZERO;
    private BigInteger maxDtScManifestId = BigInteger.ZERO;

    private BigInteger maxCodeListId = BigInteger.ZERO;
    private BigInteger maxCodeListManifestId = BigInteger.ZERO;

    private BigInteger maxCodeListValueId = BigInteger.ZERO;
    private BigInteger maxCodeListValueManifestId = BigInteger.ZERO;

    private BigInteger maxAgencyIdListId = BigInteger.ZERO;
    private BigInteger maxAgencyIdListManifestId = BigInteger.ZERO;

    private BigInteger maxAgencyIdListValueId = BigInteger.ZERO;
    private BigInteger maxAgencyIdListValueManifestId = BigInteger.ZERO;

    private BigInteger maxXbtId = BigInteger.ZERO;
    private BigInteger maxXbtManifestId = BigInteger.ZERO;

    private BigInteger maxLogId = BigInteger.ZERO;
    private BigInteger maxSeqKeyId = BigInteger.ZERO;
    private BigInteger maxNamespaceId = BigInteger.ZERO;

    private BigInteger maxModuleId = BigInteger.ZERO;
    private BigInteger maxModuleSetId = BigInteger.ZERO;
    private BigInteger maxModuleSetReleaseId = BigInteger.ZERO;

    private BigInteger maxModuleAccManifestId = BigInteger.ZERO;
    private BigInteger maxModuleAsccpManifestId = BigInteger.ZERO;
    private BigInteger maxModuleBccpManifestId = BigInteger.ZERO;
    private BigInteger maxModuleDtManifestId = BigInteger.ZERO;
    private BigInteger maxModuleCodeListManifestId = BigInteger.ZERO;
    private BigInteger maxModuleAgencyIdListManifestId = BigInteger.ZERO;
    private BigInteger maxModuleXbtManifestId = BigInteger.ZERO;
    private BigInteger maxModuleBlobContentManifestId = BigInteger.ZERO;

    public void addRelease(BigInteger releaseId, String releaseNum) {
        this.releaseIdNumMap.put(releaseId, releaseNum);
    }

    public void setMaxLogId(BigInteger maxLogId) {
        if (this.maxLogId.compareTo(maxLogId) > 0) {
            return;
        }
        this.maxLogId = maxLogId;
    }

    public void setMaxSeqKeyId(BigInteger maxSeqKeyId) {
        if (this.maxSeqKeyId.compareTo(maxSeqKeyId) > 0) {
            return;
        }
        this.maxSeqKeyId = maxSeqKeyId;
    }

    public String getTargetReleaseNum() {
        return releaseIdNumMap.get(getTargetReleaseId());
    }

    public String getScriptFileName() {
        String prevReleaseNum = getPrevReleaseNum();
        String targetReleaseNum = getTargetReleaseNum();
        return "mig_" + prevReleaseNum.replaceAll("\\.", "_") + "_to_" + targetReleaseNum.replaceAll("\\.", "_") + ".sql";
    }

    public String getPrevReleaseNum() {
        List<BigInteger> releaseIdList = new ArrayList(this.releaseIdNumMap.keySet());
        releaseIdList.remove(this.targetReleaseId);
        releaseIdList.sort(Comparator.reverseOrder());
        BigInteger prevReleaseId = releaseIdList.get(0);
        return this.releaseIdNumMap.get(prevReleaseId);
    }
}
