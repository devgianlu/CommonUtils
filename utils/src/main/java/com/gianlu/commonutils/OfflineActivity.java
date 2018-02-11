package com.gianlu.commonutils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class OfflineActivity extends AppCompatActivity {

    public static void startActivity(Context context, @StringRes int appName, Class<? extends Activity> retryClass) {
        if (context != null) {
            context.startActivity(new Intent(context, OfflineActivity.class)
                    .putExtra("appName", context.getString(appName))
                    .putExtra("retry", retryClass)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }
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
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.sendEmail(OfflineActivity.this, getIntent().getStringExtra("appName"), null);
            }
        });

        Button offline = findViewById(R.id.offline_retry);
        final Class<? extends Activity> retryClass = (Class<? extends Activity>) getIntent().getSerializableExtra("retry");
        if (retryClass == null) {
            offline.setVisibility(View.GONE);
        } else {
            offline.setVisibility(View.VISIBLE);
            offline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(OfflineActivity.this, retryClass)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }
            });
        }
    }
}
