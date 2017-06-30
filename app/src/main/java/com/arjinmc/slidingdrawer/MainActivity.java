package com.arjinmc.slidingdrawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private MyAdapter mMyAdapter;
    private String[] mItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mItems = getResources().getStringArray(R.array.items);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mMyAdapter = new MyAdapter();
        mRecyclerView.setAdapter(mMyAdapter);

    }


    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_main, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {

            holder.tvTitle.setText(mItems[position]);
            holder.tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position){
                        case 0:
                            jump(BasicDemo.class);
                            break;
                        case 1:
                            jump(ListViewDemo.class);
                            break;
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            if (mItems != null)
                return mItems.length;
            else return 0;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_item_title);
        }
    }

    private void jump(Class clz){
        startActivity(new Intent(MainActivity.this,clz));
    }


}
