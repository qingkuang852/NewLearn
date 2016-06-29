package com.amingnet;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wenming on 2016/6/21.
 */
public class HttpUrlConnStack implements HttpStack {
    @Override
    public AResponse performRequest(ARequest<?> request) {
        HttpURLConnection urlConnection = null;
        try {
            //构建httpUrlConnection
            urlConnection = createUrlConnection(request.getUrl());
            //设置headers
            setRequestHeaders(urlConnection, request);
            //设置Body参数
            setRequestParams(urlConnection,request);
            return fetchResponse(urlConnection);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private HttpURLConnection createUrlConnection(String url) throws IOException {
        URL newUrl = new URL(url);
        URLConnection urlConnection = newUrl.openConnection();
        urlConnection.setConnectTimeout(15000);
        urlConnection.setReadTimeout(10000);
        urlConnection.setDoInput(true);
        urlConnection.setUseCaches(false);
        return (HttpURLConnection) urlConnection;
    }

    private void setRequestHeaders(HttpURLConnection connection,ARequest<?> request){
        Set<String> headerKeys = request.getHeaders().keySet();
        for (String headerName: headerKeys) {
            connection.addRequestProperty(headerName,request.getHeaders().get(headerName));
        }
    }

    protected void setRequestParams(HttpURLConnection connection,ARequest<?> request) throws IOException {
        ARequest.HttpMethod method = request.getHttpMethod();
        connection.setRequestMethod(method.toString());
        byte[] body = request.getBody();
        if (body!=null){
            connection.setDoOutput(true);
            connection.addRequestProperty(ARequest.HEADER_CONTENT_TYPE, request.getBodyContentType());
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            dos.write(body);
            dos.close();
        }
    }

    private AResponse fetchResponse(HttpURLConnection connection) throws IOException {
        ProtocolVersion version = new ProtocolVersion("HTTP",1,1);
        int responseCode = connection.getResponseCode();
        if (responseCode == -1){
            throw new IOException("Could not retrieve response code from HttpUrlConnection.");
        }
        //状态行数据
        StatusLine responseStatus = new BasicStatusLine(version,connection.getResponseCode(),connection.getResponseMessage());
        //构建response
        AResponse response = new AResponse(responseStatus);
        //设置response数据
        response.setEntity(entityFromURLConnection(connection));
        addHeadersToResponse(response,connection);
        return response;
    }

    /*
        执行HTTP请求之后获取其数据流，即返回请求结果的流
     */
    private HttpEntity entityFromURLConnection(HttpURLConnection connection){
        BasicHttpEntity entity = new BasicHttpEntity();
        InputStream is = null;
        try {
            is = connection.getInputStream();
        }catch (IOException e) {
            e.printStackTrace();
            is= connection.getErrorStream();
        }
        entity.setContent(is);
        entity.setContentLength(connection.getContentLength());
        entity.setContentEncoding(connection.getContentEncoding());
        entity.setContentType(connection.getContentType());
        return entity;
    }

    private void addHeadersToResponse(BasicHttpResponse response,HttpURLConnection conn){
        for (Map.Entry<String,List<String>> header:conn.getHeaderFields().entrySet()){
            if (header.getKey()!=null){
                Header h = new BasicHeader(header.getKey(),header.getValue().get(0));
                response.addHeader(h);
            }
        }
    }
}
