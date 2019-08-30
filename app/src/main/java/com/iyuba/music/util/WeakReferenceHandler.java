package com.iyuba.music.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

public class WeakReferenceHandler<T> extends Handler {
    private final WeakReference<T> mWeakReference;
    private final IHandlerMessageByRef<T> mHandlerByRef;

    public WeakReferenceHandler(Looper looper, T t, IHandlerMessageByRef<T> handlerByRef) {
        super(looper);
        mWeakReference = new WeakReference<>(t);
        mHandlerByRef = handlerByRef;
    }

    public WeakReferenceHandler(T t, IHandlerMessageByRef<T> handlerByRef) {
        mWeakReference = new WeakReference<>(t);
        mHandlerByRef = handlerByRef;
    }

    @Override
    public void handleMessage(Message msg) {
        if (mWeakReference.get() != null) {
            mHandlerByRef.handleMessageByRef(mWeakReference.get(), msg);
        }
    }

    public interface IHandlerMessageByRef<T> {
        void handleMessageByRef(T t, Message msg);
    }
}

/*
    替换Handler模板：
    private Handler mHandler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<TTTTTT> {
        @Override
        public void handleMessageByRef(TTTTTT ttttt, Message msg) {

        }
    }
*/