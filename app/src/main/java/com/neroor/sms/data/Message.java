package com.neroor.sms.data;

/**
 * Represents the message received
 * Created by ganeshkondal on 29/04/17.
 */
import java.util.Date;


public class Message {
    private String senderMDN = null;
    private String message = null;
    private Date receivedTime = null;
    private MessageStatus status;


    public Message(String mdn, String bodyMessage){
        receivedTime = java.util.Calendar.getInstance().getTime();
        senderMDN = mdn;
        message = bodyMessage;
    }

    public String getSenderMDN() {
        return senderMDN;
    }

    public void setSenderMDN(String senderMDN) {
        this.senderMDN = senderMDN;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(Date receivedTime) {
        this.receivedTime = receivedTime;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message1 = (Message) o;

        if (senderMDN != null ? !senderMDN.equals(message1.senderMDN) : message1.senderMDN != null)
            return false;
        if (message != null ? !message.equals(message1.message) : message1.message != null)
            return false;
        return receivedTime != null ? receivedTime.equals(message1.receivedTime) : message1.receivedTime == null;

    }

    @Override
    public int hashCode() {
        int result = senderMDN != null ? senderMDN.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (receivedTime != null ? receivedTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return
                message + "  \nfrom:"  + senderMDN;

    }


    public String printAsString() {
        return "Message{" +
                "senderMDN='" + senderMDN + '\'' +
                ", message='" + message + '\'' +
                ", receivedTime=" + receivedTime +
                '}';
    }
}
