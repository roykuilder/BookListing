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
    private boolean isConnected;
    private NetworkInfo activeNetwork;
    private ConnectivityManager cm;
    private LoaderManager loaderManager;

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
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
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

                    // check for current network connection.
                    activeNetwork = cm.getActiveNetworkInfo();
                    isConnected = activeNetwork != null &&
                            activeNetwork.isConnectedOrConnecting();

                    if (isConnected) {

                        // If loaderManager is null get the loaderManager.
                        if (loaderManager == null) {
                            loaderManager = getLoaderManager();
                        }

                        // Update UI to show loading process.
                        loadingIndicator = (ProgressBar) findViewById(R.id.progress_bar);
                        loadingIndicator.setVisibility(View.VISIBLE);

                        // Check if Loader 1 and create new one or restart Loader 1.
                        if (loaderManager.getLoader(1) == null) {
                            loaderManager.initLoader(1, null, MainActivity.this);
                        } else {
                            adapter.clear();
                            loaderManager.restartLoader(1, null, MainActivity.this);
                        }
                    } else {
                        adapter.clear();
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
