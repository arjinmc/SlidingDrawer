package com.arjinmc.slidingdrawer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Eminem Lu on 30/6/17.
 * Email arjinmc@hotmail.com
 */

public class ListViewDemo extends AppCompatActivity {

    private SlidingDrawer mSlidingDrawer;
    private ListView mlistView;
    private Button mBtnTest;
    private Button mBtnOpenPartly;
    private Button mBtnClose;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        mSlidingDrawer = (SlidingDrawer) findViewById(R.id.slidingdrawer);
        mSlidingDrawer.setClosedPostionHeight(120);
        mSlidingDrawer.setPartlyPositionHeight(400);
        mSlidingDrawer.setAutoRewindHeight(280);


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

        mBtnTest = (Button) findViewById(R.id.btn_test);
        mBtnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("button","click backend");
            }
        });

        mlistView = (ListView) findViewById(R.id.listview);
        mlistView.setAdapter(new ListViewAdapter());
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

//                Toast.makeText(MainActivity.this,"item click:"+position,Toast.LENGTH_SHORT).show();
                Log.e("tag","item click:"+position);

            }
        });

    }

    private class ListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 200;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view==null)
                view = LayoutInflater.from(ListViewDemo.this).inflate(R.layout.item_listview,null);
            ((TextView) view.findViewById(R.id.tv_listview)).setText("item"+i);
            return view;
        }
    }
}
