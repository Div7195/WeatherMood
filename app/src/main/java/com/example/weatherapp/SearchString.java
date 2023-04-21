package com.example.weatherapp;

public class SearchString {
    private String searchString;
    private String searchDate;

    public SearchString(String searchString, String searchDate) {
        this.searchString = searchString;
        this.searchDate = searchDate;
    }

    public String getSearchString() {
        return searchString;
    }

    public String getSearchDate() {
        return searchDate;
    }
}