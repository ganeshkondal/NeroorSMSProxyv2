package com.neroor.sms.db;

import com.neroor.sms.data.Message;
/**
 * Implementation class that fetches the data from Android SQLLite
 * Created by ganeshkondal on 02/05/17.
 */

public class MessageDAOImpl implements MessageDAO {
    @Override
    public void insertMessage(Message message) throws PersistenceException {

    }

    @Override
    public Message[] getUnreadMessages() throws PersistenceException {
        return new Message[0];
    }

    @Override
    public Message[] getAllMessages() throws PersistenceException {
        return new Message[0];
    }
}
