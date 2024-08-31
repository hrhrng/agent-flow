package com.github.hrhrng.agent.flow;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Event {
    // end or other
    String name;
    Object data;
}
