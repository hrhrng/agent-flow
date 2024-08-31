package com.github.hrhrng.agent.flow;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

/**
 * @author xiaoyang
 **/
class WorkflowIT {

    @Test
    public void testWorkflow() {
        Workflow<String> nextStop = new Workflow<>();
        Consumer<Event> slot = nextStop.slot();
        nextStop.addNode("LIST_GENERATE", o->listGenerate(o, slot));
        nextStop.addNode("LIST_REVIEW", o->reviewList(o, slot));
        nextStop.addConditionEdge("LIST_REVIEW", this::shouldResponse);
        nextStop.addEdge("LIST_GENERATE", "LIST_REVIEW");
        nextStop.setStartNode("LIST_GENERATE");
        nextStop.start("你好")
                .subscribe((e)-> System.out.println(Thread.currentThread().getName() + e.toString()));
        System.out.println(1);
    }

    // 生成list

    /**
     * @param o 可能有review意见
     * @return list
     */
    public String listGenerate(String o, Consumer<Event> slot) {
        System.out.println(Thread.currentThread().getName() + o);
        slot.accept(new Event("listGenerateEvent", o));
        //
        return "listGenerate" + o;
    }

    // review list
    public String reviewList(String o, Consumer<Event> slot) {

        System.out.println(Thread.currentThread().getName() + o);
        slot.accept(new Event("reviewListEvent", o));
        return "reviewList" + o;
    }

    // condition edge
    public String shouldResponse(String context) {
        // 让listAgent重新生成
        if (context.equals("regenerate")) {
            return "LIST_REVIEW";
        } else {
            return Workflow.END_NODE;
        }
    }


}