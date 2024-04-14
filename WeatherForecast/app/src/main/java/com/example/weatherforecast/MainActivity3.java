package com.example.weatherforecast;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.animation.Animator;
import android.view.ViewAnimationUtils;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity3 extends AppCompatActivity {
    protected EditText temperatureEditText;
    protected TextView fahrenheitTextView;
    protected TextView kelvinTextView;
    ImageButton img;
    View rootLayout;
    private boolean isAnimationStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_main3);
            rootLayout = findViewById(R.id.main3);
            temperatureEditText = findViewById(R.id.temperatureEditText);
            fahrenheitTextView = findViewById(R.id.fahrenheitTextView);
            kelvinTextView = findViewById(R.id.kelvinTextView);
            img=findViewById(R.id.imageButton);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main3), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
            Button convertButton = findViewById(R.id.convertButton);
            startCircularRevealAnimation();
            convertButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String temp = temperatureEditText.getText().toString();
                    if (temp.equals("")) {
                        Toast.makeText(MainActivity3.this, "Temperature cannot be Empty", Toast.LENGTH_SHORT).show();
                    } else {
                        double temperature = Double.parseDouble(temp);
                        double fahrenheit = (temperature * 9 / 5) + 32;
                        double kelvin = temperature + 273.15;
                        fahrenheitTextView.setText("Fahrenheit: " + fahrenheit + "°F");
                        fahrenheitTextView.setVisibility(View.VISIBLE);
                        kelvinTextView.setText("Kelvin: " + kelvin + "K");
                        kelvinTextView.setVisibility(View.VISIBLE);
                    }
                }
            });
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(MainActivity3.this,MainActivity.class);
                    startActivity(intent);
                    reverseCircularRevealAnimation();
                }
            });
        } catch (Exception e) {
            Log.e("Err", String.valueOf(e));
        }
    }
    private void startCircularRevealAnimation() {
        try {
            rootLayout.clearAnimation();
            rootLayout.post(new Runnable() {
                @Override
                public void run() {
                    int cx = getIntent().getIntExtra("cx", 0);
                    int cy = getIntent().getIntExtra("cy", 0);

                    rootLayout.clearAnimation();
                    Animator anim = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0,
                            Math.max(rootLayout.getWidth(), rootLayout.getHeight()));
                    anim.setDuration(500); // Set animation duration
                    anim.start();
                }
            });
        }catch (Exception e){
            Log.e("Res",String.valueOf(e));
        }
    }

    private void reverseCircularRevealAnimation() {
        try {
            int cx = getIntent().getIntExtra("cx", 0);
            int cy = getIntent().getIntExtra("cy", 0);

            // Set up the Circular Reveal animation on the root layout
            Animator anim = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy,
                    Math.max(rootLayout.getWidth(), rootLayout.getHeight()), 0);
            anim.setDuration(500); // Set animation duration

            // Set up an animation listener to finish the activity when the animation ends
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    // Finish the activity when the animation ends
                    MainActivity3.super.onBackPressed();
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });

            // Start the animation
            anim.start();
        } catch (Exception e) {
            Log.e("Res", String.valueOf(e));
        }
    }
}
