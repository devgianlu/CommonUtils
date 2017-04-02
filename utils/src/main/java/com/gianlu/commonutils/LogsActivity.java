package com.gianlu.commonutils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused,WeakerAccess")
@Keep
public class LogsActivity extends AppCompatActivity {
    private static final int DELETE_LOGS_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        setTitle(R.string.log_activity_title);

        final Spinner spinner = (Spinner) findViewById(R.id.logs_spinner);
        TextView empty = (TextView) findViewById(R.id.logs_empty);
        final ListView list = (ListView) findViewById(R.id.logs_list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("stack trace", ((LoglineItem) adapterView.getItemAtPosition(i)).getMessage());
                clipboard.setPrimaryClip(clip);

                CommonUtils.UIToast(LogsActivity.this, CommonUtils.ToastMessage.COPIED_TO_CLIPBOARD);
            }
        });

        final File files[] = getFilesDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.toLowerCase().endsWith(".log");
            }
        });

        if (files.length == 0) {
            empty.setVisibility(View.VISIBLE);
            spinner.setVisibility(View.GONE);
        }

        List<String> spinnerList = new ArrayList<>();
        for (File logFile : files)
            spinnerList.add(logFile.getName());

        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerList));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<LoglineItem> logLines = new ArrayList<>();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput(adapterView.getItemAtPosition(i).toString())));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("--ERROR--")) {
                            logLines.add(new LoglineItem(TYPE.ERROR, line.replace("--ERROR--", "")));
                        } else if (line.startsWith("--INFO--")) {
                            logLines.add(new LoglineItem(TYPE.INFO, line.replace("--INFO--", "")));
                        }
                    }
                } catch (IOException ex) {
                    CommonUtils.UIToast(LogsActivity.this, CommonUtils.ToastMessage.FATAL_EXCEPTION, ex);
                    onBackPressed();
                }

                list.setAdapter(new LoglineAdapter(logLines));
                list.setSelection(list.getCount() - 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                onBackPressed();
            }
        });

        spinner.setSelection(spinner.getCount() - 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, DELETE_LOGS_ID, Menu.NONE, R.string.deleteAllLogs);
        return true;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_LOGS_ID:
                File files[] = getFilesDir().listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File file, String s) {
                        return s.toLowerCase().endsWith(".log");
                    }
                });

                for (File logFile : files)
                    logFile.delete();

                CommonUtils.UIToast(this, CommonUtils.ToastMessage.LOGS_DELETED);
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public enum TYPE {
        INFO,
        WARNING,
        ERROR
    }

    private class LoglineItem {
        private final TYPE type;
        private final String message;

        LoglineItem(TYPE type, String message) {
            this.type = type;
            this.message = message;
        }

        TYPE getType() {
            return type;
        }

        String getMessage() {
            return message;
        }
    }

    private class LoglineAdapter extends BaseAdapter {
        private final List<LoglineItem> objs;

        LoglineAdapter(List<LoglineItem> objs) {
            this.objs = objs;
        }

        @Override
        public int getCount() {
            return objs.size();
        }

        @Override
        public LoglineItem getItem(int i) {
            return objs.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout linearLayout = new LinearLayout(LogsActivity.this);
            linearLayout.setPadding(12, 12, 12, 12);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            LoglineItem item = getItem(position);

            TextView type = new TextView(LogsActivity.this);
            type.setTypeface(Typeface.DEFAULT_BOLD);
            switch (item.getType()) {
                case INFO:
                    type.setText(R.string.infoTag);
                    type.setTextColor(Color.BLACK);
                    break;
                case WARNING:
                    type.setText(R.string.warningTag);
                    type.setTextColor(Color.YELLOW);
                    break;
                case ERROR:
                    type.setText(R.string.errorTag);
                    type.setTextColor(Color.RED);
                    break;
            }
            linearLayout.addView(type);
            linearLayout.addView(CommonUtils.fastTextView(LogsActivity.this, item.getMessage()));


            return linearLayout;
        }
    }
}
