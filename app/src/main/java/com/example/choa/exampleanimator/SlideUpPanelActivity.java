package com.example.choa.exampleanimator;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.vingle.framework.util.ColorUtils;

/**
 * Created by Choa on 2015. 11. 25..
 */
public abstract class SlideUpPanelActivity extends Activity {
    private static final int BACKGROUND_ALPHA = 0x33;
    private static final int BACKGROUND_ANIMATION_DURATION = 150;
    private static final int BACKGROUND_ANIMATION_FAST_DURATION = 50;
    private static final int SLIDE_UP_DURATION = 400;
    private static final int SLIDE_DOWN_DURATION = 300;
    private static final int SLIDE_DOWN_FAST_DURATION = 200;

    private int mStatusBarColor;
    private boolean mIsInAnimation;

    private ResolverDrawerLayout mDrawer;
    private ColorDrawable mBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slideup_panel_activity);

        mDrawer = (ResolverDrawerLayout) findViewById(R.id.drawer);

        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText("SLIDE UP PANEL");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mStatusBarColor = getWindow().getStatusBarColor();
        }

        mDrawer.setVisibility(View.INVISIBLE);
        fadeBackground(true, false, null);
    }

    @Override
    public void onBackPressed() {
        if (mIsInAnimation) { return; }
        onCancel();
        slideDown(false, null);
    }

    private void fadeBackground(final boolean in, boolean fast, final Runnable callback) {
        final Window window = getWindow();
        if (mBackground == null) {
            mBackground = new ColorDrawable(Color.argb(BACKGROUND_ALPHA, 0, 0, 0));
            window.setBackgroundDrawable(mBackground);
        }
        ValueAnimator backgroundAlphaAnim = in ? ValueAnimator.ofInt(0, BACKGROUND_ALPHA) :
                ValueAnimator.ofInt(BACKGROUND_ALPHA, 0);
        backgroundAlphaAnim.setDuration(fast ? BACKGROUND_ANIMATION_FAST_DURATION : BACKGROUND_ANIMATION_DURATION);
        backgroundAlphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animation.getAnimatedFraction();
                if (!in) {
                    fraction = 1f - fraction;
                }
                mBackground.setAlpha((int) (fraction * 0xff));
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            backgroundAlphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int overlayColor = Color.argb((Integer) animation.getAnimatedValue(), 0, 0, 0);
                    int color = ColorUtils.render(mStatusBarColor, overlayColor);
                    getWindow().setStatusBarColor(color);
                }
            });
        }
        backgroundAlphaAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsInAnimation = false;
                if (in) {
                    onStartSlideIn();
                    slideIn();
                } else {
                    finish();
                }
                if (callback != null) {
                    callback.run();
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mIsInAnimation = true;
            }
        });
        backgroundAlphaAnim.start();
    }

    protected View inflateContentView(@LayoutRes int layoutId) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View contentView = inflater.inflate(layoutId, mDrawer, false);
        mDrawer.addView(contentView, mDrawer.getChildCount() - 1);
        return contentView;
    }

    protected void onStartSlideIn() {
    }

    private void slideDown(final boolean fast, final Runnable callback) {
        int dy = (int) (mDrawer.getHeight() - mDrawer.getCollapseOffset());
        //noinspection ResourceType
        Interpolator interpolator = AnimationUtils
                .loadInterpolator(SlideUpPanelActivity.this, R.anim.accelerate_quint);
        ObjectAnimator anim = ObjectAnimator.ofFloat(mDrawer, "translationY", 0, dy)
                .setDuration(fast ? SLIDE_DOWN_FAST_DURATION : SLIDE_DOWN_DURATION);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsInAnimation = false;
                fadeBackground(false, fast, callback);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mIsInAnimation = true;
            }
        });
        anim.setInterpolator(interpolator);
        anim.start();
    }

    private void slideIn() {
        mDrawer.setVisibility(View.VISIBLE);
        mDrawer.setOnClickOutsideListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mDrawer.setSwipeDownListener(new Runnable() {
            @Override
            public void run() {
                onCancel();
                fadeBackground(false, false, null);
            }
        });

        //noinspection ResourceType
        Interpolator interpolator = AnimationUtils
                .loadInterpolator(this, R.anim.decelerate_quint);
        ObjectAnimator anim = ObjectAnimator.ofFloat(mDrawer, "translationY", mDrawer.getMaxCollapsedHeight(), 0)
                .setDuration(SLIDE_UP_DURATION);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsInAnimation = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mIsInAnimation = true;
            }
        });
        anim.setInterpolator(interpolator);
        anim.start();
    }

    public void finish(Runnable callback) {
        slideDown(true, callback);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    protected void onCancel() {
    }

    protected void setMaxCollapsedHeight(int height) {
        mDrawer.setMaxCollapsedHeight(height);
    }
}
