package com.wetwo.librarymanagment.data.model;

public class RequestModel {
    private String bookId;
    private String date;
    private String userId;
    private String DocID;
    private Long bookIdR;
    private String bookName;
    private String userName;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public RequestModel(String bookId, String date, String userId) {
        this.bookId = bookId;
        this.date = date;
        this.userId = userId;
    }

    public String getDocID() {
        return DocID;
    }

    public void setDocID(String docID) {
        DocID = docID;
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

    public Long getBookIdR() {
        return bookIdR;
    }

    public void setBookIdR(Long bookIdR) {
        this.bookIdR = bookIdR;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
