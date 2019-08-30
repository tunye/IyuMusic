package com.iyuba.music.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.iyuba.music.widget.CustomToast;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by 10202 on 2015/10/8.
 */
public class ParameterUrl {
    public static String setRequestParameter(String url, Map<String, Object> para) {
        StringBuilder requestURLTemp = new StringBuilder(url);
        Iterator iterator = para.entrySet().iterator();
        HashMap.Entry entry;
        requestURLTemp.append("?");
        while (iterator.hasNext()) {
            entry = (HashMap.Entry) iterator.next();
            requestURLTemp.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        return requestURLTemp.substring(0, requestURLTemp.length() - 1);
    }

    public static String setRequestParameter(String url, Object key, Object value) {
        return url + "?" + key + "=" + value;
    }

    public static void joinQQGroup(Context context, String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            CustomToast.getInstance().showToast("您的qq尚未安装或者版本过低");
        }
    }

    public static String encode(String content) {
        try {
            if (TextUtils.isEmpty(content)) {
                return content;
            } else {
                return URLEncoder.encode(content, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decode(String content) {
        try {
            return URLDecoder.decode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String captureName(String name) {
        char[] cs = name.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }
}
