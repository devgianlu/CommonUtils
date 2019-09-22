package com.gianlu.commonutils.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.gianlu.commonutils.R;

import java.util.List;

public class GeneralItemsAdapter<E extends GeneralItemsAdapter.Item> extends BaseAdapter {
    private final List<E> items;
    private final LayoutInflater inflater;
    private final OnDelete<E> listener;

    public GeneralItemsAdapter(Context context, List<E> items, @Nullable OnDelete<E> listener) {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public E getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_deletable, parent, false);
        final E item = getItem(position);

        LinearLayout texts = (LinearLayout) ((ViewGroup) convertView).getChildAt(0);

        TextView primary = (TextView) texts.getChildAt(0);
        primary.setText(item.getPrimaryText());

        TextView secondary = (TextView) texts.getChildAt(1);
        String secondaryText = item.getSecondaryText();
        if (secondaryText == null) {
            secondary.setVisibility(View.GONE);
        } else {
            secondary.setVisibility(View.VISIBLE);
            secondary.setText(secondaryText);
        }

        ImageButton delete = (ImageButton) ((ViewGroup) convertView).getChildAt(1);
        if (listener == null) {
            delete.setVisibility(View.GONE);
        } else {
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(v -> {
                listener.delete(item);
                items.remove(position);
                notifyDataSetChanged();
            });
        }

        return convertView;
    }

    public interface Item {
        String getPrimaryText();

        @Nullable
        String getSecondaryText();
    }

    public interface OnDelete<E> {
        void delete(E element);
    }
}
