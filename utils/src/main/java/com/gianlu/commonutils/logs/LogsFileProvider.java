package com.gianlu.commonutils.logs;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.File;

public final class LogsFileProvider extends FileProvider {

    @NonNull
    public static Uri getLogFile(@NonNull Context context, File file) {
        return getUriForFile(context, context.getApplicationContext().getPackageName() + ".logs", file);
    }
}
