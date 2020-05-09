package org.oagi.srt.gateway.http.api.code_list_management.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

@Data
public class DeleteCodeListRequest {

    private List<BigInteger> codeListIds = Collections.emptyList();
}
