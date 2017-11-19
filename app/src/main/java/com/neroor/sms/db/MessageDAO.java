package com.neroor.sms.db;

/** Class to have APIs that deal with the SQLLite table
 *
 * Created by ganeshkondal on 02/05/17.
 */

import com.neroor.sms.data.Message;

public interface MessageDAO {

    /*
        Persists the messages as they come
      */
    public void insertMessage(Message message) throws PersistenceException;

    public Message[] getUnreadMessages() throws PersistenceException;

    public Message[] getAllMessages() throws PersistenceException;



}

enum NEROOR_DB_COLUMNS {
    ID,
    SENDER_MDN,
    MESSAGE_RECEIVED,
    RECEIVED_TIME,
    STATUS,
    SENT_TIME,
    RETRY_COUNT
}

