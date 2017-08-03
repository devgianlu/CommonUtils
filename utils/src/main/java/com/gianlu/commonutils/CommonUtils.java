package com.gianlu.commonutils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.AttrRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused,WeakerAccess")
public class CommonUtils {
    private static boolean DEBUG = BuildConfig.DEBUG;

    private static String pickCountryURL(boolean global) {
        String country = Locale.getDefault().getCountry();
        if (country.isEmpty() || country.length() >= 3 || global)
            return "http://www.google.com/generate_204";
        else return "http://www.google." + country.toLowerCase() + "/generate_204";
    }

    public static boolean hasInternetAccess(boolean global) {
        try {
            HttpURLConnection url = (HttpURLConnection) new URL(pickCountryURL(global)).openConnection();
            //noinspection SpellCheckingInspection
            url.setRequestProperty("User-Agent", "Connectivity test");
            url.setRequestProperty("Connection", "close");
            url.setConnectTimeout(1000);
            url.connect();
            return url.getResponseCode() == 204 && url.getContentLength() == 0;
        } catch (IOException ex) {
            return !global && hasInternetAccess(true);
        }
    }

    public static boolean equals(List<?> a, List<?> b) {
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++)
            if (!Objects.equals(a.get(i), b.get(i))) return false;

        return true;
    }

    public static int getMaskedValue(int maskedValue, int mask) {
        int definitiveMaskedValue = 0;
        int count = 0;

        maskedValue = mask & maskedValue;

        while (mask != 0) {
            while ((mask & 1) == 0) {
                mask = mask >>> 1;
                maskedValue = maskedValue >>> 1;
            }
            while ((mask & 1) == 1) {
                definitiveMaskedValue = definitiveMaskedValue + ((maskedValue & 1) << count);
                count++;

                mask = mask >>> 1;
                maskedValue = maskedValue >>> 1;
            }
        }

        return definitiveMaskedValue;
    }

    public static int setMaskedValue(int maskedValue, int mask, int valueToAdd) {
        int nbZero = 0;
        int nbLeastSignificantBit = 0;
        int tmpMask = mask;
        maskedValue = maskedValue & ~mask;

        while (tmpMask != 0) {
            while ((tmpMask & 1) == 0) {
                tmpMask = tmpMask >>> 1;
                nbLeastSignificantBit++;
                nbZero++;
            }

            while ((tmpMask & 1) == 1) {
                tmpMask = tmpMask >>> 1;

                BigInteger bigValueToAdd = BigInteger.valueOf(valueToAdd).shiftLeft(nbZero);
                int tmpValueToAdd = bigValueToAdd.intValue();
                BigInteger bigMaskOneBit = BigInteger.valueOf(1).shiftLeft(nbLeastSignificantBit);
                int maskOneBit = bigMaskOneBit.intValue();

                int bitValueToSet = getMaskedValue(tmpValueToAdd, maskOneBit);
                maskedValue = maskedValue | bitValueToSet << nbLeastSignificantBit;
                nbLeastSignificantBit++;
            }
        }
        return maskedValue;
    }

    public static HashMap<String, Object> toMap(JSONObject obj) throws JSONException {
        HashMap<String, Object> map = new HashMap<>();

        Iterator<String> iterator = obj.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            map.put(key, obj.get(key));
        }

        return map;
    }

    public static JSONObject toJSONObject(HashMap<String, Object> map) throws JSONException {
        JSONObject obj = new JSONObject();
        if (map == null) return obj;

        for (Map.Entry<String, Object> entry : map.entrySet())
            obj.put(entry.getKey(), entry.getValue());

        return obj;
    }

    @Nullable
    public static File getSecondaryStorage() {
        final String value = System.getenv("SECONDARY_STORAGE");
        if (!value.isEmpty()) {
            final String[] paths = value.split(":");
            for (String path : paths) {
                File file = new File(path);
                if (file.isDirectory())
                    return file;
            }
        }

        return null;
    }

    public static boolean isOnSecondaryStorage(File file) {
        File secondaryStorage = getSecondaryStorage();
        if (secondaryStorage == null) return false;

        try {
            return file.getCanonicalPath().startsWith(secondaryStorage.getAbsolutePath());
        } catch (IOException ex) {
            return false;
        }
    }

    public static boolean isExpanded(View v) {
        return v.getVisibility() == View.VISIBLE;
    }

    public static void expand(final View v) {
        v.measure(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? RelativeLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }


    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapseTitle(TextView v) {
        v.setSingleLine(true);
        v.setEllipsize(TextUtils.TruncateAt.MARQUEE);
    }

    public static void expandTitle(TextView v) {
        v.setSingleLine(false);
        v.setEllipsize(null);
    }

    public static Drawable resolveAttrAsDrawable(Context context, @AttrRes int id) {
        TypedArray ta = context.obtainStyledAttributes(new int[]{id});
        Drawable drawableFromTheme = ta.getDrawable(0);
        ta.recycle();
        return drawableFromTheme;
    }

    public static void animateCollapsingArrowBellows(ImageButton view, boolean expanded) {
        if (expanded) view.animate().rotation(0).setDuration(200).start();
        else view.animate().rotation(180).setDuration(200).start();
    }

    public static void showDialog(final Context context, final Dialog dialog) {
        Toaster.initHandler(context);
        Toaster.handler.post(new Runnable() {
            @Override
            public void run() {
                if (context instanceof Activity && ((Activity) context).isFinishing()) return;
                dialog.show();
            }
        });
    }

    public static void showDialog(final Context context, final AlertDialog.Builder builder) {
        Toaster.initHandler(context);
        Toaster.handler.post(new Runnable() {
            @Override
            public void run() {
                if (context instanceof Activity && ((Activity) context).isFinishing()) return;
                builder.create().show();
            }
        });
    }

    private static ProgressDialog fastIndeterminateProgressDialog(Context context, String message) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage(message);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        return pd;
    }

    public static ProgressDialog fastIndeterminateProgressDialog(Context context, @StringRes int message) {
        return fastIndeterminateProgressDialog(context, context.getString(message));
    }

    @Nullable
    public static String getText(TextInputLayout field) {
        return field.getEditText() == null ? null : field.getEditText().getText().toString();
    }

    public static void setText(TextInputLayout field, String val) {
        if (field.getEditText() != null) field.getEditText().setText(val);
    }

    public static String dimensionFormatter(float v, boolean si) {
        if (v <= 0) {
            return "0 B";
        } else {
            final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
            int digitGroups = (int) (Math.log10(v) / Math.log10(si ? 1000 : 1024));
            if (digitGroups > 4) return "∞ B";
            return new DecimalFormat("#,##0.#").format(v / Math.pow(si ? 1000 : 1024, digitGroups)) + " " + units[digitGroups];
        }
    }

    public static String speedFormatter(float v, boolean si) {
        if (v <= 0) {
            return "0 B/s";
        } else {
            final String[] units = new String[]{"B/s", "KB/s", "MB/s", "GB/s", "TB/s"};
            int digitGroups = (int) (Math.log10(v) / Math.log10(si ? 1000 : 1024));
            if (digitGroups > 4) return "∞ B/s";
            return new DecimalFormat("#,##0.#").format(v / Math.pow(si ? 1000 : 1024, digitGroups)) + " " + units[digitGroups];
        }
    }

    public static void sendEmail(Context context, String appName, @Nullable Throwable sendEx) {
        String version;
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ex) {
            version = context.getString(R.string.unknown);
        }

        Intent intent = new Intent(Intent.ACTION_SEND)
                .setType("message/rfc822")
                .putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.email)})
                .putExtra(Intent.EXTRA_SUBJECT, appName);

        String emailBody = "-------- DO NOT EDIT --------" +
                "\nOS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")" +
                "\nOS API Level: " + android.os.Build.VERSION.SDK_INT +
                "\nDevice: " + android.os.Build.DEVICE +
                "\nModel (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")" +
                "\nApplication version: " + version;

        if (sendEx != null) {
            emailBody += "\n\n";
            emailBody += Logging.getStackTrace(sendEx);
        }

        emailBody += "\n-----------------------------" + "\n\n\nProvide bug details\n";

        intent.putExtra(Intent.EXTRA_TEXT, emailBody);

        Logging.LogFile log = Logging.getLatestLogFile(context, true);
        if (log != null) {
            try {
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(Logging.moveLogFileToExternalStorage(context, log)));
            } catch (ParseException | IOException ignored) {
            }
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toaster.show(context, Toaster.Message.NO_EMAIL_CLIENT, ex);
        }
    }

    public static <T> int indexOf(T[] items, T item) {
        for (int i = 0; i < items.length; i++)
            if (items[i] == item)
                return i;

        return -1;
    }

    public static <T> int indexOf(List<T> items, T item) {
        for (int i = 0; i < items.size(); i++)
            if (items.get(i) == item)
                return i;

        return -1;
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
        in.close();
        out.close();
    }

    public static void copyFile(File src, File dst) throws IOException {
        copy(new FileInputStream(src), new FileOutputStream(dst));
    }

    public static String timeFormatter(long sec) {
        int day = (int) TimeUnit.SECONDS.toDays(sec);
        long hours = TimeUnit.SECONDS.toHours(sec) - TimeUnit.DAYS.toHours(day);
        long minute = TimeUnit.SECONDS.toMinutes(sec) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(sec));
        long second = TimeUnit.SECONDS.toSeconds(sec) - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(sec));

        if (day > 0) {
            if (day > 1000) {
                return "∞";
            } else {
                return String.format(Locale.getDefault(), "%02d", day) + "d " + String.format(Locale.getDefault(), "%02d", hours) + "h " + String.format(Locale.getDefault(), "%02d", minute) + "m " + String.format(Locale.getDefault(), "%02d", second) + "s";
            }
        } else {
            if (hours > 0) {
                return String.format(Locale.getDefault(), "%02d", hours) + "h " + String.format(Locale.getDefault(), "%02d", minute) + "m " + String.format(Locale.getDefault(), "%02d", second) + "s";
            } else {
                if (minute > 0) {
                    return String.format(Locale.getDefault(), "%02d", minute) + "m " + String.format(Locale.getDefault(), "%02d", second) + "s";
                } else {
                    if (second > 0) {
                        return String.format(Locale.getDefault(), "%02d", second) + "s";
                    } else {
                        return "∞";
                    }
                }
            }
        }
    }

    public static SimpleDateFormat getVerbalDateFormatter() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf;
    }

    public static SimpleDateFormat getFullDateFormatter() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss dd/MM/yyyy", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf;
    }

    public static boolean isDebug() {
        return DEBUG;
    }

    public static void setDebug(boolean debug) {
        CommonUtils.DEBUG = debug;
        Logging.DEBUG = debug;
    }

    public static String join(Object[] objs, String separator) {
        return join(Arrays.asList(objs), separator);
    }

    public static String join(List<?> objs, String separator) {
        if (objs == null) return "";
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < objs.size(); i++) {
            builder.append(objs.get(i).toString());
            if (i < objs.size() - 1) builder.append(separator);
        }

        return builder.toString();
    }

    public static void setCardTopMargin(Context context, RecyclerView.ViewHolder holder) {
        if (holder.itemView.getLayoutParams() == null) return;
        ((RecyclerView.LayoutParams) holder.itemView.getLayoutParams()).topMargin = holder.getLayoutPosition() == 0 ? (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics()) : 0;
    }

    public static <T> boolean contains(T[] elements, T element) {
        for (T element1 : elements)
            if (element1 == element) return true;

        return false;
    }

    public static String breakText(String str, Paint paint, float maxWidth) {
        return str.substring(0, paint.breakText(str, 0, str.length(), true, maxWidth, null));
    }
}
