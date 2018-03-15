package com.example.user.myokusuri;


import android.content.Context;
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
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private final int mBackgroundColor_1 = Color.parseColor("#e0ffff");
    private final int mBackgroundColor_2 = Color.parseColor("#faf0e6");

    private ArrayList<ShohousenData> mShohousenList = new ArrayList<>();  //処方箋リスト
    private int index = 0; //処方箋をとりあえず１件で・・・
    private RelativeLayout mShohousenLayout;
    private int mNo = 0;

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
        view.setFocusableInTouchMode(true); //このViewがタッチモードでフォーカスを受け取る。
        view.requestFocus(); //このViewにフォーカスを移す。
        //Fragmentでのキー押下時のリスナー登録
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                Log.d(CLASS_NAME, "onKey() [i/keyEvent=" + i + "/" + keyEvent + "]");
                if (i == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    Log.d(CLASS_NAME, "Back key");
                    for (int j = 0; j < mShohousenList.size(); j++) {
                        mShohousenList.get(j).removeAllKusuri();
                    }
                    mShohousenList.clear();
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
        Log.d(CLASS_NAME, "onViewCreated() start." + mParam1);
        setHasOptionsMenu(true); //オプションメニューを使用する事を宣言
        super.onViewCreated(view, savedInstanceState);
        //actionBar設定
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.title_edit); //タイトル
        actionBar.setHomeButtonEnabled(true); //HOMEへ戻る「←」セット
        //日付設定
        TextView textView = getActivity().findViewById(R.id.select_date);
        textView.setText(mDate);
        //処方箋ページ切り替えボタンのリスナー登録
        setListenerOfShohouPage();
        //処方箋域を初期表示
        initShohou(mDate);
        //リスナー登録
        setListener();
    }

    private void setListener() {
        Log.d(CLASS_NAME, "setListener() start.");
    }

    private void storeShohouWithCheck() {
        Log.d(CLASS_NAME, "storeShohouWithCheck() start.");
        if (!checkShohousenHeader()) {
            return;
        }
        //薬
        LinearLayout kusuriArea = mShohousenLayout.findViewById(R.id.kusuri_area);
        int kusuriCount = kusuriArea.getChildCount();
        Log.d(CLASS_NAME, "薬の数：" + kusuriCount);
        ArrayList<Kusuri> kusuriList = new ArrayList<>();
        for (int i = 0; i < kusuriCount; i++) {
            TableRow kusuriInfo = (TableRow) kusuriArea.getChildAt(i);
            EditText editKusuri = kusuriInfo.findViewById(R.id.edit_kusuri);
            String kusuriMei = editKusuri.getText().toString();
            Log.d(CLASS_NAME, "薬名 [ " + i + " ] = " + kusuriMei);
            if (kusuriMei.isEmpty()) {
                Log.d(CLASS_NAME, "未設定の薬名があります。");
                return;
            }
            Kusuri obj = new Kusuri();
            obj.setName(kusuriMei);
            kusuriList.add(obj);
        }
        //処方箋を編集
        //No
        TextView noText = mShohousenLayout.findViewById(R.id.text_number);
        String no = noText.getText().toString();
        mShohousenList.get(index).setNo(Integer.parseInt(no)); //viewID
        //薬局
        EditText editYakkyoku = mShohousenLayout.findViewById(R.id.edit_yakkyoku);
        String yakkyoku = editYakkyoku.getText().toString();
        mShohousenList.get(index).setYakkyoku(editYakkyoku.getText().toString()); //薬局名
        //処方日数
        EditText editNissu = mShohousenLayout.findViewById(R.id.edit_nissu);
        String nissu = editNissu.getText().toString();
        mShohousenList.get(index).setShohouNissu(Integer.parseInt(editNissu.getText().toString())); //処方日数
        //薬セット
        for (int i = 0; i < kusuriCount; i++) {
            mShohousenList.get(index).addKusuri(kusuriList.get(i));
        }
    }

    private void storeShohouWithNoCheck() {
        Log.d(CLASS_NAME, "storeShohouWithNoCheck() start.");
        //処方箋を編集
        int savePos = getCurrentShohouPage() - 1;
        //No
        TextView noText = mShohousenLayout.findViewById(R.id.text_number);
        String no = noText.getText().toString();
        mShohousenList.get(savePos).setNo(Integer.parseInt(no)); //viewID
        //薬局
        EditText editYakkyoku = mShohousenLayout.findViewById(R.id.edit_yakkyoku);
        String yakkyoku = editYakkyoku.getText().toString();
        mShohousenList.get(savePos).setYakkyoku(editYakkyoku.getText().toString()); //薬局名
        //処方日数
        EditText editNissu = mShohousenLayout.findViewById(R.id.edit_nissu);
        String nissu = editNissu.getText().toString();
        mShohousenList.get(savePos).setShohouNissu(Integer.parseInt(editNissu.getText().toString())); //処方日数
        //薬セット
        LinearLayout kusuriArea = mShohousenLayout.findViewById(R.id.kusuri_area);
        int kusuriCount = kusuriArea.getChildCount();
        for (int i = 0; i < kusuriCount; i++) {
            TableRow rowKusuri = (TableRow) kusuriArea.getChildAt(i);
            EditText editKusuri = rowKusuri.findViewById(R.id.edit_kusuri);
            Kusuri kusuri = new Kusuri();
            kusuri.setName(editKusuri.getText().toString());
            mShohousenList.get(savePos).addKusuri(kusuri);
        }
        Log.d(CLASS_NAME, "薬の数：" + kusuriCount);
    }

    private boolean checkShohousenHeader() {
        Log.d(CLASS_NAME, "checkShohousenHeader() start.");
        //viewID
        TextView viewIdText = mShohousenLayout.findViewById(R.id.text_number);
        String viewId = viewIdText.getText().toString();
//        if ( viewIdText.getText()==null ) {
//            Log.d( CLASS_NAME, "viewIDが未設定です。" );
//            return false;
//        }
        Log.d(CLASS_NAME, "viewID：" + viewId);
        //薬局
        EditText editYakkyoku = mShohousenLayout.findViewById(R.id.edit_yakkyoku);
        String yakkyoku = editYakkyoku.getText().toString();
        if (yakkyoku.isEmpty()) {
            Log.d(CLASS_NAME, "薬局が未設定です。");
            return false;
        }
        Log.d(CLASS_NAME, "薬局：" + yakkyoku);
        //処方日数
        EditText editNissu = mShohousenLayout.findViewById(R.id.edit_nissu);
        String nissu = editNissu.getText().toString();
        if (nissu.isEmpty()) {
            Log.d(CLASS_NAME, "処方日数が未設定です。");
            return false;
        }
        Log.d(CLASS_NAME, "処方日数：" + nissu);

        return true;
    }

    private void initShohou( String date ) {
        Log.d( CLASS_NAME, "initShohou() start." );

        if ( isShohouExist() ) { //登録済みの処方ファイルがある場合
            String tempDate = date;
            tempDate = tempDate.replace( "/", "" );
            Log.d( CLASS_NAME, "tempDate : "+tempDate );
            String fileName = tempDate + ".csv";
            try {

                FileInputStream fileInputStream = getContext().openFileInput( fileName );
                BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( fileInputStream ) );

                //Reads a line of text. A line is considered to be terminated by any one of a line feed ('\n'), a carriage return ('\r'), or a carriage return followed immediately by a linefeed.
                String readData = "";
                String splitChara = ","; //for CSV file
                mShohousenList.clear(); //処方データをクリアしておく。
                ShohousenData shohou = new ShohousenData();
                while ( ( readData = bufferedReader.readLine() ) != null ) {
                    String[] lineData = readData.split( splitChara );
                    Log.d( CLASS_NAME, "lineData length : "+lineData.length );
                    shohou.setNo( Integer.parseInt( lineData[0] ) ); //No
                    shohou.setYakkyoku( lineData[1] ); //薬局名
                    shohou.setShohouNissu( Integer.parseInt( lineData[2] ) ); //処方日数
                    //薬名（複数あり）
                    for ( int i=3; i<lineData.length; i++ ) {
                        Kusuri kusuri = new Kusuri();
                        kusuri.setViewId( 0 );
                        kusuri.setName( lineData[ i ] );
                        shohou.addKusuri( kusuri );
                    }
                    mShohousenList.add( shohou );
                }

                fileInputStream.close();

                initShohouView( date, mShohousenList );

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else { //一件も処方がない場合
            initShohouView( date );
        }
    }

    private void initShohouView( String date, ArrayList<ShohousenData> array ) {
        Log.d( CLASS_NAME, "initShohouView( date, readData ) start." );

        //Viewにデータセット（初期状態なので１ページ目で決め打ち）
        ScrollView scrollView = (ScrollView) mView.findViewById( R.id.scroll_shohousen ); //処方箋 scrollview
        mShohousenLayout = (RelativeLayout)scrollView.findViewById( R.id.shohousen_layout ); //id=shohousen_layout
        TextView textView = (TextView)mShohousenLayout.findViewById( R.id.text_number);
        textView.setText( String.valueOf( array.get( 0 ).getNo() ) ); //No
        EditText editYakkyoku = mShohousenLayout.findViewById( R.id.edit_yakkyoku );
        editYakkyoku.setText( array.get( 0 ).getYakkyoku() ); //薬局名
        EditText editNissu = mShohousenLayout.findViewById( R.id.edit_nissu );
        editNissu.setText( String.valueOf( array.get( 0 ).getShohouNissu() ) ); //処方日数
        //薬
        LinearLayout rowKusuri = (LinearLayout) mShohousenLayout.findViewById( R.id.kusuri_area );
        int kusuriCount = mShohousenList.get( 0 ).countOfKusuri();
        Log.d( CLASS_NAME, "kusuri count : "+kusuriCount );
        for ( int i=0; i<kusuriCount; i++ ) {
            addKusuriArea();
            EditText editKusuri = rowKusuri.getChildAt( i ).findViewById( R.id.edit_kusuri );
            editKusuri.setText( mShohousenList.get( 0 ).getKusuri( i ).getName() );
        }

        //ページをセット
        setShohouPageNo( mShohousenList.size() );
//        //薬入力域 追加
//        addKusuriArea();
//        //［薬を追加］ボタンのリスナーを登録する。
//        setListenerOfKusuriBtn();
    }

    private void initShohouView( String date ) {
        Log.d( CLASS_NAME, "initShohouView() start." );
        //処方箋データ生成
        ShohousenData shohousenData = new ShohousenData();
        shohousenData.setNo( ++ mNo );
        shohousenData.setShohouDate( mDate );
        mShohousenList.add( shohousenData );
        //ViewのIDをセット
        ScrollView scrollView = (ScrollView) mView.findViewById( R.id.scroll_shohousen ); //処方箋 scrollview
        mShohousenLayout = (RelativeLayout)scrollView.findViewById( R.id.shohousen_layout ); //id=shohousen_layout
        TextView textView = (TextView)mShohousenLayout.findViewById( R.id.text_number);
        textView.setText( String.valueOf( mNo ) );
        //ページをセット
        setShohouPageNo( mShohousenList.size() );
        //薬入力域 追加
        addKusuriArea();
        //［薬を追加］ボタンのリスナーを登録する。
        setListenerOfKusuriBtn();
    }

    private boolean isShohouExist() {
        Log.d( CLASS_NAME, "isShohouExist() start." );
        String date = mDate;
        date = date.replace( "/", "" );
        Log.d( CLASS_NAME, "date : "+date );
        String fileName = getContext().getFilesDir() + "/" + date + ".csv";
        Log.d( CLASS_NAME, ""+fileName );
        File file = new File( fileName );
        if ( file == null ) { return false; }
        if ( !file.exists() ) { return false; }
        return true;
    }

    private void addShohou( String date ) {
        Log.d( CLASS_NAME, "addShohou() start." );
        //処方箋データ生成
        ShohousenData shohousenData = new ShohousenData();
        shohousenData.setNo( ++ mNo );
        shohousenData.setShohouDate( mDate );
        mShohousenList.add( shohousenData );
        //ページをセット
        setShohouPageNo( mShohousenList.size() );
        //処方箋Viewの内容をリセット
        ScrollView scrollView = (ScrollView) mView.findViewById( R.id.scroll_shohousen ); //処方箋 scrollview
        mShohousenLayout = (RelativeLayout)scrollView.findViewById( R.id.shohousen_layout ); //id=shohousen_layout
        TextView textView = (TextView)mShohousenLayout.findViewById( R.id.text_number);
        textView.setText( String.valueOf( mNo ) );
        EditText editYakkoku = (EditText)mShohousenLayout.findViewById( R.id.edit_yakkyoku );
        editYakkoku.setText( "" );
        EditText editNisuu = (EditText)mShohousenLayout.findViewById( R.id.edit_nissu );
        editNisuu.setText( "" );
        //２つ以上の薬入力域があれば１つだけにする。
        LinearLayout kusuriArea = mShohousenLayout.findViewById( R.id.kusuri_area );
        int kusuriCount = kusuriArea.getChildCount(); //row_kusuri の数
        Log.d( CLASS_NAME, "kusuri count : "+kusuriCount );
        //薬名の先頭はクリア
        TableRow kusuriRow = (TableRow) kusuriArea.getChildAt( 0 );
        EditText editKusuri = (EditText) kusuriRow.findViewById( R.id.edit_kusuri );
        editKusuri.setText( "" );
        //２つめ以降は入力域を削除
        if ( kusuriCount>=2 ) {
            for ( int i=1; i<kusuriCount; i++ ) {
                kusuriArea.removeView( kusuriArea.getChildAt( i ) );
            }
        }
    }

    private void setShohouPageNo( int pageNo ) {
        Log.d( CLASS_NAME, "setShohouPageNo() start." );
        int totalPageNumber = mShohousenList.size();
        String pageSeparator = getString( R.string.page_separator );
        TextView  textPage = mView .findViewById( R.id.text_shohouPage );
        textPage.setText( pageNo + pageSeparator + totalPageNumber );
        Log.d( CLASS_NAME, "pageNumber / totalPageNumber = [ "+ pageNo + pageSeparator + totalPageNumber+" ]" );
    }

    private int getCurrentShohouPage() {
        Log.d( CLASS_NAME, "getCurrentShohouPage() start." );
        TextView  textPage = mView .findViewById( R.id.text_shohouPage );
        String string = textPage.getText().toString();
        Log.d( CLASS_NAME, "shohouPage getText.toString："+string );
        int index = string.indexOf( getString( R.string.page_separator) );
        if ( index<0 ) {
            return -1;
        }
        int currentPage = Integer.parseInt( string.substring( 0, index ) );
        Log.d( CLASS_NAME, "current Page of Shohousen："+currentPage );
        return currentPage;

    }

    private void setListenerOfShohouPage() {
        Log.d( CLASS_NAME, "setListenerOfShohouPage() start." );
        Button prevShohou = mView.findViewById( R.id.button_prevShohou );
        Button nextShohou = mView.findViewById( R.id.button_nextShohou );
        prevShohou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( CLASS_NAME, "＜前＞ push." );
                int page = getCurrentShohouPage();
                if ( page <= 1) {
                    Log.d( CLASS_NAME, "先頭ページです。" );
                    return;
                }
                storeShohouWithNoCheck(); //現在のページをチェック無しで内部バッファに保存
                showPrevShohou();
            }
        });
        nextShohou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( CLASS_NAME, "＜次＞ push." );
                int page = getCurrentShohouPage();
                if ( page >= mShohousenList.size() ) {
                    Log.d( CLASS_NAME, "最終ページです。" );
                    return;
                }
                storeShohouWithNoCheck(); //現在のページをチェック無しで内部バッファに保存
                showNextShohou();
            }
        });
    }

    private void showNextShohou() {
        Log.d( CLASS_NAME, "displayNextShohou() start." );
        int currentPage = getCurrentShohouPage();
        if ( currentPage+1 > mShohousenList.size()  ) { //最終ページです。
            Log.d( CLASS_NAME, "現在、最終ページです。" );
            return;
        }
        int nextPage = currentPage+1;
        ShohousenData shohousenData = mShohousenList.get( nextPage-1 );
        setShohouPageNo( nextPage ); //ページ番号表示
        //No
        TextView textNo = mShohousenLayout.findViewById( R.id.text_number );
        textNo.setText( String.valueOf( shohousenData.getNo() ));
        //薬局名
        EditText textYakkyoku = mShohousenLayout.findViewById( R.id.edit_yakkyoku );
        textYakkyoku.setText( shohousenData.getYakkyoku() );
        //処方日数
        EditText textNissu = mShohousenLayout.findViewById( R.id.edit_nissu );
        textNissu.setText( String.valueOf( shohousenData.getShohouNissu() ) );
        //薬
        int kusuriAreaCount = mShohousenLayout.getChildCount(); //薬名エリアの数
        int kusuriDataCount = shohousenData.countOfKusuri(); //薬名データ数
        if ( kusuriAreaCount < kusuriDataCount) { //薬名エリアがデータ数より少なかったら作成
            for ( int i=kusuriAreaCount; i<kusuriDataCount; i++ ) {
                addKusuriArea();
            }
        }
        else if ( kusuriAreaCount > kusuriDataCount ) { //薬名エリアがデータ数より多かったら削除
            for ( int i=kusuriDataCount; i<kusuriAreaCount; i++ ) {
                View removeView = mShohousenLayout.getChildAt( i );
                mShohousenLayout.removeView( removeView );
            }
        }
        for ( int i=0; i<kusuriDataCount; i++ ) {
            TableRow rowKusuri = (TableRow)mShohousenLayout.getChildAt( i );
            EditText editKusuri = rowKusuri.findViewById( R.id.edit_kusuri );
            editKusuri.setText( shohousenData.getKusuri( i ).getName() );
        }
    }

    private void showPrevShohou() {
        Log.d( CLASS_NAME, "displayPrevShohou() start." );
        int currentPage = getCurrentShohouPage();
        if ( currentPage<=1 ) { //先頭です。
            Log.d( CLASS_NAME, "現在、先頭ページです。" );
            return;
        }
        int prevPage = currentPage-1;
        ShohousenData shohousenData = mShohousenList.get( prevPage-1 );
        setShohouPageNo( prevPage ); //ページ番号表示
        //No
        TextView textNo = mShohousenLayout.findViewById( R.id.text_number );
        textNo.setText( String.valueOf( shohousenData.getNo() ));
        //薬局名
        EditText textYakkyoku = mShohousenLayout.findViewById( R.id.edit_yakkyoku );
        textYakkyoku.setText( shohousenData.getYakkyoku() );
        //処方日数
        EditText textNissu = mShohousenLayout.findViewById( R.id.edit_nissu );
        textNissu.setText( String.valueOf( shohousenData.getShohouNissu() ) );
        //薬
        LinearLayout kusuriData = mShohousenLayout.findViewById( R.id.kusuri_area ); //処方箋の薬欄
        int kusuriAreaCount = kusuriData.getChildCount(); //薬名エリアの数
        int kusuriDataCount = shohousenData.countOfKusuri(); //薬名データ数
        if ( kusuriAreaCount < kusuriDataCount) { //薬名エリアがデータ数より少なかったら作成
            for ( int i=kusuriAreaCount; i<kusuriDataCount; i++ ) {
                addKusuriArea();
            }
        }
        else if ( kusuriAreaCount > kusuriDataCount ) { //薬名エリアがデータ数より多かったら削除
            for ( int i=kusuriDataCount; i<kusuriAreaCount; i++ ) {
                kusuriData.removeView(  kusuriData.getChildAt( i ) );
            }
        }
        for ( int i=0; i<kusuriDataCount; i++ ) {
            TableRow kusuriRow = (TableRow) kusuriData.getChildAt( i );
            EditText editKusuri = kusuriRow.findViewById( R.id.edit_kusuri );
            editKusuri.setText( shohousenData.getKusuri( i ).getName() );
        }
    }

    private void setListenerOfKusuriBtn() {
        Log.d( CLASS_NAME, "setListenerOfKusuriBtn() start." );
        Button buttonKusuriTuika = mShohousenLayout.findViewById( R.id.kusuri_tuika_button );
        buttonKusuriTuika.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( CLASS_NAME, "buttonKusuriTuika listener start." );
                addKusuriArea();
            }
        });
    }

    private void addKusuriArea() {
        Log.d( CLASS_NAME, "addKusuriArea() start." );
        //薬入力域 追加
        LinearLayout kusuriArea = mShohousenLayout.findViewById( R.id.kusuri_area ); //kusuri_area
        int nextRow = kusuriArea.getChildCount();
        LinearLayout kusuri = (LinearLayout)getActivity().getLayoutInflater().inflate( R.layout.shohousen_kusuri, kusuriArea );
        Log.d( CLASS_NAME, "kusuri Total Count = "+kusuriArea.getChildCount() );
        //[×]ボタンのリスナー登録
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
    }

    private void delKusuri() {
        Log.d( CLASS_NAME, "delKusuri() start." );
        View hasFocusView = getActivity().getCurrentFocus();
        if ( hasFocusView instanceof EditText ) {
            Log.d( CLASS_NAME, "instanceof EditText" );
            if ( hasFocusView.getId()==R.id.edit_kusuri ) {
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
        Log.d( CLASS_NAME, "onPrepareOptionsMenu() start. ["+menu+"]" );
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d( CLASS_NAME, "onOptionsItemSelected() start. ["+item+"]" );
        switch ( item.getItemId() ) {
            case android.R.id.home :
                Log.d( CLASS_NAME, "[ android.R.id.home ]" );
                for ( int i=0; i<mShohousenList.size(); i++ ) {
                    mShohousenList.get( i ).removeAllKusuri();
                }
                mShohousenList.clear();
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
        Log.d( CLASS_NAME, "selectShohouMenu() start. ["+item+"]" );
        switch ( item.getItemId() ) {
            case R.id.menu01_shohou :
                Log.d( CLASS_NAME, "＜処方箋＞ メニュー選択" );
                //do nothing so display sub menu by android
                break;
            case R.id.menu02_shohou_add :
                Log.d( CLASS_NAME, "処方箋［追加］ メニュー選択" );
                storeShohouWithCheck();
                addShohou( mDate );
                Log.d( CLASS_NAME, "mShohousenList count："+mShohousenList.size() );
                break;
            case R.id.menu02_shohou_del :
                Log.d( CLASS_NAME, "処方箋［削除］ メニュー選択" );
                break;
            case R.id.menu02_shohou_copy :
                Log.d( CLASS_NAME, "処方箋［コピー］ メニュー選択" );
                break;
            case R.id.menu02_shohou_save :
                Log.d( CLASS_NAME, "処方箋［保存ー］ メニュー選択" );
                storeShohouWithCheck();
                Log.d( CLASS_NAME, "mShohousenList count："+mShohousenList.size() );
                saveShohouToFile();
                break;
            default:
                break;
        }
    }

    private void saveShohouToFile() {
        Log.d( CLASS_NAME, "saveShohouToFile() start." );
        int savedCount = mShohousenList.size(); //処方箋数
        String savedDate = mShohousenList.get( 0 ).getShohouDate(); //日付（ファイル名にする）
        //データ有無のチェック
        if ( savedCount <= 0 ) {
            Log.d( CLASS_NAME, "登録するデータが無い。[ savedCoun："+savedCount+" ]" );
            return;
        }
        if ( savedDate.isEmpty() ) {
            Log.d( CLASS_NAME, "日付が不明です。" );
            return;
        }
        //データ内容チェック
        int i=0;
        for ( ShohousenData shohousenData : mShohousenList ) {
            if ( shohousenData == null ) {
                Log.d( CLASS_NAME, "shohousenData == null" );
                break;
            }
            if ( shohousenData.getYakkyoku() != null &&
                    shohousenData.getYakkyoku().isEmpty() ) { //チェック：薬局名
                Log.d( CLASS_NAME, "「薬局名」が空です。[ i="+i+" ]" );
                return;
            }
            if ( shohousenData.getShohouNissu() <= 0 ) { //チェック：処方日数
                Log.d( CLASS_NAME, "「処方日数」が空です。[ i="+i+" ]" );
                return;
            }
            if ( shohousenData.countOfKusuri() <= 0 ) { //チェック：薬数
                Log.d( CLASS_NAME, "「薬名」が空です。[ i="+i+" ]" );
                return;
            }
            i++;
        } //for( shohouData )

        //ファイルに保存
        String BR = System.getProperty( "line.separator" ); //改行コード取得
        Log.d( CLASS_NAME, "getFilesDir() : " + getContext().getFilesDir() );
        try {
            savedDate = savedDate.replace( "/", "" ); //日付から "/" を抜く。
            String createdFile = savedDate + ".csv";
            //Context.openFileOutput, Context.openFileInput はandroidで既定のFolderへアクセスする。だからパスはいらない。(パスは Context.getFileDir() で確認出来る。)
            FileOutputStream fileOutputStream = getContext().openFileOutput( createdFile, Context.MODE_PRIVATE );
            i=0;
            for ( ShohousenData shohousenData : mShohousenList ) {
                StringBuffer stringBuffer = new StringBuffer();
                String strSep = ",";
                stringBuffer.append( shohousenData.getNo() ); //No
                stringBuffer.append( strSep );
                stringBuffer.append( shohousenData.getYakkyoku() ); //薬局名
                stringBuffer.append( strSep );
                stringBuffer.append( shohousenData.getShohouNissu() ); //処方日数
                stringBuffer.append( strSep );
                for ( int j=0; j<shohousenData.countOfKusuri(); j++ ) {
                    stringBuffer.append( shohousenData.getKusuri( j ).getName() ); //薬名
                    stringBuffer.append( strSep );
                } //for( j )
                Log.d( CLASS_NAME, "saved data : "+stringBuffer.toString() );
                stringBuffer.append( BR ); //改行
                fileOutputStream.write( stringBuffer.toString().getBytes() );
            } //for ( shohousenData )

            fileOutputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } //長すぎる・・・・・

    private void selectKusuriMenu( MenuItem item ) {
        Log.d( CLASS_NAME, "selectKusuriMenu() start. ["+item+"]" );
        switch ( item.getItemId() ) {
            case R.id.menu01_kusuri :
                Log.d( CLASS_NAME, "＜薬＞ メニュー選択" );
                //do nothing so display sub menu by android
                break;
            case R.id.menu02_kusuri_add :
                Log.d( CLASS_NAME, "薬［追加］ メニュー選択" );
                addKusuriArea();
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
