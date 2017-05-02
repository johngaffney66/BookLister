 package com.example.android.booklister;



import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

 public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<Books>> {
     public static final String LOG_TAG = MainActivity.class.getName();

     public static final int BOOK_LOADER_ID = 1;
     private TextView mEmptyState;
     private BooksAdapter mAdapter;
     private static String GOOGLE_BOOKS_REQUEST_URL
             = "https://www.googleapis.com/books/v1/volumes?maxResults=10&q=";

     public MainActivity () throws UnsupportedEncodingException{}

     @Override
     public Loader<List<Books>> onCreateLoader (int i, Bundle bundle) {

         Log.v(LOG_TAG, "Created Loader");
         return new BooksTaskLoader(this, GOOGLE_BOOKS_REQUEST_URL);
     }

     @Override
     public void onLoadFinished(Loader<List<Books>> loader, List<Books> books){
         ConnectivityManager connectivityManager =
                 (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
         NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
         boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

         if (books != null && !books.isEmpty()) {
             mAdapter.addAll(books);
         }
         mEmptyState.setText(R.string.no_books_found);
         Log.v(LOG_TAG, "Finished Loader");
         GOOGLE_BOOKS_REQUEST_URL.replaceAll(" ", "%20");
         if (!isConnected) {
             mEmptyState.setText(R.string.no_connection);
         }
     }

     @Override
     public void onLoaderReset(Loader<List<Books>> loader) {
         Log.v(LOG_TAG, "Reset Loader");
         mAdapter.clear();
     }

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);

         ConnectivityManager cm =
                 (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

         final NetworkInfo ni = cm.getActiveNetworkInfo();

         Button button = (Button) findViewById(R.id.search_button);
         final EditText editText = (EditText) findViewById(R.id.search_bar);
         final ListView booksList = (ListView) findViewById(R.id.list_view);

         mAdapter = new BooksAdapter(this, new ArrayList<Books>());

         booksList.setAdapter(mAdapter);

         button.setOnClickListener(new View.OnClickListener(){


             @Override
             public void onClick(View v) {
                 mAdapter.clear();

                 Log.v ("EditText", editText.getText().toString());
                 String searchString = editText.getText().toString();
                 GOOGLE_BOOKS_REQUEST_URL =
                         "https://www.googleapis.com/books/v1/volumes?q=" + searchString + "&maxResults=10";
                 if (ni != null && ni.isConnected()) {
                     LoaderManager loaderManager = getLoaderManager();
                     loaderManager.initLoader(0, null, MainActivity.this);

                     if (getLoaderManager().getLoader(0).isStarted()) {
                         getLoaderManager().restartLoader(0, null, MainActivity.this);
                     }
                 }else {
                     mEmptyState.setText(R.string.no_connection);
                 }

             }
         });

         mEmptyState = (TextView) findViewById(R.id.empty_view);
         booksList.setEmptyView(mEmptyState);


         // If there is a network connection, fetch data
         if (ni != null && ni.isConnectedOrConnecting()) {
             // Get a reference to the LoaderManager, in order to interact with loaders.
             LoaderManager loaderManager = getLoaderManager();
             // Initialize the loader. Pass in the int ID constant defined above and pass in null for
             // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
             // because this activity implements the LoaderCallbacks interface).
             loaderManager.initLoader(BOOK_LOADER_ID, null, this);
         } else {

             // Update empty state with no connection error message
             mEmptyState.setText(R.string.no_connection);
         }

     }


}
