package com.example.user.myokusuri;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements MenuFragment.OnFragmentInteractionListener {

    private final String CLASS_NAME = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d( CLASS_NAME, "onCreate() run. [arg="+savedInstanceState+"]" );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if ( savedInstanceState==null ) {
            //フラグメント
            final FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //MenuFragment menuFragment = new MenuFragment();
            //fragmentTransaction.add( R.id.container, calenderFragment );
            fragmentTransaction.add(R.id.container, MenuFragment.newInstance("", ""));
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d( CLASS_NAME, "interface onFragmentInteraction run." );
    }

    @Override
    protected void onStart() {
        Log.d( CLASS_NAME, "Activity.onStart() start." );
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d( CLASS_NAME, "Activity.onResume() start." );
        super.onResume();
    }
}
