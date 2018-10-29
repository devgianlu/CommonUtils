package com.gianlu.commonutils.Preferences;

import android.content.Context;
import android.content.pm.PackageManager;

import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem;
import com.gianlu.commonutils.Logging;
import com.gianlu.commonutils.R;

import androidx.annotation.NonNull;

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
