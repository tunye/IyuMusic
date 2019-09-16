package com.iyuba.music.wxapi;

import android.content.Intent;
import android.view.View;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.util.ThreadUtils;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {
    private IWXAPI api;

    @Override
    public int getLayoutId() {
        return R.layout.pay_result;
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        api = WXAPIFactory.createWXAPI(this, ConstantManager.WXID, false);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void setListener() {
        // no back btn
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            MyMaterialDialog dialog = new MyMaterialDialog(this);
            dialog.setTitle(R.string.app_name);
            switch (resp.errCode) {
                case 0:
                    dialog.setMessage(R.string.pay_detail_success);
                    AccountManager.getInstance().refreshVipStatus();
                    break;
                case -1:
                    dialog.setMessage(R.string.pay_detail_fail);
                    break;
                case -2:
                    dialog.setMessage(R.string.pay_detail_cancel);
                    break;
                default:
                    dialog.setMessage("未知错误");
                    break;
            }
            dialog.setPositiveButton(R.string.app_accept, new INoDoubleClick() {
                @Override
                public void activeClick(View view) {
                    finish();
                }
            });
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        ThreadUtils.postOnUiThreadDelay(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 4000);
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }
}