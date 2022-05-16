package com.example.igroove;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class WelcomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        getSupportActionBar().hide();
        Thread t = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    Intent intent = new Intent(WelcomeScreen.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };t.start();
    }
}