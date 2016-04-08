package stericson.busybox.listeners;

import stericson.busybox.App;
import stericson.busybox.Activity.MainActivity;
import stericson.busybox.Constants;
import stericson.busybox.R;
import stericson.busybox.interfaces.JobCallback;
import stericson.busybox.jobs.FindFreeSpaceJob;
import stericson.busybox.jobs.containers.JobResult;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Toast;

import com.stericson.RootTools.RootTools;

public class Location implements OnItemSelectedListener, JobCallback {

    private MainActivity activity;

    public Location(MainActivity activity) {
        this.activity = activity;
    }

    public void onItemSelected(final AdapterView<?> arg0, View arg1,
                               int position, long arg3) {

        int selection = 0;

        // /su/xbin and /su/bin
        if((position == 2 || position == 3) && !App.getInstance().isSystemlessRoot())
        {
            App.getInstance().setCustomInstallPath(false);
            Toast.makeText(activity, activity.getString(R.string.systemless_root_not_configured), Toast.LENGTH_LONG).show();
            arg0.setSelection(selection);
            App.getInstance().setInstallPath(arg0.getSelectedItem().toString());
            App.getInstance().setPathPosition(selection);
        }
        else if(position == 2 && !RootTools.exists(Constants.locations[2], true))
        {
            App.getInstance().setCustomInstallPath(false);
            Toast.makeText(activity, activity.getString(R.string.systemless_root_xbin_not_configured), Toast.LENGTH_LONG).show();

            if(App.getInstance().isSystemlessRoot())
            {
                // /su/bin
                selection = 3;
            }

            arg0.setSelection(selection);
            App.getInstance().setInstallPath(arg0.getSelectedItem().toString());
            App.getInstance().setPathPosition(selection);
        }
        //position 4 is custom
        else if (position == 4) {
            final EditText input = new EditText(activity);
            new AlertDialog.Builder(activity)
                    .setTitle("Custom Path")
                    .setMessage("Please enter the Path you want to install Busybox, Success is not gauranteed. \n\n This is an advanced option")
                    .setView(input)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (RootTools.exists(input.getText().toString(), true)) {
                                App.getInstance().setCustomInstallPath(true);
                                App.getInstance().setInstallPath(input.getText().toString());
                                Toast.makeText(activity, "Custom install path set to " + App.getInstance().getInstallPath(), Toast.LENGTH_LONG).show();
                            } else {
                                App.getInstance().setCustomInstallPath(false);
                                Toast.makeText(activity, "That path does not exist or is not valid!", Toast.LENGTH_LONG).show();
                                arg0.setSelection(0);
                                App.getInstance().setInstallPath(arg0.getSelectedItem().toString());
                                App.getInstance().setPathPosition(0);
                            }

                            new FindFreeSpaceJob(activity, Location.this, App.getInstance().isCustomInstallPath() ? "/system" : App.getInstance().getInstallPath()).start();

                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int whichButton)
                {
                    arg0.setSelection(0);
                    App.getInstance().setInstallPath(arg0.getSelectedItem().toString());
                    App.getInstance().setPathPosition(0);

                    new FindFreeSpaceJob(activity, Location.this, App.getInstance().isCustomInstallPath() ? "/system" : App.getInstance().getInstallPath()).start();

                }
            }).show();
        }
        else {
            App.getInstance().setCustomInstallPath(false);
            App.getInstance().setPathPosition(position);
            App.getInstance().setInstallPath(arg0.getSelectedItem().toString());

            new FindFreeSpaceJob(activity, Location.this, App.getInstance().getInstallPath()).start();
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {
    }

    public void jobFinished(JobResult result, int id) {
        activity.updateList();
    }

    @Override
    public void jobProgress(Object value, int id) {

    }

}
