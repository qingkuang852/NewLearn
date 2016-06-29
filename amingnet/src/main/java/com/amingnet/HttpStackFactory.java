package com.amingnet;

import android.os.Build;

/**
 * 根据api版本选择httpclient或者httpUrlConnection
 * Created by wenming on 2016/6/21.
 */
public final class HttpStackFactory {
    private static final int GINGERBREAD_SDK_NUM = 9;
    //根据api版本号来创建不同的http执行器
    public static HttpStack createHttpStack(){
        int runTimeSDKApi = Build.VERSION.SDK_INT;
        if (runTimeSDKApi >= GINGERBREAD_SDK_NUM){
            return new HttpUrlConnStack();
        }else {
            return new HttpClientStack();
        }
    }
}
