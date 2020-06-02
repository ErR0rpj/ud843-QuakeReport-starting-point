package com.example.android.quakereport;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CompaniesDetailsAdapter extends ArrayAdapter<CompaniesDetails> {

    /***This gives the name of the class.*/
    //Note that static final is used instead of final only, can cause error.
    public static final String LOG_TAG = CompaniesDetailsAdapter.class.getSimpleName();

    public CompaniesDetailsAdapter(Activity context, ArrayList<CompaniesDetails> companiesDetails){
        super(context, 0, companiesDetails);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView =convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        CompaniesDetails currentcompany = getItem(position);
        TextView companyname= (TextView) listItemView.findViewById(R.id.TVcompany);
        companyname.setText(currentcompany.getName());

        TextView currentprice= (TextView) listItemView.findViewById(R.id.TVcurrentprice);
        currentprice.setText(Double.toString(currentcompany.getCurrentprice()));

        TextView percentchange= (TextView) listItemView.findViewById(R.id.TVpercentchange);
        String pchng= currentcompany.getPercentchange();
        if(pchng.charAt(0)=='-'){
            percentchange.setTextColor(Color.RED);
        }
        else{
            percentchange.setTextColor(Color.GREEN);
        }
        percentchange.setText(currentcompany.getPercentchange());

        return listItemView;
    }
}
