package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditBbieScNodeDetail extends BieEditBbieScNode {

    private int cardinalityMin;
    private int cardinalityMax;
    private String fixedValue;
    private String defaultValue;
    private String bizTerm;
    private String remark;

    private String contextDefinition;
    private String componentDefinition;

    public BieEditBbieScNodeDetail append(BieEditBbieScNode bbieScNode) {

        this.setTopLevelAbieId(bbieScNode.getTopLevelAbieId());
        this.setReleaseId(bbieScNode.getReleaseId());
        this.setType(bbieScNode.getType());
        this.setName(bbieScNode.getName());
        this.setUsed(bbieScNode.isUsed());
        this.setHasChild(bbieScNode.isHasChild());

        this.setBbieScId(bbieScNode.getBbieScId());
        this.setDtScId(bbieScNode.getDtScId());

        return this;
    }
}
