package com.example.weatherforecast;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity3 extends AppCompatActivity {
    protected EditText temperatureEditText;
    protected TextView fahrenheitTextView;
    protected TextView kelvinTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main3);
        temperatureEditText = findViewById(R.id.temperatureEditText);
        fahrenheitTextView = findViewById(R.id.fahrenheitTextView);
        kelvinTextView = findViewById(R.id.kelvinTextView);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main3), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button convertButton = findViewById(R.id.convertButton);
        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double temperature = Double.parseDouble(temperatureEditText.getText().toString());
                double fahrenheit = (temperature * 9 / 5) + 32;
                double kelvin = temperature + 273.15;
                fahrenheitTextView.setText("Fahrenheit: " + fahrenheit + "°F");
                fahrenheitTextView.setVisibility(View.VISIBLE);
                kelvinTextView.setText("Kelvin: " + kelvin + "K");
                kelvinTextView.setVisibility(View.VISIBLE);
            }
        });
    }
}