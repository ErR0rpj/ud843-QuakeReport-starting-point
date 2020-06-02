package com.example.android.quakereport;

import android.util.Log;

import java.net.URL;
import java.util.ArrayList;

public class BuildURL {

    public static final String LOG_TAG = BuildURL.class.getName();

    public String getStringURL(){
        ArrayList<String> companyCodes = WhichStocks.getCodes();
        int siz = companyCodes.size();

        String url = "https://fcsapi.com/api-v2/stock/latest?id=";
        for(int i=0; i<siz-1;i++){
            url = url + companyCodes.get(i)+",";
        }

        url = url + companyCodes.get(siz-1);
        url = url + "&access_key=rhmryNUtxihosA4D1hNWnEIUpjrLOBMwhjbLkeJwS6PSu";
        Log.e(LOG_TAG, "Url: "+ url);

        return url;
    }
}
