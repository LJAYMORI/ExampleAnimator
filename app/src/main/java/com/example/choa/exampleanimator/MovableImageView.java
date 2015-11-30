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

    public static final int MAX_MOVE_LENGTH = 500;
    private VelocityTracker mVelocityTracker;

    private float mMaxFlingVelocity;
    private float mMinFlingVelocity;

    private float mFirstEventX;
    private float mFirstEventY;

    private float mViewOriginX;
    private float mViewOriginY;

    private float mMovedX;
    private float mMovedY;

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
        mMinFlingVelocity = vc.getScaledMinimumFlingVelocity();
        mMaxFlingVelocity = (int) mMinFlingVelocity * 10;

        Log.d("velocity", "maxFling:" + mMaxFlingVelocity + ", minFling:" + mMinFlingVelocity);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mVelocityTracker.clear();

                mFirstEventX = event.getRawX();
                mFirstEventY = event.getRawY();

                mViewOriginX = getX();
                mViewOriginY = getY();

                Log.d("down", "originX:" + mViewOriginX + ", originY:" + mViewOriginY);

                break;
            }
            case MotionEvent.ACTION_MOVE: {
                mVelocityTracker.addMovement(event);
                final float dx = event.getRawX() - mFirstEventX;
                final float dy = event.getRawY() - mFirstEventY;

                setTranslationX(dx);
                setTranslationY(dy);

                mMovedX = dx;
                mMovedY = dy;

                break;
            }
            case MotionEvent.ACTION_UP: {
                layout((int) getX(), (int) getY(), (int) (getX() + getWidth()), (int) (getY() + getHeight()));

                mVelocityTracker.computeCurrentVelocity(500);

                float velX = mVelocityTracker.getXVelocity();
                float velY = mVelocityTracker.getYVelocity();

                if (Math.abs(velX) > mMaxFlingVelocity) {
                    velX = velX < 0 ? -mMaxFlingVelocity : mMaxFlingVelocity;
                } else if (Math.abs(velX) < mMinFlingVelocity) {
                    velX = 0;
                }

                if (Math.abs(velY) > mMinFlingVelocity) {
                    velY = velY < 0 ? -mMaxFlingVelocity : mMaxFlingVelocity;
                } else if (Math.abs(velY) < mMinFlingVelocity) {
                    velY = 0;
                }

                float dx = velX / mMaxFlingVelocity * MAX_MOVE_LENGTH;
                float dy = velY / mMaxFlingVelocity * MAX_MOVE_LENGTH;

                Interpolator interpolator = AnimationUtils
                        .loadInterpolator(getContext(), android.R.anim.decelerate_interpolator);

                Path path = new Path();
                path.lineTo(dx, dy);

                ObjectAnimator anim = ObjectAnimator.ofFloat(this, "translationX", "translationY", path);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        float x = getX();
                        float y = getY();
                        layout((int) x, (int) y, (int) (x + getWidth()), (int) (y + getHeight()));
                        setTranslationX(0);
                        setTranslationY(0);
                    }
                });

                anim.setDuration(1000);
                anim.setInterpolator(interpolator);
                anim.start();
                break;
            }
        }
        return true;
    }

}
