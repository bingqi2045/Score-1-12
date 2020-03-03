package org.oagi.srt.gateway.http.api.graph;

import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class Edge {

    private Set<String> targets = new LinkedHashSet();

    public void addTarget(String target) {
        targets.add(target);
    }
}
