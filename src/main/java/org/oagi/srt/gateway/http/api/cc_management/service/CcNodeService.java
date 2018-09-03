package org.oagi.srt.gateway.http.api.cc_management.service;

import org.oagi.srt.gateway.http.api.cc_management.data.node.CcAccNode;
import org.oagi.srt.gateway.http.api.cc_management.data.node.CcAsccpNode;
import org.oagi.srt.gateway.http.api.cc_management.data.node.CcBccpNode;
import org.oagi.srt.gateway.http.api.cc_management.data.node.CcNode;
import org.oagi.srt.gateway.http.api.cc_management.repository.CcNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CcNodeService {

    @Autowired
    private CcNodeRepository repository;

    public CcAccNode getAccNode(User user, long accId, long releaseId) {
        return repository.getAccNode(accId, releaseId);
    }

    public CcAsccpNode getAsccpNode(User user, long asccpId, long releaseId) {
        return repository.getAsccpNode(asccpId, releaseId);
    }

    public CcBccpNode getBccpNode(User user, long bccpId, long releaseId) {
        return repository.getBccpNode(bccpId, releaseId);
    }

    public List<CcNode> getDescendants(User user, CcAccNode accNode) {
        return Collections.emptyList();
    }

    public List<CcNode> getDescendants(User user, CcAsccpNode asccpNode) {
        return Collections.emptyList();
    }

    public List<CcNode> getDescendants(User user, CcBccpNode bccpNode) {
        return Collections.emptyList();
    }

}
