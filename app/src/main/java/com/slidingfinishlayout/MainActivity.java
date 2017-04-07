package com.slidingfinishlayout;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity implements SlidingFinishView.SlidingFinishCallback{

    private SlidingFinishView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view= (SlidingFinishView) findViewById(R.id.activity_main);
//        view.setSlidingDirection(SlidingFinishView.Horizontal);
    }

    @Override
    public void onFinish() {
        finish();
    }
}
