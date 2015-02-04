package com.race604.client;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.race604.servicelib.IParticipateCallback;
import com.race604.servicelib.IRemoteService;

import java.util.List;
import java.util.Random;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private IRemoteService mService;
    private boolean mIsBound = false;
    private boolean mIsJoin = false;
    private boolean mIsRegistered = false;

    private IBinder mToken = new Binder();
    private Random mRand = new Random();

    private Button mJoinBtn;
    private Button mRegisterBtn;

    private ListView mList;

    private ArrayAdapter<String> mAdapter;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(MainActivity.this, "Service connected", Toast.LENGTH_SHORT).show();

            mService = IRemoteService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(MainActivity.this, "Service disconnected", Toast.LENGTH_SHORT).show();

            mService = null;
        }
    };

    private IParticipateCallback mParticipateCallback = new IParticipateCallback.Stub() {

        @Override
        public void onParticipate(String name, boolean joinOrLeave) throws RemoteException {
            if (joinOrLeave) {
                mAdapter.add(name);
            } else {
                mAdapter.remove(name);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.bind).setOnClickListener(this);
        findViewById(R.id.unbind).setOnClickListener(this);
        findViewById(R.id.call).setOnClickListener(this);
        findViewById(R.id.get_participators).setOnClickListener(this);


        mList = (ListView) findViewById(R.id.list);
        mJoinBtn = (Button) findViewById(R.id.join);
        mJoinBtn.setOnClickListener(this);

        mRegisterBtn = (Button) findViewById(R.id.register_callback);
        mRegisterBtn.setOnClickListener(this);

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        mList.setAdapter(mAdapter);
    }

    private void callRemote() {

        if (isServiceReady()) {
            try {
                int result = mService.someOperate(1, 2);
                Toast.makeText(this, "Remote call return: " + result, Toast.LENGTH_SHORT).show();
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isServiceReady() {
        if (mService != null) {
            return true;
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsBound) {
            unbindService(mServiceConnection);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bind:
                Intent intent = new Intent(IRemoteService.class.getName());
                intent.setClassName("com.race604.remoteservice", "com.race604.remoteservice.RemoteService");
                //intent.setPackage("com.race604.remoteservice");
                bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
                mIsBound = true;
                break;
            case R.id.unbind:
                if (mIsBound) {
                    unbindService(mServiceConnection);
                    mIsBound = false;
                }
                break;
            case R.id.call:
                callRemote();
                break;
            case R.id.join:
                toggleJoin();
                break;
            case R.id.get_participators:
                updateParticipators();
                break;
            case R.id.register_callback:
                toggleRegisterCallback();
                break;
        }
    }

    private void toggleRegisterCallback() {
        if (!isServiceReady()) {
            return;
        }

        try {
            if (mIsRegistered) {
                mService.unregisterParticipateCallback(mParticipateCallback);
                mRegisterBtn.setText(R.string.register);
                mIsRegistered = false;
            } else {
                mService.registerParticipateCallback(mParticipateCallback);
                mRegisterBtn.setText(R.string.unregister);
                mIsRegistered = true;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void updateParticipators() {
        if (!isServiceReady()) {
            return;
        }

        try {
            List<String> participators = mService.getParticipators();
            mAdapter.clear();
            mAdapter.addAll(participators);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void toggleJoin() {
        if (!isServiceReady()) {
            return;
        }

        try {
            if (!mIsJoin) {
                String name = "Client:" + mRand.nextInt(10);
                mService.join(mToken, name);
                mJoinBtn.setText(R.string.leave);
                mIsJoin = true;
            } else {
                mService.leave(mToken);
                mJoinBtn.setText(R.string.join);
                mIsJoin = false;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
