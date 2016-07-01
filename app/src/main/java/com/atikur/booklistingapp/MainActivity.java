package com.atikur.booklistingapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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

    private ListView booksListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        booksListView = (ListView) findViewById(R.id.books_listview);

        final EditText searchEditText = (EditText) findViewById(R.id.search_edit_text);
        final Button searchButton = (Button) findViewById(R.id.search_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchTerm = searchEditText.getText().toString();
                new SearchBooksTask().execute("https://www.googleapis.com/books/v1/volumes?q=" + searchTerm + "&maxResults=20");
            }
        });
    }

    private class SearchBooksTask extends AsyncTask<String, Void, String> {

        ArrayList<Book> books = new ArrayList<Book>();

        @Override
        protected String doInBackground(String... params) {
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

                    // get title
                    String title = voluemInfo.getString("title");

                    // get authors
                    JSONArray authorsObject = voluemInfo.getJSONArray("authors");
                    String[] authors = new String[authorsObject.length()];
                    for (int j = 0; j < authorsObject.length(); j++) {
                        authors[j] = authorsObject.getString(j);
                    }

                    // create Book object
                    Book book = new Book(title, authors);
                    books.add(book);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    booksListView.setAdapter(new BooksAdapter(MainActivity.this, books));
                }
            });
        }
    }
}
