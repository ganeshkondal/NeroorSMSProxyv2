package com.neroor.sms.autoresponder;

import com.neroor.sms.data.Message;

public class AutoResponderFactory{
            private static final AutoResponder smsResponder = new SMSAutoResponder();

            public static AutoResponder getAutoResponder(AutoResponderType type){
                switch(type){
                    case SMS_RESPONDER:
                        return smsResponder;
                    default:
                        return smsResponder;


                }
            }
}