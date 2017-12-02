package com.neroor.sms;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Context;

import com.neroor.sms.data.Message;
import com.neroor.sms.util.PersistentMessageQueue;
import com.neroor.sms.util.MessageQueue;
import com.neroor.sms.service.SmsManager;
import com.neroor.sms.util.Wifi4GSwitcher;
import java.util.List;
/**
 * Primary activity in this app. Provides a list view of messages received /handled.
 * Makes use of a persistent message queue as the view model holder (via the ArrayAdapter)
 * ViewModel holder (MesssageQueue) internally forwards it to the destination(as a ProxyPass in Apache)
 * On error sending, it stores in a local DB (for retries).
 * Some of the above are yet to be done
 * @author Ganesh Kondal
 * @ver 1.0
 */
public class SmsActivity extends Activity implements OnItemClickListener {

    public static final String TAG = "Neroor";
    private static SmsActivity inst;
    MessageQueue<Message> smsMessagesList = SmsManager.getMessageQueue();
    ListView smsListView;
    ArrayAdapter<Message> arrayAdapter;
    static Context appContext;

    public static SmsActivity instance() {
        return inst;
    }

    public static Context getAppContext(){
        return appContext;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = this.getApplicationContext();
        setContentView(R.layout.activity_sms);
        smsListView = (ListView) findViewById(R.id.SMSList);
        arrayAdapter = new ArrayAdapter<Message>(this, android.R.layout.simple_list_item_1, (List)smsMessagesList);
        smsListView.setAdapter(arrayAdapter);
        smsListView.setOnItemClickListener(this);

        // For release v1 - we will support what we receive when the app is active or called.
        // No persistence support OR reading from SMS inbox
        //refreshSmsInbox();
    }

    public void refreshSmsInbox() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = smsInboxCursor.getColumnIndex("body");
        int indexAddress = smsInboxCursor.getColumnIndex("address");
        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return;
        arrayAdapter.clear();
        do {
            String str = "SMS From: " + smsInboxCursor.getString(indexAddress) +
                    "\n" + smsInboxCursor.getString(indexBody) + "\n";
            arrayAdapter.add(new Message(smsInboxCursor.getString(indexAddress), smsInboxCursor.getString(indexBody) ));
        } while (smsInboxCursor.moveToNext());
    }

    /**
     * Called by the SMS handler. updates the backing list (MessageQueue)
     */
    private void updateList(final Message smsMessage) {

        print( "Message List size : " + smsMessagesList.size());
        arrayAdapter.insert(smsMessage, 0);
        //arrayAdapter.add(smsMessage) // comes to the last in the view
        arrayAdapter.notifyDataSetChanged();
        print( "Message List size : Post adapter Change: " + smsMessagesList.size());
    }

    /**
     * Called by the SMS handler. updates the backing list (MessageQueue)
     */
    public void notifyListUpdation() {
        print( "Message List size : " + smsMessagesList.size());
        //arrayAdapter.insert(smsMessage, 0);
        //arrayAdapter.add(smsMessage) // comes to the last in the view
        arrayAdapter.notifyDataSetChanged();
        print( "Message List size : Post adapter Change: " + smsMessagesList.size());
    }

    /**
     * Show the message to String on click
     * @param parent
     * @param view
     * @param pos
     * @param id
     */
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        try {
            Message smsMessage = smsMessagesList.get(pos);
            Toast.makeText(this, smsMessage.toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void print(Object o){
        System.out.println( o );
    }

    public void onStop(){

        print("onStop() called. Neroor App is going to be stopped");
        super.onStop();
    }

    public void onDestroy(){

        print("onDestroy() called. Neroor App is going to be destroyed");
        super.onDestroy();
    }
}

