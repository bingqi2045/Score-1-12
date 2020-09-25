package org.oagi.score.export.service;

import org.oagi.score.export.model.CoreComponentRelation;
import org.oagi.score.provider.CoreComponentProvider;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.AsccRecord;
import org.oagi.score.repo.api.impl.jooq.entity.tables.records.BccRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CoreComponentService {

    public List<CoreComponentRelation> getCoreComponents(
            long accId, CoreComponentProvider coreComponentProvider) {
        List<BccRecord> bcc_tmp_assoc = coreComponentProvider.getBCCs(accId);
        List<AsccRecord> ascc_tmp_assoc = coreComponentProvider.getASCCs(accId);

        List<CoreComponentRelation> coreComponents = gatheringBySeqKey(bcc_tmp_assoc, ascc_tmp_assoc);
        return coreComponents;
    }


    private List<CoreComponentRelation> gatheringBySeqKey(
            List<BccRecord> bccList, List<AsccRecord> asccList
    ) {
        int size = bccList.size() + asccList.size();
        List<CoreComponentRelation> tmp_assoc = new ArrayList(size);
        tmp_assoc.addAll(bccList.stream().map(e -> (CoreComponentRelation) e).collect(Collectors.toList()));
        tmp_assoc.addAll(asccList.stream().map(e -> (CoreComponentRelation) e).collect(Collectors.toList()));
        Collections.sort(tmp_assoc, (a, b) -> a.getSeqKey() - b.getSeqKey());

        List<CoreComponentRelation> coreComponents = new ArrayList(size);
        for (BccRecord basicCoreComponent : bccList) {
            if (1 == basicCoreComponent.getEntityType()) {
                coreComponents.add((CoreComponentRelation) basicCoreComponent);
            }
        }

        for (CoreComponentRelation coreComponent : tmp_assoc) {
            if (coreComponent instanceof BccRecord) {
                BccRecord basicCoreComponent = (BccRecord) coreComponent;
                if (1 == basicCoreComponent.getEntityType()) {
                    coreComponents.add((CoreComponentRelation) basicCoreComponent);
                }
            } else {
                AsccRecord associationCoreComponent = (AsccRecord) coreComponent;
                coreComponents.add((CoreComponentRelation) associationCoreComponent);
            }
        }

        return coreComponents;
    }
}
