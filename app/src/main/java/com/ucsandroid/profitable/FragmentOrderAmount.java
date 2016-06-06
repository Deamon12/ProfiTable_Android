package com.ucsandroid.profitable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class FragmentOrderAmount extends Fragment {

    private BroadcastReceiver mDoCalculationUpdate;

    private double taxRate = 7.5;
    private Locale currentLocale;
    private Currency currentCurrency;
    private NumberFormat currencyFormatter;

    private double subTotal = 0;
    private double tax = 0;
    private double discount = 0;
    private double amountDue = 0;

    private TextView subTotalText;
    private TextView taxText;
    private TextView discountText;
    private TextView amountDueText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (!Singleton.hasBeenInitialized()) {
            Singleton.initialize(getActivity());
        }

        //temp US currency
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String localeLang = settings.getString("locale_lang", "en");
        String localeCountry = settings.getString("locale_country", "us");
        taxRate = settings.getLong("tax_rate", 75)*.001;

        currentLocale = new Locale(localeLang, localeCountry);
        currentCurrency = Currency.getInstance(currentLocale);
        currencyFormatter = NumberFormat.getCurrencyInstance(currentLocale);

        View view = inflater.inflate(R.layout.fragment_order_amount, container, false);

        subTotalText = (TextView) view.findViewById(R.id.subtotal_label);
        taxText = (TextView) view.findViewById(R.id.tax_textview);
        discountText = (TextView) view.findViewById(R.id.discount_text);
        amountDueText = (TextView) view.findViewById(R.id.amountdue_textview);

        initUpdateAmountListener();
        getOrders();


        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mDoCalculationUpdate);

    }


    //Run this everytime an update broadcast is received
    public void getOrders(){

        subTotal = Singleton.getInstance().getCurrentLocation().getLocationCost();
        subTotal = (subTotal/100);
        updateUI();

    }

    private void updateUI(){

        tax = (subTotal-(discount))*(taxRate);
        amountDue = subTotal+tax;

        if(Singleton.getInstance().getCurrentLocation().getCurrentTab().getDiscount() != null){
            System.out.println("Discount: "+Singleton.getInstance().getCurrentLocation().getCurrentTab().getDiscount().getPercent());
            discount = Singleton.getInstance().getCurrentLocation().getCurrentTab().getDiscount().getPercent();
        }

        subTotalText.setText(currencyFormatter.format(subTotal));
        taxText.setText(currencyFormatter.format(tax));
        discountText.setText(currencyFormatter.format(discount));
        amountDueText.setText(currencyFormatter.format(amountDue));
    }

    private void initUpdateAmountListener() {

        mDoCalculationUpdate = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.hasExtra("tableNumber")){
                    //System.out.println("Amount frag received update broadcast for table: "+intent.getSerializableExtra("tableNumber"));
                    getOrders();
                }
                else{
                    //System.out.println("Amount frag received update broadcast");
                    getOrders();
                }

            }
        };

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mDoCalculationUpdate,
                new IntentFilter("update-amount"));
    }


}