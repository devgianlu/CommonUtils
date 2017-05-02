package com.gianlu.commonutils.Drawer;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gianlu.commonutils.R;

import java.util.List;

@Keep
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class ProfilesAdapter extends RecyclerView.Adapter<ProfilesAdapter.ViewHolder> {
    protected final Context context;
    protected final List<BaseDrawerProfile> profiles;
    private final LayoutInflater inflater;
    private final int ripple_dark;
    protected IAdapter listener;

    public ProfilesAdapter(Context context, List<BaseDrawerProfile> profiles, @DrawableRes int ripple_dark, IAdapter listener) {
        this.context = context;
        this.profiles = profiles;
        this.inflater = LayoutInflater.from(context);
        this.ripple_dark = ripple_dark;
        this.listener = listener;
    }

    public List<BaseDrawerProfile> getItems() {
        return profiles;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    public void startProfilesTest(@Nullable IFinished handler) {
        for (int i = 0; i < profiles.size(); i++)
            if (i == profiles.size() - 1) runTest(i, handler);
            else runTest(i, null);
    }

    protected abstract BaseDrawerProfile getItem(int pos);

    protected abstract void runTest(int pos, IFinished tester);

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final BaseDrawerProfile profile = getItem(position);

        holder.name.setText(profile.getProfileName());
        holder.secondary.setText(profile.getSecondaryText());

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

    public void setDrawerListener(IAdapter listener) {
        this.listener = listener;
    }

    public interface IAdapter {
        void onProfileSelected(BaseDrawerProfile profile);
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
            itemView.setBackgroundResource(ripple_dark);

            loading = (ProgressBar) itemView.findViewById(R.id.drawerProfileItem_loading);
            status = (ImageView) itemView.findViewById(R.id.drawerProfileItem_status);
            ping = (TextView) itemView.findViewById(R.id.drawerProfileItem_ping);
            globalName = (TextView) itemView.findViewById(R.id.drawerProfileItem_globalName);
            name = (TextView) itemView.findViewById(R.id.drawerProfileItem_name);
            secondary = (TextView) itemView.findViewById(R.id.drawerProfileItem_secondary);
        }
    }
}
