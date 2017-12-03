package com.neroor.sms.handler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.neroor.sms.util.Logger;
import com.neroor.sms.util.VolleyHttpRequestHandler;

/**
 * Created by ganeshkondal on 02/12/17.
 */

public class BatteryLevelReceiver extends BroadcastReceiver{


    private static VolleyHttpRequestHandler requestHandler = VolleyHttpRequestHandler.getInstance();


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if( null != action && action == Intent.ACTION_BATTERY_LOW ){
            Logger.print("N_TAG", "Battery level low: " + action );
            requestHandler.sendMessage("Battery low :" + action );
            // message a list of users to charge
        } else if( null != action && action == Intent.ACTION_BATTERY_OKAY ){
            Logger.print("N_TAG", "Battery level Okay: " + action );
            requestHandler.sendMessage("Battery okay :" + action );
        } else {
            Logger.print("N_TAG", "Battery level action: " + action );
            requestHandler.sendMessage("Battery action :" + action );

        }
    }
}
