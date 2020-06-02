package com.example.android.quakereport;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class LoaderClass extends AsyncTaskLoader<ArrayList<CompaniesDetails>> {

    private static final String LOG_TAG = LoaderClass.class.getSimpleName();
    private String stringURL;
    //private ProgressBar progressBar;
    ArrayList<CompaniesDetails> companies;

    public LoaderClass(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        Log.e(LOG_TAG, "onStartLoading");
        forceLoad();
    }

    private URL createURL(String stringURL){
        URL url = null;
        try{
            url = new URL(stringURL);
            Log.e(LOG_TAG,"URL created successfully");
        }catch(MalformedURLException e){
            Log.e(LOG_TAG, "createURL: Malformed URL",e);
            return null;
        }
        return url;
    }

    private String makeHttpRequest(URL url){
        String JSONResponse = "";

        if(url==null){
            Log.e(LOG_TAG, "makeHttpRequest: URL is null");
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

    @Nullable
    @Override
    public ArrayList<CompaniesDetails> loadInBackground() {
        Log.e(LOG_TAG, "loadInBackground");
        BuildURL u = new BuildURL();
        stringURL= u.getStringURL();

        if(stringURL.length()<=7){
            Log.e(LOG_TAG, "loadInBackground: BuildURL created invalid URL of length less than 8");
            companies.add(new CompaniesDetails());
            return companies;
        }

        URL url = createURL(stringURL);

        String JSONResponse;
        JSONResponse = makeHttpRequest(url);

        if(JSONResponse==null || JSONResponse==""){
            Log.e(LOG_TAG, "loadInBackground: JSON response is empty");
        }

        QueryUtils q= new QueryUtils();
        companies = q.extractCompanies(JSONResponse);

        return companies;
    }


}
