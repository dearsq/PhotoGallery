package com.iyounix.photogallery;

import android.os.HandlerThread;
import android.util.Log;


// 这里使用了泛型<T> ,
// ThumbnailDownloader 类的使用者 (比如 PhotoGalleryFragment) 需要使用 T对象来识别每次下载，
// 并确定该使用已下载图片更新哪个UI元素。
public class ThumbnailDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private Boolean mHasQuit = false;

    //存根方法
    public ThumbnailDownloader() {
        super(TAG);
    }

    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }

    //queueThumbnail 方法需要T类型的对象target 和 String的URL链接)
    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got a URL:" + url);
    }
}

