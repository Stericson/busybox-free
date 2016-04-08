package stericson.busybox.Support;

import android.content.Context;
import android.os.Debug;
import android.util.Log;

import com.stericson.RootShell.RootShell;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;
import com.stericson.RootShell.execution.Shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import stericson.busybox.App;
import stericson.busybox.Constants;
import stericson.busybox.interfaces.CommandCallback;

public class Common {

    static final int BB_VERSION = 65463;
    static final int SPACE = 9384;

    static ShellCommand command;
    static List<String> paths = new ArrayList<String>();
    static List<String> freeSpaceResults = new ArrayList<String>();

    public static void cleanExtractedBusybox()
    {
        try {

            //prepares Busybox for installation
            Command command = new Command(0,
                    "rm " + Constants.preparedLocation + "busybox");
            Shell shell = RootShell.getShell(true);
            shell.add(command);

        } catch (Exception e) {}

    }

    public static boolean extractBusybox(Context context, String customBinaryLocation) {

        String binaryLocation = customBinaryLocation.length() > 0 ? customBinaryLocation : Constants.storageLocation;
        String realFile = "busybox-" + App.getInstance().getArch() + ".png";

        //sometimes this can cause a nullpointer
        //reference https://code.google.com/p/android/issues/detail?id=8886
        File storageDir;

        try
        {
            Constants.storageLocation = context.getFilesDir().toString() + "/bb/";
        }
        catch (NullPointerException e)
        {
            //try to use the path that was set manually in the constant...
        }

        storageDir = new File(Constants.storageLocation);

        if(!storageDir.exists())
        {
            storageDir.mkdirs();
        }

        new File(storageDir.getPath() + "/busybox").delete();

        try {

            InputStream in;

            if(customBinaryLocation.length() > 0)
            {
                in = new FileInputStream(new File(customBinaryLocation));
            }
            else
            {
                in = context.getResources().getAssets().open(realFile);
            }

            OutputStream out = new FileOutputStream(
                    storageDir.getPath() + "/busybox");
            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            // we have to close these here
            out.flush();
            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try {

            //prepares Busybox for installation
            Command command = new Command(0,
                    "dd if=" + binaryLocation + "busybox of=" + Constants.preparedLocation + "busybox",
                    "chmod 0755 " + Constants.preparedLocation + "busybox");
            Shell shell = RootShell.getShell(true);
            shell.add(command);

        } catch (Exception e) {}

        return RootTools.exists(binaryLocation + "busybox");
    }

    /**
     * @return long Size, converted to kilobytes (from xxx or xxxm or xxxk etc.)
     */
    private static long getConvertedSpace(String spaceStr) {
        try {
            double multiplier = 1.0;
            char c;
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < spaceStr.length(); i++) {
                c = spaceStr.charAt(i);
                if (!Character.isDigit(c) && c != '.') {
                    if (c == 'm' || c == 'M') {
                        multiplier = 1024.0;
                    } else if (c == 'g' || c == 'G') {
                        multiplier = 1024.0 * 1024.0;
                    }
                    break;
                }
                sb.append(spaceStr.charAt(i));
            }
            return (long) Math.ceil(Double.valueOf(sb.toString()) * multiplier);
        } catch (Exception e) {
            return -1;
        }
    }

    public static String getSingleBusyBoxPath() {

        try {
            command = new ShellCommand(new CCB(), BB_VERSION, "busybox which busybox");
            Shell.startRootShell().add(command);
            command.pause();

            for (String path : paths) {
                if (path.startsWith("/")) {
                    return path.replace("busybox", "");
                } else {
                    break;
                }
            }

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static long getSpace(final String path) {
        freeSpaceResults = new ArrayList<String>();
        boolean found = false;
        RootTools.log("Looking for Space");

        try {
            command = new ShellCommand(new CCB(), SPACE, "df " + path);
            Shell.startRootShell().add(command);
            command.pause();

        } catch (Exception e) {
            return 0;
        }

        if (freeSpaceResults != null) {
            int columnIndex = 0;
            int count = 0;

            for (String line : freeSpaceResults ) {
                count = 0;
                String[] columns = line.split(" ");

                for(String column : columns)
                {
                    if(column.trim().length() > 0)
                    {
                        count++;

                        if (found && columnIndex == count)
                        {
                            return getConvertedSpace(column);
                        }
                        else if (column.toLowerCase().equals("available") || column.toLowerCase().equals("free"))
                        {
                            found = true;
                            columnIndex = count;
                        }
                    }
                }
            }
        }

        return -1;
    }

    public static String[] findBusyBoxLocations(boolean includeSymlinks, boolean single) {

        if (single) {
            String single_location = getSingleBusyBoxPath();

            if (single_location != null) {
                return new String[]{single_location};
            }
        }

        Set<String> tmpSet = new HashSet<String>();

        try {
            for (String paths : RootTools.getPath()) {
                if (RootTools.exists(paths + "/busybox")) {
                    String symlink = RootTools.getSymlink(paths + "/busybox");

                    if (includeSymlinks || symlink.equals("")) {
                        tmpSet.add(paths);

                        if (single)
                            break;
                    }
                }
            }
        } catch (Exception ignore) {
            // nothing found.
        }

        String locations[] = new String[tmpSet.size()];

        int i = 0;
        for (String paths : tmpSet) {
            locations[i] = paths + "/";
            i++;
        }

        return locations;
    }

    public static class CCB implements CommandCallback {
        @Override
        public void commandCallback(CommandResult result) {

        }

        @Override
        public void commandOutput(int id, String line) {
            if (id == BB_VERSION) {
                paths.add(line);
            }
            else if (id == SPACE) {
                freeSpaceResults.add(line);
            }

        }
    }
}
