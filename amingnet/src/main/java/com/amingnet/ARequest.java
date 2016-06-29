package com.amingnet;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 网络请求类
 * Created by wenming on 2016/6/19.
 */
public abstract class ARequest<T> implements Comparable<ARequest<T>> {
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    //默认的编码方式
    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";
    //请求序列号
    protected int mSerialNum = 0;
    //优先级默认设置为NORMAL
    protected Priority mPriority = Priority.NORMAL;
    //是否取消该请求
    protected boolean isCancel = false;
    //该请求是否应该缓存
    private boolean mShouldCache = true;
    //请求的Listener
    protected RequestListener<T> mRequestListener;
    //请求的URL
    private String mUrl = "";
    //请求的方式
    HttpMethod mHttpMethod = HttpMethod.GET;
    //请求的header
    private Map<String,String> mHeaders = new HashMap<>();
    //请求参数
    private Map<String,String> mBodyParams = new HashMap<>();

    /*
        构造方法
     */
    public ARequest(HttpMethod httpMethod,String url,RequestListener requestListener){
        mHttpMethod = httpMethod;
        mUrl = url;
        mRequestListener = requestListener;
    }

    //从原生的网络请求中解析结果，子类必须复写
    public abstract T parseResponse(AResponse response);

    //处理AResponse，该方法需要运行在UI线程
    public final void deliveryResponse(AResponse response){
        T result = parseResponse(response);
        if (mRequestListener!=null){
            int stCode = response!=null ?response.getStatusCode() : -1;
            String msg = response!=null ? response.getMessage() : "unknown error";
            mRequestListener.onComplete(stCode,result,msg);
        }
    }

    protected String getParamsEncoding(){
        return DEFAULT_PARAMS_ENCODING;
    }

    public String getBodyContentType(){
        return "application/x-www-form-urlencoded; charset="+getParamsEncoding();
    }

    //返回POST或者PUT请求时的Body参数字节数组
    public byte[] getBody(){
        Map<String,String> params = getParams();
        if (params!=null&&params.size()>0){
            return encodeParameters(params,getParamsEncoding());
        }
        return null;
    }

    //将参数转换为URL编码的参数串，格式为key1=value1&key2=value2
    private byte[] encodeParameters(Map<String,String> params,String paramsEncoding){
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String,String> entry : params.entrySet()){
                encodedParams.append(URLEncoder.encode(entry.getKey(),paramsEncoding));
                encodedParams.append("=");
                encodedParams.append(URLEncoder.encode(entry.getValue(),paramsEncoding));
                encodedParams.append("&");
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        }catch (UnsupportedEncodingException e){
            throw new RuntimeException("Encoding not supported:"+paramsEncoding,e);
        }
    }

    //用户对请求的排序处理，根据优先级和加入到队列的序号进行排序
    @Override
    public int compareTo(ARequest<T> another) {
        Priority myPriority = this.getPriority();
        Priority anotherPriority = another.getPriority();
        return myPriority.equals(anotherPriority) ? this.getSerialNumber() - another.getSerialNumber() : myPriority.ordinal() - anotherPriority.ordinal();
    }

    public String getUrl() {
        return mUrl;
    }

    public Map<String, String> getParams() {
        return mBodyParams;
    }

    public Priority getPriority() {
        return mPriority;
    }

    public int getSerialNumber() {
        return mSerialNum;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public void setSerialNumber(int mSerialNum) {
        this.mSerialNum = mSerialNum;
    }

    public HttpMethod getHttpMethod() {
        return mHttpMethod;
    }

    //HTTP请求方法枚举，这里只有四种
    public static enum HttpMethod{
        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE");

        private String mHttpMethod = "";
        private HttpMethod(String method){
               mHttpMethod = method;
        }

        @Override
        public String toString() {
            return mHttpMethod;
        }
    }

    //优先级枚举
    public static enum Priority{
        LOW,NORMAL,HIGH,IMMEDIATE
    }

    /**
     * 网络请求的listener，执行在ui线程
     */
    public static interface RequestListener<T>{
        public void onComplete(int stCode,T response,String errMsg);
    }

    public boolean isCanceled() {
        return isCancel;
    }

    public boolean shouldCache() {
        return mShouldCache;
    }
}
