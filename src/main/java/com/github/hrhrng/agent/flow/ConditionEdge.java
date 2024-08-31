package com.github.hrhrng.agent.flow;

import java.util.function.Function;

public class ConditionEdge<T> implements Edge {
    Function<T, String> condition;
    public ConditionEdge(Function<T, String> condition) {
        this.condition = condition;
    }
    public String next(T o) {
        return condition.apply(o);
    }
}
