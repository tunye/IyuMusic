package com.iyuba.music.activity.me;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.view.MaterialRippleLayout;
import com.buaa.ct.core.view.image.CircleImageView;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.pay.BuyIyubiActivity;
import com.iyuba.music.activity.pay.BuyVipActivity;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.util.AppImageUtil;
import com.iyuba.music.widget.dialog.MyMaterialDialog;

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
    public int getLayoutId() {
        return R.layout.vip_center;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        vipSymbol = findViewById(R.id.vip_symbol);
        vipAllapp = findViewById(R.id.vip_allapp);
        vipAd = findViewById(R.id.vip_ad);
        vipHuge = findViewById(R.id.vip_huge);
        vipHighspeed = findViewById(R.id.vip_highspeed);
        vipUpdate = findViewById(R.id.vip_update);

        vipUpdateText = findViewById(R.id.vip_update_text);
        vipIyubi = findViewById(R.id.vip_iyubi);
        vipDeadline = findViewById(R.id.vip_deadline);
        vipName = findViewById(R.id.vip_name);
        toolbarOper = findViewById(R.id.toolbar_oper);

        vipStatus = findViewById(R.id.vip_status);
        vipPhoto = findViewById(R.id.vip_photo);
    }

    @Override
    public void setListener() {
        super.setListener();
        vipSymbol.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
                materialDialog.setPositiveButton(R.string.app_accept, new INoDoubleClick() {
                    @Override
                    public void onClick(View view) {
                        super.onClick(view);
                        materialDialog.dismiss();
                    }
                });
                materialDialog.setTitle(R.string.vip_symbol).setMessage(R.string.vip_symbol_content);
                materialDialog.show();
            }
        });
        vipAllapp.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
                materialDialog.setPositiveButton(R.string.app_accept, new INoDoubleClick() {
                    @Override
                    public void onClick(View view) {
                        super.onClick(view);
                        materialDialog.dismiss();
                    }
                });
                materialDialog.setTitle(R.string.vip_allapp).setMessage(R.string.vip_allapp_content);
                materialDialog.show();
            }
        });
        vipAd.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
                materialDialog.setPositiveButton(R.string.app_accept, new INoDoubleClick() {
                    @Override
                    public void onClick(View view) {
                        super.onClick(view);
                        materialDialog.dismiss();
                    }
                });
                materialDialog.setTitle(R.string.vip_ad).setMessage(R.string.vip_ad_content);
                materialDialog.show();
            }
        });
        vipHuge.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
                materialDialog.setPositiveButton(R.string.app_accept, new INoDoubleClick() {
                    @Override
                    public void onClick(View view) {
                        super.onClick(view);
                        materialDialog.dismiss();
                    }
                });
                materialDialog.setTitle(R.string.vip_huge).setMessage(R.string.vip_huge_content);
                materialDialog.show();
            }
        });
        vipHighspeed.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
                materialDialog.setPositiveButton(R.string.app_accept, new INoDoubleClick() {
                    @Override
                    public void onClick(View view) {
                        super.onClick(view);
                        materialDialog.dismiss();
                    }
                });
                materialDialog.setTitle(R.string.vip_highspeed).setMessage(R.string.vip_highspeed_content);
                materialDialog.show();
            }
        });
        vipUpdate.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                startActivity(new Intent(context, BuyVipActivity.class));
            }
        });
        toolbarOper.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                startActivity(new Intent(context, BuyIyubiActivity.class));
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        toolbarOper.setText(R.string.vip_recharge);
        title.setText(R.string.vip_title);
    }

    @Override
    public void onActivityResumed() {
        userInfo = AccountManager.getInstance().getUserInfo();
        if (userInfo.getVipStatus().equals("1")) {
            vipStatus.setImageResource(R.drawable.vip);
            vipUpdateText.setText(R.string.vip_update);
            vipDeadline.setText(context.getString(R.string.vip_deadline, userInfo.getDeadline()));
        } else {
            vipStatus.setImageResource(R.drawable.unvip);
            vipUpdateText.setText(R.string.vip_buy);
            vipDeadline.setText(context.getString(R.string.vip_undeadline));
        }
        AppImageUtil.loadAvatar(AccountManager.getInstance().getUserId(), vipPhoto);
        vipName.setText(userInfo.getUsername());
        vipIyubi.setText(context.getString(R.string.vip_iyubi, userInfo.getIyubi()));
    }
}
