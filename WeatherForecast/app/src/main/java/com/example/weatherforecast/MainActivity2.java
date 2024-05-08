package com.example.weatherforecast;

import static android.content.ContentValues.TAG;

import static com.google.firebase.messaging.Constants.MessagePayloadKeys.SENDER_ID;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity2 extends AppCompatActivity {

    ImageButton back;
    TextView tview;
    String token;
    View act;
    String turl;
    private final String url="https://api.openweathermap.org/data/2.5/weather";
    private final String apid="caa95405da3c122ede62d8692dc4c65e";
    ProgressBar prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        back = findViewById(R.id.imageButton2);
        tview = findViewById(R.id.textView2);
        act=findViewById(R.id.main2);
        prog=findViewById(R.id.progressbar);
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour > 6 && hour <= 18) {
            Log.d("datework", String.valueOf(hour));
            act.setBackgroundResource(R.drawable.day);
            int blacktinted = Color.parseColor("#80000000");
            tview.setTextColor(Color.WHITE);
            tview.setBackgroundTintList(ColorStateList.valueOf(blacktinted));
        } else {
            act.setBackgroundResource(R.drawable.night);
            tview.setTextColor(Color.BLACK);
            int whitetinted = Color.parseColor("#80FFFFFF");
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
        prog.setVisibility(View.VISIBLE);
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
                            Date currentDate = new Date();
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
                            output = "Current weather of " + cityName + "(" + countryName + ")" + "\n Temp: " + String.valueOf(temp)+"°C"+"\n Feels Like: "+String.valueOf(feelsLike)+"°C"+"\n Humidity: "+String.valueOf(humidity)+" %"+"\n Description: "+description+"\n Wind Speed: "+String.valueOf(wind)+" m/s (meters per second)"+"\n Cloudiness: "+String.valueOf(clouds)+" %"+"\n Pressure: "+String.valueOf(pressure)+" hPa";
                            // Populate the WeatherData object with values
                            WeatherData weatherData = new WeatherData();
                            weatherData.setCityName(cityName);
                            weatherData.setCountryName(countryName);
                            weatherData.setTemperature(temp);
                            weatherData.setFeelsLike(feelsLike);
                            weatherData.setHumidity(humidity);
                            weatherData.setDescription(description);
                            weatherData.setWindSpeed(wind);
                            weatherData.setClouds(clouds);
                            weatherData.setPressure(pressure);
                            weatherData.setDateTime(currentDate);

// Add the WeatherData object to Firestore
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("weatherDataCollection")
                                    .add(weatherData)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "Weather data added with ID: " + documentReference.getId());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding weather data", e);
                                        }
                                    });
                            FirebaseMessaging.getInstance().getToken()
                                    .addOnCompleteListener(new OnCompleteListener<String>() {
                                        @Override
                                        public void onComplete(@NonNull Task<String> task) {
                                            if (!task.isSuccessful()) {
                                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                                return;
                                            }

                                            // Get new FCM registration token
                                            token = task.getResult();

                                            // Log and use the token as needed
                                            Log.d(TAG, "FCM Token: " + token);
                                        }
                                    });
                            JSONObject message = new JSONObject();
                            message.put("to", token);
                            JSONObject data = new JSONObject();
                            data.put("message", output);
                            message.put("data", data);

                            AtomicInteger msgId = new AtomicInteger();
                            FirebaseMessaging.getInstance().send(new RemoteMessage.Builder(SENDER_ID + "@fcm.googleapis.com")
                                    .setMessageId(Integer.toString(msgId.incrementAndGet()))
                                    .addData("data", message.toString())
                                    .build());

                            if (temp>=35&&humidity<=30&&humidity>=0&&cloudper<=30){
                                act.setBackgroundResource(R.drawable.summerback);
                            }else if(temp<15&&humidity>30&&humidity>=45&&cloudper>30&&cloudper<=50){
                                act.setBackgroundResource(R.drawable.winterback);
                            }else if(temp>=12&&temp<35&&humidity>45&&cloudper>50){
                                act.setBackgroundResource(R.drawable.rainback);
                            }
                            tview.setText(output);
                            prog.setVisibility(View.INVISIBLE);
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

class WeatherData {
    private String cityName;
    private String countryName;
    private int temperature;
    private int feelsLike;
    private int humidity;
    private String description;
    private String windSpeed;
    private String clouds;
    private float pressure;
    private Date dateTime;
    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getFeelsLike() {
        return feelsLike;
    }

    public void setFeelsLike(int feelsLike) {
        this.feelsLike = feelsLike;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getClouds() {
        return clouds;
    }

    public void setClouds(String clouds) {
        this.clouds = clouds;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }
}

class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // Process the incoming message
        Log.d(TAG, "Message received: " + remoteMessage.getData());
        // Handle the message as needed
    }
}
