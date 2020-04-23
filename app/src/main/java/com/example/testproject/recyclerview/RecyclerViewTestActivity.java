package com.example.testproject.recyclerview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.testproject.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewTestActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private Button mBtn,mBtnNotifydatasetchange,mBtnSwitchAdapter;
    private EditText mEdt;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_test);
        mRecyclerView = findViewById(R.id.recyclerview);
        mBtn = findViewById(R.id.btn);
        mBtnNotifydatasetchange = findViewById(R.id.btn2);
        mBtnSwitchAdapter = findViewById(R.id.btnSwitch);
        mEdt = findViewById(R.id.edt_num);
        mBtn.setOnClickListener(v -> click());
        setLayoutManager();
        setRecyclerViewDatas();
        Father son = new Son();
        son.eat();
    }

    abstract class Father{
        protected void eat(){
            Log.e("xiaojun","father eat");
        }
    }

    class Son extends Father{
        @Override
        protected void eat() {
//            super.eat();
            Log.e("xiaojun","son eat");
        }
    }

    private void click() {
        String text = mEdt.getText().toString();
        mRecyclerView.smoothScrollToPosition(Integer.parseInt(text));
    }

    private void setRecyclerViewDatas() {
        List<String> datas = getDatasForRecyclerView();
        mAdapter = new RecyclerViewAdapter(datas);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void setLayoutManager() {
//        CustomLayoutManager layoutManager = new CustomLayoutManager();
//        CustomLayoutManagerAgain layoutManager = new CustomLayoutManagerAgain();
//        CustomLayoutManagerThird layoutManager = new CustomLayoutManagerThird();
//        CustomLayoutManagerForuth layoutManager = new CustomLayoutManagerForuth();
//        CustomLayoutManagerFifth layoutManager = new CustomLayoutManagerFifth();
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
//        mLayoutManager = new CustomLayoutManagerNew();
//        mLayoutManager = new CustomLayoutManagerNew();
        mLayoutManager = new LayoutManagerFinal2();
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    private List<String> mList;

    private List<String> getDatasForRecyclerView() {
        mList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            mList.add(i + "");
        }
        return mList;
//        mAdapter = new RecyclerViewAdapter(list);
    }

    public void notifyDataSetChange(View view){
        mList.set(0,28+"");
        mAdapter.notifyDataSetChanged();
    }

    public void switchNewAdapter(View view){
        setRecyclerViewDatas();
    }

}
