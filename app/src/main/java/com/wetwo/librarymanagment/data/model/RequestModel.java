package com.wetwo.librarymanagment.data.model;

public class RequestModel {
    private String bookId;
    private String date;
    private String userId;

    public RequestModel(String bookId, String date, String userId) {
        this.bookId = bookId;
        this.date = date;
        this.userId = userId;
    }

    public RequestModel() {}

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
