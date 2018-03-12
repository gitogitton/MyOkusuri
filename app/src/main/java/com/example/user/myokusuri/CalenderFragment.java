package com.example.user.myokusuri;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Calendar;

public class CalenderFragment extends Fragment {
    private final String CLASS_NAME=getClass().getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int mCurrentYear;
    private int mCurrentMonth;

    enum DayOfTheWeek {
        SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
    };

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
        // Inflate the shohousen for this fragment
        View view = inflater.inflate(R.layout.fragment_calender, container, false);
        //Backキー対応
        view.setFocusableInTouchMode( true ); //このViewがタッチモードでフォーカスを受け取る。
        view.requestFocus(); //このViewにフォーカスを移す。
        //Fragmentでのキー押下時のリスナー登録
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                Log.d( CLASS_NAME, "onKey() [i/keyEvent="+i+"/"+keyEvent+"]" );
                if ( i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_DOWN ) {
                    Log.d( CLASS_NAME, "Back key" );
                    ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
                    actionBar.setTitle( R.string.app_name ); //タイトル変更
                    actionBar.setDisplayHomeAsUpEnabled( false ); //HOMEへ戻る「←」非表示
                }
                return false;
            }
        });
        return view;
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
        //現在年月を設定
        Calendar calendar = Calendar.getInstance(); //カレンダーのインスタンスを取得
        mCurrentYear = calendar.get( Calendar.YEAR );
        mCurrentMonth = calendar.get( Calendar.MONTH )+1;
        TextView targetMonth = getActivity().findViewById( R.id.year_month );
        targetMonth.setText( mCurrentYear+"年 "+mCurrentMonth+"月" );
        // 日付行の追加
        ViewGroup viewGroup = getActivity().findViewById( R.id.calender_date_part );
        for ( int i=0; i<6; i++ ) {
            View dateRowText = getActivity().getLayoutInflater().inflate( R.layout.calender_date_row, viewGroup );
            Log.d( CLASS_NAME, "dateRowText="+dateRowText );
        }
        //カレンダー作成
        setCalender( mCurrentYear, mCurrentMonth );
        //リスナー登録
        setListener();
    }

    //リスナー登録
    private void setListener() {
        Log.d( CLASS_NAME, "setOnListener() run." );
        //前月、次月ボタンリスナー登録
        Button buttonPrev = getActivity().findViewById( R.id.button_prev );
        Button buttonNext = getActivity().findViewById( R.id.button_next );
        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( CLASS_NAME, "button prev. onClick()" );
                setPrevMonth();
            }
        });
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( CLASS_NAME, "button next. onClick()" );
                setNextMonth();
            }
        });
        //カレンダー日付部
        ViewGroup viewGroup = getActivity().findViewById( R.id.calender_date_part );
        for ( int i=0; i<viewGroup.getChildCount(); i++ ) {
            TableRow tableRow = (TableRow)viewGroup.getChildAt( i );
            for ( int j=0; j<tableRow.getChildCount(); j++ ) {
                TextView textView = (TextView)tableRow.getChildAt( j );
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d( CLASS_NAME, "date is clicked. "+((TextView)view).getText() );
                        editMemo( (TextView)view );
                    }
                });
            } //for (j)
        } //for (i)
    }

    //該当日付の記録を開き、編集可とする。
    private void editMemo( TextView textView ) {
        Log.d( CLASS_NAME, "editMemo() run." );
        Log.d( CLASS_NAME, "selected date : "+mCurrentYear+"/"+mCurrentMonth+"/"+textView.getText() );
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        String inputDate = String.valueOf( mCurrentYear )+"/"+String.valueOf( mCurrentMonth )+"/"+textView.getText();
        fragmentTransaction.replace( R.id.container, DetailEditFragment.newInstance( inputDate, "" ) );
        fragmentTransaction.addToBackStack( null );
        fragmentTransaction.commit();
    }

    //前月カレンダー表示
    private void setPrevMonth() {
        //年月編集
        mCurrentMonth--;
        if ( mCurrentMonth<1  ) {
            mCurrentYear--;
            mCurrentMonth = 12;
        }
        //カレンダー作成
        setCalender( mCurrentYear, mCurrentMonth );
        TextView targetMonth = getActivity().findViewById( R.id.year_month );
        targetMonth.setText( mCurrentYear+"年 "+mCurrentMonth+"月" );
    }

    //次月カレンダー表示
    private void setNextMonth() {
        //年月編集
        mCurrentMonth++;
        if ( mCurrentMonth>12  ) {
            mCurrentYear++;
            mCurrentMonth = 1;
        }
        //カレンダー作成
        setCalender( mCurrentYear, mCurrentMonth );
        TextView targetMonth = getActivity().findViewById( R.id.year_month );
        targetMonth.setText( mCurrentYear+"年 "+mCurrentMonth+"月" );
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

    //月末日付を取得
    private int getLastDateOfMonth( int year, int month ) {
        Log.d( CLASS_NAME, "getMaximumDate() run. [year="+year+"/month="+month+"]" );
        Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.YEAR, year );
        calendar.set( Calendar.MONTH, month-1 );
        int maxDate = calendar.getActualMaximum( Calendar.DATE );
        return maxDate;
    }

    //カレンダー作成（month は １～１２で指定）
    private void setCalender( int year, int month ) {
        Log.d( CLASS_NAME, "setCalender() run. [year="+year+"/month="+month+"]" );
        //月の最大日付と1日の曜日を取得
        Calendar calendar = Calendar.getInstance();
        calendar.set( Calendar.YEAR, year );
        calendar.set( Calendar.MONTH, month-1 );
        calendar.set( Calendar.DAY_OF_MONTH, 1 );
        int lastDate = calendar.getActualMaximum( Calendar.DATE ); //該当月の最終日付
        int dayOfTheWeek = calendar.get( Calendar.DAY_OF_WEEK ); //曜日：SUNDAY(1)、MONDAY(2)、TUESDAY(3)、WEDNESDAY(4)、THURSDAY(5)、FRIDAY(6)、SATURDAY(7)
        Log.d( CLASS_NAME, "youbi="+String.valueOf( dayOfTheWeek )+" / lastDate="+String.valueOf( lastDate ) );
        ViewGroup viewGroup = getActivity().findViewById( R.id.calender_date_part );
        int tableRowCount=0;
        int textViewCount=0;
        tableRowCount = viewGroup.getChildCount();
        Log.d( CLASS_NAME, "tableRowCount="+tableRowCount );

//        if ( tableRowCount>0 ) {
//            TableRow tableRow = (TableRow) viewGroup.getChildAt( 0 );
//            textViewCount = tableRow.getChildCount();
//            Log.d( CLASS_NAME, "textViewCount="+textViewCount );
//            if ( textViewCount>0 ) {
//                TextView textView = (TextView) tableRow.getChildAt(0 );
//            }
//        }

        int date = 1;
        for ( int i=0; i<tableRowCount; i++ ) {
            Log.d( CLASS_NAME, "i/date/lastDate="+i+"/"+date+"/"+lastDate );
            TableRow tableRow = (TableRow)viewGroup.getChildAt( i );
            tableRow.setVisibility( View.VISIBLE );
            textViewCount = tableRow.getChildCount();
            for ( int j=0; tableRow!=null && j<textViewCount; j++ ) {
                TextView textView = (TextView)tableRow.getChildAt( j );
                if ( i==0 && (j+1)<dayOfTheWeek ) { //月の最初の週、且つ、開始曜日でない場合はnullクリア。
                    textView.setText( R.string.calender_date_none );
                }
                else {
                    if ( date<=lastDate ) { //月の最大日付を越えない場合はテキストセット。越えればnullクリア。
                        textView.setText( String.valueOf( date ) );
                        Log.d( CLASS_NAME, "date="+date );
                        date ++;
                    }
                    else {
                        if ( j==0 && date>lastDate ) { //最初の曜日である場合、日付カウントが月の日付の最大値を越えていれば行を非表示にして処理を終了する。
                            tableRow.setVisibility( View.GONE );
                            Log.d( CLASS_NAME, "date>lastDate" );
                            break;
                        }
                        else {
                            textView.setText(R.string.calender_date_none);
                            Log.d(CLASS_NAME, "date is none");
                        }
                    }
                }
            }//for (j)
        }//for (i)
    }

    @Override
    public void onStart() {
        Log.d( CLASS_NAME, "CalenderFragment.onStart() start." );
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d( CLASS_NAME, "CalenderFragment.onResume() start." );
        super.onResume();
    }

}
