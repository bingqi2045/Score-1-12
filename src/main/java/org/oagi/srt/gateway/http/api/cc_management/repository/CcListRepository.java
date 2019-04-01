package org.oagi.srt.gateway.http.api.cc_management.repository;

import org.oagi.srt.data.*;
import org.oagi.srt.gateway.http.api.cc_management.data.*;
import org.oagi.srt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.oagi.srt.data.OagisComponentType.UserExtensionGroup;
import static org.oagi.srt.gateway.http.api.cc_management.helper.CcUtility.getLatestEntity;
import static org.oagi.srt.gateway.http.api.cc_management.helper.CcUtility.getRevision;

@Repository
public class CcListRepository {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoreComponentRepository coreComponentRepository;

    public List<CcList> getAccList(long releaseId) {
        Map<Long, String> usernameMap = userRepository.getUsernameMap();

        Map<String, List<ACC>> accList = coreComponentRepository.getAccList()
                .stream().collect(groupingBy(CoreComponent::getGuid));

        return accList.entrySet().stream()
                .map(entry -> getLatestEntity(releaseId, entry.getValue()))
                .filter(item -> item != null)
                .map(acc -> {
                    OagisComponentType oagisComponentType = OagisComponentType.valueOf(acc.getOagisComponentType());
                    CcList ccList = new CcList();
                    ccList.setType("ACC");
                    ccList.setId(acc.getAccId());
                    ccList.setGuid(acc.getGuid());
                    ccList.setDen(acc.getDen());
                    ccList.setDefinition(acc.getDefinition());
                    ccList.setDefinitionSource(acc.getDefinitionSource());
                    ccList.setOagisComponentType(oagisComponentType);
                    ccList.setState(CcState.valueOf(acc.getState()));
                    ccList.setDeprecated(acc.isDeprecated());
                    ccList.setCurrentId(acc.getCurrentAccId());
                    ccList.setLastUpdateTimestamp(acc.getLastUpdateTimestamp());
                    ccList.setRevision(getRevision(releaseId, accList.getOrDefault(acc.getGuid(), Collections.emptyList())));
                    ccList.setOwner(usernameMap.get(acc.getOwnerUserId()));
                    ccList.setLastUpdateUser(usernameMap.get(acc.getLastUpdatedBy()));

                    return ccList;
                })
                .collect(Collectors.toList());
    }

    public List<CcList> getAsccList(long releaseId) {
        Map<Long, String> usernameMap = userRepository.getUsernameMap();

        Map<String, List<ASCC>> asccList = coreComponentRepository.getAsccList()
                .stream().collect(groupingBy(CoreComponent::getGuid));

        return asccList.entrySet().stream()
                .map(entry -> getLatestEntity(releaseId, entry.getValue()))
                .filter(item -> item != null)
                .map(ascc -> {
                    CcList ccList = new CcList();
                    ccList.setType("ASCC");
                    ccList.setId(ascc.getAsccId());
                    ccList.setGuid(ascc.getGuid());
                    ccList.setDen(ascc.getDen());
                    ccList.setDefinition(ascc.getDefinition());
                    ccList.setDefinitionSource(ascc.getDefinitionSource());
                    ccList.setState(CcState.valueOf(ascc.getState()));
                    ccList.setDeprecated(ascc.isDeprecated());
                    ccList.setCurrentId(ascc.getCurrentAsccId());
                    ccList.setLastUpdateTimestamp(ascc.getLastUpdateTimestamp());
                    ccList.setRevision(getRevision(releaseId, asccList.getOrDefault(ascc.getGuid(), Collections.emptyList())));
                    ccList.setOwner(usernameMap.get(ascc.getOwnerUserId()));
                    ccList.setLastUpdateUser(usernameMap.get(ascc.getLastUpdatedBy()));

                    return ccList;
                })
                .collect(Collectors.toList());
    }

    public List<CcList> getBccList(long releaseId) {
        Map<Long, String> usernameMap = userRepository.getUsernameMap();

        Map<String, List<BCC>> bccList = coreComponentRepository.getBccList()
                .stream().collect(groupingBy(CoreComponent::getGuid));

        return bccList.entrySet().stream()
                .map(entry -> getLatestEntity(releaseId, entry.getValue()))
                .filter(item -> item != null)
                .map(bcc -> {
                    CcList ccList = new CcList();
                    ccList.setType("BCC");
                    ccList.setId(bcc.getBccId());
                    ccList.setGuid(bcc.getGuid());
                    ccList.setDen(bcc.getDen());
                    ccList.setDefinition(bcc.getDefinition());
                    ccList.setDefinitionSource(bcc.getDefinitionSource());
                    ccList.setState(CcState.valueOf(bcc.getState()));
                    ccList.setDeprecated(bcc.isDeprecated());
                    ccList.setCurrentId(bcc.getCurrentBccId());
                    ccList.setLastUpdateTimestamp(bcc.getLastUpdateTimestamp());
                    ccList.setRevision(getRevision(releaseId, bccList.getOrDefault(bcc.getGuid(), Collections.emptyList())));
                    ccList.setOwner(usernameMap.get(bcc.getOwnerUserId()));
                    ccList.setLastUpdateUser(usernameMap.get(bcc.getLastUpdatedBy()));

                    return ccList;
                })
                .collect(Collectors.toList());
    }

    public List<CcList> getAsccpList(long releaseId) {
        Map<Long, String> usernameMap = userRepository.getUsernameMap();

        Map<String, List<ASCCP>> asccpList = coreComponentRepository.getAsccpList()
                .stream().collect(groupingBy(CoreComponent::getGuid));

        return asccpList.entrySet().stream()
                .map(entry -> getLatestEntity(releaseId, entry.getValue()))
                .filter(item -> item != null)
                .map(asccp -> {
                    CcList ccList = new CcList();
                    ccList.setType("ASCCP");
                    ccList.setId(asccp.getAsccpId());
                    ccList.setGuid(asccp.getGuid());
                    ccList.setDen(asccp.getDen());
                    ccList.setDefinition(asccp.getDefinition());
                    ccList.setDefinitionSource(asccp.getDefinitionSource());
                    ccList.setState(CcState.valueOf(asccp.getState()));
                    ccList.setDeprecated(asccp.isDeprecated());
                    ccList.setCurrentId(asccp.getCurrentAsccpId());
                    ccList.setLastUpdateTimestamp(asccp.getLastUpdateTimestamp());
                    ccList.setRevision(getRevision(releaseId, asccpList.getOrDefault(asccp.getGuid(), Collections.emptyList())));
                    ccList.setOwner(usernameMap.get(asccp.getOwnerUserId()));
                    ccList.setLastUpdateUser(usernameMap.get(asccp.getLastUpdatedBy()));

                    return ccList;
                })
                .collect(Collectors.toList());
    }

    public List<CcList> getBccpList(long releaseId) {
        Map<Long, String> usernameMap = userRepository.getUsernameMap();

        Map<String, List<BCCP>> bccpList = coreComponentRepository.getBccpList()
                .stream().collect(groupingBy(CoreComponent::getGuid));

        return bccpList.entrySet().stream()
                .map(entry -> getLatestEntity(releaseId, entry.getValue()))
                .filter(item -> item != null)
                .map(bccp -> {
                    CcList ccList = new CcList();
                    ccList.setType("BCCP");
                    ccList.setId(bccp.getBccpId());
                    ccList.setGuid(bccp.getGuid());
                    ccList.setDen(bccp.getDen());
                    ccList.setDefinition(bccp.getDefinition());
                    ccList.setDefinitionSource(bccp.getDefinitionSource());
                    ccList.setState(CcState.valueOf(bccp.getState()));
                    ccList.setDeprecated(bccp.isDeprecated());
                    ccList.setCurrentId(bccp.getCurrentBccpId());
                    ccList.setLastUpdateTimestamp(bccp.getLastUpdateTimestamp());
                    ccList.setRevision(getRevision(releaseId, bccpList.getOrDefault(bccp.getGuid(), Collections.emptyList())));
                    ccList.setOwner(usernameMap.get(bccp.getOwnerUserId()));
                    ccList.setLastUpdateUser(usernameMap.get(bccp.getLastUpdatedBy()));

                    return ccList;
                })
                .collect(Collectors.toList());
    }
}
