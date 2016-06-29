package com.amingnet;

import android.text.TextUtils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

/**
 * Created by wenming on 2016/6/21.
 */
public class MultipartEntity implements HttpEntity {

    private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    //回车符和换行符
    private final String NEW_LINE_STR = "\r\n";

    private final String CONTENT_TYPE = "Content-Type: ";
    private final String CONTENT_DISPOSITION = "Content-Disposition: ";
    //文本参数和字符集
    private final String TYPE_TEXT_CHARSET = "text/plain; charset=UTF-8";

    //字节流参数
    private final String TYPE_OCTET_STREAM = "application/octet-stream";
    //字节数组参数
    private final byte[] BINARY_ENCODING = "Content-Transfer-Encoding: binary\r\n\r\n".getBytes();
    //文本参数
    private final byte[] BIT_ENCODING = "Content-Transfer-Encoding: 8bit\r\n\r\n".getBytes();
    //参数分隔符
    private String mBoundary = null;
    //输出流，用于缓存参数数据
    ByteArrayOutputStream mOutputStream = new ByteArrayOutputStream();

    public MultipartEntity(){
        this.mBoundary = generateBoundary();
    }

    //生成并返回参数的boundary分隔符
    private String generateBoundary(){
        final StringBuffer buf = new StringBuffer();
        final Random random = new Random();
        for (int i = 0; i < 30; i++) {
            buf.append(MULTIPART_CHARS[random.nextInt(MULTIPART_CHARS.length)]);
        }
        return buf.toString();
    }

    //参数开头的分隔符
    private void writeFirstBoundary() throws IOException {
        mOutputStream.write(("--" + mBoundary + "\r\n").getBytes());
    }

    //添加文本参数
    public void addStringPart(final String paramName,final String value){
        writeToOutputStream(paramName, value.getBytes(), TYPE_TEXT_CHARSET, BIT_ENCODING, "");
    }

    //将数据写入到输出流中
    private void writeToOutputStream(String paramName,byte[] rawData,String type,byte[] encodingBytes,String fileName){
        try {
            writeFirstBoundary();
            mOutputStream.write((CONTENT_TYPE+type+NEW_LINE_STR).getBytes());
            mOutputStream.write((getContentDispositionBytes(paramName,fileName)));
            mOutputStream.write(encodingBytes);
            mOutputStream.write(rawData);
            mOutputStream.write(NEW_LINE_STR.getBytes());
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private byte[] getContentDispositionBytes(String paramName, String fileName){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(CONTENT_DISPOSITION+"form-data; name=\""+paramName+"\"");
        if (!TextUtils.isEmpty(fileName)){
            stringBuilder.append("; filename=\""+fileName+"\"");
        }
        return stringBuilder.append(NEW_LINE_STR).toString().getBytes();
    }

    //添加字节数组参数，例如Bitmap的字节流数据
    public void addByteArrayPart(String paramName, final byte[] rawData){
        writeToOutputStream(paramName,rawData,TYPE_OCTET_STREAM,BINARY_ENCODING,"no-file");
    }

    //添加文件参数，可以实现文件上传功能
    public void addFilePart(final String key,File file){
        InputStream fis = null;
        try {
            fis = new FileInputStream(file);
            writeFirstBoundary();
            final String type = CONTENT_TYPE + TYPE_OCTET_STREAM + NEW_LINE_STR;
            mOutputStream.write(getContentDispositionBytes(key,file.getName()));
            mOutputStream.write(type.getBytes());
            mOutputStream.write(BINARY_ENCODING);

            final byte[] tmp = new byte[4096];
            int len = 0;
            while ((len = fis.read(tmp)) != -1){
                mOutputStream.write(tmp,0,len);
            }
            mOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            closeSilently(fis);
        }
    }

    private void closeSilently(Closeable closeable){
        try {
            if (closeable!=null){
                closeable.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public long getContentLength() {
        return mOutputStream.toByteArray().length;
    }

    @Override
    public Header getContentType() {
        return new BasicHeader("Content-Type","multipart/form-data; boundary" + mBoundary);
    }

    @Override
    public Header getContentEncoding() {
        return null;
    }

    @Override
    public InputStream getContent() {
        return new ByteArrayInputStream(mOutputStream.toByteArray());
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        //参数最末尾的结束符
        final String endString = "--" + mBoundary + "--\r\n";
        //写入结束符
        mOutputStream.write(endString.getBytes());
        //将缓存在mOutputStream中的数据全部写入到outputStream中
        outputStream.write(mOutputStream.toByteArray());
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public void consumeContent() throws IOException {
        if (isStreaming()){
            throw new UnsupportedOperationException(
                    "Streaming entity does not implement #consumeContent()");
        }
    }
}
