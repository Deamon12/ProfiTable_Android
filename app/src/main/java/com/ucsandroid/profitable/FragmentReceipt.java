package com.ucsandroid.profitable;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ucsandroid.profitable.adapters.KitchenCustomerRecyclerAdapter;
import com.ucsandroid.profitable.adapters.NestedKitchenRecyclerAdapter;
import com.ucsandroid.profitable.adapters.OrderedItemRecyclerAdapter;
import com.ucsandroid.profitable.listeners.DialogDismissListener;
import com.ucsandroid.profitable.supportclasses.FileOpen;
import com.ucsandroid.profitable.supportclasses.MyLinearLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;


public class FragmentReceipt extends DialogFragment {

    private File file;
    private View rootView;

    private double taxRate = 7.5;
    private Locale currentLocale;
    private Currency currentCurrency;
    private NumberFormat currencyFormatter;

    private double subTotal = 0;
    private double tax = 0;
    private double discount = 0;
    private double amountDue = 0;

    private TextView serverText;
    private TextView restaurantName;
    private TextView tabId;

    private TextView subTotalText;
    private TextView taxText;
    private TextView discountText;
    private TextView amountDueText;

    RecyclerView recyclerView;



    public static FragmentReceipt newInstance() {
        FragmentReceipt fragment = new FragmentReceipt();
        return fragment;
    }

    public FragmentReceipt() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NO_FRAME, 0);

        if (!Singleton.hasBeenInitialized()) {
            Singleton.initialize(getActivity());
        }

        rootView = inflater.inflate(R.layout.layout_receipt, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.the_recycler);
        serverText = (TextView) rootView.findViewById(R.id.server_text);
        restaurantName = (TextView) rootView.findViewById(R.id.restaurant_name_text);
        tabId = (TextView) rootView.findViewById(R.id.tabid_text);

        subTotalText = (TextView) rootView.findViewById(R.id.subtotal_label);
        taxText = (TextView) rootView.findViewById(R.id.tax_textview);
        discountText = (TextView) rootView.findViewById(R.id.discount_text);
        amountDueText = (TextView) rootView.findViewById(R.id.amountdue_textview);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //System.out.println("RestNAme: "+settings.getString("rest_name", "ProfiTable"));

        String restName = Singleton.getInstance().getRestaurantName();
        restaurantName.setText(restName+"");
        tabId.setText(Singleton.getInstance().getCurrentLocation().getCurrentTab().getTabId()+"");
        serverText.setText(Singleton.getInstance().getCurrentLocation().getCurrentTab().getServer().getFirstName()+"");


        file = new File(getActivity().getExternalFilesDir(null), "ProfiTable_Receipt.pdf");


        initCurrencyFormatting();


        initAmounts();
        initRecycler();

        return rootView;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
        {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }


    private void initAmounts() {



        subTotal = Singleton.getInstance().getCurrentLocation().getLocationCost();
        subTotal = (subTotal/100);

        if(Singleton.getInstance().getCurrentLocation().getCurrentTab().getDiscount() != null){
            discount = Singleton.getInstance().getCurrentLocation().getCurrentTab().getDiscount().getPercent();
            //System.out.println("Discount: "+discount);
        }
        else{
            //discountLabel.
            //discountText.
        }

        tax = (subTotal-(discount))*(taxRate);
        amountDue = subTotal+tax;

        subTotalText.setText(currencyFormatter.format(subTotal));
        taxText.setText(currencyFormatter.format(tax));
        discountText.setText(currencyFormatter.format(discount));
        amountDueText.setText(currencyFormatter.format(amountDue));

    }

    private void initCurrencyFormatting() {

        //temp US currency
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String localeLang = settings.getString("locale_lang", "en");
        String localeCountry = settings.getString("locale_country", "us");
        taxRate = settings.getLong("tax_rate", 75)*.001;

        currentLocale = new Locale(localeLang, localeCountry);
        currentCurrency = Currency.getInstance(currentLocale);
        currencyFormatter = NumberFormat.getCurrencyInstance(currentLocale);

    }




    //TODO: this will change depending on type of receipt
    private void initRecycler() {


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        KitchenCustomerRecyclerAdapter mAdapter = new KitchenCustomerRecyclerAdapter(getActivity(),
                Singleton.getInstance().getCurrentLocation().getCurrentTab().getCustomers(),
                R.layout.tile_recyclerview,
                0,
                null,
                null,
                R.layout.item_textview_textview_textview);

        recyclerView.setAdapter(mAdapter);


        ViewTreeObserver vto = rootView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);


                buildPDF();
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void buildPDF() {

        PdfDocument document = new PdfDocument();

        int width = rootView.getMeasuredWidth();
        int recHeight = recyclerView.getMeasuredHeight();
        int height = (int) (recHeight + recyclerView.getY());

        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, 1).create();

        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);

        rootView.draw(page.getCanvas());

        // finish the page
        document.finishPage(page);

        // write the document content
        try {
            document.writeTo(getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // close the document
        document.close();

        Dialog dialog = getDialog();
        dialog.dismiss();

        showShareDialog();

    }

    private void showShareDialog() {

        try {
            FileOpen.openFile(getActivity(), file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private OutputStream getOutputStream() throws FileNotFoundException {
        return new FileOutputStream(file);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        //int customer = getArguments().getInt("customer");
        //int position = getArguments().getInt("position");
        ((ActivityOrderView) getActivity()).dialogDismissListener(0, 0, null);
    }

}