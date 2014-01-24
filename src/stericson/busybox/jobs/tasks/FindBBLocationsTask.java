package stericson.busybox.jobs.tasks;

import android.content.Context;

import com.stericson.RootTools.RootTools;

import stericson.busybox.R;
import stericson.busybox.Support.Common;
import stericson.busybox.jobs.AsyncJob;
import stericson.busybox.jobs.containers.JobResult;

public class FindBBLocationsTask {

    public static JobResult execute(AsyncJob j, boolean single) {
        Context context = j.getContext();
        JobResult result = new JobResult();

        try {
            RootTools.getShell(true);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setError(context.getString(R.string.shell_error));
            return result;
        }

        result.setLocations(Common.findBusyBoxLocations(false, single));

        result.setSuccess(true);
        return result;

    }
}
