package com.neroor.sms.autoresponder;

import com.neroor.sms.data.Message;

public interface AutoResponder{

    public boolean autoRespond(Message message);
        }