package com.example.anais.ig2work;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class HomeworkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework);

        int idHomework = this.getIntent().getExtras().getInt("idHomework");
        Log.d("id", String.valueOf(idHomework));
    }
}
