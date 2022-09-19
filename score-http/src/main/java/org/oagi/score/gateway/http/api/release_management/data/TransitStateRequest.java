package org.oagi.score.gateway.http.api.release_management.data;

import lombok.Data;

@Data
public class TransitStateRequest {

    private String releaseId;
    private String state;

    private ReleaseValidationRequest validationRequest;
}
