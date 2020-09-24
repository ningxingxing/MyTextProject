package com.example.mytestproject.recycle;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mytestproject.R;


/**
 * Created by yushuangping on 2018/8/23.
 */

public class DescHolder extends RecyclerView.ViewHolder {
    public ImageView descView;

    public DescHolder(View itemView) {
        super(itemView);
        initView();
    }

    private void initView() {
        descView = (ImageView) itemView.findViewById(R.id.iv_icon);
    }
}
