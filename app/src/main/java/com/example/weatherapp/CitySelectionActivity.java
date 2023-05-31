package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class CitySelectionActivity extends AppCompatActivity {

    private ListView cityList;
    private SearchView searchView;
    private ArrayList<String> cities;
    private JSONArray cityJsonArray;
    private ArrayAdapter<String> adapter;
    private HashMap<String, String> cityIdMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_selection);

        cityList = findViewById(R.id.cityList);
        searchView = findViewById(R.id.searchCity);
        cities = new ArrayList<>();
        cityIdMap = new HashMap<>();

        String cityListJson = loadJSONFromAsset();
        if (cityListJson != null) {
            try {
                cityJsonArray = new JSONArray(cityListJson);
                for(int i = 0; i < cityJsonArray.length(); i++) {
                    JSONObject cityObject = cityJsonArray.getJSONObject(i);
                    String cityName = cityObject.getString("name");
                    String cityId = cityObject.getString("id");
                    cities.add(cityName);
                    cityIdMap.put(cityName, cityId);
                }
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, cities);
                cityList.setAdapter(adapter);

                cityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(CitySelectionActivity.this, WeatherActivity.class);
                        String selectedCityName = adapter.getItem(position);
                        if (selectedCityName != null) {
                            intent.putExtra("cityID", cityIdMap.get(selectedCityName));
                            startActivity(intent);
                        }
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<String> filteredCities = new ArrayList<>();
                for (String city : cities) {
                    if (city.toLowerCase().contains(newText.toLowerCase())) {
                        filteredCities.add(city);
                    }
                }
                adapter = new ArrayAdapter<>(CitySelectionActivity.this, android.R.layout.simple_list_item_1, filteredCities);
                cityList.setAdapter(adapter);
                return false;
            }
        });

    }

    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getAssets().open("city.list.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
