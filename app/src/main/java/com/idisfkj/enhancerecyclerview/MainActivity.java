package com.idisfkj.enhancerecyclerview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.idisfkj.enhancerecyclerview.adapter.RecyclerViewAdapter;
import com.idisfkj.enhancerecyclerview.view.EnhanceRecyclerView;
import com.idisfkj.enhancerecyclerview.view.MyItemDecoration;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.recyclerView)
    EnhanceRecyclerView mRecyclerView;
    private View view;
    private RecyclerView.Adapter adapter;
    private int lastItem;
    private int totalCount;
    private ArrayList<String> list;
    private boolean isLoad = false;
    private int[] into;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL));


        list = new ArrayList<>();
        for (int i = 0; i < 13; i++)
            list.add("no thing " + i);

        view = LayoutInflater.from(this).inflate(R.layout.footer_layout, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        mRecyclerView.addFooterView(view);
//        mRecyclerView.addHeaderView(view);
        adapter = new RecyclerViewAdapter(this, list);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(new MyItemDecoration(this));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (lastItem == adapter.getItemCount() && newState == RecyclerView.SCROLL_STATE_IDLE && !isLoad) {
                    Log.d("TAG", "--------------->OK<----------------");
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    view.setLayoutParams(params);
                    view.setVisibility(View.VISIBLE);
                    mRecyclerView.smoothScrollToPosition(totalCount);
                    isLoad = true;
                    mHandler.sendEmptyMessageDelayed(1, 2000);
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                totalCount = recyclerView.getLayoutManager().getItemCount();
                if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    lastItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                } else {
                    into = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPositions(into);
                    lastItem = into[0];
                }
            }
        });
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.width = 0;
                params.height = 0;
                view.setLayoutParams(params);
                view.setVisibility(View.GONE);
                for (int i = 0; i < 7; i++)
                    list.add(i + " no thing");
                isLoad = false;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("add header");
        menu.add("add footer");
        menu.add("change");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //不能在onCreate()中获取View,防止添加时的无缘故的错乱.
//        view = LayoutInflater.from(this).inflate(R.layout.head_layout, null);
//        if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            view.setLayoutParams(params);
//        }
        if (item.getTitle().toString() == "add header") {
            mRecyclerView.addHeaderView(view);
        } else if (item.getTitle().toString() == "add footer") {
            mRecyclerView.addFooterView(view);
        } else {
            view.setVisibility(View.VISIBLE);
        }
        return true;
    }
}
