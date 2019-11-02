package com.gianlu.commonutils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class CommonUtils {
    public static final String LOT_OF_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"£$%&/()=?^-_.:,;<>|\\*[]";
    private static boolean DEBUG = BuildConfig.DEBUG;

    public static boolean isNightModeOn(@NonNull Context context, boolean fallback) {
        int mode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (mode) {
            case Configuration.UI_MODE_NIGHT_YES:
                return true;
            case Configuration.UI_MODE_NIGHT_NO:
                return false;
            default:
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                return fallback;
        }
    }

    public static void setTextColor(@NonNull TextView view, @ColorRes int res) {
        view.setTextColor(ContextCompat.getColor(view.getContext(), res));
    }

    public static void setTextColorFromAttr(@NonNull TextView view, @AttrRes int res) {
        view.setTextColor(resolveAttrAsColor(view.getContext(), res));
    }

    public static void zip(List<? extends File> files, File dest) throws IOException {
        byte[] buffer = new byte[8192];
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(dest))) {
            for (File file : files) {
                out.putNextEntry(new ZipEntry(file.getName()));
                try (FileInputStream in = new FileInputStream(file)) {
                    int read;
                    while ((read = in.read(buffer)) != -1)
                        out.write(buffer, 0, read);
                } finally {
                    out.closeEntry();
                }
            }
        }
    }

    public static boolean isVisible(@NonNull Fragment fragment) {
        View root = fragment.getView();
        return root != null && root.getGlobalVisibleRect(new Rect());
    }

    public static void setBackgroundColor(@NonNull FloatingActionButton fab, @ColorRes int color) {
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(fab.getContext(), color)));
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

    public static void collapse(@NonNull final View v, @Nullable Animation.AnimationListener listener) {
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

    public static void collapseTitle(@NonNull TextView v) {
        v.setSingleLine(true);
    }

    public static void expandTitle(@NonNull TextView v) {
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

    public static void animateCollapsingArrowBellows(@NonNull View view, boolean expanded) {
        if (expanded) view.animate().rotation(0).setDuration(200).start();
        else view.animate().rotation(180).setDuration(200).start();
    }

    public static void clearErrorOnEdit(@NonNull final TextInputLayout layout) {
        getEditText(layout).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                layout.setErrorEnabled(false);
            }
        });
    }

    @NonNull
    public static EditText getEditText(@NonNull TextInputLayout layout) {
        if (layout.getEditText() == null)
            throw new IllegalStateException("TextInputLayout hasn't a TextInputEditText");
        return layout.getEditText();
    }

    @NonNull
    public static String getText(@NonNull TextInputLayout layout) {
        if (layout.getEditText() == null)
            throw new IllegalStateException("TextInputLayout hasn't a TextInputEditText");
        return layout.getEditText().getText().toString();
    }

    public static void setText(@NonNull TextInputLayout layout, CharSequence val) {
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
    public static String join(@NonNull Object[] objs, @NonNull String separator) {
        return join(Arrays.asList(objs), separator);
    }

    @NonNull
    public static String join(@NonNull Collection<?> objs, @NonNull String separator, boolean copy) {
        return join(copy ? new ArrayList<>(objs) : objs, separator);
    }

    @NonNull
    public static <T> String join(@NonNull Collection<T> objs, @NonNull String separator, @Nullable ToString<T> func) {
        StringBuilder builder = new StringBuilder();

        boolean first = true;
        for (T obj : objs) {
            if (!first) builder.append(separator);
            first = false;
            builder.append(func == null ? obj.toString() : func.toString(obj));
        }

        return builder.toString();
    }

    @NonNull
    public static String join(@NonNull JSONArray array, @NonNull String separator) throws JSONException {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length(); i++) {
            if (i > 0) builder.append(separator);
            builder.append(array.getString(i));
        }

        return builder.toString();
    }


    @NonNull
    public static String join(@NonNull Collection<?> objs, @NonNull String separator) {
        return join(objs, separator, null);
    }

    public static void setRecyclerViewTopMargin(@NonNull RecyclerView.ViewHolder holder) {
        if (holder.itemView.getLayoutParams() == null) return;

        Context context = holder.itemView.getContext();
        ((RecyclerView.LayoutParams) holder.itemView.getLayoutParams()).topMargin = holder.getLayoutPosition() == 0 ? (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics()) : 0;
    }

    public static void setRecyclerViewBottomMargin(@NonNull RecyclerView.ViewHolder holder, int items) {
        if (holder.itemView.getLayoutParams() == null) return;

        Context context = holder.itemView.getContext();
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

    @NonNull
    public static String randomString(int length) {
        return randomString(length, ThreadLocalRandom.current(), LOT_OF_CHARS);
    }

    public static int countOccurrences(@NonNull String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) if (str.charAt(i) == c) count++;
        return count;
    }

    public static void setTextPlural(@NonNull TextView view, @PluralsRes int res, int num, Object... args) {
        view.setText(view.getContext().getResources().getQuantityString(res, num, args));
    }

    public static void setText(TextView view, @StringRes int res, Object... args) {
        view.setText(view.getContext().getResources().getString(res, args));
    }

    public static void setImageTintColor(ImageView view, @ColorRes int res) {
        view.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(view.getContext(), res)));
    }

    @NonNull
    public static String readEntirely(@NonNull InputStream stream) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int count;
        try {
            while ((count = stream.read(buffer)) != -1) out.write(buffer, 0, count);
            return new String(out.toByteArray());
        } finally {
            stream.close();
        }
    }

    public interface ToString<T> {
        @NonNull
        String toString(@NonNull T obj);
    }
}
