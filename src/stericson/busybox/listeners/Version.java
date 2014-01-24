package stericson.busybox.listeners;

import stericson.busybox.App;
import stericson.busybox.R;
import stericson.busybox.Activity.MainActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class Version implements OnItemSelectedListener {

    private MainActivity activity = null;

    public Version(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                               long arg3) {
        if (arg0.getSelectedItem().toString().toLowerCase().contains("custom")) {
            this.activity.initiatePopupWindow(activity.getString(R.string.proonly_custom), false, activity);
            arg0.setSelection(0);
        } else
            App.getInstance().setVersion(arg0.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

}
