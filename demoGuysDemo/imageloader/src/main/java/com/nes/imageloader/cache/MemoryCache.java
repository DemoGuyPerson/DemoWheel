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

package com.nes.imageloader.cache;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.nes.imageloader.request.BitmapRequest;


/**
 * 图片的内存缓存,key为图片的uri,值为图片本身
 * 
 *
 * @author liuqz
 * @date 2019/5/16
 */
public class MemoryCache implements BitmapCache {

    private LruCache<String, Bitmap> mMemeryCache;

    public MemoryCache() {

        // 计算可使用的最大内存
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // 取4分之一的可用内存作为缓存
        final int cacheSize = maxMemory / 4;
        mMemeryCache = new LruCache<String, Bitmap>(cacheSize) {
            /**
             * 这个方法从源码中看出来是设置已用缓存的计算方式的
             * 默认返回的值是 1，也就是每缓存一张图片就将已用缓存大小加 1
             * 缓存图片看的是占用的内存的大小，每张图片的占用内存也是不一样的
             * 因此要重写这个方法，手动将这里改为本次缓存的图片的大小
             */
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                bitmap.getByteCount();
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
            }
        };

    }

    @Override
    public Bitmap get(BitmapRequest key) {
        return mMemeryCache.get(key.imageUri);
    }

    @Override
    public void put(BitmapRequest key, Bitmap value) {
        mMemeryCache.put(key.imageUri, value);
    }

    @Override
    public void remove(BitmapRequest key) {
        mMemeryCache.remove(key.imageUri);
    }

    @Override
    public void clean() {

    }

}
