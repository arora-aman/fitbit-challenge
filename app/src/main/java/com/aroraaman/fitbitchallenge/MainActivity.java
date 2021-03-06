package com.aroraaman.fitbitchallenge;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aroraaman.fitbitchallenge.model.Row;
import com.aroraaman.fitbitchallenge.viewmodel.ColorsViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ColorsViewModel mViewModel;
    private ListView mListView;
    private CommandsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewModel = ViewModelProviders.of(this).get(ColorsViewModel.class);
        mViewModel.setMainThreadHandler(new Handler());

        mListView = findViewById(R.id.commandList);
        mAdapter = new CommandsAdapter(new ArrayList<Row>());
        mListView.setAdapter(mAdapter);
        
        LiveData<ArrayList<Row>> data = mViewModel.connectSocketAndProcess("", 1234); // ViewModel will pick default values since host is missing

        data.observe(this, new Observer<ArrayList<Row>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Row> rows) {
                    if (rows == null) {
                    return;
                }

                mAdapter.setRows(rows);
                mListView.setSelection(mAdapter.getCount() -1);
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

            holder.mTextView.setBackground(new ColorDrawable(mViewModel.getBackgroundColor(row)));
            holder.mTextView.setTextColor(mViewModel.calcTextColor(row));

            return view;
        }

        private void setRows(ArrayList<Row> rows) {
            mRows = rows;
            notifyDataSetChanged();
        }
    }

    static class ViewHolder {
        final TextView mTextView;

        ViewHolder(View view) {
            mTextView = view.findViewById(R.id.text);
        }
    }
}