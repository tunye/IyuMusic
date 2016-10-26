package com.iyuba.music.request.apprequest;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

/**
 * Created by 10202 on 2015/11/20.
 */
public class UpdateRequest {

    private static UpdateRequest instance;
    private final String updateUrl = "http://api.iyuba.com/mobile/android/iyumusic/islatestn.plain";


    public UpdateRequest() {
    }

    public static synchronized UpdateRequest getInstance() {
        if (instance == null) {
            instance = new UpdateRequest();
        }
        return instance;
    }

    public void exeRequest(final String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            StringRequest request = new StringRequest(url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String message) {
                            String[] content = message.split(",");
                            BaseApiEntity apiEntity = new BaseApiEntity();
                            if (content[0].equals("NO")) {
                                apiEntity.setState(BaseApiEntity.State.SUCCESS);
                                apiEntity.setValue(content[2].replace("||", "@@@"));
                            } else {
                                apiEntity.setState(BaseApiEntity.State.FAIL);
                            }
                            response.response(apiEntity);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    response.onServerError(VolleyErrorHelper.getMessage(error));
                }
            });
            MyVolley.getInstance().addToRequestQueue(request);
        } else {
            response.onNetError(RuntimeManager.getString(R.string.no_internet));
        }
    }

    public String generateUrl(int version) {
        return ParameterUrl.setRequestParameter(updateUrl, "currver", version);
    }
}
