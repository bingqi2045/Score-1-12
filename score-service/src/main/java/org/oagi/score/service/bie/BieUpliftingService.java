package org.oagi.score.service.bie;

import org.oagi.score.repo.api.ScoreRepositoryFactory;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.bie.model.*;
import org.oagi.score.repo.api.corecomponent.model.*;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.oagi.score.repo.api.release.ReleaseReadRepository;
import org.oagi.score.repo.api.release.model.GetReleaseRequest;
import org.oagi.score.repo.api.release.model.Release;
import org.oagi.score.service.corecomponent.CcDocument;
import org.oagi.score.service.corecomponent.model.CcDocumentImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BieUpliftingService {

    @Autowired
    private ScoreRepositoryFactory scoreRepositoryFactory;

    @Autowired
    private BieReadService bieReadService;

    private class Association {

        private String parentPath;
        private CcAssociation ccAssociation;

        public Association(String parentPath, CcAssociation ccAssociation) {
            this.parentPath = parentPath;
            int ch = parentPath.lastIndexOf('>');
            if (ch >= 0) {
                if (!parentPath.substring(ch).contains("ACC")) {
                    throw new IllegalStateException();
                }
            }
            this.ccAssociation = ccAssociation;
        }

        public String getParentPath() {
            return parentPath;
        }

        public String getPath() {
            return parentPath + ">" + ((this.ccAssociation.isAscc()) ?
                    "ASCC-" + ((AsccManifest) this.ccAssociation).getAsccManifestId() :
                    "BCC-" + ((BccManifest) this.ccAssociation).getBccManifestId());
        }

        public CcAssociation getCcAssociation() {
            return ccAssociation;
        }

        public boolean isMatched(CcAssociation ccAssociation) {
            return this.ccAssociation.equals(ccAssociation);
        }
    }

    private class BbieAndBccp {

        private BigInteger bbieId;
        private BccpManifest bccpManifest;

        public BbieAndBccp(BigInteger bbieId, BccpManifest bccpManifest) {
            this.bbieId = bbieId;
            this.bccpManifest = bccpManifest;
        }

        public BigInteger getBbieId() {
            return bbieId;
        }

        public BccpManifest getBccpManifest() {
            return bccpManifest;
        }
    }

    private class ScoreResult<T> {

        private double score;
        private T obj;

        public ScoreResult(double score, T obj) {
            this.score = score;
            this.obj = obj;
        }

        public double getScore() {
            return score;
        }

        public T getObj() {
            return obj;
        }
    }

    private class BieDiff implements BieVisitor {

        private List<BieUpliftingDiffListener> listeners = new ArrayList();

        private BieDocument sourceBieDocument;
        private CcDocument targetCcDocument;
        private BigInteger targetAsccpManifestId;

        private Queue<AsccpManifest> targetAsccpManifestQueue = new LinkedBlockingQueue();
        private Queue<AccManifest> targetAccManifestQueue = new LinkedBlockingQueue();
        private Queue<BbieAndBccp> targetBccpManifestQueue = new LinkedBlockingQueue();

        private String currentSourcePath;
        private String currentTargetPath;

        private Map<BigInteger, List<Association>> abieSourceAssociationsMap = new HashMap();
        private Map<BigInteger, List<Association>> abieTargetAssociationsMap = new HashMap();
        private Map<BigInteger, List<DtScManifest>> bbieTargetDtScManifestsMap = new HashMap();


        BieDiff(BieDocument sourceBieDocument, CcDocument targetCcDocument,
                BigInteger targetAsccpManifestId) {
            this.sourceBieDocument = sourceBieDocument;
            this.targetCcDocument = targetCcDocument;
            this.targetAsccpManifestId = targetAsccpManifestId;
        }

        public void addListener(BieUpliftingDiffListener listener) {
            this.listeners.add(listener);
        }

        public void diff() {
            sourceBieDocument.accept(this);
        }

        @Override
        public void visitStart(TopLevelAsbiep topLevelAsbiep, BieVisitContext context) {
            AsccpManifest targetAsccpManifest = targetCcDocument.getAsccpManifest(targetAsccpManifestId);
            targetAsccpManifestQueue.offer(targetAsccpManifest);
        }

        @Override
        public void visitEnd(TopLevelAsbiep topLevelAsbiep, BieVisitContext context) {

        }

        @Override
        public void visitAbie(Abie abie, BieVisitContext context) {
            CcDocument sourceCcDocument = context.getBieDocument().getCcDocument();
            AccManifest sourceAccManifest = sourceCcDocument.getAccManifest(
                    abie.getBasedAccManifestId()
            );
            List<Association> sourceAssociations =
                    getAssociationsRegardingBases(currentSourcePath, sourceCcDocument, sourceAccManifest);
            abieSourceAssociationsMap.put(abie.getAbieId(), sourceAssociations);

            AccManifest targetAccManifest = targetAccManifestQueue.poll();
            if (targetAccManifest != null) { // found matched acc
                List<Association> targetAssociations =
                        getAssociationsRegardingBases(currentTargetPath, targetCcDocument, targetAccManifest);
                abieTargetAssociationsMap.put(abie.getAbieId(), targetAssociations);
            }
        }

        private List<Association> getAssociationsRegardingBases(String path, CcDocument ccDocument, AccManifest accManifest) {
            Stack<AccManifest> accManifestStack = new Stack();
            while (accManifest != null) {
                accManifestStack.push(accManifest);
                accManifest = ccDocument.getBasedAccManifest(accManifest);
            }

            List<Association> associations = new ArrayList();
            while (!accManifestStack.isEmpty()) {
                String parentPath = path + ">" + String.join(">", accManifestStack.stream()
                        .map(e -> "ACC-" + e.getAccManifestId()).collect(Collectors.toList()));
                accManifest = accManifestStack.pop();
                associations.addAll(getAssociationsRegardingGroup(parentPath, ccDocument, accManifest));
            }

            return associations;
        }

        private List<Association> getAssociationsRegardingGroup(String parentPath, CcDocument ccDocument, AccManifest accManifest) {
            Collection<CcAssociation> ccAssociations = ccDocument.getAssociations(accManifest);
            List<Association> associations = new ArrayList();
            for (CcAssociation ccAssociation : ccAssociations) {
                if (ccAssociation.isAscc()) {
                    AsccManifest asccManifest = (AsccManifest) ccAssociation;
                    AsccpManifest asccpManifest =
                            ccDocument.getAsccpManifest(asccManifest.getToAsccpManifestId());
                    AccManifest roleOfAccManifest =
                            ccDocument.getRoleOfAccManifest(asccpManifest);
                    Acc acc = ccDocument.getAcc(roleOfAccManifest);
                    if (acc.isGroup()) {
                        associations.addAll(
                                getAssociationsRegardingGroup(
                                        String.join(">",
                                                Arrays.asList(parentPath,
                                                        "ASCC-" + asccManifest.getAsccManifestId(),
                                                        "ASCCP-" + asccpManifest.getAsccpManifestId(),
                                                        "ACC-" + roleOfAccManifest.getAccManifestId())
                                        ),
                                        ccDocument, roleOfAccManifest)
                        );
                        continue;
                    }
                }

                associations.add(new Association(parentPath, ccAssociation));
            }

            return associations;
        }

        private ScoreResult<Association> score(CcDocument sourceCcDocument, CcDocument targetCcDocument,
                                               Association sourceAssociation, Association targetAssociation) {
            if (sourceAssociation.getCcAssociation().isAscc()) {
                Ascc sourceAscc = sourceCcDocument.getAscc((AsccManifest) sourceAssociation.getCcAssociation());
                Ascc targetAscc = targetCcDocument.getAscc((AsccManifest) targetAssociation.getCcAssociation());
                if (sourceAscc.getGuid().equals(targetAscc.getGuid())) {
                    return new ScoreResult(1.0d, targetAssociation);
                }
            } else {
                Bcc sourceBcc = sourceCcDocument.getBcc((BccManifest) sourceAssociation.getCcAssociation());
                Bcc targetBcc = targetCcDocument.getBcc((BccManifest) targetAssociation.getCcAssociation());
                if (sourceBcc.getGuid().equals(targetBcc.getGuid())) {
                    return new ScoreResult(1.0d, targetAssociation);
                }
            }
            return new ScoreResult(0.0d, targetAssociation);
        }

        private ScoreResult<DtScManifest> score(CcDocument sourceCcDocument, CcDocument targetCcDocument,
                                                DtScManifest sourceDtScManifest, DtScManifest targetDtScManifest) {
            DtSc sourceDtSc = sourceCcDocument.getDtSc(sourceDtScManifest);
            DtSc targetDtSc = targetCcDocument.getDtSc(targetDtScManifest);
            if (sourceDtSc.getGuid().equals(targetDtSc.getGuid())) {
                return new ScoreResult(1.0d, targetDtScManifest);
            }
            return new ScoreResult(0.0d, targetDtScManifest);
        }

        @Override
        public void visitAsbie(Asbie asbie, BieVisitContext context) {
            CcDocument sourceCcDocument = context.getBieDocument().getCcDocument();
            AsccManifest sourceAsccManifest = sourceCcDocument.getAsccManifest(asbie.getBasedAsccManifestId());
            List<Association> sourceAssociations =
                    abieSourceAssociationsMap.getOrDefault(asbie.getFromAbieId(), Collections.emptyList());
            Association sourceAssociation = sourceAssociations.stream()
                    .filter(e -> e.isMatched(sourceAsccManifest)).findAny().orElse(null);

            List<Association> targetAssociations =
                    abieTargetAssociationsMap.getOrDefault(asbie.getFromAbieId(), Collections.emptyList());
            Association targetAssociation = (Association) targetAssociations.stream().filter(e -> e.getCcAssociation().isAscc())
                    .map(e -> score(sourceCcDocument, targetCcDocument, sourceAssociation, e))
                    .max(Comparator.comparing(ScoreResult::getScore))
                    .orElse(new ScoreResult(0.0d, null)).getObj();

            if (targetAssociation == null) {
                this.listeners.forEach(listener -> {
                    listener.notFoundMatchedAsbie(asbie,
                            (AsccManifest) sourceAssociation.getCcAssociation(), sourceAssociation.getPath());
                });

                currentSourcePath = sourceAssociation.getPath();
                currentTargetPath = null;
            } else {
                this.listeners.forEach(listener -> {
                    listener.foundBestMatchedAsbie(asbie,
                            (AsccManifest) sourceAssociation.getCcAssociation(), sourceAssociation.getPath(),
                            (AsccManifest) targetAssociation.getCcAssociation(), targetAssociation.getPath());
                });

                currentSourcePath = sourceAssociation.getPath();
                currentTargetPath = targetAssociation.getPath();

                AsccpManifest toAsccpManifest = targetCcDocument.getAsccpManifest(
                        ((AsccManifest) targetAssociation.getCcAssociation()).getToAsccpManifestId());
                targetAsccpManifestQueue.offer(toAsccpManifest);
            }
        }

        @Override
        public void visitBbie(Bbie bbie, BieVisitContext context) {
            CcDocument sourceCcDocument = context.getBieDocument().getCcDocument();
            BccManifest sourceBccManifest = sourceCcDocument.getBccManifest(bbie.getBasedBccManifestId());
            List<Association> sourceAssociations =
                    abieSourceAssociationsMap.getOrDefault(bbie.getFromAbieId(), Collections.emptyList());
            Association sourceAssociation = sourceAssociations.stream()
                    .filter(e -> e.isMatched(sourceBccManifest)).findAny().orElse(null);

            List<Association> targetAssociations =
                    abieTargetAssociationsMap.getOrDefault(bbie.getFromAbieId(), Collections.emptyList());
            Association targetAssociation = (Association) targetAssociations.stream().filter(e -> e.getCcAssociation().isBcc())
                    .map(e -> score(sourceCcDocument, targetCcDocument, sourceAssociation, e))
                    .max(Comparator.comparing(ScoreResult::getScore))
                    .orElse(new ScoreResult(0.0d, null)).getObj();

            if (targetAssociation == null) {
                this.listeners.forEach(listener -> {
                    listener.notFoundMatchedBbie(bbie,
                            (BccManifest) sourceAssociation.getCcAssociation(), sourceAssociation.getPath());
                });

                currentSourcePath = sourceAssociation.getPath();
                currentTargetPath = null;
            } else {
                this.listeners.forEach(listener -> {
                    listener.foundBestMatchedBbie(bbie,
                            (BccManifest) sourceAssociation.getCcAssociation(), sourceAssociation.getPath(),
                            (BccManifest) targetAssociation.getCcAssociation(), targetAssociation.getPath());
                });

                currentSourcePath = sourceAssociation.getPath();
                currentTargetPath = targetAssociation.getPath();

                BccpManifest toBccpManifest = targetCcDocument.getBccpManifest(
                        ((BccManifest) targetAssociation.getCcAssociation()).getToBccpManifestId());
                targetBccpManifestQueue.offer(new BbieAndBccp(bbie.getBbieId(), toBccpManifest));
            }
        }

        @Override
        public void visitAsbiep(Asbiep asbiep, BieVisitContext context) {
            CcDocument sourceCcDocument = context.getBieDocument().getCcDocument();
            AsccpManifest sourceAsccpManifest = sourceCcDocument.getAsccpManifest(
                    asbiep.getBasedAsccpManifestId());

            currentSourcePath = (StringUtils.hasLength(currentSourcePath)) ?
                    currentSourcePath + ">" + "ASCCP-" + sourceAsccpManifest.getAsccpManifestId() :
                    "ASCCP-" + sourceAsccpManifest.getAsccpManifestId();

            AsccpManifest targetAsccpManifest = targetAsccpManifestQueue.poll();
            if (targetAsccpManifest != null) { // found matched asccp
                AccManifest targetRoleOfAccManifest = targetCcDocument.getRoleOfAccManifest(targetAsccpManifest);
                targetAccManifestQueue.offer(targetRoleOfAccManifest);
                currentTargetPath = (StringUtils.hasLength(currentTargetPath)) ?
                        currentTargetPath + ">" + "ASCCP-" + targetAsccpManifest.getAsccpManifestId() :
                        "ASCCP-" + targetAsccpManifest.getAsccpManifestId();
            }
        }

        @Override
        public void visitBbiep(Bbiep bbiep, BieVisitContext context) {
            CcDocument sourceCcDocument = context.getBieDocument().getCcDocument();
            BccpManifest sourceBccpManifest = sourceCcDocument.getBccpManifest(
                    bbiep.getBasedBccpManifestId());
            DtManifest sourceDtManifest = sourceCcDocument.getDtManifest(
                    sourceBccpManifest.getBdtManifestId());
            currentSourcePath = currentSourcePath + ">" + "BCCP-" + sourceBccpManifest.getBccpManifestId() + ">" +
                    "BDT-" + sourceDtManifest.getDtManifestId();

            BbieAndBccp targetBccpManifest = targetBccpManifestQueue.poll();
            if (targetBccpManifest != null) {
                BigInteger targetBdtManifestId = targetBccpManifest.getBccpManifest().getBdtManifestId();
                DtManifest targetDtManifest = targetCcDocument.getDtManifest(targetBdtManifestId);
                bbieTargetDtScManifestsMap.put(targetBccpManifest.getBbieId(),
                        targetCcDocument.getDtScManifests(targetDtManifest));
                currentTargetPath = currentTargetPath + ">" + "BCCP-" + targetBdtManifestId + ">" +
                        "BDT-" + targetDtManifest.getDtManifestId();
            }
        }

        @Override
        public void visitBbieSc(BbieSc bbieSc, BieVisitContext context) {
            CcDocument sourceCcDocument = context.getBieDocument().getCcDocument();
            DtScManifest sourceDtScManifest = sourceCcDocument.getDtScManifest(bbieSc.getBasedDtScManifestId());

            String sourcePath = currentSourcePath + ">" + "BDT_SC-" + sourceDtScManifest.getDtScManifestId();
            DtScManifest targetDtScManifest =
                    (DtScManifest) bbieTargetDtScManifestsMap.getOrDefault(bbieSc.getBbieId(), Collections.emptyList()).stream()
                            .map(e -> score(sourceCcDocument, targetCcDocument, sourceDtScManifest, e))
                            .max(Comparator.comparing(ScoreResult::getScore))
                            .orElse(new ScoreResult(0.0d, null)).getObj();

            if (targetDtScManifest == null) {
                this.listeners.forEach(listener -> {
                    listener.notFoundMatchedBbieSc(bbieSc, sourceDtScManifest, sourcePath);
                });
            } else {
                String targetPath = currentTargetPath + ">" + "BDT_SC-" + targetDtScManifest.getDtScManifestId();

                this.listeners.forEach(listener -> {
                    listener.foundBestMatchedBbieSc(bbieSc,
                            sourceDtScManifest, sourcePath,
                            targetDtScManifest, targetPath);
                });
            }
        }
    }

    public AnalysisBieUpliftingResponse analysisBieUplifting(AnalysisBieUpliftingRequest request) {
        AnalysisBieUpliftingResponse response = new AnalysisBieUpliftingResponse();

        ReleaseReadRepository releaseReadRepository = scoreRepositoryFactory.createReleaseReadRepository();
        Release sourceRelease = releaseReadRepository.getRelease(new GetReleaseRequest(request.getRequester())
                .withTopLevelAsbiepId(request.getTopLevelAsbiepId()))
                .getRelease();
        Release targetRelease = releaseReadRepository.getRelease(new GetReleaseRequest(request.getRequester())
                .withReleaseId(request.getTargetReleaseId()))
                .getRelease();

        if (sourceRelease.compareTo(targetRelease) >= 0) {
            throw new IllegalArgumentException();
        }

        BieDocument sourceBieDocument = bieReadService.getBieDocument(request.getRequester(), request.getTopLevelAsbiepId());
        BigInteger targetAsccpManifestId = scoreRepositoryFactory.createCcReadRepository().findNextAsccpManifest(
                new FindNextAsccpManifestRequest(request.getRequester())
                        .withAsccpManifestId(sourceBieDocument.getRootAsbiep().getBasedAsccpManifestId())
                        .withNextReleaseId(request.getTargetReleaseId()))
                .getNextAsccpManifestId();
        if (targetAsccpManifestId == null) {
            throw new ScoreDataAccessException("Unable to find the target ASCCP.");
        }

        CcDocument targetCcDocument = new CcDocumentImpl(scoreRepositoryFactory.createCcReadRepository()
                .getCcPackage(new GetCcPackageRequest(request.getRequester())
                        .withAsccpManifestId(targetAsccpManifestId))
                .getCcPackage());

        BieDiff bieDiff = new BieDiff(sourceBieDocument, targetCcDocument, targetAsccpManifestId);
        bieDiff.addListener(response);
        bieDiff.diff();

        return response;
    }
}
