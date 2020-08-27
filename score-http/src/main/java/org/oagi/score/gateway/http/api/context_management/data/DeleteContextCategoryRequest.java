package org.oagi.score.gateway.http.api.context_management.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Data
public class DeleteContextCategoryRequest {

    private List<BigInteger> ctxCategoryIds = Collections.emptyList();
}
