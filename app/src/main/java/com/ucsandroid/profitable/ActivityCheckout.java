package com.ucsandroid.profitable;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.ucsandroid.profitable.serverclasses.Customer;
import com.ucsandroid.profitable.serverclasses.FoodAddition;
import com.ucsandroid.profitable.serverclasses.Location;
import com.ucsandroid.profitable.serverclasses.OrderedItem;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static android.nfc.NdefRecord.TNF_WELL_KNOWN;
import static android.nfc.NdefRecord.createExternal;
import static android.nfc.NdefRecord.createMime;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ActivityCheckout extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {

    NfcAdapter mNfcAdapter;
    private double taxRate = 7.5;
    private Locale currentLocale;
    private Currency currentCurrency;
    private NumberFormat currencyFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);


        //temp US currency
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String localeLang = settings.getString("locale_lang", "en");
        String localeCountry = settings.getString("locale_country", "us");
        taxRate = settings.getLong("tax_rate", 75)*.001;

        currentLocale = new Locale(localeLang, localeCountry);
        currentCurrency = Currency.getInstance(currentLocale);
        currencyFormatter = NumberFormat.getCurrencyInstance(currentLocale);

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


        NdefMessage nfcReceipt = createRecieptFromLocation(Singleton.getInstance().getCurrentLocation());

        return nfcReceipt;
    }


    private NdefMessage createRecieptFromLocation(Location location) {


        List<Customer> custs = location.getCurrentTab().getCustomers();
        System.out.println(custs.size());

        //Create record array and add space for logo and total
        NdefRecord[] recordArray = new NdefRecord[(custs.size()+1)];


        //Add header logo
        Bitmap logo = BitmapFactory.decodeResource(getResources(),
                R.mipmap.profit_logo);
        recordArray[0] = createBitampRecord(logo);



        for(int a = 0; a < custs.size(); a++){

            StringBuilder sb = new StringBuilder();
            //String customerOrders = "";
            for(OrderedItem item : custs.get(a).getOrder()){

                //Add itemName
                sb.append(item.getMenuItem().getName());
                if(item.getAdditions().size() > 0){
                    //customerOrders = customerOrders + ": ";
                    sb.append(": ");
                }
                for(int b = 0; b < item.getAdditions().size(); b++){
                    //Add addition
                    //customerOrders = customerOrders + "\n    *" +item.getAdditions().get(b).getName();
                    sb.append("\n    *" +item.getAdditions().get(b).getName());
                }
                //Add new line for next item
                sb.append("\n");
            }
            sb.append("\n\n" + currencyFormatter.format((custs.get(a).getCustomerCost()/100)));
            //customerOrders = customerOrders +"\n";
            System.out.println("sb: "+sb.toString());
            recordArray[(a+1)] = createTextRecord(sb.toString());
        }

        //TODO Add footer with total, tax, discount, whatevs



        NdefMessage receipt = new NdefMessage(
                recordArray);

        return receipt;
    }


    private NdefRecord createApplicationRecord(String someAppPackage){
        return NdefRecord.createApplicationRecord("com.example.android.beam");
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
        //return NdefRecord.createMime("Test Image", byteArray);
    }



}
