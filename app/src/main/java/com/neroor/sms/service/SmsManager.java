package com.neroor.sms.service;

import com.neroor.sms.SmsActivity;
import com.neroor.sms.data.Message;
import com.neroor.sms.util.Logger;
import com.neroor.sms.util.MessageQueue;
import com.neroor.sms.util.PersistentMessageQueue;
/**
 * Primary class that does the work OR enables the receiver / activity to do the work
 * Activity does not need to know the way to get the queue.
 * Samething with the broadcast receiver (SMS receiver). They will come to this service to get what they need.
 * Doing this way, the broadcast receiver will get the messageList even after the Activity has been destroyed
 */
public class SmsManager{
    private static MessageQueue<Message> smsMessagesList = new PersistentMessageQueue<Message>();


    public static MessageQueue<Message> getMessageQueue(){
        return smsMessagesList;
    }


    /**
     * Called by the SMS handler. updates the backing list (MessageQueue)
     */
    public static void updateList(final Message smsMessage) {

        // add to the backing list
        smsMessagesList.add( smsMessage );

        SmsActivity inst = SmsActivity.instance();
        if( null != inst ) {
            //inst.updateList(messageReceived);
            inst.notifyListUpdation();
        }

    }

}