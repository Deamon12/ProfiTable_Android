package com.ucsandroid.profitable;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.w3c.dom.Document;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DialogBillSplit extends DialogFragment implements View.OnClickListener {


    private RelativeLayout customerSplit;
    private RelativeLayout noSplit;
    private RelativeLayout equalSplit;

    static DialogBillSplit newInstance() {
        DialogBillSplit f = new DialogBillSplit();

        //Bundle args = new Bundle();
        //f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, 0);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().setTitle("Split the bill?");
        View v = inflater.inflate(R.layout.dialog_bill_split, container, false);

        customerSplit = (RelativeLayout) v.findViewById(R.id.split_pre_customer_relative);
        equalSplit = (RelativeLayout) v.findViewById(R.id.equal_split_relative);
        noSplit = (RelativeLayout) v.findViewById(R.id.no_split_relative);

        customerSplit.setOnClickListener(this);
        equalSplit.setOnClickListener(this);
        noSplit.setOnClickListener(this);



        return v;
    }


    @Override
    public void onClick(View v) {

        if(v == customerSplit){

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {

                showReciept();
                //buildPDF();
            }

        }
        else if(v == equalSplit){

        }
        else if(v == noSplit){

        }


    }

    private void showReciept() {


        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("receipt");

        if (prev != null) {
            fragmentTransaction.remove(prev);
        }

        fragmentTransaction.addToBackStack(null);
        FragmentReciept newFragment = FragmentReciept.newInstance();
        newFragment.show(fragmentTransaction, "receipt");


    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void buildPDF() {

        PdfDocument document = new PdfDocument();

        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1400, 1600, 1).create();



        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);

        //todo receiptView.draw(page.getCanvas());

        // finish the page
        document.finishPage(page);


        //Add pages...



        // write the document content
        try {
            document.writeTo(getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // close the document
        document.close();


    }



    private OutputStream getOutputStream() throws FileNotFoundException {
        File file = new File(getActivity().getExternalFilesDir(null), "fileName.pdf");
        System.out.println("File: "+file.getAbsolutePath());
        return new FileOutputStream(file);
    }

    private BufferedOutputStream getBufferedOutputStream() throws FileNotFoundException {
        File file = new File(getActivity().getFilesDir(), "filename");
        return new BufferedOutputStream(new FileOutputStream(file));
    }

/*
    private View getContentView(){

        //System.out.println("cointainer params: "+container.getLayoutParams().width + ", "+container.getLayoutParams().height);


        //View theView =
        //theView.setLayoutParams(new ViewGroup.LayoutParams(500, 500));

        //RelativeLayout theRelative = (RelativeLayout) theView.findViewById(R.id.the_relative);
        //theRelative.setLayoutParams(new ViewGroup.LayoutParams(1500, 1500));



        //System.out.println("View params: "+theView.getLayoutParams().width + ", "+theView.getLayoutParams().height);


        return theView;
    }*/





}
