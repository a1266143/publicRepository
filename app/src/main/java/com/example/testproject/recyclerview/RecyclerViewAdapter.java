package com.example.testproject.recyclerview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testproject.R;

import java.util.List;

/**
 * RecyclerViewAdapter
 * created by xiaojun at 2020/3/9
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    public List<String> mDatas;

    public RecyclerViewAdapter(List<String> datas){
        this.mDatas = datas;
    }

    private int mCreatedView;
    private int mBindViewHolder;

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = ((LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_layout,parent,false);
        mCreatedView++;
        Log.e("xiaojun","mCreatedView="+mCreatedView);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.mTv.setText(mDatas.get(position));
        mBindViewHolder++;
        Log.e("xiaojun","onBindViewHolder="+mBindViewHolder);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
}
