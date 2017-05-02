package com.example.android.booklister;

/**
 * Created by HAL on 5/1/2017.
 */

public class Books {

    private String mTitle;

    private String mAuthor;

    public Books (String title, String author) {

        mTitle =  title;
        mAuthor = author;

    }

    public String getTitle() {return mTitle;}

    public String getAuthor() {return mAuthor;}
}
