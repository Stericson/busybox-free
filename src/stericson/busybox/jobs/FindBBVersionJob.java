package stericson.busybox.jobs;

import android.app.Activity;

import stericson.busybox.R;
import stericson.busybox.interfaces.JobCallback;
import stericson.busybox.jobs.containers.JobResult;
import stericson.busybox.jobs.tasks.FindBBVersionTask;

public class FindBBVersionJob extends AsyncJob {
    public static final int FIND_BB_VERSION_JOB = 254865;

    private JobCallback cb;

    public FindBBVersionJob(Activity activity, JobCallback cb) {
        super(activity, R.string.initialChecks, false, false);
        this.cb = cb;
    }

    @Override
    JobResult handle() {
        return FindBBVersionTask.execute(this);
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);

    }

    @Override
    void callback(JobResult result) {
        cb.jobFinished(result, FIND_BB_VERSION_JOB);
    }
}
