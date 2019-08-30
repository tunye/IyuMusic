package com.iyuba.music.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.request.apprequest.QunRequest;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.view.AddRippleEffect;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wx_official_accounts);
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = findViewById(R.id.toolbar_oper);
        shareToFriend = findViewById(R.id.share_to_friend);
        shareToCircle = findViewById(R.id.share_to_circle);
        shareToWx = findViewById(R.id.share_to_wx);
    }

    @Override
    protected void setListener() {
        super.setListener();
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("mipush regid", MiPushClient.getRegId(context));
                clipboard.setPrimaryClip(clip);
                CustomToast.getInstance().showToast("regid已经复制，get新技巧");
            }
        });
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String brand = "iyu360";
                if (!android.os.Build.MANUFACTURER.contains("360")) {
                    brand = android.os.Build.MANUFACTURER;
                }
                QunRequest.exeRequest(QunRequest.generateUrl(brand), new IProtocolResponse() {
                    @Override
                    public void onNetError(String msg) {

                    }

                    @Override
                    public void onServerError(String msg) {

                    }

                    @Override
                    public void response(Object object) {
                        BaseApiEntity result = (BaseApiEntity) object;
                        ParameterUrl.joinQQGroup(context, result.getValue());
                    }
                });
            }
        });
        shareToCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(true);
            }
        });
        shareToFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(false);
            }
        });
        shareToWx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.oper_wx);
        toolbarOper.setText(R.string.app_qun);
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
