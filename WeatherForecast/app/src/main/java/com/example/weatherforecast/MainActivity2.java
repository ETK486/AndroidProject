package com.example.weatherforecast;

import static android.content.ContentValues.TAG;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
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
import java.util.List;
import java.util.Locale;
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

    static final String COLUMN_DATE = "date";

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
                    COLUMN_PRESSURE + " REAL," +
                    COLUMN_DATE + " TEXT)";

    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Add upgrade logic here if needed
    }

    public List<WeatherData> getWeatherData(String cityName, String date) {
        List<WeatherData> weatherDataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_CITY_NAME,
                COLUMN_COUNTRY_NAME,
                COLUMN_TEMPERATURE,
                COLUMN_FEELS_LIKE,
                COLUMN_HUMIDITY,
                COLUMN_DESCRIPTION,
                COLUMN_WIND_SPEED,
                COLUMN_CLOUDS,
                COLUMN_PRESSURE,
                COLUMN_DATE
        };
        Log.d("DBHelper", "City Name: " + cityName + ", Date: " + date);
        String selection = COLUMN_CITY_NAME + " = ? AND " + COLUMN_DATE + " = ?";
        String[] selectionArgs = { cityName, date };
        String sqlQuery = "SELECT * FROM " + TABLE_NAME;
        Log.d("DBHelper", "Executing SQL query: " + sqlQuery);

        Cursor cursor = db.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        try {
            while (cursor.moveToNext()) {
                String rowData = "";
                for (String column : projection) {
                    int columnIndex = cursor.getColumnIndexOrThrow(column);
                    rowData += column + ": " + cursor.getString(columnIndex) + ", ";
                }
                Log.d("DBHelper", "Retrieved row: " + rowData);
                String retrievedCityName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CITY_NAME));
                String countryName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COUNTRY_NAME));
                int temperature = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TEMPERATURE));
                int feelsLike = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FEELS_LIKE));
                int humidity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HUMIDITY));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                String windSpeed = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WIND_SPEED));
                String clouds = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLOUDS));
                float pressure = cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_PRESSURE));

                WeatherData weatherData = new WeatherData(retrievedCityName, countryName, temperature, feelsLike, humidity, description, windSpeed, clouds, pressure);
                weatherDataList.add(weatherData);
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error while retrieving weather data: " + e.getMessage());
        } finally {
            cursor.close();
            db.close();
        }

        Log.d("DBHelper", "Number of rows retrieved from database: " + weatherDataList.size());
        return weatherDataList;
    }


}

class WeatherData implements Parcelable {
    private String cityName;
    private String countryName;
    private int temperature;
    private int feelsLike;
    private int humidity;
    private String description;
    private String windSpeed;
    private String clouds;
    private float pressure;
    private String date;


    public String toString() {
        return "City: " + cityName + ", Country: " + countryName + "\n"
                + "Temperature: " + temperature + "°C, Feels like: " + feelsLike + "°C\n"
                + "Humidity: " + humidity + "%, Description: " + description + "\n"
                + "Wind Speed: " + windSpeed + " m/s, Cloudiness: " + clouds + "%\n"
                + "Pressure: " + pressure + " hPa\n";
    }
    public WeatherData(String cityName, String countryName, int temperature, int feelsLike, int humidity, String description, String windSpeed, String clouds, float pressure) {
        this.cityName = cityName;
        this.countryName = countryName;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.description = description;
        this.windSpeed = windSpeed;
        this.clouds = clouds;
        this.pressure = pressure;
    }

    protected WeatherData(Parcel in) {
        cityName = in.readString();
        countryName = in.readString();
        temperature = in.readInt();
        feelsLike = in.readInt();
        humidity = in.readInt();
        description = in.readString();
        windSpeed = in.readString();
        clouds = in.readString();
        pressure = in.readFloat();
    }

    public static final Parcelable.Creator<WeatherData> CREATOR = new Creator<WeatherData>() {
        @Override
        public WeatherData createFromParcel(Parcel in) {
            return new WeatherData(in);
        }

        @Override
        public WeatherData[] newArray(int size) {
            return new WeatherData[size];
        }
    };

    // Getter methods for all fields
    public String getCityName() {
        return cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public int getTemperature() {
        return temperature;
    }
    public int getFeelsLike() {
        return feelsLike;
    }

    public int getHumidity() {
        return humidity;
    }

    public String getDescription() {
        return description;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getClouds() {
        return clouds;
    }

    public float getPressure() {
        return pressure;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cityName);
        dest.writeString(countryName);
        dest.writeInt(temperature);
        dest.writeInt(feelsLike);
        dest.writeInt(humidity);
        dest.writeString(description);
        dest.writeString(windSpeed);
        dest.writeString(clouds);
        dest.writeFloat(pressure);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}

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
                startActivity(intent);
            }
        });
        getWeatherDetails();
    }
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    // Method to retrieve intent extras and display in the TextView
    private void getWeatherDetails() {
        prog.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        if (intent != null) {
            String country = intent.getStringExtra("Location");
            if (country != null) {
                String currentDate = getCurrentDate(); // Retrieve current date
                List<WeatherData> weatherDataList = dbHelper.getWeatherData(country, currentDate);
                Intent data = new Intent(MainActivity2.this, MainActivity4.class);
                data.putParcelableArrayListExtra("weatherDataList", new ArrayList<>(weatherDataList));
                turl =url+"?q="+country+"&appid="+apid;
                StringRequest str = new StringRequest(Request.Method.GET, turl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.d("Response",s);
                        String currentDate = getCurrentDate(); // Retrieve current date
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
                            insertWeatherData(cityName, countryName, temp, feelsLike, humidity, description, wind, clouds, pressure, currentDate); // Pass current date
                            prog.setVisibility(View.INVISIBLE);
                        } catch (Exception e){
                            tview.setText("Error parsing weather data: " + e.getMessage());
                            Log.e("JSON Parse Error", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        tview.setText("Error: " + volleyError.getMessage());
                        Log.e("Error", volleyError.getMessage());
                        // Insert a placeholder entry with default values and current date
                        String currentDate = getCurrentDate();
                        insertWeatherData("Unknown", "Unknown", 0, 0, 0, "N/A", "N/A", "N/A", 0, currentDate);
                        // Hide the progress bar
                        prog.setVisibility(View.INVISIBLE);
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

    private void insertWeatherData(String cityName, String countryName, int temperature, int feelsLike, int humidity, String description, String windSpeed, String clouds, float pressure, String date) {
        // Open the database for writing
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Extract date without time
        String dateWithoutTime = date.split(" ")[0]; // Extract date part

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
        values.put(DBHelper.COLUMN_DATE, dateWithoutTime); // Insert date without time into ContentValues

        try {
            // Insert the data into the database
            long newRowId = db.insert(DBHelper.TABLE_NAME, null, values);

            // Check if the data was inserted successfully
            if (newRowId != -1) {
                Log.d(TAG, "Weather data inserted with ID: " + newRowId);
            } else {
                Log.e(TAG, "Error inserting weather data: newRowId is -1");
            }
        } catch (SQLiteException e) {
            Log.e(TAG, "SQLiteException while inserting weather data: " + e.getMessage());
        } finally {
            // Close the database connection
            db.close();
        }
    }

}


