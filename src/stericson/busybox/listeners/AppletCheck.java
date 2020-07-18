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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import com.stericson.RootTools.RootTools;

public class AppletCheck implements OnItemSelectedListener, CommandCallback {

    BaseActivity activity;
    List<String> result = new ArrayList<String>();


    public AppletCheck(BaseActivity activity) {
        this.activity = activity;
    }

    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                               long arg3) {

        if (arg2 == 0 || arg2 == 1) {
            if (arg2 == 1) {
                activity.initiatePopupWindow(activity.getString(R.string.whatisthis), false, activity);
            }
        } else {
            String applet = arg0.getAdapter().getItem(arg2).toString();

            String appletInfo = "";

            List<String> foundPaths = RootTools.findBinary(applet, true);

            if (foundPaths.size() > 0) {
                appletInfo += activity.getString(R.string.foundhere) + " " + foundPaths.get(0);
                String symlink = RootTools.getSymlink(foundPaths.get(0) + "/" + applet);
                if (symlink.equals("")) {
                    appletInfo += "\n" + activity.getString(R.string.notsymlinked);
                } else {
                    appletInfo += "\n" + activity.getString(R.string.symlinkedTo) + " " + symlink + "\n\n";
                }
            } else {
                appletInfo += activity.getString(R.string.notFound);
            }

            if (RootTools.isAppletAvailable(applet)) {
                try {

                    ShellCommand command = new ShellCommand(this, 0, "busybox " + applet + " --help");
                    Shell.startRootShell().add(command);
                    command.pause();

                    for (String info : result) {
                        if (!info.equals("1")) {
                            appletInfo += info + "\n";
                        }
                    }

                    activity.initiatePopupWindow(appletInfo, false, activity);
                } catch (Exception e) {
                    appletInfo += "\n" + activity.getString(R.string.noInfo);
                }
            } else {
                appletInfo += "\n" + activity.getString(R.string.noInfo);
            }
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void commandCallback(CommandResult result) {

    }

    @Override
    public void commandOutput(int id, String line) {
        result.add(line);
    }
}
