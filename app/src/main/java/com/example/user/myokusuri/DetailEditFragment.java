package com.example.user.myokusuri;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
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
    private int index = 0; //処方箋をとりあえず１件で・・・
    private RelativeLayout mShohousenLayout;

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
        //処方箋ページ切り替えボタンのリスナー登録
        setListenerOfShohouPage();
        //処方箋域を初期表示
        initShohouView( mDate );
        //リスナー登録
        setListener();
    }

    private void setListener() {
        Log.d( CLASS_NAME, "setListener() start." );
    }

    private void saveShohou() {
        Log.d( CLASS_NAME, "saveShohou() start." );
        //viewID
        TextView viewIdText = mShohousenLayout.findViewById( R.id.viewID );
        String viewId = viewIdText.getText().toString();
        if ( viewIdText.getText()==null ) {
            Log.d( CLASS_NAME, "viewIDが未設定です。" );
            return;
        }
        Log.d( CLASS_NAME, "viewID："+viewId );
        //薬局
        EditText editYakkyoku = mShohousenLayout.findViewById( R.id.edit_yakkyoku );
        String yakkyoku = editYakkyoku.getText().toString();
        if ( yakkyoku.isEmpty() ) {
            Log.d( CLASS_NAME, "薬局が未設定です。" );
            return;
        }
        Log.d( CLASS_NAME, "薬局："+yakkyoku );
        //処方日数
        EditText editNissu = mShohousenLayout.findViewById( R.id.edit_nissu );
        String nissu = editNissu.getText().toString();
        if ( nissu.isEmpty() ) {
            Log.d( CLASS_NAME, "処方日数が未設定です。" );
            return;
        }
        Log.d( CLASS_NAME, "処方日数："+nissu );
        //薬
        LinearLayout kusuriArea = mShohousenLayout.findViewById( R.id.kusuri_area );
        int kusuriCount = kusuriArea.getChildCount();
        Log.d( CLASS_NAME, "薬の数："+kusuriCount );
        ArrayList<Kusuri> kusuriList = new ArrayList<>();
        for ( int i=0; i<kusuriCount; i++ ) {
            TableRow kusuriInfo = (TableRow) kusuriArea.getChildAt( i );
            EditText editKusuri = kusuriInfo.findViewById( R.id.edit_kusuri );
            String kusuriMei = editKusuri.getText().toString();
            Log.d( CLASS_NAME, "薬名 [ "+i+" ] = "+kusuriMei );
            if ( kusuriMei.isEmpty() ) {
                Log.d( CLASS_NAME, "未設定の薬名があります。" );
                return;
            }
            Kusuri obj = new Kusuri();
            obj.setName( kusuriMei );
            kusuriList.add( obj );
        }
        //処方箋を編集
        mShohousenList.get( index ).setViewId( Integer.parseInt( viewId ) ); //viewID
        mShohousenList.get( index ).setYakkyoku( editYakkyoku.getText().toString() ); //薬局名
        mShohousenList.get( index ).setShohouNissu( Integer.parseInt( editNissu.getText().toString() ) ); //処方日数
        //薬セット
        for ( int i=0; i<kusuriCount; i++ ) {
            mShohousenList.get( index ).addKusuri( kusuriList.get( i ) );
        }
    }
    private void initShohouView( String date ) {
        Log.d( CLASS_NAME, "initShohou() start." );
        //処方箋データ生成
        int id = View.generateViewId(); //動的viewにふるIDをシステムで生成
        ShohousenData shohousenData = new ShohousenData();
        shohousenData.setViewId( id );
        shohousenData.setShohouDate( mDate );
        mShohousenList.add( shohousenData );
        //処方箋Viewを動的生成
        ScrollView scrollView = (ScrollView) mView.findViewById( R.id.scroll_shohousen ); //処方箋 scrollview
        mShohousenLayout = (RelativeLayout)scrollView.findViewById( R.id.shohousen_layout ); //id=shohousen_layout
        TextView textView = (TextView)mShohousenLayout.findViewById( R.id.viewID );
        textView.setText( String.valueOf( id ) );
        //ページ数をセット
        setShohouPage();
        //薬入力域 追加
        addKusuri();
        //［薬を追加］ボタンのリスナーを登録する。
        setListenerOfKusuriBtn();
    }
    private void addShohou( String date ) {
        Log.d( CLASS_NAME, "addShohou() start." );
        //処方箋データ生成
        int id = View.generateViewId(); //動的viewにふるIDをシステムで生成
        ShohousenData shohousenData = new ShohousenData();
        shohousenData.setViewId( id );
        shohousenData.setShohouDate( mDate );
        mShohousenList.add( shohousenData );
        //処方箋Viewを動的生成
        ScrollView scrollView = (ScrollView) mView.findViewById( R.id.scroll_shohousen ); //処方箋 scrollview
        mShohousenLayout = (RelativeLayout)scrollView.findViewById( R.id.shohousen_layout ); //id=shohousen_layout
        TextView textView = (TextView)mShohousenLayout.findViewById( R.id.viewID );
        textView.setText( String.valueOf( id ) );

        //ページ数をセット
        setShohouPage();

        //２つ以上の薬入力域があれば１つだけにする。
        LinearLayout kusuriArea = mShohousenLayout.findViewById( R.id.kusuri_area );
        int kusuriCount = kusuriArea.getChildCount(); //row_kusuri の数
        Log.d( CLASS_NAME, "kusuri count : "+kusuriCount );
        if ( kusuriCount>=2 ) {
            for ( int i=1; i<kusuriCount; i++ ) {
                kusuriArea.removeView( kusuriArea.getChildAt( i ) );
            }
        }
    }
    private void setShohouPage() {
        Log.d( CLASS_NAME, "setShohouPage() start." );
        int totalPageNumber = mShohousenList.size();
        int pageNumber = mShohousenList.size();
        String pageSeparator = getString( R.string.page_separator );
        TextView  textPage = mView .findViewById( R.id.text_shohouPage );
        textPage.setText( pageNumber + pageSeparator + totalPageNumber );
        Log.d( CLASS_NAME, "pageNumber / totalPageNumber = [ "+ pageNumber + pageSeparator + totalPageNumber+" ]" );
    }

//    //スクロールView域に追加していくバージョン（初期版）
//    private void addShohou( String date ) {
//        Log.d( CLASS_NAME, "addShohou() start." );
//        int id = View.generateViewId();
//        ShohousenData shohousenData = new ShohousenData();
//        shohousenData.setViewId( id );
//        shohousenData.setShohouDate( mDate );
//        mShohousenList.add( shohousenData );
//        //処方箋Viewを動的作成し、IDをセットする。
//        ScrollView scrollView = (ScrollView) mView.findViewById( R.id.scroll_shohousen ); //処方箋 scrollview
//        LinearLayout shohousenArea = (LinearLayout) scrollView.getChildAt( 0 ); //処方箋 TableLayout
//        LinearLayout inflateView = (LinearLayout) getActivity().getLayoutInflater().inflate( R.layout.shohousen, shohousenArea ); //linearLayout に layout.shohousen を入れる。
//        inflateView.setId( id );
//        mShohousenLayout = (RelativeLayout)inflateView.getChildAt( mShohousenList.size()-1 ); //id=shohousen
//        TextView textView = (TextView)mShohousenLayout.findViewById( R.id.viewID );
//        textView.setText( String.valueOf( id ) );
//        //処方箋View取得、バックカラーをトグルしてセット
//        int shohousenCount = mShohousenList.size();
//        Log.d( CLASS_NAME, "shohousenCount="+shohousenCount );
//        if ( shohousenCount<=0 ) { return; }
//        int colorCode = ( (shohousenCount%2==0)?mBackgroundColor_2:mBackgroundColor_1 );
//        mShohousenLayout.setBackgroundColor( colorCode );
//        //薬入力域 追加
//        addKusuri();
//        //［薬を追加］ボタンのリスナーを登録する。
//        setListenerOfKusuriBtn();
//    }

    private void setListenerOfShohouPage() {
        Log.d( CLASS_NAME, "setListenerOfShohouPage() start." );
        Button prevShohou = mView.findViewById( R.id.button_prevShohou );
        Button nextShohou = mView.findViewById( R.id.button_nextShohou );
        prevShohou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( CLASS_NAME, "＜前＞ push." );
            }
        });
        nextShohou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( CLASS_NAME, "＜次＞ push." );
            }
        });
    }

    private void setListenerOfKusuriBtn() {
        Log.d( CLASS_NAME, "setListenerOfKusuriBtn() start." );
        Button buttonKusuriTuika = mShohousenLayout.findViewById( R.id.kusuri_tuika_button );
        buttonKusuriTuika.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( CLASS_NAME, "buttonKusuriTuika listener start." );
                addKusuri();
            }
        });
    }

    private void addKusuri() {
        Log.d( CLASS_NAME, "addKusuri() start." );
        //薬入力域 追加
        LinearLayout kusuriArea = (LinearLayout) mShohousenLayout.findViewById( R.id.kusuri_area ); //kusuri_area
        int nextRow = kusuriArea.getChildCount();
        LinearLayout kusuri = (LinearLayout)getActivity().getLayoutInflater().inflate( R.layout.shohousen_kusuri, kusuriArea );
        Log.d( CLASS_NAME, "kusuri Total Count = "+kusuriArea.getChildCount() );
        //[×]ボタンのリスナー登録
//        TableRow kusuriRow = (TableRow) kusuri.findViewById( R.id.row_kusuri ); //IDがみんな同じだから先頭しか取れない！！！
        TableRow kusuriRow = (TableRow) kusuri.getChildAt( nextRow );
        ImageView imageView = kusuriRow.findViewById( R.id.imageView );
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( CLASS_NAME, "× push." );
                clearKusuriMei( view );
            }
        });
    }

    private void clearKusuriMei( View view ) {
        Log.d( CLASS_NAME, "clearKusuriMei() start." );
        ViewParent viewParent = view.getParent();
        EditText editText = ( (ViewGroup)viewParent ).findViewById( R.id.edit_kusuri );
        editText.setText( "" );
        return;
    }

    private void delKusuri() {
        Log.d( CLASS_NAME, "delKusuri() start." );
        View hasFocusView = getActivity().getCurrentFocus();
        if ( hasFocusView instanceof EditText ) {
            Log.d( CLASS_NAME, "instanceof EditText" );
            if ( ( (EditText)hasFocusView).getId()==R.id.edit_kusuri ) {
                Log.d( CLASS_NAME, "フォーカスのある薬" );
                ViewParent removeRow = hasFocusView.getParent(); //row_kusuri
                ViewParent parent = removeRow.getParent(); //kusuri_area
                ( (ViewGroup)parent ).removeView( (View)removeRow );
            }
            else {
                Log.d(CLASS_NAME, "薬名入力域にカーソルを移動していません。");
            }
        }
        else {
            Log.d( CLASS_NAME, "削除したい薬名入力域にカーソルを移動して下さい。" );
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d( CLASS_NAME, "onCreateOptionsMenu() start. ["+menu+"]" );
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate( R.menu.menu_detail_edit, menu );
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
                Log.d( CLASS_NAME, "[ android.R.id.home ]" );
                FragmentManager fragmentManager = getFragmentManager();
                if ( fragmentManager.getBackStackEntryCount()>0 ) {
                    fragmentManager.popBackStack();
                }
                else {
                    Log.d( CLASS_NAME, "BackStack is none." );
                }
                break;

            case R.id.menu01_shohou :
            case R.id.menu02_shohou_add :
            case R.id.menu02_shohou_del :
            case R.id.menu02_shohou_copy :
            case R.id.menu02_shohou_save :
                selectShohouMenu( item );
                break;

            case R.id.menu01_kusuri :
            case R.id.menu02_kusuri_add :
            case R.id.menu02_kusuri_del :
                selectKusuriMenu( item );
                break;

            default:
                Log.d( CLASS_NAME, "[ Undefined Menu ]" );
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectShohouMenu( MenuItem item ) {
        Log.d( CLASS_NAME, "selectShohouMenu() run. ["+item+"]" );
        switch ( item.getItemId() ) {
            case R.id.menu01_shohou :
                Log.d( CLASS_NAME, "＜処方箋＞ メニュー選択" );
                //do nothing so display sub menu by android
                break;
            case R.id.menu02_shohou_add :
                Log.d( CLASS_NAME, "処方箋［追加］ メニュー選択" );
                addShohou( mDate );
                break;
            case R.id.menu02_shohou_del :
                Log.d( CLASS_NAME, "処方箋［削除］ メニュー選択" );
                break;
            case R.id.menu02_shohou_copy :
                Log.d( CLASS_NAME, "処方箋［コピー］ メニュー選択" );
                break;
            case R.id.menu02_shohou_save :
                Log.d( CLASS_NAME, "処方箋［保存ー］ メニュー選択" );
                saveShohou();
                break;
            default:
                break;
        }
    }

    private void selectKusuriMenu( MenuItem item ) {
        Log.d( CLASS_NAME, "selectKusuriMenu() run. ["+item+"]" );
        switch ( item.getItemId() ) {
            case R.id.menu01_kusuri :
                Log.d( CLASS_NAME, "＜薬＞ メニュー選択" );
                //do nothing so display sub menu by android
                break;
            case R.id.menu02_kusuri_add :
                Log.d( CLASS_NAME, "薬［追加］ メニュー選択" );
                addKusuri();
                break;
            case R.id.menu02_kusuri_del :
                Log.d( CLASS_NAME, "薬［削除］ メニュー選択" );
                delKusuri();
                break;
            default:
                break;
        }
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
