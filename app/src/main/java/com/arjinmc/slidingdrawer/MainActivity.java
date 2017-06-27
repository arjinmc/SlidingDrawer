package com.arjinmc.slidingdrawer;

import android.os.Bundle;
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
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private SlidingDrawer mSlidingDrawer;
    private ListView mlistView;
    private Button btnTest;
    private Button btnOpenPartly;
    private Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSlidingDrawer = (SlidingDrawer) findViewById(R.id.slidingdrawer);
        mSlidingDrawer.setClosedPostionHeight(180);
        mSlidingDrawer.setPartlyPositionHeight(400);
        mSlidingDrawer.setAutoRewindHeight(250);


        btnOpenPartly = (Button) findViewById(R.id.btn_open_partly);
        btnOpenPartly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlidingDrawer.openPartly();
            }
        });

        btnClose = (Button) findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingDrawer.close();
            }
        });

        btnTest = (Button) findViewById(R.id.btn_test);
        btnTest.setOnClickListener(new View.OnClickListener() {
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
                TextView tvHh = (TextView) view.findViewById(R.id.tv_listview);
                tvHh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        mSlidingDrawer.open();
                        Toast.makeText(MainActivity.this,"item child click:"+position,Toast.LENGTH_SHORT).show();
                        Log.e("tag","item child click:"+position);
                        return ;
                    }
                });

//                Toast.makeText(MainActivity.this,"item click:"+position,Toast.LENGTH_SHORT).show();
                Log.e("tag","item click:"+position);

            }
        });


    }

    private class ListViewAdapter extends BaseAdapter{

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
                view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_listview,null);
            ((TextView) view.findViewById(R.id.tv_listview)).setText("item"+i);
            return view;
        }
    }
}
