package com.vladnamik.developer.androidphotogallery.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

public class ConstRatioImageView extends AppCompatImageView {
    public static final String LOG_TAG = "ConstRatioImageView";
    public static final int WIDTH_RATIO = 4;
    public static final int HEIGHT_RATIO = 3;

    public ConstRatioImageView(Context context) {
        super(context);
    }

    public ConstRatioImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ConstRatioImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
        int originalHeight = MeasureSpec.getSize(heightMeasureSpec);

        int calculatedHeight = originalWidth * HEIGHT_RATIO / WIDTH_RATIO;
        int calculatedWidth = originalWidth;
        if (originalWidth == 0)
        {
            calculatedHeight = originalHeight;
            calculatedWidth = originalHeight * WIDTH_RATIO / HEIGHT_RATIO;
        }

        Log.d(LOG_TAG, "Height: " + calculatedHeight);
        Log.d(LOG_TAG, "Width: " + calculatedWidth);

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(calculatedWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(calculatedHeight, MeasureSpec.EXACTLY));
    }
}
