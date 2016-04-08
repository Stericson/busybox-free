package stericson.busybox.jobs.tasks;

import android.content.Context;
import android.provider.DocumentsContract;

import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;

import stericson.busybox.App;
import stericson.busybox.Constants;
import stericson.busybox.R;
import stericson.busybox.Support.Common;
import stericson.busybox.Support.ShellCommand;
import stericson.busybox.jobs.AsyncJob;
import stericson.busybox.jobs.containers.JobResult;

public class InitialChecksTask {

    public static JobResult execute(AsyncJob j) {
        Context context = j.getContext();
        JobResult result = new JobResult();

        //Make sure we support their device
        String arch = App.getInstance().getArch();

        if(!arch.equals(Constants.ARM) && !arch.equals(Constants.X86))
        {
            result.setSuccess(false);
            result.setError(context.getString(R.string.device_unsupported) + " " + arch);
            return result;
        }

        try {
            RootTools.getShell(true);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setError(context.getString(R.string.shell_error));
            return result;
        }

        if (!RootTools.isRootAvailable()) {
            result.setError(context.getString(R.string.noroot2));
        } else {
            try {
                if (!RootTools.isAccessGiven()) {
                    result.setError(context.getString(R.string.noAccess));
                }

                //index 2 should be /su/xbin
                String suXbinLocation = Constants.locations[2];
                //index 3 should be /su/bin
                String subinLocation = Constants.locations[3];

                boolean subinExists = RootTools.exists(subinLocation, true);

                //check for /su/xbin
                if(RootTools.exists(suXbinLocation, true))
                {
                    //Default /su/xbin for systemless root
                    App.getInstance().setSystemlessRoot(true);
                    App.getInstance().setPathPosition(2);
                    App.getInstance().setSpace((float) (Common.getSpace(suXbinLocation) / 1000));
                    App.getInstance().setInstallPath(suXbinLocation);
                }
                else if(subinExists)
                {
                    //Default /su/bin for systemless root if /su/xbin doesn't exist
                    App.getInstance().setSystemlessRoot(true);
                    App.getInstance().setPathPosition(3);
                    App.getInstance().setSpace((float) (Common.getSpace(subinLocation) / 1000));
                    App.getInstance().setInstallPath(subinLocation);
                }
                else
                {
                    App.getInstance().setSpace((float) (Common.getSpace(Constants.locations[0]) / 1000));
                    App.getInstance().setInstallPath(Constants.locations[0]);
                }
            } catch (Exception e) {
                result.setError(context.getString(R.string.accessUndetermined));
            }
        }

        App.getInstance().setInstalled(RootTools.isBusyboxAvailable());

        Common.extractBusybox(context, "");

        return result;

    }
}
