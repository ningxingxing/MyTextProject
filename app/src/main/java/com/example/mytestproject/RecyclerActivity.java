package com.example.mytestproject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytestproject.recycle.HotelEntityAdapter;
import com.example.mytestproject.recycle.RecyclerItem;
import com.example.mytestproject.utils.ACache;

import java.util.ArrayList;
import java.util.List;

public class RecyclerActivity extends Activity {
    private RecyclerView mRecyclerView;
    private HotelEntityAdapter mAdapter;
    private List<RecyclerItem> mItemList = new ArrayList<>();
    private List<Integer> mIconList = new ArrayList<>();
    private ACache mACache;
    private List<RecyclerItem> mItemList1 = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        mRecyclerView = findViewById(R.id.recyclerView);
        mAdapter = new HotelEntityAdapter(this);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        //设置header
//        manager.setSpanSizeLookup(new SectionedSpanSizeLookup(mAdapter,manager));
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mACache = ACache.get(this);
        for (int i=0;i<15;i++){
            mIconList.add(R.mipmap.ic_launcher);
        }

        for (int i=0;i<5;i++){
            RecyclerItem recyclerItem = new RecyclerItem();
            recyclerItem.setIcon(R.mipmap.ic_launcher);
            recyclerItem.setIconList(mIconList);
            recyclerItem.setName("i="+i);
            mItemList.add(recyclerItem);
        }

        for (int i=0;i<6;i++){
            RecyclerItem recyclerItem = new RecyclerItem();
            recyclerItem.setIcon(R.mipmap.ic_launcher);
            recyclerItem.setIconList(mIconList);
            recyclerItem.setName("i="+i);
            mItemList1.add(recyclerItem);
        }

        mACache.put(this,"xing",mItemList);
        Log.e("nsc","onCreate="+mItemList.size());

        mAdapter.setOnItemClick(new HotelEntityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int section, int position) {
                List<RecyclerItem> list = mACache.getList(RecyclerActivity.this,"xing");
                Log.e("nsc","onCreate="+list);
                Toast.makeText(RecyclerActivity.this,section+"=="+list.size(),Toast.LENGTH_SHORT).show();
            }
        });

        DiffUtil.DiffResult diffResult  = DiffUtil.calculateDiff(new MyCallback(mItemList, mItemList1),true);
        mAdapter.setData(mItemList1);
        diffResult.dispatchUpdatesTo(mAdapter);
    }
}
