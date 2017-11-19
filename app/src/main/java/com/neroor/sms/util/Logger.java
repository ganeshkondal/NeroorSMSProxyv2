package com.neroor.sms.util;
import android.util.Log;
/**
 * Wrapper class that makes use of the logger available OR writes to sysout
 *
 */
public class Logger{


    public static void print(String tag, Object o){
        Log.i( tag, o.toString() );
    }

    public static void print(String tag, String o){
        Log.i( tag, o );
    }

}