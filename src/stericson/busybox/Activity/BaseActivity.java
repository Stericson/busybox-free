package stericson.busybox.activity;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import stericson.busybox.R;
import stericson.busybox.interfaces.PopupCallback;
import stericson.busybox.support.Common;
import stericson.busybox.interfaces.Choice;
import stericson.busybox.jobs.AsyncJob;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.stericson.RootShell.RootShell;
import com.stericson.RootTools.RootTools;

public class BaseActivity extends Activity {

    //protected IabHelper mHelper;

    public PopupWindow pw;
    public boolean endApplication;

    public Typeface tf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RootTools.debugMode = true;
        RootShell.debugMode = true;
        RootTools.default_Command_Timeout = 5000;

        try {
            tf = Typeface.createFromAsset(getAssets(), "fonts/DJGROSS.ttf");
        } catch (Exception e) {
        }

//        mHelper = new IabHelper(this, Constants.key);
//
//        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
//            public void onIabSetupFinished(IabResult result) {
//                if (!result.isSuccess()) {
//                    // Oh no, there was a problem.
//                    RootTools.log("Problem setting up In-app Billing: " + result);
//                } else {
//                    RootTools.log("IAB is set up");
//                }
//                // Hooray, IAB is fully set up!
//            }
//        });
    }

    @Override
    public void onDestroy()
    {
        Common.cleanExtractedBusybox();
        AsyncJob.cancelAllJobs(this);
        super.onDestroy();

//        try
//        {
//            if (mHelper != null)
//            {
//                mHelper.dispose();
//            }
//            mHelper = null;
//        } catch (Exception e) {}
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // We do nothing here. We're only handling this to keep orientation
        // or keyboard hiding from causing the WebView activity to restart.
    }

    public void initiatePopupWindow(String text, boolean endApplication,
            Activity context) {
        initiatePopupWindow(text, endApplication, context, null, -1);
    }

    public void initiatePopupWindow(String text, boolean endApplication,
                                    Activity context, final PopupCallback popupCallback, final int choice) {

        if (pw != null && pw.isShowing())
            pw.dismiss();

        if (!context.isFinishing()) {
            this.endApplication = endApplication;

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.popupwindow, null);
            pw = new PopupWindow(layout, LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);

            if(null != popupCallback)
            {
                pw.setOnDismissListener(new PopupWindow.OnDismissListener()
                {
                    @Override
                    public void onDismiss()
                    {
                        popupCallback.popupDismissed(choice);
                    }
                });
            }

            context.findViewById(R.id.pop).post(new Runnable() {
                public void run() {
                    pw.showAtLocation(findViewById(R.id.pop), Gravity.CENTER, 0, 0);
                }
            });

            TextView header = (TextView) layout.findViewById(R.id.header_main);
            header.setTypeface(tf);

            TextView textView = (TextView) layout.findViewById(R.id.content);
            textView.setText(text);
        }
    }

    public void makeChoice(final Choice choice, final int id, int title, int content, int positive, int negative) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        choice.choiceMade(true, id);
                    }
                });

        if(-1 != negative) {
            builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    choice.choiceMade(false, id);
                }
            });
        }

        builder.show();
    }

    public String[] configureSpinner() {

        String[] busyboxLocation;

        busyboxLocation = Common.findBusyBoxLocations(false, false);

        Set<String> tmpSet = new HashSet<String>();
        if (busyboxLocation != null) {
            for (String locations : busyboxLocation) {
                tmpSet.add(locations);
            }
            tmpSet.add("/system/bin/");
            tmpSet.add("/system/xbin/");

            busyboxLocation = new String[tmpSet.size() + 1];
            int count = 0;
            for (String locations : tmpSet) {
                busyboxLocation[count] = locations;
                count++;
            }

            busyboxLocation[tmpSet.size()] = getString(R.string.custompath);

            RootTools.log("Count " + busyboxLocation.length);

        } else {
            busyboxLocation = new String[3];
            busyboxLocation[0] = "/system/bin/";
            busyboxLocation[1] = "/system/xbin/";
            busyboxLocation[2] = "Custom Path";
        }

        return busyboxLocation;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            if (pw != null && pw.isShowing()) {
                close(new View(this));
            } else {
                finish();
                randomAnimation();
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void randomAnimation() {
        Random random = new Random();
        switch (random.nextInt(3)) {
            case 0:
                overridePendingTransition(this, R.anim.enter_scalein,
                        R.anim.exit_slideout);
                break;
            case 1:
                overridePendingTransition(this, R.anim.enter_dropin,
                        R.anim.exit_dropout);
                break;
            case 2:
                overridePendingTransition(this, R.anim.enter_slidein,
                        R.anim.exit_slideout);
                break;
        }
    }

    public void close(View v) {
        pw.dismiss();
        if (endApplication) {
            finish();
            randomAnimation();
        }
    }

    private static Method overridePendingTransition;

    static {
        try {
            overridePendingTransition = Activity.class
                    .getMethod(
                            "overridePendingTransition", new Class[]{Integer.TYPE, Integer.TYPE}); //$NON-NLS-1$
        } catch (NoSuchMethodException e) {
            overridePendingTransition = null;
        }
    }

    /**
     * Calls Activity.overridePendingTransition if the method is available
     * (>=Android 2.0)
     *
     * @param activity  the activity that launches another activity
     * @param animEnter the entering animation
     * @param animExit  the exiting animation
     */
    public static void overridePendingTransition(Activity activity,
                                                 int animEnter, int animExit) {
        if (overridePendingTransition != null) {
            try {
                overridePendingTransition.invoke(activity, animEnter, animExit);
            } catch (Exception e) {
                // do nothing
            }
        }
    }

    public void debug(View v) {
        RootTools.debugMode = !RootTools.debugMode;
        Toast.makeText(this, "Debug is " + RootTools.debugMode, Toast.LENGTH_LONG).show();
    }
}
