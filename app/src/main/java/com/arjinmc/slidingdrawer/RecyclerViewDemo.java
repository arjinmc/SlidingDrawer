package com.arjinmc.slidingdrawer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Eminem Lu on 4/7/17.
 * Email arjinmc@hotmail.com
 */

public class RecyclerViewDemo extends AppCompatActivity {

    private SlidingDrawer mSlidingDrawer;
    private Button mBtnOpenPartly;
    private Button mBtnClose;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        getSupportActionBar().setSubtitle("recyclerview");

        mSlidingDrawer = (SlidingDrawer) findViewById(R.id.slidingdrawer);
        mSlidingDrawer.setClosedPostionHeight(120);
        mSlidingDrawer.setPartlyPositionHeight(400);
        mSlidingDrawer.setAutoRewindHeight(280);
        mSlidingDrawer.setOnFirstChildClickListener(new SlidingDrawer.OnFirstChildClickListener() {
            @Override
            public void onClick() {
                Log.e("recyclerview","click first child");
            }
        });
        mBtnOpenPartly = (Button) findViewById(R.id.btn_open_partly);
        mBtnOpenPartly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlidingDrawer.openPartly();
            }
        });

        mBtnClose = (Button) findViewById(R.id.btn_close);
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingDrawer.close();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new MyAdapter());
    }


    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(RecyclerViewDemo.this)
                    .inflate(R.layout.item_listview, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.tvText.setText("item " + position);
            holder.tvText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("recyclerview","item"+position);
                }
            });

        }

        @Override
        public int getItemCount() {
            return 50;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvText;

        public MyViewHolder(View itemView) {
            super(itemView);

            tvText = (TextView) itemView.findViewById(R.id.tv_listview);

        }
    }
}
