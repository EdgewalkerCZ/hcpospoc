package com.example.vaibhavchahal93788.myapplication.billdesk.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.vaibhavchahal93788.myapplication.billdesk.fragments.FragmentMonth;
import com.example.vaibhavchahal93788.myapplication.billdesk.fragments.FragmentToday;
import com.example.vaibhavchahal93788.myapplication.billdesk.fragments.FragmentWeek;
import com.example.vaibhavchahal93788.myapplication.billdesk.fragments.FragmentYear;

public class HistoryPagerAdapter extends FragmentStatePagerAdapter {

    //integer to count number of tabs
    int tabCount;

    //Constructor to the class
    public HistoryPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        //Initializing tab count
        this.tabCount = tabCount;
    }

    //Overriding method getItem
    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                FragmentToday tab1 = new FragmentToday();
                return tab1;
            case 1:
                FragmentWeek tab2 = new FragmentWeek();
                return tab2;
            case 2:
                FragmentMonth tab3 = new FragmentMonth();
                return tab3;
            case 3:
                FragmentYear tab4 = new FragmentYear();
                return tab4;
            default:
                return null;
        }
    }

    //Overriden method getCount to get the number of tabs
    @Override
    public int getCount() {
        return tabCount;
    }
}
