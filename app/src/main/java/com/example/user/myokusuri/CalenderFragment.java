package com.example.user.myokusuri;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalenderFragment extends Fragment {
    private final String CLASS_NAME=getClass().getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Calendar mCalendar;

    public CalenderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalenderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CalenderFragment newInstance(String param1, String param2) {
        CalenderFragment fragment = new CalenderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d( CLASS_NAME, "onCreate() run." );
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d( CLASS_NAME, "onCreateView() run." );
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calender, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d( CLASS_NAME, "onViewCreated() run." );
        setHasOptionsMenu( true ); //オプションメニューを使用する事を宣言
        super.onViewCreated(view, savedInstanceState);

        //actionBar 設定
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle( R.string.title_calender ); //タイトル変更
        actionBar.setDisplayHomeAsUpEnabled( true ); //HOMEへ戻る「←」表示

        //カレンダー設定
        mCalendar = Calendar.getInstance(); //カレンダーのインスタンスを取得
        int year = mCalendar.get( Calendar.YEAR );
        int month = mCalendar.get( Calendar.MONTH )+1;
        TextView targetMonth = getActivity().findViewById( R.id.year_month );
        targetMonth.setText( year+"年 "+month+"月" );

        // 日付行の追加
        ViewGroup viewGroup = getActivity().findViewById( R.id.calender_date );
        for ( int i=0; i<5; i++ ) {
            getActivity().getLayoutInflater().inflate( R.layout.calender_date_row, viewGroup );
        }
    }

    //オーバーフローメニュー設定
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d( CLASS_NAME, "onCreateOptionsMenu() run. ["+menu+"]" );
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate( R.menu.menu_calender, menu );
    }

    //メニューの表示／非表示をコントロール
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.d( CLASS_NAME, "onPrepareOptionsMenu() run. ["+menu+"]" );
        super.onPrepareOptionsMenu(menu);
    }

    //メニューを選択時
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d( CLASS_NAME, "onOptionsItemSelected() run. ["+item+"]" );
        switch ( item.getItemId() ) {
            case android.R.id.home :
                Log.d( CLASS_NAME, "onOptionsItemSelected() [ android.R.id.home ]" );
                FragmentManager fragmentManager = getFragmentManager();
                if ( fragmentManager.getBackStackEntryCount()>0 ) {
                    fragmentManager.popBackStack();
                    //actionBar 設定
                    ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
                    actionBar.setTitle( R.string.app_name ); //タイトル変更
                    actionBar.setDisplayHomeAsUpEnabled( false ); //HOMEへ戻る「←」表示
                }
                else {
                    Log.d( CLASS_NAME, "BackStack is none." );
                }
                break;
            default:
                Log.d( CLASS_NAME, "onOptionsItemSelected() [ Undefined ]" );
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // yyyy/mm/dd
    public String getCurrentDate() {
        Log.d( CLASS_NAME, "getCurrentDate() run." );
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get( Calendar.YEAR );
        int month = calendar.get( Calendar.MONTH ) + 1;
        int date = calendar.get( Calendar.DAY_OF_MONTH );

        return String.valueOf( year ) + "/" + String.valueOf( month ) + "/" + String.valueOf( date );
    }
}
