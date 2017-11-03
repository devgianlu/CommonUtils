package com.gianlu.commonutils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@SuppressWarnings({"unused", "WeakerAccess"})
public class Logging {
    public static boolean DEBUG = BuildConfig.DEBUG; // Overwritten by CommonUtils
    private static File logFile;
    private static File secretLogFile;

    @SuppressWarnings("ResultOfMethodCallIgnored")
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
                Date date = getFileDateFormatter().parse(file.getName().replace(".log", "").replace(".secret", ""));
                if (date.before(cal.getTime())) file.delete();
            } catch (ParseException ignored) {
            }
        }
    }

    private static SimpleDateFormat getFileDateFormatter() {
        return new SimpleDateFormat("d-LL-yyyy", Locale.getDefault());
    }

    public static void init(Context context) {
        logFile = new File(context.getFilesDir(), getFileDateFormatter().format(new Date()) + ".log");
        secretLogFile = new File(context.getFilesDir(), getFileDateFormatter().format(new Date()) + ".secret");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static boolean shouldLog() {
        if (logFile != null && secretLogFile != null) {
            try {
                logFile.createNewFile();
                secretLogFile.createNewFile();
            } catch (IOException ignored) {
            }

            return logFile.canWrite() && secretLogFile.canWrite();
        }

        return false;
    }

    private static SimpleDateFormat getTimeFormatter() {
        return new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
    }

    @Nullable
    public static LogFile getLatestLogFile(Context context, boolean secret) {
        List<LogFile> logs = listLogFiles(context, secret);
        return logs.isEmpty() ? null : logs.get(0);
    }

    public static LogFile moveLogFileToExternalStorage(Context context, LogFile log) throws ParseException, IOException {
        LogFile dest = new LogFile(new File(context.getExternalCacheDir(), log.getName()));
        CommonUtils.copyFile(log, dest);
        return dest;
    }

    public static List<LogFile> listLogFiles(Context context, final boolean secret) {
        final File files[] = context.getFilesDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.toLowerCase().endsWith(secret ? ".secret" : ".log");
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

    public static void secretLog(Throwable exx) {
        if (DEBUG) exx.printStackTrace();
        if (!shouldLog()) return;

        try (FileOutputStream out = new FileOutputStream(secretLogFile, true)) {
            out.write((getTimeFormatter().format(new Date()) + " >> " + getStackTrace(exx) + "\n\n").getBytes());
            out.flush();
        } catch (IOException ex) {
            if (DEBUG) ex.printStackTrace();
        }
    }

    public static String getStackTrace(@NonNull Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        ex.printStackTrace(writer);
        return sw.toString();
    }

    public static void logMe(Throwable ex) {
        if (ex == null) return;
        logMe(ex.getMessage(), true);
        secretLog(ex);
    }

    public static void logMe(String message, boolean isError) {
        if (message == null) message = "No message given";

        if (DEBUG) {
            if (isError) System.err.println(message);
            else System.out.println(message);
        }

        if (!shouldLog()) return;

        try (FileOutputStream out = new FileOutputStream(logFile, true)) {
            out.write(((isError ? "--ERROR--" : "--INFO--") + getTimeFormatter().format(new Date()) + " >> " + message.replace("\n", " ") + "\n").getBytes());
            out.flush();
        } catch (IOException ex) {
            if (DEBUG) ex.printStackTrace();
        }
    }

    public static class LogLine implements Serializable {
        public final Type type;
        public final String message;

        public LogLine(Type type, String message) {
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

    public static class LogLineAdapter extends RecyclerView.Adapter<LogLineAdapter.ViewHolder> {
        private final List<LogLine> objs;
        private final IAdapter listener;
        private final LayoutInflater inflater;

        public LogLineAdapter(Context context, List<LogLine> objs, @Nullable IAdapter listener) {
            this.inflater = LayoutInflater.from(context);
            this.objs = objs;
            this.listener = listener;
        }

        public void clear() {
            objs.clear();
            notifyDataSetChanged();
        }

        public void add(LogLine line) {
            objs.add(line);
            notifyItemInserted(objs.size() - 1);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final LogLine item = objs.get(position);

            holder.msg.setText(item.message);
            switch (item.type) {
                case INFO:
                    holder.level.setText(R.string.infoTag);
                    holder.level.setTextColor(Color.BLACK);
                    break;
                case WARNING:
                    holder.level.setText(R.string.warningTag);
                    holder.level.setTextColor(Color.YELLOW);
                    break;
                case ERROR:
                    holder.level.setText(R.string.errorTag);
                    holder.level.setTextColor(Color.RED);
                    break;
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) listener.onLogLineSelected(item);
                }
            });
        }

        @Override
        public int getItemCount() {
            return objs.size();
        }

        public interface IAdapter {
            void onLogLineSelected(LogLine line);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final TextView msg;
            final TextView level;

            public ViewHolder(ViewGroup parent) {
                super(inflater.inflate(R.layout.log_line_item, parent, false));

                msg = itemView.findViewById(R.id.logLine_msg);
                level = itemView.findViewById(R.id.logLine_level);
            }
        }
    }
}
