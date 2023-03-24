package com.example.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout homeRL;
    private ProgressBar loadingPB;
    private TextView cityNameh,temperatureTv,conditionTv;
    private TextInputEditText cityEdt;
    private ImageView backIv,logoIv,searchIv;
    private RecyclerView weatherRv;
    private ArrayList<WatherRvModel> watherRvModelArrayList;
    private WeatherRvAdapter weatherRvAdapter;
   private LocationManager locationManager;
   private int PERMISSION_CODE=1;
   private String CityNameGet;
   Criteria criteria;
   String bestProvider;
   double latitude;
   double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        homeRL=findViewById(R.id.idhome);
        loadingPB=findViewById(R.id.progressBar);
        cityNameh=findViewById(R.id.txtcity);
        temperatureTv=findViewById(R.id.txtTemperature);
        conditionTv=findViewById(R.id.txtTemperatureCondition);
        cityEdt=findViewById(R.id.edtInputCity);
        backIv=findViewById(R.id.imgBack);
        logoIv=findViewById(R.id.imgicon);
        searchIv=findViewById(R.id.imgSearch);
        weatherRv=findViewById(R.id.rweather);
        watherRvModelArrayList=new ArrayList<>();
        weatherRvAdapter=new WeatherRvAdapter(this,watherRvModelArrayList);
        weatherRv.setAdapter(weatherRvAdapter);
        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
        }
        Location location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//        criteria=new Criteria();
//        bestProvider=String.valueOf(locationManager.getBestProvider(criteria,true)).toString();
//        Location location=locationManager.getLastKnownLocation(bestProvider);
//        if(location!=null){
//            Log.e("TAG","GPS is ON");
//            latitude=location.getLatitude();
//            longitude=location.getLatitude();
//        }
         longitude=location.getLongitude();
         latitude=location.getLatitude();


        Log.d("LOg","LOng="+longitude+"Latitude"+latitude);
        CityNameGet=getcityname(longitude,latitude);
        getWeatherInfo(CityNameGet);
        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city=cityEdt.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please Enter City Name", Toast.LENGTH_SHORT).show();
                }
                else{
                    cityNameh.setText(city);
                    getWeatherInfo(city);
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Provide the Permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getcityname(double longitude, double latitude){
        String cityName="Not Found";
        Geocoder gcd=new Geocoder(getBaseContext(), Locale.getDefault());
        try{
            List<Address> addresses=gcd.getFromLocation(latitude,longitude,10);
            for(Address adr:addresses){
                if(adr!=null){
                    String city=adr.getLocality();
                    if(city!=null&&!city.equals("")){
                        cityName= city;

                    }
                    else{
                        Log.d("Tag","City Not Found");
                        Toast.makeText(this, "City Not Found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        return cityName;
    }

    private void getWeatherInfo(String cityName){
        String url="http://api.weatherapi.com/v1/forecast.json?key=74767e5cd9374fccaf842321232303&q="+cityName+"&days=1&aqi=no&alerts=no";
        cityNameh.setText(cityName);

        RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(MainActivity.this, "done", Toast.LENGTH_SHORT).show();
                loadingPB.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                watherRvModelArrayList.clear();

                try {
                    String temperature=response.getJSONObject("current").getString("temp_c");
                    Log.d("CHECK","APICHECK:"+response.getJSONObject("current").getInt("is_day"));
                    temperatureTv.setText(temperature+"°c");
                    int isDay=response.getJSONObject("current").getInt("is_day");
                    String condition=response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon=response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(logoIv);
                    conditionTv.setText(condition);
                    if(isDay==1){
                        Picasso.get().load("https://images.unsplash.com/photo-1512641406448-6574e777bec6?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=387&q=80").into(backIv);
                    }
                    else{
                        Picasso.get().load("https://images.unsplash.com/photo-1500877015165-e1fb7f2db007?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=388&q=80").into(backIv);
                    }
                    JSONObject forcaseObj=response.getJSONObject("forecast");
                    JSONObject foreCastO=forcaseObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray=foreCastO.getJSONArray("hour");
                    for(int i=0;i< hourArray.length();i++){
                        JSONObject hourObj=hourArray.getJSONObject(i);
                        String time =hourObj.getString("time");
                        String temper =hourObj.getString("temp_c");
                        String img =hourObj.getJSONObject("condition").getString("icon");
                        String wind =hourObj.getString("wind_kph");
                        watherRvModelArrayList.add(new WatherRvModel(time,temper,img,wind));


                    }
                    weatherRvAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please Enter Valid City Name.....", Toast.LENGTH_SHORT).show();
                Log.d("ERROR:","ERR="+error.getLocalizedMessage());
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}