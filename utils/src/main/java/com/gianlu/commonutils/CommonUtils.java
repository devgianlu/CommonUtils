package com.gianlu.commonutils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.gianlu.commonutils.Preferences.Prefs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public final class CommonUtils {
    public static final String LOT_OF_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"£$%&/()=?^-_.:,;<>|\\*[]";
    private static boolean DEBUG = BuildConfig.DEBUG;

    public static boolean isVisible(@NonNull Fragment fragment) {
        View root = fragment.getView();
        return root != null && root.getGlobalVisibleRect(new Rect());
    }

    public static void setBackgroundColor(FloatingActionButton fab, @ColorRes int color) {
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(fab.getContext(), color)));
    }

    public static List<NameValuePair> splitQuery(URL url) {
        return splitQuery(url.getQuery());
    }

    public static List<NameValuePair> splitQuery(String query) {
        try {
            List<NameValuePair> queryPairs = new ArrayList<>();
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx > 0)
                    queryPairs.add(new NameValuePair(
                            URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                            URLDecoder.decode(pair.substring(idx + 1), "UTF-8")));
            }

            return queryPairs;
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String formQuery(List<NameValuePair> pairs) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;

        try {
            for (NameValuePair pair : pairs) {
                if (!first) builder.append("&");
                builder.append(URLEncoder.encode(pair.key(), "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(pair.value(""), "UTF-8"));

                first = false;
            }
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }

        return builder.toString();
    }

    public static boolean equals(List<?> a, List<?> b) {
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++)
            if (!Objects.equals(a.get(i), b.get(i))) return false;

        return true;
    }

    public static void shuffleArray(int[] ar) {
        Random rnd = ThreadLocalRandom.current();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    public static ArrayList<String> toStringsList(JSONArray array, boolean checkForDuplicates) throws JSONException {
        if (array == null) return new ArrayList<>();
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            String val = array.getString(i);
            if (!checkForDuplicates || !list.contains(val)) list.add(val);
        }
        return list;
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

    public static <T> HashMap<String, T> toMap(JSONObject obj, Class<T> valueClass) throws JSONException {
        HashMap<String, T> map = new HashMap<>();

        Iterator<String> iterator = obj.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            map.put(key, valueClass.cast(obj.get(key)));
        }

        return map;
    }

    public static JSONObject toJSONObject(Map<String, Object> map) throws JSONException {
        JSONObject obj = new JSONObject();
        if (map == null) return obj;

        for (Map.Entry<String, Object> entry : map.entrySet())
            obj.put(entry.getKey(), entry.getValue());

        return obj;
    }

    public static boolean isExpanded(View v) {
        return v.getVisibility() == View.VISIBLE;
    }

    public static void expand(final View v, @Nullable Animation.AnimationListener listener) {
        final int targetHeight;
        if (v.getMinimumHeight() != 0) {
            targetHeight = v.getMinimumHeight();
        } else {
            v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            targetHeight = v.getMeasuredHeight();
        }

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        if (listener != null) a.setAnimationListener(listener);

        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v, @Nullable Animation.AnimationListener listener) {
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

        if (listener != null) a.setAnimationListener(listener);

        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapseTitle(TextView v) {
        v.setSingleLine(true);
    }

    public static void expandTitle(TextView v) {
        v.setSingleLine(false);
    }

    @Nullable
    public static Drawable resolveAttrAsDrawable(@NonNull Context context, @AttrRes int id) {
        TypedArray a = context.obtainStyledAttributes(new int[]{id});
        Drawable drawable = a.getDrawable(0);
        a.recycle();
        return drawable;
    }

    @ColorInt
    public static int resolveAttrAsColor(@NonNull Context context, @AttrRes int id) {
        TypedArray a = context.obtainStyledAttributes(new int[]{id});
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    public static void animateCollapsingArrowBellows(View view, boolean expanded) {
        if (expanded) view.animate().rotation(0).setDuration(200).start();
        else view.animate().rotation(180).setDuration(200).start();
    }

    @NonNull
    public static EditText getEditText(TextInputLayout layout) {
        if (layout.getEditText() == null)
            throw new IllegalStateException("TextInputLayout hasn't a TextInputEditText");
        return layout.getEditText();
    }

    @NonNull
    public static String getText(TextInputLayout layout) {
        if (layout.getEditText() == null)
            throw new IllegalStateException("TextInputLayout hasn't a TextInputEditText");
        return layout.getEditText().getText().toString();
    }

    public static void setText(TextInputLayout layout, CharSequence val) {
        if (layout.getEditText() != null) layout.getEditText().setText(val);
    }

    @NonNull
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

    @NonNull
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

    public static void sendEmail(@NonNull Context context, @Nullable Throwable sendEx) {
        String version;
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ex) {
            version = context.getString(R.string.unknown);
        }

        Intent intent = new Intent(Intent.ACTION_SEND)
                .setType("message/rfc822")
                .putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.devgianluEmail)})
                .putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));

        String emailBody = "-------- DO NOT EDIT --------" +
                "\r\nOS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")" +
                "\r\nOS API Level: " + android.os.Build.VERSION.SDK_INT +
                "\r\nDevice: " + android.os.Build.DEVICE +
                "\r\nModel (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")" +
                "\r\nApplication version: " + version +
                "\r\nCrashlytics UID: " + Prefs.getString(CommonPK.ANALYTICS_USER_ID, null);

        if (sendEx != null) {
            emailBody += "\r\n\r\n";
            emailBody += Logging.getStackTrace(sendEx);
        }

        emailBody += "\r\n------------------------------------" + "\r\n\r\n\r\nProvide bug details\r\n";

        intent.putExtra(Intent.EXTRA_TEXT, emailBody);

        Logging.LogFile log = Logging.getLatestLogFile(context);
        if (log != null) {
            try {
                Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".logs", log);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
            } catch (IllegalArgumentException ex) {
                Logging.log(ex);
            }
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toaster.with(context).message(R.string.noMailClients).ex(ex).show();
        }
    }

    public static int manipulateAlpha(@ColorInt int color, float factor) {
        return Color.argb(Math.min(Math.round(Color.alpha(color) * factor), 255), Color.red(color), Color.green(color), Color.blue(color));
    }

    public static <T> int indexOf(T[] items, T item) {
        for (int i = 0; i < items.length; i++)
            if (items[i] == item)
                return i;

        return -1;
    }

    public static <T> int indexOf(Set<T> items, T item) {
        Iterator<T> iterator = items.iterator();
        int pos = 0;
        while (iterator.hasNext()) {
            if (Objects.equals(iterator.next(), item))
                return pos;

            pos++;
        }

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

    @NonNull
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

    @NonNull
    public static SimpleDateFormat getVerbalDateFormatter() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf;
    }

    @NonNull
    public static SimpleDateFormat getFullDateFormatter() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss dd/MM/yyyy", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf;
    }

    @NonNull
    public static SimpleDateFormat getFullVerbalDateFormatter() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, hh:mm:ss dd/MM/yyyy", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf;
    }

    public static boolean isDebug() {
        return DEBUG;
    }

    public static void setDebug(boolean debug) {
        CommonUtils.DEBUG = debug;
    }

    @NonNull
    public static String join(Object[] objs, String separator) {
        return join(Arrays.asList(objs), separator);
    }

    @NonNull
    public static String join(Collection<?> objs, String separator) {
        if (objs == null) return "";
        StringBuilder builder = new StringBuilder();

        boolean first = true;
        for (Object obj : objs) {
            if (!first) builder.append(separator);
            first = false;
            builder.append(obj.toString());
        }

        return builder.toString();
    }

    public static void setRecyclerViewTopMargin(Context context, RecyclerView.ViewHolder holder) {
        if (holder.itemView.getLayoutParams() == null) return;
        ((RecyclerView.LayoutParams) holder.itemView.getLayoutParams()).topMargin = holder.getLayoutPosition() == 0 ? (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics()) : 0;
    }

    public static void setRecyclerViewBottomMargin(Context context, RecyclerView.ViewHolder holder, int items) {
        if (holder.itemView.getLayoutParams() == null) return;
        ((RecyclerView.LayoutParams) holder.itemView.getLayoutParams()).bottomMargin = holder.getLayoutPosition() == items - 1 ? (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics()) : 0;
    }

    public static <T> boolean contains(T[] elements, T element) {
        for (T element1 : elements)
            if (element1 == element) return true;

        return false;
    }

    @NonNull
    public static JSONArray toJSONArray(String[] keys) {
        JSONArray array = new JSONArray();
        for (String key : keys) array.put(key);
        return array;
    }

    @NonNull
    public static JSONArray toJSONArray(Collection<String> keys, boolean skipNulls) {
        JSONArray array = new JSONArray();
        for (String key : keys) {
            if (skipNulls && key == null) continue;
            array.put(key);
        }
        return array;
    }

    @NonNull
    public static Integer[] toIntsList(String str, String separator) throws NumberFormatException {
        String[] split = str.split(separator);
        Integer[] ints = new Integer[split.length];
        for (int i = 0; i < split.length; i++) ints[i] = Integer.parseInt(split[i].trim());
        return ints;
    }

    public static void handleCollapseClick(ImageButton button, View target) {
        handleCollapseClick(button, target, null);
    }

    public static void handleCollapseClick(ImageButton button, View target, @Nullable Animation.AnimationListener listener) {
        animateCollapsingArrowBellows(button, isExpanded(target));
        if (isExpanded(target)) collapse(target, listener);
        else expand(target, listener);
    }

    public static String[] toStringArray(JSONArray jsonArray) throws JSONException {
        if (jsonArray == null) return null;

        String[] array = new String[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) array[i] = jsonArray.getString(i);
        return array;
    }

    @Nullable
    public static String getStupidString(JSONObject obj, String key) {
        String val = obj.optString(key, null);
        return val == null || val.equals("null") ? null : val;
    }

    public static boolean isStupidNull(JSONObject obj, String key) throws JSONException {
        return obj.isNull(key) || Objects.equals(obj.getString(key), "null");
    }

    @NonNull
    public static String randomString(int length, @NonNull Random random, @NonNull String chars) {
        if (length < 1) throw new IllegalArgumentException();
        if (chars.length() < 1) throw new IllegalArgumentException();

        StringBuilder str = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            str.append(chars.charAt(random.nextInt(chars.length())));

        return str.toString();
    }

    @NonNull
    public static String randomString(int length, @NonNull Random random) {
        return randomString(length, random, LOT_OF_CHARS);
    }

    public static int countOccurrences(@NonNull String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) if (str.charAt(i) == c) count++;
        return count;
    }
}
