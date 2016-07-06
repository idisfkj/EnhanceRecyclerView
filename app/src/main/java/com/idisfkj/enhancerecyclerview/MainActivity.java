package com.idisfkj.enhancerecyclerview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private int firstVisible;
    private ArrayList<String> list;
    private boolean isLoad = false;
    private boolean isTop = true;
    private int[] into;
    private int startY = 0;
    private int endY;
    private int moveY = 0;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL));


        list = new ArrayList<>();
        for (int i = 0; i < 13; i++)
            list.add("no thing " + i);

//        view = LayoutInflater.from(this).inflate(R.layout.footer_layout, null);
        view = LayoutInflater.from(this).inflate(R.layout.head_layout, null);
        text = (TextView) view.findViewById(R.id.header_text);
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 50);
        params.setMargins(0, -50, 0, 0);
        view.setLayoutParams(params);
//        mRecyclerView.addFooterView(view);
        mRecyclerView.addHeaderView(view);
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
//                    mHandler.sendEmptyMessageDelayed(1, 2000);
                }
                Log.d("TAG", firstVisible + "==");
                if (firstVisible == 0) {
                    isTop = true;
                } else {
                    isTop = false;
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.height = 50;
                    params.setMargins(0, -50, 0, 0);
                    view.setLayoutParams(params);
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                totalCount = recyclerView.getLayoutManager().getItemCount();
                if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    lastItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                    firstVisible = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                } else {
                    into = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPositions(into);
//                    firstVisible = ((StaggeredGridLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPositions(into);
                    lastItem = into[0];
                }
            }
        });
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isTop) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startY = (int) event.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            endY = (int) event.getY();
                            moveY = endY - startY;
                            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
                            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            params.height = 50;
                            params.setMargins(0, moveY, 0, 0);
                            view.setLayoutParams(params);
                            if (moveY > 180) {
                                text.setText(getString(R.string.release_to_refresh));
                            } else {
                                text.setText(getString(R.string.pull_to_refresh));
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            RecyclerView.LayoutParams params1 = (RecyclerView.LayoutParams) view.getLayoutParams();
                            params1.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            params1.height = 50;

                            if (moveY >= 180) {
                                text.setText(getString(R.string.refreshing));
                                params1.setMargins(0, 0, 0, 0);
                                //刷新数据
                                mHandler.sendEmptyMessageDelayed(2, 3000);
                            } else {
                                params1.setMargins(0, -50, 0, 0);
                            }
                            view.setLayoutParams(params1);
                            break;
                    }
                }
                return false;
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
            if (msg.what == 2) {
                for (int i = 0; i < 2; i++)
                    list.add(0, "add" + i + " no thing");
                RecyclerView.LayoutParams params1 = (RecyclerView.LayoutParams) view.getLayoutParams();
                params1.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params1.height = 50;
                params1.setMargins(0, -50, 0, 0);
                view.setLayoutParams(params1);
                mRecyclerView.getAdapter().notifyDataSetChanged();
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
        View Hview = LayoutInflater.from(this).inflate(R.layout.head_layout, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Hview.setLayoutParams(params);
//        if (mRecyclerView.getLayoutManager() instanceof LinearLayoutManager) {
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            view.setLayoutParams(params);
//        }
        if (item.getTitle().toString() == "add header") {
            mRecyclerView.addHeaderView(Hview);
        } else if (item.getTitle().toString() == "add footer") {
            mRecyclerView.addFooterView(view);
        } else {
            view.setVisibility(View.VISIBLE);
        }
        return true;
    }
}
