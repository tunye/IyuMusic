package com.iyuba.music.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.WebViewActivity;
import com.iyuba.music.activity.pay.BuyVipActivity;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.util.ImageUtil;

import de.hdodenhof.circleimageview.CircleImageView;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 10202 on 2015/12/28.
 */
public class VipCenterActivity extends BaseActivity {
    private UserInfo userInfo;
    private MaterialRippleLayout vipSymbol, vipAllapp, vipAd, vipHuge, vipHighspeed, vipUpdate;
    private TextView vipUpdateText, vipIyubi, vipDeadline, vipName;
    private ImageView vipStatus;
    private CircleImageView vipPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vip_center);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    public void onResume() {
        super.onResume();
        userInfo = AccountManager.instance.getUserInfo();
        changeUIResumeByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        vipSymbol = (MaterialRippleLayout) findViewById(R.id.vip_symbol);
        vipAllapp = (MaterialRippleLayout) findViewById(R.id.vip_allapp);
        vipAd = (MaterialRippleLayout) findViewById(R.id.vip_ad);
        vipHuge = (MaterialRippleLayout) findViewById(R.id.vip_huge);
        vipHighspeed = (MaterialRippleLayout) findViewById(R.id.vip_highspeed);
        vipUpdate = (MaterialRippleLayout) findViewById(R.id.vip_update);

        vipUpdateText = (TextView) findViewById(R.id.vip_update_text);
        vipIyubi = (TextView) findViewById(R.id.vip_iyubi);
        vipDeadline = (TextView) findViewById(R.id.vip_deadline);
        vipName = (TextView) findViewById(R.id.vip_name);
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);

        vipStatus = (ImageView) findViewById(R.id.vip_status);
        vipPhoto = (CircleImageView) findViewById(R.id.vip_photo);
    }

    @Override
    protected void setListener() {
        super.setListener();
        vipSymbol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog materialDialog = new MaterialDialog(context);
                materialDialog.setPositiveButton(R.string.app_accept, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                    }
                });
                materialDialog.setTitle(R.string.vip_symbol).setMessage(R.string.vip_symbol_content);
                materialDialog.show();
            }
        });
        vipAllapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog materialDialog = new MaterialDialog(context);
                materialDialog.setPositiveButton(R.string.app_accept, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                    }
                });
                materialDialog.setTitle(R.string.vip_allapp).setMessage(R.string.vip_allapp_content);
                materialDialog.show();
            }
        });
        vipAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog materialDialog = new MaterialDialog(context);
                materialDialog.setPositiveButton(R.string.app_accept, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                    }
                });
                materialDialog.setTitle(R.string.vip_ad).setMessage(R.string.vip_ad_content);
                materialDialog.show();
            }
        });
        vipHuge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog materialDialog = new MaterialDialog(context);
                materialDialog.setPositiveButton(R.string.app_accept, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                    }
                });
                materialDialog.setTitle(R.string.vip_huge).setMessage(R.string.vip_huge_content);
                materialDialog.show();
            }
        });
        vipHighspeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog materialDialog = new MaterialDialog(context);
                materialDialog.setPositiveButton(R.string.app_accept, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                    }
                });
                materialDialog.setTitle(R.string.vip_highspeed).setMessage(R.string.vip_highspeed_content);
                materialDialog.show();
            }
        });
        vipUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, BuyVipActivity.class));
            }
        });
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, WebViewActivity.class);
                intent.putExtra("url", "http://app.iyuba.com/wap/index.jsp?uid="
                        + AccountManager.instance.getUserId() + "&appid="
                        + ConstantManager.instance.getAppId());
                intent.putExtra("title", context.getString(R.string.vip_recharge));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        toolbarOper.setText(R.string.vip_recharge);
        title.setText(R.string.vip_title);
    }

    private void changeUIResumeByPara() {
        if (userInfo.getVipStatus().equals("1")) {
            vipStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.vip));
            vipUpdateText.setText(R.string.vip_update);
            vipDeadline.setText(context.getString(R.string.vip_deadline, userInfo.getDeadline()));
        } else {
            vipStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.unvip));
            vipUpdateText.setText(R.string.vip_buy);
            vipDeadline.setText(context.getString(R.string.vip_undeadline));
        }
        ImageUtil.loadAvatar(AccountManager.instance.getUserId(), vipPhoto);
        vipName.setText(userInfo.getUsername());
        vipIyubi.setText(context.getString(R.string.vip_iyubi, userInfo.getIyubi()));
    }
}