package com.iyuba.music.request;

import com.buaa.ct.core.okhttp.RequestBase;

public class Request<T> extends RequestBase<T> {
    public String url;

    @Override
    public String getUrl() {
        return url;
    }

}