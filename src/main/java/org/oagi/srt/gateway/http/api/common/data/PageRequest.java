package org.oagi.srt.gateway.http.api.common.data;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public class PageRequest {

    private String sortActive;
    private String sortDirection;
    private int pageIndex;
    private int pageSize;
}
