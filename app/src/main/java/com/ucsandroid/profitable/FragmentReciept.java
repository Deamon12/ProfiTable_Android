package com.ucsandroid.profitable;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ucsandroid.profitable.adapters.OrderedItemRecyclerAdapter;
import com.ucsandroid.profitable.supportclasses.MyLinearLayoutManager;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;


public class FragmentReciept extends DialogFragment {

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



    public static FragmentReciept newInstance() {
        FragmentReciept fragment = new FragmentReciept();
        return fragment;
    }

    public FragmentReciept() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NO_FRAME, 0);

        View rootView = inflater.inflate(R.layout.layout_receipt, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.the_recycler);
        serverText = (TextView) rootView.findViewById(R.id.server_text);
        restaurantName = (TextView) rootView.findViewById(R.id.restaurant_name_text);
        tabId = (TextView) rootView.findViewById(R.id.tabid_text);

        subTotalText = (TextView) rootView.findViewById(R.id.subtotal_label);
        taxText = (TextView) rootView.findViewById(R.id.tax_textview);
        discountText = (TextView) rootView.findViewById(R.id.discount_text);
        amountDueText = (TextView) rootView.findViewById(R.id.amountdue_textview);

        //restaurantName.setText(Singleton.getInstance().getCurrentLocation().getCurrentTab().getServer().get);

        tabId.setText(Singleton.getInstance().getCurrentLocation().getCurrentTab().getTabId()+"");
        serverText.setText(Singleton.getInstance().getCurrentLocation().getCurrentTab().getServer().getFirstName()+"");

        getRestaurantId();
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




    private void initRecycler() {

        recyclerView.setLayoutManager(new MyLinearLayoutManager(getActivity()));
        OrderedItemRecyclerAdapter mAdapter = new OrderedItemRecyclerAdapter(getActivity(),
                Singleton.getInstance().getCurrentLocation().getCurrentTab().getCustomers().get(0).getOrders(),
                R.layout.item_textview_textview_textview,
                0,
                null,
                null,
                null);

        recyclerView.setAdapter(mAdapter);

    }

    private void getRestaurantId() {
        //todo: volley

    }

}