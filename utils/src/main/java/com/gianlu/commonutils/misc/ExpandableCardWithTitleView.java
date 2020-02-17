package com.gianlu.commonutils.misc;

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
import com.gianlu.commonutils.R;

public class ExpandableCardWithTitleView extends CardView {
    private final LinearLayout layout;
    private final SuperTextView title;
    private final ImageButton toggle;
    private View body;
    private ExpandableCardWithTitleView[] exclusiveOpenViews;

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
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (body == null) {
            body = child;
            layout.addView(child, index, params);
            toggle.setOnClickListener((v) -> handleCollapseClick());
        }
    }

    private void handleCollapseClick() {
        if (CommonUtils.isExpanded(body)) collapse();
        else expand();
    }

    public void expand() {
        if (!CommonUtils.isExpanded(body)) {
            CommonUtils.expand(body, null);
            CommonUtils.animateCollapsingArrowBellows(toggle, true);
        }

        if (exclusiveOpenViews != null && exclusiveOpenViews.length > 0) {
            for (ExpandableCardWithTitleView view : exclusiveOpenViews)
                view.collapse();
        }
    }

    public void collapse() {
        if (CommonUtils.isExpanded(body)) {
            CommonUtils.collapse(body, null);
            CommonUtils.animateCollapsingArrowBellows(toggle, false);
        }
    }

    public void exclusiveOpen(ExpandableCardWithTitleView... views) {
        exclusiveOpenViews = views;
    }

    public void setTitle(@StringRes int res) {
        title.setText(res);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled) {
            toggle.setEnabled(true);
            title.setEnabled(true);
        } else {
            collapse();
            toggle.setEnabled(false);
            title.setEnabled(false);
        }

        super.setEnabled(enabled);
    }
}
