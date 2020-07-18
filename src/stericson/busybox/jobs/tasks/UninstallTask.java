package stericson.busybox.jobs.tasks;

import android.content.Context;

import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;

import java.util.ArrayList;
import java.util.List;
import stericson.busybox.App;
import stericson.busybox.R;
import stericson.busybox.support.ShellCommand;
import stericson.busybox.jobs.AsyncJob;
import stericson.busybox.jobs.containers.JobResult;

public class UninstallTask extends BaseTask {

    public JobResult execute(AsyncJob j) {
        Context context = j.getContext();
        JobResult result = new JobResult();
        result.setSuccess(true);

        List<String> paths = new ArrayList<>(RootShell.getPath());
        paths.add("/sbin/supersu/xbin");
        paths.add("/sbin/supersu/bin");


        try
        {
            RootTools.getShell(true);
        }
        catch (Exception e)
        {
            result.setSuccess(false);
            result.setError(context.getString(R.string.shell_error));
            return result;
        }

        j.publishCurrentProgress("Checking System...");

        try
        {
            if (!RootTools.fixUtils(new String[]{"ls", "rm", "ln", "dd", "chmod", "mount"}))
            {
                result.setError(context.getString(R.string.utilProblem));
                result.setSuccess(false);
                return result;
            }
        }
        catch (Exception e)
        {
            result.setError(context.getString(R.string.utilProblem));
            result.setSuccess(false);
            return result;
        }

        //try to remount system as rw...
        RootTools.remount("/system", "rw");

        j.publishCurrentProgress("preparing system...");

        try
        {
            j.publishCurrentProgress("Removing BusyBox!");

            List<String> foundPaths = RootShell.findBinary("busybox", paths, false);

            for (String binaryPath : foundPaths)
            {
                if(!binaryPath.endsWith("/")) {
                    binaryPath += "/";
                }

                RootTools.remount(binaryPath, "rw");
                command = new ShellCommand(this, 0,
                        "toolbox rm " + binaryPath + "busybox",
                        "toybox rm " + binaryPath + "busybox",
                        "rm " + binaryPath + "busybox");
                Shell.startRootShell(0, Shell.ShellContext.SUPERSU, 3).add(command);
                command.pause();

                RootTools.remount(binaryPath, "ro");
            }
        }
        catch (Exception e) {}

        RootTools.remount("/system", "ro");

        App.getInstance().setInstalled(!RootShell.findBinary("busybox", paths, false).isEmpty());

        return result;
    }
}
