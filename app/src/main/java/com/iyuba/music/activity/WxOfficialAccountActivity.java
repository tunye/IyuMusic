package com.iyuba.music.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.util.AddRippleEffect;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.request.apprequest.QunRequest;
import com.iyuba.music.util.ParameterUrl;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.xiaomi.mipush.sdk.MiPushClient;

/**
 * Created by 10202 on 2017/2/21.
 */

public class WxOfficialAccountActivity extends BaseActivity {
    View shareToFriend, shareToCircle, shareToWx;

    @Override
    public int getLayoutId() {
        return R.layout.wx_official_accounts;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        toolbarOper = findViewById(R.id.toolbar_oper);
        shareToFriend = findViewById(R.id.share_to_friend);
        shareToCircle = findViewById(R.id.share_to_circle);
        shareToWx = findViewById(R.id.share_to_wx);
    }

    @Override
    public void setListener() {
        super.setListener();
        title.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("mipush regid", MiPushClient.getRegId(context));
                clipboard.setPrimaryClip(clip);
                CustomToast.getInstance().showToast("regid已经复制，get新技巧");
            }
        });
        toolbarOper.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                String brand = "iyu360";
                if (!android.os.Build.MANUFACTURER.contains("360")) {
                    brand = android.os.Build.MANUFACTURER;
                }
                RequestClient.requestAsync(new QunRequest(brand), new SimpleRequestCallBack<BaseApiEntity<String>>() {
                    @Override
                    public void onSuccess(BaseApiEntity<String> result) {
                        ParameterUrl.joinQQGroup(context, result.getValue());
                    }

                    @Override
                    public void onError(ErrorInfoWrapper errorInfoWrapper) {

                    }
                });
            }
        });
        shareToCircle.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                share(true);
            }
        });
        shareToFriend.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                share(false);
            }
        });
        shareToWx.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("wx official accounts", "iyubasong");
                clipboard.setPrimaryClip(clip);
                CustomToast.getInstance().showToast(R.string.wx_clip_board);
                try {
                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    CustomToast.getInstance().showToast(R.string.share_no_wechat);
                }
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText(R.string.oper_wx);
        enableToolbarOper(R.string.app_qun);
        AddRippleEffect.addRippleEffect(shareToCircle);
        AddRippleEffect.addRippleEffect(shareToWx);
        AddRippleEffect.addRippleEffect(shareToFriend);
    }

    @Override
    public void onBackPressed() {
        if (!mipush) {
            super.onBackPressed();
        } else {
            startActivity(new Intent(context, MainActivity.class));
        }
    }

    private void share(boolean circleShare) {
        UMWeb web = new UMWeb("http://mp.weixin.qq.com/mp/profile_ext?action=home&__biz=MzA5MjI4NjUwNA==&from=singlemessage#wechat_redirect");
        web.setTitle("听歌学英语");//标题
        web.setThumb(new UMImage(context, R.mipmap.ic_launcher));  //缩略图
        web.setDescription("关注听歌学英语微信公众号");//描述

        new ShareAction(this).setPlatform(circleShare ? SHARE_MEDIA.WEIXIN_CIRCLE : SHARE_MEDIA.WEIXIN)
                .withMedia(web).setCallback(new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                CustomToast.getInstance().showToast("请点击分享内容关注听歌学英语公众号");
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                CustomToast.getInstance().showToast("操作取消");
            }
        }).share();
    }
}
