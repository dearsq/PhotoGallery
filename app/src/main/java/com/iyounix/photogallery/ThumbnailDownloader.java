package com.iyounix.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


// 这里使用了泛型<T> ,
// ThumbnailDownloader 类的使用者 (比如 PhotoGalleryFragment) 需要使用 T对象来识别每次下载，
// 并确定该使用已下载图片更新哪个UI元素。
public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0; //用来标志下载请求消息
    private Boolean mHasQuit = false;
    private Handler mRequestHandler; //用来存储对 Handler 的引用 //负责在ThumbnailDownloader 后台线程上管理下载请求消息队列
    private ConcurrentMap<T,String> mRequestMap = new ConcurrentHashMap<>(); //线程安全的 HashMap

    //存根方法
    public ThumbnailDownloader() {
        super(TAG);
    }

    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }


    // 初始化 mRequestHandler 并定义该 Handler 在得到消息队列中的下载消息后应执行的任务
    @Override
    protected void onLooperPrepared() {

        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    Log.i(TAG, "Got a request for URL: " + mRequestMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }

    private void handleRequest(final T target) {
        try {
            final String url = mRequestMap.get(target);
            if (url == null) {
                return;
            }
            byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory
                    .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            Log.i(TAG, "Bitmap created");
        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }
    }
    }

    //queueThumbnail 方法需要T类型的对象target 和 String的URL链接)
    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got a URL:" + url);

        //获取并发送信息给他的目标
        if (url == null) {
            mRequestMap.remove(target);
        } else {
            mRequestMap.put(target, url);
            // what: MESSAGE_DOWNLOAD
            // obj:  PhotoHolder
            //
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
                           .sendToTarget();
        }

        // Message 本身不包含 URL 信息.
        // 根据 PhotoHolder 和 URL 的对应关系更新 mRequestMap
        // 然后从 mRequestMap 中取出图片 URL, 以保证总是使用了匹配 PhotoHolder 实例的最新下载请求 URL
    }
}

