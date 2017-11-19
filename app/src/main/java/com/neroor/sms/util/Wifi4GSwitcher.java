package com.neroor.sms.util;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import android.net.wifi.WifiManager;
import android.content.Context;


/**
 * When called checks for internet connection. Based on that switches from wifi to 4g OR from 4g to wifi automatically.
 *
 * <p/>
 *
 * Created by ganeshkondal on 11/11/17.
 */

public class Wifi4GSwitcher {

    enum CONNECTION_TYPE {
        MOBILE_WIFI, MOBILE_DATA
    }

    static String LOG_TAG = "WIFI_DATA_TOGGLE";

    /**
     * If Wifi is not working switch it off, so 4g starts working automatically.
     * If 4g is not working then switch on wifi so it can connect and try.
     *
     * @param context
     * @return
     * @throws NoConnectionException
     */
    static boolean toggleWifi4G(Context context) throws NoConnectionException{

        boolean toggleComplete = false;
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if( isConnected ) {
            Logger.print(LOG_TAG, "Internet Connect is there, so returning as is !!!");
            return true;
        }

        Logger.print(LOG_TAG, "Internet Connect is NOT there, so going to toggle !!!");

        // no connection so coming down
        boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        Logger.print(LOG_TAG, "Wifi Enabled status : " + isWiFi);

        boolean isMobileDataOn = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        Logger.print(LOG_TAG, "Mobile Data Status : " + isMobileDataOn);

        if( !isConnected ){

            if( isWiFi && !isMobileDataOn ) {
                Logger.print(LOG_TAG, "Wifi Off & Mobile Data is enabled !!");
                disableWifi(context);
                boolean mobileDataSwitchedOn = enableMobileData(context);

                if( !mobileDataSwitchedOn ) {
                    Logger.print(LOG_TAG, " Enabling Mobile Data didn't work !!!!");

                    //TBD: Send SMS as all options are out
                    return false;
                }
                return mobileDataSwitchedOn;
            }

            if( !isWiFi  ){
                Logger.print(LOG_TAG, " Wifi is off");

                // no need to check 4g enabled or not
                // as wifi switch on should take care.

                boolean wifiSwitchedOn = enableWifi( context );

                if( !wifiSwitchedOn ) {
                    Logger.print(LOG_TAG, " Enabling Wifi work !!!!");
                    //TBD: Send SMS as all options are out
                    return false;
                }

                Logger.print(LOG_TAG, " Enabling Mobile Data DID work !!!!");
                return wifiSwitchedOn;
            }
        }
        return true;

    }

    static boolean disableWifi(Context context){
        Logger.print(LOG_TAG, " Disabling Wifi");
        return setWifiEnabled(context, false);
    }

    static boolean enableWifi(Context context){
        Logger.print(LOG_TAG, " Enabling Wifi");
        return setWifiEnabled(context, true);
    }

    static boolean setWifiEnabled(Context context, boolean enable){
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wifi.setWifiEnabled(enable);

    }

    static boolean enableMobileData(Context context)throws NoConnectionException{
        Logger.print(LOG_TAG, " Enabling Mobile data");

        return setMobileDataEnabled(context, true);
    }

//    static boolean disableMobileData(Context context)throws NoConnectionException{
//        return setMobileDataEnabled(context, false);
//    }

    static boolean setMobileDataEnabled(Context context, boolean enable) throws NoConnectionException{

        final ConnectivityManager conman =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            final Class conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class iConnectivityManagerClass = Class.forName(
                    iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass
                    .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, enable);

            return true;

        } catch (ClassNotFoundException e) {
            throw new NoConnectionException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new NoConnectionException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new NoConnectionException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new NoConnectionException(e.getMessage());
        } catch (NoSuchFieldException e) {
            throw new NoConnectionException(e.getMessage());
        }
    }

}
