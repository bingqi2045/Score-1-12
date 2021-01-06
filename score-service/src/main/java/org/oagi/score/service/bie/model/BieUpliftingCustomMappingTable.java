package org.oagi.score.service.bie.model;

import java.math.BigInteger;

public class BieUpliftingCustomMappingTable {

    public interface BieUpliftingMapping {
        String getType();
        BigInteger getBieId();
        BigInteger getSourceManifestId();
        String getSourcePath();
        BigInteger getTargetManifestId();
        String getTargetPath();
    }

    public BigInteger getTargetAccManifestIdBySourcePath(String sourcePath) {
        return null;
    }

    public BigInteger getTargetAsccpManifestIdBySourcePath(String sourcePath) {
        return null;
    }

    public BigInteger getTargetBccpManifestIdBySourcePath(String sourcePath) {
        return null;
    }

    public BieUpliftingMapping getTargetAsccMappingBySourcePath(String sourcePath) {
        return null;
    }

    public BieUpliftingMapping getTargetBccMappingBySourcePath(String sourcePath) {
        return null;
    }

    public BieUpliftingMapping getTargetDtScMappingBySourcePath(String sourcePath) {
        return null;
    }

}
