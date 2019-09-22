package com.gianlu.commonutils.preferences;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.gianlu.commonutils.R;
import com.gianlu.commonutils.logging.Logging;

public class MaterialAboutVersionItem extends MaterialAboutActionItem {

    public MaterialAboutVersionItem(@NonNull Context context) {
        super(R.string.version, 0, R.drawable.outline_info_24, null);

        String version;
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ex) {
            Logging.log(ex);
            version = context.getString(R.string.unknown);
        }

        setSubText(version);
    }
}
