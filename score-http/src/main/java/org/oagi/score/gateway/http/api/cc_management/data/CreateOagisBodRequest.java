package org.oagi.score.gateway.http.api.cc_management.data;

import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class CreateOagisBodRequest {

    private List<String> verbManifestIdList;

    private List<String> nounManifestIdList;

}
