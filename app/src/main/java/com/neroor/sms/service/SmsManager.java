package com.neroor.sms.service;

import com.neroor.sms.NeroorApp;
import com.neroor.sms.SmsActivity;
import com.neroor.sms.config.Config;
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
public class SmsManager implements EventListener {
    private static MessageQueue<Message> smsMessagesList = new PersistentMessageQueue<Message>();

    private static final SmsManager instance = new SmsManager();

    public static MessageQueue<Message> getMessageQueue(){
        return smsMessagesList;
    }

    public SmsManager(){
        EventManager.bindListener(EventType.MESSAGE_SENDING_ERROR, this );
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

    public String getName(){
        return "SMS_Manager_EventListener";
    }

    @Override
    public void handleEvent(EventType type, Message message) {
        Logger.print("N_TAG", "SMSManager.handleEvent(): " + message.toString());
        switch(type){
            case TOGGLE_WIFI_4G:
                this.handleWifiToggleEvent(type, message);
                return;
            case MESSAGE_SENDING_ERROR:
                this.handleSendingError(type, message);
                return;
            default:
                return;
        }
    }

    private boolean handleSendingError(EventType type, Message message){
        if( message.getRetryCount() < Config.MAX_RETRY_COUNT ){
            return smsMessagesList.add( message );
        } else {
            Logger.print("N_TAG", "Going to drop message as it has crossed the retry count : " + message.toString());
            return false;
        }

    }

    /**
     * Wrote this for a different reason; now this event type isn't thrown
     * as it is error prone. For now keeping the code.
     * @param type
     * @param message
     * @return
     */
    private boolean handleWifiToggleEvent(EventType type, Message message){
        if( null != type && type.equals(EventType.TOGGLE_WIFI_4G)){
            try {
                boolean wifiToggleStatus = Wifi4GSwitcher.toggleWifi4G(NeroorApp.getAppContext());
                if( !wifiToggleStatus ){
                    // drop the message, as can't go in a recursive loop
                    Logger.print("N_TAG", message.toString());
                    return false;
                } else {
                    // do a readd
                    // messageList will take care of duplicate
                    smsMessagesList.add( message );
                }
            } catch (NoConnectionException e) {
                Logger.print("N_TAG", "Exception toggling wifi : " + e.toString());
            }
        }
        return true;
    }


//    @Override
//    public void handleEvent(EventType type, Message message) {
//
//    }
}