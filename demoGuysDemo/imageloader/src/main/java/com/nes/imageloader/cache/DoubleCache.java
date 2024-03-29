/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 bboyfeiyu@gmail.com ( Mr.Simple )
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

import android.content.Context;
import android.graphics.Bitmap;

import com.nes.imageloader.request.BitmapRequest;
import com.nes.utils.LogX;


/**
 * 综合缓存,内存和sd卡双缓存
 * 
 *
 * @author liuqz
 * @date 2019/5/16
 */
public class DoubleCache implements BitmapCache {

    private String TAG = DoubleCache.class.getSimpleName();
    /**
     * 
     */
    DiskCache mDiskCache;
    /**
     * 
     */
    MemoryCache mMemoryCache = new MemoryCache();

    public DoubleCache(Context context) {
        mDiskCache = DiskCache.getDiskCache(context);
    }

    @Override
    public Bitmap get(BitmapRequest key) {
        Bitmap value = mMemoryCache.get(key);
        if (value == null) {
            value = mDiskCache.get(key);
            LogX.d(TAG+" get mDiskCache bitmap : "+value);
            saveBitmapIntoMemory(key, value);
        }
        LogX.d(TAG+" get bitmap value : "+value);
        return value;
    }

    private void saveBitmapIntoMemory(BitmapRequest key, Bitmap bitmap) {
        // 如果Value从disk中读取,那么存入内存缓存
        if (bitmap != null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    @Override
    public void put(BitmapRequest key, Bitmap value) {
        mDiskCache.put(key, value);
        mMemoryCache.put(key, value);
    }

    @Override
    public void remove(BitmapRequest key) {
        mDiskCache.remove(key);
        mMemoryCache.remove(key);
    }

    @Override
    public void clean() {
        mMemoryCache.clean();
        mDiskCache.clean();
    }

}
