package com.pachecoluc.geolocaltest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class GameOver extends AppCompatActivity implements View.OnClickListener{

    private Button buttonRetry;
    Intent game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameover);
        game = new Intent(this, MainActivity.class);

        buttonRetry = (Button) findViewById(R.id.button_retry);
        buttonRetry.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        startActivity(game);
    }
}
