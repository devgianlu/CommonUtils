package com.gianlu.commonutils.Drawer;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.FontsManager;
import com.gianlu.commonutils.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MenuItemsAdapter<E extends Enum> extends RecyclerView.Adapter<MenuItemsAdapter.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<BaseDrawerItem<E>> items;
    private final Listener<E> listener;
    private final int colorTextPrimary;
    private final int colorAccent;
    private final Drawable selectableItemBackground;

    MenuItemsAdapter(@NonNull Context context, List<BaseDrawerItem<E>> items, Listener<E> listener) {
        this.inflater = LayoutInflater.from(context);
        this.items = items;
        this.listener = listener;
        this.colorTextPrimary = CommonUtils.resolveAttrAsColor(context, android.R.attr.textColorPrimary);
        this.colorAccent = ContextCompat.getColor(context, R.color.colorAccent);
        this.selectableItemBackground = CommonUtils.resolveAttrAsDrawable(context, android.R.attr.selectableItemBackground);
    }

    @Override
    @NonNull
    public MenuItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemsAdapter.ViewHolder holder, int position) {
        final BaseDrawerItem<E> item = items.get(position);

        holder.icon.setImageResource(item.icon);
        holder.name.setText(item.name);

        if (item.badgeNumber >= 0) {
            holder.badge.setVisibility(View.VISIBLE);
            holder.badge.setText(String.valueOf(item.badgeNumber));
        } else {
            holder.badge.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onMenuItemSelected(item);
        });

        if (item.active) {
            holder.name.setTextColor(colorAccent);
            holder.itemView.setBackgroundResource(R.drawable.item_drawer_active);
            holder.icon.setImageTintList(ColorStateList.valueOf(colorAccent));
        } else {
            holder.name.setTextColor(colorTextPrimary);
            holder.itemView.setBackground(selectableItemBackground);
            holder.icon.setImageTintList(ColorStateList.valueOf(colorTextPrimary));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private int indexOf(E which) {
        for (int i = 0; i < items.size(); i++)
            if (items.get(i).id == which)
                return i;

        return -1;
    }

    @UiThread
    void updateBadge(E which, int badgeNumber) {
        int pos = indexOf(which);
        if (pos != -1) {
            BaseDrawerItem item = items.get(pos);
            if (item.badgeNumber != badgeNumber) {
                item.badgeNumber = badgeNumber;
                notifyItemChanged(pos);
            }
        }
    }

    @UiThread
    void setActiveItem(E which) {
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

    public interface Listener<E extends Enum> {
        void onMenuItemSelected(@NonNull BaseDrawerItem<E> which);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView icon;
        final TextView name;
        final TextView badge;

        public ViewHolder(@NonNull ViewGroup parent) {
            super(inflater.inflate(R.layout.item_drawer, parent, false));

            icon = itemView.findViewById(R.id.drawerItem_icon);
            name = itemView.findViewById(R.id.drawerItem_name);
            FontsManager.set(name, FontsManager.ROBOTO_BOLD);
            badge = itemView.findViewById(R.id.drawerItem_badge);
        }
    }
}
