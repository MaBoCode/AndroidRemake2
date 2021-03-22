package com.example.app_template_java.views.base;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerViewAdapter<T, V extends View> extends RecyclerView.Adapter<RecyclerViewWrapper<V>> {

    protected List<T> items;

    @NonNull
    @Override
    public final RecyclerViewWrapper<V> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewWrapper<V>(onCreateItemView(parent, viewType));
    }

    protected abstract V onCreateItemView(ViewGroup parent, int viewType);

    public void setItems(List<T> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
