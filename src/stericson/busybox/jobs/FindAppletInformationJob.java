package stericson.busybox.jobs;

import stericson.busybox.Constants;
import stericson.busybox.R;
import stericson.busybox.activity.MainActivity;
import stericson.busybox.interfaces.JobCallback;
import stericson.busybox.jobs.containers.JobResult;
import stericson.busybox.jobs.tasks.FindAppletInformationTask;

import android.widget.TextView;

public class FindAppletInformationJob extends AsyncJob {
    public static final int APPLET_INFO = 16544;

    protected TextView view;
    private boolean silent;
    private JobCallback jcb;

    public FindAppletInformationJob(MainActivity activity, JobCallback cb, boolean silent) {
        super(activity, R.string.gathering, false, false);
        this.silent = silent;
        this.jcb = cb;
    }

    @Override
    JobResult handle() {
        return new FindAppletInformationTask().execute(this, silent, Constants.appletsString);
    }

    public void publishCurrentProgress(Object... values) {
        this.publishProgress(values);
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);

        jcb.jobProgress(values[0], APPLET_INFO);
    }

    @Override
    void callback(JobResult result) {
        if (!silent)
            jcb.jobFinished(result, APPLET_INFO);
    }
}
