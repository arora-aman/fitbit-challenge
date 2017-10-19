package com.aroraaman.fitbitchallenge;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

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

    private class CommandsAdapter extends BaseAdapter {
        private ArrayList<Row> mRows;

        private CommandsAdapter(ArrayList<Row> rows) {
            mRows = rows;
        }

        @Override
        public int getCount() {
            return mRows.size();
        }

        @Override
        public Object getItem(int position) {
            return mRows.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder holder;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.list_item, null);

                holder = new ViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            Row row = mRows.get(position);

            holder.mTextView.setText(row.toString());

            int color = Color.rgb(row.fRValue, row.fGValue, row.fBValue);
            holder.mTextView.setBackground(new ColorDrawable(color));

            return view;
        }

        private void setRows(ArrayList<Row> rows) {
            mRows = rows;
        }
    }

    static class ViewHolder {
        final TextView mTextView;

        ViewHolder(View view) {
            mTextView = view.findViewById(R.id.text);
        }
    }
}