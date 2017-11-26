package com.neroor.sms.service;

import com.neroor.sms.NeroorApp;
import com.neroor.sms.SmsActivity;
import com.neroor.sms.data.Message;
import com.neroor.sms.event.EventListener;
import com.neroor.sms.event.EventManager;
import com.neroor.sms.event.EventType;
import com.neroor.sms.util.Logger;
import com.neroor.sms.util.MessageQueue;
import com.neroor.sms.util.NoConnectionException;
import com.neroor.sms.util.PersistentMessageQueue;
import com.neroor.sms.util.Wifi4GSwitcher;

/**
 * Primary class that does the work OR enables the receiver / activity to do the work
 * Activity does not need to know the way to get the queue.
 * Samething with the broadcast receiver (SMS receiver). They will come to this service to get what they need.
 * Doing this way, the broadcast receiver will get the messageList even after the Activity has been destroyed
 */
public class SmsManager {
    private static MessageQueue<Message> smsMessagesList = new PersistentMessageQueue<Message>();


    public static MessageQueue<Message> getMessageQueue(){
        return smsMessagesList;
    }

    static {
        EventManager.bindListener(EventType.TOGGLE_WIFI_4G, new EventListener() {
            @Override
            public void handleEvent(EventType type, Message data) {
                Logger.print("N_TAG", "SMSManager.handleEvent(): " + message.toString());
                if( null != type && type.equals(EventType.TOGGLE_WIFI_4G)){
                    try {
                        boolean wifiToggleStatus = Wifi4GSwitcher.toggleWifi4G(NeroorApp.getAppContext());
                        if( !wifiToggleStatus ){
                            // drop the message, as can't go in a recursive loop
                            Logger.print("N_TAG", message.toString());
                            return;
                        } else {
                            // do a readd
                            // messageList will take care of duplicates
                            smsMessagesList.add( message );
                        }
                    } catch (NoConnectionException e) {
                        Logger.print("N_TAG", "Exception toggling wifi : " + e.toString());
                    }
                }
            }
        });
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


//    @Override
//    public void handleEvent(EventType type, Message message) {
//
//    }
}