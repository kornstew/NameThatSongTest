package com.thelevelgrinder.namethatsong;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private String song;// something to hold data
    //
    private String baseUrl =getString(R.string.web_service_url);
    private String apiKey = getString(R.string.api_key);

    private SongArrayAdapter songArrayAdapter;  // for indenting
    private ImageView songListView; // displays song

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songListView= (ImageView)findViewById(R.id.songImageView);
       // songArrayAdapter = new SongArrayAdapter(this,song);

    }

    private class GetWeatherTask
            extends AsyncTask<URL, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(URL... params) {
            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) params[0].openConnection();
                int response = connection.getResponseCode();

                if (response == HttpURLConnection.HTTP_OK) {
                    StringBuilder builder = new StringBuilder();

                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()))) {

                        String line;

                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    }
                    catch (IOException e) {
                        Snackbar.make(findViewById(R.id.coordinatorLayout),
                                R.string.read_error, Snackbar.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    return new JSONObject(builder.toString());
                }
                else {
                    Snackbar.make(findViewById(R.id.coordinatorLayout),
                            R.string.connect_error, Snackbar.LENGTH_LONG).show();
                }
            }
            catch (Exception e) {
                Snackbar.make(findViewById(R.id.coordinatorLayout),
                        R.string.connect_error, Snackbar.LENGTH_LONG).show();
                e.printStackTrace();
            }
            finally {
                connection.disconnect(); // close the HttpURLConnection
            }

            return null;
        }

        // process JSON response and update ListView
        @Override
        protected void onPostExecute(JSONObject weather) {
            convertJSONtoArrayList(weather); // repopulate weatherList
            weatherArrayAdapter.notifyDataSetChanged(); // rebind to ListView
            weatherListView.smoothScrollToPosition(0); // scroll to top
        }
    }

}
