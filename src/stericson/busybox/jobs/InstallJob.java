package stericson.busybox.jobs;

import android.app.Activity;

import stericson.busybox.R;
import stericson.busybox.interfaces.JobCallback;
import stericson.busybox.jobs.containers.JobResult;
import stericson.busybox.jobs.tasks.InstallTask;

public class InstallJob extends AsyncJob {
    public static final int INSTALL_JOB = 1253;

    private String path;
    private String version;
    private JobCallback cb;

    public InstallJob(Activity activity, JobCallback cb, String path, String version) {
        super(activity, R.string.installing, true, false);
        this.path = path;
        this.cb = cb;
        this.version = version;
    }

    @Override
    JobResult handle() {
        return new InstallTask().execute(this, path, version, false, false);
    }

    public void publishCurrentProgress(Object... values) {
        this.publishProgress(values);
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
        cb.jobProgress(values[0], INSTALL_JOB);
    }

    @Override
    void callback(JobResult result) {
        cb.jobFinished(result, INSTALL_JOB);
    }
}