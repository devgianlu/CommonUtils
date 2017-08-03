package com.gianlu.commonutils.Drawer;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.R;

import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class ProfilesAdapter<P extends BaseDrawerProfile> extends RecyclerView.Adapter<ProfilesAdapter.ViewHolder> {
    protected final Context context;
    protected final List<P> profiles;
    protected final boolean black;
    private final LayoutInflater inflater;
    private final int colorAccent;
    protected IAdapter<P> listener;

    public ProfilesAdapter(Context context, List<P> profiles, @ColorRes int colorAccent, boolean black, IAdapter<P> listener) {
        this.context = context;
        this.profiles = profiles;
        this.inflater = LayoutInflater.from(context);
        this.colorAccent = colorAccent;
        this.black = black;
        this.listener = listener;
    }

    public List<P> getItems() {
        return profiles;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    public void startProfilesTest() {
        for (int i = 0; i < profiles.size(); i++)
            if (i == profiles.size() - 1) runTest(i);
            else runTest(i);
    }

    protected abstract P getItem(int pos);

    protected abstract void runTest(int pos);

    public void onBindViewHolder(ViewHolder holder, int position) {
        final P profile = getItem(position);

        holder.name.setText(profile.getProfileName(context));
        holder.secondary.setText(profile.getSecondaryText(context));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onProfileSelected(profile);
            }
        });
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public void setDrawerListener(IAdapter<P> listener) {
        this.listener = listener;
    }

    public interface IAdapter<P extends BaseDrawerProfile> {
        void onProfileSelected(P profile);
    }

    public interface IFinished {
        void onFinished();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final ProgressBar loading;
        public final ImageView status;
        public final TextView ping;
        public final TextView globalName;
        public final TextView name;
        public final TextView secondary;

        public ViewHolder(ViewGroup parent) {
            super(inflater.inflate(R.layout.drawer_profile_item, parent, false));
            itemView.setBackground(CommonUtils.resolveAttrAsDrawable(context, R.attr.selectableItemBackground));

            loading = itemView.findViewById(R.id.drawerProfileItem_loading);
            loading.setIndeterminateTintList(ColorStateList.valueOf(ContextCompat.getColor(context, colorAccent)));
            status = itemView.findViewById(R.id.drawerProfileItem_status);
            ping = itemView.findViewById(R.id.drawerProfileItem_ping);
            globalName = itemView.findViewById(R.id.drawerProfileItem_globalName);
            name = itemView.findViewById(R.id.drawerProfileItem_name);
            secondary = itemView.findViewById(R.id.drawerProfileItem_secondary);

            if (!black) {
                ping.setTextColor(Color.WHITE);
                ping.setAlpha(.7f);
                globalName.setTextColor(Color.WHITE);
                name.setTextColor(Color.WHITE);
                secondary.setTextColor(Color.WHITE);
                secondary.setAlpha(.7f);
            }
        }
    }
}
