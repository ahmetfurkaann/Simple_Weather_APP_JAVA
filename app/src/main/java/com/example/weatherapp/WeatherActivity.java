package com.example.weatherapp;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class WeatherActivity extends AppCompatActivity {

    private TextView cityName, currentTemperature, feelsLike, highestTemperature, lowestTemperature, humidity, wind, seaLevel, coordinates, weatherDescription;
    private String cityID, apiKey;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        cityName = findViewById(R.id.cityName);
        currentTemperature = findViewById(R.id.currentTemperature);
        feelsLike = findViewById(R.id.feelsLikeTemperature);
        highestTemperature = findViewById(R.id.highestTemperature);
        lowestTemperature = findViewById(R.id.lowestTemperature);
        humidity = findViewById(R.id.humidity);
        wind = findViewById(R.id.wind);
        seaLevel = findViewById(R.id.seaLevel);
        coordinates = findViewById(R.id.coordinates);
        weatherDescription = findViewById(R.id.weatherDescription);

        cityID = getIntent().getStringExtra("cityID");
        apiKey = "fe6bc44b49bcd11d908d81cb9d34eb7b";

        if (cityID != null) {
            String urlString = "https://api.openweathermap.org/data/2.5/weather?APPID=" + apiKey + "&units=metric&id=" + cityID;
            new GetWeatherTask().execute(urlString);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("cityID", cityID);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cityID = savedInstanceState.getString("cityID");
        if (cityID != null) {
            new GetWeatherTask().execute(cityID);
        }
    }


    private class GetWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection conn = null;
            try {
                URL url = new URL(urls[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                Scanner inStream = new Scanner(conn.getInputStream());

                while (inStream.hasNextLine()) {
                    response += (inStream.nextLine());
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            System.out.println(response);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);

                JSONObject mainObject = jsonObject.getJSONObject("main");
                JSONObject windObject = jsonObject.getJSONObject("wind");
                JSONObject coordObject = jsonObject.getJSONObject("coord");

                String city = jsonObject.getString("name");
                String temp = mainObject.getString("temp");
                String feelsLikeTemp = mainObject.getString("feels_like");
                String tempMax = mainObject.getString("temp_max");
                String tempMin = mainObject.getString("temp_min");
                String humidityValue = mainObject.getString("humidity");
                String windSpeed = windObject.getString("speed");

                String seaLevelValue;
                if (mainObject.has("sea_level")) {
                    seaLevelValue = mainObject.getString("sea_level");
                } else {
                    seaLevelValue = "N/A";
                }

                String coordLat = coordObject.getString("lat");
                String coordLon = coordObject.getString("lon");

                JSONArray weatherArray = jsonObject.getJSONArray("weather");
                JSONObject weatherObject = weatherArray.getJSONObject(0);
                String weatherDesc = weatherObject.getString("description");

                cityName.setText(city);
                currentTemperature.setText(temp + "°C");
                feelsLike.setText("Feels Like: " + feelsLikeTemp + "°C");
                highestTemperature.setText("High: " + tempMax + "°C");
                lowestTemperature.setText("Low: " + tempMin + "°C");
                humidity.setText("Humidity: " + humidityValue + "%");
                wind.setText("Wind Speed: " + windSpeed + " m/s");
                seaLevel.setText("Sea Level: " + seaLevelValue + " m");
                coordinates.setText("Coordinates: " + coordLat + ", " + coordLon);
                weatherDescription.setText(weatherDesc);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSON Okuma Hatası", "Error: ", e);
            }
        }



    }
}
