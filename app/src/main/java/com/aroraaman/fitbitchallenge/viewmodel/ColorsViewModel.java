package com.aroraaman.fitbitchallenge.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.ColorInt;

import com.aroraaman.fitbitchallenge.model.Command;
import com.aroraaman.fitbitchallenge.model.Row;
import com.aroraaman.fitbitchallenge.socket.SocketAsyncTask;
import com.aroraaman.fitbitchallenge.wrapper.ClientSocketWrapper;

import java.util.ArrayList;

public class ColorsViewModel extends ViewModel {
    private static final String HOST = "192.168.0.112";
    private static final int PORT = 1234;

    private final ClientSocketWrapper mWrapper = new ClientSocketWrapper();

    private Handler mMainThreadHandler; // needed to call setValue on MutableLiveData for immediate updates

    public void setMainThreadHandler(Handler handler) {
        mMainThreadHandler = handler;
    }

    public LiveData<ArrayList<Row>> connectSocketAndProcess(String host, int port) {
        MutableLiveData<ArrayList<Row>> data = new MutableLiveData<>();

        if (host.isEmpty()) {
            host = HOST;
            port = PORT;
        }

        SocketAsyncTask task = new SocketAsyncTask(mWrapper, new BytesProcessedCallback(data));
        task.execute(new SocketAsyncTask.SocketAddress(host, port));

        return data;
    }

    @ColorInt
    public int getBackgroundColor(Row row) {
        return Color.rgb(row.fRValue, row.fGValue, row.fBValue);
    }

    // Ref: https://stackoverflow.com/questions/1855884/determine-font-color-based-on-background-color
    @ColorInt
    public int calcTextColor(Row row) {
        int r = row.fRValue;
        int g = row.fGValue;
        int b = row.fBValue;
        // Counting the perceptive luminance - human eye favors green color...
        double a = 1 - (0.299 * r + 0.587 * g + 0.114 * b) / 255;

        // bright colors - black font
        // dark colors - white font

        return a < 0.5 ? Color.BLACK : Color.WHITE;
    }

    private class BytesProcessedCallback implements SocketAsyncTask.OnBytesCallback {

        private final MutableLiveData<ArrayList<Row>> mData;

        private BytesProcessedCallback(MutableLiveData<ArrayList<Row>> data) {
            mData = data;
        }

        @Override
        public void onBytesRead(int commandId, int rValue, int gValue, int bValue) {
            ArrayList<Row> currentList = mData.getValue();

            final ArrayList<Row> list;
            if (currentList != null) {
               list = new ArrayList<>(currentList);
            } else {
                list = new ArrayList<>();
            }

            Row lastRow;
            int size = list.size();

            if (size != 0) {
                lastRow = list.get(size -1);
            } else {
                lastRow = new Row("", 0, 0, 0, 127, 127, 127);
            }

            Command command = null;
            int fRValue = 0, fGValue = 0, fBValue = 0;

            if (Command.RELATIVE.INT_VALUE == commandId) {
                command = Command.RELATIVE;
                fRValue = rValue + lastRow.fRValue;
                fGValue = gValue + lastRow.fGValue;
                fBValue = bValue + lastRow.fBValue;
            } else if (Command.ABSOLUTE.INT_VALUE == commandId) {
                command = Command.ABSOLUTE;
                fRValue = rValue;
                fGValue = gValue;
                fBValue = bValue;
            }

            Row newRow = new Row(command.name(), rValue, gValue, bValue, fRValue, fGValue, fBValue);

            list.add(newRow);

            if (mMainThreadHandler != null) {
                mMainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mData.setValue(list);

                    }
                });
            } else {
                mData.postValue(list);
            }
        }
    }
}
