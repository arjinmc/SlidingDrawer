package com.arjinmc.slidingdrawer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * basic layout
 * Created by Eminem Lo on 30/6/17.
 * Email arjinmc@hotmail.com
 */

public class BasicDemo extends AppCompatActivity {

    private SlidingDrawer mSlidingDrawer;
    private Button mBtnOpenPartly;
    private Button mBtnClose;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        getSupportActionBar().setSubtitle("basic");

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
    }
}
