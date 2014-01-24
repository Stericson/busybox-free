package stericson.busybox.jobs;

import android.app.Activity;

import stericson.busybox.R;
import stericson.busybox.interfaces.JobCallback;
import stericson.busybox.jobs.containers.JobResult;
import stericson.busybox.jobs.tasks.FindFreeSpaceTask;

public class FindFreeSpaceJob extends AsyncJob {
    public static final int FIND_FREE_SPACE_JOB = 32548;

    private String location;
    private JobCallback cb;

    public FindFreeSpaceJob(Activity activity, JobCallback cb, String location) {
        super(activity, R.string.initialChecks, false, false);
        this.location = location;
        this.cb = cb;
    }

    @Override
    JobResult handle() {
        return FindFreeSpaceTask.execute(this, location);
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
    }

    @Override
    void callback(JobResult result) {
        cb.jobFinished(result, FIND_FREE_SPACE_JOB);
    }
}
