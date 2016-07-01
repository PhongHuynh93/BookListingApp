package com.atikur.booklistingapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new SearchBooksTask().execute("https://www.googleapis.com/books/v1/volumes?q=android&maxResults=20");
    }

    private class SearchBooksTask extends AsyncTask<String, Void, String> {

        ArrayList<String> books;

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute called.");
        }

        @Override
        protected String doInBackground(String... params) {
            Log.d(TAG, "doing in background.");

            try {
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }

                JSONObject rootObject = new JSONObject(builder.toString());
                JSONArray bookItems = rootObject.getJSONArray("items");

                for (int i = 0; i < bookItems.length(); i++) {
                    JSONObject bookItem = bookItems.getJSONObject(i);
                    JSONObject voluemInfo = bookItem.getJSONObject("volumeInfo");
                    String title = voluemInfo.getString("title");

                    JSONArray authorsObject = voluemInfo.getJSONArray("authors");
                    String[] authorsArr = new String[authorsObject.length()];

                    for (int j = 0; j < authorsObject.length(); j++) {
                        authorsArr[j] = authorsObject.getString(j);
                    }

                    Log.d(TAG, "Title: " + title + ", Author: " + authorsArr[0]);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d(TAG, "onPostExecute called.");
        }
    }
}
