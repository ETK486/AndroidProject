package com.example.weatherforecast;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import java.util.Calendar;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;

public class LaunchActivity extends AppCompatActivity {
    LottieAnimationView lottie;
    View full;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_launch);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        full=findViewById(R.id.main);
        lottie=findViewById(R.id.lottieAnimationView2);
        lottie.animate();
        lottie.playAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i= new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slidein,R.anim.slideout);
            }
        },1500);
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int ivory = Color.parseColor("#FFFFF0");
        int blackcow = Color.parseColor("#222940");
        if (hour>6&&hour<=18){
            full.setBackgroundTintList(ColorStateList.valueOf(ivory));
        }else {
            full.setBackgroundTintList(ColorStateList.valueOf(blackcow));
        }
    }
}