package com.github.hrhrng.agent.flow;


import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class Workflow<ContextType> {
    EventBus eventBus = Vertx.vertx().eventBus();

    {
        eventBus.registerDefaultCodec(Event.class, new EventCodec());
    }


    // node
    Map<String, Node> nodes = new HashMap<>();
    /**
     * condition or edge condition
     * 一个node只能有一个边
     */
    Map<String, Edge> edges = new HashMap<>();

    String startNodeName;

    public static final String END_NODE = "END";

    /**
     * 外部感知
     */
    Sinks.Many<Event> sink = Sinks.many().multicast().onBackpressureBuffer();

    public Workflow() {
    }

    public Consumer<Event> slot() {
        return (e) -> {
            eventBus.publish("workflow.event", e);
        };
    }

    public void setStartNode(String startNodeName) {
        if (!nodes.containsKey(startNodeName)) throw new RuntimeException("start node not exists");
        this.startNodeName = startNodeName;
    }

    public void addNode(String name, Function<ContextType, ContextType> function) {
        if (nodes.containsKey(name)) throw new RuntimeException("node name already exists");
        nodes.put(name, new Node(function, name));
    }

    public void addEdge(String from, String toNode) {
        if (edges.containsKey(from)) throw new RuntimeException("edge already exists");
        if (!nodes.containsKey(toNode)) throw new RuntimeException("to node not exists");
        if (!nodes.containsKey(from)) throw new RuntimeException("from node not exists");
        SingleEdge singleEdge = new SingleEdge(toNode);
        edges.put(from, singleEdge);
    }

    public void addConditionEdge(String from, /*String为nodeName*/Function<ContextType, String> condition) {
        if (edges.containsKey(from)) throw new RuntimeException("edge already exists");
        ConditionEdge<ContextType> conditionEdge = new ConditionEdge<>(condition);
        edges.put(from, conditionEdge);
    }

    public Flux<Event> start(ContextType input) {
        eventBus.<JsonObject>consumer("workflow.node_end", message -> {
            JsonObject body = message.body();
            onNodeEnd(body.getString("nodeName"), (ContextType)body.getValue("data"));
        });
        eventBus.<Event>consumer("workflow.flow_end", message -> {
            sink.tryEmitNext(message.body());
            sink.tryEmitComplete();
        });
        eventBus.<Event>consumer("workflow.event", message -> {
            sink.tryEmitNext(message.body());
        });
        runNode(startNodeName, input);
        return sink.asFlux();
    }

    void onNodeEnd(String nodeName, ContextType data) {
        var edge = edges.get(nodeName);
        if (edge instanceof SingleEdge) {
            runNode(((SingleEdge) edge).to, data);
        } else if (edge instanceof ConditionEdge) {
            String toNode = ((ConditionEdge) edge).next(data);
            runNode(toNode, data);
        }
    }


    void runNode(String nodeName, ContextType o) {
        if (nodeName.equals(END_NODE)) {
            eventBus.publish("workflow.flow_end", new Event("END", o));
        }
        Object run = nodes.get(nodeName).run(o);
        eventBus.publish("workflow.node_end", (new JsonObject()).put("nodeName", nodeName).put("data", run));
    }

}
