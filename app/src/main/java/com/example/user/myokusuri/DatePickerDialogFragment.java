package com.example.user.myokusuri;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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

        //（本当は要らないけれど）練習のつもりでダイアログへ引数を渡してみた。
        // -----> 使ってません。
        Bundle argDate = getArguments();
        int year01  = Integer.parseInt( argDate.getString( "ARG_YEAR" ) );
        int month01 = Integer.parseInt( argDate.getString( "ARG_MONTH" ) ) - 1;
        int dayOfMonth01= Integer.parseInt( argDate.getString( "ARG_DATE" ) );

        //スピナーモード、カレンダーモード
        // api21 より前はスピナーモードしか使用できない・・・。setSpinnersShown()、setCalendarShown()は使用不可になっていた・・・。
//        DatePickerDialog datePickerDialog = new DatePickerDialog( getContext(), this, year, month, dayOfMonth );
//        datePickerDialog.setCalenderShow( true );
//        return datePickerDialog;

        return new DatePickerDialog( getContext(), this, year, month, dayOfMonth );
//        return new DatePickerDialog( getContext(), this, year01, month01, dayOfMonth01 );
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthIndex, int date ) {
        Log.d( TAG, "onDateSet() start. [ " + year + "/" + monthIndex+1 + "/" + date + " ]" );

        Fragment targetFragment = getTargetFragment(); //表示中のフラグメント
        if ( targetFragment==null ) {
            dismiss();
            return;
        }

        int[] data = new int[ 3 ];
        data[ 0 ] = year;
        data[ 1 ] = monthIndex + 1;
        data[ 2 ] = date;

        Intent returnVal = new Intent();
        returnVal.putExtra( "SET_DATE", data );
        targetFragment.onActivityResult( getTargetRequestCode(), Activity.RESULT_OK, returnVal );
    }
}
