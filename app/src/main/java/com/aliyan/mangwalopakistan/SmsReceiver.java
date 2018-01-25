package com.aliyan.mangwalopakistan;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * Created by Saad Mujeeb on 19/4/2017.
 */

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            String senderNumber = null;
            Boolean notify = false;

            for (int i = 0; i < pdus.length; i++) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[i]);

                String message = sms.getDisplayMessageBody();
                senderNumber = sms.getOriginatingAddress();

                if (senderNumber.equals("+923354091046") && message.equals("Dear customer your order is on the way!"))
                    notify = true;
                else notify = false;
            }
            if(notify)
                sendNotification(context);
        }
    }

    private void sendNotification(Context context) {
        Intent intent = new Intent(context,LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(context).
                setTicker("Mangwalo Pakistan").
                setContentTitle("Mangwalo Pakistan").
                setContentText("Dear customer your order is on the way!").
                setSmallIcon(R.drawable.cart_white).
                setContentIntent(pendingIntent).getNotification();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
        try {
            Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notificationUri);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

