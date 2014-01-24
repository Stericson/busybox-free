package stericson.busybox.jobs;

import android.app.Activity;

import stericson.busybox.R;
import stericson.busybox.interfaces.JobCallback;
import stericson.busybox.jobs.containers.JobResult;
import stericson.busybox.jobs.tasks.FindBBLocationsTask;

public class FindBBLocationsJob extends AsyncJob {
    public static final int FIND_BB_LOCATIONS_JOB = 158465;

    private JobCallback cb;
    private boolean single;

    public FindBBLocationsJob(Activity activity, JobCallback cb, boolean single) {
        super(activity, R.string.initialChecks, false, false);
        this.cb = cb;
        this.single = single;
    }

    @Override
    JobResult handle() {
        return FindBBLocationsTask.execute(this, single);
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);

    }

    @Override
    void callback(JobResult result) {
        cb.jobFinished(result, FIND_BB_LOCATIONS_JOB);
    }
}
