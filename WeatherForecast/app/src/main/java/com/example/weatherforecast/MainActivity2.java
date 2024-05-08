package com.example.weatherforecast;

import static android.content.ContentValues.TAG;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {

    ImageButton back;
    TextView tview;
    private DBHelper dbHelper;
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
        back = findViewById(R.id.imageButton2);
        tview = findViewById(R.id.textView2);
        act=findViewById(R.id.main2);
        prog=findViewById(R.id.progressbar);
        dbHelper = new DBHelper(this);
        boolean isDatabaseExists = checkDatabaseExists();

        if (isDatabaseExists) {
            Log.d(TAG, "Database exists");
        } else {
            Log.d(TAG, "Database does not exist");
        }
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
                            insertWeatherData(cityName, countryName, temp, feelsLike, humidity, description, wind, clouds, pressure);
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
    private void insertWeatherData(String cityName, String countryName, int temperature, int feelsLike, int humidity, String description, String windSpeed, String clouds, float pressure) {
        // Open the database for writing
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a ContentValues object to store the data
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_CITY_NAME, cityName);
        values.put(DBHelper.COLUMN_COUNTRY_NAME, countryName);
        values.put(DBHelper.COLUMN_TEMPERATURE, temperature);
        values.put(DBHelper.COLUMN_FEELS_LIKE, feelsLike);
        values.put(DBHelper.COLUMN_HUMIDITY, humidity);
        values.put(DBHelper.COLUMN_DESCRIPTION, description);
        values.put(DBHelper.COLUMN_WIND_SPEED, windSpeed);
        values.put(DBHelper.COLUMN_CLOUDS, clouds);
        values.put(DBHelper.COLUMN_PRESSURE, pressure);

        // Insert the data into the database
        long newRowId = db.insert(DBHelper.TABLE_NAME, null, values);

        // Check if the data was inserted successfully
        if (newRowId != -1) {
            Log.d(TAG, "Weather data inserted with ID: " + newRowId);
        } else {
            Log.e(TAG, "Error inserting weather data");
        }

        // Close the database connection
        db.close();
    }
    private boolean checkDatabaseExists() {
        SQLiteDatabase db = null;
        try {
            // Attempt to open the database
            db = SQLiteDatabase.openDatabase(
                    getDatabasePath(DBHelper.DATABASE_NAME).getPath(),
                    null,
                    SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            // Database does not exist or cannot be opened
            Log.e(TAG, "Database does not exist or cannot be opened: " + e.getMessage());
        }

        // Check if the database was opened successfully
        if (db != null) {
            // Close the database
            db.close();
            return true;
        } else {
            return false;
        }
    }
}
class DBHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "weather_database";
    private static final int DATABASE_VERSION = 1;

    static final String TABLE_NAME = "weather_data";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_CITY_NAME = "city_name";
    static final String COLUMN_COUNTRY_NAME = "country_name";
    static final String COLUMN_TEMPERATURE = "temperature";
    static final String COLUMN_FEELS_LIKE = "feels_like";
    static final String COLUMN_HUMIDITY = "humidity";
    static final String COLUMN_DESCRIPTION = "description";
    static final String COLUMN_WIND_SPEED = "wind_speed";
    static final String COLUMN_CLOUDS = "clouds";
    static final String COLUMN_PRESSURE = "pressure";

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_CITY_NAME + " TEXT," +
                    COLUMN_COUNTRY_NAME + " TEXT," +
                    COLUMN_TEMPERATURE + " INTEGER," +
                    COLUMN_FEELS_LIKE + " INTEGER," +
                    COLUMN_HUMIDITY + " INTEGER," +
                    COLUMN_DESCRIPTION + " TEXT," +
                    COLUMN_WIND_SPEED + " TEXT," +
                    COLUMN_CLOUDS + " TEXT," +
                    COLUMN_PRESSURE + " REAL)";

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

