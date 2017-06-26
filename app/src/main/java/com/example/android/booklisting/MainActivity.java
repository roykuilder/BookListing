package com.example.android.booklisting;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.booklisting.Utils.LOG_TAG;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private ProgressBar loadingIndicator;
    private BookListAdapter adapter;
    private TextView emptyListView;

    private static final String API_REQUEST_1 = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final String API_REQUEST_2 = "&maxResults=10";
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // creates an ArrayList and sets the a custom adapter on it.
        // the empty view is added to display a hint for the user.
        ArrayList<Book> emptyList = new ArrayList<>();
        emptyListView = (TextView) findViewById(R.id.empty_list);
        ListView bookListView = (ListView) findViewById(R.id.book_list);
        bookListView.setItemsCanFocus(true);
        bookListView.setEmptyView(emptyListView);
        adapter = new BookListAdapter(MainActivity.this, emptyList);
        bookListView.setAdapter(adapter);

        // Check for internet connectivity and store the boolean isConnected.
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        // Inform user of no internet connection.
        if (!isConnected) {
            emptyListView.setText(R.string.no_internet);
        }

        // Listen for user input on the EditText View. If the search button is clicked the
        // LoaderManager is started.
        final EditText editText = (EditText) findViewById(R.id.search_view);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    url = makeURL(editText.getText().toString());

                    if (isConnected) {
                        //Show the loading indicator and start loadermanager to handle the HTML
                        //request.
                        loadingIndicator = (ProgressBar) findViewById(R.id.progress_bar);
                        loadingIndicator.setVisibility(View.GONE);
                        LoaderManager loaderManager = getLoaderManager();
                        loaderManager.initLoader(1, null, MainActivity.this);
                    } else {
                        emptyListView.setText(R.string.no_internet);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        Log.v(LOG_TAG, "onCreateLoader()");
        // Create a new BookListLoader for the given URL
        return new BookListLoader(this, url);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, final List<Book> bookList) {
        // Clear adapter.
        Log.v(LOG_TAG, "onLoadFinisched() is called");
        adapter.clear();

        // Check if the list hold valid Object and set the list on the adapter.
        if (bookList != null && !bookList.isEmpty()) {
            loadingIndicator.setVisibility(View.GONE);
            adapter.addAll(bookList);
            Log.v(LOG_TAG, "Finished Loading: adapter updated.");

        } else {
            loadingIndicator.setVisibility(View.GONE);
            emptyListView.setText(R.string.unable);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        adapter.clear();
        Log.v(LOG_TAG, "onLoaderReset adapter is cleared");
    }

    //Build the String for the http request and include the search result.
    private String makeURL(String searchValue) {
        return API_REQUEST_1 + searchValue + API_REQUEST_2;
    }
}
