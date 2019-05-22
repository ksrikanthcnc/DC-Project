package com.dcproject.nodues;

import android.app.Application;

/**
 * Created by SAI on 26-03-2018.
 */

public class ConnectivityListener extends Application {

    private static ConnectivityListener mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized ConnectivityListener getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
