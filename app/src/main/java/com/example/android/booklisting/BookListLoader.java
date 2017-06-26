package com.example.android.booklisting;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class BookListLoader extends AsyncTaskLoader<List<Book>> {

    // Variables
    private static final String LOG_TAG = "BookListLoader:";
    private String dataString;

    /**
     * Constructor for a new BookListLoader. It handles the loading of data from the Google Books
     * API on a background thread.
     *
     * @param context
     * @param url
     */

    public BookListLoader(Context context, String url) {
        super(context);
        dataString = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
        Log.v(LOG_TAG, "onStartLoading is called");
    }

    /**
     * Make the request to the Google Books API and return a List of Book Items.
     *
     * @return
     */

    @Override
    public List<Book> loadInBackground() {
        if (dataString == null) {
            return null;
        }

        List<Book> list = Utils.getBooksFromGoogle(dataString);
        Log.v(LOG_TAG, "loadInBackground has finished");
        return list;
    }
}
