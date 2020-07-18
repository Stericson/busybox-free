package stericson.busybox.jobs.tasks;

import android.content.Context;

import com.stericson.RootShell.RootShell;
import com.stericson.RootTools.RootTools;

import java.util.ArrayList;
import java.util.List;

import stericson.busybox.App;
import stericson.busybox.Constants;
import stericson.busybox.R;
import stericson.busybox.support.Common;
import stericson.busybox.jobs.AsyncJob;
import stericson.busybox.jobs.containers.JobResult;

public class InitialChecksTask {

    public static JobResult execute(AsyncJob j) {
        Context context = j.getContext();
        JobResult result = new JobResult();

        //Make sure we support their device
        String arch = App.getInstance().getArch();

        if(!arch.equals(Constants.ARM) && !arch.equals(Constants.X86))
        {
            result.setSuccess(false);
            result.setError(context.getString(R.string.device_unsupported) + " " + arch);
            return result;
        }

        //configure busybox locations..
        addPath("/system/xbin");
        addPath("/system/bin");

        try {
            RootTools.getShell(true);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setError(context.getString(R.string.shell_error));
            return result;
        }

        if (!RootTools.isRootAvailable()) {
            result.setError(context.getString(R.string.noroot2));
        } else {
            try {
                if (!RootTools.isAccessGiven()) {
                    result.setError(context.getString(R.string.noAccess));
                }

                String suXbinLocation = "/su/xbin";
                String subinLocation = "/su/bin";
                String supersuXbinLocation = "/sbin/supersu/xbin";
                String supersubinLocation = "/sbin/supersu/bin";
                String magisksbinLocation = "/sbin/magisk";
                String magisksbinSuLocation = "/sbin/su";

                boolean suxbinExists = RootTools.exists(suXbinLocation, true);
                boolean subinExists = RootTools.exists(subinLocation, true);
                boolean supersuXbinExists = RootTools.exists(supersuXbinLocation, true);
                boolean supersubinExists = RootTools.exists(supersubinLocation, true);
                boolean magisksbinExists = RootTools.exists(magisksbinLocation, false);
                boolean magisksbinSuExists = RootTools.exists(magisksbinSuLocation, false);

                //check for /su/xbin
                if(suxbinExists)
                {
                    //Default /su/xbin for systemless root
                    App.getInstance().setSystemlessRoot(true);
                    addPath("/su/xbin");
                    App.getInstance().setSpace((float) (Common.getSpace(suXbinLocation) / 1000));
                    App.getInstance().setInstallPath(suXbinLocation);
                    App.getInstance().setPathPosition(2);
                }
                else if(subinExists)
                {
                    //Default /su/bin for systemless root if /su/xbin doesn't exist
                    App.getInstance().setSystemlessRoot(true);
                    addPath("/su/bin");
                    App.getInstance().setSpace((float) (Common.getSpace(subinLocation) / 1000));
                    App.getInstance().setInstallPath(subinLocation);
                    App.getInstance().setPathPosition(2);
                }
                //check for /su/xbin
                else if(supersuXbinExists)
                {
                    //Default /sbin/supersu/xbin for systemless root
                    App.getInstance().setSystemlessRoot(true);
                    addPath("/sbin/supersu/xbin");
                    App.getInstance().setSpace((float) (Common.getSpace(supersuXbinLocation) / 1000));
                    App.getInstance().setInstallPath(supersuXbinLocation);
                    App.getInstance().setPathPosition(2);
                }
                else if(supersubinExists)
                {
                    //Default /sbin/supersu/bin for systemless root if /sbin/supersu/xbin doesn't exist
                    App.getInstance().setSystemlessRoot(true);
                    addPath("/sbin/supersu/bin");
                    App.getInstance().setSpace((float) (Common.getSpace(supersubinLocation) / 1000));
                    App.getInstance().setInstallPath(supersubinLocation);
                    App.getInstance().setPathPosition(2);
                }
                else if(magisksbinExists && magisksbinSuExists) {
                    //Default /sbin for systemless root if /sbin/su exist and /sbin/magisk exists
                    App.getInstance().setSystemlessRoot(true);
                    addPath("/sbin");
                    App.getInstance().setSpace((float) (Common.getSpace("/sbin") / 1000));
                    App.getInstance().setInstallPath("/sbin");
                    App.getInstance().setPathPosition(2);
                }
                else
                {
                    App.getInstance().setSpace((float) (Common.getSpace(Constants.locations.get(0)) / 1000));
                    App.getInstance().setInstallPath(Constants.locations.get(0));
                }

            } catch (Exception e) {
                result.setError(context.getString(R.string.accessUndetermined));
            }
        }

        addPath("Custom Path");

        List<String> paths = new ArrayList<>(RootShell.getPath());
        paths.add("/sbin/supersu/xbin");
        paths.add("/sbin/supersu/bin");

        App.getInstance().setInstalled(!RootShell.findBinary("busybox", paths, true).isEmpty());

        Common.extractBusybox(context, "");

        return result;
    }

    private static void addPath(String path) {
        boolean found = false;

        for(String p : Constants.locations) {
            if(p.trim().equals(path.trim())) {
                found = true;
            }
        }

        if(!found) {
            Constants.locations.add(path);
        }
    }
}
