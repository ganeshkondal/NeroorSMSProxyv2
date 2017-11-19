package com.neroor.sms.autoresponder;

import com.neroor.sms.data.Message;
import com.neroor.sms.util.Logger;

/**
 *
 */
public class SMSAutoResponder implements AutoResponder {

    public boolean autoRespond(Message message){
        if( null != message ){
            Logger.print("N_TAG", "SMSAutoResponder: " + message.toString() );
        }
        return true;
    }

}