package com.example.android.booklister;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HAL on 5/1/2017.
 */

public class BooksTaskLoader extends AsyncTaskLoader<List<Books>> {
    private static final String LOG_TAG = BooksTaskLoader.class.getSimpleName();

    private String mUrl;


    public BooksTaskLoader(Context context, String url){
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading(){
        Log.v(LOG_TAG, "On Start");
        forceLoad();
    }

    @Override
    public List<Books> loadInBackground() {
        Log.v(LOG_TAG, "Load In Background");
        if (mUrl == null) {
            return null;
        }

        List<Books> books = Query.fetchBookData(mUrl);
        return books;
    }
}
