package com.example.android.quakereport;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class QueryUtils { //This class converts the JSON string to companiesdetails objects.

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    //private String JSON_RESPONSE ="{\"status\":true,\"code\":200,\"msg\":\"Successfully\",\"response\":[{\"price\":\"695.00\",\"high\":\"699.45\",\"low\":\"688.45\",\"chg\":\"+2.65\",\"chg_percent\":\"0.38%\",\"dateTime\":\"2020-05-26 05:22:54\",\"symbol\":\"INFY\",\"country\":\"india\",\"id\":\"63595\"},{\"price\":\"190.80\",\"high\":\"194.95\",\"low\":\"189.00\",\"chg\":\"+4.45\",\"chg_percent\":\"2.39%\",\"dateTime\":\"2020-05-26 05:22:54\",\"symbol\":\"ITC\",\"country\":\"india\",\"id\":\"63596\"},{\"price\":\"175.10\",\"high\":\"177.50\",\"low\":\"168.00\",\"chg\":\"+8.95\",\"chg_percent\":\"5.39%\",\"dateTime\":\"2020-05-26 05:22:52\",\"symbol\":\"JSTL\",\"country\":\"india\",\"id\":\"63597\"},{\"price\":\"1429.65\",\"high\":\"1449.00\",\"low\":\"1425.15\",\"chg\":\"-1.95\",\"chg_percent\":\"-0.14%\",\"dateTime\":\"2020-05-26 05:07:39\",\"symbol\":\"RELI\",\"country\":\"india\",\"id\":\"63562\"},{\"price\":\"84.60\",\"high\":\"85.65\",\"low\":\"83.85\",\"chg\":\"+1.85\",\"chg_percent\":\"2.24%\",\"dateTime\":\"2020-05-26 05:07:56\",\"symbol\":\"TAMO\",\"country\":\"india\",\"id\":\"63567\"}],\"info\":{\"server_time\":\"2020-05-26 05:44:48 UTC\",\"credit_count\":1,\"_t\":\"2020-05-26 05:44:48 UTC\"}}";

    public QueryUtils() {
    }

    public ArrayList<CompaniesDetails> extractCompanies(String JSON_RESPONSE) {

        Log.e(LOG_TAG, "In QueryUtils");

        // Create an empty ArrayList that we can start adding companies to
        ArrayList<CompaniesDetails> list_companiesDetails = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject root = new JSONObject(JSON_RESPONSE);
            boolean status = root.getBoolean("status");

            if(status==false || JSON_RESPONSE==""){
                Log.e(LOG_TAG, "No Internet connection or too many request in a minute!");
                return null;
            }

            JSONArray array_company= root.getJSONArray("response");
            String company_name, change, url;
            double current_price;

            for(int i=0;i<array_company.length(); i++){
                JSONObject full_details= array_company.getJSONObject(i);
                company_name= full_details.getString("symbol");
                current_price= Double.parseDouble(full_details.getString("price"));
                change= full_details.getString("chg");
                change= change.trim();
                url="https://www.google.com/search?q="+company_name+"+share+price";

                list_companiesDetails.add(new CompaniesDetails(current_price, company_name, change, url));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return list_companiesDetails;
    }

}