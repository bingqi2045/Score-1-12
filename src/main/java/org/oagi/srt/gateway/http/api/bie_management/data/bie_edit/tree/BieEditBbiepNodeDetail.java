package org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.tree;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.BieEditAgencyIdList;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.BieEditCodeList;
import org.oagi.srt.gateway.http.api.bie_management.data.bie_edit.BieEditXbt;

import java.util.Collections;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class BieEditBbiepNodeDetail extends BieEditBbiepNode {

    private int cardinalityMin;
    private int cardinalityMax;
    private boolean nillable;
    private String fixedValue;
    private String bizTerm;
    private String remark;

    private long bdtId;
    private String bdtDen;

    private Long bdtPriRestriId;
    private Long codeListId;
    private Long agencyIdListId;

    private String contextDefinition;
    private String associationDefinition;
    private String componentDefinition;

    public BieEditBbiepNodeDetail append(BieEditBbiepNode bbiepNode) {

        this.setTopLevelAbieId(bbiepNode.getTopLevelAbieId());
        this.setReleaseId(bbiepNode.getReleaseId());
        this.setType(bbiepNode.getType());
        this.setName(bbiepNode.getName());
        this.setUsed(bbiepNode.isUsed());
        this.setHasChild(bbiepNode.isHasChild());

        this.setBbieId(bbiepNode.getBbieId());
        this.setBccId(bbiepNode.getBccId());
        this.setBbiepId(bbiepNode.getBbiepId());
        this.setBccpId(bbiepNode.getBccpId());
        this.setAttribute(bbiepNode.isAttribute());

        return this;
    }
}
