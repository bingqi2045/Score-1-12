package org.oagi.srt.gateway.http.bie_management;

import org.oagi.srt.gateway.http.bie_management.bie_edit.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BieTree {

    private BieTreeNode node;

    private Map<Long, BieEditAbie> abieMap = new ConcurrentHashMap();
    private Map<Long, BieEditAcc> accMap = new ConcurrentHashMap();

    private Map<Long, BieEditAsbie> asbieMap = new ConcurrentHashMap();
    private Map<Long, BieEditAscc> asccMap = new ConcurrentHashMap();

    private Map<Long, BieEditBbie> bbieMap = new ConcurrentHashMap();
    private Map<Long, BieEditBcc> bccMap = new ConcurrentHashMap();

    private Map<Long, BieEditAsbiep> asbiepMap = new ConcurrentHashMap();
    private Map<Long, BieEditAsccp> asccpMap = new ConcurrentHashMap();

    private Map<Long, BieEditBbiep> bbiepMap = new ConcurrentHashMap();
    private Map<Long, BieEditBccp> bccpMap = new ConcurrentHashMap();

    private Map<Long, BieEditBbieSc> bbieScMap = new ConcurrentHashMap();
    private Map<Long, BieEditBdtSc> bdtScMap = new ConcurrentHashMap();


    public BieTreeNode getNode() {
        return node;
    }

    public BieTree(BieEditAsbiep asbiep, BieEditAsccp asccp) {
        this.node = createNode(asbiep, asccp);
    }

    public BieTreeNode createNode(BieEditAbie abie, BieEditAcc acc) {
        BieTreeNode node = new BieTreeNode("ABIE");

        if (abie != null) {
            node.setBieId(abie.getAbieId());
            abieMap.putIfAbsent(abie.getAbieId(), abie);
        }

        node.setCcId(acc.getAccId());
        accMap.putIfAbsent(acc.getAccId(), acc);

        return node;
    }

    public BieTreeNode createNode(BieEditBbie bbie, BieEditBcc bcc) {
        BieTreeNode node = new BieTreeNode("BBIE");

        if (bbie != null) {
            node.setBieId(bbie.getBbieId());
            bbieMap.putIfAbsent(bbie.getBbieId(), bbie);
        }

        node.setCcId(bcc.getBccId());
        bccMap.putIfAbsent(bcc.getBccId(), bcc);

        return node;
    }

    public BieTreeNode createNode(BieEditAsbie asbie, BieEditAscc ascc) {
        BieTreeNode node = new BieTreeNode("ASBIE");

        if (asbie != null) {
            node.setBieId(asbie.getAsbieId());
            asbieMap.putIfAbsent(asbie.getAsbieId(), asbie);
        }

        node.setCcId(ascc.getAsccId());
        asccMap.putIfAbsent(ascc.getAsccId(), ascc);

        return node;
    }

    public BieTreeNode createNode(BieEditAsbiep asbiep, BieEditAsccp asccp) {
        BieTreeNode node = new BieTreeNode("ASBIEP");

        if (asbiep != null) {
            node.setBieId(asbiep.getAsbiepId());
            asbiepMap.putIfAbsent(asbiep.getAsbiepId(), asbiep);
        }

        node.setCcId(asccp.getAsccpId());
        asccpMap.putIfAbsent(asccp.getAsccpId(), asccp);

        return node;
    }

    public BieTreeNode createNode(BieEditBbiep bbiep, BieEditBccp bccp) {
        BieTreeNode node = new BieTreeNode("BBIEP");

        if (bbiep != null) {
            node.setBieId(bbiep.getBbiepId());
            bbiepMap.putIfAbsent(bbiep.getBbiepId(), bbiep);
        }

        node.setCcId(bccp.getBccpId());
        bccpMap.putIfAbsent(bccp.getBccpId(), bccp);

        return node;
    }

    public BieTreeNode createNode(BieEditBbieSc bbieSc, BieEditBdtSc bdtSc) {
        BieTreeNode node = new BieTreeNode("BBIE_SC");

        if (bbieSc != null) {
            node.setBieId(bbieSc.getBbieScId());
            bbieScMap.putIfAbsent(bbieSc.getBbieScId(), bbieSc);
        }

        node.setCcId(bdtSc.getDtScId());
        bdtScMap.putIfAbsent(bdtSc.getDtScId(), bdtSc);

        return node;
    }
}
