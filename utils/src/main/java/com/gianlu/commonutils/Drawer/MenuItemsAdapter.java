package com.gianlu.commonutils.Drawer;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.R;

import java.util.List;

@SuppressWarnings({"unused", "WeakerAccess"})
class MenuItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_NORMAL = 0;
    private static final int ITEM_SEPARATOR = 1;
    private final LayoutInflater inflater;
    private final List<BaseDrawerItem> menuItems;
    private final int badge;
    private final int separator;
    private final Context context;
    private IAdapter listener;

    MenuItemsAdapter(Context context, List<BaseDrawerItem> menuItems, @DrawableRes int badge, @ColorRes int separator, IAdapter listener) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.menuItems = menuItems;
        this.badge = badge;
        this.separator = separator;
        this.listener = listener;
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEPARATOR) return new SeparatorViewHolder(context, separator);
        else return new ViewHolder(inflater, parent);
    }

    @Override
    public int getItemViewType(int position) {
        if (menuItems.get(position) == null) return ITEM_SEPARATOR;
        else return ITEM_NORMAL;
    }

    void setDrawerListener(IAdapter listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder castHolder = (ViewHolder) holder;
            final BaseDrawerItem item = menuItems.get(position);

            castHolder.icon.setImageResource(item.icon);
            castHolder.name.setText(item.name);

            if (item.badgeNumber != -1) {
                castHolder.badgeContainer.setVisibility(View.VISIBLE);
                castHolder.badgeContainer.setBackgroundResource(badge);
                castHolder.badge.setText(String.valueOf(item.badgeNumber));
            } else {
                castHolder.badgeContainer.setVisibility(View.GONE);
            }

            castHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onMenuItemSelected(item);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    private int indexOf(int id) {
        for (int i = 0; i < menuItems.size(); i++)
            if (menuItems.get(i) != null && menuItems.get(i).id == id)
                return i;

        return -1;
    }

    void updateBadge(int which, int badgeNumber) {
        int pos = indexOf(which);

        if (pos != -1 && menuItems.get(pos).badgeNumber != badgeNumber) {
            menuItems.get(pos).badgeNumber = badgeNumber;
            notifyItemChanged(pos);
        }
    }

    interface IAdapter {
        void onMenuItemSelected(BaseDrawerItem which);
    }

    static class SeparatorViewHolder extends RecyclerView.ViewHolder {
        SeparatorViewHolder(Context context, @ColorRes int separator) {
            super(getSeparator(context, separator));
        }

        static View getSeparator(Context context, @ColorRes int separator) {
            View view = new View(context);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics())));
            view.setBackgroundResource(separator);
            return view;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView icon;
        final TextView name;
        final LinearLayout badgeContainer;
        final TextView badge;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.drawer_item_primary, parent, false));
            itemView.setBackground(CommonUtils.resolveAttrAsDrawable(parent.getContext(), R.attr.selectableItemBackground));

            icon = itemView.findViewById(R.id.drawerItem_icon);
            name = itemView.findViewById(R.id.drawerItem_name);
            badgeContainer = itemView.findViewById(R.id.drawerItem_badgeContainer);
            badge = itemView.findViewById(R.id.drawerItem_badge);
        }
    }
}
