package com.example.choa.exampleanimator;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.ImageView;

/**
 * Created by Choa on 2015. 11. 27..
 */
public class MovableImageView extends ImageView {

    private VelocityTracker mVelocityTracker;
    private int mActivePointerId = MotionEvent.INVALID_POINTER_ID;


    private float mFirstTouchX;
    private float mFirstTouchY;
    private float mMinFlingVelocity;
    private float mMaxFlingVelocity;
    private float mTouchSlop;

    private MainActivity.OnTouchMoveEvent mTouchEvent;

    public MovableImageView(Context context) {
        this(context, null);
    }

    public MovableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MovableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mVelocityTracker = VelocityTracker.obtain();

        final ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
        mMaxFlingVelocity = vc.getScaledMaximumFlingVelocity();
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                Log.d("onTouchEvent", "down");
                mVelocityTracker.clear();

                mFirstTouchX = event.getRawX();
                mFirstTouchY = event.getRawY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final float x = event.getRawX();
                final float y = event.getRawY();
                Log.d("onTouchEvent", "move x : " + x + ", y : " + y);

                if (mTouchEvent != null) {
                    mTouchEvent.onMoving(mFirstTouchX, mFirstTouchY, x, y);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                Log.d("onTouchEvent", "up");

                final float x = event.getRawX();
                final float y = event.getRawY();

                mVelocityTracker.computeCurrentVelocity(1000);

                float velocityX = mVelocityTracker.getXVelocity();
                float velocityY = mVelocityTracker.getYVelocity();

                if (mTouchEvent != null) {
                    mTouchEvent.onFling(x, y ,velocityX, velocityY);
                }

                break;
            }
            default: {
                return super.onTouchEvent(event);
            }
        }
        return true;
    }

    private void resetTouch() {
        mActivePointerId = MotionEvent.INVALID_POINTER_ID;
        mVelocityTracker.clear();

    }

    public void setOnTouchMoveEvent(MainActivity.OnTouchMoveEvent mTouchEvent) {
        this.mTouchEvent = mTouchEvent;
    }
}
