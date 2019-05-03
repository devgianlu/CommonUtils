package com.gianlu.commonutils;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.gianlu.commonutils.CasualViews.RecyclerViewLayout;
import com.gianlu.commonutils.Dialogs.ActivityWithDialog;

import java.io.IOException;
import java.util.List;

import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LogsActivity extends ActivityWithDialog {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        setTitle(R.string.log_activity_title);

        ActionBar bar = getSupportActionBar();
        if (bar != null) bar.setDisplayHomeAsUpEnabled(true);

        final Spinner spinner = findViewById(R.id.logs_spinner);
        final RecyclerViewLayout layout = findViewById(R.id.logs_recyclerViewLayout);
        layout.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        layout.getList().addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        final List<Logging.LogFile> logFiles = Logging.listLogFiles(this);

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
                    layout.loadListData(new Logging.LogLineAdapter(LogsActivity.this, Logging.getLogLines(logFiles.get(i))));
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
}
