package com.example.app_template_java.views.base;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewWrapper<V extends View> extends RecyclerView.ViewHolder {

    private V view;

    public RecyclerViewWrapper(@NonNull V itemView) {
        super(itemView);
        view = itemView;
    }

    public V getView() {
        return view;
    }
}
