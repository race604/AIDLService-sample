package com.race604.remoteservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.race604.servicelib.IRemoteService;

public class RemoteService extends Service {

    private static final String TAG = RemoteService.class.getSimpleName();

    private final IRemoteService.Stub mBinder = new IRemoteService.Stub() {
        @Override
        public int someOperate(int a, int b) throws RemoteException {
            Log.d(TAG, "called RemoteService someOperate()");
            return a + b;
        }
    };

    public RemoteService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


}
