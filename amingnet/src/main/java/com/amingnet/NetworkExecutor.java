package com.amingnet;

import android.util.Log;

import java.net.CacheRequest;
import java.util.concurrent.BlockingQueue;

/**
 * Created by wenming on 2016/6/21.
 */
public final class NetworkExecutor extends Thread {
    //网络请求队列
    private BlockingQueue<ARequest<?>> mRequestQueue;
    //网络请求栈
    private HttpStack mHttpStack;
    //结果分发器,将结果投递到主线程
    private static ResponseDelivery mResponseDelivery = new ResponseDelivery();
    //请求缓存
    private static Cache<String,AResponse> mReqCache = new LruMemCache();
    //是否停止
    private boolean isStop = false;

    public NetworkExecutor(BlockingQueue<ARequest<?>> queue,HttpStack httpStack){
        mRequestQueue = queue;
        mHttpStack = httpStack;
    }

    @Override
    public void run() {
        super.run();
        try {
            while (!isStop){
                final ARequest<?> request = mRequestQueue.take();
                if (request.isCanceled()){
                    Log.d("###","取消执行了");
                    continue;
                }
                AResponse response = null;
                if (isUserCache(request)){
                    //从缓存中取
                    //response = mReqCache.get(request.getUrl());
                }else {
                    //从网络中获取数据
                    response = mHttpStack.performRequest(request);
                    //如果该请求需要缓存，那么请求成功则缓存到mResponseCache中
//                    if (request.shouldCache() && isSuccess(response)){
//
//                    }
                }
                //分发请求结果
                mResponseDelivery.deliveryResponse(request,response);
            }
        }catch (InterruptedException e){
            Log.i("","请求分发器退出");
        }
    }

    private boolean isUserCache(ARequest<?> request){
//        return request.shouldCache();
        return false;
    }

    private boolean isSuccess(AResponse response){
        return  response!=null && response.getStatusCode() == 200;
    }

    public void quit(){
        isStop = true;
        interrupt();
    }
}
