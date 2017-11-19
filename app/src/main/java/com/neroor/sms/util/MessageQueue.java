package com.neroor.sms.util;

/**
 * Created by ganeshkondal on 29/04/17.
 */
import java.util.ArrayList;
import com.neroor.sms.data.Message;

public abstract class MessageQueue<Message> extends ArrayList<Message> {

    protected static final int MAX_MESSAGE_COUNT = 100;
}
