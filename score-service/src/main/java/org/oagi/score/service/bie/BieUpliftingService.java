package org.oagi.score.service.bie;

import org.oagi.score.repo.api.ScoreRepositoryFactory;
import org.oagi.score.repo.api.base.ScoreDataAccessException;
import org.oagi.score.repo.api.bie.model.*;
import org.oagi.score.repo.api.corecomponent.CodeListReadRepository;
import org.oagi.score.repo.api.corecomponent.model.*;
import org.oagi.score.repo.api.impl.jooq.utils.ScoreGuidUtils;
import org.oagi.score.repo.api.release.ReleaseReadRepository;
import org.oagi.score.repo.api.release.model.GetReleaseRequest;
import org.oagi.score.repo.api.release.model.Release;
import org.oagi.score.repo.api.user.model.ScoreUser;
import org.oagi.score.service.bie.model.BieDocumentBuilder;
import org.oagi.score.service.bie.model.BieUpliftingCustomMappingTable;
import org.oagi.score.service.bie.model.BieUpliftingMapping;
import org.oagi.score.service.bie.model.BieUpliftingValidation;
import org.oagi.score.service.corecomponent.CcDocument;
import org.oagi.score.service.corecomponent.CcMatchingService;
import org.oagi.score.service.corecomponent.model.CcDocumentImpl;
import org.oagi.score.service.corecomponent.model.CcMatchingScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.oagi.score.repo.api.impl.jooq.utils.ScoreDigestUtils.sha256;
import static org.oagi.score.repo.api.impl.utils.StringUtils.hasLength;
import static org.oagi.score.service.bie.model.BieUpliftingCustomMappingTable.extractManifestId;
import static org.oagi.score.service.bie.model.BieUpliftingCustomMappingTable.getLastTag;

@Service
@Transactional(readOnly = true)
public class BieUpliftingService {

    @Autowired
    private ScoreRepositoryFactory scoreRepositoryFactory;

    @Autowired
    private BieReadService bieReadService;

    @Autowired
    private CcMatchingService ccMatchingService;

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

    private class BieDiff implements BieVisitor {

        private List<BieUpliftingListener> listeners = new ArrayList();

        private BieDocument sourceBieDocument;
        private CcDocument targetCcDocument;
        private BigInteger targetAsccpManifestId;

        private Queue<AsccpManifest> targetAsccpManifestQueue = new LinkedBlockingQueue();
        private Queue<AccManifest> targetAccManifestQueue = new LinkedBlockingQueue();
        private Bbie previousBbie;
        private Queue<BccpManifest> targetBccpManifestQueue = new LinkedBlockingQueue();

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

        public void addListener(BieUpliftingListener listener) {
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
            // reused bie case;
            if (abie == null) {
                return;
            }
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
            CcMatchingScore matchingScore = targetAssociations.stream().filter(e -> e.getCcAssociation().isAscc())
                    .map(e -> ccMatchingService.score(
                            sourceCcDocument,
                            sourceAssociation,
                            targetCcDocument,
                            e,
                            (ccDocument, association) -> ccDocument.getAscc((AsccManifest) association.getCcAssociation())))
                    .max(Comparator.comparing(CcMatchingScore::getScore))
                    .orElse(new CcMatchingScore(0.0d, null, null));

            if (matchingScore.getScore() == 0.0d || matchingScore.getTarget() == null) {
                this.listeners.forEach(listener -> {
                    listener.notFoundMatchedAsbie(asbie,
                            (AsccManifest) sourceAssociation.getCcAssociation(), sourceAssociation.getPath());
                });

                currentSourcePath = sourceAssociation.getPath();
                currentTargetPath = null;
            } else {
                Association targetAssociation = (Association) matchingScore.getTarget();
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
            CcMatchingScore matchingScore = targetAssociations.stream().filter(e -> e.getCcAssociation().isBcc())
                    .map(e -> ccMatchingService.score(
                            sourceCcDocument,
                            sourceAssociation,
                            targetCcDocument,
                            e,
                            (ccDocument, association) -> ccDocument.getBcc((BccManifest) association.getCcAssociation())))
                    .max(Comparator.comparing(CcMatchingScore::getScore))
                    .orElse(new CcMatchingScore(0.0d, null, null));

            if (matchingScore.getScore() == 0.0d || matchingScore.getTarget() == null) {
                this.listeners.forEach(listener -> {
                    listener.notFoundMatchedBbie(bbie,
                            (BccManifest) sourceAssociation.getCcAssociation(), sourceAssociation.getPath());
                });

                currentSourcePath = sourceAssociation.getPath();
                currentTargetPath = null;
            } else {
                Association targetAssociation = (Association) matchingScore.getTarget();
                this.listeners.forEach(listener -> {
                    listener.foundBestMatchedBbie(bbie,
                            (BccManifest) sourceAssociation.getCcAssociation(), sourceAssociation.getPath(),
                            (BccManifest) targetAssociation.getCcAssociation(), targetAssociation.getPath());
                });

                currentSourcePath = sourceAssociation.getPath();
                currentTargetPath = targetAssociation.getPath();

                BccpManifest toBccpManifest = targetCcDocument.getBccpManifest(
                        ((BccManifest) targetAssociation.getCcAssociation()).getToBccpManifestId());
                this.previousBbie = bbie;
                targetBccpManifestQueue.offer(toBccpManifest);
            }
        }

        @Override
        public void visitAsbiep(Asbiep asbiep, BieVisitContext context) {
            CcDocument sourceCcDocument = context.getBieDocument().getCcDocument();
            // reused bie case;
            if (asbiep == null) {
                return;
            }
            AsccpManifest sourceAsccpManifest = sourceCcDocument.getAsccpManifest(
                    asbiep.getBasedAsccpManifestId());

            currentSourcePath = (hasLength(currentSourcePath)) ?
                    currentSourcePath + ">" + "ASCCP-" + sourceAsccpManifest.getAsccpManifestId() :
                    "ASCCP-" + sourceAsccpManifest.getAsccpManifestId();

            AsccpManifest targetAsccpManifest = targetAsccpManifestQueue.poll();
            if (targetAsccpManifest != null) { // found matched asccp
                AccManifest targetRoleOfAccManifest = targetCcDocument.getRoleOfAccManifest(targetAsccpManifest);
                targetAccManifestQueue.offer(targetRoleOfAccManifest);
                currentTargetPath = (hasLength(currentTargetPath)) ?
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

            BccpManifest targetBccpManifest = targetBccpManifestQueue.poll();
            if (targetBccpManifest != null) {
                BigInteger targetBdtManifestId = targetBccpManifest.getBdtManifestId();
                DtManifest targetDtManifest = targetCcDocument.getDtManifest(targetBdtManifestId);
                bbieTargetDtScManifestsMap.put(previousBbie.getBbieId(),
                        targetCcDocument.getDtScManifests(targetDtManifest));
                currentTargetPath = currentTargetPath + ">" + "BCCP-" + targetBccpManifest.getBccpManifestId() + ">" +
                        "BDT-" + targetDtManifest.getDtManifestId();
            }
        }

        @Override
        public void visitBbieSc(BbieSc bbieSc, BieVisitContext context) {
            CcDocument sourceCcDocument = context.getBieDocument().getCcDocument();
            DtScManifest sourceDtScManifest = sourceCcDocument.getDtScManifest(bbieSc.getBasedDtScManifestId());

            String sourcePath = currentSourcePath + ">" + "BDT_SC-" + sourceDtScManifest.getDtScManifestId();
            CcMatchingScore matchingScore =
                    bbieTargetDtScManifestsMap.getOrDefault(bbieSc.getBbieId(), Collections.emptyList()).stream()
                            .map(e -> ccMatchingService.score(
                                    sourceCcDocument,
                                    sourceDtScManifest,
                                    targetCcDocument,
                                    e,
                                    (ccDocument, dtScManifest) -> ccDocument.getDtSc(dtScManifest)))
                            .max(Comparator.comparing(CcMatchingScore::getScore))
                            .orElse(new CcMatchingScore(0.0d, null, null));

            if (matchingScore.getScore() == 0.0d || matchingScore.getTarget() == null) {
                this.listeners.forEach(listener -> {
                    listener.notFoundMatchedBbieSc(bbieSc, sourceDtScManifest, sourcePath);
                });
            } else {
                DtScManifest targetDtScManifest = (DtScManifest) matchingScore.getTarget();
                String targetPath = currentTargetPath + ">" + "BDT_SC-" + targetDtScManifest.getDtScManifestId();

                this.listeners.forEach(listener -> {
                    listener.foundBestMatchedBbieSc(bbieSc,
                            sourceDtScManifest, sourcePath,
                            targetDtScManifest, targetPath);
                });
            }
        }
    }

    public FindTargetAsccpManifestResponse findTargetAsccpManifest(FindTargetAsccpManifestRequest request) {
        FindTargetAsccpManifestResponse response = new FindTargetAsccpManifestResponse();

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

        response.setAsccpManifestId(targetAsccpManifestId);
        if (request.isIncludingBieDocument()) {
            response.setBieDocument(sourceBieDocument);
        }
        return response;
    }

    public AnalysisBieUpliftingResponse analysisBieUplifting(AnalysisBieUpliftingRequest request) {
        AnalysisBieUpliftingResponse response = new AnalysisBieUpliftingResponse();

        FindTargetAsccpManifestResponse targetAsccpManifestResponse = findTargetAsccpManifest(
                new FindTargetAsccpManifestRequest(request.getRequester())
                .withTopLevelAsbiepId(request.getTopLevelAsbiepId())
                .withTargetReleaseId(request.getTargetReleaseId())
                .withIncludingBieDocument(true));

        BieDocument sourceBieDocument = targetAsccpManifestResponse.getBieDocument();
        BigInteger targetAsccpManifestId = targetAsccpManifestResponse.getAsccpManifestId();

        CcDocument targetCcDocument = new CcDocumentImpl(scoreRepositoryFactory.createCcReadRepository()
                .getCcPackage(new GetCcPackageRequest(request.getRequester())
                        .withAsccpManifestId(targetAsccpManifestId))
                .getCcPackage());

        BieDiff bieDiff = new BieDiff(sourceBieDocument, targetCcDocument, targetAsccpManifestId);
        bieDiff.addListener(response);
        bieDiff.diff();

        return response;
    }

    private class BieUpliftingHandler implements BieVisitor {

        private ScoreUser requester;
        private List<BigInteger> bizCtxIds;
        private BieUpliftingCustomMappingTable customMappingTable;

        private BieDocument sourceBieDocument;
        private CcDocument targetCcDocument;
        private BigInteger targetAsccpManifestId;

        private Queue<AsccpManifest> targetAsccpManifestQueue = new LinkedBlockingQueue();
        private Queue<AccManifest> targetAccManifestQueue = new LinkedBlockingQueue();
        private Bbie previousBbie;
        private Queue<BccpManifest> targetBccpManifestQueue = new LinkedBlockingQueue();

        private String currentSourcePath;
        private String currentTargetPath;

        private Map<BigInteger, List<Association>> abieSourceAssociationsMap = new HashMap();
        private Map<BigInteger, List<Association>> abieTargetAssociationsMap = new HashMap();
        private Map<BigInteger, List<DtScManifest>> bbieTargetDtScManifestsMap = new HashMap();

        private Map<BigInteger, WrappedAsbiep> asbiepMap;
        private Map<BigInteger, WrappedAsbiep> roleOfAbieToAsbiepMap;
        private Map<BigInteger, Abie> abieIdToAbieMap;
        private Map<BigInteger, WrappedAsbie> toAsbiepToAsbieMap;
        private Map<BigInteger, WrappedBbie> toBbiepToBbieMap;
        private Map<BigInteger, Bbie> bbieMap;
        private List<WrappedBbieSc> bbieScList;

        private BigInteger targetTopLevelAsbiepId;

        BieUpliftingHandler(ScoreUser requester, List<BigInteger> bizCtxIds,
                            BieUpliftingCustomMappingTable customMappingTable,
                            BieDocument sourceBieDocument, CcDocument targetCcDocument,
                            BigInteger targetAsccpManifestId) {

            this.requester = requester;
            this.bizCtxIds = bizCtxIds;
            this.customMappingTable = customMappingTable;

            this.sourceBieDocument = sourceBieDocument;
            this.targetCcDocument = targetCcDocument;
            this.targetAsccpManifestId = targetAsccpManifestId;

            this.asbiepMap = new HashMap();
            this.roleOfAbieToAsbiepMap = new HashMap();
            this.abieIdToAbieMap = new HashMap();
            this.toAsbiepToAsbieMap = new HashMap();
            this.toBbiepToBbieMap = new HashMap();
            this.bbieMap = new HashMap();
            this.bbieScList = new ArrayList();
        }

        public BigInteger uplift() {
            this.sourceBieDocument.accept(this);
            return targetTopLevelAsbiepId;
        }

        @Override
        public void visitStart(TopLevelAsbiep topLevelAsbiep, BieVisitContext context) {
            AsccpManifest targetAsccpManifest = targetCcDocument.getAsccpManifest(targetAsccpManifestId);
            targetAsccpManifestQueue.offer(targetAsccpManifest);
        }

        private String extractAbiePath(String assocPath) {
            Stack<String> stack = new Stack();
            stack.addAll(Arrays.asList(assocPath.split(">")));

            stack.pop(); // drop the assoc tag.
            while (!stack.isEmpty()) {
                String tag = stack.pop();
                if (tag.startsWith("ACC") && !stack.lastElement().startsWith("ACC")) { // find the last tag of ACCs
                    stack.push(tag);
                    return String.join(">", stack);
                }
            }

            throw new IllegalStateException();
        }

        private String extractBbiePath(String bbieScPath) {
            Stack<String> stack = new Stack();
            stack.addAll(Arrays.asList(bbieScPath.split(">")));

            stack.pop(); // drop 'BDT_SC' tag
            stack.pop(); // drop 'BDT' tag
            stack.pop(); // drop 'BCCP' tag

            return String.join(">", stack);
        }

        @Override
        public void visitEnd(TopLevelAsbiep topLevelAsbiep, BieVisitContext context) {
            List<Abie> emptySourceAbieList = new ArrayList();
            List<WrappedAsbie> emptySourceAsbieList = new ArrayList();
            List<WrappedBbie> emptySourceBbieList = new ArrayList();
            List<WrappedAsbiep> emptySourceAsbiepList = new ArrayList();
            List<Bbiep> emptySourceBbiepList = new ArrayList();
            List<WrappedBbieSc> emptySourceBbieScList = new ArrayList();

            Function<String, Abie> getAbieIfExist = (path) -> {
                Abie abie = abieIdToAbieMap.values().stream()
                        .filter(e -> e.getPath().equals(path))
                        .findAny().orElse(null);
                if (abie == null) {
                    abie = emptySourceAbieList.stream()
                            .filter(e -> e.getPath().equals(path))
                            .findAny().orElse(null);
                }
                return abie;
            };

            Function<String, Bbie> getBbieIfExist = (path) -> {
                Bbie bbie = bbieMap.values().stream()
                        .filter(e -> e.getPath().equals(path))
                        .findAny().orElse(null);
                if (bbie == null) {
                    bbie = emptySourceBbieList.stream()
                            .map(e -> e.getBbie())
                            .filter(e -> e.getPath().equals(path))
                            .findAny().orElse(null);
                }
                return bbie;
            };

            this.customMappingTable.getMappingList().stream()
                    .filter(e -> !hasLength(e.getSourcePath()))
                    .forEach(mapping -> {
                        switch (mapping.getBieType()) {
                            case "ASBIE":
                            case "BBIE":
                                String targetFromAbiePath = extractAbiePath(mapping.getTargetPath());
                                Abie targetFromAbie = getAbieIfExist.apply(targetFromAbiePath);

                                if (targetFromAbie == null) {
                                    BigInteger targetAccManifestId = extractManifestId(getLastTag(targetFromAbiePath));
                                    AccManifest targetAccManifest =
                                            targetCcDocument.getAccManifest(targetAccManifestId);

                                    targetFromAbie = new Abie();
                                    targetFromAbie.setGuid(ScoreGuidUtils.randomGuid());
                                    targetFromAbie.setBasedAccManifestId(targetAccManifest.getAccManifestId());
                                    targetFromAbie.setPath(targetFromAbiePath);
                                    targetFromAbie.setHashPath(sha256(targetFromAbie.getPath()));

                                    emptySourceAbieList.add(targetFromAbie);
                                }

                                break;
                        }

                        switch (mapping.getBieType()) {
                            case "ASBIE":
                                AsccManifest targetAsccManifest =
                                        targetCcDocument.getAsccManifest(mapping.getTargetManifestId());
                                Ascc targetAscc = targetCcDocument.getAscc(targetAsccManifest);
                                AsccpManifest targetAsccpManifest =
                                        targetCcDocument.getAsccpManifest(targetAsccManifest.getToAsccpManifestId());
                                AccManifest targetRoleOfAccManifest =
                                        targetCcDocument.getAccManifest(targetAsccpManifest.getRoleOfAccManifestId());

                                Asbie targetAsbie = new Asbie();
                                targetAsbie.setGuid(ScoreGuidUtils.randomGuid());
                                targetAsbie.setBasedAsccManifestId(targetAsccManifest.getAsccManifestId());
                                targetAsbie.setPath(mapping.getTargetPath());
                                targetAsbie.setHashPath(sha256(targetAsbie.getPath()));
                                targetAsbie.setCardinalityMin(targetAscc.getCardinalityMin());
                                targetAsbie.setCardinalityMax(targetAscc.getCardinalityMax());
                                targetAsbie.setNillable(false);
                                targetAsbie.setUsed(true);

                                WrappedAsbie wrappedAsbie = new WrappedAsbie();
                                wrappedAsbie.setAsbie(targetAsbie);
                                emptySourceAsbieList.add(wrappedAsbie);

                                Asbiep targetAsbiep = new Asbiep();
                                targetAsbiep.setGuid(ScoreGuidUtils.randomGuid());
                                targetAsbiep.setBasedAsccpManifestId(targetAsccpManifest.getAsccpManifestId());
                                targetAsbiep.setPath(targetAsbie.getPath() + ">" + "ASCCP-" + targetAsccpManifest.getAsccpManifestId());
                                targetAsbiep.setHashPath(sha256(targetAsbiep.getPath()));

                                WrappedAsbiep wrappedAsbiep = new WrappedAsbiep();
                                wrappedAsbie.setToAsbiep(wrappedAsbiep);
                                wrappedAsbiep.setAsbiep(targetAsbiep);
                                emptySourceAsbiepList.add(wrappedAsbiep);

                                String targetRoleOfAccPath = targetAsbiep.getPath() + ">" + "ACC-" + targetRoleOfAccManifest.getAccManifestId();
                                Abie targetRoleOfAbie = getAbieIfExist.apply(targetRoleOfAccPath);
                                if (targetRoleOfAbie == null) {
                                    targetRoleOfAbie = new Abie();
                                    targetRoleOfAbie.setGuid(ScoreGuidUtils.randomGuid());
                                    targetRoleOfAbie.setBasedAccManifestId(targetRoleOfAccManifest.getAccManifestId());
                                    targetRoleOfAbie.setPath(targetRoleOfAccPath);
                                    targetRoleOfAbie.setHashPath(sha256(targetRoleOfAbie.getPath()));

                                    emptySourceAbieList.add(targetRoleOfAbie);
                                }

                                break;

                            case "BBIE":
                                BccManifest targetBccManifest =
                                        targetCcDocument.getBccManifest(mapping.getTargetManifestId());
                                Bcc targetBcc = targetCcDocument.getBcc(targetBccManifest);
                                BccpManifest targetBccpManifest =
                                        targetCcDocument.getBccpManifest(targetBccManifest.getToBccpManifestId());
                                DtManifest targetDtManifest =
                                        targetCcDocument.getDtManifest(targetBccpManifest.getBdtManifestId());
                                Dt targetDt = targetCcDocument.getDt(targetDtManifest);
                                BdtPriRestri targetDefaultBdtPriRestri =
                                        targetCcDocument.getBdtPriRestriList(targetDt).stream()
                                                .filter(e -> e.isDefault())
                                                .findFirst().get();

                                Bbie targetBbie = new Bbie();
                                targetBbie.setGuid(ScoreGuidUtils.randomGuid());
                                targetBbie.setBasedBccManifestId(targetBccManifest.getBccManifestId());
                                targetBbie.setPath(mapping.getTargetPath());
                                targetBbie.setHashPath(sha256(targetBbie.getPath()));
                                targetBbie.setBdtPriRestriId(targetDefaultBdtPriRestri.getBdtPriRestriId());
                                targetBbie.setDefaultValue(targetBcc.getDefaultValue());
                                targetBbie.setFixedValue(targetBcc.getFixedValue());
                                targetBbie.setCardinalityMin(targetBcc.getCardinalityMin());
                                targetBbie.setCardinalityMax(targetBcc.getCardinalityMax());
                                targetBbie.setNillable(targetBcc.isNillable());
                                targetBbie.setUsed(true);

                                WrappedBbie wrappedBbie = new WrappedBbie();
                                wrappedBbie.setBbie(targetBbie);
                                emptySourceBbieList.add(wrappedBbie);

                                Bbiep targetBbiep = new Bbiep();
                                targetBbiep.setGuid(ScoreGuidUtils.randomGuid());
                                targetBbiep.setBasedBccpManifestId(targetBccpManifest.getBccpManifestId());
                                targetBbiep.setPath(targetBbie.getPath() + ">" + "BCCP-" + targetBccpManifest.getBccpManifestId());
                                targetBbiep.setHashPath(sha256(targetBbiep.getPath()));

                                wrappedBbie.setToBbiep(targetBbiep);
                                emptySourceBbiepList.add(targetBbiep);

                                break;

                            case "BBIE_SC":
                                DtScManifest targetDtScManifest =
                                        targetCcDocument.getDtScManifest(mapping.getTargetManifestId());
                                DtSc targetDtSc = targetCcDocument.getDtSc(targetDtScManifest);
                                BdtScPriRestri targetDefaultBdtScPriRestri =
                                        targetCcDocument.getBdtScPriRestriList(targetDtSc).stream()
                                                .filter(e -> e.isDefault())
                                                .findFirst().get();

                                BbieSc targetBbieSc = new BbieSc();
                                targetBbieSc.setGuid(ScoreGuidUtils.randomGuid());
                                targetBbieSc.setBasedDtScManifestId(targetDtScManifest.getDtScManifestId());
                                targetBbieSc.setPath(mapping.getTargetPath());
                                targetBbieSc.setHashPath(sha256(targetBbieSc.getPath()));
                                targetBbieSc.setDtScPriRestriId(targetDefaultBdtScPriRestri.getBdtScPriRestriId());
                                targetBbieSc.setDefaultValue(targetDtSc.getDefaultValue());
                                targetBbieSc.setFixedValue(targetDtSc.getFixedValue());
                                targetBbieSc.setCardinalityMin(targetDtSc.getCardinalityMin());
                                targetBbieSc.setCardinalityMax(targetDtSc.getCardinalityMax());
                                targetBbieSc.setUsed(true);

                                WrappedBbieSc wrappedBbieSc = new WrappedBbieSc();
                                wrappedBbieSc.setBbieSc(targetBbieSc);
                                emptySourceBbieScList.add(wrappedBbieSc);

                                break;
                        }
                    });

            CreateBieRequest createBieRequest = new CreateBieRequest(this.requester);
            createBieRequest.setBizCtxIds(bizCtxIds);
            createBieRequest.setStatus(topLevelAsbiep.getStatus());
            createBieRequest.setVersion(topLevelAsbiep.getVersion());
            createBieRequest.setTopLevelAsbiep(this.asbiepMap.get(topLevelAsbiep.getAsbiepId()));
            createBieRequest.setAsbieList(
                    Stream.concat(toAsbiepToAsbieMap.values().stream(),
                            emptySourceAsbieList.stream())
                            .map(asbie -> {
                                if (asbie.getFromAbie() == null) {
                                    String targetFromAbiePath = extractAbiePath(asbie.getAsbie().getPath());
                                    Abie targetFromAbie = getAbieIfExist.apply(targetFromAbiePath);
                                    if (targetFromAbie == null) {
                                        throw new IllegalStateException();
                                    }
                                    asbie.setFromAbie(targetFromAbie);
                                }

                                WrappedAsbiep asbiep = asbie.getToAsbiep();
                                if (asbiep == null) {
                                    String path = asbie.getAsbie().getPath();
                                    BieUpliftingMapping mapping = customMappingTable.getTargetAsccMappingByTargetPath(path);
                                    asbie.setRefTopLevelAsbiepId(mapping.getRefTopLevelAsbiepId());

                                } else {
                                    if (asbiep.getRoleOfAbie() == null) {
                                        AsccpManifest targetAsccpManifest =
                                                targetCcDocument.getAsccpManifest(asbiep.getAsbiep().getBasedAsccpManifestId());
                                        String targetRoleOfAbiePath = asbiep.getAsbiep().getPath() + ">" + "ACC-" +
                                                targetAsccpManifest.getRoleOfAccManifestId();
                                        Abie targetRoleOfAbie = getAbieIfExist.apply(targetRoleOfAbiePath);
                                        if (targetRoleOfAbie == null) {
                                            throw new IllegalStateException();
                                        }
                                        asbiep.setRoleOfAbie(targetRoleOfAbie);
                                    }
                                }

                                return asbie;
                            })
                    .collect(Collectors.toList()));
            createBieRequest.setBbieList(
                    Stream.concat(toBbiepToBbieMap.values().stream(),
                            emptySourceBbieList.stream())
                            .map(bbie -> {
                                if (bbie.getFromAbie() == null) {
                                    String targetFromAbiePath = extractAbiePath(bbie.getBbie().getPath());
                                    Abie targetFromAbie = getAbieIfExist.apply(targetFromAbiePath);
                                    if (targetFromAbie == null) {
                                        throw new IllegalStateException();
                                    }
                                    bbie.setFromAbie(targetFromAbie);
                                }

                                return bbie;
                            })
                            .collect(Collectors.toList()));
            createBieRequest.setBbieScList(
                    Stream.concat(bbieScList.stream(),
                            emptySourceBbieScList.stream())
                            .map(bbieSc -> {
                                if (bbieSc.getBbie() == null) {
                                    String targetBbiePath = extractBbiePath(bbieSc.getBbieSc().getPath());
                                    Bbie targetBbie = getBbieIfExist.apply(targetBbiePath);
                                    if (targetBbie == null) {
                                        throw new IllegalStateException();
                                    }
                                    bbieSc.setBbie(targetBbie);
                                }

                                return bbieSc;
                            })
                            .collect(Collectors.toList()));

            targetTopLevelAsbiepId = scoreRepositoryFactory.createBieWriteRepository()
                    .createBie(createBieRequest)
                    .getTopLevelAsbiepId();
        }

        @Override
        public void visitAbie(Abie abie, BieVisitContext context) {
            CcDocument sourceCcDocument = context.getBieDocument().getCcDocument();
            if (abie == null) {
                return;
            }
            AccManifest sourceAccManifest = sourceCcDocument.getAccManifest(
                    abie.getBasedAccManifestId()
            );
            List<Association> sourceAssociations =
                    getAssociationsRegardingBases(currentSourcePath, sourceCcDocument, sourceAccManifest);
            abieSourceAssociationsMap.put(abie.getAbieId(), sourceAssociations);

            AccManifest targetAccManifest = targetAccManifestQueue.poll();
            if (targetAccManifest == null) {
                BigInteger targetAccManifestId =
                        this.customMappingTable.getTargetAccManifestIdBySourcePath(currentSourcePath);
                if (targetAccManifestId != null) {
                    targetAccManifest = targetCcDocument.getAccManifest(targetAccManifestId);
                }
            }

            if (targetAccManifest != null) { // found matched acc
                List<Association> targetAssociations =
                        getAssociationsRegardingBases(currentTargetPath, targetCcDocument, targetAccManifest);
                abieTargetAssociationsMap.put(abie.getAbieId(), targetAssociations);

                currentTargetPath = currentTargetPath + ">" + "ACC-" + targetAccManifest.getAccManifestId();
                Abie targetAbie = new Abie();
                targetAbie.setGuid(ScoreGuidUtils.randomGuid());
                targetAbie.setBasedAccManifestId(targetAccManifest.getAccManifestId());
                targetAbie.setPath(currentTargetPath);
                targetAbie.setHashPath(sha256(currentTargetPath));
                targetAbie.setDefinition(abie.getDefinition());
                targetAbie.setRemark(abie.getRemark());
                targetAbie.setBizTerm(abie.getBizTerm());

                this.roleOfAbieToAsbiepMap.get(abie.getAbieId()).setRoleOfAbie(targetAbie);
                this.abieIdToAbieMap.put(abie.getAbieId(), targetAbie);
            }
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
            CcMatchingScore matchingScore = targetAssociations.stream().filter(e -> e.getCcAssociation().isAscc())
                    .map(e -> ccMatchingService.score(
                            sourceCcDocument,
                            sourceAssociation,
                            targetCcDocument,
                            e,
                            (ccDocument, association) -> ccDocument.getAscc((AsccManifest) association.getCcAssociation())))
                    .max(Comparator.comparing(CcMatchingScore::getScore))
                    .orElse(new CcMatchingScore(0.0d, null, null));

            currentSourcePath = sourceAssociation.getPath();
            AsccManifest targetAsccManifest = null;
            if (matchingScore.getScore() == 0.0d || matchingScore.getTarget() == null) {
                BieUpliftingMapping targetAsccMapping =
                        this.customMappingTable.getTargetAsccMappingBySourcePath(currentSourcePath);
                if (targetAsccMapping != null) {
                    currentTargetPath = targetAsccMapping.getTargetPath();
                    targetAsccManifest = targetCcDocument.getAsccManifest(targetAsccMapping.getTargetManifestId());
                }
            } else {
                Association targetAssociation = (Association) matchingScore.getTarget();
                currentTargetPath = targetAssociation.getPath();
                targetAsccManifest = (AsccManifest) targetAssociation.getCcAssociation();
            }

            if (targetAsccManifest != null) {
                AsccpManifest toAsccpManifest = targetCcDocument.getAsccpManifest(
                        targetAsccManifest.getToAsccpManifestId());
                targetAsccpManifestQueue.offer(toAsccpManifest);

                Asbie targetAsbie = new Asbie();
                targetAsbie.setGuid(ScoreGuidUtils.randomGuid());
                targetAsbie.setBasedAsccManifestId(targetAsccManifest.getAsccManifestId());
                targetAsbie.setPath(currentTargetPath);
                targetAsbie.setHashPath(sha256(targetAsbie.getPath()));
                targetAsbie.setCardinalityMin(asbie.getCardinalityMin());
                targetAsbie.setCardinalityMax(asbie.getCardinalityMax());
                targetAsbie.setNillable(asbie.isNillable());
                targetAsbie.setDefinition(asbie.getDefinition());
                targetAsbie.setRemark(asbie.getRemark());
                targetAsbie.setUsed(asbie.isUsed());

                WrappedAsbie upliftingAsbie = new WrappedAsbie();
                upliftingAsbie.setFromAbie(this.abieIdToAbieMap.get(asbie.getFromAbieId()));
                upliftingAsbie.setAsbie(targetAsbie);

                this.toAsbiepToAsbieMap.put(asbie.getToAsbiepId(), upliftingAsbie);
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
            CcMatchingScore matchingScore = targetAssociations.stream().filter(e -> e.getCcAssociation().isBcc())
                    .map(e -> ccMatchingService.score(
                            sourceCcDocument,
                            sourceAssociation,
                            targetCcDocument,
                            e,
                            (ccDocument, association) -> ccDocument.getBcc((BccManifest) association.getCcAssociation())))
                    .max(Comparator.comparing(CcMatchingScore::getScore))
                    .orElse(new CcMatchingScore(0.0d, null, null));

            currentSourcePath = sourceAssociation.getPath();
            BccManifest targetBccManifest = null;
            if (matchingScore.getScore() == 0.0d || matchingScore.getTarget() == null) {
                BieUpliftingMapping targetBccMapping =
                        this.customMappingTable.getTargetBccMappingBySourcePath(currentSourcePath);
                if (targetBccMapping != null) {
                    currentTargetPath = targetBccMapping.getTargetPath();
                    targetBccManifest = targetCcDocument.getBccManifest(targetBccMapping.getTargetManifestId());
                }
            } else {
                Association targetAssociation = (Association) matchingScore.getTarget();
                currentTargetPath = targetAssociation.getPath();
                targetBccManifest = (BccManifest) targetAssociation.getCcAssociation();
            }

            if (targetBccManifest != null) {
                BccpManifest toBccpManifest = targetCcDocument.getBccpManifest(
                        targetBccManifest.getToBccpManifestId());
                this.previousBbie = bbie;
                targetBccpManifestQueue.offer(toBccpManifest);

                Bbie targetBbie = new Bbie();
                targetBbie.setGuid(ScoreGuidUtils.randomGuid());
                targetBbie.setBasedBccManifestId(targetBccManifest.getBccManifestId());
                targetBbie.setPath(currentTargetPath);
                targetBbie.setHashPath(sha256(targetBbie.getPath()));
                targetBbie.setBdtPriRestriId(bbie.getBdtPriRestriId());
                targetBbie.setCodeListId(bbie.getCodeListId());
                targetBbie.setAgencyIdListId(bbie.getAgencyIdListId());
                targetBbie.setDefaultValue(bbie.getDefaultValue());
                targetBbie.setFixedValue(bbie.getFixedValue());
                targetBbie.setCardinalityMin(bbie.getCardinalityMin());
                targetBbie.setCardinalityMax(bbie.getCardinalityMax());
                targetBbie.setNillable(bbie.isNillable());
                targetBbie.setDefinition(bbie.getDefinition());
                targetBbie.setRemark(bbie.getRemark());
                targetBbie.setExample(bbie.getExample());
                targetBbie.setUsed(bbie.isUsed());

                WrappedBbie upliftingBbie = new WrappedBbie();
                upliftingBbie.setFromAbie(abieIdToAbieMap.get(bbie.getFromAbieId()));
                upliftingBbie.setBbie(targetBbie);

                this.toBbiepToBbieMap.put(bbie.getToBbiepId(), upliftingBbie);
                this.bbieMap.put(bbie.getBbieId(), targetBbie);
            }
        }

        @Override
        public void visitAsbiep(Asbiep asbiep, BieVisitContext context) {
            CcDocument sourceCcDocument = context.getBieDocument().getCcDocument();
            if (asbiep == null) {
                return;
            }
            AsccpManifest sourceAsccpManifest = sourceCcDocument.getAsccpManifest(
                    asbiep.getBasedAsccpManifestId());

            currentSourcePath = (hasLength(currentSourcePath)) ?
                    currentSourcePath + ">" + "ASCCP-" + sourceAsccpManifest.getAsccpManifestId() :
                    "ASCCP-" + sourceAsccpManifest.getAsccpManifestId();

            AsccpManifest targetAsccpManifest = targetAsccpManifestQueue.poll();
            if (targetAsccpManifest == null) {
                BigInteger targetAsccpManifestId =
                        this.customMappingTable.getTargetAsccpManifestIdBySourcePath(currentSourcePath);
                if (targetAsccpManifestId != null) {
                    targetAsccpManifest = targetCcDocument.getAsccpManifest(targetAsccpManifestId);
                }
            }

            if (targetAsccpManifest != null) { // found matched asccp
                AccManifest targetRoleOfAccManifest = targetCcDocument.getRoleOfAccManifest(targetAsccpManifest);
                targetAccManifestQueue.offer(targetRoleOfAccManifest);
                currentTargetPath = (hasLength(currentTargetPath)) ?
                        currentTargetPath + ">" + "ASCCP-" + targetAsccpManifest.getAsccpManifestId() :
                        "ASCCP-" + targetAsccpManifest.getAsccpManifestId();

                Asbiep targetAsbiep = new Asbiep();
                targetAsbiep.setGuid(ScoreGuidUtils.randomGuid());
                targetAsbiep.setBasedAsccpManifestId(targetAsccpManifest.getAsccpManifestId());
                targetAsbiep.setPath(currentTargetPath);
                targetAsbiep.setHashPath(sha256(targetAsbiep.getPath()));
                targetAsbiep.setDefinition(asbiep.getDefinition());
                targetAsbiep.setRemark(asbiep.getRemark());
                targetAsbiep.setBizTerm(asbiep.getBizTerm());

                WrappedAsbiep upliftingAsbiep = new WrappedAsbiep();
                upliftingAsbiep.setAsbiep(targetAsbiep);

                WrappedAsbie upliftingAsbie = this.toAsbiepToAsbieMap.get(asbiep.getAsbiepId());
                if (upliftingAsbie != null) {
                    upliftingAsbie.setToAsbiep(upliftingAsbiep);
                }
                this.asbiepMap.put(asbiep.getAsbiepId(), upliftingAsbiep);
                this.roleOfAbieToAsbiepMap.put(asbiep.getRoleOfAbieId(), upliftingAsbiep);
            }
        }

        @Override
        public void visitBbiep(Bbiep bbiep, BieVisitContext context) {
            CcDocument sourceCcDocument = context.getBieDocument().getCcDocument();
            BccpManifest sourceBccpManifest = sourceCcDocument.getBccpManifest(
                    bbiep.getBasedBccpManifestId());
            currentSourcePath = currentSourcePath + ">" + "BCCP-" + sourceBccpManifest.getBccpManifestId();

            BccpManifest targetBccpManifest = targetBccpManifestQueue.poll();
            if (targetBccpManifest == null) {
                BigInteger targetBccpManifestId =
                        this.customMappingTable.getTargetBccpManifestIdBySourcePath(currentSourcePath);
                if (targetBccpManifestId != null) {
                    targetBccpManifest = targetCcDocument.getBccpManifest(targetBccpManifestId);
                }
            }

            if (targetBccpManifest != null) {
                currentTargetPath = currentTargetPath + ">" + "BCCP-" + targetBccpManifest.getBccpManifestId();

                BigInteger targetBdtManifestId = targetBccpManifest.getBdtManifestId();
                DtManifest targetDtManifest = targetCcDocument.getDtManifest(targetBdtManifestId);
                bbieTargetDtScManifestsMap.put(previousBbie.getBbieId(),
                        targetCcDocument.getDtScManifests(targetDtManifest));

                Bbiep targetBbiep = new Bbiep();
                targetBbiep.setGuid(ScoreGuidUtils.randomGuid());
                targetBbiep.setBasedBccpManifestId(targetBccpManifest.getBccpManifestId());
                targetBbiep.setPath(currentTargetPath);
                targetBbiep.setHashPath(sha256(targetBbiep.getPath()));
                targetBbiep.setDefinition(bbiep.getDefinition());
                targetBbiep.setRemark(bbiep.getRemark());
                targetBbiep.setBizTerm(bbiep.getBizTerm());

                this.toBbiepToBbieMap.get(bbiep.getBbiepId()).setToBbiep(targetBbiep);
            }
        }

        @Override
        public void visitBbieSc(BbieSc bbieSc, BieVisitContext context) {
            CcDocument sourceCcDocument = context.getBieDocument().getCcDocument();
            DtScManifest sourceDtScManifest = sourceCcDocument.getDtScManifest(bbieSc.getBasedDtScManifestId());
            DtManifest sourceDtManifest = sourceCcDocument.getDtManifest(sourceDtScManifest.getOwnerDtManifestId());

            String sourcePath = currentSourcePath + ">" + "BDT-" + sourceDtManifest.getDtManifestId() +
                    ">" + "BDT_SC-" + sourceDtScManifest.getDtScManifestId();
            CcMatchingScore matchingScore =
                    bbieTargetDtScManifestsMap.getOrDefault(bbieSc.getBbieId(), Collections.emptyList()).stream()
                            .map(e -> ccMatchingService.score(
                                    sourceCcDocument,
                                    sourceDtScManifest,
                                    targetCcDocument,
                                    e,
                                    (ccDocument, dtScManifest) -> ccDocument.getDtSc(dtScManifest)))
                            .max(Comparator.comparing(CcMatchingScore::getScore))
                            .orElse(new CcMatchingScore(0.0d, null, null));

            DtScManifest targetDtScManifest = null;
            String targetPath = null;
            if (matchingScore.getScore() == 0.0d || matchingScore.getTarget() == null) {
                BieUpliftingMapping targetDtScMapping =
                        this.customMappingTable.getTargetDtScMappingBySourcePath(sourcePath);
                if (targetDtScMapping != null) {
                    targetDtScManifest = targetCcDocument.getDtScManifest(targetDtScMapping.getTargetManifestId());
                    targetPath = targetDtScMapping.getTargetPath();
                }
            } else {
                targetDtScManifest = (DtScManifest) matchingScore.getTarget();
                DtManifest targetDtManifest = targetCcDocument.getDtManifest(targetDtScManifest.getOwnerDtManifestId());
                targetPath = currentTargetPath + ">" + "BDT-" + targetDtManifest.getDtManifestId() +
                        ">" + "BDT_SC-" + targetDtScManifest.getDtScManifestId();
            }

            if (targetDtScManifest != null) {
                BbieSc targetBbieSc = new BbieSc();
                targetBbieSc.setGuid(ScoreGuidUtils.randomGuid());
                targetBbieSc.setBasedDtScManifestId(targetDtScManifest.getDtScManifestId());
                targetBbieSc.setPath(targetPath);
                targetBbieSc.setHashPath(sha256(targetBbieSc.getPath()));
                targetBbieSc.setDtScPriRestriId(bbieSc.getDtScPriRestriId());
                targetBbieSc.setCodeListId(bbieSc.getCodeListId());
                targetBbieSc.setAgencyIdListId(bbieSc.getAgencyIdListId());
                targetBbieSc.setDefaultValue(bbieSc.getDefaultValue());
                targetBbieSc.setFixedValue(bbieSc.getFixedValue());
                targetBbieSc.setCardinalityMin(bbieSc.getCardinalityMin());
                targetBbieSc.setCardinalityMax(bbieSc.getCardinalityMax());
                targetBbieSc.setNillable(bbieSc.isNillable());
                targetBbieSc.setDefinition(bbieSc.getDefinition());
                targetBbieSc.setRemark(bbieSc.getRemark());
                targetBbieSc.setBizTerm(bbieSc.getBizTerm());
                targetBbieSc.setExample(bbieSc.getExample());
                targetBbieSc.setUsed(bbieSc.isUsed());

                WrappedBbieSc upliftingBbieSc = new WrappedBbieSc();
                upliftingBbieSc.setBbie(this.bbieMap.get(bbieSc.getBbieId()));
                upliftingBbieSc.setBbieSc(targetBbieSc);

                this.bbieScList.add(upliftingBbieSc);
            }
        }
    }

    @Transactional
    public UpliftBieResponse upliftBie(UpliftBieRequest request) {
        BigInteger targetAsccpManifestId = request.getTargetAsccpManifestId();
        BieDocument sourceBieDocument = bieReadService.getBieDocument(request.getRequester(), request.getTopLevelAsbiepId());
        CcDocument targetCcDocument = new CcDocumentImpl(scoreRepositoryFactory.createCcReadRepository()
                .getCcPackage(new GetCcPackageRequest(request.getRequester())
                        .withAsccpManifestId(targetAsccpManifestId))
                .getCcPackage());

        List<BigInteger> bizCtxIds = scoreRepositoryFactory.createBieReadRepository()
                .getAssignedBusinessContext(new GetAssignedBusinessContextRequest(request.getRequester())
                        .withTopLevelAsbiepId(request.getTopLevelAsbiepId()))
                .getBusinessContextIdList();

        List<BieUpliftingMapping> mappingList = request.getCustomMappingTable();
        BieUpliftingCustomMappingTable customMappingTable = new BieUpliftingCustomMappingTable(
                sourceBieDocument.getCcDocument(),
                targetCcDocument,
                mappingList);

        BieUpliftingHandler upliftingHandler =
                new BieUpliftingHandler(request.getRequester(), bizCtxIds, customMappingTable,
                        sourceBieDocument, targetCcDocument, targetAsccpManifestId);
        BigInteger targetTopLevelAsbiepId = upliftingHandler.uplift();

        UpliftBieResponse response = new UpliftBieResponse();
        response.setTopLevelAsbiepId(targetTopLevelAsbiepId);
        return response;
    }

    public UpliftValidationResponse validateBieUplifting(UpliftValidationRequest request) {
        UpliftValidationResponse response = new UpliftValidationResponse();
        List<BieUpliftingValidation> validations = new ArrayList<>();

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

        CodeListReadRepository codeListReadRepository = scoreRepositoryFactory.createCodeListReadRepository();
        Map<BigInteger, CodeList> codeListMap = codeListReadRepository.getCodeListMap(request.getTargetReleaseId());

        request.getMappingList().forEach(mapping -> {
            BieUpliftingValidation validation = new BieUpliftingValidation();
            validation.setBieId(mapping.getBieId());
            validation.setBieType(mapping.getBieType());
            switch (mapping.getBieType().toUpperCase()) {
                case "ABIE":
                    validation.setValid(true);
                    break;
                case "ASBIE":
                    validation.setValid(true);
                    break;
                case "BBIE":
                    Bbie bbie = sourceBieDocument.getBbie(mapping.getBieId());
                    if (bbie.getCodeListId() != null) {
                        validation.setValid(codeListMap.get(bbie.getCodeListId()) != null);
                    } else {
                        validation.setValid(true);
                    }
                    break;
                case "BBIE_SC":
                    BbieSc bbieSc = sourceBieDocument.getBbieSc(mapping.getBieId());
                    if (bbieSc.getCodeListId() != null) {
                        validation.setValid(codeListMap.get(bbieSc.getCodeListId()) != null);
                    } else {
                        validation.setValid(true);
                    }
                    break;
            }
            validations.add(validation);
        });
        response.setValidations(validations);
        return response;
    }
}
