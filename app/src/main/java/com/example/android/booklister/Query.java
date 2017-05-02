package com.example.android.booklister;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.booklister.MainActivity.LOG_TAG;

/**
 * Created by HAL on 5/1/2017.
 */

public class Query {

    private Query() {
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error when making connection, code:" + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making a url connection", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Books} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<Books> extractFeatureFromJson(String booksJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(booksJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<Books> books = new ArrayList<>();


        // If the results are not null proceed to parsing and creating Book Objects
        // Convert the results from String to JSONObject
        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(booksJSON);

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or books).
            JSONArray booksArray = baseJsonResponse.getJSONArray("items");

            // For each book in the bookArray, create an {@link Books} object
            for (int i = 0; i < booksArray.length(); i++) {

                // Get a single book at position i within the list of books
                JSONObject currentBook = booksArray.getJSONObject(i);

                // For a given book, extract the JSONObject associated with the
                // key called "volumeInfo", which represents a list of all properties
                // for that book.
                JSONObject properties = currentBook.getJSONObject("volumeInfo");


                // Extract the value for the key called "title"
                String title = properties.getString("title");
                Log.d(LOG_TAG, "grabbed title");
                // Extract the value for the key called "authors"
                JSONArray bookAuthors = null;
                try {
                    bookAuthors = properties.getJSONArray("authors");
                } catch (JSONException ignored) {
                }
                // Convert the authors to a string
                String bookAuthorsString = "";
                // If the author is empty, set it as "Unknown"
                if (bookAuthors == null) {
                    bookAuthorsString = "Unknown Author";
                } else {
                    // Format the authors as "author1, author2, and author3"
                    int countAuthors = bookAuthors.length();
                    for (int e = 0; e < countAuthors; e++) {
                        String author = bookAuthors.getString(e);
                        if (bookAuthorsString.isEmpty()) {
                            bookAuthorsString = author;
                        } else if (e == countAuthors - 1) {
                            bookAuthorsString = bookAuthorsString + " and " + author;
                        } else {
                            bookAuthorsString = bookAuthorsString + ", " + author;
                        }
                    }
                }
                Log.d(LOG_TAG, "grabbed author");

                // Create a new {@link Books} object with the title, authors and url from the JSON response.
                Books bookItems = new Books(title, bookAuthorsString);

                // Add the new {@link Books} to the list of books.
                books.add(bookItems);
                Log.d(LOG_TAG, "books added");
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the books JSON results", e);
        }

        // Return the list of books
        return books;
    }

    static List<Books> fetchBookData(String requestUrl) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.v(LOG_TAG, "TEST: Fetch Method Called");
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Books}s

        // Return the list of {@link Books}s
        return extractFeatureFromJson(jsonResponse);
    }
}
