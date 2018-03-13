package com.example.user.myokusuri;

// * Created by user on 2018/03/12.

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//
//処方箋毎の内容データ
//
public class ShohousenData {
//				・動的に割り当てたＩＤ
//				・日付
//				・薬局名
//				・処方日数
//				・薬名（複数可）

    final String TAG = getClass().getSimpleName();

    private int mViewId;
    private String mShohouDate;
    private String mYakkyoku;
    private int mShohouNissu;
    private ArrayList<Kusuri> mKusuri= new ArrayList<>();

    public ShohousenData() {
        Log.d( TAG, "ShohousenData()" );
    }

    public void setViewId( int id ) {
        mViewId = id;
    }
    public int getViewId() {
        return mViewId;
    }

    public String getShohouDate() {
        return mShohouDate;
    }
    public void setShohouDate(String mShohouDate) {
        this.mShohouDate = mShohouDate;
    }

    public String getYakkyoku() {
        return mYakkyoku;
    }
    public void setYakkyoku(String mYakkyoku) {
        this.mYakkyoku = mYakkyoku;
    }

    public int getShohouNissu() {
        return this.mShohouNissu;
    }
    public void setShohouNissu( int shohouNissu ) {
        this.mShohouNissu = shohouNissu;
    }

    //薬の数
    public int countOfKusuri() {
        return this.mKusuri.size();
    }
    //薬を追加
    public void addKusuri( Kusuri kusuri ) {
        mKusuri.add( kusuri );
    }
    //薬の削除
    public void removeKusuri( int index ) {
        mKusuri.remove( index );
    }

}
