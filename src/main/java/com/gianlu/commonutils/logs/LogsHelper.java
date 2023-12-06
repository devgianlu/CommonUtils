package com.gianlu.commonutils.logs;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gianlu.commonutils.BuildConfig;
import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.R;
import com.gianlu.commonutils.preferences.CommonPK;
import com.gianlu.commonutils.preferences.Prefs;
import com.gianlu.commonutils.ui.Toaster;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class LogsHelper {
    private static final String TAG = LogsHelper.class.getSimpleName();

    private LogsHelper() {
    }

    @NonNull
    public static String getStackTrace(@NonNull Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        ex.printStackTrace(writer);
        return sw.toString();
    }

    /**
     * Open a GitHub issue containing debug information.
     */
    public static void openGithubIssue(@NonNull Context context, @NonNull String projectName, @Nullable Throwable sendEx) {
        String version;
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ex) {
            version = context.getString(R.string.unknown);
        }

        version += "-" + BuildConfig.FLAVOR;

        String body = "-------- DO NOT EDIT --------" +
                "\r\nOS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")" +
                "\r\nOS API Level: " + android.os.Build.VERSION.SDK_INT +
                "\r\nDevice: " + android.os.Build.DEVICE +
                "\r\nModel (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")" +
                "\r\nApplication version: " + version +
                "\r\nCrashlytics UID: " + Prefs.getString(CommonPK.ANALYTICS_USER_ID, null);

        if (sendEx != null) {
            body += "\r\n\r\n";
            body += getStackTrace(sendEx);
        }

        body += "\r\n------------------------------------\r\n\r\n\r\nProvide bug details\r\n";

        Uri.Builder uri = new Uri.Builder()
                .scheme("https")
                .authority("github.com")
                .appendPath("devgianlu")
                .appendPath(projectName)
                .appendPath("issues")
                .appendPath("new")
                .appendQueryParameter("body", body);

        Intent intent = new Intent(Intent.ACTION_VIEW, uri.build());
        context.startActivity(Intent.createChooser(intent, "Open link..."));
    }

    /**
     * Exports the log files with logcat and attaches them to {@param intent}.
     *
     * @return {@code null} if successful, the exception otherwise.
     */
    @Nullable
    public static Exception exportLogFiles(@NonNull Context context, @NonNull Intent intent) {
        try {
            File parent = new File(context.getCacheDir(), "logs");
            if (!parent.exists() && !parent.mkdir())
                return new IOException("Cannot create logs directory.");

            Process process = Runtime.getRuntime().exec("logcat -d");
            File file = new File(parent, "logs-" + System.currentTimeMillis() + ".txt");
            try (FileOutputStream out = new FileOutputStream(file, false)) {
                CommonUtils.copy(process.getInputStream(), out);
            } finally {
                process.destroy();
            }

            Uri uri = LogsFileProvider.getLogFile(context, file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            return null;
        } catch (IllegalArgumentException | IOException ex) {
            Log.e(TAG, "Failed exporting logs.", ex);
            return ex;
        }
    }
}
