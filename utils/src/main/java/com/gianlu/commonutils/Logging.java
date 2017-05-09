package com.gianlu.commonutils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Logging {
    public static boolean DEBUG = BuildConfig.DEBUG;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("SimpleDateFormat")
    public static void clearLogs(Context context) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);

        for (File file : context.getFilesDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.toLowerCase().endsWith(".log") || s.toLowerCase().endsWith(".secret");
            }
        })) {
            try {
                Date date = new SimpleDateFormat("d-LL-yyyy").parse(file.getName().replace(".log", "").replace(".secret", ""));
                if (date.before(cal.getTime())) file.delete();
            } catch (ParseException ignored) {
            }
        }
    }

    @Nullable
    public static LogFile getLatestLogFile(Context context) {
        List<LogFile> logs = listLogFiles(context);
        return logs.isEmpty() ? null : logs.get(0);
    }

    public static LogFile moveLogFileToExternalStorage(Context context, LogFile log) throws ParseException, IOException {
        LogFile dest = new LogFile(new File(context.getExternalCacheDir(), log.getName()));
        CommonUtils.copyFile(log, dest);
        return dest;
    }

    public static List<LogFile> listLogFiles(Context context) {
        final File files[] = context.getFilesDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.toLowerCase().endsWith(".log");
            }
        });

        List<LogFile> logFiles = new ArrayList<>();
        for (File file : files) {
            try {
                logFiles.add(new LogFile(file));
            } catch (ParseException ignored) {
            }
        }

        Collections.sort(logFiles, new LogFileComparator());

        return logFiles;
    }

    public static List<LogLine> getLogLines(Context context, LogFile log) throws IOException {
        List<LogLine> logLines = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(context.openFileInput(log.getName())));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("--ERROR--")) {
                logLines.add(new LogLine(LogLine.Type.ERROR, line.replace("--ERROR--", "")));
            } else if (line.startsWith("--INFO--")) {
                logLines.add(new LogLine(LogLine.Type.INFO, line.replace("--INFO--", "")));
            }
        }

        return logLines;
    }

    public static void secretLog(Context context, Throwable exx) {
        if (DEBUG) exx.printStackTrace();

        try {
            FileOutputStream fOut = context.openFileOutput(new SimpleDateFormat("d-LL-yyyy", Locale.getDefault()).format(new java.util.Date()) + ".secret", Context.MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            osw.write(new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(new java.util.Date()) + " >> " + exx.toString() + "\n" + Arrays.toString(exx.getStackTrace()) + "\n\n");
            osw.flush();
            osw.close();
        } catch (IOException ignored) {
        }
    }

    public static void logMe(Context context, Throwable ex) {
        if (DEBUG) ex.printStackTrace();
        if (ex == null) return;
        logMe(context, ex.getMessage(), true);
    }

    public static void logMe(Context context, String message, boolean isError) {
        if (message == null) message = "No message given";

        if (DEBUG)
            if (isError) System.err.println(message);
            else System.out.println(message);

        try {
            FileOutputStream fOut = context.openFileOutput(new SimpleDateFormat("d-LL-yyyy", Locale.getDefault()).format(new java.util.Date()) + ".log", Context.MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            osw.write((isError ? "--ERROR--" : "--INFO--") + new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(new java.util.Date()) + " >> " + message.replace("\n", " ") + "\n");
            osw.flush();
            osw.close();
        } catch (IOException ignored) {
        }
    }

    public static class LogLine {
        public final Type type;
        public final String message;

        LogLine(Type type, String message) {
            this.type = type;
            this.message = message;
        }

        public enum Type {
            INFO,
            WARNING,
            ERROR
        }
    }

    public static class LogFileComparator implements Comparator<LogFile> {
        @Override
        public int compare(LogFile o1, LogFile o2) {
            if (o1.date == o2.date) return 0;
            else if (o1.date > o2.date) return -1;
            else return 1;
        }
    }

    public static class LogFile extends File {
        public final long date;

        @SuppressLint("SimpleDateFormat")
        public LogFile(File file) throws ParseException {
            super(file.getAbsolutePath());
            date = new SimpleDateFormat("d-LL-yyyy").parse(toString()).getTime();
        }

        @Override
        public String toString() {
            return getName().split("\\.")[0];
        }
    }

    public static class LogLineAdapter extends BaseAdapter {
        private final Context context;
        private final List<LogLine> objs;

        LogLineAdapter(Context context, List<LogLine> objs) {
            this.context = context;
            this.objs = objs;
        }

        @Override
        public int getCount() {
            return objs.size();
        }

        @Override
        public LogLine getItem(int i) {
            return objs.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setPadding(12, 12, 12, 12);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            LogLine item = getItem(position);

            TextView type = new TextView(context);
            type.setTypeface(Typeface.DEFAULT_BOLD);
            switch (item.type) {
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
            linearLayout.addView(new SuperTextView(context, item.message));

            return linearLayout;
        }
    }
}
