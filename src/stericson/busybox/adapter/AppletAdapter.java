package stericson.busybox.adapter;

import stericson.busybox.App;
import stericson.busybox.R;
import stericson.busybox.Activity.BaseActivity;
import stericson.busybox.jobs.containers.Item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AppletAdapter extends BaseAdapter {
    private int[] colors = new int[]{0xff303030, 0xff404040};
    private BaseActivity activity;
    private static LayoutInflater inflater = null;

    public AppletAdapter(BaseActivity activity) {
        this.activity = activity;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        App.getInstance().setAppletadapter(this);
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        private RelativeLayout container;
        private TextView applet;
        private TextView appletStatus;
        private TextView appletSymlinkedTo;
        private TextView appletInformation;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        final App app = App.getInstance();

        if (convertView == null) {
            vi = inflater.inflate(R.layout.applet_item, null);
            holder = new ViewHolder();

            holder.container = (RelativeLayout) vi.findViewById(R.id.container);

            holder.applet = (TextView) vi.findViewById(R.id.applet);

            holder.appletStatus = (TextView) vi.findViewById(R.id.appletStatus);

            holder.appletSymlinkedTo = (TextView) vi.findViewById(R.id.appletSymlinkedTo);

            holder.appletInformation = (TextView) vi.findViewById(R.id.appletInformation);

            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        final Item item = app.getItemList().get(position);

        holder.applet.setText(" " + item.getApplet());

        if (item.getFound()) {
            if (!item.getSymlinkedTo().equals("")) {
                holder.appletSymlinkedTo.setVisibility(View.VISIBLE);
                holder.appletStatus.setText(R.string.symlinked);
                holder.appletSymlinkedTo.setText(activity.getString(R.string.symlinkedTo) + " " + item.getSymlinkedTo());
            } else {
                holder.appletSymlinkedTo.setVisibility(View.GONE);
                holder.appletStatus.setText(R.string.hardlinked);
            }
        } else {
            holder.appletStatus.setText(R.string.notFound);
            holder.appletSymlinkedTo.setText(activity.getString(R.string.notsymlinked));
        }

        if (item.getDescription().equals("")) {
            holder.appletInformation.setText(activity.getString(R.string.noInfo));
        } else {
            holder.appletInformation.setText(item.getDescription());
        }

        if (position % 2 == 0) {
            holder.container.setBackgroundColor(colors[position % 2]);
        } else {
            holder.container.setBackgroundColor(colors[position % 2]);
        }

        return vi;
    }

    public int getCount() {
        return (App.getInstance().getItemList() != null) ? App.getInstance().getItemList().size() : 0;
    }

    public void update() {
        this.notifyDataSetChanged();
    }
}
