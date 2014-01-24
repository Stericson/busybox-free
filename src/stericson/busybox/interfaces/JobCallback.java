package stericson.busybox.interfaces;


import stericson.busybox.jobs.containers.JobResult;

public interface JobCallback {

    public void jobFinished(JobResult result, int id);

    public void jobProgress(Object value, int id);
}
