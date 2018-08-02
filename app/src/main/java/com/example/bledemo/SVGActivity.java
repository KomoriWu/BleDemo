package com.example.bledemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import timber.log.Timber;

public class SVGActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_svg);
        Timber.tag("wz");
        Timber.d("Activity Created");
    }
}
