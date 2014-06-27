package stericson.busybox.jobs.tasks;

import android.content.Context;
import com.stericson.RootTools.RootTools;
import java.util.ArrayList;
import java.util.List;

import stericson.busybox.R;
import stericson.busybox.Support.ShellCommand;
import stericson.busybox.jobs.AsyncJob;
import stericson.busybox.jobs.containers.Item;
import stericson.busybox.jobs.containers.JobResult;

public class FindAppletInformationTask extends BaseTask {

    private final int FIND_APPLET_INFORMATION = 2;

    Item item = null;
    Context context = null;
    List<String> result = new ArrayList<String>();
    List<Item> itemList;


    public JobResult execute(AsyncJob j, boolean updating, String[] applets) {
        context = j.getContext();
        JobResult result = new JobResult();
        result.setSuccess(true);

        try {
            RootTools.getShell(true);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setError(context.getString(R.string.shell_error));
            return result;
        }

        itemList = new ArrayList<Item>();

        for (String applet : applets) {
            if (j.isCancelled()) {
                return null;
            }

            if (!updating)
                j.publishCurrentProgress(applet);

            item = new Item();

            item.setApplet(applet);

            findApplet(applet);
        }

        result.setItemList(itemList);
        return result;
    }

    private void findApplet(String applet) {
        result = new ArrayList<String>();

        if (RootTools.findBinary(applet) && RootTools.lastFoundBinaryPaths.size() > 0) {

            item.setFound(true);

            List<String> paths = new ArrayList<String>();
            paths.addAll(RootTools.lastFoundBinaryPaths);

            for (String path : paths) {
                if (path != null && path.contains("/system/bin")) {
                    item.setAppletPath(path);
                    break;
                }
            }

            if (item.getAppletPath().equals("")) {
                if(paths.size() > 0)
                {
                    item.setAppletPath(paths.get(0));
                }
                else
                {
                    item.setAppletPath("");
                }
            }

            String symlink = RootTools.getSymlink(item.getAppletPath() + applet);

            item.setSymlinkedTo(symlink);

            if ((!item.getSymlinkedTo().equals(""))) {
                if (item.getSymlinkedTo().trim().toLowerCase().endsWith("busybox")) {
                    item.setRecommend(true);
                } else {
                    item.setRecommend(false);
                }
            } else if (item.getSymlinkedTo().equals("")) {
                item.setRecommend(false);
            } else
                item.setRecommend(true);

            item.setOverwrite(item.getRecommend());

            if (item.getDescription().equals("")) {
                try {
                    command = new ShellCommand(this, FIND_APPLET_INFORMATION, false, "busybox " + applet + " --help");
                    RootTools.getShell(true).add(command);
                    command.pause();

                    String appletInfo = "";

                    for (String info : result) {
                        if (!info.contains("not found") && !info.equals("1") && !info.contains("multi-call binary") && !info.trim().equals("")) {
                            appletInfo += info + "\n";
                        }
                    }

                    item.setDescription(appletInfo);
                } catch (Exception e) {
                }
            }
        } else {
            item.setFound(false);
            item.setSymlinkedTo("");
            item.setRecommend(true);
        }

        itemList.add(item);
        result = new ArrayList<String>();

    }

    //callbacks
    @Override
    public void commandOutput(int id, String line) {
        if (id == FIND_APPLET_INFORMATION) {
            result.add(line);
        }
    }
}
