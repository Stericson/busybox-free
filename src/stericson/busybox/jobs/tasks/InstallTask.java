package stericson.busybox.jobs.tasks;

import android.content.Context;
import android.os.Environment;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.Shell;

import java.io.File;
import stericson.busybox.App;
import stericson.busybox.Constants;
import stericson.busybox.R;
import stericson.busybox.Support.Common;
import stericson.busybox.Support.ShellCommand;
import stericson.busybox.jobs.AsyncJob;
import stericson.busybox.jobs.containers.Item;
import stericson.busybox.jobs.containers.JobResult;

public class InstallTask extends BaseTask {

    public JobResult execute(AsyncJob j, String path, String version, boolean silent, boolean update_only) {
        Context context = j.getContext();
        String toolbox = Constants.toolbox;
        JobResult result = new JobResult();
        result.setSuccess(true);

        try {
            RootTools.getShell(true);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setError(context.getString(R.string.shell_error));
            return result;
        }

        if (!silent)
            j.publishCurrentProgress("Checking System...");

        try {
            if (!RootTools.fixUtils(new String[]{"ls", "rm", "ln", "dd", "chmod", "mount"})) {
                result.setError(context.getString(R.string.utilProblem));
                result.setSuccess(false);
                return result;
            }
        } catch (Exception e) {
            result.setError(context.getString(R.string.utilProblem));
            result.setSuccess(false);
            return result;
        }

        if (!silent)
            j.publishCurrentProgress("Preparing System...");

        try {
            RootTools.remount("/", "rw");
            RootTools.remount("/system", "rw");

            try {
                command = new ShellCommand(this, 0,
                        "cat /dev/null > /system/etc/resolv.conf",
                        "echo \"nameserver 8.8.4.4\" >> /system/etc/resolv.conf",
                        "echo \"nameserver 8.8.8.8\" >> /system/etc/resolv.conf",
                        "chmod 4755 /system/etc/resolv.conf");
                Shell.startRootShell().add(command);
                command.pause();

            } catch (Exception ignore) {
            }

            //ALWAYS run this, I don't care if it does exist...I want to always make sure it is there.
            Common.extractResources(context, "toolbox", Environment.getExternalStorageDirectory() + "/toolbox-stericson");

            try {
                command = new ShellCommand(this, 0,
                        "dd if=" + Environment.getExternalStorageDirectory() + "/toolbox-stericson of=" + toolbox,
                        "/system/bin/toolbox dd if=" + Environment.getExternalStorageDirectory() + "/toolbox-stericson of=" + toolbox,
                        "chmod 0755 " + toolbox,
                        "/system/bin/toolbox chmod 0755 " + toolbox);
                Shell.startRootShell().add(command);
                command.pause();

            } catch (Exception ignore) {
            }

            if (!new File("/system/bin/reboot").exists()) {
                if (!silent)
                    j.publishCurrentProgress("Adding reboot...");

                Common.extractResources(context, "reboot", Environment.getExternalStorageDirectory() + "/reboot-stericson");

                command = new ShellCommand(this, 0,
                        toolbox + " dd if=" + Environment.getExternalStorageDirectory() + "/reboot-stericson of=/system/bin/reboot",
                        "/system/bin/toolbox dd if=" + Environment.getExternalStorageDirectory() + "/reboot-stericson of=/system/bin/reboot",
                        "dd if=" + Environment.getExternalStorageDirectory() + "/reboot-stericson of=/system/bin/reboot",
                        toolbox + " chmod 0755 /system/bin/reboot",
                        "/system/bin/toolbox chmod 0755 /system/bin/reboot",
                        "chmod 0755 /system/bin/reboot");
                Shell.startRootShell().add(command);
                command.pause();
            }

            Common.extractResources(context, version, Environment.getExternalStorageDirectory() + "/busybox-stericson");

            if (path == null || path.equals("")) {
                path = "/system/bin/";
            }

            if (!path.endsWith("/")) {
                path = path + "/";
            }

            if (!silent)
                j.publishCurrentProgress("Installing Busybox...");

            command = new ShellCommand(this, 0,
                    toolbox + " rm " + path + "busybox",
                    "/system/bin/toolbox rm " + path + "busybox",
                    "rm " + path + "busybox",
                    "dd if=" + Environment.getExternalStorageDirectory() + "/busybox-stericson of=" + path + "busybox",
                    toolbox + " dd if=" + Environment.getExternalStorageDirectory() + "/busybox-stericson of=" + path + "busybox",
                    "/system/bin/toolbox dd if=" + Environment.getExternalStorageDirectory() + "/busybox-stericson of=" + path + "busybox",
                    toolbox + " chmod 0755 " + path + "busybox",
                    "/system/bin/toolbox chmod 0755 " + path + "busybox",
                    "chmod 0755 " + path + "busybox");
            Shell.startRootShell().add(command);
            command.pause();

            if (!new File(path + "busybox").exists()) {
                command = new ShellCommand(this, 0,
                        "cat " + Environment.getExternalStorageDirectory() + "/busybox-stericson > " + path + "busybox",
                        "cp " + Environment.getExternalStorageDirectory() + "/busybox-stericson " + path + "busybox",
                        toolbox + " cat " + Environment.getExternalStorageDirectory() + "/busybox-stericson > " + path + "busybox",
                        toolbox + " cp " + Environment.getExternalStorageDirectory() + "/busybox-stericson " + path + "busybox",
                        toolbox + " chmod 0755 " + path + "busybox",
                        "/system/bin/toolbox cat " + Environment.getExternalStorageDirectory() + "/busybox-stericson > " + path + "busybox",
                        "/system/bin/toolbox cp " + Environment.getExternalStorageDirectory() + "/busybox-stericson " + path + "busybox",
                        "/system/bin/toolbox chmod 0755 " + path + "busybox",
                        "chmod 0755 " + path + "busybox");
                Shell.startRootShell().add(command);
                command.pause();

            }

            if (!update_only) {
                if (App.getInstance().getItemList() == null || !App.getInstance().isSmartInstall()) {
                    command = new ShellCommand(this, 0, path + "busybox --install -s " + path);
                    Shell.startRootShell().add(command);
                    command.pause();

                } else {
                    for (Item item : App.getInstance().getItemList()) {

                        if (item.getOverWrite()) {
                            if (!silent)
                                j.publishCurrentProgress("Setting up " + item.getApplet());

                            command = new ShellCommand(this, 0,
                                    toolbox + " rm " + path + item.getApplet(),
                                    "/system/bin/toolbox rm " + path + item.getApplet(),
                                    "rm " + path + item.getApplet(),
                                    toolbox + " ln -s " + path + "busybox " + path + item.getApplet(),
                                    "/system/bin/toolbox ln -s " + path + "busybox " + path + item.getApplet(),
                                    "ln -s " + path + "busybox " + path + item.getApplet());
                            Shell.startRootShell().add(command);
                            command.pause();

                        } else if (item.getRemove()) {
                            if (!silent)
                                j.publishCurrentProgress("Removing " + item.getApplet());

                            command = new ShellCommand(this, 0,
                                    toolbox + " rm " + item.getAppletPath() + "/" + item.getApplet(),
                                    "/system/bin/toolbox rm " + item.getAppletPath() + "/" + item.getApplet(),
                                    "rm " + item.getAppletPath() + "/" + item.getApplet());
                            Shell.startRootShell().add(command);
                            command.pause();

                        }
                    }
                }
            }

            if (!silent)
                j.publishCurrentProgress("Removing old copies...");

            if (RootTools.exists(path + "busybox")) {
                String[] locations = Common.findBusyBoxLocations(true, false);
                if (locations.length >= 1) {
                    int i = 0;
                    while (i < locations.length) {
                        if (!locations[i].equals(path)) {
                            //Removing old copies
                            //TODO this may need to be moved, in the future this will become a lot larger.
                            RootTools.remount("/", "rw");
                            RootTools.remount(locations[i], "RW");

                            command = new ShellCommand(this, 0,
                                    toolbox + " rm " + locations[i] + "busybox",
                                    "/system/bin/toolbox rm " + locations[i] + "busybox",
                                    "rm " + locations[i] + "busybox",
                                    toolbox + " ln -s " + path + "busybox " + locations[i],
                                    "/system/bin/toolbox ln -s " + path + "busybox " + locations[i],
                                    "ln -s " + path + "busybox " + locations[i]);

                            Shell.startRootShell().add(command);
                            command.pause();

                            if (new File(locations[i] + "busybox").exists()) {
                                RootTools.log("BusyBox Installer", "The file was not removed: " + locations[i] + "busybox");
                            } else {
                                RootTools.log("BusyBox Installer", "The file was successfully removed: " + locations[i] + "busybox");
                            }
                        }
                        i++;
                    }
                }
            }
        } catch (Exception e) {
        }

        App.getInstance().setInstalled(RootTools.isBusyboxAvailable());

        return result;
    }
}
