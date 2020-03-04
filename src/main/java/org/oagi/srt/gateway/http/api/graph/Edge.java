package org.oagi.srt.gateway.http.api.graph;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Edge {

    private List<String> targets = new ArrayList();

    public void addTarget(String target) {
        targets.add(target);
    }
}
