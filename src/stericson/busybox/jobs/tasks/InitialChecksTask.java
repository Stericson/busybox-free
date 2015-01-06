package stericson.busybox.jobs.tasks;

import android.content.Context;

import com.stericson.RootTools.RootTools;

import stericson.busybox.App;
import stericson.busybox.Constants;
import stericson.busybox.R;
import stericson.busybox.Support.Common;
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

                App.getInstance().setSpace((float) (RootTools.getSpace("/system") / 1000));

            } catch (Exception e) {
                result.setError(context.getString(R.string.accessUndetermined));
            }
        }

        App.getInstance().setInstalled(RootTools.isBusyboxAvailable());

        Common.extractBusybox(context, "");

        return result;

    }
}
