package org.oagi.score.service.corecomponent;

import org.oagi.score.repo.api.corecomponent.model.*;

import java.math.BigInteger;
import java.util.List;

public interface CcDocument {

    AccManifest getAccManifest(String accManifestId);

    Acc getAcc(AccManifest accManifest);

    AccManifest getRoleOfAccManifest(AsccpManifest asccpManifest);

    AccManifest getBasedAccManifest(AccManifest accManifest);

    List<CcAssociation> getAssociations(AccManifest accManifest);

    AsccManifest getAsccManifest(String asccManifestId);

    Ascc getAscc(AsccManifest asccManifest);

    BccManifest getBccManifest(String bccManifestId);

    Bcc getBcc(BccManifest bccManifest);

    AsccpManifest getAsccpManifest(String asccpManifestId);

    Asccp getAsccp(AsccpManifest asccpManifest);

    BccpManifest getBccpManifest(String bccpManifestId);

    Bccp getBccp(BccpManifest bccpManifest);

    DtManifest getDtManifest(String dtManifestId);

    Dt getDt(DtManifest dtManifest);

    DtScManifest getDtScManifest(String dtScManifestId);

    DtSc getDtSc(DtScManifest dtScManifest);

    List<DtScManifest> getDtScManifests(DtManifest dtManifest);

    List<BdtPriRestri> getBdtPriRestriList(Dt bdt);

    List<BdtScPriRestri> getBdtScPriRestriList(DtSc bdtSc);

}
