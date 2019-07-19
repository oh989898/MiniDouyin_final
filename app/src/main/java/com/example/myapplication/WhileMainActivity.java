package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class WhileMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_while_main);
    }

    public void myClick_whilewhite(View view){
        Intent intent = new Intent(WhileMainActivity.this,MainActivity.class);
        startActivity(intent);
    }
    public void myClick_redwhile(View view){
        Intent intent = new Intent(WhileMainActivity.this,CustomCameraActivity.class);
        startActivity(intent);
    }
    public void myClick_whileround(View view){
        Intent intent = new Intent(WhileMainActivity.this,Describe.class);
        startActivity(intent);
    }

}
