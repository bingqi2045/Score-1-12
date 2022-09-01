package org.oagi.score.gateway.http.api.module_management.data;

import lombok.Data;
import org.oagi.score.repo.api.module.model.AssignableNode;

import java.math.BigInteger;
import java.util.List;

@Data
public class AssignCCToModule {
    List<AssignableNode> nodes;
    String moduleId;
    String moduleSetId;
    String moduleSetReleaseId;
    String releaseId;
}
