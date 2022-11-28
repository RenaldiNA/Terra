package com.example.terraproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.terraproject.activities.LoginActivity;

public class SplashScreen extends AppCompatActivity {
    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        ImageView imageView = findViewById(R.id.imgSplash);
        TextView textView = findViewById(R.id.txtViewSplash);

        animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade_in);
        imageView.startAnimation(animation);
        textView.startAnimation(animation);

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }, 4500); //5000 = 5 detik
    }
}