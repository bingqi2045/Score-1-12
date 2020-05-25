package org.oagi.srt.gateway.http.api.cc_management.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CcId {
    private String type;
    private BigInteger manifestId;
}
