package com.race604.remoteservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.race604.servicelib.IRemoteService;

import java.util.ArrayList;
import java.util.List;

public class RemoteService extends Service {

    private static final String TAG = RemoteService.class.getSimpleName();

    private List<Client> mClients = new ArrayList<>();

    private final IRemoteService.Stub mBinder = new IRemoteService.Stub() {
        @Override
        public int someOperate(int a, int b) throws RemoteException {
            Log.d(TAG, "called RemoteService someOperate()");
            return a + b;
        }

        @Override
        public void join(IBinder token, String name) throws RemoteException {
            int idx = findClient(token);
            if (idx >= 0) {
                Log.d(TAG, "already joined");
                return;
            }

            Client client = new Client(token, name);

            // 注册客户端死掉的通知
            token.linkToDeath(client, 0);
            mClients.add(client);
        }

        @Override
        public void leave(IBinder token) throws RemoteException {
            int idx = findClient(token);
            if (idx < 0) {
                Log.d(TAG, "already left");
                return;
            }

            Client client = mClients.get(idx);
            mClients.remove(client);

            // 取消注册
            client.mToken.unlinkToDeath(client, 0);
        }

        @Override
        public List<String> getParticipators() throws RemoteException {
            ArrayList<String> names = new ArrayList<>();
            for (Client client : mClients) {
                names.add(client.mName);
            }
            return names;
        }
    };

    public RemoteService() {
    }

    private int findClient(IBinder token) {
        for (int i = 0; i < mClients.size(); i++) {
            if (mClients.get(i).mToken == token) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final class Client implements IBinder.DeathRecipient {
        public final IBinder mToken;
        public final String mName;

        public Client(IBinder token, String name) {
            mToken = token;
            mName = name;
        }

        @Override
        public void binderDied() {
            // 客户端死掉，执行此回调
            int index = mClients.indexOf(this);
            if (index < 0) {
                return;
            }

            Log.d(TAG, "client died: " + mName);
            mClients.remove(this);
        }
    }
}
