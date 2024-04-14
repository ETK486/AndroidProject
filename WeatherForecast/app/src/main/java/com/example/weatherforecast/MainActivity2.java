package com.example.weatherforecast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.Calendar;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity2 extends AppCompatActivity {

    ImageButton back;
    TextView tview;
    View act;
    String turl;
    private final String url="https://api.openweathermap.org/data/2.5/weather";
    private final String apid="caa95405da3c122ede62d8692dc4c65e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        back = findViewById(R.id.imageButton2);
        tview = findViewById(R.id.textView2);
        act=findViewById(R.id.main2);
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour>6&&hour<=18){
            act.setBackgroundResource(R.drawable.day);
            int blacktinted = Color.parseColor("#80000000");
            tview.setTextColor(Color.WHITE);
            tview.setBackgroundTintList(ColorStateList.valueOf(blacktinted));
        }else {
            act.setBackgroundResource(R.drawable.night);
            tview.setTextColor(Color.BLACK);
            int whitetinted=Color.parseColor("#80FFFFFF");
            tview.setBackgroundTintList(ColorStateList.valueOf(whitetinted));
        }
        // Set a click listener on the back button to navigate back to MainActivity
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slidein, R.anim.slideout);
            }
        });
        getWeatherDetails();
    }

    // Method to retrieve intent extras and display in the TextView
    private void getWeatherDetails() {
        Intent intent = getIntent();
        if (intent != null) {
            String country = intent.getStringExtra("Location");
            if (country != null) {
                turl =url+"?q="+country+"&appid="+apid;
                StringRequest str = new StringRequest(Request.Method.GET, turl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.d("Response",s);
                        // Update UI with response data
                        String output;
                        try {
                            JSONObject jsonResponse=new JSONObject(s);
                            JSONArray jsonArray= jsonResponse.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                            String description=jsonObjectWeather.getString("description");
                            JSONObject jsonObjectMain=jsonResponse.getJSONObject("main");
                            int temp= (int) (jsonObjectMain.getDouble("temp")-273.15);
                            int feelsLike= (int) (jsonObjectMain.getDouble("feels_like")-273.15);
                            float pressure = jsonObjectMain.getInt("pressure");
                            int humidity=jsonObjectMain.getInt("humidity");
                            JSONObject jsonObjectWind=jsonResponse.getJSONObject("wind");
                            String wind=jsonObjectWind.getString("speed");
                            JSONObject jsonObjectClouds=jsonResponse.getJSONObject("clouds");
                            String clouds=jsonObjectClouds.getString("all");
                            JSONObject jsonObjectSys =jsonResponse.getJSONObject("sys");
                            String countryName=jsonObjectSys.getString("country");
                            String cityName=jsonResponse.getString("name");
                            int cloudper=Integer.parseInt(clouds);
                            if (temp>=35&&humidity<=30&&humidity>=0&&cloudper<=30){
                                act.setBackgroundResource(R.drawable.summerback);
                            }else if(temp<15&&humidity>30&&humidity>=45&&cloudper>30&&cloudper<=50){
                                act.setBackgroundResource(R.drawable.winterback);
                            }else if(temp>=12&&temp<35&&humidity>45&&cloudper>50){
                                act.setBackgroundResource(R.drawable.rainback);
                            }
                            output = "Current weather of " + cityName + "(" + countryName + ")" + "\n Temp: " + String.valueOf(temp)+"°C"+"\n Feels Like: "+String.valueOf(feelsLike)+"°C"+"\n Humidity: "+String.valueOf(humidity)+" %"+"\n Description: "+description+"\n Wind Speed: "+String.valueOf(wind)+" m/s (meters per second)"+"\n Cloudiness: "+String.valueOf(clouds)+" %"+"\n Pressure: "+String.valueOf(pressure)+" hPa";
                            tview.setText(output);
                        }catch (Exception e){
                            tview.setText("Error parsing weather data: " + e.getMessage());
                            Log.e("JSON Parse Error", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        tview.setText("Error: " + volleyError.getMessage());
                        Log.e("Error", volleyError.getMessage());
                    }
                });
                RequestQueue requeue= Volley.newRequestQueue(getApplicationContext());
                requeue.add(str);
            } else {
                tview.setText("Location is null");
            }
        } else {
            tview.setText("Intent is null");
        }
    }
}
