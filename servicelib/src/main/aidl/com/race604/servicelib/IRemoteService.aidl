// IRemoteService.aidl
package com.race604.servicelib;

interface IRemoteService {
    int someOperate(int a, int b);

    void join(IBinder token, String name);
    void leave(IBinder token);
    List<String> getParticipators();
}

