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
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

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

        String text = ("Beam me up, Android!\n\n" +
                "Beam Time: " + System.currentTimeMillis());


        /*
        NdefMessage msg = new NdefMessage(
                NdefRecord.createExternal("example.com", "mycustomtype", payload),
                NdefRecord.createApplicationRecord("com.example.your.app.package")
        )*/

        String name = "yoyo";
        String title = "brobro";


        //Bitmap mBitmap = mPhoto;
        //ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        //byte[] byteArray = stream.toByteArray();

        //NdefRecord picRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,  "image/jpeg".getBytes(), null, byteArray);
        String informations = "name: "+name + "\ntitle: " + title + "\naddress: "+ name + "\ncity: "+ title + "\nphone: "+ name + "\nmail: "+title + "\n";
        NdefRecord textRecord = createTextRecord(informations);
        NdefMessage message = new NdefMessage(new NdefRecord[]{ textRecord}); //picRecord,


        Uri myUri = Uri.parse("http://www.google.com");
        NdefRecord rtdUriRecord2 = NdefRecord.createUri(myUri);

        NdefMessage msg = new NdefMessage(
                new NdefRecord[] {
                        rtdUriRecord2,
                        //createMime("application/com.sos.anchor", text.getBytes())
                        createMime("image/jpeg", text.getBytes()),
                        createMime(text, text.getBytes())
                        /**
                         * The Android Application Record (AAR) is commented out. When a device
                         * receives a push with an AAR in it, the application specified in the AAR
                         * is guaranteed to run. The AAR overrides the tag dispatch system.
                         * You can add it back in to guarantee that this
                         * activity starts when receiving a beamed message. For now, this code
                         * uses the tag dispatch system.
                        */
                        //,NdefRecord.createApplicationRecord("com.example.android.beam")
                });

        return message;
    }

    private NdefRecord createTextRecord(String informations) {
        return createMime(informations, informations.getBytes());
    }


}
