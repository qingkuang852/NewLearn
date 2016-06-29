package com.newlearn.server;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.newlearn.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BundleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bundle);

        Intent intent = getIntent();
        if (intent.getExtras()!=null) {
            String data = intent.getExtras().getString("data");
            Log.v("fag", "data >>>>>>>>>>>> " + data);
        }

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoverFromFile();
            }
        });
    }

    private void recoverFromFile(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = Environment.getExternalStorageDirectory().getPath()+"/writeFile/ff.txt";
                File  file = new File(path);
                if (file.exists()){
                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);

                        int len = fileInputStream.available();
                        byte[] buffer = new byte[len];
                        fileInputStream.read(buffer);
                        String data = new String(buffer,"utf-8");
                        Log.v("fag","read >>>>>>" + data);
                        fileInputStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
