package com.wetwo.librarymanagment.data.model;

public class ImageUploadInfo {
    public String bookSub;
    public String bookAuthor;
    public String bookName;

    public String imageURL;


    public ImageUploadInfo() {

    }

    public ImageUploadInfo(String bookSub,String bookAuthor,String bookName, String url) {

        this.bookSub =bookSub;
        this.bookAuthor=bookAuthor;
        this.bookName = bookName;
        this.imageURL= url;
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


}