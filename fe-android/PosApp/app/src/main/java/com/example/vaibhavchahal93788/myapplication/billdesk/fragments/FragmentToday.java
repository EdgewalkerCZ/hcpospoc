package com.example.vaibhavchahal93788.myapplication.billdesk.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vaibhavchahal93788.myapplication.R;
import com.example.vaibhavchahal93788.myapplication.billdesk.adapter.ProductStockListAdapter;

import java.util.ArrayList;

public class FragmentToday extends Fragment {

    private RecyclerView recyclerView;
    private ProductStockListAdapter adapter;
    private ArrayList<String> names;

    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        View rootView = inflater.inflate(R.layout.fragment_today, container, false);
        populateList();
        initViews(rootView);
        return rootView;
    }

    private void populateList() {

        names = new ArrayList<>();

        names.add("Coffee");
        names.add("Black Coffee");
        names.add("Tea");
        names.add("Green Tea");
        names.add("Black Tea");
        names.add("Red Tea");
        names.add("Nescafe");
        names.add("Nestle");
        names.add("Taza");
        names.add("Red Label");
    }

    private void initViews(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        adapter = new ProductStockListAdapter(names);

        recyclerView.setAdapter(adapter);
    }

}
