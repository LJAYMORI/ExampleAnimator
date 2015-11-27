package com.example.choa.exampleanimator;

import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;


public class MainActivity extends AppCompatActivity {

    private MovableImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (MovableImageView) findViewById(R.id.object);

        imageView.setOnTouchMoveEvent(new OnTouchMoveEvent() {
            @Override
            public void onMoving(float prevX, float prevY, float curX, float curY) {
                float dx = curX - prevX;
                float dy = curY - prevY;

                imageView.setTranslationX(dx);
                imageView.setTranslationY(dy);
            }

            @Override
            public void onFling(float curX, float curY, float velocityX, float velocityY) {
                Log.d("moving", "curX :" + curX + ", curY : " + curY + ", velocityX : " + velocityX + ", velocityY : " + velocityY);
                Interpolator interpolator = AnimationUtils
                        .loadInterpolator(MainActivity.this, android.R.anim.decelerate_interpolator);

                Path path = new Path();
                path.lineTo(curX, curY);

                ObjectAnimator anim = ObjectAnimator.ofFloat(imageView, "translationX", "translationY", path);


                anim.setDuration(500);
                anim.setInterpolator(interpolator);
                anim.start();
            }
        });

    }

    public interface OnTouchMoveEvent {
        void onMoving(float prevX, float prevY, float curX, float curY);
        void onFling(float curX, float curY, float velocityX, float velocityY);
    }
}
