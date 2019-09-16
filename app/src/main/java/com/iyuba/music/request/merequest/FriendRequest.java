package com.iyuba.music.request.merequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.buaa.ct.core.manager.RuntimeManager;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.friends.Fans;
import com.iyuba.music.entity.friends.Follows;
import com.iyuba.music.entity.friends.Friend;
import com.iyuba.music.entity.friends.RecommendFriend;
import com.iyuba.music.entity.friends.SearchFriend;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10202 on 2015/9/30.
 */
public class FriendRequest extends Request<BaseListEntity<List<Friend>>> {
    public static final String FAN_REQUEST_CODE = "51002";
    public static final String FOLLOW_REQUEST_CODE = "51001";
    public static final String RECOMMEND_REQUEST_CODE = "52003";
    public static final String SEARCH_REQUEST_CODE = "52001";
    private static final String FAN_SUCCESS_CODE = "560";
    private static final String FOLLOW_SUCCESS_CODE = "550";
    private static final String RECOMMEND_SUCCESS_CODE = "591";
    private static final String RECOMMEND_ALL_DATA_CODE = "592";
    private static final String SEARCH_SUCCESS_CODE = "591";

    private String protocol;

    public FriendRequest(String uid, String protocol, String content, int page) {
        this.protocol = protocol;
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", protocol);
        para.put("uid", uid);
        if (protocol.equals(SEARCH_REQUEST_CODE)) {
            para.put("search", ParameterUrl.encode(content));
        }
        para.put("format", "json");
        para.put("pageNumber", page);
        para.put("pageCounts", 20);
        para.put("sign", MD5.getMD5ofStr(51002 + uid + "iyubaV2"));
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public BaseListEntity<List<Friend>> parseJsonImpl(JSONObject jsonObject) {
        String resultCode = jsonObject.getString("result");
        if (getSuccessCode().equals(resultCode)) {
            BaseListEntity<List<Friend>> baseListEntity = new BaseListEntity<>();
            List<? extends Friend> list = JSON.parseArray(jsonObject.getString("data"), getDataClass());
            baseListEntity.setTotalCount(jsonObject.getInteger("num"));
            baseListEntity.setTotalPage(baseListEntity.getTotalCount() / 20 + (baseListEntity.getTotalCount() % 20 == 0 ? 0 : 1));
            if (list.size() == 0) {
                baseListEntity.setIsLastPage(true);
            } else {
                baseListEntity.setIsLastPage(false);
                for (Friend friend : list) {
                    friend.setDoing(ParameterUrl.decode(friend.getDoing()));
                }
                baseListEntity.setData(new ArrayList<>(list));
            }
            if (protocol.equals(RECOMMEND_REQUEST_CODE) && resultCode.equals(RECOMMEND_ALL_DATA_CODE)) {
                baseListEntity.setIsLastPage(true);
            }
            if (protocol.equals(SEARCH_REQUEST_CODE)) {
                baseListEntity.setTotalPage(jsonObject.getInteger("lastPage"));
                baseListEntity.setIsLastPage(jsonObject.getIntValue("nextPage") != jsonObject.getIntValue("lastPage"));
            }
            return baseListEntity;
        } else {
            return null;
        }
    }

    @Override
    public String getDataErrorMsg() {
        return RuntimeManager.getInstance().getString(R.string.friend_find_error);
    }

    public String getSuccessCode() {
        switch (protocol) {
            default:
            case FAN_REQUEST_CODE:
                return FAN_SUCCESS_CODE;
            case FOLLOW_REQUEST_CODE:
                return FOLLOW_SUCCESS_CODE;
            case RECOMMEND_REQUEST_CODE:
                return RECOMMEND_SUCCESS_CODE;
            case SEARCH_REQUEST_CODE:
                return SEARCH_SUCCESS_CODE;
        }
    }

    public Class<? extends Friend> getDataClass() {
        switch (protocol) {
            default:
            case FAN_REQUEST_CODE:
                return Fans.class;
            case FOLLOW_REQUEST_CODE:
                return Follows.class;
            case RECOMMEND_REQUEST_CODE:
                return RecommendFriend.class;
            case SEARCH_REQUEST_CODE:
                return SearchFriend.class;
        }
    }
}
