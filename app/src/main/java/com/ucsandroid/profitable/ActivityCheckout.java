package com.ucsandroid.profitable;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.nfc.NdefRecord.TNF_WELL_KNOWN;
import static android.nfc.NdefRecord.createExternal;
import static android.nfc.NdefRecord.createMime;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ActivityCheckout extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {

    NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        sendNdefMessage();
    }


    private void sendNdefMessage() {
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // Check for available NFC Adapter
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
            if (mNfcAdapter == null) {
                Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
                //finish();
                return;
            }
            // Register callback
            mNfcAdapter.setNdefPushMessageCallback(this, this);
        //}
    }


    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {


        String name = "yoyo";
        String title = "brobro";



        List<String> tesList = new ArrayList();
        for(int a = 0; a < 10; a++){
            tesList.add("num "+a);
        }

        String informations = "name: "+name + "\ntitle: " + title + "\naddress: "+ name + "\ncity: "+ title + "\nphone: "+ name + "\nmail: "+title + "\n";

        NdefRecord test = createMime(informations, informations.getBytes());


        NdefMessage msg = new NdefMessage(
                new NdefRecord[] {
                        test,
                        test,
                        test,
                        test,
                        test
                        //rtdUriRecord2,
                        //createMime("application/com.sos.anchor", text.getBytes())
                        //createMime(text, text.getBytes())
                        /**
                         * The Android Application Record (AAR) is commented out. When a device
                         * receives a push with an AAR in it, the application specified in the AAR
                         * is guaranteed to run. The AAR overrides the tag dispatch system.
                         * You can add it back in to guarantee that this
                         * activity starts when receiving a beamed message. For now, this code
                         * uses the tag dispatch system.
                        */
                        ,NdefRecord.createApplicationRecord("com.example.android.beam")
                });

        return msg;
    }

    private NdefRecord createUriRecord(String uri){

        Uri myUri = Uri.parse("http://www.google.com");
        return NdefRecord.createUri(myUri);

    }

    private NdefRecord createTextRecord(String informations) {
        return createMime(informations, informations.getBytes());
    }

    private NdefRecord createBitampRecord(Bitmap bitmap){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return new NdefRecord(NdefRecord.TNF_MIME_MEDIA,  "image/jpeg".getBytes(), null, byteArray);
    }



}
