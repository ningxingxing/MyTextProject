package com.example.mytestproject.recycle;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mytestproject.R;


/**
 * Created by yushuangping on 2018/8/23.
 */

public class HeaderHolder extends RecyclerView.ViewHolder {
    public TextView titleView;
    public ImageView openView;
    public LinearLayout llHotel;

    public HeaderHolder(View itemView) {
        super(itemView);
        initView();
    }

    private void initView() {
        titleView = (TextView) itemView.findViewById(R.id.tv_title);
        openView = (ImageView) itemView.findViewById(R.id.tv_icon);
        llHotel = itemView.findViewById(R.id.ll_hotel);
    }
}
