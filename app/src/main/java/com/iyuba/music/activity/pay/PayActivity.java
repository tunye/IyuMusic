package com.iyuba.music.activity.pay;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.account.AliPay;
import com.iyuba.music.request.account.WxPay;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.TextAttr;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.Dialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 10202 on 2017/1/9.
 */

public class PayActivity extends BaseActivity {
    //private static final String[] PAY_MONEY = {"0.01", "0.01", "0.01", "0.01", "0.01"};
    private static final String PAY_TYPE = "pay_type";
    private static final String PAY_DETAIL = "pay_detail";
    private static final String PAY_MONEY = "pay_money";
    private static final String PAY_GOODS = "pay_goods";
    private String payDetailString, payMoneyString, payGoods, payType;
    private TextView username, payDetail, payMoney;
    private ImageView wxSelected, baoSelected;
    private View wxView, baoView;
    private View paySure;
    private Dialog waitingDialog;

    private IWXAPI msgApi;
    private Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());

    public static void launch(Activity context, String payDetail, String payMoney, String payGoods, String payType, int requestCode) {
        Intent intent = new Intent(context, PayActivity.class);
        intent.putExtra(PAY_TYPE, payType);
        intent.putExtra(PAY_DETAIL, payDetail);
        intent.putExtra(PAY_MONEY, payMoney);
        intent.putExtra(PAY_GOODS, payGoods);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_detail);
        context = this;
        payType = getIntent().getStringExtra(PAY_TYPE);
        payDetailString = getIntent().getStringExtra(PAY_DETAIL);
        payMoneyString = getIntent().getStringExtra(PAY_MONEY);
        payGoods = getIntent().getStringExtra(PAY_GOODS);
        waitingDialog = new WaitingDialog.Builder(context).create();
        msgApi = WXAPIFactory.createWXAPI(context, ConstantManager.WXID, false);
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        username = (TextView) findViewById(R.id.pay_detail_user);
        payDetail = (TextView) findViewById(R.id.pay_detail_buy);
        payMoney = (TextView) findViewById(R.id.pay_detail_money);
        wxSelected = (ImageView) findViewById(R.id.pay_detail_wx_checked);
        baoSelected = (ImageView) findViewById(R.id.pay_detail_bao_checked);
        wxView = findViewById(R.id.pay_detail_wx);
        baoView = findViewById(R.id.pay_detail_bao);
        paySure = findViewById(R.id.pay_detail_sure);
    }

    @Override
    protected void setListener() {
        super.setListener();
        wxView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWxSelect();
            }
        });
        baoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBaoSelect();
            }
        });
        paySure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wxSelected.getVisibility() == View.VISIBLE) {
                    if (msgApi.isWXAppInstalled()) {
                        wechatPay();
                    } else {
                        CustomToast.INSTANCE.showToast(R.string.pay_detail_no_wechat);
                    }
                } else {
                    aliPay();
                }
            }
        });
    }

    private void showWxSelect() {
        wxSelected.setVisibility(View.VISIBLE);
        wxSelected.setColorFilter(GetAppColor.instance.getAppColor(context));
        baoSelected.setVisibility(View.GONE);
    }

    private void showBaoSelect() {
        wxSelected.setVisibility(View.GONE);
        baoSelected.setVisibility(View.VISIBLE);
        baoSelected.setColorFilter(GetAppColor.instance.getAppColor(context));
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.pay_detail_title);
        username.setText(AccountManager.instance.getUserName());
        payDetail.setText(payDetailString);
        payMoney.setText(getString(R.string.pay_detail_money_content, payMoneyString));
        showBaoSelect();
    }

    private void wechatPay() {
        waitingDialog.show();
        WxPay.getInstance().exeRequest(WxPay.getInstance().generateUrl(payMoneyString, payGoods, payType), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.INSTANCE.showToast(R.string.pay_detail_generate_failed);
                waitingDialog.dismiss();
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.INSTANCE.showToast(R.string.pay_detail_generate_failed);
                waitingDialog.dismiss();
            }

            @Override
            public void response(Object object) {
                waitingDialog.dismiss();
                BaseApiEntity result = (BaseApiEntity) object;
                if (result.getState().equals(BaseApiEntity.State.SUCCESS)) {
                    PayReq req = (PayReq) result.getData();
                    msgApi.sendReq(req);
                } else {
                    CustomToast.INSTANCE.showToast(R.string.pay_detail_generate_failed);
                }
            }
        });
    }

    private void aliPay() {
        waitingDialog.show();
        String subject = TextAttr.encode(payDetail.getText().toString());
        String body = TextAttr.encode(payMoney.getText().toString());
        AliPay.getInstance().exeRequest(AliPay.getInstance().generateUrl(subject, body, payMoneyString, payGoods, payType), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.INSTANCE.showToast(R.string.pay_detail_generate_failed);
                waitingDialog.dismiss();
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.INSTANCE.showToast(R.string.pay_detail_generate_failed);
                waitingDialog.dismiss();
            }

            @Override
            public void response(Object object) {
                BaseApiEntity baseApiEntity = (BaseApiEntity) object;
                waitingDialog.dismiss();
                if (baseApiEntity.getState().equals(BaseApiEntity.State.SUCCESS)) {
                    final String payInfo = baseApiEntity.getData() + "&sign=\"" + baseApiEntity.getValue()
                            + "\"&" + "sign_type=\"RSA\"";
                    Runnable payRunnable = new Runnable() {

                        @Override
                        public void run() {
                            // 构造PayTask 对象
                            PayTask alipay = new PayTask(PayActivity.this);
                            // 调用支付接口，获取支付结果
                            String result = alipay.pay(payInfo, true);
                            Message msg = new Message();
                            msg.what = 0;
                            msg.obj = result;
                            handler.sendMessage(msg);
                        }
                    };
                    // 必须异步调用
                    Thread payThread = new Thread(payRunnable);
                    payThread.start();
                } else {
                    CustomToast.INSTANCE.showToast(R.string.pay_detail_generate_failed);
                }
            }
        });
    }


    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<PayActivity> {
        @Override
        public void handleMessageByRef(final PayActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    PayResult payResult = new PayResult((String) msg.obj);
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        UserInfo userInfo = AccountManager.instance.getUserInfo();
                        userInfo.setVipStatus("1");
                        AccountManager.instance.setUserInfo(userInfo);
                        final MaterialDialog dialog = new MaterialDialog(activity.context);
                        dialog.setTitle(R.string.app_name).setMessage(R.string.pay_detail_success);
                        dialog.setPositiveButton(R.string.app_accept, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                activity.setResult(RESULT_OK);
                                activity.finish();
                            }
                        });
                        dialog.show();
                    } else {
                        // 判断resultStatus 为非“9000”则代表可能支付失败
                        // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，
                        // 最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            CustomToast.INSTANCE.showToast("支付结果确认中");
                        } else if (TextUtils.equals(resultStatus, "6001")) {
                            CustomToast.INSTANCE.showToast(R.string.pay_detail_cancel);
                        } else if (TextUtils.equals(resultStatus, "6002")) {
                            CustomToast.INSTANCE.showToast("网络连接出错");
                        } else {
                            // 其他值就可以判断为支付失败，或者系统返回的错误
                            CustomToast.INSTANCE.showToast(R.string.pay_detail_fail);
                        }
                    }
                    break;
            }
        }
    }
}
