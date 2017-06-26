package com.example.android.booklisting;

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

public class Utils {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = Utils.class.getSimpleName();

    /**
     * Creates an URL and return a List of Book objects.
     */
    public static List<Book> getBooksFromGoogle(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract data from Google books API (JSON) response.
        List<Book> bookList = extractFeatureFromJson(jsonResponse);

        return bookList;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Makes a connection to the google books API and returns the JSON response as a string.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Makes a readable string from the streamed data.
     */
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
     * Return an List<Book> from the parsed JSON response.
     */
    private static List<Book> extractFeatureFromJson(String bookListJSON) {
        // Check if JSONObject is not empty
        if (TextUtils.isEmpty(bookListJSON)) {
            return null;
        }

        try {
            JSONObject baseJsonResponse = new JSONObject(bookListJSON);
            JSONArray itemsArray = baseJsonResponse.getJSONArray("items");
            int length = itemsArray.length();

            // Check if array is empty and return early or extract data to list.
            if (itemsArray.length() == 0) {
                Log.v(LOG_TAG, "JSONArray is empty");
                ArrayList<Book> noList = null;
                return noList;
            } else {
                ArrayList<Book> bookItems = new ArrayList<>();

                // Make new Book items for the items in the list.
                for (int i = 0; i < length; i++) {
                    JSONObject item = itemsArray.getJSONObject(i);
                    JSONObject volumeInfo = item.getJSONObject("volumeInfo");

                    // Get title information
                    String title = volumeInfo.getString("title");
                    // try to get description. if no desciption is available the set the standard
                    // message.
                    String description;
                    try {
                        description = volumeInfo.getString("description");
                    } catch (JSONException e) {
                        description = "no description available";
                    }

                    // Get link information
                    String infoLink = volumeInfo.getString("infoLink");

                    // Try to get authors from author array. If none available set standard message.
                    ArrayList<String> author = new ArrayList<>();
                    try {
                        JSONArray authorArray = volumeInfo.getJSONArray("authors");
                        int arrayLength = authorArray.length();
                        for (int c = 0; c < arrayLength; c++) {
                            author.add(authorArray.getString(c));
                        }
                    } catch (JSONException c) {
                        author.add("No Author available");
                    }

                    // Add new Book items to the list
                    bookItems.add(new Book(title, author, infoLink, description));
                    Log.v(LOG_TAG, "item " + i + "is created");
                }
                // return the whole List<Book>
                return bookItems;
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the Google Books JSON results", e);
        }
        return null;
    }
}
