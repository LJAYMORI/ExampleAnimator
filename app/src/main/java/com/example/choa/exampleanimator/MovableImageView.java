package com.example.choa.exampleanimator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;

/**
 * Created by Choa on 2015. 11. 27..
 */
public class MovableImageView extends ImageView {

    public static final int MAX_MOVE_PIXEL = 1000;
    private VelocityTracker mVelocityTracker;

    private int mMaxFlingVelocity;
    private int mMinFlingVelocity;

    private float mFirstEventX;
    private float mFirstEventY;

    private Interpolator mInterpolator;

    public MovableImageView(Context context) {
        this(context, null);
    }

    public MovableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MovableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mVelocityTracker = VelocityTracker.obtain();

        mInterpolator = AnimationUtils
                .loadInterpolator(getContext(), android.R.anim.decelerate_interpolator);

        final ViewConfiguration vc = ViewConfiguration.get(context);
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = mMinFlingVelocity * 20;

        Log.d("velocity", "maxFling:" + mMaxFlingVelocity + ", minFling:" + mMinFlingVelocity);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN) {
            mVelocityTracker.clear();
        }

        mVelocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mFirstEventX = event.getRawX();
                mFirstEventY = event.getRawY();

                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final float dx = event.getRawX() - mFirstEventX;
                final float dy = event.getRawY() - mFirstEventY;

                setTranslationX(dx);
                setTranslationY(dy);

                break;
            }
            case MotionEvent.ACTION_UP: {
                float x = getX();
                float y = getY();
                layout((int) x, (int) y, (int) (x + getWidth()), (int) (y + getHeight()));

                mVelocityTracker.computeCurrentVelocity(500, mMaxFlingVelocity);

                float velX = mVelocityTracker.getXVelocity();
                float velY = mVelocityTracker.getYVelocity();

                float absVelX = Math.abs(velX);
                if (absVelX > mMaxFlingVelocity) {
                    velX = velX < 0 ? -mMaxFlingVelocity : mMaxFlingVelocity;
                } else if (absVelX < mMinFlingVelocity) {
                    velX = 0;
                }

                float absVelY = Math.abs(velY);
                if (absVelY > mMaxFlingVelocity) {
                    velY = velY < 0 ? -mMaxFlingVelocity : mMaxFlingVelocity;
                } else if (absVelY < mMinFlingVelocity) {
                    velY = 0;
                }

                float dx = velX / mMaxFlingVelocity * MAX_MOVE_PIXEL;
                float dy = velY / mMaxFlingVelocity * MAX_MOVE_PIXEL;

                Path path = new Path();
                path.lineTo(dx, dy);
                Log.d("path", "dx:" + dx + ", dy:" + dy);

                ObjectAnimator anim = ObjectAnimator.ofFloat(this, "translationX", "translationY", path);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        setClickable(false);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        float lastX = getX();
                        float lastY = getY();
                        layout((int) lastX, (int) lastY, (int) (lastX + getWidth()), (int) (lastY + getHeight()));
                        setTranslationX(0);
                        setTranslationY(0);
                        setClickable(true);
                    }
                });

                anim.setDuration(dx == 0 && dy == 0 ? 0 : 500);
                anim.setInterpolator(mInterpolator);
                anim.start();

                break;
            }
        }
        return true;
    }

}
