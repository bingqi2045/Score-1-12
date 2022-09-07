package org.oagi.score.gateway.http.api.cc_management.data.node;

import lombok.Data;
import org.oagi.score.gateway.http.api.cc_management.data.CcType;
import org.oagi.score.service.common.data.CcState;

import java.math.BigInteger;
import java.util.List;

@Data
public class CcBdtNodeDetail implements CcNodeDetail {

    private CcType type = CcType.DT;

    private String bdtId;
    private String manifestId;
    private String guid;
    private String dataTypeTerm;
    private String representationTerm;
    private String sixDigitId;
    private String qualifier;
    private String basedBdtId;
    private String basedBdtManifestId;
    private String basedBdtDen;
    private String contentComponentDefinition;
    private boolean commonlyUsed;
    private boolean deprecated;
    private String den;
    private String definition;
    private String definitionSource;
    private boolean hasNoSc;
    private String namespaceId;

    private CcState state;
    private String owner;
    private String releaseId;
    private String releaseNum;
    private String logId;
    private int revisionNum;
    private int revisionTrackingNum;
    private String spec;

    private List<CcBdtPriRestri> bdtPriRestriList;
}
