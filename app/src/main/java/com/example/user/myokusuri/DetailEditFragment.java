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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


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
        return view;
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
        textView.setText( mParam1 );

        //指定された日付の処方情報を読み込んで動的に追加
        //指定された日に処方情報があれば初期表示し、無ければ１つだけ表示する。
//        ViewGroup viewGroup = getActivity().findViewById( R.id.shohousen ); //処方箋
        LinearLayout linearLayout = getActivity().findViewById( R.id.shohousen ); //処方箋
        getActivity().getLayoutInflater().inflate( R.layout.shohousen, linearLayout );

        //リスナー登録
        setListener();
    }

    private void setListener() {
        Button shohousenButton = getActivity().findViewById( R.id.shohousen_copy_button );
        Button shohousenDelButton = getActivity().findViewById( R.id.shohousen_del_button );
        Button kusuriTuikaButton = getActivity().findViewById( R.id.kusuri_tuika_button );
        ImageView kusuriDel = getActivity().findViewById( R.id.imageView );
        shohousenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( CLASS_NAME, "処方箋 追加ボタン 押下" );
                LinearLayout linearLayout = getActivity().findViewById( R.id.shohousen ); //処方箋
                View shohousen = getActivity().getLayoutInflater().inflate( R.layout.shohousen, linearLayout );
                int shohousenCount = ((LinearLayout)shohousen).getChildCount();
                Log.d( CLASS_NAME, "shohousenCount="+shohousenCount );
                if ( shohousenCount<=0 ) { return; }
                int colorCode = ( (shohousenCount%2==0)?mBackgroundColor_2:mBackgroundColor_1 );
                TableLayout tableLayout = (TableLayout)((LinearLayout)shohousen).getChildAt( shohousenCount-1 );
                tableLayout.setBackgroundColor( colorCode ); //shohousen.xml
                TableRow tableRow = tableLayout.findViewById( R.id.kusuri );
                ImageView imageView = tableRow.findViewById( R.id.imageView );
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d( CLASS_NAME, "imageView is clicked." );
                        TableRow vp = (TableRow)view.getParent();
                        EditText editText = vp.findViewById( R.id.edit_kusuri );
                        editText.setText( "" );
                    }
                });

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

//                    ViewGroup rootView = (ViewGroup)view.getRootView();
//                    Log.d( CLASS_NAME, "root view = "+rootView );
//                    ViewGroup viewGroup = (ViewGroup)rootView.getChildAt( 0 );
//                    Log.d( CLASS_NAME, "child view 1 of root view = "+viewGroup );
                }

//                ViewParent viewParent1 = viewParent.getParent();
//                Log.d( CLASS_NAME, "parent view 1 -> "+viewParent1 );
//
//                ViewParent viewParent2 = viewParent1.getParent();
//                Log.d( CLASS_NAME, "parent view 2 -> "+viewParent2 );
//
//                ViewParent viewParent3 = viewParent2.getParent();
//                Log.d( CLASS_NAME, "parent view 3 -> "+viewParent3 );

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
        kusuriDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( CLASS_NAME, "kusuriDel listenner run."+view );

                ViewParent vp = view.getParent();
                TableRow tableRow = (TableRow)vp;
                Log.d( CLASS_NAME, ""+tableRow.getId() ); //id=kusuri
                if ( vp instanceof ViewGroup ) {
                    Log.d( CLASS_NAME, "ViewParent instanceof ViewGroup" );
                }

                vp = vp.getParent();
                Log.d( CLASS_NAME, ""+vp ); //id=shohousen_header

            }
        });
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
}
