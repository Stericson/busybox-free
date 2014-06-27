package stericson.busybox.Activity;

import stericson.busybox.App;
import stericson.busybox.R;
import stericson.busybox.adapter.PageAdapter;
import stericson.busybox.interfaces.Choice;
import stericson.busybox.interfaces.JobCallback;
import stericson.busybox.jobs.FindAppletInformationJob;
import stericson.busybox.jobs.InitialChecksJob;
import stericson.busybox.jobs.InstallJob;
import stericson.busybox.jobs.UninstallJob;
import stericson.busybox.jobs.containers.JobResult;
import stericson.busybox.listeners.PageChange;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.stericson.RootTools.RootTools;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.Calendar;

public class MainActivity extends BaseActivity implements JobCallback, Choice {

    //Constants
    private static final int UNINSTALL = 0;
    private static final int INSTALL = 1;

    private TextView header;
    private ViewPager pager;
    private PageAdapter adapter;
    private TitlePageIndicator indicator;

    private Button install;
    private Button uninstall;

    public TextView view1;
    public TextView view2;

    //maintains current page position
    public int position;

    private String custom = "";
    private TextView freespace;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        App.getInstance().setItemList(null);

        randomAnimation();

        install = (Button) findViewById(R.id.install);
        uninstall = (Button) findViewById(R.id.uninstall);

        header = (TextView) findViewById(R.id.header_main);
        header.setTypeface(tf);

        new InitialChecksJob(this, this).execute();

        final CheckBox autoupdate = (CheckBox) findViewById(R.id.autoupdate);
        autoupdate.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                MainActivity.this.initiatePopupWindow(getString(R.string.autoupdate), false, MainActivity.this);
                autoupdate.setChecked(false);
            }

        });
    }

    public void initiatePager() {
        if (pager == null) {
            pager = (ViewPager) findViewById(R.id.viewpager);
            indicator = (TitlePageIndicator) findViewById(R.id.indicator);

            adapter = new PageAdapter(MainActivity.this);
            indicator.setOnPageChangeListener(new PageChange(MainActivity.this));
            pager.setAdapter(adapter);
            indicator.setViewPager(pager);
            pager.setCurrentItem(1);
            this.position = 1;
        }
    }

    public void install(View v) {
        install.setEnabled(false);

        if (App.getInstance().getItemList() == null) {
            App.getInstance().setSmartInstall(false);
        }

        if (App.getInstance().getPath() != null) {
            new InstallJob(this, this, App.getInstance().getPath()).execute();
        } else {
            initiatePopupWindow("An unexpected error has occured, please take a screenshot of the application and send it to me at StericDroid@gmail.com", false, this);
        }
    }

    public void uninstall(View v) {
        this.makeChoice(this, UNINSTALL, R.string.careful, R.string.beforeUninstall, R.string.uninstall, R.string.cancel);
    }

    public void uninstallDone() {
        if (pager != null)
            pager.setCurrentItem(2);

        RootTools.remount("/system", "ro");
        App.getInstance().setFound("Busybox is not installed.");

        if (!App.getInstance().isInstalled()) {
            initiatePopupWindow(this.getString(R.string.uninstallsuccess), false, this);
        } else {
            initiatePopupWindow(this.getString(R.string.uninstallfailed), false, this);
        }
    }

    public void installDone() {
        String currentVersion = App.getInstance().getCurrentVersion();

        install.setEnabled(true);

        if(pager != null)
        {
            pager.setCurrentItem(2);
        }

        RootTools.remount("/system", "ro");

        boolean result = RootTools.checkUtil("busybox");

        if (result) {
            uninstall.setEnabled(true);

            String thisVersion = RootTools.getBusyBoxVersion();

            if (thisVersion == null) {
                thisVersion = "";
            }

            if (currentVersion == null) {
                currentVersion = "-1";
            }

            if (thisVersion.contains(App.getInstance().getVersion().toLowerCase().replace("busybox", "").trim())) {
                if (currentVersion.equals(thisVersion)) {
                    initiatePopupWindow(this.getString(R.string.installedsame), false, this);
                } else {
                    initiatePopupWindow(this.getString(R.string.installedunique), false, this);
                }
            } else {
                if (currentVersion.equals(thisVersion)) {
                    initiatePopupWindow(this.getString(R.string.installedsame), false, this);
                } else {
                    initiatePopupWindow(this.getString(R.string.installedsomethingelse), false, this);
                }
            }
        } else {
            initiatePopupWindow(this.getString(R.string.failed), true, this);
        }
    }

    @Override
    public void choiceMade(boolean choice, int id) {
        if (id == UNINSTALL) {
            if (choice) {
                uninstall.setEnabled(false);

                new UninstallJob(this, this).execute();
            }
        }
    }

    public void jobFinished(JobResult result, int id) {

        switch (id) {
            case InitialChecksJob.Checks:
                if (result.getError() != null && !result.getError().equals(""))
                    this.initiatePopupWindow(result.getError(), true, this);
                else {
                    new FindAppletInformationJob(this, this, false).execute();

                    initiatePager();

                    Calendar cal = Calendar.getInstance();

                    boolean showPromo = ((cal.get(Calendar.DAY_OF_MONTH) >= 25 && cal.get(Calendar.MONTH) == 10 && cal.get(Calendar.YEAR) == 2013));

                    if (showPromo) {
                    	this.initiatePopupWindow(getString(R.string.welcome_sale), false, this);
                    } else {
                    	this.initiatePopupWindow(getString(R.string.welcome), false, this);
                    }

//                    this.initiatePopupWindow(getString(R.string.welcome), false, this);

                    install.setEnabled(true);

                    if (App.getInstance().isInstalled()) {
                        uninstall.setEnabled(true);
                    }
                }
                break;
            case FindAppletInformationJob.APPLET_INFO:
                App.getInstance().setItemList(result.getItemList());
                pager.setAdapter(adapter);
                pager.setCurrentItem(position);

                break;

            case InstallJob.INSTALL_JOB:
                if (result.isSuccess()) {
                    installDone();
                } else {
                    initiatePopupWindow(result.getError(), true, this);
                }
                break;

            case UninstallJob.UNINSTALL_JOB:
                if (result.isSuccess()) {
                    uninstallDone();
                } else {
                    initiatePopupWindow(result.getError(), false, this);
                }
                break;
        }
    }

    @Override
    public void jobProgress(Object value, int id) {
        View popView = App.getInstance().getPopupView();

        TextView header = popView != null ? (TextView) popView.findViewById(R.id.header) : null;

        switch (id) {
            case FindAppletInformationJob.APPLET_INFO:
                try {
                    //TextView header = (TextView) App.getInstance().getPopupView().findViewById(R.id.header);
                    view1.setText(getString(R.string.gatheringAbout) + " " + value);
                } catch (Exception e) {
                }

                try {
                    //TextView header = (TextView) App.getInstance().getPopupView().findViewById(R.id.header);
                    view2.setText(getString(R.string.gatheringAbout) + " " + value);
                } catch (Exception e) {
                }
                break;
            case InstallJob.INSTALL_JOB:
                if (header != null) {
                    header.setText(this.getString(R.string.installing) + " " + value);
                }
                break;
            case UninstallJob.UNINSTALL_JOB:
                if (header != null) {
                    header.setText(this.getString(R.string.uninstalling) + " " + value);
                }
                break;
        }
    }

    public void toggle_smart(View v) {
        try {
            App.getInstance().setToggled(!App.getInstance().isToggled());
            updateList();

            ImageButton toggle = (ImageButton) pager.findViewById(R.id.toggle_smart);
            toggle.setImageDrawable(getResources().getDrawable(App.getInstance().isToggled() ? R.drawable.arrow_up_float : R.drawable.arrow_down_float));
        } catch (Exception ignore) {
        }
    }

    public void updateList() {
        try {
            App.getInstance().getAppletadapter().update();
        } catch (Exception ignore) {
        }

        try {
            App.getInstance().getTuneadapter().update();
        } catch (Exception ignore) {
        }
    }

    public void close(View v) {
        super.close(v);

        if (!endApplication) {

        }
    }

    public String getCustomPath() {
        return this.custom;
    }

    public TextView getFreeSpace() {
        return this.freespace;
    }

    public void setCustomPath(String path) {
        this.custom = path;
    }

    public void setFreeSpace(TextView space) {
        this.freespace = space;
    }

    public ListView getListView() {
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

}
