package com.example.weatherforecast;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class MainActivity4 extends AppCompatActivity {

    TextView tex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main4);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        tex = findViewById(R.id.tex3);

        // Retrieve weather data from the database
        DBHelper dbHelper = new DBHelper(this);
        List<WeatherData> weatherDataList = dbHelper.getWeatherData();

        if (!weatherDataList.isEmpty()) {
            // If weather data is available, set the text of the TextView
            StringBuilder stringBuilder = new StringBuilder();
            for (WeatherData weatherData : weatherDataList) {
                stringBuilder.append(weatherData.toString()).append("\n\n");
            }
            tex.setText(stringBuilder.toString());
        } else {
            // If weatherDataList is empty, show a message or handle the situation accordingly
            Toast.makeText(this, "No weather data available", Toast.LENGTH_SHORT).show();
        }
    }
}
