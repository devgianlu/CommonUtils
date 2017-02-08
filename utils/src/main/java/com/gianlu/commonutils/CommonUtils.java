package com.gianlu.commonutils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.Keep;
import android.support.v7.app.AlertDialog;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused,WeakerAccess")
@Keep
public class CommonUtils {
    @SuppressWarnings("CanBeFinal")
    public static boolean DEBUG = BuildConfig.DEBUG;

    public static boolean hasInternetAccess() {
        try {
            HttpURLConnection url = (HttpURLConnection) new URL("http://clients3.google.com/generate_204").openConnection();
            //noinspection SpellCheckingInspection
            url.setRequestProperty("User-Agent", "Connectivity test");
            url.setRequestProperty("Connection", "close");
            url.setConnectTimeout(1000);
            url.connect();
            return url.getResponseCode() == 204 && url.getContentLength() == 0;
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

    public static void expandTitle(TextView v) {
        v.setSingleLine(false);
        v.setEllipsize(null);
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

    public static void animateCollapsingArrowList(ImageButton view, boolean expanded) {
        if (expanded)
            view.animate()
                    .rotation(0)
                    .setDuration(200)
                    .start();
        else
            view.animate()
                    .rotation(90)
                    .setDuration(200)
                    .start();
    }

    public static void animateCollapsingArrowBellows(ImageButton view, boolean expanded) {
        if (expanded)
            view.animate()
                    .rotation(0)
                    .setDuration(200)
                    .start();
        else
            view.animate()
                    .rotation(180)
                    .setDuration(200)
                    .start();
    }

    public static View fastHorizontalLinearLayoutWeightDummy(Context context, int weight) {
        View view = new View(context);
        view.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight));

        return view;
    }

    public static View fastVerticalLinearLayoutWeightDummy(Context context, int weight) {
        View view = new View(context);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, weight));

        return view;
    }

    public static void showDialog(Activity activity, final Dialog dialog) {
        if (activity == null || activity.isFinishing() || dialog == null) return;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        });
    }

    public static void showDialog(Activity activity, final AlertDialog.Builder builder) {
        if (activity == null || activity.isFinishing() || builder == null) return;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder.create().show();
            }
        });
    }

    public static TextView fastTextView(Context context, String text, int textAlignment) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextAlignment(textAlignment);

        return textView;
    }

    public static TextView fastTextView(Context context, String text) {
        return fastTextView(context, text, TextView.TEXT_ALIGNMENT_VIEW_START);
    }

    public static TextView fastTextView(Context context, Spanned text) {
        TextView textView = new TextView(context);
        textView.setText(text);

        return textView;
    }

    public static LinearLayout fastLinearLayout(Context context, int orientation, int padding) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(orientation);
        int pad = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding, context.getResources().getDisplayMetrics());
        layout.setPadding(pad, pad, pad, pad);

        return layout;
    }

    private static ProgressDialog fastIndeterminateProgressDialog(Context context, String message) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage(message);
        pd.setIndeterminate(true);
        pd.setCancelable(false);
        return pd;
    }

    public static ProgressDialog fastIndeterminateProgressDialog(Context context, int message) {
        return fastIndeterminateProgressDialog(context, context.getString(message));
    }

    public static String dimensionFormatter(float v) {
        if (v <= 0) {
            return "0 B";
        } else {
            final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
            int digitGroups = (int) (Math.log10(v) / Math.log10(1000));
            if (digitGroups > 4)
                return "∞ B";
            return new DecimalFormat("#,##0.#").format(v / Math.pow(1000, digitGroups)) + " " + units[digitGroups];
        }
    }

    public static String speedFormatter(float v) {
        if (v <= 0) {
            return "0 B/s";
        } else {
            final String[] units = new String[]{"B/s", "KB/s", "MB/s", "GB/s", "TB/s"};
            int digitGroups = (int) (Math.log10(v) / Math.log10(1000));
            if (digitGroups > 4)
                return "∞ B/s";
            return new DecimalFormat("#,##0.#").format(v / Math.pow(1000, digitGroups)) + " " + units[digitGroups];
        }
    }

    public static void sendEmail(Activity activity, String appName) {
        String version;
        try {
            version = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException ex) {
            version = activity.getString(R.string.unknown);
        }

        Intent intent = new Intent(Intent.ACTION_SEND)
                .setType("message/rfc822")
                .putExtra(Intent.EXTRA_EMAIL, new String[]{activity.getString(R.string.email)})
                .putExtra(Intent.EXTRA_SUBJECT, appName)
                .putExtra(Intent.EXTRA_TEXT, "OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")" +
                        "\nOS API Level: " + android.os.Build.VERSION.SDK_INT +
                        "\nDevice: " + android.os.Build.DEVICE +
                        "\nModel (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")" +
                        "\nApplication version: " + version);
        try {
            activity.startActivity(Intent.createChooser(intent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            CommonUtils.UIToast(activity, ToastMessage.NO_EMAIL_CLIENT);
        }
    }

    public static String timeFormatter(Long sec) {
        if (sec == null) return "∞";

        int day = (int) TimeUnit.SECONDS.toDays(sec);
        long hours = TimeUnit.SECONDS.toHours(sec) -
                TimeUnit.DAYS.toHours(day);
        long minute = TimeUnit.SECONDS.toMinutes(sec) -
                TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(sec));
        long second = TimeUnit.SECONDS.toSeconds(sec) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(sec));

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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("SimpleDateFormat")
    public static void logCleaner(Context context) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);

        for (File file : context.getFilesDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.toLowerCase().endsWith(".log") || s.toLowerCase().endsWith(".secret");
            }
        })) {
            try {
                if (new SimpleDateFormat("d-LL-yyyy").parse(file.getName().replace(".log", "").replace(".secret", "")).before(cal.getTime())) {
                    file.delete();
                }
            } catch (ParseException ignored) {}
        }
    }

    public static void secretLog(Context context, Throwable exx) {
        if (DEBUG)
            exx.printStackTrace();

        try {
            FileOutputStream fOut = context.openFileOutput(new SimpleDateFormat("d-LL-yyyy", Locale.getDefault()).format(new java.util.Date()) + ".secret", Context.MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            osw.write(new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(new java.util.Date()) + " >> " + exx.toString() + "\n" + Arrays.toString(exx.getStackTrace()) + "\n\n");
            osw.flush();
            osw.close();
        } catch (IOException ignored) {}
    }

    public static void logMe(Context context, Throwable ex) {
        if (DEBUG)
            ex.printStackTrace();
        if (ex == null)
            return;
        logMe(context, ex.getMessage(), true);
    }

    public static void logMe(Context context, String message, boolean isError) {
        if (message == null)
            message = "No message given";

        try {
            FileOutputStream fOut = context.openFileOutput(new SimpleDateFormat("d-LL-yyyy", Locale.getDefault()).format(new java.util.Date()) + ".log", Context.MODE_APPEND);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            osw.write((isError ? "--ERROR--" : "--INFO--") + new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(new java.util.Date()) + " >> " + message.replace("\n", " ") + "\n");
            osw.flush();
            osw.close();
        } catch (IOException ignored) {}
    }

    public static void UIToast(final Activity context, final String text) {
        UIToast(context, text, Toast.LENGTH_SHORT);
    }

    public static void UIToast(final Activity context, final String text, final int duration) {
        if (context == null)
            return;
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, duration).show();
            }
        });
    }

    public static void UIToast(final Activity context, final String text, final int duration, Runnable extra) {
        if (context == null)
            return;
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, duration).show();
            }
        });
        context.runOnUiThread(extra);
    }

    public static void UIToast(final Activity context, final ToastMessage message) {
        if (context == null)
            return;
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message.toString() + (message.isError() ? " See logs for more..." : ""), Toast.LENGTH_SHORT).show();
            }
        });
        CommonUtils.logMe(context, message.toString(), message.isError());
    }

    public static void UIToast(final Activity context, final ToastMessage message, final String message_extras) {
        if (context == null)
            return;
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        CommonUtils.logMe(context, message + " Details: " + message_extras, message.isError());
    }

    public static void UIToast(final Activity context, final ToastMessage message, final Throwable exception) {
        if (context == null)
            return;
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        if (exception == null)
            return;

        CommonUtils.logMe(context, message + " Details: " + exception.getMessage(), message.isError());
        CommonUtils.secretLog(context, exception);
    }

    public static void UIToast(final Activity context, final ToastMessage message, final String message_extras, Runnable extra) {
        if (context == null)
            return;
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        context.runOnUiThread(extra);
        CommonUtils.logMe(context, message + " Details: " + message_extras, message.isError());
    }

    public static void UIToast(final Activity context, final ToastMessage message, final Throwable exception, Runnable extra) {
        if (context == null)
            return;
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        context.runOnUiThread(extra);

        if (exception == null)
            return;

        CommonUtils.logMe(context, message + " Details: " + exception.getMessage(), message.isError());
        CommonUtils.secretLog(context, exception);
    }

    public static void UIToast(final Activity context, final ToastMessage message, Runnable extra) {
        if (context == null)
            return;
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        context.runOnUiThread(extra);
        CommonUtils.logMe(context, message.toString(), message.isError());
    }

    @Keep
    public static class ToastMessage {
        public static final ToastMessage NO_EMAIL_CLIENT = new CommonUtils.ToastMessage("There are no email clients installed.", true);
        public static final ToastMessage COPIED_TO_CLIPBOARD = new ToastMessage("Copied to clipboard!", false);
        public static final ToastMessage LOGS_DELETED = new ToastMessage("Deleted all logs.", false);
        public static final ToastMessage FATAL_EXCEPTION = new ToastMessage("Fatal exception! Don't worry...", true);

        private final String message;
        private final boolean isError;

        public ToastMessage(String message, boolean isError) {
            this.message = message;
            this.isError = isError;
        }

        public String getMessage() {
            return message;
        }

        public boolean isError() {
            return isError;
        }

        public String toString(String extra) {
            return message + " " + extra;
        }

        @Override
        public String toString() {
            return message;
        }
    }
}
