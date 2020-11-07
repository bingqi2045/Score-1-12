package org.oagi.score.repo.api.bie.model;

import org.oagi.score.repo.api.base.PaginationResponse;

import java.util.List;

public class GetAbieListResponse  extends PaginationResponse<Abie> {

    public GetAbieListResponse(List<Abie> results, int page, int size, int length) {
        super(results, page, size, length);
    }

}
