package stericson.busybox.listeners;

import stericson.busybox.App;
import stericson.busybox.Activity.MainActivity;
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
                               int arg2, long arg3) {
        if (arg2 == 2) {
            final EditText input = new EditText(activity);
            new AlertDialog.Builder(activity)
                    .setTitle("Custom Path")
                    .setMessage("Please enter the Path you want to install Busybox, Success is not gauranteed. \n\n This is an advanced option")
                    .setView(input)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (RootTools.exists(input.getText().toString())) {
                                activity.setCustomPath(input.getText().toString());
                                Toast.makeText(activity, "Custom install path set to " + activity.getCustomPath(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(activity, "That path does not exist or is not valid!", Toast.LENGTH_LONG).show();
                                arg0.setSelection(0);
                                App.getInstance().setPath(arg0.getSelectedItem().toString());
                                App.getInstance().setPathPosition(0);
                                activity.setCustomPath("");
                            }

                            new FindFreeSpaceJob(activity, Location.this, activity.getCustomPath().equals("") ? "/system" : activity.getCustomPath()).start();

                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    arg0.setSelection(0);
                    App.getInstance().setPath(arg0.getSelectedItem().toString());
                    App.getInstance().setPathPosition(0);
                    activity.setCustomPath("");

                    new FindFreeSpaceJob(activity, Location.this, activity.getCustomPath().equals("") ? "/system" : activity.getCustomPath()).start();

                }
            }).show();
        } else {
            activity.setCustomPath("");
            App.getInstance().setPathPosition(0);
            App.getInstance().setPath(arg0.getSelectedItem().toString());
            new FindFreeSpaceJob(activity, Location.this, activity.getCustomPath().equals("") ? "/system" : activity.getCustomPath()).start();
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
