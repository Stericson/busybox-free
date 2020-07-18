package stericson.busybox.jobs.tasks;

import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Shell;
import com.stericson.RootTools.RootTools;

import android.content.Context;

import java.util.Collections;

import stericson.busybox.R;
import stericson.busybox.App;
import stericson.busybox.Constants;
import stericson.busybox.support.Common;
import stericson.busybox.support.ShellCommand;
import stericson.busybox.jobs.AsyncJob;
import stericson.busybox.jobs.containers.Item;
import stericson.busybox.jobs.containers.JobResult;

public class InstallTask extends BaseTask
{
    AsyncJob j;
    boolean silent;

    private boolean checkBusybox(String location)
    {
        if (RootTools.exists(location + "busybox"))
        {
            //the file exists...
            if(RootTools.getBusyBoxVersion(location).length() > 0)
            {
                //looks like it's working
                return true;
            }
        }

        return false;
    }

    public JobResult execute(AsyncJob j, String destination, boolean silent, boolean update_only)
    {
        this.j = j;
        this.silent = silent;
        Context context = j.getContext();

        String binaryLocation = Constants.preparedLocation + "busybox";


        JobResult result = new JobResult();
        result.setSuccess(true);

        if (!RootTools.exists(binaryLocation))
        {
            result.setSuccess(false);
            result.setError(context.getString(R.string.fatal_error));
            return result;
        }
        else
        {
            //Check the integrity of the file
            String tmp_version = RootTools.getBusyBoxVersion(Constants.preparedLocation);

            if (tmp_version.equals(""))
            {
                result.setSuccess(false);
                result.setError(context.getString(R.string.binary_verification_failed_install));
                return result;
            }

        }

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

        if (!silent)
        {
            j.publishCurrentProgress("Checking System...");
        }

        //open up the system
        RootTools.remount("/", "rw");
        RootTools.remount("/system", "rw");

        /*
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
        */

        if (!silent)
        {
            j.publishCurrentProgress("Preparing System...");
        }

        try
        {
            if(!RootTools.exists("/system/etc/resolv.conf"))
            {

                command = new ShellCommand(this, 0,
                        "cat /dev/null > /system/etc/resolv.conf",
                        "echo \"nameserver 8.8.4.4\" >> /system/etc/resolv.conf",
                        "echo \"nameserver 8.8.8.8\" >> /system/etc/resolv.conf",
                        "chmod 0644 /system/etc/resolv.conf");
                Shell.startRootShell(0, Shell.ShellContext.SUPERSU, 3).add(command);
                command.pause();
            }

            if (destination == null || destination.equals(""))
            {
                destination = "/system/xbin/";
            }

            if (!destination.endsWith("/"))
            {
                destination = destination + "/";
            }

            if (!silent)
            {
                j.publishCurrentProgress("Installing Busybox...");
            }

            //open the destination
            RootTools.remount(destination, "rw");

            //install the binary
            boolean installBinaryResult = this.installBusybox(binaryLocation, destination);

            //installation failed
            if(!installBinaryResult)
            {
                App.getInstance().setInstalled(false);
                return result;
            }

            if(!update_only)
            {
                //start applet installation
                this.configureApplets(destination);
            }

            if (!silent)
            {
                j.publishCurrentProgress("Removing old copies...");
            }

            //Cleanup other copies of Busybox
            this.handleDuplicates(destination);

            //clean things up
            RootTools.remount("/", "ro");
            RootTools.remount("/system", "ro");

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        App.getInstance().setInstalled(!RootShell.findBinary("busybox", Collections.singletonList(destination), true).isEmpty());

        return result;
    }

    private boolean configureApplets(String destination)
    {
        if (App.getInstance().getItemList() == null || !App.getInstance().isSmartInstall())
        {
            try
            {
                command = new ShellCommand(this, 0, destination + "busybox --install -s " + destination);
                Shell.startRootShell(0, Shell.ShellContext.SUPERSU, 3).add(command);
                command.pause();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return false;
            }
        }
        else
        {
            for (Item item : App.getInstance().getItemList())
            {
                this.configureApplet(item, destination);
            }
        }

        return true;
    }

    private boolean configureApplet(Item item, String destination)
    {
        //installing
        if (item.getOverWrite())
        {
            if (!silent)
            {
                j.publishCurrentProgress("Setting up " + item.getApplet());
            }

            try
            {
                command = new ShellCommand(this, 0,
                        destination + "busybox rm " + destination + item.getApplet(),
                        destination + "busybox ln -s " + destination + "busybox " + destination + item.getApplet());
                Shell.startRootShell(0, Shell.ShellContext.SUPERSU, 3).add(command);
                command.pause();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return false;
            }

        }
        //removing
        else if (item.getRemove())
        {
            if (!silent)
            {
                j.publishCurrentProgress("Removing " + item.getApplet());
            }

            try
            {
                command = new ShellCommand(this, 0,
                        destination + "busybox rm " + item.getAppletPath() + "/" + item.getApplet());
                Shell.startRootShell(0, Shell.ShellContext.SUPERSU, 3).add(command);
                command.pause();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }

    private boolean installBusybox(String binaryLocation, String destination)
    {
        try
        {
            command = new ShellCommand(this, 0,
                    "dd if=" + binaryLocation + " of=" + destination + "busybox",
                    "chmod 0755 " + destination + "busybox");
            Shell.startRootShell(0, Shell.ShellContext.SUPERSU, 3).add(command);
            command.pause();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(this.checkBusybox(destination))
        {
            return true;
        }

        try
        {
            command = new ShellCommand(this, 0,
                    "toolbox dd if=" + binaryLocation + " of=" + destination + "busybox",
                    "toolbox chmod 0755 " + destination + "busybox");
            Shell.startRootShell(0, Shell.ShellContext.SUPERSU, 3).add(command);
            command.pause();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(this.checkBusybox(destination))
        {
            return true;
        }

        try
        {
            command = new ShellCommand(this, 0,
                    "toybox dd if=" + binaryLocation + " of=" + destination + "busybox",
                    "toybox chmod 0755 " + destination + "busybox");
            Shell.startRootShell(0, Shell.ShellContext.SUPERSU, 3).add(command);
            command.pause();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(this.checkBusybox(destination))
        {
            return true;
        }

        try
        {
            command = new ShellCommand(this, 0,
                    "cat " + binaryLocation + " > " + destination + "busybox",
                    "chmod 0755 " + destination + "busybox");
            Shell.startRootShell(0, Shell.ShellContext.SUPERSU, 3).add(command);
            command.pause();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(this.checkBusybox(destination))
        {
            return true;
        }

        try
        {
            command = new ShellCommand(this, 0,
                    "toolbox cat " + binaryLocation + " > " + destination + "busybox",
                    "toolbox chmod 0755 " + destination + "busybox");
            Shell.startRootShell(0, Shell.ShellContext.SUPERSU, 3).add(command);
            command.pause();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return this.checkBusybox(destination);
    }

    private void handleDuplicates(String destination)
    {
        String[] locations = Common.findBusyBoxLocations(true, false);
        if (locations.length >= 1)
        {
            int i = 0;
            while (i < locations.length)
            {
                String location = locations[i];

                if (!location.equals(destination))
                {
                    //Removing old copies
                    RootTools.remount(location, "rw");

                    try
                    {
                        command = new ShellCommand(this, 0,
                                destination + "busybox rm " + location + "busybox");
                        Shell.startRootShell(0, Shell.ShellContext.SUPERSU, 3).add(command);
                        command.pause();
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                    if (RootTools.exists(location + "busybox"))
                    {
                        RootTools.log("BusyBox Installer", "The file was not removed: " + location + "busybox");
                    }
                    else
                    {
                        RootTools.log("BusyBox Installer", "The file was successfully removed: " + location + "busybox");
                    }

                    //don't make /su ro
                    if(!location.startsWith("/su"))
                    {
                        RootTools.remount(location, "ro");
                    }
                }

                i++;
            }

        }
    }
}
