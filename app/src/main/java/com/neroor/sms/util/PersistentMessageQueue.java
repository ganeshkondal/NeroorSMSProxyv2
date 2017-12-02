package com.neroor.sms.util;

import android.util.Log;

import com.neroor.sms.SmsActivity;
import com.neroor.sms.config.Config;
import com.neroor.sms.util.VolleyHttpRequestHandler;
import com.neroor.sms.util.MessageQueue;
import com.neroor.sms.data.Message;
/**
 * Created by ganeshkondal on 01/05/17.
 */

public class PersistentMessageQueue<Message>
        extends MessageQueue<Message> {
    private VolleyHttpRequestHandler requestHandler;

    public PersistentMessageQueue(){
        super();
        requestHandler = new VolleyHttpRequestHandler();
    }

    // as add or insert gets called
    // persist to localstore (so when app dies and comes up, we can handle left over messages

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean add(Object obj ){
        Logger.print(SmsActivity.TAG, " Adding Message to Queue: " + obj.toString());

        // send to Neroor
        requestHandler.sendAppointmentRequest((com.neroor.sms.data.Message) obj);
        removeItemOnReachingMaxLimit();

        synchronized ( PersistentMessageQueue.class ) {
            return super.add((Message) obj);
        }
    }

    @Override
    public void add(int index, Object obj){
        Logger.print(SmsActivity.TAG, " Adding Message to Queue: add(index, obj): " + obj.toString());

        // send to Neroor
        requestHandler.sendAppointmentRequest((com.neroor.sms.data.Message) obj);
        removeItemOnReachingMaxLimit();
        ///super.add(index, (Message) obj);

        synchronized ( PersistentMessageQueue.class ) {
            if (null != obj && !this.contains(obj)) {
                super.add(index, (Message) obj);
            }
        }
    }

    private void removeItemOnReachingMaxLimit(){
        Logger.print(SmsActivity.TAG, " Removign Message from the Queue: ");

        if( size() >= Config.MAX_QUEUE_COUNT ) {
            // remove the first element
            synchronized ( PersistentMessageQueue.class ) {
                Logger.print(SmsActivity.TAG, " Removed Message " + remove(0));
            }
        }
    }

}
