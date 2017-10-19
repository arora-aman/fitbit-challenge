package com.aroraaman.fitbitchallenge.socket;

import android.os.AsyncTask;
import android.util.Log;

import com.aroraaman.fitbitchallenge.model.Command;
import com.aroraaman.fitbitchallenge.wrapper.ClientSocketWrapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SocketAsyncTask extends AsyncTask<SocketAsyncTask.SocketAddress, Void, Void> {
    private static final String TAG = "SocketAsyncTask";

    private final ClientSocketWrapper mWrapper;
    private final OnBytesCallback mCallback;

    public SocketAsyncTask(ClientSocketWrapper wrapper, OnBytesCallback callback) {
        mWrapper = wrapper;
        mCallback = callback;
    }

    @Override
    protected Void doInBackground(SocketAddress... socketAddresses) {
        SocketAddress address = socketAddresses[0];

        Socket socket = null;
        InputStream is = null;

        try {
            socket = mWrapper.openSocket(address.host, address.port);
            is = mWrapper.getSocketInputStream(socket);

            while (true) {
                int commandId = parseUint8(readBits(is, 8));
                Command command = null;

                if (Command.RELATIVE.INT_VALUE == commandId) {
                    command = Command.RELATIVE;
                } else if (Command.ABSOLUTE.INT_VALUE == commandId) {
                    command = Command.ABSOLUTE;
                }

                if (command == null) {
                    socket.close();
                    throw new IOException("Invalid command " + commandId);
                }

                int[] args = new int[3];

                for (int i = 0; i < command.ARG_COUNT; ++i) {
                    if (8 == command.ARG_BITS) {
                        args[i] = parseUint8(readBits(is, command.ARG_BITS));
                    } else if (16 == command.ARG_BITS) {
                        args[i] = parseInt16(readBits(is, command.ARG_BITS));
                    }
                }

                mCallback.onBytesRead(commandId, args[0], args[1], args[2]);
            }
        } catch (IOException e) {
            Log.d(TAG, e.getLocalizedMessage());
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.d(TAG, e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }
        
        return null;
    }

    /**
     * Reads <code>bytes</code> as a 16 signed bit integer
     */
    private short parseInt16(byte[] bytes) {
        return (short) (((bytes[0] & 0xFF) << 8) | (bytes[1] & 0xFF));
    }

    /**
     * Reads <code>bytes</code> as a 8 unsigned bit integer
     */
    private short parseUint8(byte[] bytes) {
        return (short) (0x000000FF & bytes[0]);
    }

    /**
     * Read bits from input steam of length `bits`
     * @param is The input stream.
     * @param bits The number of bits to read.
     * @return A bytes array with the bits read.
     * @throws IOException When the read fails or the stream ends.
     */
    private byte[] readBits(InputStream is, int bits) throws IOException {
        byte[] buffer = new byte[2];
        int lengthRead = is.read(buffer, 0, bits / 8);
        if (lengthRead == -1) {
            throw new IOException("End of stream");
        }

        return buffer;
    }

    public interface OnBytesCallback {
        void onBytesRead(int commandId, int rValue, int gValue, int bValue);
    }

    public static class SocketAddress {
        private final String host;
        private final int port;

        public SocketAddress(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }
}
