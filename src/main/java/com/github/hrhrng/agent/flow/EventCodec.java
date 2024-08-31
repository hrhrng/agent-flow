package com.github.hrhrng.agent.flow;

import com.github.hrhrng.agent.flow.internal.JsonUtils;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

import java.util.Objects;

public class EventCodec implements MessageCodec<Event, Event> {

    @Override
    public void encodeToWire(Buffer buffer, Event event) {
        // 在这里将你的 Event 对象序列化到 buffer
        byte[] serialized = Objects.requireNonNull(JsonUtils.toJson(event)).getBytes();
        buffer.appendBytes(serialized);
    }

    @Override
    public Event decodeFromWire(int position, Buffer buffer) {
        // 在这里从 buffer 反序列化你的 Event 对象
        byte[] bytes = buffer.getBytes();
        return JsonUtils.fromJson(bytes.toString(), Event.class);
    }

    @Override
    public Event transform(Event event) {
        // 如果事件不需要跨越不同的event bus，可以直接返回
        return event;
    }

    @Override
    public String name() {
        return "eventCodec";
    }

    @Override
    public byte systemCodecID() {
        return -1; // 自定义编解码器应返回 -1
    }
}
