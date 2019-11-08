/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 bboyfeiyu@gmail.com, Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.nes.imageloader.request;

import android.text.TextUtils;
import android.widget.ImageView;


import androidx.annotation.NonNull;

import com.nes.imageloader.config.DisplayConfig;
import com.nes.imageloader.core.SimpleImageLoader;
import com.nes.imageloader.loader.LoaderManager;
import com.nes.imageloader.policy.LoadPolicy;
import com.nes.imageloader.utils.ImageViewHelper;
import com.nes.imageloader.utils.Md5Helper;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * 网络请求类. 注意GET和DELETE不能传递参数,因为其请求的性质所致,用户可以将参数构建到url后传递进来到Request中.
 * 
 *
 * @author liuqz
 * @date 2019/5/16
 */
public class BitmapRequest implements Comparable<BitmapRequest> {

    public static final int IMAGE_TAG_KEY = 154863423;
    /**
     * 
     */
    private Reference<ImageView> mImageViewRef;
    public DisplayConfig displayConfig;
    public SimpleImageLoader.ImageListener imageListener;
    public String imageUri = "";
    public String imageUriMd5 = "";
    /**
     * 请求序列号
     */
    public int serialNum = 0;
    /**
     * 是否取消该请求
     */
    public boolean isCancel = false;

    /**
     * 
     */
    public boolean justCacheInMem = false;

    /**
     * 加载策略
     */
    LoadPolicy mLoadPolicy = SimpleImageLoader.getInstance().getConfig().loadPolicy;

    /**
     * @param imageView
     * @param uri
     * @param config
     * @param listener
     */
    public BitmapRequest(ImageView imageView, String uri, DisplayConfig config,
            SimpleImageLoader.ImageListener listener) {
        if (imageView != null) {
            mImageViewRef = new WeakReference<ImageView>(imageView);
            imageView.setTag(IMAGE_TAG_KEY,uri);
        }
        displayConfig = config;
        imageListener = listener;
        imageUri = uri;
        imageUriMd5 = Md5Helper.toMD5(imageUri);
    }


    public boolean isAppIconType(){
        boolean result = false;
        if (!TextUtils.isEmpty(imageUri) && imageUri.startsWith(LoaderManager.APP_ICON)){
            result = true;
        }
        return result;
    }

    /**
     * @param policy
     */
    public void setLoadPolicy(LoadPolicy policy) {
        if (policy != null) {
            mLoadPolicy = policy;
        }
    }

    /**
     * 判断imageview的tag与uri是否相等
     * 
     * @return
     */
    public boolean isImageViewTagValid() {
        boolean result = false;
        ImageView imageView = mImageViewRef.get();
        if (imageView != null && imageView.getTag(BitmapRequest.IMAGE_TAG_KEY)!=null){
             if (imageView.getTag(BitmapRequest.IMAGE_TAG_KEY).equals(imageUri)){
                 result = true;
             }
        }
        return result;
    }

    public ImageView getImageView() {
        return mImageViewRef.get();
    }

    public int getImageViewWidth() {
        return ImageViewHelper.getImageViewWidth(mImageViewRef.get());
    }

    public int getImageViewHeight() {
        return ImageViewHelper.getImageViewHeight(mImageViewRef.get());
    }

    @Override
    public int compareTo(@NonNull BitmapRequest another) {
        return mLoadPolicy.compare(this, another);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((imageUri == null) ? 0 : imageUri.hashCode());
        result = prime * result + ((mImageViewRef == null) ? 0 : mImageViewRef.get().hashCode());
        result = prime * result + serialNum;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BitmapRequest other = (BitmapRequest) obj;
        if (imageUri == null) {
            if (other.imageUri != null)
                return false;
        } else if (!imageUri.equals(other.imageUri))
            return false;
        if (mImageViewRef == null) {
            if (other.mImageViewRef != null)
                return false;
        } else if (!mImageViewRef.get().equals(other.mImageViewRef.get()))
            return false;
        if (serialNum != other.serialNum)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "BitmapRequest{" +
                "imageUri='" + imageUri + '\'' +
                '}';
    }
}
