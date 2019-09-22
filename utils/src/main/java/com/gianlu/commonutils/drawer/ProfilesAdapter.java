package com.gianlu.commonutils.drawer;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class ProfilesAdapter<P extends BaseDrawerProfile, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected final Context context;
    protected final List<P> profiles;
    protected final DrawerManager.ProfilesDrawerListener<P> listener;
    private RecyclerView list;

    public ProfilesAdapter(@NonNull Context context, @NonNull List<P> profiles, @NonNull DrawerManager.ProfilesDrawerListener<P> listener) {
        this.context = context.getApplicationContext();
        this.profiles = profiles;
        this.listener = listener;
    }

    public final void startProfilesTest() {
        for (int i = 0; i < profiles.size(); i++) {
            if (i == profiles.size() - 1) runTest(i);
            else runTest(i);
        }
    }

    protected abstract P getItem(int pos);

    protected void runTest(int pos) {
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        list = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        list = null;
    }

    protected final void post(@NonNull Runnable action) {
        if (list != null) list.post(action);
    }

    public final void onBindViewHolder(@NonNull VH holder, int position) {
        final P profile = getItem(position);

        onBindViewHolder(holder, position, profile);

        holder.itemView.setOnClickListener(v -> listener.onDrawerProfileSelected(profile));

        holder.itemView.setOnLongClickListener(v -> listener.onDrawerProfileLongClick(profile));
    }

    protected abstract void onBindViewHolder(@NonNull VH holder, int position, @NonNull P profile);

    @Override
    public final int getItemCount() {
        return profiles.size();
    }
}
