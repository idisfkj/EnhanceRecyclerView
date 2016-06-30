package com.idisfkj.enhancerecyclerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.idisfkj.enhancerecyclerview.adapter.RecyclerViewAdapter;
import com.idisfkj.enhancerecyclerview.view.EnhanceRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.recyclerView)
    EnhanceRecyclerView recyclerView;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL));


        List<String> list = new ArrayList<>();
        for (int i = 0; i < 13; i++)
            list.add("no thing " + i);

        recyclerView.setAdapter(new RecyclerViewAdapter(this, list));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("add header");
        menu.add("add footer");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //不能在onCreate()中获取View,防止添加时的无缘故的错乱.
        view = LayoutInflater.from(this).inflate(R.layout.head_layout, null);
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(params);
        }
        if (item.getTitle().toString() == "add header") {
            recyclerView.addHeaderView(view);
        } else if (item.getTitle().toString() == "add footer") {
            recyclerView.addFooterView(view);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }
}
