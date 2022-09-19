package org.oagi.score.gateway.http.api.cc_management.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CcId {
    private String type;
    private String manifestId;

    public CcId(CcType type, String manifestId) {
        this.type = type.name().toLowerCase();
        this.manifestId = manifestId;
    }
}
