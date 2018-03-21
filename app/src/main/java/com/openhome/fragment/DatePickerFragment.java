package com.openhome.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;

/**
 * Created by Bhargav on 10/31/2015.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private EditText selectedDateView;

    public static DatePickerFragment newInstance(EditText selectedDateView) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        //args.putInt("count", arg);
        datePickerFragment.setArguments(args);
        datePickerFragment.setTextView(selectedDateView);
        return datePickerFragment;
    }

    public void setTextView(EditText selectedDateView) {
        this.selectedDateView = selectedDateView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        if(selectedDateView != null)
            selectedDateView.setText(day +"-"+ (month+1) +"-"+year);
    }
}
