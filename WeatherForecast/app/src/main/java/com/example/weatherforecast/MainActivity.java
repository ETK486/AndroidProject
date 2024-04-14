package com.example.weatherforecast;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RenderNode;
import android.os.Build;
import android.os.Bundle;
import android.service.controls.actions.FloatAction;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    Button search;
    EditText location;
    FloatingActionButton calculate;
    View act1;
    TextView title;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        search = findViewById(R.id.button);
        location = findViewById(R.id.editTextText);
        calculate=findViewById(R.id.calcbutton);
        act1=findViewById(R.id.main1);
        title=findViewById(R.id.textView);
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (hour>6&&hour<=18){
            act1.setBackgroundResource(R.drawable.day);
            title.setTextColor(Color.WHITE);
        }else {
            act1.setBackgroundResource(R.drawable.night);
        }
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Location=location.getText().toString();
                if (Location.equals("")){
                    Toast.makeText(MainActivity.this,"Location Cannot be Empty", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                    intent.putExtra("Location",Location);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slidein, R.anim.slideout);
                }
            }
        });
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calc=new Intent(MainActivity.this, MainActivity3.class);
                int cx = (v.getLeft() + v.getRight()) / 2;
                int cy = (v.getTop() + v.getBottom()) / 2;
                calc.putExtra("cx", cx);
                calc.putExtra("cy", cy);
                startActivity(calc);
                overridePendingTransition(0, 0);
            }
        });

    }

}
