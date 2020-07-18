package stericson.busybox.receivers;

import stericson.busybox.Constants;
import android.content.Context;
import android.content.Intent;

public class OnUpgradeReceiver extends BaseReceiver {

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

            showNotification(context, "Update!", ticker);
        }
    }
}
