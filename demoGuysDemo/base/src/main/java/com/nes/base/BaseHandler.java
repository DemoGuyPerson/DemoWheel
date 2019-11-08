package com.nes.base;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 *
 * @author liuqz
 * @date 2018/12/15
 */

public abstract class BaseHandler<T> extends Handler {

    private final WeakReference<T> mWeakReference;
    protected abstract void baseHandleMessage(Message message);
    protected T getContent(){
        return mWeakReference.get();
    }

    public boolean isEmptyContent(){
        return mWeakReference.get()==null;
    }

    public BaseHandler(T content){ 
        super();
        mWeakReference = new WeakReference<T>(content);
    }

    public BaseHandler(Looper looper,T content){
        super(looper);
        mWeakReference = new WeakReference<T>(content);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        T content = mWeakReference.get();
        if(content!=null){
            this.baseHandleMessage(msg);
        }
    }

}
