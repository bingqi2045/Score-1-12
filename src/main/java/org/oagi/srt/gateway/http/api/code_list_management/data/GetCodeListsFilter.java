package org.oagi.srt.gateway.http.api.code_list_management.data;

import lombok.Data;

@Data
public class GetCodeListsFilter {

    private String state;
    private Boolean extensible;

}
