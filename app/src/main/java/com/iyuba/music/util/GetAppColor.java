package com.iyuba.music.util;

import android.content.Context;

import com.buaa.ct.skin.SkinManager;
import com.iyuba.music.R;
import com.iyuba.music.manager.RuntimeManager;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 10202 on 2016/7/19.
 */
public class GetAppColor {
    private List<String> flavorsDef;

    private GetAppColor() {
        flavorsDef = Arrays.asList(RuntimeManager.getContext().getResources().getStringArray(R.array.flavors_def));
    }

    public static GetAppColor getInstance() {
        return SingleInstanceHelper.instance;
    }

    public static int getResource(Context context, String colorName) {
        return context.getResources().getIdentifier(colorName, "color", context.getPackageName());
    }

    public int getAppColor(Context context) {
        int skin = SkinManager.getInstance().getCurrSkin();
        if (skin == 0) {
            return context.getResources().getColor(R.color.skin_app_color);
        } else {
            return context.getResources().getColor(GetAppColor.getResource(context, "skin_app_color_" + flavorsDef.get(skin)));
        }
    }

    public int getAppColorLight(Context context) {
        int skin = SkinManager.getInstance().getCurrSkin();
        if (skin == 0) {
            return context.getResources().getColor(R.color.skin_app_color_light);
        } else {
            return context.getResources().getColor(GetAppColor.getResource(context, "skin_app_color_light_" + flavorsDef.get(skin)));
        }
    }

    private static class SingleInstanceHelper {
        private static GetAppColor instance = new GetAppColor();
    }
}
