package org.oagi.score.repo.api.businessterm.model;

import org.oagi.score.repo.api.base.Auditable;

import java.util.List;

public class CreateBulkBusinessTermResponse extends Auditable {

    private final List<String> businessTermIds;

    public CreateBulkBusinessTermResponse(List<String> businessTermIds) {
        this.businessTermIds = businessTermIds;
    }

    public List<String> getBusinessTermIds() {
        return businessTermIds;
    }
}
