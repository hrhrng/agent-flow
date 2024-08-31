package com.github.hrhrng.agent.flow;

public class SingleEdge implements Edge {
    String to;
    public SingleEdge(String to) {
        this.to = to;
    }

    public String next() {
        return to;
    }
}
