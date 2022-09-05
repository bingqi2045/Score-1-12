package org.oagi.score.service.codelist.model;

import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class CodeListUpliftingResponse {

    private String codeListManifestId;

    private List<String> duplicatedValues;

}
