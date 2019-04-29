package com.gianlu.commonutils.CasualViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.cardview.widget.CardView;

import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.FontsManager;
import com.gianlu.commonutils.R;
import com.gianlu.commonutils.SuperTextView;

public class ExpandableCardWithTitleView extends CardView {
    private final LinearLayout layout;
    private final SuperTextView title;
    private final ImageButton toggle;
    private boolean hasBody = false;

    public ExpandableCardWithTitleView(@NonNull Context context) {
        this(context, null, 0);
    }

    public ExpandableCardWithTitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableCardWithTitleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        super.addView(layout, -1, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout titleLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.view_exapandable_card_view_title, layout, false);
        title = titleLayout.findViewById(R.id.expandableCardViewTitle_title);
        toggle = titleLayout.findViewById(R.id.expandableCardViewTitle_toggle);
        layout.addView(titleLayout);

        FontsManager.set(title, FontsManager.ROBOTO_BOLD);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (!hasBody) {
            layout.addView(child, index, params);
            toggle.setOnClickListener((v) -> CommonUtils.handleCollapseClick(toggle, child));
            hasBody = true;
        }
    }

    public void setTitle(@StringRes int res) {
        title.setText(res);
    }
}
