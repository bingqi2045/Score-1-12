package org.oagi.srt.gateway.http.api.cc_management.service;

import org.oagi.srt.data.ACC;
import org.oagi.srt.data.ASCC;
import org.oagi.srt.data.ASCCP;
import org.oagi.srt.repository.ACCRepository;
import org.oagi.srt.repository.ASCCPRepository;
import org.oagi.srt.repository.ASCCRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class ExtensionPathHandler implements InitializingBean {

    private Long releaseId;

    @Autowired
    private ACCRepository accRepository;

    @Autowired
    private ASCCPRepository asccpRepository;

    @Autowired
    private ASCCRepository asccRepository;

    private Map<Long, ACC> accMap;
    private Map<Long, List<ASCC>> asccMap;
    private Map<Long, ASCCP> asccpMap;

    public ExtensionPathHandler(Long releaseId) {
        this.releaseId = releaseId;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (releaseId == null) {
            throw new IllegalStateException("'releaseId' parameter must not be null.");
        }

        init(releaseId);
    }

    private void init(Long releaseId) {
        accMap = Collections.emptyMap();
        asccMap = Collections.emptyMap();
        asccpMap = Collections.emptyMap();
    }

    public boolean containsExtension(long accId, long targetExtensionId) {
        ACC targetAcc = accMap.get(targetExtensionId);
        /*
         * Issue #711
         *
         * If the target extension is the global extension (i.e., All Extension),
         * this code loops forever.
         */
        if ("All Extension".equals(targetAcc.getObjectClassTerm())) {
            return true;
        }

        ACC acc = accMap.get(accId);
        if (acc.getBasedAccId() != null) {
            if (containsExtension(acc.getBasedAccId(), targetExtensionId)) {
                return true;
            }
        }

        List<Long> accList =
                asccMap.getOrDefault(acc.getAccId(), Collections.emptyList()).stream()
                        .map(e -> asccpMap.getOrDefault(e.getToAsccpId(), null))
                        .filter(e -> e != null)
                        .map(e -> accMap.getOrDefault(e.getRoleOfAccId(), null))
                        .filter(e -> e != null)
                        .map(e -> e.getAccId())
                        .collect(Collectors.toList());

        if (accList.contains(targetExtensionId)) {
            return true;
        }

        for (Long childAccId : accList) {
            if (containsExtension(childAccId, targetExtensionId)) {
                return true;
            }
        }

        return false;
    }

    public Collection<String> getPath(long accId, long targetExtensionId) {
        Stack<String> path = new Stack();
        fillPath(accId, targetExtensionId, path, true);
        return path;
    }

    private boolean fillPath(long accId, long targetExtensionId, Stack<String> path, boolean fill) {
        ACC acc = accMap.get(accId);
        if (fill) {
            path.push(acc.getObjectClassTerm());
        }

        if (acc.getBasedAccId() != null) {
            if (fillPath(acc.getBasedAccId(), targetExtensionId, path, false)) {
                return true;
            }
        }

        List<ACC> accList =
                asccMap.getOrDefault(acc.getAccId(), Collections.emptyList()).stream()
                        .map(e -> asccpMap.getOrDefault(e.getToAsccpId(), null))
                        .filter(e -> e != null)
                        .map(e -> accMap.getOrDefault(e.getRoleOfAccId(), null))
                        .filter(e -> e != null)
                        .collect(Collectors.toList());

        for (ACC next : accList) {
            if (targetExtensionId == next.getAccId()) {
                path.push(next.getObjectClassTerm());
                return true;
            }

            if (fillPath(next.getAccId(), targetExtensionId, path, true)) {
                return true;
            }
        }

        if (fill) {
            path.pop();
        }
        return false;
    }
}
