package com.anadolstudio.adelaide.adapters;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class GalleryDiffUtilCallback extends DiffUtil.Callback {
    private final List<String> oldList;
    private final List<String> newList;

    public GalleryDiffUtilCallback(List<String> oldList, List<String> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        String oldItem = oldList.get(oldItemPosition);
        String newItem = newList.get(newItemPosition);

        return oldItem.equals(newItem);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        String oldItem = oldList.get(oldItemPosition);
        String newItem = newList.get(newItemPosition);
        return oldItem.equals(newItem);
    }
}
