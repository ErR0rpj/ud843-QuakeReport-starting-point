/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.util.ArrayList;


public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<CompaniesDetails>> {

    private static final String LOG_TAG = EarthquakeActivity.class.getName();
    //private String stringURL;
    private CompaniesDetailsAdapter adapter;
    private TextView TVempty;
    private ListView companiesListView;
    public ProgressBar progressBar;
    //ArrayList<CompaniesDetails> companies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        companiesListView = findViewById(R.id.list);
        TVempty = findViewById(R.id.TVempty);
        progressBar = findViewById(R.id.progressBar);
        TVempty.setVisibility(View.GONE);

        //ArrayList<CompaniesDetails> companies = new ArrayList<>();
        //companies.add(new CompaniesDetails());

        // Create a new {@link ArrayAdapter} of companies
        adapter= new CompaniesDetailsAdapter(this, new ArrayList<CompaniesDetails>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        companiesListView.setAdapter(adapter);

        companiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                CompaniesDetails currentcompany= adapter.getItem(i);

                Uri companyUri= Uri.parse(currentcompany.getGoogle_url());
                Intent website= new Intent(Intent.ACTION_VIEW, companyUri);
                startActivity(website);
            }
        });

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork!=null && activeNetwork.isConnectedOrConnecting();

        if(isConnected==false){
            Log.e(LOG_TAG, "isConnected = false");
            progressBar.setVisibility(View.GONE);
            TVempty.setVisibility(View.VISIBLE);
            TVempty.setText("Check your Internet Connection and try again!");
        }
        else {
            LoaderManager.getInstance(this).initLoader(1, null, this).forceLoad();
            Log.e(LOG_TAG, "After initLoader");
            /*APIConnection apiConnection = new APIConnection();
            apiConnection.execute();*/
        }

    }

    private void updateUI(ArrayList<CompaniesDetails> companies){

        progressBar.setVisibility(View.GONE);
        if(companies==null || companies.size()==0){
            Log.e(LOG_TAG, "UpdateUI: companies arraylist is empty or null");
            TVempty.setVisibility(View.VISIBLE);
            TVempty.setText("Problem Loading Data\nRetry Karo Beta");
            adapter.clear();
            return;
        }
        TVempty.setVisibility(View.GONE);
        adapter.clear();
        adapter.addAll(companies);
        Log.e(LOG_TAG, "Items updated");
    }


    @NonNull
    @Override
    public Loader<ArrayList<CompaniesDetails>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.e(LOG_TAG, "onCreateLoader");
        return new LoaderClass(EarthquakeActivity.this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<CompaniesDetails>> loader, ArrayList<CompaniesDetails> companiesDetails) {
        Log.e(LOG_TAG, "onLoadFinished");
        if(companiesDetails==null || companiesDetails.size()==0){
            updateUI(null);
            Log.e(LOG_TAG,"onLoadFinished: companiesDetails is null");
            return;
        }
        updateUI(companiesDetails);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<CompaniesDetails>> loader) {
        Log.e(LOG_TAG, "onLoaderReset");
        ArrayList<CompaniesDetails> companies = new ArrayList<>();
        companies.add(new CompaniesDetails());
        updateUI(companies);
    }


    /*private class APIConnection extends AsyncTask<URL, Void, ArrayList<CompaniesDetails>>{

        @Override
        protected ArrayList<CompaniesDetails> doInBackground(URL... urls) {
            BuildURL u = new BuildURL();
            stringURL= u.getStringURL();

            if(stringURL.length()<=7){
                Log.e(LOG_TAG, "doInBackground: BuildURL created invalid URL of length less than 8");
                companies.add(new CompaniesDetails());
                return companies;
            }

            URL url = createURL(stringURL);

            String JSONResponse = "";
            JSONResponse = makeHttpRequest(url);

            if(JSONResponse==""){
                Log.e(LOG_TAG, "doInBackground: JSON response is empty");
            }

            QueryUtils q= new QueryUtils();
            companies = q.extractCompanies(JSONResponse);

            return companies;
        }

        @Override
        protected void onPostExecute(ArrayList<CompaniesDetails> companiesDetails) {
            if(companiesDetails==null){
                Log.e(LOG_TAG,"onPostExcecute: companiesDetails is null");
                return;
            }
            UpdateUI(companiesDetails);
        }

        private URL createURL(String stringURL){
            URL url = null;
            try{
                url = new URL(stringURL);
                Log.e(LOG_TAG,"URL created successfully");
            }catch(MalformedURLException e){
                Log.e(LOG_TAG, "Malformed URL",e);
                return null;
            }
            return url;
        }

        private String makeHttpRequest(URL url){
            String JSONResponse = "";

            if(url==null){
                Log.e(LOG_TAG, "makeHttpRequest: url is null");
                return JSONResponse;
            }

            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            try{
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.connect();

                if(httpURLConnection.getResponseCode()==200){
                    inputStream = httpURLConnection.getInputStream();
                    JSONResponse = readFromStream(inputStream);
                }
                else{
                    Log.e(LOG_TAG, "Response Code Error: "+ httpURLConnection.getResponseCode());
                }

            }catch (IOException e){
                Log.e(LOG_TAG, "Error retrieving JSON response in makeHttpRequest");
            }finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    try {
                        inputStream.close();
                    }catch(IOException e){
                        Log.e(LOG_TAG,"makeHttpRequest: Error closing InputStream",e);
                    }
                }
            }
            return JSONResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException{
            StringBuilder output = new StringBuilder();

            if(inputStream!=null){
                InputStreamReader streamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(streamReader);
                String line= reader.readLine();
                while(line != null){
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

    }*/
}
