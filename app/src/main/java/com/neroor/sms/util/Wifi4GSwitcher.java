package com.neroor.sms.util;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

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

    static String LOG_TAG = "N_TAG_WIFI_DATA_TOGGLE";
    static long lastToggledTime = 0L;

    static {
        lastToggledTime = System.currentTimeMillis();
        Logger.print(LOG_TAG, "Initializing the last login time: !!!" + lastToggledTime);
    }

    /**
     * If Wifi is not working switch it off, so 4g starts working automatically.
     * If 4g is not working then switch on wifi so it can connect and try.
     *
     * Default : Wifi on / Mobile data on
     *
     * case 1) error sending; then wifi is the issue; switch off and send
     *
     * case 2) still issue with mobile data on - switch on wifi
     *
     * @param context
     * @return
     * @throws NoConnectionException
     */
    public static boolean toggleWifi4G(Context context) throws NoConnectionException{
        if( lastToggledTime == 0 ) lastToggledTime = System.currentTimeMillis();

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

        if( !isToggleAllowed() ) {
            Logger.print(LOG_TAG, "Retrying the toggle too quick ... so not going to allow it !!!");
            return false;
        }

        boolean isWiFi = false;
        boolean isMobileDataOn = false;
        if( null != activeNetwork ) {

            // no connection so coming down
            isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
            Logger.print(LOG_TAG, "Wifi Enabled status : " + isWiFi);

            isMobileDataOn = activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
            Logger.print(LOG_TAG, "Mobile Data Status : " + isMobileDataOn);
        } else {
            Logger.print(LOG_TAG, "activeNetwork is null. Unable to determine wifi / mobile data status: " + activeNetwork);
        }

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

            if( !isWiFi  ){ // covers two scenarios - w - off & md - off && w - off & md - on
                Logger.print(LOG_TAG, " Wifi is off");

                // no need to check 4g enabled or not
                // as wifi switch on should take care.

                boolean wifiSwitchedOn = enableWifi( context );

                if( !wifiSwitchedOn ) {
                    Logger.print(LOG_TAG, " Enabling Wifi did not work !!!!");
                    //TBD: Send SMS as all options are out
                    return false;
                }

                Logger.print(LOG_TAG, " Enabling Wifi worked !!!!");
                return wifiSwitchedOn;
            }

            if( isWiFi && isMobileDataOn ){
                // switch off wifi here - as the probability to go out with MobileData is higher.
                Logger.print(LOG_TAG, "Wifi On & Mobile Data is enabled .. Going to try switching off Wifi !!");
                boolean wifiswitchedOn = disableWifi(context);
                if( !wifiswitchedOn ) {
                    Logger.print(LOG_TAG, " Disabling Wifi didn't work !!!!");
                    //TBD: Send SMS as all options are out
                    return false;
                }
                return wifiswitchedOn;
            }
        }
        // after toggling again check for connectivity

        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        Logger.print(LOG_TAG, " After toggle checking the internet connectivity  !!!! " + isConnected );

        return isConnected;

    }


    static boolean isToggleAllowed(){
        long currentTime = System.currentTimeMillis();
        // less than one minute
        return ( currentTime - lastToggledTime < 5*1000 )?false: true; // 10 seconds for testing
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
