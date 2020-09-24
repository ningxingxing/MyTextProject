package com.example.mytestproject;


import androidx.recyclerview.widget.DiffUtil;

import com.example.mytestproject.recycle.RecyclerItem;

import java.util.ArrayList;
import java.util.List;

class MyCallback extends DiffUtil.Callback {
    private List<RecyclerItem> mOldData, mNewData;

    MyCallback(List<RecyclerItem> data, List<RecyclerItem> students) {
        this.mOldData = data;
        this.mNewData = students;
    }

    @Override
    public int getOldListSize() {
        return mOldData.size();
    }

    @Override
    public int getNewListSize() {
        return mNewData.size();
    }

    // 判断Item是否已经存在
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldData.get(oldItemPosition).getName() == mNewData.get(newItemPosition).getName();
    }

    // 如果Item已经存在则会调用此方法，判断Item的内容是否一致
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldData.get(oldItemPosition).getName().equals(mNewData.get(newItemPosition).getName());
    }
}