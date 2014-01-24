package stericson.busybox.jobs;

import android.app.Activity;

import stericson.busybox.R;
import stericson.busybox.interfaces.JobCallback;
import stericson.busybox.jobs.containers.JobResult;
import stericson.busybox.jobs.tasks.UninstallTask;

public class UninstallJob extends AsyncJob {
    public static final int UNINSTALL_JOB = 5492;

    private JobCallback cb;

    public UninstallJob(Activity activity, JobCallback cb) {
        super(activity, R.string.uninstalling, true, false);
        this.cb = cb;
    }

    @Override
    JobResult handle() {
        return new UninstallTask().execute(this);
    }

    public void publishCurrentProgress(Object... values) {
        this.publishProgress(values);
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);

        cb.jobProgress(values[0], UNINSTALL_JOB);
    }

    @Override
    void callback(JobResult result) {
        cb.jobFinished(result, UNINSTALL_JOB);
    }
}
