package com.iyuba.music.download;

import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;

/**
 * Created by 10202 on 2016/3/7.
 */
public class DownloadService {
    public static boolean checkVip() {

        boolean isvip = AccountManager.getInstance().checkUserLogin();
        if (isvip) {
            isvip = "1".equals(AccountManager.getInstance().getUserInfo().getVipStatus());
        }
        return isvip;
    }

    public static String getAnnouncerUrl(int id, String sound) {
        StringBuilder url = new StringBuilder();
        if (checkVip()) {
            if (id < 1000) {
                url.append(ConstantManager.oldSoundVipUrl).append(sound);
            } else {
                url.append(ConstantManager.vipUrl).append(sound);
            }
        } else {
            if (id < 1000) {
                url.append(ConstantManager.oldSoundUrl).append(sound);
            } else {
                url.append(ConstantManager.songUrl).append(sound);
            }
        }
        return url.toString();
    }

    public static String getSongUrl(String app, String song) {
        StringBuilder url = new StringBuilder();
        if (checkVip()) {
            switch (app) {
                case "209":
                    url.append(ConstantManager.vipUrl).append(song);
                    break;
                case "215":
                case "221":
                case "231":
                    url.append("http://staticvip.iyuba.com/sounds/minutes/").append(song);
                    break;
                default:
                    url.append("http://staticvip.iyuba.com/sounds/voa").append(song);
                    break;
            }
        } else {
            switch (app) {
                case "209":
                    url.append(ConstantManager.songUrl).append(song);
                    break;
                case "215":
                case "221":
                case "231":
                    url.append("http://static.iyuba.com/sounds/minutes/").append(song);
                    break;
                default:
                    url.append("http://static.iyuba.com/sounds/voa").append(song);
                    break;
            }
        }
        return url.toString();
    }
}
