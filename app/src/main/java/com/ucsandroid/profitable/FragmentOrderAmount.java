package com.ucsandroid.profitable;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class FragmentOrderAmount extends Fragment {


    private Locale currentLocale;
    private Currency currentCurrency;
    private NumberFormat currencyFormatter;

    private int subTotal = 0;
    private int tax = 0;
    private int discount = 0;
    private int amountDue = 0;

    private TextView subTotalText;
    private TextView taxText;
    private TextView discountText;
    private TextView amountDueText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String localeLang = settings.getString("locale_lang", "en");
        String localeCountry = settings.getString("locale_country", "us");
        currentLocale = new Locale(localeLang, localeCountry);
        currentCurrency = Currency.getInstance(currentLocale);
        currencyFormatter = NumberFormat.getCurrencyInstance(currentLocale);

        View view = inflater.inflate(R.layout.fragment_order_amount, container, false);

        subTotalText = (TextView) view.findViewById(R.id.subtotal_label);
        taxText = (TextView) view.findViewById(R.id.tax_textview);
        discountText = (TextView) view.findViewById(R.id.discount_textview);
        amountDueText = (TextView) view.findViewById(R.id.amountdue_textview);

        subTotalText.setText(currencyFormatter.format(subTotal));
        taxText.setText(currencyFormatter.format(tax));
        discountText.setText(currencyFormatter.format(discount));
        amountDueText.setText(currencyFormatter.format(amountDue));

        /*

        System.out.println(
                currentLocale.getDisplayName() + ", " +
                        currentCurrency.getDisplayName() + ": " +
                        currencyFormatter.format(currencyAmount));
*/



        return view;
    }


}