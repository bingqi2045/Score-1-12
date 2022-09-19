package org.oagi.score.service.corecomponent.model;

import org.oagi.score.repo.api.corecomponent.model.*;
import org.oagi.score.service.corecomponent.CcDocument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CcDocumentImpl implements CcDocument {

    private CcPackage ccPackage;

    private Map<String, AccManifest> accManifestMap;
    private Map<String, Acc> accMap;
    private Map<String, AsccManifest> asccManifestMap;
    private Map<String, Ascc> asccMap;
    private Map<String, BccManifest> bccManifestMap;
    private Map<String, Bcc> bccMap;
    private Map<String, List<CcAssociationSequence>> sequenceMap;
    private Map<String, AsccpManifest> asccpManifestMap;
    private Map<String, Asccp> asccpMap;
    private Map<String, BccpManifest> bccpManifestMap;
    private Map<String, Bccp> bccpMap;
    private Map<String, DtManifest> dtManifestMap;
    private Map<String, Dt> dtMap;
    private Map<String, DtScManifest> dtScManifestMap;
    private Map<String, DtSc> dtScMap;
    private Map<String, List<DtScManifest>> dtScManifestByDtManifestMap;
    private Map<String, List<BdtPriRestri>> bdtPriRestriByDtMap;
    private Map<String, List<BdtScPriRestri>> bdtScPriRestriByDtScMap;

    public CcDocumentImpl(CcPackage ccPackage) {
        this.accManifestMap = ccPackage.getAccManifestList().stream()
                .collect(Collectors.toMap(AccManifest::getAccManifestId, Function.identity()));
        this.accMap = ccPackage.getAccList().stream()
                .collect(Collectors.toMap(Acc::getAccId, Function.identity()));
        this.asccManifestMap = ccPackage.getAsccManifestList().stream()
                .collect(Collectors.toMap(AsccManifest::getAsccManifestId, Function.identity()));
        this.asccMap = ccPackage.getAsccList().stream()
                .collect(Collectors.toMap(Ascc::getAsccId, Function.identity()));
        this.bccManifestMap = ccPackage.getBccManifestList().stream()
                .collect(Collectors.toMap(BccManifest::getBccManifestId, Function.identity()));
        this.bccMap = ccPackage.getBccList().stream()
                .collect(Collectors.toMap(Bcc::getBccId, Function.identity()));
        this.sequenceMap = ccPackage.getSequenceList().stream()
                .collect(Collectors.groupingBy(CcAssociationSequence::getFromAccManifestId));
        this.asccpManifestMap = ccPackage.getAsccpManifestList().stream()
                .collect(Collectors.toMap(AsccpManifest::getAsccpManifestId, Function.identity()));
        this.asccpMap = ccPackage.getAsccpList().stream()
                .collect(Collectors.toMap(Asccp::getAsccpId, Function.identity()));
        this.bccpManifestMap = ccPackage.getBccpManifestList().stream()
                .collect(Collectors.toMap(BccpManifest::getBccpManifestId, Function.identity()));
        this.bccpMap = ccPackage.getBccpList().stream()
                .collect(Collectors.toMap(Bccp::getBccpId, Function.identity()));
        this.dtManifestMap = ccPackage.getDtManifestList().stream()
                .collect(Collectors.toMap(DtManifest::getDtManifestId, Function.identity()));
        this.dtMap = ccPackage.getDtList().stream()
                .collect(Collectors.toMap(Dt::getDtId, Function.identity()));
        this.dtScManifestMap = ccPackage.getDtScManifestList().stream()
                .collect(Collectors.toMap(DtScManifest::getDtScManifestId, Function.identity()));
        this.dtScMap = ccPackage.getDtScList().stream()
                .collect(Collectors.toMap(DtSc::getDtScId, Function.identity()));
        this.dtScManifestByDtManifestMap = ccPackage.getDtScManifestList().stream()
                .collect(Collectors.groupingBy(DtScManifest::getOwnerDtManifestId));
        this.bdtPriRestriByDtMap = ccPackage.getBdtPriRestriList().stream()
                .collect(Collectors.groupingBy(BdtPriRestri::getBdtId));
        this.bdtScPriRestriByDtScMap = ccPackage.getBdtScPriRestriList().stream()
                .collect(Collectors.groupingBy(BdtScPriRestri::getBdtScId));
    }

    @Override
    public AccManifest getAccManifest(String accManifestId) {
        return (accManifestId != null) ? accManifestMap.get(accManifestId) : null;
    }

    @Override
    public Acc getAcc(AccManifest accManifest) {
        if (accManifest == null) {
            return null;
        }

        return accMap.get(accManifest.getAccId());
    }

    @Override
    public AccManifest getRoleOfAccManifest(AsccpManifest asccpManifest) {
        if (asccpManifest == null) {
            return null;
        }

        return getAccManifest(asccpManifest.getRoleOfAccManifestId());
    }

    @Override
    public AccManifest getBasedAccManifest(AccManifest accManifest) {
        if (accManifest == null) {
            return null;
        }

        return getAccManifest(accManifest.getBasedAccManifestId());
    }

    @Override
    public List<CcAssociation> getAssociations(AccManifest accManifest) {
        if (accManifest == null) {
            return Collections.emptyList();
        }
        List<CcAssociation> associations = new ArrayList();
        sort(this.sequenceMap.get(accManifest.getAccManifestId())).forEach(sequence -> {
            if (sequence.getAsccManifestId() != null) {
                associations.add(this.asccManifestMap.get(sequence.getAsccManifestId()));
            } else if (sequence.getBccManifestId() != null) {
                associations.add(this.bccManifestMap.get(sequence.getBccManifestId()));
            }
        });

        return associations;
    }

    @Override
    public AsccManifest getAsccManifest(String asccManifestId) {
        return (asccManifestId != null) ? asccManifestMap.get(asccManifestId) : null;
    }

    @Override
    public Ascc getAscc(AsccManifest asccManifest) {
        if (asccManifest == null) {
            return null;
        }

        return asccMap.get(asccManifest.getAsccId());
    }

    @Override
    public BccManifest getBccManifest(String bccManifestId) {
        return (bccManifestId != null) ? bccManifestMap.get(bccManifestId) : null;
    }

    @Override
    public Bcc getBcc(BccManifest bccManifest) {
        if (bccManifest == null) {
            return null;
        }

        return bccMap.get(bccManifest.getBccId());
    }

    private List<CcAssociationSequence> sort(List<CcAssociationSequence> sequences) {
        if (sequences == null || sequences.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, CcAssociationSequence> sequenceMap =
                sequences.stream().collect(Collectors.toMap(CcAssociationSequence::getSeqKeyId, Function.identity()));

        List<CcAssociationSequence> sorted = new ArrayList(sequences.size());
        CcAssociationSequence sequence = sequences.stream().filter(e -> e.getPrevSeqKeyId() == null).findFirst().get();
        sorted.add(sequence);

        while (sequence.getNextSeqKeyId() != null) {
            sequence = sequenceMap.get(sequence.getNextSeqKeyId());
            sorted.add(sequence);
        }

        return sorted;
    }

    @Override
    public AsccpManifest getAsccpManifest(String asccpManifestId) {
        return (asccpManifestId != null) ? asccpManifestMap.get(asccpManifestId) : null;
    }

    @Override
    public Asccp getAsccp(AsccpManifest asccpManifest) {
        if (asccpManifest == null) {
            return null;
        }

        return asccpMap.get(asccpManifest.getAsccpId());
    }

    @Override
    public BccpManifest getBccpManifest(String bccpManifestId) {
        return (bccpManifestId != null) ? bccpManifestMap.get(bccpManifestId) : null;
    }

    @Override
    public Bccp getBccp(BccpManifest bccpManifest) {
        if (bccpManifest == null) {
            return null;
        }

        return bccpMap.get(bccpManifest.getBccpId());
    }

    @Override
    public DtManifest getDtManifest(String dtManifestId) {
        return (dtManifestId != null) ? dtManifestMap.get(dtManifestId) : null;
    }

    @Override
    public Dt getDt(DtManifest dtManifest) {
        if (dtManifest == null) {
            return null;
        }

        return dtMap.get(dtManifest.getDtId());
    }

    @Override
    public DtScManifest getDtScManifest(String dtScManifestId) {
        return (dtScManifestId != null) ? dtScManifestMap.get(dtScManifestId) : null;
    }

    @Override
    public DtSc getDtSc(DtScManifest dtScManifest) {
        if (dtScManifest == null) {
            return null;
        }

        return dtScMap.get(dtScManifest.getDtScId());
    }

    @Override
    public List<DtScManifest> getDtScManifests(DtManifest dtManifest) {
        if (dtManifest == null) {
            return null;
        }

        return dtScManifestByDtManifestMap.getOrDefault(dtManifest.getDtManifestId(), Collections.emptyList());
    }

    @Override
    public List<BdtPriRestri> getBdtPriRestriList(Dt bdt) {
        if (bdt == null) {
            return Collections.emptyList();
        }

        return bdtPriRestriByDtMap.getOrDefault(bdt.getDtId(), Collections.emptyList());
    }

    @Override
    public List<BdtScPriRestri> getBdtScPriRestriList(DtSc bdtSc) {
        if (bdtSc == null) {
            return Collections.emptyList();
        }

        return bdtScPriRestriByDtScMap.getOrDefault(bdtSc.getDtScId(), Collections.emptyList());
    }
}
