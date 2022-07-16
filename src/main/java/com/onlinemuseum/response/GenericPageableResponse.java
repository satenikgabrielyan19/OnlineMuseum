package com.onlinemuseum.response;

import java.util.*;

public class GenericPageableResponse<T> {
    private List<T> data;

    private Integer totalPageCount;

    private Integer currentPageNumber;

    GenericPageableResponse() {
    }

    public GenericPageableResponse(List<T> data, Integer totalPageCount, Integer currentPageNumber) {
        this.data = data;
        this.totalPageCount = totalPageCount;
        this.currentPageNumber = currentPageNumber;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Integer getTotalPageCount() {
        return totalPageCount;
    }

    public void setTotalPageCount(Integer totalPageCount) {
        this.totalPageCount = totalPageCount;
    }

    public Integer getCurrentPageNumber() {
        return currentPageNumber;
    }

    public void setCurrentPageNumber(Integer currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }
}
