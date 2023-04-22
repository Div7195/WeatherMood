package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ShareActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private final static int REQUEST_CODE = 100;
    String message;
    ListView listView;
    ProgressBar loadingViewSearch;
    String [] name = {};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast.makeText(this, "hetwerwe", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        SmsManager smsManager = SmsManager.getDefault();
        loadingViewSearch = findViewById(R.id.loadingProgressBar2);
        listView = findViewById(R.id.listviewContacts);

//        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,name);
        List<Contact> contactString = new ArrayList<>();
        ContactsAdapter contactsAdapter = new ContactsAdapter(ShareActivity.this, (ArrayList<Contact>) contactString);
        listView.setAdapter(contactsAdapter);
//        loadingViewSearch = findViewById(R.id.loadingProgressBar2);
//        loadingViewSearch.setVisibility(View.GONE);
        Bundle b = new Bundle();
        b = getIntent().getExtras();
        message = b.getString("EXTRA_SMSBODY");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Cursor cursor_contact = null;
        ContentResolver contentResolver = getContentResolver();
        List<Contact> contactString = new ArrayList<>();

        try{
            cursor_contact = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        }catch (Exception e){
            Log.d("Error on contact", e.getMessage());
        }

        if(cursor_contact.getCount() > 0){
            while (cursor_contact.moveToNext()) {
                    Contact contact_data = new Contact();
                    @SuppressLint("Range") String contact_id = cursor_contact.getString(cursor_contact.getColumnIndex(ContactsContract.Contacts._ID));
                    @SuppressLint("Range") String contact_display_name = cursor_contact.getString(cursor_contact.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    contact_data.contact_name = contact_display_name;

                    @SuppressLint("Range") int hasPhoneNumber = Integer.parseInt(cursor_contact.getString(cursor_contact.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                    if(hasPhoneNumber > 0){
                        Cursor phoneCursor = contentResolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                                ,null
                                ,ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
                                ,new String[]{contact_id}
                                ,null);
                        while(phoneCursor.moveToNext()){
                            @SuppressLint("Range") String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contact_data.contact_number = phoneNumber;
                        }
                        phoneCursor.close();
                        contactString.add(contact_data);
                    }
            }
        }


        ContactsAdapter contactsAdapter = new ContactsAdapter(ShareActivity.this, (ArrayList<Contact>) contactString);
        listView.setAdapter(contactsAdapter);
        getMenuInflater().inflate(R.menu.menu1,menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setIconified(false);
        searchView.setQueryHint("Search contact");
        loadingViewSearch.setVisibility(View.GONE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact selectedContact = (Contact) parent.getItemAtPosition(position);
//                String selectedItem = (String) parent.getItemAtPosition(position);
                searchView.setQuery(selectedContact.getSearchString(), true);
                listView.setVisibility(View.GONE);
                loadingViewSearch.setVisibility(View.VISIBLE);
//                if(selectedContact.getSearchString().equals("Me") || selectedContact.getSearchString().equals("Mummy")){
//
//                }else{
//                    Toast.makeText(ShareActivity.this, "Will not share", Toast.LENGTH_SHORT).show();
//                }
                sendSMS(selectedContact.getContactNumberString(), message);
            }
        });



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                contactsAdapter.getFilter().filter(s.toString());
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    protected void sendSMS(String phoneNo, String msgBody) {
        Log.i("Send SMS", "");
        Intent smsIntent = new Intent(Intent.ACTION_VIEW);

        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address"  , phoneNo);
        smsIntent.putExtra("sms_body"  , msgBody);

        try {
            startActivity(smsIntent);
            finish();
            Log.i("Finished sending SMS...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ShareActivity.this,
                    "SMS faild, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }
}
