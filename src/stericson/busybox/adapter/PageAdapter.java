package stericson.busybox.adapter;

import stericson.busybox.App;
import stericson.busybox.Constants;
import stericson.busybox.R;
import stericson.busybox.Activity.MainActivity;
import stericson.busybox.custom.FontableTextView;
import stericson.busybox.listeners.Location;
import stericson.busybox.listeners.Version;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.viewpagerindicator.TitleProvider;

public class PageAdapter extends PagerAdapter implements TitleProvider {
    private Spinner version;
    private Spinner path;
    private static FontableTextView foundAt;

    private static String[] titles = new String[]
            {
                    "Applet Manager",
                    "Install Busybox",
                    "About BusyBox"
            };

    private final MainActivity context;

    public PageAdapter(MainActivity context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object instantiateItem(final View pager, int position) {
        View view = null;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (position == 0) {
            if (App.getInstance().getItemList() != null) {
                view = inflater.inflate(R.layout.generic_list, null);
                ((ViewPager) pager).addView(view);
                context.setListView((ListView) view.findViewById(R.id.list));
                ListView listView = context.getListView();

                listView.setAdapter(new AppletAdapter(context));
                listView.setCacheColorHint(0);
            } else {
                view = inflater.inflate(R.layout.progress, null);
                ((ViewPager) pager).addView(view);
                context.view1 = (TextView) view.findViewById(R.id.text);
            }
        } else if (position == 1) {
            if (App.getInstance().getItemList() != null) {
                view = inflater.inflate(R.layout.generic_list, null);
                ((ViewPager) pager).addView(view);
                context.setListView((ListView) view.findViewById(R.id.list));
                ListView listView = context.getListView();

                listView.setAdapter(new TuneAdapter(context));
                listView.setCacheColorHint(0);
            } else {
                view = inflater.inflate(R.layout.main_content, null);
                ((ViewPager) pager).addView(view);

                foundAt = (FontableTextView) view.findViewById(R.id.foundat);

                foundAt.setText("Loading...");

                version = (Spinner) view.findViewById(R.id.busyboxversiontobe);
                ArrayAdapter<String> versionAdapter = new ArrayAdapter<String>(context, R.layout.simple_spinner_item, Constants.versions);
                versionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                version.setOnItemSelectedListener(new Version(context));
                version.setAdapter(versionAdapter);

                path = (Spinner) view.findViewById(R.id.path);
                ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(context, R.layout.simple_spinner_item, Constants.locations);
                locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                path.setAdapter(locationAdapter);
                path.setOnItemSelectedListener(new Location(context));
                path.setSelection(App.getInstance().getPathPosition());


                context.setFreeSpace((FontableTextView) view.findViewById(R.id.freespace));

                TextView freeSpace = context.getFreeSpace();
                float freeSpaceValue = App.getInstance().getSpace();
                boolean isCustomInstall = App.getInstance().isCustomInstallPath();
                String installPath = App.getInstance().getInstallPath();
                String message = "";

                if(installPath.length() > 0)
                {
                    message = freeSpaceValue > 0 ? context.getString(R.string.amount) + " " + (isCustomInstall ? "/system" : installPath) + " " + freeSpaceValue + "mb" : context.getString(R.string.space_undetermined) + " " + installPath;
                }

                freeSpace.setText(message);

                context.view2 = (TextView) view.findViewById(R.id.text);
            }
        } else if (position == 2) {
            view = new ScrollView(context);
            FontableTextView tv = new FontableTextView(context);
            ((ScrollView) view).addView(tv);
            ((ViewPager) pager).addView(view);

            tv.setText(context.getString(R.string.about));
            Linkify.addLinks(tv, Linkify.ALL);
        } else if (position == 3) {

        }

        return view;
    }

    @Override
    public void destroyItem(View pager, int position, Object view) {
        ((ViewPager) pager).removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void finishUpdate(View view) {
    }

    @Override
    public void restoreState(Parcelable p, ClassLoader c) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View view) {
    }

    public String getTitle(int position) {
        return titles[position];
    }

    public static void updateBusyboxInformation() {
        try {
            foundAt.setText(App.getInstance().getFound());
        } catch (Exception ignore) {}
    }
}
