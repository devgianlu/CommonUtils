package com.gianlu.commonutils.Drawer;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.FontsManager;
import com.gianlu.commonutils.R;

import java.util.List;

public class MenuItemsAdapter extends RecyclerView.Adapter<MenuItemsAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<BaseDrawerItem> items;
    private final Listener listener;
    private final Typeface roboto;
    private final Typeface robotoBold;
    private final Context context;

    MenuItemsAdapter(@NonNull Context context, List<BaseDrawerItem> items, Listener listener) {
        this.inflater = LayoutInflater.from(context);
        this.items = items;
        this.context = context;
        this.listener = listener;
        this.roboto = FontsManager.get().get(context, FontsManager.ROBOTO_REGULAR);
        this.robotoBold = FontsManager.get().get(context, FontsManager.ROBOTO_BOLD);
    }

    @Override
    @NonNull
    public MenuItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemsAdapter.ViewHolder holder, int position) {
        final BaseDrawerItem item = items.get(position);

        holder.icon.setImageResource(item.icon);
        holder.name.setText(item.name);

        if (item.badgeNumber >= 0) {
            holder.badge.setVisibility(View.VISIBLE);
            holder.badge.setBackgroundResource(R.drawable.drawer_badge);
            holder.badge.setText(String.valueOf(item.badgeNumber));
        } else {
            holder.badge.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onMenuItemSelected(item);
            }
        });

        if (item.active) {
            int accent = ContextCompat.getColor(context, R.color.colorAccent);
            holder.name.setTextColor(accent);
            holder.name.setTypeface(robotoBold);
            holder.icon.setImageTintList(ColorStateList.valueOf(accent));
        } else {
            int primary = CommonUtils.resolveAttrAsColor(context, android.R.attr.textColorPrimary);
            holder.name.setTextColor(primary);
            holder.name.setTypeface(roboto);
            holder.icon.setImageTintList(ColorStateList.valueOf(primary));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private int indexOf(int id) {
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).id == id)
                return i;

        return -1;
    }

    public void updateBadge(int which, int badgeNumber) {
        int pos = indexOf(which);
        if (pos != -1) {
            BaseDrawerItem item = items.get(pos);
            if (item.badgeNumber != badgeNumber) {
                item.badgeNumber = badgeNumber;
                notifyItemChanged(pos);
            }
        }
    }

    public void setActiveItem(int which) {
        int pos = indexOf(which);
        for (int i = 0; i < getItemCount(); i++) {
            BaseDrawerItem item = items.get(i);
            boolean active = i == pos;
            if (item.active != active) {
                item.active = active;
                notifyItemChanged(pos);
            }
        }
    }

    public interface Listener {
        void onMenuItemSelected(@NonNull BaseDrawerItem which);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView icon;
        final TextView name;
        final TextView badge;

        public ViewHolder(ViewGroup parent) {
            super(inflater.inflate(R.layout.drawer_item_primary, parent, false));
            itemView.setBackground(CommonUtils.resolveAttrAsDrawable(parent.getContext(), R.attr.selectableItemBackground));

            icon = itemView.findViewById(R.id.drawerItem_icon);
            name = itemView.findViewById(R.id.drawerItem_name);
            badge = itemView.findViewById(R.id.drawerItem_badge);
        }
    }
}
