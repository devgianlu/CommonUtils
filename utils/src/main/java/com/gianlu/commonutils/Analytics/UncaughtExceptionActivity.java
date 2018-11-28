package com.gianlu.commonutils.Analytics;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gianlu.commonutils.Dialogs.ActivityWithDialog;
import com.gianlu.commonutils.Logging;
import com.gianlu.commonutils.R;

import androidx.appcompat.app.ActionBar;

public class UncaughtExceptionActivity extends ActivityWithDialog {
    public static void startActivity(Context context, Throwable ex) {
        if (context != null) {
            context.startActivity(new Intent(context, UncaughtExceptionActivity.class)
                    .putExtra("exception", ex)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unchaught_exception);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        Button email = findViewById(R.id.uncaughtException_email);
        email.setOnClickListener(v -> Logging.sendEmail(UncaughtExceptionActivity.this, (Throwable) getIntent().getSerializableExtra("exception")));
    }
}
