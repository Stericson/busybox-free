package stericson.busybox.receivers;

import stericson.busybox.Constants;
import stericson.busybox.R;
import stericson.busybox.Activity.MainActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnUpgradeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Constants.updateType != 0 && intent.getDataString().contains("stericson.busybox") && !intent.getDataString().contains("stericson.busybox.donate")) {
            String ticker = "";

            switch (Constants.updateType) {
                case 1:
                    ticker = "Update available for BusyBox binary!";
                    break;
                case 2:
                    ticker = "New BusyBox binary available!";
                    break;
                case 3:
                    ticker = "New binary and updates available!";
                    break;
            }

            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);

            Notification notification = new Notification();
            notification.icon = R.drawable.notif;
            notification.when = System.currentTimeMillis();

            Intent notificationIntent = new Intent(context, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            notification.contentIntent = contentIntent;

            notification.flags |= Notification.FLAG_AUTO_CANCEL;

            notification.setLatestEventInfo(context, "Update!",
                    ticker, contentIntent);

            mNotificationManager.notify(1, notification);
        }
    }

}
