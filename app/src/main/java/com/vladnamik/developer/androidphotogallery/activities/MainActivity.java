package com.vladnamik.developer.androidphotogallery.activities;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.vladnamik.developer.androidphotogallery.R;

import java.lang.reflect.Field;


public class MainActivity extends AppCompatActivity {
    private static final String MAIN_ACTIVITY_LOG_TAG = "MainActivity";

    private final int pageMinValue = 1;
    private final int pageMaxValue = 1000;
    private int currentPageValue;

    private NumberPicker pageNumberPicker;
    private ViewPager pager;
    private MainFragmentPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //current page value retrieving
        if (savedInstanceState !=null) {
            currentPageValue = savedInstanceState.getInt("current_page_value");
        } else {
            currentPageValue = pageMinValue;
        }

        pageNumberPickerInit();

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
    }


    private void pageNumberPickerInit() {
        pageNumberPicker = (NumberPicker) findViewById(R.id.page_number_picker);
        pageNumberPicker.setMinValue(pageMinValue);
        pageNumberPicker.setMaxValue(pageMaxValue);
        pageNumberPicker.setValue(currentPageValue);
        setNumberPickerTextColor(pageNumberPicker, ContextCompat.getColor(this, R.color.mainTextColor));

        pageNumberPicker.setOnScrollListener(new NumberPicker.OnScrollListener() {
            @Override
            public void onScrollStateChange(NumberPicker view, int scrollState) {
                if (scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE){
                    currentPageValue = view.getValue();
                    loadPage(currentPageValue);
                }
            }
        });
    }

    //Set text color in NumberPicker object
    private void setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText) child).setTextColor(color);
                    numberPicker.invalidate();
                    return;
                } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
                    Log.w(MAIN_ACTIVITY_LOG_TAG, e);
                }
            }
        }
    }

    public void onPageLeftArrowClick(View view) {
        if (pageNumberPicker.getValue() != pageMinValue) {
            pageNumberPicker.setValue(currentPageValue - 1);
            currentPageValue--;
            loadPage(pageNumberPicker.getValue());
        }
    }

    public void onPageRightArrowClick(View view) {
        if (pageNumberPicker.getValue() != pageMaxValue) {
            pageNumberPicker.setValue(currentPageValue + 1);
            currentPageValue++;
            loadPage(pageNumberPicker.getValue());
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("current_page_value", currentPageValue);
    }

    private int positionToPageNumber(int position) {
        return position + pageMinValue;
    }

    private int pageNumberToPosition(int pageNumber) {
        return pageNumber - pageMinValue;
    }

    private void loadPage(int pageNumber) {
        Log.d(MAIN_ACTIVITY_LOG_TAG, "loading page; current position is " + pageNumberToPosition(pageNumber));
        pager.setCurrentItem(pageNumberToPosition(pageNumber));
    }

    private class MainFragmentPagerAdapter extends FragmentPagerAdapter {
        private final FragmentManager mFragmentManager;

        MainFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            PageFragment pageFragment = new PageFragment();
            Bundle arguments = new Bundle();

            int pageNumber = positionToPageNumber(position);
            if (pageNumber < pageMinValue || pageNumber > pageMaxValue) {
                pageNumber = pageMinValue;
            }
            arguments.putInt(PageFragment.ARGUMENT_PAGE_NUMBER, pageNumber);
            pageFragment.setArguments(arguments);
            return pageFragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            Log.d(MAIN_ACTIVITY_LOG_TAG, "set primary item; position is " + position);
            super.setPrimaryItem(container, position, object);
            currentPageValue = positionToPageNumber(position);
            pageNumberPicker.setValue(currentPageValue);

            PageFragment selectedFragment = pagerAdapter.getFragmentByPosition(pager, position);
            if (selectedFragment != null) {
                selectedFragment.startProgressDialogBeforeRequest();
            }
        }

        @Override
        public int getCount() {
            return pageMaxValue;
        }

        private PageFragment getFragmentByPosition(ViewGroup container, int position) {
            return (PageFragment) mFragmentManager
                    .findFragmentByTag(
                            makeFragmentName(container.getId(), position)
                    );
        }

        private String makeFragmentName(int viewId, long id) {
            return "android:switcher:" + viewId + ":" + id;
        }
    }

}
