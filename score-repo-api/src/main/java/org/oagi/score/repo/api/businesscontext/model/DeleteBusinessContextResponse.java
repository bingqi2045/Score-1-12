package org.oagi.score.repo.api.businesscontext.model;

import org.oagi.score.repo.api.base.Response;

import java.util.List;

public class DeleteBusinessContextResponse extends Response {

    private final List<String> contextSchemeIdList;

    public DeleteBusinessContextResponse(List<String> contextSchemeIdList) {
        this.contextSchemeIdList = contextSchemeIdList;
    }

    public List<String> getContextSchemeIdList() {
        return contextSchemeIdList;
    }

    public boolean contains(String contextSchemeId) {
        return this.contextSchemeIdList.contains(contextSchemeId);
    }

    public boolean containsAll(List<String> contextSchemeIdList) {
        for (String contextSchemeId : contextSchemeIdList) {
            if (!this.contextSchemeIdList.contains(contextSchemeId)) {
                return false;
            }
        }
        return true;
    }
}
