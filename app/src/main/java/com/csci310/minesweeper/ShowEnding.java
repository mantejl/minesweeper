package com.csci310.minesweeper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ShowEnding extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_main);

        Intent intent = getIntent();
        String result = intent.getStringExtra("winOrLose");
        String time = intent.getStringExtra("timeTaken");
        TextView textView = (TextView) findViewById(R.id.endingTitle);
        textView.setTextSize(30);
        textView.setText("Used " + time + " seconds." + "\n" + result);
    }

    public void returnToStart(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}
