package com.example.android.booklister;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by HAL on 5/1/2017.
 */

public class BooksAdapter extends ArrayAdapter<Books> {

    private static final String LOG_TAG = BooksAdapter.class.getSimpleName();

   public BooksAdapter (Activity context, ArrayList<Books> books) {

       super(context, 0,  books);
   }

   @Override
    public View getView(int position, View convertView, ViewGroup parent) {

       if (convertView == null) {
           convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
       }

       Books currentBook = getItem(position);

       String title = currentBook.getTitle();
       TextView titleView = (TextView) convertView.findViewById(R.id.title);
       titleView.setText(title);

       String author = currentBook.getAuthor();
       TextView authorView = (TextView) convertView.findViewById(R.id.author);
       authorView.setText(author);

       return convertView;

   }
}
