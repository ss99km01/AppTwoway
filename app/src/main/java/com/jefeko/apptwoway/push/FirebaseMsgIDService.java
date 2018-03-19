package com.jefeko.apptwoway.push;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.jefeko.apptwoway.ServiceCommon;
import com.jefeko.apptwoway.utils.LogUtils;


public class FirebaseMsgIDService extends FirebaseInstanceIdService {

    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        ServiceCommon.PUSH_TOKEN = token;
        LogUtils.e("Refreshed token: " + token);
    }
}
