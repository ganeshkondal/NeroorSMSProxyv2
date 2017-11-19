package com.neroor.sms.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import java.util.List;
import java.util.ArrayList;



public class EventManager{

    private static Map<EventType, ArrayList<EventListener>> listenerMap = new ConcurrentHashMap<EventType, ArrayList<EventListener>>();

    /**
     * Register the listener
     */
    public static void bindListener(EventType eventType, EventListener eventListener ){
        if( null != eventType && null != eventListener ) {
            if (listenerMap.get(eventType) == null) {
                ArrayList<EventListener> listenerList = new ArrayList<EventListener>();
                listenerList.add(eventListener);
                listenerMap.put(eventType, listenerList);
            } else {
                (listenerMap.get(eventType)).add(eventListener);
            }
        }
    }

    public static boolean fireEvent(EventType type, Object data){
        if( type != null ) {
            List<EventListener> listeners = listenerMap.get( type );
            //listeners.forEach( listener -> listener.handleEvent(type, data) ); source setting is still 1.7
            for(EventListener listener: listeners ){
                listener.handleEvent(type, data);
            }
        }
        return true;
    }
}