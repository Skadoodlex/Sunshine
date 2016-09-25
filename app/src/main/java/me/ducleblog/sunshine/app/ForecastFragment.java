package me.ducleblog.sunshine.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
     * A placeholder fragment containing a simple view.
     */
    public class ForecastFragment extends Fragment {

    public ForecastFragment() {
    }

    @Override
    //LayoutInflater is a public class, that has the constructor getLayoutInflater that creates
    // a standard LayoutInflater Instance. Give this as an argument to onCreateView, and then
    //using  its .inflate method, creates rootView (UI component).
    // ViewGroup object is a base class for layouts and view containers.
    //  Bundles are used to pass data to various activities (roughly one screen).
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //create rootView containing all the other views.
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        //an array of fake data
        String a[] = {"Today - Sunny - 88/63", "Tomorrow - Rainy - 55/20",
                "Wednesday - Cloudy - 25/23", "Thursday - Asteroids - 75/65"};

        //Interface List can not be instantiated.
        //List<String> == List<MyType> an interface type for weekForecast.
        //ArrayList<String> is an implementation of List<String>
        //Arrays.asList(a) converts an array of string into List type.
        List<String> weekForecast = new ArrayList<String>(Arrays.asList(a));


        //Making an Adapter to control the Data view
        //list_item_forecast is the id of the ListView in the fragment (sublcass of View)
        //displaying lists by displaying multiples of a layout.
        //List_item_forecast_textview is the id of the textView (subclass of View)
        // ID of list item layout R.layout
        // ID of text view R.id specifics a xml element with id attribute
        //
        ArrayAdapter<String> myArrayAdapt = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast, R.id.list_item_forecast_textview, weekForecast);


        //Now binding the adapter to a list view
        //Casts the rootView's list_item_forecast layout which is a textView to a ListView
        ListView listView = (ListView) rootView.findViewById(R.id.list_view_forecast);
        listView.setAdapter(myArrayAdapt);

        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<Void, Void, Void> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=Toronto" +
                        "&mode=json&units=metric&cnt=7&APPID=f9592d2b6c31f365b60e68071282145f");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }
    }
}