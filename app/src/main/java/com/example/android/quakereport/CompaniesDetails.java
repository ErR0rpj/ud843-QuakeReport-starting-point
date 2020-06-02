package com.example.android.quakereport;

public class CompaniesDetails {

    private String name, percentchange, google_url;
    private double currentprice;

    public CompaniesDetails(double currentprice ,String name , String percentchange, String google_url) {
        this.name = name;
        this.google_url=google_url;
        this.currentprice = currentprice;
        this.percentchange = percentchange;
    }

    public CompaniesDetails(){
        this.name= "Updating your stocks";
        this.google_url="https://www.google.com";
        this.currentprice=0;
        this.percentchange= "0.00%";
    }

    public CompaniesDetails(String s){
        
    }

    public String getGoogle_url(){ return google_url; }

    public String getName() {
        return name;
    }

    public double getCurrentprice() {
        return currentprice;
    }

    public String getPercentchange() {
        return percentchange;
    }


}

