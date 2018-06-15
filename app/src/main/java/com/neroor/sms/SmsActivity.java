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
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

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

    private static final int PERMISSIONS_REQUEST_RECEIVE_SMS = 0;

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

        requestSMSReadingPermission();
    }


    private void requestSMSReadingPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_SMS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(this, "Granting SMS reading permission is necessary!", Toast.LENGTH_LONG).show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_SMS},
                        PERMISSIONS_REQUEST_RECEIVE_SMS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            Toast.makeText( this, "SMS Reading permission is already there !!! hayy !!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_RECEIVE_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Toast.makeText(this, "onRequestPermissionsResult: Granting SMS reading permission is necessary!", Toast.LENGTH_LONG).show();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(this, "onRequestPermissionsResult: Sad that we don't have SMS reading permission necessary !!!", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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

