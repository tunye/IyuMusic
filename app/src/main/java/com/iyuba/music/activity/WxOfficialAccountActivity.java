package com.iyuba.music.activity;

import android.os.Bundle;
import android.view.View;

import com.iyuba.music.R;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

/**
 * Created by 10202 on 2017/2/21.
 */

public class WxOfficialAccountActivity extends BaseActivity {
    View shareToFriend, shareToCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wx_official_accounts);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        shareToFriend = findViewById(R.id.share_to_friend);
        shareToCircle = findViewById(R.id.share_to_circle);
    }

    @Override
    protected void setListener() {
        super.setListener();
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
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.oper_wx);
    }

    private void share(boolean circleShare) {
        UMImage umImage=new UMImage(context,R.mipmap.ic_launcher);
        umImage.setTargetUrl("http://mp.weixin.qq.com/mp/profile_ext?action=home&__biz=MzA5MjI4NjUwNA==&from=singlemessage#wechat_redirect");
        umImage.setThumb("关注听歌学英语微信公众号");
        umImage.setTitle("关注听歌学英语微信公众号");
        new ShareAction(this).setPlatform(circleShare ? SHARE_MEDIA.WEIXIN_CIRCLE : SHARE_MEDIA.WEIXIN)
                .withText("关注听歌学英语微信公众号")
                .withMedia(umImage)
                .withTargetUrl("http://mp.weixin.qq.com/mp/profile_ext?action=home&__biz=MzA5MjI4NjUwNA==&from=singlemessage#wechat_redirect")
                .share();
    }
}
