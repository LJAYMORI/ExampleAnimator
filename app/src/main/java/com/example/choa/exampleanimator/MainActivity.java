package com.example.choa.exampleanimator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MovableImageView imageView = (MovableImageView) findViewById(R.id.object);
        findViewById(R.id.btn_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.layout(400, 400, 400 + imageView.getWidth(), 400 + imageView.getHeight());
            }
        });

    }
}
