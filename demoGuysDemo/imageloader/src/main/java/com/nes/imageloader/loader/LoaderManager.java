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

package com.nes.imageloader.loader;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liuqz
 */
public class LoaderManager {

    public static final String HTTP = "http";
    public static final String HTTPS = "https";
    public static final String FILE = "file";
    public static final String APP_ICON = "app_icon";

    /**
     * 
     */
    private Map<String, Loader> mLoaderMap = new HashMap<String, Loader>();

    private Loader mNullLoader = new NullLoader();
    /**
     * 
     */
    private static LoaderManager INSTANCE;

    /**
     * 
     */
    private LoaderManager() {
        register(HTTP, new UrlLoader());
        register(HTTPS, new UrlLoader());
        register(FILE, new LocalLoader());
        register(APP_ICON,new AppIconLoader());
    }

    /**
     * @return
     */
    public static LoaderManager getInstance() {
        if (INSTANCE == null) {
            synchronized (LoaderManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LoaderManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * @param schema
     * @param loader
     */
    public final synchronized void register(String schema, Loader loader) {
        mLoaderMap.put(schema, loader);
    }

    public Loader getLoader(String schema) {
        if (mLoaderMap.containsKey(schema)) {
            return mLoaderMap.get(schema);
        }
        return mNullLoader;
    }
}
