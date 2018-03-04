package com.contenedoressatur.android.choferesandroid.notificaciones;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.contenedoressatur.android.choferesandroid.LoginActivity;
import com.contenedoressatur.android.choferesandroid.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Creado por user el 23/02/2018.
 * <p>
 * Todos los derechos reservados.
 *
 * @adolfoonrubia adolfo.onrubia.es
 */

public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = MessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String image = remoteMessage.getNotification().getIcon();
        if (image == null) {
            image = "default";
        }
        String title = remoteMessage.getNotification().getTitle();
        String text = remoteMessage.getNotification().getBody();
        String sound = remoteMessage.getNotification().getSound();

        int id = 0;
        Object obj = remoteMessage.getData().get("id");
        if (obj != null) {
            id = Integer.valueOf(obj.toString());
            title += String.valueOf(remoteMessage.getData().get("pedido"));
            text += remoteMessage.getData().get("metros") + "m3";
        }

        this.sendNotification(new NotificationData(image, id, title, text, sound));

        if(remoteMessage.getData().size() > 0) {
            Log.i(TAG, "Payload del mensaje : " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
            } else {
                // Handle message within 10 seconds
//                handleNow();
            }

            if(remoteMessage.getNotification() != null) {
                Log.i(TAG,"Notificacion: " + remoteMessage.getNotification().getTitle());
            }
        }
    }

    @TargetApi(26)
    private void createChannel(NotificationManager notificationManager) {
        String name = "FileDownload";
        String description = "Notifications for download status";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel mChannel = new NotificationChannel(name, name, importance);
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        notificationManager.createNotificationChannel(mChannel);
    }

    private void sendNotification(NotificationData notificationData) {

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(NotificationData.TEXT, notificationData.getTextMessage());

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);


        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = null;
        int icon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.drawable.truck_trailer: R.mipmap.satur;
        Bitmap logo = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.satur));
        try {

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(icon)
                    .setLargeIcon(logo)
                    .setContentTitle(notificationData.getTitle())
                    .setContentText(notificationData.getTextMessage())
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                    .setLights(Color.RED, 3000, 3000)
                    .setContentIntent(pendingIntent);



        } catch (UnsupportedOperationException e) {
            e.fillInStackTrace();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (notificationBuilder != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createChannel(notificationManager);
                notificationBuilder = new NotificationCompat.Builder(this, "PEDIDOS").setSmallIcon(android.R.drawable.stat_sys_download).setColor
                        (ContextCompat.getColor(this, R.color.colorPrimary)).setContentTitle("Titulo Notificacion").setContentText("Descripcion y contenido de la notificación");
                notificationManager.notify(notificationData.getId(), notificationBuilder.build());

//                notificationManager.notify(notificationData.getId(), notificationBuilder.build());
            }
        } else {
            Log.i(TAG, "No se ha podido enviar la notificación");
        }
    }

    public MessagingService() {
        super();
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }
}
