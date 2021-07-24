package com.iyuba.music.wxapi;

import android.content.Intent;

import com.buaa.ct.core.util.ThreadUtils;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
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
            switch (resp.errCode) {
                case 0:
                    CustomToast.getInstance().showToast(R.string.pay_detail_success, CustomToast.LENGTH_LONG);
                    AccountManager.getInstance().refreshVipStatus();
                    break;
                case -1:
                    CustomToast.getInstance().showToast(R.string.pay_detail_fail, CustomToast.LENGTH_LONG);
                    break;
                case -2:
                    CustomToast.getInstance().showToast(R.string.pay_detail_cancel, CustomToast.LENGTH_LONG);
                    break;
                default:
                    CustomToast.getInstance().showToast("未知错误", CustomToast.LENGTH_LONG);
                    break;
            }
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