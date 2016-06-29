package com.query;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final QueryAnimateView queryAnimateView = (QueryAnimateView) findViewById(R.id.query);

        findViewById(R.id.query).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (queryAnimateView.isCanQuery()) {
                    queryAnimateView.query();
                }
            }
        });

        findViewById(R.id.btn_end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryAnimateView.complete();
            }
        });

        findViewById(R.id.btn_requery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryAnimateView.reQuery();
            }
        });

        ProgressDialog a;
    }
}
