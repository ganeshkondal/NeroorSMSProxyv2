package com.neroor.sms.handler;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.neroor.sms.autoresponder.AutoResponderFactory;
import com.neroor.sms.autoresponder.AutoResponderType;
import com.neroor.sms.gatekeeper.Gatekeeper;
import com.neroor.sms.data.Message;
import com.neroor.sms.service.SmsManager;
import com.neroor.sms.util.Logger;

/**
 * SMS handler. Used to receive the message
 */
public class SmsHandler extends WakefulBroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsMessageStr = "";
            Message messageReceived = null;
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                messageReceived = new Message(smsMessage.getOriginatingAddress(), smsMessage.getMessageBody().toString() );
                Logger.print("N_TAG", "onReceive: " + messageReceived.toString() );
                smsMessageStr += "SMS From: " + messageReceived.getSenderMDN() + "\n";
                smsMessageStr += messageReceived.getMessage() + "\n";
            }
            Toast.makeText(context, smsMessageStr, Toast.LENGTH_SHORT).show();

            SmsManager.updateList( messageReceived );

            // show throw an event rather than calling SmsManager; hence manager and auto responder will get it
            // for now- hardwire them; sucks; but got to make this work in an hour.
            if( Gatekeeper.isFeatureAllowed(AutoResponderType.SMS_RESPONDER, SmsHandler.class )) {
                AutoResponderFactory.getAutoResponder(AutoResponderType.SMS_RESPONDER).autoRespond(messageReceived);
            }
        }
    }
}