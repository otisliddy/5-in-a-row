package com.otisliddy.fiveinarow.util;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

/**
 * Generic frame handler for listening to subscribed STOMP sessions.
 *
 * @param <T>
 *            The data type the subscribed message payload should be converted to.
 */
public class GenericFrameHandler<T> implements StompFrameHandler {
    private BlockingQueue<T> responseQueue = new LinkedBlockingQueue<>();
    private final Class<T> type;

    public GenericFrameHandler(Class<T> type) {
        this.type = type;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return type;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object object) {
        responseQueue.offer((T) object);
    }

    public T getRespoonse() {
        try {
            return responseQueue.poll(1L, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            throw new RuntimeException(exception);
        }
    }
}
