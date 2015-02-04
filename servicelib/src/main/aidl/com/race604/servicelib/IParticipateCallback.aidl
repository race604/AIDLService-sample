// IParticipateCallback.aidl
package com.race604.servicelib;

interface IParticipateCallback {
    // 用户加入或者离开的回调
    void onParticipate(String name, boolean joinOrLeave);
}
