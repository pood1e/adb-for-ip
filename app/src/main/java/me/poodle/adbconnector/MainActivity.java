package me.poodle.adbconnector;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import me.poodle.adbconnector.service.ServerService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startForeground();
    }

    @SuppressLint("NewApi")
    public void startForeground() {
        Intent intent = new Intent(this, ServerService.class);
        startForegroundService(intent);
    }
}
