// IRemoteService.aidl
package com.race604.servicelib;

import com.race604.servicelib.IParticipateCallback;

interface IRemoteService {
    int someOperate(int a, int b);

    void join(IBinder token, String name);
    void leave(IBinder token);
    List<String> getParticipators();

    void registerParticipateCallback(IParticipateCallback cb);
    void unregisterParticipateCallback(IParticipateCallback cb);
}

