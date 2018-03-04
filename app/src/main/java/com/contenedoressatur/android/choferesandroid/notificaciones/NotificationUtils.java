package com.contenedoressatur.android.choferesandroid.notificaciones;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompatSideChannelService;

import com.contenedoressatur.android.choferesandroid.LoginActivity;
import com.contenedoressatur.android.choferesandroid.R;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Creado por user el 04/03/2018.
 * <p>
 * Todos los derechos reservados.
 *
 * @adolfoonrubia adolfo.onrubia.es
 */

public class NotificationUtils extends ContextWrapper {

    private NotificationManager mManager;
    public static final String PEDIDOS_CHANNEL_ID = "com.contenedoressatur.android.PEDIDOS";
    public static final String PEDIDOS_CHANNEL_NAME = "CANAL PEDIDOS";

    public NotificationUtils(Context base) {
        super(base);
        createChannels();
    }

    public void createChannels() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel pedidosChannel = new NotificationChannel(PEDIDOS_CHANNEL_ID, PEDIDOS_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            pedidosChannel.enableLights(true);
            pedidosChannel.enableVibration(true);
            pedidosChannel.setLightColor(Color.GREEN);
            pedidosChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            pedidosChannel.canBypassDnd();
            pedidosChannel.canShowBadge();
            pedidosChannel.setShowBadge(true);
            getManager().createNotificationChannel(pedidosChannel);
        }
    }
    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }



    public Notification.Builder getPedidosChannelNotification(String title, String body) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Notification.Action action = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            action = new Notification.Action(R.drawable.satur, "Ver Pedido", pendingIntent);
            try {
                action.getAllowGeneratedReplies();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new Notification.Builder(getApplicationContext(), PEDIDOS_CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.satur)
                    .addAction(action)
                    .setChronometerCountDown(true)
                    .setNumber(1)
                    .setAutoCancel(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new Notification.Builder(getApplicationContext(), PEDIDOS_CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.satur)
                    .setAutoCancel(true);
        } else {
            return null;
        }
    }


}
