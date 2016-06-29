package com.amingnet;

import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.ReasonPhraseCatalog;
import org.apache.http.StatusLine;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Locale;

/**
 * 请求结果类，将结果存储在rawData中，继承自http包的BasicHttpResponse
 * Created by wenming on 2016/6/20.
 */
public class AResponse extends BasicHttpResponse{

    //原始的response主体数据
    public byte[] rawData = new byte[0];

    public AResponse(StatusLine statusLine){
        super(statusLine);
    }

    public AResponse(ProtocolVersion ver, int code, String reason) {
        super(ver, code, reason);
    }

    @Override
    public void setEntity(HttpEntity entity) {
        super.setEntity(entity);
        rawData = entityToBytes(getEntity());
    }

    public byte[] getRawData() {
        return rawData;
    }

    private byte[] entityToBytes(HttpEntity entity){
        try {
            return EntityUtils.toByteArray(entity);
        }catch (IOException e){
            e.printStackTrace();
        }
        return new byte[0];
    }

    public int getStatusCode(){
        return getStatusLine().getStatusCode();
    }

    public String getMessage(){
        return getStatusLine().getReasonPhrase();
    }
}
