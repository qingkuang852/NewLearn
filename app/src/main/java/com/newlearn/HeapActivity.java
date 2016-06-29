package com.newlearn;

import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HeapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heap);

        //获取堆的大小
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int heapSize = am.getLargeMemoryClass();
    }

}
