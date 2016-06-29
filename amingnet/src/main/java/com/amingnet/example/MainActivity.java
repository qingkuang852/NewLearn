package com.amingnet.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amingnet.AMingNet;
import com.amingnet.ARequest;
import com.amingnet.HttpUrlConnStack;
import com.amingnet.R;
import com.amingnet.RequestQueue;
import com.amingnet.StringRequest;

public class MainActivity extends AppCompatActivity {
    final RequestQueue queue = AMingNet.newRequestQueue();
    private int time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView tv = (TextView) findViewById(R.id.tv_response);

        final String url = "http://gank.io/api/data/福利/1/1";

        findViewById(R.id.btn_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest request = new StringRequest(ARequest.HttpMethod.GET, url, new ARequest.RequestListener<String>() {
                    @Override
                    public void onComplete(int stCode, String response, String errMsg) {
                        tv.setText(time+" response :"+response);
                        time ++ ;
                    }
                });

                queue.addRequest(request);
            }
        });

        findViewById(R.id.btn_move).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.scrollBy(20,0);

            }
        });
    }

    @Override
    protected void onDestroy() {
        queue.stop();
        super.onDestroy();
    }
}
