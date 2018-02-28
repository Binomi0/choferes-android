package com.contenedoressatur.android.choferesandroid.notificaciones;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Creado por user el 23/02/2018.
 * <p>
 * Todos los derechos reservados.
 *
 * #adolfoonrubia adolfo.onrubia.es
 */

public class FbInstanceService extends FirebaseInstanceIdService {
    private String TAG = "FirebaseInstanceIdService";


    @Override
    public void onTokenRefresh() {
//        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }


    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        Log.i(TAG, token);
    }

}
