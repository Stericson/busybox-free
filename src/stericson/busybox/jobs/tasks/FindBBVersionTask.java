package stericson.busybox.jobs.tasks;

import android.content.Context;

import com.stericson.RootTools.RootTools;

import stericson.busybox.App;
import stericson.busybox.R;
import stericson.busybox.jobs.AsyncJob;
import stericson.busybox.jobs.containers.JobResult;

public class FindBBVersionTask {

    public static JobResult execute(AsyncJob j) {
        Context context = j.getContext();
        JobResult result = new JobResult();

        try {
            RootTools.getShell(true);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setError(context.getString(R.string.shell_error));
            return result;
        }

        result.setMessage(RootTools.getBusyBoxVersion());

        App.getInstance().setCurrentVersion(result.getMessage());

        result.setSuccess(true);
        return result;
    }
}
