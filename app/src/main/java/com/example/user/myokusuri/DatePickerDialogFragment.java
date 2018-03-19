package com.example.user.myokusuri;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

// * Created by user on 2018/03/19.

public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    final String TAG = getClass().getSimpleName();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d( TAG, "onCreateDialog() start." );

        Calendar calender = Calendar.getInstance();
        int year = calender.get( Calendar.YEAR );
        int month = calender.get( Calendar.MONTH );
        int dayOfMonth = calender.get( Calendar.DAY_OF_MONTH );

//        DatePickerDialog datePickerDialog = new DatePickerDialog( getContext(), this, year, month, dayOfMonth );
//        return datePickerDialog;

        return new DatePickerDialog( getContext(), this, year, month, dayOfMonth );
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        Log.d( TAG, "onDateSet() start." );
    }
}
