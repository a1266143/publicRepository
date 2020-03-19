package com.example.testproject.recyclerview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

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
        CustomLayoutManagerAgain layoutManager = new CustomLayoutManagerAgain();
//        CustomLayoutManagerThird layoutManager = new CustomLayoutManagerThird();
        mRecyclerView.setLayoutManager(layoutManager);
    }

    private List<String> getDatasForRecyclerView(){
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add((i+1)+"");
        }
        return list;
//        mAdapter = new RecyclerViewAdapter(list);
    }
}
