package com.gianlu.commonutils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.gianlu.commonutils.Dialogs.ActivityWithDialog;

import java.io.IOException;
import java.util.List;

public class LogsActivity extends ActivityWithDialog implements Logging.LogLineAdapter.Listener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        setTitle(R.string.log_activity_title);

        ActionBar bar = getSupportActionBar();
        if (bar != null) bar.setDisplayHomeAsUpEnabled(true);

        final Spinner spinner = findViewById(R.id.logs_spinner);
        final RecyclerViewLayout layout = findViewById(R.id.logs_recyclerViewLayout);
        layout.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        layout.getList().addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        final List<Logging.LogFile> logFiles = Logging.listLogFiles(this, Logging.Type.LOG);

        if (logFiles.isEmpty()) {
            spinner.setVisibility(View.GONE);
            layout.showInfo(R.string.noLogs);
            return;
        }

        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, logFiles));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    layout.loadListData(new Logging.LogLineAdapter(LogsActivity.this, Logging.getLogLines(logFiles.get(i)), LogsActivity.this));
                } catch (IOException ex) {
                    Logging.log(ex);
                    onBackPressed();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                onBackPressed();
            }
        });

        spinner.setSelection(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLogLineSelected(@NonNull Logging.LogLine line) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("stack trace", line.message);
            clipboard.setPrimaryClip(clip);
            Toaster.with(this).message(R.string.copiedToClipboard).show();
        }
    }
}
