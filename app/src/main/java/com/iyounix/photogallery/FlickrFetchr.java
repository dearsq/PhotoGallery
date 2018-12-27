package com.iyounix.photogallery;

import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FlickrFetchr {

    private static final String TAG = "FlickrFetchr";
    private static final String API_KEY = "4f721bgafa75bf6d2cb9af54f937bb70";

    // 从指定URL获取原始数据并返回一个字节流数组
    public byte[] getUrlBytes(String urlSpec) throws IOException {

        //首先根据传入的字符串参数，如 https://www.iyounix.com，创建一个URL对象
        URL url = new URL(urlSpec);
        //调用openConnection()方法创建一个指向要访问URL的连接对象
        //URL.openConnection()方法默认返回的是URLConnection对象
        //强转为 HttpURLConnection
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    // 将getUrlBytes(String)方法返回的结果转换为String
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public void fetchItems(){
        try {
            //使用 Uri.Builder 构建了 Flickr API 请求 URL
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format","json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON:" + jsonString);
        } catch (IOException e) {
            Log.e(TAG, "Failed to fetch items", e);
        }
    }
}
