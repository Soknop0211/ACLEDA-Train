package com.example.acledaproject;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.SeekBar;

import androidx.appcompat.widget.AppCompatSeekBar;

public class SmoothSeekBar extends AppCompatSeekBar implements SeekBar.OnSeekBarChangeListener {

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;

    private boolean mNeedCallListener = true;

    private ValueAnimator mAnimator;

    public SmoothSeekBar(Context context) {
        super(context);
        init(context);
    }

    public SmoothSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SmoothSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    public void init(Context context) {
        Context mContext = context;
    }

    @Override
    public void setOnSeekBarChangeListener(
            SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        mOnSeekBarChangeListener = onSeekBarChangeListener;
        super.setOnSeekBarChangeListener(this);
    }

    @Override
    public void setProgress(final int progress) {
        final int currentProgress = getProgress();
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator.removeAllUpdateListeners();
            mAnimator.removeAllListeners();
            mAnimator = null;
            mNeedCallListener = true;
        }
        mAnimator = ValueAnimator.ofInt(currentProgress, progress);
        mAnimator.setDuration(getResources().getInteger(android.R.integer.config_longAnimTime));
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                if (value == progress) {
                    mNeedCallListener = true;
                } else {
                    mNeedCallListener = false;
                }
                // Logger.d("ProgressBar value ", value + "");
                SmoothSeekBar.super.setProgress(value);
            }
        });
        mAnimator.start();

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser || mNeedCallListener) {
            if (mOnSeekBarChangeListener != null) {
                mOnSeekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStartTrackingTouch(seekBar);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStopTrackingTouch(seekBar);
        }
    }
}