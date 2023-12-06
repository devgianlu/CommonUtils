package com.gianlu.commonutils.dialogs;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.gianlu.commonutils.R;

import java.util.Calendar;
import java.util.Date;

public class MaterialDatePickerDialog extends DialogFragment {
    private DatePickerDialog.OnDateSetListener listener;

    @NonNull
    public static MaterialDatePickerDialog get(@Nullable String title, @NonNull Date selected, @NonNull DatePickerDialog.OnDateSetListener listener) {
        return get(title, selected, null, null, listener);
    }

    @NonNull
    public static MaterialDatePickerDialog get(@Nullable String title, @NonNull Date selected, @Nullable Date min, @Nullable Date max, @NonNull DatePickerDialog.OnDateSetListener listener) {
        MaterialDatePickerDialog dialog = new MaterialDatePickerDialog();
        dialog.listener = listener;
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putSerializable("min", min);
        args.putSerializable("max", max);
        args.putSerializable("selected", selected);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_date_picker, container, false);

        DatePicker picker = layout.findViewById(R.id.datePicker_picker);
        TextView title = layout.findViewById(R.id.datePicker_title);

        Button ok = layout.findViewById(R.id.datePicker_ok);
        ok.setOnClickListener(view -> {
            if (listener != null)
                listener.onDateSet(picker, picker.getYear(), picker.getMonth(), picker.getDayOfMonth());

            dismiss();
        });

        Button cancel = layout.findViewById(R.id.datePicker_cancel);
        cancel.setOnClickListener(view -> dismiss());

        String titleStr = null;
        Date min = null;
        Date max = null;
        Date current;
        Bundle args = getArguments();
        if (args == null) {
            current = null;
        } else {
            current = (Date) args.getSerializable("selected");
            min = (Date) args.getSerializable("min");
            max = (Date) args.getSerializable("max");
            titleStr = args.getString("title");
        }

        if (titleStr != null) {
            title.setVisibility(View.VISIBLE);
            title.setText(titleStr);
        } else {
            title.setVisibility(View.GONE);
        }


        Calendar cal = Calendar.getInstance();
        cal.setTime(current == null ? new Date() : current);

        picker.init(cal.get(Calendar.YEAR), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.DAY_OF_MONTH), null);

        if (min != null) picker.setMinDate(min.getTime());
        if (max != null) picker.setMaxDate(max.getTime());

        return layout;
    }
}
