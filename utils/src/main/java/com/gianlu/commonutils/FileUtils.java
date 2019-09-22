package com.gianlu.commonutils;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

public final class FileUtils {
    private static final String PRIMARY_VOLUME_NAME = "primary";

    @Nullable
    public static String getFullPathFromTreeUri(@Nullable Uri treeUri, @NonNull Context con) {
        if (treeUri == null)
            return null;

        String volumePath = FileUtils.getVolumePath(FileUtils.getVolumeIdFromTreeUri(treeUri), con);
        if (volumePath == null)
            return File.separator;

        if (volumePath.endsWith(File.separator))
            volumePath = volumePath.substring(0, volumePath.length() - 1);

        String documentPath = FileUtils.getDocumentPathFromTreeUri(treeUri);
        if (documentPath != null && documentPath.endsWith(File.separator))
            documentPath = documentPath.substring(0, documentPath.length() - 1);

        if (documentPath != null && documentPath.length() > 0) {
            if (documentPath.startsWith(File.separator)) return volumePath + documentPath;
            else return volumePath + File.separator + documentPath;
        } else {
            return volumePath;
        }
    }

    @Nullable
    @SuppressWarnings({"ConstantConditions", "JavaReflectionMemberAccess"})
    private static String getVolumePath(@NonNull String volumeId, @NonNull Context con) {
        try {
            StorageManager mStorageManager = (StorageManager) con.getSystemService(Context.STORAGE_SERVICE);
            Class<?> storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");

            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getUuid = storageVolumeClazz.getMethod("getUuid");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isPrimary = storageVolumeClazz.getMethod("isPrimary");
            Object result = getVolumeList.invoke(mStorageManager);

            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String uuid = (String) getUuid.invoke(storageVolumeElement);
                Boolean primary = (Boolean) isPrimary.invoke(storageVolumeElement);

                // primary volume?
                if (primary && PRIMARY_VOLUME_NAME.equals(volumeId))
                    return (String) getPath.invoke(storageVolumeElement);

                // other volumes?
                if (uuid != null) {
                    if (uuid.equals(volumeId))
                        return (String) getPath.invoke(storageVolumeElement);
                }
            }

            // not found.
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    @Nullable
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static String getVolumeIdFromTreeUri(@NonNull Uri treeUri) {
        final String docId = DocumentsContract.getTreeDocumentId(treeUri);
        final String[] split = docId.split(":");
        if (split.length > 0) return split[0];
        else return null;
    }

    @NonNull
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static String getDocumentPathFromTreeUri(@NonNull Uri treeUri) {
        final String docId = DocumentsContract.getTreeDocumentId(treeUri);
        final String[] split = docId.split(":");
        if ((split.length >= 2) && (split[1] != null)) return split[1];
        else return File.separator;
    }
}
