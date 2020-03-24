package com.example.testproject.recyclerview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.testproject.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewTestActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_test);
        mRecyclerView = findViewById(R.id.recyclerview);
        setLayoutManager();
        setRecyclerViewDatas();
    }

    private void setRecyclerViewDatas(){
        List<String> datas = getDatasForRecyclerView();
        mAdapter = new RecyclerViewAdapter(datas);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setLayoutManager(){
//        CustomLayoutManager layoutManager = new CustomLayoutManager();
//        CustomLayoutManagerAgain layoutManager = new CustomLayoutManagerAgain();
//        CustomLayoutManagerThird layoutManager = new CustomLayoutManagerThird();
//        CustomLayoutManagerForuth layoutManager = new CustomLayoutManagerForuth();
//        CustomLayoutManagerFifth layoutManager = new CustomLayoutManagerFifth();
        CustomLayoutManagerRecycler2 layoutManager = new CustomLayoutManagerRecycler2(this);
        layoutManager.setOnSelectedListener(new CustomLayoutManagerRecycler2.OnSelectedListener() {
            @Override
            public void selected(int position) {
                Log.e("RecyclerView1","selected:"+position);
            }

            @Override
            public void change(int position) {
                Log.e("RecyclerView1","change:"+position);
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);
    }

    private List<String> getDatasForRecyclerView(){
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            list.add(i+"");
        }
        return list;
//        mAdapter = new RecyclerViewAdapter(list);
    }
}
