package com.iyuba.music.activity.pay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.WebViewActivity;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.imageview.CircleImageView;
import com.iyuba.music.widget.view.MaterialRippleLayout;

import java.util.Calendar;

/**
 * Created by 10202 on 2015/12/29.
 */
public class BuyIyubiActivity extends BaseActivity {
    private static final String[] PAY_MONEY = {"19.9", "59.9", "99.9", "599", "999"};
    private final static String[] PAY_GOODS = {"210", "650", "1100", "6500", "12000"};
    private static final int ONLINE_PAY_CODE = 101;
    private UserInfo userInfo;
    private MaterialRippleLayout iyubi2h, iyubi6h, iyubi1k, iyubi6k, iyubi1w;
    private TextView vipIyubi, vipDeadline, vipName;
    private ImageView vipStatus;
    private CircleImageView vipPhoto;
    private TextView copyright;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_iyubi);
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    public void onResume() {
        super.onResume();
        userInfo = AccountManager.getInstance().getUserInfo();
        changeUIResumeByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        iyubi2h = findViewById(R.id.iyubi200);
        iyubi6h = findViewById(R.id.iyubi600);
        iyubi1k = findViewById(R.id.iyubi1k);
        iyubi6k = findViewById(R.id.iyubi6k);
        iyubi1w = findViewById(R.id.iyubi1w);

        vipIyubi = findViewById(R.id.iyubi_iyubi);
        vipDeadline = findViewById(R.id.iyubi_deadline);
        vipName = findViewById(R.id.iyubi_name);
        toolbarOper = findViewById(R.id.toolbar_oper);

        vipStatus = findViewById(R.id.iyubi_status);
        vipPhoto = findViewById(R.id.iyubi_photo);

        copyright = findViewById(R.id.copyright);
    }

    @Override
    protected void setListener() {
        super.setListener();
        iyubi2h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(0);
            }
        });
        iyubi6h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(1);
            }
        });
        iyubi1k.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(2);
            }
        });
        iyubi6k.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(3);
            }
        });
        iyubi1w.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(4);
            }
        });
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recharge();
            }
        });
    }

    private void pay(int type) {
        String goodsDetail;
        switch (type) {
            case 0:
                goodsDetail = getString(R.string.iyubi200);
                break;
            case 1:
                goodsDetail = getString(R.string.iyubi600);
                break;
            case 2:
                goodsDetail = getString(R.string.iyubi1k);
                break;
            case 3:
                goodsDetail = getString(R.string.iyubi6k);
                break;
            case 4:
                goodsDetail = getString(R.string.iyubi1w);
                break;
            default:
                goodsDetail = getString(R.string.iyubi200);
                break;
        }
        PayActivity.launch(this, goodsDetail.split("-")[1], PAY_MONEY[type], PAY_GOODS[type], "1", ONLINE_PAY_CODE);
    }


    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.vip_iyubi_title);
        toolbarOper.setText(R.string.vip_iyubi_web);
        copyright.setText(context.getString(R.string.about_company,
                Calendar.getInstance().get(Calendar.YEAR)));
    }

    private void changeUIResumeByPara() {
        ImageUtil.loadAvatar(AccountManager.getInstance().getUserId(), vipPhoto);
        if (userInfo.getVipStatus().equals("1")) {
            vipStatus.setImageResource(R.drawable.vip);
            vipDeadline.setText(context.getString(R.string.vip_deadline, userInfo.getDeadline()));
        } else {
            vipStatus.setImageResource(R.drawable.unvip);
            vipDeadline.setText(context.getString(R.string.vip_undeadline));
        }
        vipName.setText(userInfo.getUsername());
        vipIyubi.setText(context.getString(R.string.vip_iyubi, userInfo.getIyubi()));
    }

    private void recharge() {
        Intent intent = new Intent();
        intent.setClass(context, WebViewActivity.class);
        intent.putExtra("url", "http://app.iyuba.cn/wap/index.jsp?uid="
                + AccountManager.getInstance().getUserId() + "&appid="
                + ConstantManager.appId);
        intent.putExtra("title", context.getString(R.string.vip_recharge));
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ONLINE_PAY_CODE && resultCode == RESULT_OK) {              // 刷新vip时长
            AccountManager.getInstance().refreshVipStatus();
        }
    }
}
