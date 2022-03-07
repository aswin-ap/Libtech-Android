package com.wetwo.librarymanagment.data.model;

public class ImageUploadInfo {
    public String firebaseId;
    public int bookID;
    public String bookSub;
    public String bookAuthor;
    public String bookName;
    public String imageName;
    public String imageURL;
    public Boolean isBookAvailable;
    public String bookBuyer;


    public ImageUploadInfo() {

    }

    public String getFirebaseId() {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId) {
        this.firebaseId = firebaseId;
    }

    public Boolean getBookAvailable() {
        return isBookAvailable;
    }

    public void setBookAvailable(Boolean bookAvailable) {
        isBookAvailable = bookAvailable;
    }

    public String getBookBuyer() {
        return bookBuyer;
    }

    public void setBookBuyer(String bookBuyer) {
        this.bookBuyer = bookBuyer;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public ImageUploadInfo(String firebaseId,int bookID, String bookSub, String bookAuthor, String bookName, String imageName, String url, Boolean isBookAvailable, String bookBuyer) {
        this.bookID = bookID;
        this.bookSub = bookSub;
        this.bookAuthor = bookAuthor;
        this.bookName = bookName;
        this.imageName = imageName;
        this.imageURL = url;
        this.isBookAvailable = isBookAvailable;
        this.bookBuyer = bookBuyer;
        this.firebaseId = firebaseId;
    }

    public ImageUploadInfo(int bookID, String bookSub, String bookAuthor, String bookName, String imageName, String url, Boolean isBookAvailable, String bookBuyer) {
        this.bookID = bookID;
        this.bookSub = bookSub;
        this.bookAuthor = bookAuthor;
        this.bookName = bookName;
        this.imageName = imageName;
        this.imageURL = url;
        this.isBookAvailable = isBookAvailable;
        this.bookBuyer = bookBuyer;
    }

    public String getBookSub() {
        return bookSub;
    }

    public void setBookSub(String bookSub) {
        this.bookSub = bookSub;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getBookName() {
        return bookName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}