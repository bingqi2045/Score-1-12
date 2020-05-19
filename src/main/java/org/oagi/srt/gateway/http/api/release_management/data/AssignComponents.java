package org.oagi.srt.gateway.http.api.release_management.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@Data
public class AssignComponents {

    private Map<BigInteger, String> assignableAccManifestMap = new HashMap();
    private Map<BigInteger, String> assignableAsccpManifestMap = new HashMap();
    private Map<BigInteger, String> assignableBccpManifestMap = new HashMap();

    private Map<BigInteger, String> unassignableAccManifestMap = new HashMap();
    private Map<BigInteger, String> unassignableAsccpManifestMap = new HashMap();
    private Map<BigInteger, String> unassignableBccpManifestMap = new HashMap();

    public void addAssignableAccManifest(BigInteger accManifestId, String accDisplayName) {
        assignableAccManifestMap.put(accManifestId, accDisplayName);
    }

    public void addAssignableAsccpManifest(BigInteger asccpManifestId, String asccpDisplayName) {
        assignableAsccpManifestMap.put(asccpManifestId, asccpDisplayName);
    }

    public void addAssignableBccpManifest(BigInteger bccpManifestId, String bccpDisplayName) {
        assignableBccpManifestMap.put(bccpManifestId, bccpDisplayName);
    }

    public void addUnassignableAccManifest(BigInteger accManifestId, String accDisplayName) {
        unassignableAccManifestMap.put(accManifestId, accDisplayName);
    }

    public void addUnassignableAsccpManifest(BigInteger asccpManifestId, String asccpDisplayName) {
        unassignableAsccpManifestMap.put(asccpManifestId, asccpDisplayName);
    }

    public void addUnassignableBccpManifest(BigInteger bccpManifestId, String bccpDisplayName) {
        unassignableBccpManifestMap.put(bccpManifestId, bccpDisplayName);
    }
}
