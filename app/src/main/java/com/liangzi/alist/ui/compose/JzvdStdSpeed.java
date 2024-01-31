package com.liangzi.alist.ui.compose;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.liangzi.alist.R;

import cn.jzvd.JzvdStd;

public class JzvdStdSpeed extends JzvdStd {
    TextView tvSpeed;
    FrameLayout surface_container;
    int currentSpeedIndex = 2;

    private GestureDetector gestureDetector;
    private Handler longPressHandler = new Handler(Looper.getMainLooper());
    private boolean longPressStarted = false;

    public JzvdStdSpeed(Context context) {
        super(context);
    }

    public JzvdStdSpeed(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);
        tvSpeed = findViewById(R.id.tv_speed);
        tvSpeed.setOnClickListener(view -> {
            if (currentSpeedIndex == 10) {
                currentSpeedIndex = 0;
            } else {
                currentSpeedIndex += 1;
            }
            mediaInterface.setSpeed(getSpeedFromIndex(currentSpeedIndex));
            tvSpeed.setText(getSpeedFromIndex(currentSpeedIndex) + "X");
            jzDataSource.objects[0] = currentSpeedIndex;
        });
    }

    public void setScreenNormal() {
        super.setScreenNormal();
        tvSpeed.setVisibility(View.GONE);
    }

    @Override
    public void setScreenFullscreen() {
        super.setScreenFullscreen();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
            tvSpeed.setVisibility(View.VISIBLE);

        if (jzDataSource.objects == null) {
            Object[] object = {2};
            jzDataSource.objects = object;
            currentSpeedIndex = 2;
        } else {
            currentSpeedIndex = (int) jzDataSource.objects[0];
        }
        if (currentSpeedIndex == 2) {
            tvSpeed.setText("倍速");
        } else {
            tvSpeed.setText(getSpeedFromIndex(currentSpeedIndex) + "X");
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.layout_std_speed;
    }

    private float getSpeedFromIndex(int index) {
        float ret = 0f;
        if (index == 0) {
            ret = 0.5f;
        } else if (index == 1) {
            ret = 0.75f;
        } else if (index == 2) {
            ret = 1.0f;
        } else if (index == 3) {
            ret = 1.25f;
        } else if (index == 4) {
            ret = 1.5f;
        } else if (index == 5) {
            ret = 1.75f;
        } else if (index == 6) {
            ret = 2.0f;
        } else if (index == 7) {
            ret = 2.5f;
        } else if (index == 8) {
            ret = 3.0f;
        } else if (index == 9) {
            ret = 4f;
        } else if (index == 10) {
            ret = 5f;
        }
        return ret;
    }

}
