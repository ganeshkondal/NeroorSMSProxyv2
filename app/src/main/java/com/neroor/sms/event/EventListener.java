package com.neroor.sms.event;

public interface EventListener {

    public void handleEvent(EventType type, Object data);
}