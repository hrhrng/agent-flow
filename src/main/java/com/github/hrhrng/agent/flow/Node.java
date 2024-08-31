package com.github.hrhrng.agent.flow;


import java.util.function.Function;

public class Node {
    Function function;

    String identify;

    public Node(Function function, String identify) {
        this.identify = identify;
        this.function = function;
    }

    public Object run(Object o) {
        Object apply = function.apply(o);
        return apply;
    }
}
