package stericson.busybox.receivers;

import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import stericson.busybox.Activity.MainActivity;
import stericson.busybox.R;

public class OnBootReciever extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

//        String ticker = "Upgrade to Busybox pro!";
//
//        String ns = Context.NOTIFICATION_SERVICE;
//        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
//
//        Notification notification = new Notification();
//        notification.icon = R.drawable.notif;
//        notification.when = System.currentTimeMillis();
//
//        Intent notificationIntent = new Intent(context, MainActivity.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
//
//        notification.contentIntent = contentIntent;
//
//        notification.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_NO_CLEAR;
//
//        notification.setLatestEventInfo(context, "Busybox!",
//                ticker, contentIntent);
//
//        mNotificationManager.notify(1, notification);
    }
}
