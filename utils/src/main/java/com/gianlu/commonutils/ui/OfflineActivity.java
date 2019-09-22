package com.gianlu.commonutils.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import com.gianlu.commonutils.R;
import com.gianlu.commonutils.dialogs.ActivityWithDialog;
import com.gianlu.commonutils.logging.Logging;

public class OfflineActivity extends ActivityWithDialog {

    public static void startActivity(@NonNull Context context, @Nullable Class<? extends Activity> retryClass) {
        context.startActivity(new Intent(context, OfflineActivity.class)
                .putExtra("retry", retryClass)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        Button email = findViewById(R.id.offline_email);
        email.setOnClickListener(v -> Logging.sendEmail(OfflineActivity.this, null));

        Button offline = findViewById(R.id.offline_retry);
        final Class<?> retryClass = (Class) getIntent().getSerializableExtra("retry");
        if (retryClass == null) {
            offline.setVisibility(View.GONE);
        } else {
            offline.setVisibility(View.VISIBLE);
            offline.setOnClickListener(v -> startActivity(new Intent(OfflineActivity.this, retryClass)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)));
        }
    }
}
