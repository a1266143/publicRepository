package com.example.testproject.recyclerview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testproject.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewTestActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private TextView mTv;

    public void returnClick(View view){
        mRecyclerView.smoothScrollToPosition(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_test);
        mRecyclerView = findViewById(R.id.recyclerview);
        mTv = findViewById(R.id.activity_recycler_view_test_tv);
        setLayoutManager();
        setRecyclerViewDatas();
    }

    private void setRecyclerViewDatas(){
        List<String> datas = getDatasForRecyclerView();
        mAdapter = new RecyclerViewAdapter(this,datas);
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
                mTv.setText(position+"被选择");
            }

            @Override
            public void change(int position) {
                Log.e("RecyclerView1","change:"+position);
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);
    }

    int[] res = {R.drawable.img,R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6,R.drawable.img7,R.drawable.img8,R.drawable.img9,
            R.drawable.img,R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6,R.drawable.img7,R.drawable.img8,R.drawable.img9,
            R.drawable.img,R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6,R.drawable.img7,R.drawable.img8,R.drawable.img9,
            R.drawable.img,R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6,R.drawable.img7,R.drawable.img8,R.drawable.img9,
            R.drawable.img,R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6,R.drawable.img7,R.drawable.img8,R.drawable.img9,
            R.drawable.img,R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6,R.drawable.img7,R.drawable.img8,R.drawable.img9,
            R.drawable.img,R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4,R.drawable.img5,R.drawable.img6,R.drawable.img7,R.drawable.img8,R.drawable.img9};

    private List<String> getDatasForRecyclerView(){
        List<String> list = new ArrayList<>();
        for (int i = 0; i < res.length; i++) {
            list.add(res[i]+"");
        }
        return list;
//        mAdapter = new RecyclerViewAdapter(list);
    }
}
