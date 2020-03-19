package com.example.testproject.recyclerview_play;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * The views in the list are represented by view holder objects.
 * Each view holder is in charge of displaying a single item with a view.
 * For example, if your list shows music collection, each view holder might represent a single album.
 * The view holder objects are managed by an adapter, which you create by extending RecyclerView.Adapter.
 *
 * created by xiaojun at 2019/12/13
 */
public class ViewHolder extends RecyclerView.ViewHolder {
    public ViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}
