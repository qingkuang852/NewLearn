package com.newlearn;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class IpcActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipc);

        findViewById(R.id.buttonPanel1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userBundle();
            }
        });

        findViewById(R.id.buttonPanel2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userFileShare();
            }
        });

        findViewById(R.id.buttonPanel3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userMessenger();
            }
        });
    }

    private void userMessenger() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(this, "com.newlearn.server.MessengerService"));
        bindService(intent,conn,BIND_AUTO_CREATE);
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Messenger mMessenger = new Messenger(service);
            Message msg = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putString("data","这里是客户端");
            msg.setData(bundle);
            try {
                mMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        //写到文件里面去
        final String path = Environment.getExternalStorageDirectory().getPath();
        Log.v("fag", "path >>>>>>" + path);
        new Thread(new Runnable() {
            @Override
            public void run() {
                File dir = new File(path+"/writeFile");
                if (!dir.exists()){
                    dir.mkdirs();
                }
                String data = "哈哈哈";
                FileOutputStream fileOutputStream;
                try {
                    fileOutputStream = new FileOutputStream(dir.getPath()+"/ff.txt");
                    byte[] bytes = data.getBytes();
                    fileOutputStream.write(bytes);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void userFileShare() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(this, "com.newlearn.server.BundleActivity"));
        startActivity(intent);
    }

    private void userBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("data", "数据");
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setComponent(new ComponentName(this, "com.newlearn.server.BundleActivity"));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        super.onDestroy();
    }
}
