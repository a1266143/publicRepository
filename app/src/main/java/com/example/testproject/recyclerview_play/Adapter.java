package com.example.testproject.recyclerview_play;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * The adapter creates view holders as needed
 * The adapter also binds the view holders to their data.
 * It does this by assigning the view holder to a position, and calling the adapter's onBindViewHolder() method.
 * That method uses the view holder's position to determine what the contents should be, based on its list position.
 *
 * created by xiaojun at 2019/12/13
 */
public class Adapter extends RecyclerView.Adapter<ViewHolder> {
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
