package com.cadastroaluno.projeto.cadastroaluno;

import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by leobo on 9/18/2016.
 */
public class ListAdapter<T> extends ArrayList<T> {
    private BaseAdapter adapter;

    public void setAdapter(BaseAdapter adapter) {

        this.adapter = adapter;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        clear();
        boolean superAddAll = super.addAll(c);

        notifyDataSetChanged();

        return superAddAll;
    }

    private void notifyDataSetChanged() {
        if (adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }
}
