package stericson.busybox.jobs.tasks;

import android.content.Context;

import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;
import java.util.List;
import stericson.busybox.App;
import stericson.busybox.R;
import stericson.busybox.Support.ShellCommand;
import stericson.busybox.jobs.AsyncJob;
import stericson.busybox.jobs.containers.JobResult;

public class UninstallTask extends BaseTask {

    public JobResult execute(AsyncJob j) {
        Context context = j.getContext();
        JobResult result = new JobResult();
        result.setSuccess(true);

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

        if (RootTools.remount("/system", "rw"))
        {
            j.publishCurrentProgress("preparing system...");

            try
            {
                j.publishCurrentProgress("Removing BusyBox!");

                List<String> foundPaths = RootTools.findBinary("busybox");

                for (String binaryPath : foundPaths)
                {
                    RootTools.remount(binaryPath, "rw");
                    command = new ShellCommand(this, 0,
                            "toolbox rm " + binaryPath + "/busybox",
                            "rm " + binaryPath + "/busybox");
                    Shell.startRootShell().add(command);
                    command.pause();

                    RootTools.remount(binaryPath, "ro");
                }
            }
            catch (Exception e) {}
        }

        App.getInstance().setInstalled(RootTools.isBusyboxAvailable());

        return result;
    }
}
