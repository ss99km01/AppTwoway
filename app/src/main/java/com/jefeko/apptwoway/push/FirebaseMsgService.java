package com.jefeko.apptwoway.push;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.app.Notification;
import android.app.NotificationManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jefeko.apptwoway.R;
import com.jefeko.apptwoway.ui.MainActivity;
import com.jefeko.apptwoway.ui.waytalk.WayTalkActivity;
import com.jefeko.apptwoway.utils.LogUtils;

import java.io.IOException;

public class FirebaseMsgService extends FirebaseMessagingService {
    public static final int RESULT_CODE_PUSH = 1400;
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        LogUtils.e("From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            LogUtils.e("Message data payload: " + remoteMessage.getData());
            sendNotification(remoteMessage.getData().get("message"));
        }

        if (remoteMessage.getNotification() != null) {
            LogUtils.e("Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private void sendNotification(String messageBody) {
        String[] msg = messageBody.split("\\|");
        LogUtils.e("messageBody = " + messageBody);

        String text = "";

        Intent notifyIntent = null;

        LogUtils.d("0 = "+msg[0]);
        LogUtils.d("1 = "+msg[1]);

        if("suju".equals(msg[0])) {
            if("input".equals(msg[1])) {
                text = "신규 수주 내역이 있습니다.";
            }else{
                text = "수주 내역이 취소 되었습니다.";
            }
            notifyIntent = new Intent(this, MainActivity.class);
        }else if("balju".equals(msg[0])){
            if("input".equals(msg[1])) {
                text = "신규 발주 내역이 있습니다.";
            }else{
                text = "발주 내역이 취소 되었습니다.";
            }
            notifyIntent = new Intent(this, MainActivity.class);
        }else if("msg".equals(msg[0])){
            text = "신규 메세지가 있습니다.";
            notifyIntent = new Intent(this, WayTalkActivity.class);
            notifyIntent.putExtra("page","waytalkmms");
        }
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivities(this, RESULT_CODE_PUSH, new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.icon)
                    .setContentTitle("알림")
                    .setContentText(text)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();
        }

        notification.defaults |= Notification.DEFAULT_SOUND;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}
