package com.example.weatherforecast;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity4 extends AppCompatActivity {
    EditText cityEditText, dateEditText;
    Button searchButton;
    TextView tex;
    ImageButton backbut;
    String selectedDate;

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
        cityEditText = findViewById(R.id.cityEditText);
        searchButton = findViewById(R.id.button2);
        backbut=findViewById(R.id.imageButton3);
        Button openCalendarButton = findViewById(R.id.open_calendar_button);
        openCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalendar();
            }
            private void openCalendar() {
                // Get current date
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // Create a DatePickerDialog and show it
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity4.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Handle selected date
                        // You can update your UI or perform any other actions here
                        selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, (month + 1), dayOfMonth);
                        // Now you have the selected date, you can use it as needed
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }

        });

        backbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity4.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slidein, R.anim.slideout);
                startActivity(intent);
            }
        });
        searchButton.setOnClickListener(v -> {
            String cityName = cityEditText.getText().toString();

            if (!cityName.isEmpty() && !selectedDate.isEmpty()) {
                DBHelper dbHelper = new DBHelper(this);
                List<WeatherData> weatherDataList = dbHelper.getWeatherData(cityName, selectedDate);
                if (!weatherDataList.isEmpty()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (WeatherData weatherData : weatherDataList) {
                        stringBuilder.append(weatherData.toString()).append("\n\n");
                    }
                    tex.setText(stringBuilder.toString());
                } else {
                    // If weatherDataList is empty, show a message or handle the situation accordingly
                    Toast.makeText(this, "No weather data available for the selected city and date", Toast.LENGTH_SHORT).show();
                    tex.setText(""); // Clear the TextView if no data is available
                }
            } else {
                Toast.makeText(this, "Please enter both city and date", Toast.LENGTH_SHORT).show();
                tex.setText(""); // Clear the TextView if input fields are empty
            }
        });


    }
}
