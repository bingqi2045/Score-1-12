package org.oagi.srt.gateway.http.bie_management;

import org.oagi.srt.gateway.http.bie_management.bie_edit.BieEditAbie;
import org.oagi.srt.gateway.http.bie_management.bie_edit.BieEditAcc;
import org.oagi.srt.gateway.http.bie_management.bie_edit.BieEditAsbiep;
import org.oagi.srt.gateway.http.bie_management.bie_edit.BieEditAsccp;

import java.util.HashMap;
import java.util.Map;

public class BieTree {

    private BieTreeNode node;

    private Map<Long, BieEditAbie> abieMap = new HashMap();
    private Map<Long, BieEditAcc> accMap = new HashMap();

    private Map<Long, BieEditAsbiep> asbiepMap = new HashMap();
    private Map<Long, BieEditAsccp> asccpMap = new HashMap();

    public BieTreeNode getNode() {
        return node;
    }

    public BieTree(BieEditAsbiep asbiep, BieEditAsccp asccp) {
        this.node = createNode(asbiep, asccp);
    }

    public BieTreeNode createNode(BieEditAsbiep asbiep, BieEditAsccp asccp) {
        BieTreeNode node = new BieTreeNode("ASBIEP");
        node.setBieId(asbiep.getAsbiepId());
        node.setCcId(asccp.getAsccpId());

        asbiepMap.putIfAbsent(asbiep.getAsbiepId(), asbiep);
        asccpMap.putIfAbsent(asccp.getAsccpId(), asccp);

        return this.node;
    }

    public BieTreeNode createNode(BieEditAbie abie, BieEditAcc acc) {
        BieTreeNode node = new BieTreeNode("ABIE");
        node.setBieId(abie.getAbieId());
        node.setCcId(acc.getAccId());

        abieMap.putIfAbsent(abie.getAbieId(), abie);
        accMap.putIfAbsent(acc.getAccId(), acc);

        return this.node;
    }
}
