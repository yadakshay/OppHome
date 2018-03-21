package com.openhome.model;

/**
 * Created by Bhargav on 3/12/2017.
 */

public class SearchRequest {

    private int userId;
    private String searchType;
    private String searchText;
    private String sortBy;
    private SearchRefineDto refineOptions;
    private int limitStart;
    private int limitEnd;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public SearchRefineDto getRefineOptions() {
        return refineOptions;
    }

    public void setRefineOptions(SearchRefineDto refineOptions) {
        this.refineOptions = refineOptions;
    }

    public int getLimitStart() {
        return limitStart;
    }

    public void setLimitStart(int limitStart) {
        this.limitStart = limitStart;
    }

    public int getLimitEnd() {
        return limitEnd;
    }

    public void setLimitEnd(int limitEnd) {
        this.limitEnd = limitEnd;
    }
}
