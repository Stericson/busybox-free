package stericson.busybox.listeners;

import stericson.busybox.Activity.MainActivity;
import stericson.busybox.App;
import stericson.busybox.R;
import stericson.busybox.adapter.PageAdapter;
import stericson.busybox.interfaces.JobCallback;
import stericson.busybox.jobs.FindBBLocationsJob;
import stericson.busybox.jobs.FindBBVersionJob;
import stericson.busybox.jobs.FindFreeSpaceJob;
import stericson.busybox.jobs.containers.JobResult;

import android.support.v4.view.ViewPager.OnPageChangeListener;

public class PageChange implements OnPageChangeListener, JobCallback {

    private MainActivity context;

    public PageChange(MainActivity context) {
        this.context = context;
    }

    public void onPageScrollStateChanged(int arg0) {

    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    public void onPageSelected(int position) {
        context.position = position;
        if (position == 0) {
            context.initiatePopupWindow("This feature allows you to install, uninstall, or reinstall the applets listed below on an individual basis. \n\n This feature will also tell you a little information about the applet and whether or not it is currently installed or symlinked. \n\n This feature is useful if you are having a problem with a specific applet not functioning correctly. \n\n To access this feature, please long press on an applet.", false, context);
        } else if (position == 1) {
            new FindBBVersionJob(context, this).start();
        }
    }

    @Override
    public void jobFinished(JobResult result, int id) {
        if (id == FindBBLocationsJob.FIND_BB_LOCATIONS_JOB) {
            if (result.getLocations().length > 0) {
                App.getInstance().setFound(result.getLocations().length > 1 ? context.getString(R.string.morethanone) : context.getString(R.string.busybox) + " " + App.getInstance().getCurrentVersion() + " " + context.getString(R.string.foundit) + "\n\n" + context.getString(R.string.installedto) + " " + result.getLocations()[0]);
            } else {
                App.getInstance().setFound("Busybox is not installed.");
            }

            context.updateList();
            PageAdapter.updateBusyboxInformation();
        } else if (id == FindBBVersionJob.FIND_BB_VERSION_JOB) {
            new FindBBLocationsJob(context, this, true).start();
            PageAdapter.updateBusyboxInformation();
        } else if (id == FindFreeSpaceJob.FIND_FREE_SPACE_JOB) {
            context.updateList();
        }
    }

    @Override
    public void jobProgress(Object value, int id) {

    }
}
