package stericson.busybox.jobs;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stericson.busybox.App;
import stericson.busybox.R;
import stericson.busybox.jobs.containers.JobResult;

public abstract class AsyncJob extends AsyncTask<Void, Object, JobResult> {
    protected Activity context = null;
    protected int stringId = -1;
    protected boolean showDialog = true;
    protected PopupWindow popupWindow;
    protected boolean isDialogDismissable;
    protected JobResult result;

    @SuppressWarnings("rawtypes")
    private static Map<Context, List<AsyncJob>> contextJobs = new HashMap<Context, List<AsyncJob>>();

    public AsyncJob(Activity context, int stringId) {
        this(context, stringId, true);
    }

    public AsyncJob(Activity context, int stringId, boolean showDialog) {
        this(context, stringId, showDialog, true);
    }

    public AsyncJob(Activity context, int stringId, boolean showDialog, boolean isDialogDismissable) {
        this.context = context;
        this.stringId = stringId;
        this.showDialog = showDialog;
        this.isDialogDismissable = isDialogDismissable;
        add(this);
    }

    abstract JobResult handle();

    abstract void callback(JobResult result);

    public Context getContext() {
        return context;
    }

    @Override
    protected JobResult doInBackground(Void... params) {
        return handle();
    }

    @Override
    protected void onPreExecute() {
        // Show spinner.
        if (showDialog) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.popupwindow_spinner, null);
            App.getInstance().setPopupView(layout);

            popupWindow = new PopupWindow(layout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

            context.findViewById(R.id.main).post(new Runnable() {
                public void run() {
                    try {
                        popupWindow.showAtLocation(context.findViewById(R.id.main), Gravity.CENTER, 0, 0);
                    } catch (Exception e) {
                        //do nothing
                    }
                }
            });

            TextView textView = (TextView) layout.findViewById(R.id.header);
            textView.setText(context.getString(stringId));
        }
    }

    @Override
    protected void onPostExecute(JobResult result) {
        if (!isCancelled()) {
            if (popupWindow != null) {
                context.findViewById(R.id.main).post(new Runnable() {
                    public void run() {
                        try {
                            popupWindow.dismiss();
                        } catch (Exception e) {
                        }
                    }
                });
            }
        }
        remove(this);

        callback(result);
    }

    @SuppressWarnings("rawtypes")
    private void add(AsyncJob job) {
        List<AsyncJob> jobs = contextJobs.get(context);
        if (jobs == null) {
            jobs = new ArrayList<AsyncJob>();
            contextJobs.put(context, jobs);
        }
        jobs.add(job);
    }

    @SuppressWarnings("rawtypes")
    private void remove(AsyncJob job) {
        List<AsyncJob> jobs = contextJobs.get(context);
        if (jobs != null) {
            jobs.remove(job);
        }
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    public boolean isDialogDismissable() {
        return isDialogDismissable;
    }

    @SuppressWarnings("rawtypes")
    public static void cancelAllJobs(Context context) {
        List<AsyncJob> jobs = contextJobs.get(context);
        if (jobs != null) {
            for (AsyncJob job : jobs) {
                job.cancel(true);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public static boolean isShowingDialog(Context context) {
        List<AsyncJob> jobs = contextJobs.get(context);
        if (jobs != null) {
            for (AsyncJob job : jobs) {
                PopupWindow popupWindow = job.getPopupWindow();
                if (popupWindow != null && popupWindow.isShowing())
                    return true;
            }
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    public static boolean isDismissableDialog(Context context) {
        List<AsyncJob> jobs = contextJobs.get(context);
        if (jobs != null) {
            for (AsyncJob job : jobs) {
                PopupWindow popupWindow = job.getPopupWindow();
                if (popupWindow != null && popupWindow.isShowing() && !job.isDialogDismissable())
                    return false;
            }
        }
        return true;
    }

    @SuppressWarnings("rawtypes")
    public static void dismissDialog(Context context) {
        List<AsyncJob> jobs = contextJobs.get(context);
        if (jobs != null) {
            for (AsyncJob job : jobs) {
                PopupWindow popupWindow = job.getPopupWindow();
                if (popupWindow != null && popupWindow.isShowing())
                    popupWindow.dismiss();
            }
        }
    }

    public void publishCurrentProgress(Object... values) {
        this.publishProgress(values);
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
    }

    public void start()
    {
        if(android.os.Build.VERSION.SDK_INT > 10)
        {
            this.executeOnExecutor(THREAD_POOL_EXECUTOR);
        }
        else
        {
            super.execute();
        }
    }
}
