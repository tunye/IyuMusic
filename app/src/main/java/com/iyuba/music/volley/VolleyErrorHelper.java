package com.iyuba.music.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyuba.music.R;
import com.iyuba.music.manager.RuntimeManager;

import java.util.HashMap;

/**
 * Created by 10202 on 2015/10/21.
 */
public class VolleyErrorHelper {
    public static String getMessage(Object error) {

        if (error instanceof TimeoutError) {
            return RuntimeManager.getString(R.string.generic_server_down);
        } else if (isServerProblem(error)) {
            return handleServerError(error);
        } else if (isNetworkProblem(error)) {
            return RuntimeManager.getString(R.string.no_internet);
        }
        return RuntimeManager.getString(R.string.generic_error);
    }


    private static boolean isNetworkProblem(Object error) {
        return (error instanceof NetworkError) || (error instanceof NoConnectionError);
    }

    private static boolean isServerProblem(Object error) {
        return (error instanceof ServerError) || (error instanceof AuthFailureError);
    }

    private static String handleServerError(Object err) {
        VolleyError error = (VolleyError) err;

        NetworkResponse response = error.networkResponse;

        if (response != null) {
            switch (response.statusCode) {
                case 404:
                case 422:
                case 401:
                    try {
                        // server might return error like this { "error": "Some error occured" }
                        // Use "Gson" to parse the result
                        HashMap<String, String> result = new Gson().fromJson(new String(response.data),
                                new TypeToken<HashMap<String, String>>() {
                                }.getType());

                        if (result != null && result.containsKey("error")) {
                            return result.get("error").toString();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return error.getMessage();
                default:
                    return RuntimeManager.getString(R.string.generic_server_down);
            }
        }
        return RuntimeManager.getString(R.string.generic_error);
    }
}