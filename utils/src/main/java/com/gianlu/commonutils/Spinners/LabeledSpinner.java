package com.gianlu.commonutils.Spinners;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.gianlu.commonutils.R;

import java.util.ArrayList;
import java.util.List;

public class LabeledSpinner extends LinearLayout {
    private final TextView label;
    private final Spinner spinner;
    private final int mLabelTextColor;
    private final int mDropdownTextColor;

    public LabeledSpinner(Context context) {
        this(context, null, 0);
    }

    public LabeledSpinner(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LabeledSpinner(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);

        this.label = new TextView(context);
        addView(label);

        this.spinner = new Spinner(context);
        addView(spinner);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LabeledSpinner, 0, 0);
        try {
            setLabel(a.getString(R.styleable.LabeledSpinner_labelText));
            mLabelTextColor = a.getColor(R.styleable.LabeledSpinner_labelTextColor, 0);
            mDropdownTextColor = a.getColor(R.styleable.LabeledSpinner_dropdownTextColor, 0);
        } finally {
            a.recycle();
        }

        if (mLabelTextColor != 0) {
            label.setTextColor(mLabelTextColor);
            spinner.setBackgroundTintList(ColorStateList.valueOf(mLabelTextColor));
        }
    }

    @NonNull
    private static String getText(@NonNull Context context, @NonNull Object item) {
        if (item instanceof GetText) return ((GetText) item).getText(context);
        else return item.toString();
    }

    public void setStringItems(List<String> items) {
        this.spinner.setAdapter(new Adapter(getContext(), items));
    }

    public void setNumberItems(List<? extends Number> items) {
        this.spinner.setAdapter(new Adapter(getContext(), items));
    }

    public void setItems(List<? extends GetText> items) {
        this.spinner.setAdapter(new Adapter(getContext(), items));
    }

    @SuppressWarnings("unchecked")
    public <A> void setOnItemSelectedListener(@NonNull final SelectListener<A> listener) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                A item = (A) parent.getItemAtPosition(position);
                listener.selected(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void setLabel(String str) {
        this.label.setText(str);
    }

    public interface GetText {
        @NonNull
        String getText(@NonNull Context context);
    }

    public interface SelectListener<A> {
        void selected(@NonNull A item);
    }

    private class Adapter implements SpinnerAdapter {
        private final List<?> items;
        private final LayoutInflater inflater;
        private final List<ViewHolder> dropdownViewHolders;
        private final List<ViewHolder> viewHolders;
        private final Context context;

        private Adapter(Context context, List<?> items) {
            this.items = items;
            this.context = context;
            this.viewHolders = new ArrayList<>(items.size());
            this.dropdownViewHolders = new ArrayList<>(items.size());
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getView(position, parent, true);
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
        }

        @Override
        public final int getCount() {
            return items.size();
        }

        @Override
        public final Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public final long getItemId(int position) {
            return items.get(position).hashCode();
        }

        @Override
        public final boolean hasStableIds() {
            return true;
        }

        private View getView(int position, ViewGroup parent, boolean dropdown) {
            List<ViewHolder> list = dropdown ? dropdownViewHolders : viewHolders;

            ViewHolder holder = list.size() <= position ? null : list.get(position);
            if (holder == null) {
                holder = new ViewHolder(parent, dropdown);
                list.add(position, holder);
            }

            holder.text.setText(getText(context, getItem(position)));

            return holder.text;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getView(position, parent, false);
        }

        @Override
        public final int getItemViewType(int position) {
            return 0;
        }

        @Override
        public final int getViewTypeCount() {
            return 1;
        }

        @Override
        public final boolean isEmpty() {
            return items.isEmpty();
        }

        private class ViewHolder {
            private final TextView text;

            public ViewHolder(ViewGroup parent, boolean dropdown) {
                text = (TextView) inflater.inflate(R.layout.item_labeled_spinner, parent, false);

                if (dropdown) {
                    if (mDropdownTextColor != 0)
                        text.setTextColor(mDropdownTextColor);
                } else {
                    if (mLabelTextColor != 0)
                        text.setTextColor(mLabelTextColor);
                }
            }
        }
    }
}
