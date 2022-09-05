package org.oagi.score.repo.api.businesscontext.model;

import org.oagi.score.repo.api.base.Request;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DeleteContextSchemeRequest extends Request {

    private List<String> contextSchemeIdList = Collections.emptyList();

    public DeleteContextSchemeRequest(ScoreUser requester) {
        super(requester);
    }

    public List<String> getContextSchemeIdList() {
        return contextSchemeIdList;
    }

    public void setContextSchemeId(String contextSchemeId) {
        if (contextSchemeId != null) {
            this.contextSchemeIdList = Arrays.asList(contextSchemeId);
        }
    }

    public DeleteContextSchemeRequest withContextSchemeId(String contextSchemeId) {
        this.setContextSchemeId(contextSchemeId);
        return this;
    }

    public void setContextSchemeIdList(List<String> contextSchemeIdList) {
        if (contextSchemeIdList != null) {
            this.contextSchemeIdList = contextSchemeIdList;
        }
    }

    public DeleteContextSchemeRequest withContextSchemeIdList(List<String> contextSchemeIdList) {
        this.setContextSchemeIdList(contextSchemeIdList);
        return this;
    }

}
