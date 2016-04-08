package stericson.busybox.adapter;

import stericson.busybox.App;
import stericson.busybox.Constants;
import stericson.busybox.R;
import stericson.busybox.Activity.MainActivity;
import stericson.busybox.custom.FontableTextView;
import stericson.busybox.jobs.containers.Item;
import stericson.busybox.listeners.AppletLongClickListener;
import stericson.busybox.listeners.Location;
import stericson.busybox.listeners.Version;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class TuneAdapter extends BaseAdapter {
    private int[] colors = new int[]{0xff303030, 0xff404040};
    private MainActivity activity;
    private static LayoutInflater inflater = null;
    private CheckBox all;

    public TuneAdapter(MainActivity activity) {
        this.activity = activity;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        App.getInstance().setTuneadapter(this);
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    private class InformationHolder {
        private Spinner path;
        private Spinner version;
        private FontableTextView foundAt;
        private LinearLayout options;
        private FontableTextView status;
        private LinearLayout container;
        private CheckBox clean;
        private CheckBox smartInstall;
        private FontableTextView freeSpace;
    }

    private class ViewHolder {
        private RelativeLayout container;
        private CheckBox appletCheck;
        private CheckBox appletDecision;
        private TextView appletState;
        private TextView appletStatus;
        private TextView appletSymlinkedTo;
        private TextView appletRecomendation;
    }

    private ViewHolder getHolder(View convertView, int position) {
        if (convertView == null) {
            if (position != 0) {
                return new ViewHolder();
            }
        } else {
            if (convertView.getTag() != null) {
                if (position != 0) {
                    return ((ViewHolder) convertView.getTag());
                }
            }
        }

        return null;
    }

    private InformationHolder getInformationHolder(View convertView, int position) {
        if (convertView == null) {
            if (position == 0) {
                return new InformationHolder();
            }
        } else {
            if (convertView.getTag() != null) {
                if (position == 0) {
                    return ((InformationHolder) convertView.getTag());
                }
            }
        }

        return null;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final App app = App.getInstance();
        final ViewHolder holder = getHolder(convertView, position);
        final InformationHolder info_holder = getInformationHolder(convertView, position);


        if (convertView == null) {
            if (position == 0) {
                vi = inflater.inflate(R.layout.main_content, null);

                //Set everything up
                info_holder.foundAt = (FontableTextView) vi.findViewById(R.id.foundat);
                info_holder.version = (Spinner) vi.findViewById(R.id.busyboxversiontobe);
                info_holder.path = (Spinner) vi.findViewById(R.id.path);
                info_holder.freeSpace = (FontableTextView) vi.findViewById(R.id.freespace);
                info_holder.container = (LinearLayout) vi.findViewById(R.id.custom_container);
                info_holder.status = (FontableTextView) vi.findViewById(R.id.customtune_status);
                info_holder.options = (LinearLayout) vi.findViewById(R.id.smart_install_options);
                info_holder.clean = (CheckBox) vi.findViewById(R.id.clean_all);
                info_holder.smartInstall = (CheckBox) vi.findViewById(R.id.enable_smart);
                all = (CheckBox) vi.findViewById(R.id.sym_all);

                //assign values, turn things on, hide what we don't need.
                info_holder.foundAt.setText(App.getInstance().getFound());

                ArrayAdapter<String> versionAdapter = new ArrayAdapter<String>(activity, R.layout.simple_spinner_item, Constants.versions);
                versionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                info_holder.version.setAdapter(versionAdapter);
                info_holder.version.setOnItemSelectedListener(new Version(activity));
                info_holder.version.setSelection(App.getInstance().getVersionPosition());

                ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(activity, R.layout.simple_spinner_item, Constants.locations);
                locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                info_holder.path.setAdapter(locationAdapter);
                info_holder.path.setOnItemSelectedListener(new Location(activity));
                info_holder.path.setSelection(App.getInstance().getPathPosition());

                this.setFreeSpace(info_holder);

                info_holder.container.setVisibility(View.GONE);

                info_holder.status.setVisibility(app.isToggled() ? View.VISIBLE : View.GONE);
                info_holder.status.setText(R.string.advanced);

                info_holder.options.setVisibility(app.isToggled() ? View.VISIBLE : View.GONE);

                all.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {

                        activity.initiatePopupWindow(activity.getString(R.string.replaceall), false, activity);
                        all.setChecked(false);

                    }
                });

                info_holder.clean = (CheckBox) vi.findViewById(R.id.clean_all);

                info_holder.clean.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {

                        activity.initiatePopupWindow(activity.getString(R.string.cleanall), false, activity);
                        info_holder.clean.setChecked(false);

                    }
                });

                info_holder.smartInstall
                        .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                            public void onCheckedChanged(
                                    CompoundButton buttonView, boolean isChecked) {
                            activity.initiatePopupWindow(activity.getString(R.string.proonly_smartInstall), false, activity);
                            info_holder.smartInstall.setChecked(false);

                            }
                        });

                vi.setTag(info_holder);
            } else {
                vi = inflater.inflate(R.layout.list_item, null);

                holder.container = (RelativeLayout) vi.findViewById(R.id.container);
                holder.container.setClickable(true);
                holder.container.setOnLongClickListener(new AppletLongClickListener(this.activity));

                holder.appletCheck = (CheckBox) vi.findViewById(R.id.appletCheck);

                holder.appletDecision = (CheckBox) vi.findViewById(R.id.appletDecision);

                holder.appletState = (TextView) vi.findViewById(R.id.appletState);

                holder.appletStatus = (TextView) vi.findViewById(R.id.appletStatus);

                holder.appletSymlinkedTo = (TextView) vi.findViewById(R.id.appletSymlinkedTo);

                holder.appletRecomendation = (TextView) vi.findViewById(R.id.appletRecommendation);

                vi.setTag(holder);
            }
        }

        if (position == 0) {
            info_holder.foundAt.setText(App.getInstance().getFound());

            this.setFreeSpace(info_holder);

            info_holder.foundAt.setText(App.getInstance().getFound());

            info_holder.options.setVisibility(app.isToggled() ? View.VISIBLE : View.GONE);

            info_holder.status.setVisibility(!app.isToggled() ? View.VISIBLE : View.GONE);
            info_holder.status.setText(R.string.advanced);

        } else {
            final int modified_position = position - 1;

            final Item item = app.getItemList().get(modified_position);

            holder.appletDecision.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    item.setRemove(!isChecked);

                    if (isChecked) {
                        holder.appletDecision.setText(R.string.leaveApplet);
                        holder.appletDecision.setTextColor(Color.GREEN);
                    } else {
                        holder.appletDecision.setText(R.string.removeApplet);
                        holder.appletDecision.setTextColor(Color.RED);
                    }
                }
            });

            holder.appletCheck.setText(item.getApplet());
            holder.appletCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    if (buttonView.isPressed()) {
                        all.setChecked(false);
                        item.setOverwrite(isChecked);

                        if (isChecked) {
                            holder.appletState.setText(R.string.willSymlink);
                            holder.appletState.setTextColor(Color.GREEN);

                            holder.appletDecision.setVisibility(View.GONE);
                        } else {
                            holder.appletState.setText(R.string.willNotSymlink);
                            holder.appletState.setTextColor(Color.RED);

                            holder.appletDecision.setVisibility(View.VISIBLE);

                            if (item.getRemove()) {
                                holder.appletDecision.setText(R.string.removeApplet);
                                holder.appletDecision.setTextColor(Color.RED);
                                holder.appletDecision.setChecked(false);
                            } else {
                                holder.appletDecision.setText(R.string.leaveApplet);
                                holder.appletDecision.setTextColor(Color.GREEN);
                                holder.appletDecision.setChecked(true);
                            }

                        }
                    }
                }
            });

            holder.appletCheck.setChecked(item.getOverWrite());

            if (holder.appletCheck.isChecked()) {
                holder.appletState.setText(R.string.willSymlink);
                holder.appletState.setTextColor(Color.GREEN);
                holder.appletDecision.setVisibility(View.GONE);
            } else {
                holder.appletState.setText(R.string.willNotSymlink);
                holder.appletState.setTextColor(Color.RED);
                holder.appletDecision.setVisibility(View.VISIBLE);

                if (item.getRemove()) {
                    holder.appletDecision.setText(R.string.removeApplet);
                    holder.appletDecision.setTextColor(Color.RED);
                    holder.appletDecision.setChecked(false);
                } else {
                    holder.appletDecision.setText(R.string.leaveApplet);
                    holder.appletDecision.setTextColor(Color.GREEN);
                    holder.appletDecision.setChecked(true);
                }
            }

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

            if (item.getRecommend()) {
                holder.appletRecomendation.setText(R.string.cautionDo);
            } else {
                holder.appletRecomendation.setText(R.string.cautionDoNot);
            }

            if (position % 2 == 0) {
                holder.container.setBackgroundColor(colors[position % 2]);
            } else {
                holder.container.setBackgroundColor(colors[position % 2]);
            }

        }

        return vi;
    }


    public int getCount() {
        if (App.getInstance().isToggled())
            return (App.getInstance().getItemList() != null) ? App.getInstance().getItemList().size() + 1 : 1;
        else
            return 1;
    }

    public int getViewTypeCount() {
        return 2;
    }

    public int getItemViewType(int position) {
        if (position == 0)
            return 0;
        else
            return 1;
    }

    public void setFreeSpace(InformationHolder info_holder)
    {
        TextView freeSpace = info_holder.freeSpace;
        float freeSpaceValue = App.getInstance().getSpace();
        boolean isCustomInstall = App.getInstance().isCustomInstallPath();
        String installPath = App.getInstance().getInstallPath();
        String message = "";

        if(installPath.length() > 0)
        {
            message = freeSpaceValue > 0 ? activity.getString(R.string.amount) + " " + (isCustomInstall ? "/system" : installPath) + " " + freeSpaceValue + "mb" : activity.getString(R.string.space_undetermined) + " " + installPath;
        }

        freeSpace.setText(message);
    }

    public void update() {
        this.notifyDataSetChanged();
    }
}
