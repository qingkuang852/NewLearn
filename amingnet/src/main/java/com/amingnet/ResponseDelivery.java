package com.amingnet;

import android.annotation.NonNull;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

/**
 * Created by wenming on 2016/6/21.
 */
public class ResponseDelivery implements Executor{
    //关联主线程消息队列的handler
    Handler mResponseHandler = new Handler(Looper.getMainLooper());

    /*
        处理结果请求，使其执行在ui线程
     */
    public void deliveryResponse(final ARequest<?> request,final AResponse response){
        Runnable responseRunnable = new Runnable() {
            @Override
            public void run() {
                request.deliveryResponse(response);
            }
        };
        execute(responseRunnable);
    }

    @Override
    public void execute(Runnable command) {
        mResponseHandler.post(command);
    }
}
