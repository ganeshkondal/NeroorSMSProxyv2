package com.neroor.sms;

import android.app.Application;
import android.content.Context;

/**
 * Application subclass to get the application context
 * Application Context is used to create the VolleyHttpRequestHandler singleton instance
 *
 * Created by ganeshkondal on 29/05/17.
 */

public class  NeroorApp extends Application {

    private static Context appContext = null;

    public NeroorApp() {
        super();
    }

    @Override
    public void onCreate(){
        super.onCreate();
        appContext = getApplicationContext();
    }

    public static Context getAppContext(){
        return appContext;
    }
}
