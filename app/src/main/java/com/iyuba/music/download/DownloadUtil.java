package com.iyuba.music.download;

import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.account.ScoreOperRequest;
import com.iyuba.music.widget.CustomToast;

/**
 * Created by 10202 on 2016/3/7.
 */
public class DownloadUtil {
    public static boolean checkVip() {
        return AccountManager.getInstance().checkUserLogin() && (AccountManager.getInstance().getUserId().equals("46738") || "1".equals(AccountManager.getInstance().getUserInfo().getVipStatus()));
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
                    url.append("http://staticvip.iyuba.cn/sounds/minutes/").append(song);
                    break;
                default:
                    url.append("http://staticvip.iyuba.cn/sounds/voa").append(song);
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
                    url.append("http://static.iyuba.cn/sounds/minutes/").append(song);
                    break;
                default:
                    url.append("http://static.iyuba.cn/sounds/voa").append(song);
                    break;
            }
        }
        return url.toString();
    }

    public static void checkScore(int articleId, final IOperationResult iOperationResult) {
        if (checkVip()) {
            iOperationResult.success(null);
            return;
        }
        ScoreOperRequest.exeRequest(ScoreOperRequest.generateUrl(AccountManager.getInstance().getUserId(), articleId, 40), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                iOperationResult.fail(null);
                CustomToast.getInstance().showToast("网络错误，请稍后尝试");
            }

            @Override
            public void onServerError(String msg) {
                iOperationResult.fail(null);
                CustomToast.getInstance().showToast("积分剩余不足，不能够下载文章了。");
            }

            @Override
            public void response(Object object) {
                BaseApiEntity apiEntity = (BaseApiEntity) object;
                if (apiEntity.getState() == BaseApiEntity.SUCCESS) {
                    CustomToast.getInstance().showToast("积分已经扣除" + Math.abs(Integer.parseInt(apiEntity.getMessage()))
                            + "，现在剩余积分为" + apiEntity.getValue());
                    iOperationResult.success(null);
                } else {
                    CustomToast.getInstance().showToast("积分剩余不足，不能够下载文章了。");
                    iOperationResult.fail(null);
                }
            }
        });
    }
}
