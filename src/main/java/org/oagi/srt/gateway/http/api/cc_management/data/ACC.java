package org.oagi.srt.gateway.http.api.cc_management.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.gateway.http.api.common.data.Trackable;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class ACC extends Trackable implements CoreComponent {

    private long accId;
    private String guid;

    private String objectClassTerm;
    private String den;
    private String definition;
    private String definitionSource;

    private Long basedAccId;
    private String objectClassQualifier;
    private Integer oagisComponentType;

    private Long moduleId;
    private Long namespaceId;

    private long createdBy;
    private long ownerUserId;
    private long lastUpdatedBy;
    private Date creationTimestamp;
    private Date lastUpdateTimestamp;

    private int state;
    private Long currentAccId;
    private boolean deprecated;
    private boolean isAbstract;

}
