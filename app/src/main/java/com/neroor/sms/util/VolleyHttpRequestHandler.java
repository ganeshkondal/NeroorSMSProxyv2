package com.neroor.sms.util;

import java.net.URLEncoder;
import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.StringRequest;
import com.neroor.sms.NeroorApp;
import com.neroor.sms.config.Config;
import com.neroor.sms.data.Message;
import com.neroor.sms.util.Logger;
import com.neroor.sms.event.EventManager;
import com.neroor.sms.event.EventType;



/**
 * Class that enables sending of request to Neroor.com to obtain apppointments
 * <p>
 * Created by ganeshkondal on 01/05/17.
 */

public class VolleyHttpRequestHandler {
    private static final String REQ_URL = "http://www.neroor.com/appt-mgr-servlet/apptmgr?"; // no need to have this in github

    //singleton queue reference
    private static RequestQueue queue = null;
    private DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy( 5000, Config.MAX_RETRY_COUNT, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT );

    public RequestQueue getRequestQueue(){
        if( null == queue ) {
            //queue = Volley.newRequestQueue(SmsActivity.getAppContext());
            queue = Volley.newRequestQueue(NeroorApp.getAppContext());
        }
        return queue;
    }

    public boolean sendAppointmentRequest(final Message message) {
        message.incrementRetryCount();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, buildNeroorGetRequest(message),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Logger.print("N_RESP", "Response is: " + response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.print("N_RESP", "That didn't work! Going to fireevent.." + error.toString() );
                //error.printStackTrace();

                //fire a message sending error
                //EventManager.fireEvent(EventType.MESSAGE_SENDING_ERROR, message);
            }
        });

        // default retry
        stringRequest.setRetryPolicy( getRetryPolicy() );
        // Add the request to the RequestQueue.
        getRequestQueue().add(stringRequest);
        return true;

    }

    private DefaultRetryPolicy getRetryPolicy(){
        return retryPolicy;
    }

    private static String buildNeroorGetRequest(Message message) {
        if (null == message || message.getSenderMDN() == null || message.getMessage() == null) {
            Logger.print("N_RESP", "Message received is null, so not constructing any URL to neroor ");
        }
        StringBuilder builder = new StringBuilder();
        builder.append(REQ_URL);
        builder.append("phone=");
        //builder.append("+91");
        builder.append(message.getSenderMDN());
        builder.append("&");
        builder.append("binary");
        builder.append("=");
        String encodedMessage = null;
        if( message.getMessage() != null ) {
            builder.append(URLEncoder.encode(message.getMessage()));
        }
        Logger.print("N_RESP", "URL to send data: " + builder.toString());
        return builder.toString();
    }

}
