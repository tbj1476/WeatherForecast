package com.example.balaji.fusedlocationexample;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.balaji.fusedlocationexample.datamodel.Current;
import com.example.balaji.fusedlocationexample.datamodel.Forecast;
import com.example.balaji.fusedlocationexample.datamodel.Forecastday;
import com.example.balaji.fusedlocationexample.datamodel.WeatherModel;
import com.example.balaji.fusedlocationexample.network.RequestBlocks;
import com.example.balaji.fusedlocationexample.network.RequestBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "fusedlocation";

    private Gson gson;

    private TextView city, temperature;
    private ListView future_forecast;

    private LinearLayout loading_lay, weather_lay, current_lay, forecast_lay, error_lay;
    ImageView load;

    RotateAnimation rotate;
    Animation bottomUp;

    private Button retry_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gson = new GsonBuilder().create();

        city = findViewById(R.id.city);
        temperature = findViewById(R.id.temp);

        future_forecast = findViewById(R.id.fututre_forecast);

        loading_lay = findViewById(R.id.loading_lay);
        weather_lay = findViewById(R.id.weather_lay);
        current_lay = findViewById(R.id.current_lay);
        forecast_lay = findViewById(R.id.forecast_lay);
        error_lay = findViewById(R.id.error_lay);

        load = findViewById(R.id.loading_img);
        retry_button = findViewById(R.id.retry);

        // refresh functionality
        current_lay.setOnClickListener(clickListener);
        retry_button.setOnClickListener(clickListener);

        rotate = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(500);
        rotate.setRepeatCount(Animation.INFINITE);

        bottomUp = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bottom_up);

        callWeatherAPI();

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            callWeatherAPI();
        }
    };

    void displayWeather(String currentResponse) {

        if (currentResponse != null) {
//            Log.e(TAG,currentResponse.toString()) ;

            if (!TextUtils.isEmpty(currentResponse)) {

                JSONObject jsonObject = null;

                try {
                    jsonObject = new JSONObject(currentResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (jsonObject != null) {

                    WeatherModel weatherModel = gson.fromJson(currentResponse, WeatherModel.class);

                    com.example.balaji.fusedlocationexample.datamodel.Location location = weatherModel.getLocation();

                    String cityName = location.getName();

                    city.setText(cityName);


                    Current current = weatherModel.getCurrent();

                    int tempC = (int) current.getTempC();

                    temperature.setText(Html.fromHtml(tempC + "&#8451;"));


                    Forecast forecast = weatherModel.getForecast();

                    ArrayList<Forecastday> forecastWeather = forecast.getForecastday();

                    if (forecastWeather.size() > 0) {

                        forecastWeather.remove(0);

                        CustomWeatherAdapter forecastAdapter = new CustomWeatherAdapter(getApplicationContext(), forecastWeather);

                        future_forecast.setAdapter(forecastAdapter);

                    }


                } else {
                    loading_lay.setVisibility(View.GONE);
                    weather_lay.setVisibility(View.GONE);
                    error_lay.setVisibility(View.VISIBLE);
                }


            }

        } else {
//            Log.e(TAG,"Sorry, Response is null") ;


            loading_lay.setVisibility(View.GONE);
            weather_lay.setVisibility(View.GONE);
            error_lay.setVisibility(View.VISIBLE);
        }


    }

    private void callWeatherAPI() {

        if (!isNetworkOnline()) {

            loading_lay.setVisibility(View.GONE);
            weather_lay.setVisibility(View.GONE);
            error_lay.setVisibility(View.VISIBLE);

            return;
        }

        String api_url = null;

        try {
            api_url = getString(R.string.api_url) + RequestBuilder.PrepareRequestByAutoIP(RequestBlocks.MethodType.Forecast, getString(R.string.apixu_api_key), RequestBlocks.Days.Five);
        } catch (Exception e) {
            e.printStackTrace();

            loading_lay.setVisibility(View.GONE);
            weather_lay.setVisibility(View.GONE);
            error_lay.setVisibility(View.VISIBLE);
        }

        final OkHttpClient okHttpClient = new OkHttpClient();

        new AsyncTask<String, Response, String>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loading_lay.setVisibility(View.VISIBLE);
                load.startAnimation(rotate);
                forecast_lay.clearAnimation();
                weather_lay.setVisibility(View.GONE);
                error_lay.setVisibility(View.GONE);
            }

            @Override
            protected String doInBackground(String... strings) {

                Request.Builder requestBuilder = new Request.Builder();
                requestBuilder.url(strings[0]);
                requestBuilder.header("Cache-Control", "no-cache");
                requestBuilder.get();

                Request request = requestBuilder.build();

                String responseString = null;

                try {
                    Response response = okHttpClient.newCall(request).execute();

                    if (response.code() == 200) {
                        responseString = response.body().string();
                    }

                    response.body().close();

                } catch (IOException e) {
                    e.printStackTrace();

                    loading_lay.setVisibility(View.GONE);
                    weather_lay.setVisibility(View.GONE);
                    error_lay.setVisibility(View.VISIBLE);
                }

                return responseString;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                loading_lay.setVisibility(View.GONE);
                error_lay.setVisibility(View.GONE);
                forecast_lay.setAnimation(bottomUp);
                weather_lay.setVisibility(View.VISIBLE);

                displayWeather(s);

            }
        }.execute(api_url);


    }

    public boolean isNetworkOnline() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        Toast.makeText(getApplicationContext(),
                getString(R.string.lbl_network_unavailable),
                Toast.LENGTH_SHORT).show();

        return false;
    }


}
