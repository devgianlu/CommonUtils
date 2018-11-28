package com.gianlu.commonutils.Spinners;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gianlu.commonutils.GetText;
import com.gianlu.commonutils.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LabeledSpinner extends LinearLayout {
    private final TextView label;
    private final TextView selected;
    private final int mDropdownTextColor;
    private Adapter adapter;
    private SelectListener selectListener;
    private PopupWindow popupWindow;

    public LabeledSpinner(Context context) {
        this(context, null, 0);
    }

    public LabeledSpinner(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LabeledSpinner(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(context, R.layout.view_labeled_spinner, this);

        this.label = findViewById(R.id.labeledSpinner_label);
        this.selected = findViewById(R.id.labeledSpinner_selected);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LabeledSpinner, 0, 0);
        int textColor;
        try {
            setLabel(a.getString(R.styleable.LabeledSpinner_labelText));
            textColor = a.getColor(R.styleable.LabeledSpinner_textColor, 0);
            mDropdownTextColor = a.getColor(R.styleable.LabeledSpinner_dropdownTextColor, 0);
        } finally {
            a.recycle();
        }

        if (textColor != 0) {
            label.setTextColor(textColor);
            selected.setTextColor(textColor);

            ImageView icon = findViewById(R.id.labeledSpinner_icon);
            icon.setImageTintList(ColorStateList.valueOf(textColor));
        }

        setOnClickListener(v -> togglePopup());
    }

    @NonNull
    private static String getText(@NonNull Context context, @NonNull Object item) {
        if (item instanceof GetText) return ((GetText) item).getText(context);
        else return item.toString();
    }

    private boolean dismissPopup() {
        if (popupWindow != null) {
            popupWindow.setOnDismissListener(null);
            popupWindow.dismiss();
            popupWindow = null;
            return true;
        }

        return false;
    }

    private void togglePopup() {
        if (dismissPopup()) return;
        if (adapter == null) throw new IllegalStateException("Adapter not attached!");

        popupWindow = new PopupWindow(adapter.getDropdownView(), ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        popupWindow.setOnDismissListener(this::dismissPopup);
        popupWindow.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));
        popupWindow.showAsDropDown(this);
    }

    public void setStringItems(List<String> items, @NonNull String selected) {
        this.adapter = new Adapter(getContext(), items);
        setSelected(selected, false);
    }

    public void setNumberItems(List<? extends Number> items, @NonNull Number selected) {
        this.adapter = new Adapter(getContext(), items);
        setSelected(selected, false);
    }

    public <A extends GetText> void setItems(List<A> items, @NonNull A selected) {
        this.adapter = new Adapter(getContext(), items);
        setSelected(selected, false);
    }

    @SuppressWarnings("unchecked")
    private void setSelected(Object item, boolean notify) {
        if (adapter != null) {
            adapter.setSelectedItem(item);
            selected.setText(getText(getContext(), item));

            if (notify) {
                if (selectListener != null)
                    selectListener.selected(item);
            }
        }
    }

    public void setOnItemSelectedListener(@NonNull final SelectListener<?> listener) {
        this.selectListener = listener;
    }

    public void setLabel(String str) {
        this.label.setText(str);
    }

    @SuppressWarnings("unchecked")
    public <A> A getSelectedItem() {
        return adapter == null ? null : (A) adapter.getSelectedItem();
    }

    public void setSelectedItem(GetText text, boolean notify) {
        setSelected(text, notify);
    }

    public void setSelectedItem(String text, boolean notify) {
        setSelected(text, notify);
    }

    public void setSelectedItem(Number num, boolean notify) {
        setSelected(num, notify);
    }

    public interface SelectListener<A> {
        void selected(@NonNull A item);
    }

    private class Adapter {
        private final List<?> items;
        private final LayoutInflater inflater;
        private final Context context;
        private int selectedIndex = 0;
        private View dropdownView;

        private Adapter(Context context, List<?> items) {
            this.items = items;
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        private void invalidate() {
            dropdownView = null;
        }

        @NonNull
        private View getView(int position, ViewGroup parent) {
            final Object item = items.get(position);

            TextView text = (TextView) inflater.inflate(R.layout.item_labeled_spinner, parent, false);
            text.setText(getText(context, item));
            text.setOnClickListener(v -> {
                setSelected(item, true);
                dismissPopup();
            });

            if (position == selectedIndex)
                text.setTypeface(Typeface.DEFAULT_BOLD);

            if (mDropdownTextColor != 0)
                text.setTextColor(mDropdownTextColor);

            return text;
        }

        private Object getSelectedItem() {
            return items.get(selectedIndex);
        }

        private void setSelectedItem(Object selected) {
            selectedIndex = items.indexOf(selected);
            invalidate();
        }

        @NonNull
        private View getDropdownView() {
            if (dropdownView == null) {
                ScrollView scrollView = new ScrollView(context);
                LinearLayout linearLayout = new LinearLayout(context);
                scrollView.addView(linearLayout);
                linearLayout.setOrientation(VERTICAL);
                for (int i = 0; i < items.size(); i++)
                    linearLayout.addView(getView(i, linearLayout));
                return dropdownView = scrollView;
            } else {
                return dropdownView;
            }
        }
    }
}
