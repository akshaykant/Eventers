package com.akshaykant.com.eventers.gcm;

import android.content.Intent;

import com.akshaykant.com.eventers.gcm.RegistrationIntentService;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Akshay Kant on 29-11-2016.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {

        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
    // [END refresh_token]


}


