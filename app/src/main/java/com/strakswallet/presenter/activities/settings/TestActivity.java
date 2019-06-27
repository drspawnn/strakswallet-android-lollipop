package com.strakswallet.presenter.activities.settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.strakswallet.R;


/**
 * Used for Unit testing onlyg
 */
public class TestActivity extends AppCompatActivity {
    private static final String TAG = TestActivity.class.getName();


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


}
