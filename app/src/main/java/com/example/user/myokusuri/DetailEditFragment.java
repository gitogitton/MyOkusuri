package com.example.user.myokusuri;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
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
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailEditFragment extends Fragment {
    private final String CLASS_NAME = getClass().getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    //処方箋のバックグランドカラー（処方箋ごとにトグルする）
    private final int mBackgroundColor_1 = Color.parseColor( "#e0ffff" );
    private final int mBackgroundColor_2 = Color.parseColor( "#faf0e6" );

    private ArrayList<ShohousenData> mShohousenList = new ArrayList<>();  //処方箋リスト

    private View mView;
    private String mDate;

    public DetailEditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailEditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailEditFragment newInstance(String param1, String param2) {
        DetailEditFragment fragment = new DetailEditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the shohousen for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_edit, container, false);
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
                }
                return false;
            }
        });

        mView = view;
        mDate = mParam1;

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d( CLASS_NAME, "onViewCreated() run."+mParam1 );
        setHasOptionsMenu( true ); //オプションメニューを使用する事を宣言
        super.onViewCreated(view, savedInstanceState);
        //actionBar設定
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle( R.string.title_edit ); //タイトル
        actionBar.setHomeButtonEnabled( true ); //HOMEへ戻る「←」セット
        //日付設定
        TextView textView = getActivity().findViewById( R.id.select_date );
        textView.setText( mDate );
        //処方箋域を初期表示
        addShohou( mDate );
        //リスナー登録
        setListener();
    }

    private void setListener() {
        //処方箋［追加］［削除］［登録］ボタン取得
        Button shohousenButton = getActivity().findViewById( R.id.shohousen_copy_button );
        Button shohousenDelButton = getActivity().findViewById( R.id.shohousen_del_button );
        Button shohousenSaveButton = getActivity().findViewById( R.id.shohousen_save_button );
        //薬［追加］ボタン取得
        Button kusuriTuikaButton = getActivity().findViewById( R.id.kusuri_tuika_button );
        //薬［削除］用「×」マークのView取得
        ImageView kusuriDel = getActivity().findViewById( R.id.imageView );

        shohousenSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( CLASS_NAME, "処方箋 保存ボタン 押下" );
                saveShohou( view );
            }
        });
        shohousenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( CLASS_NAME, "処方箋 追加ボタン 押下" );
                addShohou( mDate );
            }
        });
        shohousenDelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( CLASS_NAME, "処方箋 削除ボタン 押下" );

                View focusView = getActivity().getCurrentFocus();
                Log.d( CLASS_NAME, "focus -> "+focusView );

                ViewParent vp = focusView.getParent();
                while( vp!=null ) {
                    ViewParent viewParent = vp.getParent();
                    Log.d( CLASS_NAME, "parent view -> "+viewParent );
                    if ( viewParent instanceof TableLayout ) {
                        Log.d( CLASS_NAME, "find tableLayout !!" );
                    }
                    vp = viewParent;
                }
            }
        });
        kusuriTuikaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( CLASS_NAME, "薬 追加ボタン 押下" );

                //選択中の処方箋viewを取得
//                ボタンの親Viewをとる！！！（？）

                //薬の入力域を追加
            }
        });
//        kusuriDel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d( CLASS_NAME, "kusuriDel listenner run."+view );
//
//                ViewParent vp = view.getParent();
//                TableRow tableRow = (TableRow)vp;
//                Log.d( CLASS_NAME, ""+tableRow.getId() ); //id=kusuri
//                if ( vp instanceof ViewGroup ) {
//                    Log.d( CLASS_NAME, "ViewParent instanceof ViewGroup" );
//                }
//
//                vp = vp.getParent();
//                Log.d( CLASS_NAME, ""+vp ); //id=shohousen_header
//
//            }
//        });
    }

    private void saveShohou( View view ) {

        Log.d( CLASS_NAME, "saveShohou() start. [view="+view+"]" );

        ScrollView scrollView = (ScrollView) mView.findViewById( R.id.scroll_shohousen );
        Log.d( CLASS_NAME, "scrollView = "+scrollView );

        Log.d( CLASS_NAME, "scrollView.getChildCount()="+scrollView.getChildCount() );

        LinearLayout linearLayout = (LinearLayout)scrollView.getChildAt( 0 ); //id=shohousen

        Log.d( CLASS_NAME, "linearLayout.getChildCount()="+linearLayout.getChildCount() );

        TableLayout tableLayout = (TableLayout)linearLayout.getChildAt( 0 ); //id=shohousen_header (薬局名など)

        TableLayout tableLayout1 = linearLayout.findViewById( R.id.shohousen );

        Log.d( CLASS_NAME, "linearLayout = "+linearLayout );
        Log.d( CLASS_NAME, "tableLayout = "+tableLayout );
        Log.d( CLASS_NAME, "tableLayout1 = "+tableLayout1 );

    }

    private void addShohou( String date ) {

        Log.d( CLASS_NAME, "addShohou() start." );

        int id = View.generateViewId();

        ShohousenData shohousenData = new ShohousenData();
        shohousenData.setViewId( id );
        shohousenData.setShohouDate( mDate );
        mShohousenList.add( shohousenData );

        //処方箋Viewを動的作成し、IDをセットする。
        ScrollView scrollView = (ScrollView) mView.findViewById( R.id.scroll_shohousen ); //処方箋 scrollview
        TableLayout shohousenArea = (TableLayout) scrollView.getChildAt( 0 ); //処方箋 TableLayout
        View inflateView = getActivity().getLayoutInflater().inflate( R.layout.shohousen, shohousenArea ); //linearLayout に layout.shohousen を入れる。
        inflateView.setId( id );

        TableLayout tableLayout = (TableLayout) ( (TableLayout)inflateView).getChildAt( mShohousenList.size()-1 );
        TableRow tableRow = (TableRow) tableLayout.getChildAt( 0 );
        TextView textView = (TextView)tableRow.findViewById( R.id.viewID );
        textView.setText( String.valueOf( id ) );

        //処方箋View取得、バックカラーをトグルしてセット
        int shohousenCount = mShohousenList.size();
        Log.d( CLASS_NAME, "shohousenCount="+shohousenCount );
        if ( shohousenCount<=0 ) { return; }
        int colorCode = ( (shohousenCount%2==0)?mBackgroundColor_2:mBackgroundColor_1 );
        TableLayout shohousen = (TableLayout) ( (TableLayout)inflateView ).getChildAt( shohousenCount-1 );
        shohousen.setBackgroundColor( colorCode );

        //薬入力域 追加
        getActivity().getLayoutInflater().inflate( R.layout.shohousen_kusuri, tableLayout );
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d( CLASS_NAME, "onCreateOptionsMenu() run. ["+menu+"]" );
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate( R.menu.menu_shohousen, menu );
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.d( CLASS_NAME, "onPrepareOptionsMenu() run. ["+menu+"]" );
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d( CLASS_NAME, "onOptionsItemSelected() run. ["+item+"]" );
        switch ( item.getItemId() ) {
            case android.R.id.home :
                Log.d( CLASS_NAME, "onOptionsItemSelected() [ android.R.id.home ]" );
                FragmentManager fragmentManager = getFragmentManager();
                if ( fragmentManager.getBackStackEntryCount()>0 ) {
                    fragmentManager.popBackStack();
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

    @Override
    public void onStart() {
        Log.d( CLASS_NAME, "DetailFragment.onStart() start." );
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d( CLASS_NAME, "DetailFragment.onResume() start." );
        super.onResume();
    }

    @Override
    public void onDetach() {
        Log.d( CLASS_NAME, "DetailFragment.onDetach() start." );
        super.onDetach();
    }

    @Override
    public void onStop() {
        Log.d( CLASS_NAME, "DetailFragment.onStop() start." );
        super.onStop();
    }

    @Override
    public void onPause() {
        Log.d( CLASS_NAME, "DetailFragment.onPause() start." );
        super.onPause();
    }

}
