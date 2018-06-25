package com.gianlu.commonutils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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

public final class Logging {
    public static boolean DEBUG = BuildConfig.DEBUG; // Overwritten by CommonUtils
    private static File logFile;
    private static File secretLogFile;

    @NonNull
    private static File getLogsDirectory(@NonNull Context context) {
        return new File(context.getFilesDir(), "logs");
    }

    private static boolean isLogFile(@NonNull String name) {
        return name.toLowerCase().endsWith(".log");
    }

    private static boolean isSecretLogFile(@NonNull String name) {
        return name.toLowerCase().endsWith(".secret");
    }

    @NonNull
    private static File[] listLogsInDirectory(@NonNull File dir, @NonNull final Type type) {
        return dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (type == Type.ALL) return isLogFile(name) || isSecretLogFile(name);
                else if (type == Type.SECRET) return isSecretLogFile(name);
                else return isLogFile(name);
            }
        });
    }

    private static File[] listLogFilesInternal(@NonNull Context context, @NonNull Type type) {
        File[] first = listLogsInDirectory(context.getFilesDir(), type);
        File[] second = listLogsInDirectory(getLogsDirectory(context), type);
        File[] files = new File[first.length + second.length];
        System.arraycopy(first, 0, files, 0, first.length);
        System.arraycopy(second, 0, files, first.length, second.length);
        return files;
    }

    @NonNull
    private static String removeExtension(@NonNull File file) {
        String name = file.getName();
        int last = name.lastIndexOf('.');
        return name.substring(0, last);
    }

    @NonNull
    private static Date getDate(@NonNull File file) throws ParseException {
        String date = removeExtension(file);
        return getFileDateFormatter().parse(date);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void clearLogs(@NonNull Context context) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);

        for (File file : listLogFilesInternal(context, Type.ALL)) {
            try {
                Date date = getDate(file);
                if (date.before(cal.getTime())) file.delete();
            } catch (ParseException ex) {
                if (CommonUtils.isDebug()) ex.printStackTrace();
            }
        }
    }

    @Nullable
    public static LogFile getLatestLogFile(@NonNull Context context, @NonNull Type type) {
        List<LogFile> logs = listLogFiles(context, type);
        return logs.isEmpty() ? null : logs.get(0);
    }

    @SuppressLint("SimpleDateFormat")
    @NonNull
    private static SimpleDateFormat getFileDateFormatter() {
        return new SimpleDateFormat("d-MM-yyyy");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void init(@NonNull Context context) {
        File logs = getLogsDirectory(context);
        if (!logs.exists()) logs.mkdir();

        logFile = new File(logs, getFileDateFormatter().format(new Date()) + ".log");
        secretLogFile = new File(logs, getFileDateFormatter().format(new Date()) + ".secret");

        log("Logging initialized!", false);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static boolean shouldLog() {
        if (logFile != null && secretLogFile != null) {
            try {
                logFile.createNewFile();
                secretLogFile.createNewFile();
            } catch (IOException ex) {
                if (CommonUtils.isDebug()) ex.printStackTrace();
            }

            return logFile.canWrite() && secretLogFile.canWrite();
        }

        return false;
    }

    @NonNull
    private static SimpleDateFormat getTimeFormatter() {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    }

    public static List<LogFile> listLogFiles(Context context, @NonNull Type type) {
        File[] files = listLogFilesInternal(context, type);

        List<LogFile> logs = new ArrayList<>();
        for (File file : files) {
            try {
                logs.add(new LogFile(file));
            } catch (ParseException ex) {
                if (CommonUtils.isDebug()) ex.printStackTrace();
            }
        }

        Collections.sort(logs, new LogFilesComparator());

        return logs;
    }

    public enum Type {
        ALL,
        SECRET,
        LOG
    }

    @NonNull
    public static List<LogLine> getLogLines(@NonNull LogFile log) throws IOException {
        List<LogLine> logLines = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(log)));
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

    private static void secret(@NonNull Throwable exx) {
        if (DEBUG) exx.printStackTrace();
        if (!shouldLog()) return;

        try (FileOutputStream out = new FileOutputStream(secretLogFile, true)) {
            out.write((getTimeFormatter().format(new Date()) + " >> " + getStackTrace(exx) + "\n\n").getBytes());
            out.flush();
        } catch (IOException ex) {
            if (DEBUG) ex.printStackTrace();
        }
    }

    @NonNull
    public static String getStackTrace(@NonNull Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        ex.printStackTrace(writer);
        return sw.toString();
    }

    public static void log(@Nullable Throwable ex) {
        if (ex == null) return;
        log(ex.getMessage(), true);
        secret(ex);
    }

    public static void log(String message, boolean isError) {
        if (message == null) message = "No message given";

        if (DEBUG) {
            if (isError) System.err.println(message);
            else System.out.println(message);
        }

        if (!shouldLog()) return;

        SimpleDateFormat sdf = getTimeFormatter();
        try (FileOutputStream out = new FileOutputStream(logFile, true)) {
            out.write(((isError ? "--ERROR--" : "--INFO--") + sdf.format(new Date()) + " >> " + message.replace("\n", " ") + "\n").getBytes());
            out.flush();
        } catch (IOException ex) {
            if (DEBUG) ex.printStackTrace();
        }
    }

    public static class LogLine implements Serializable {
        public final Type type;
        public final String message;

        public LogLine(@NonNull Type type, @NonNull String message) {
            this.type = type;
            this.message = message;
        }

        public enum Type {
            INFO,
            WARNING,
            ERROR
        }
    }

    public static class LogFilesComparator implements Comparator<LogFile> {
        @Override
        public int compare(LogFile o1, LogFile o2) {
            return -o1.date.compareTo(o2.date);
        }
    }

    public static class LogFile extends File {
        public final Date date;

        LogFile(@NonNull File file) throws ParseException {
            super(file.getAbsolutePath());
            date = getDate(file);
        }

        @Override
        @NonNull
        public String toString() {
            return removeExtension(this);
        }
    }

    @UiThread
    public static class LogLineAdapter extends RecyclerView.Adapter<LogLineAdapter.ViewHolder> {
        private final List<LogLine> logs;
        private final Listener listener;
        private final LayoutInflater inflater;

        public LogLineAdapter(Context context, List<LogLine> logs, @Nullable Listener listener) {
            this.inflater = LayoutInflater.from(context);
            this.logs = logs;
            this.listener = listener;
        }

        public void clear() {
            logs.clear();
            notifyDataSetChanged();
        }

        public void add(@NonNull LogLine line) {
            logs.add(line);
            notifyItemInserted(logs.size() - 1);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(parent);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            final LogLine item = logs.get(position);

            holder.msg.setText(item.message);
            switch (item.type) {
                case INFO:
                    holder.level.setText("INFO: ");
                    holder.level.setTextColor(Color.BLACK);
                    break;
                case WARNING:
                    holder.level.setText("WARNING: ");
                    holder.level.setTextColor(Color.YELLOW);
                    break;
                case ERROR:
                    holder.level.setText("ERROR: ");
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
            return logs.size();
        }

        public interface Listener {
            void onLogLineSelected(@NonNull LogLine line);
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
