package stericson.busybox;

import java.util.ArrayList;
import java.util.List;

import stericson.busybox.adapter.AppletAdapter;
import stericson.busybox.adapter.TuneAdapter;
import stericson.busybox.jobs.containers.Item;

import android.os.Build;
import android.util.Log;
import android.view.View;

public class App {
    private static App instance = null;

    private String installPath = "";
    private String currentVersion = "";
    private String version = Constants.versions[0];
    private String found = "";

    private boolean smartInstall = false;
    private boolean isInstalled = false;
    private boolean toggled = true;
    private boolean customInstallPath = false;
    private boolean systemlessRoot = false;

    private int versionPosition = 0;
    private int pathPosition = 0;

    private float space = 0;

    private List<Item> itemList;
    private View popupView;
    private AppletAdapter appletadapter;
    private TuneAdapter tuneadapter;

    private App() {
    }

    public boolean isInstalled() {
        return isInstalled;
    }

    public void setInstalled(boolean isInstalled) {
        this.isInstalled = isInstalled;
    }

    public static App getInstance() {
        if (instance == null)
            instance = new App();

        return instance;
    }

    public String getArch()
    {
        String arch;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            arch = Build.CPU_ABI;
        } else {
            arch = Build.SUPPORTED_ABIS[0];
        }

        String arc = arch.substring(0, 3).toUpperCase();

        if (arc.equals("ARM"))
        {
            return Constants.ARM;
        }
        else if (arc.equals("MIP"))
        {
            return Constants.MIPS;
        }
        else if (arc.equals("X86"))
        {
            return Constants.X86;
        }
        else
        {
            Log.e("Stericson-Busybox", "Unknown arch: " + arc);

            return arc;
        }
    }

    public AppletAdapter getAppletadapter() {
        return appletadapter;
    }

    public void setAppletadapter(AppletAdapter appletadapter) {
        this.appletadapter = appletadapter;
    }

    public TuneAdapter getTuneadapter() {
        return tuneadapter;
    }

    public void setTuneadapter(TuneAdapter tuneadapter) {
        this.tuneadapter = tuneadapter;
    }

    public String getFound() {
        return found;
    }

    public void setFound(String found) {
        this.found = found;
    }

    public int getVersionPosition() {
        return versionPosition;
    }

    public int getPathPosition() {
        return pathPosition;
    }

    public void setPathPosition(int path_position) {
        this.pathPosition = path_position;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getInstallPath() {
        return installPath;
    }

    public void setInstallPath(String installPath) {
        this.installPath = installPath;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isSmartInstall() {
        return smartInstall;
    }

    public void setSmartInstall(boolean smartInstall) {
        this.smartInstall = smartInstall;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }

    public void setPopupView(View view) {
        this.popupView = view;
    }

    public void setItemList(List<Item> itemList) {
        if (itemList != null) {
            this.itemList = new ArrayList<Item>();
            this.itemList.addAll(itemList);
        } else {
            this.itemList = null;
        }
    }

    public List<Item> getItemList() {
        return this.itemList;
    }

    public View getPopupView() {
        return this.popupView;
    }

    public float getSpace() {
        return space;
    }

    public void setSpace(float space) {
        this.space = space;
    }

    public boolean isCustomInstallPath()
    {
        return customInstallPath;
    }

    public void setCustomInstallPath(boolean customInstallPath)
    {
        this.customInstallPath = customInstallPath;
    }

    public boolean isSystemlessRoot()
    {
        return systemlessRoot;
    }

    public void setSystemlessRoot(boolean systemlessRoot)
    {
        this.systemlessRoot = systemlessRoot;
    }
}

