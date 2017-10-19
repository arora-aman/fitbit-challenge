package com.aroraaman.fitbitchallenge;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.aroraaman.fitbitchallenge.model.Row;
import com.aroraaman.fitbitchallenge.viewmodel.ColorsViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ColorsViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewModel = ViewModelProviders.of(this).get(ColorsViewModel.class);

        LiveData<ArrayList<Row>> data = mViewModel.connectSocketAndProcess("", 1234);

        data.observe(this, new Observer<ArrayList<Row>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Row> rows) {
                if (rows == null) {
                    return;
                }

                int size = rows.size();
                Log.d(TAG, rows.get(size -1).toString());
            }
        });
    }
}