package com.example.weatherforecast;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
<<<<<<< HEAD
=======
import android.widget.EditText;
import android.widget.Toast;
>>>>>>> 49f42706eedc7c1964477cab26c4233588e75297

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Button search;
<<<<<<< HEAD
=======
    EditText location;
>>>>>>> 49f42706eedc7c1964477cab26c4233588e75297

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        search = findViewById(R.id.button);
<<<<<<< HEAD

        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    startActivity(new Intent(MainActivity.this, MainActivity2.class));
                    return true;
                }
                return false;
=======
        location = findViewById(R.id.editTextText);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Location=location.getText().toString();
                if (Location.equals("")){
                    Toast.makeText(MainActivity.this,"Location Cannot be Empty", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                    startActivity(intent);
                }
>>>>>>> 49f42706eedc7c1964477cab26c4233588e75297
            }
        });
    }
}
