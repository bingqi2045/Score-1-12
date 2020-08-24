package org.oagi.srt.gateway.http.api.account_management.data;

import lombok.Data;
import org.oagi.srt.gateway.http.api.common.data.PageRequest;

import java.util.Date;

@Data
public class PendingListRequest {

    private String preferredUsername;
    private String email;
    private String providerName;

    private Date createStartDate;
    private Date createEndDate;
    private PageRequest pageRequest;
}