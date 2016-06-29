package com.amingnet;

import android.telecom.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 请求队列，使用优先队列，使得请求可以按照优先级进行处理
 * Created by wenming on 2016/6/21.
 */
public final class RequestQueue {
    //线程安全的请求队列
    private BlockingQueue<ARequest<?>> mRequestQueue = new PriorityBlockingQueue<>();
    //请求的序列化生成器
    private AtomicInteger mSerialNumGenerator = new AtomicInteger(0);
    //默认的核心数为CPU格式加1
    public static int DEFAULT_CORE_NUM = Runtime.getRuntime().availableProcessors()+1;
    //CPU核心数+1个分发线程数
    private int mDispatcherNum = DEFAULT_CORE_NUM;
    //NetworkExecutor 执行网络请求的线程
    private NetworkExecutor[] mDispatchers = null;
    //Http请求的真正执行者
    private HttpStack mHttpStack;

    protected RequestQueue(int coreNum,HttpStack httpStack){
        mDispatcherNum = coreNum;
        mHttpStack = httpStack!=null ? httpStack : HttpStackFactory.createHttpStack();
    }

    //启动NetworkExecutor
    private final void startNetworkExecutors(){
        mDispatchers = new NetworkExecutor[mDispatcherNum];
        for (int i = 0; i < mDispatcherNum; i++) {
            mDispatchers[i] = new NetworkExecutor(mRequestQueue,mHttpStack);
            mDispatchers[i].start();
        }
    }

    public void start(){
        startNetworkExecutors();
    }

    /*
        停止NetworkExecutor
     */
    public void stop(){
        if (mDispatchers != null && mDispatchers.length>0){
            for (int i = 0; i < mDispatchers.length; i++) {
                mDispatchers[i].quit();
            }
        }
    }

    //添加请求到队列中
    public void addRequest(ARequest<?> request){
        if (!mRequestQueue.contains(request)){
            //为请求设置序列号
            request.setSerialNumber(this.generateSerialNumber());
            mRequestQueue.add(request);
        }else {
            Log.d("","请求队列中已存在该请求");
        }
    }

    /*
        为每个请求生成序列号
     */
    private int generateSerialNumber(){
        return mSerialNumGenerator.incrementAndGet();
    }
}
