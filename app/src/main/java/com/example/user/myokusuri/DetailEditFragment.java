package com.example.user.myokusuri;


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
import android.widget.Button;
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
        TextView textView = getActivity().findViewById( R.id.input_date );
        textView.setText( mParam1 );
        //指定された日付の処方情報を読み込んで動的に追加
        //指定された日に処方情報があれば初期表示し、無ければ１つだけ表示する。
        ViewGroup viewGroup = getActivity().findViewById( R.id.shohousen ); //処方箋
        getActivity().getLayoutInflater().inflate( R.layout.shohousen, viewGroup );
        ViewGroup viewGroup1 = getActivity().findViewById( R.id.shohousen_header ); //薬(処方箋に含まれる！)
        getActivity().getLayoutInflater().inflate( R.layout.shohousen_kusuri, viewGroup1 );

        //リスナー登録
        setListener();
    }

    private void setListener() {
        Button shohousenButton = getActivity().findViewById( R.id.shohousen_copy_button );
        Button shohousenDelButton = getActivity().findViewById( R.id.shohousen_del_button );
        Button kusuriTuikaButton = getActivity().findViewById( R.id.kusuri_tuika_button );
        Button kusuriSakujoButton = getActivity().findViewById( R.id.kusuri_sakujo_button );
        shohousenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( CLASS_NAME, "処方箋 追加ボタン 押下" );
                ViewGroup viewGroup = getActivity().findViewById( R.id.shohousen ); //処方箋
                getActivity().getLayoutInflater().inflate( R.layout.shohousen, viewGroup );
                ViewGroup viewGroup1 = getActivity().findViewById( R.id.shohousen_header ); //薬(処方箋に含まれる！)
                getActivity().getLayoutInflater().inflate( R.layout.shohousen_kusuri, viewGroup1 );
            }
        });
        shohousenDelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( CLASS_NAME, "処方箋 削除ボタン 押下" );
            }
        });
        kusuriTuikaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( CLASS_NAME, "薬 追加ボタン 押下" );

                //選択中の処方箋は取得

                //薬の入力域を追加
            }
        });
        kusuriSakujoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d( CLASS_NAME, "薬 削除ボタン 押下" );
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
