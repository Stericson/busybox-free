package stericson.busybox;

import java.util.ArrayList;
import java.util.List;

import stericson.busybox.adapter.AppletAdapter;
import stericson.busybox.adapter.TuneAdapter;
import stericson.busybox.jobs.containers.Item;

import android.view.View;

public class App {
    private static App instance = null;
    private List<Item> itemList;
    private View popupView;
    private boolean toggled = false;
    private boolean smartInstall = false;
    private boolean isInstalled = false;
    private String path = "";
    private String currentVersion = "";
    private String version = Constants.versions[0];
    private int versionPosition = 0;
    private int pathPosition = 0;
    private String found = "";
    private AppletAdapter appletadapter;
    private TuneAdapter tuneadapter;
    private float space = 0;

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

    public void setVersionPosition(int version_position) {
        this.versionPosition = version_position;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
}

