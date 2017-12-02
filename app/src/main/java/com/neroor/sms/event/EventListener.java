package com.neroor.sms.event;

import com.neroor.sms.data.Message;

public interface EventListener {

    public void handleEvent(EventType type, Message data);

    public String getName();
}