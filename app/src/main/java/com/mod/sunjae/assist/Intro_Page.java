package com.mod.sunjae.assist;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import java.util.Timer;

public class Intro_Page extends AppCompatActivity {
    Handler h;
    Timer timer;
    TextView introText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_page);
        introText = (TextView) findViewById(R.id.Introtext);
        h = new Handler();
        timer = new Timer();
        roding();
        startService(new Intent(this, Service_Page.class));
    }

    public void roding() {
        for(int i = 100; i<900; i+=100) {
            final int finalI = i;
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    introText.setTextColor(Color.parseColor("#"+(000+ finalI) +"00000"));
                }
            }, 100 + i);
        }
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                introText.setTextColor(Color.parseColor("#ffFF0000"));
                Intent intent = new Intent(Intro_Page.this, First_Page.class);
                startActivity(intent);
                finish();
            }
        }, 1200);
    }


    @Override
    public void onBackPressed(){
    }
}
