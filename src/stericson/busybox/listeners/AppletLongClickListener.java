package stericson.busybox.listeners;

import java.util.ArrayList;
import java.util.List;

import com.stericson.RootShell.execution.Shell;
import stericson.busybox.R;
import stericson.busybox.activity.BaseActivity;
import stericson.busybox.support.CommandResult;
import stericson.busybox.support.ShellCommand;
import stericson.busybox.interfaces.CommandCallback;

import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.CheckBox;
import android.widget.Toast;

import com.stericson.RootTools.RootTools;

public class AppletLongClickListener implements OnLongClickListener, CommandCallback {

    BaseActivity activity;
    List<String> result = new ArrayList<String>();


    public AppletLongClickListener(BaseActivity activity) {
        this.activity = activity;
    }

    public boolean onLongClick(View v) {
        CheckBox applet = (CheckBox) v.findViewById(R.id.appletCheck);

        if (applet != null && RootTools.isAppletAvailable(applet.getText().toString())) {
            try {

                ShellCommand command = new ShellCommand(this, 0, "busybox " + applet.getText().toString() + " --help");
                Shell.startRootShell().add(command);
                command.pause();

                String appletInfo = "";

                for (String info : result) {
                    if (!info.equals("1") && !info.contains("multi-call binary") && !info.trim().equals("")) {
                        appletInfo += info + "\n";
                    }
                }

                activity.initiatePopupWindow(appletInfo, false, activity);
            } catch (Exception e) {
                Toast.makeText(activity, R.string.noInfo, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(activity, R.string.noInfo, Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void commandCallback(CommandResult result) {

    }

    @Override
    public void commandOutput(int id, String line) {
        result.add(line);
    }
}
