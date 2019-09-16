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
import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.util.GetAppColor;
import com.buaa.ct.core.util.ThreadPoolUtil;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.account.AliPay;
import com.iyuba.music.request.account.WxPay;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.util.Utils;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

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
    private IyubaDialog waitingDialog;

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
    public int getLayoutId() {
        return R.layout.pay_detail;
    }

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        payType = getIntent().getStringExtra(PAY_TYPE);
        payDetailString = getIntent().getStringExtra(PAY_DETAIL);
        payMoneyString = getIntent().getStringExtra(PAY_MONEY);
        payGoods = getIntent().getStringExtra(PAY_GOODS);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        toolbarOper = findViewById(R.id.toolbar_oper);
        username = findViewById(R.id.pay_detail_user);
        payDetail = findViewById(R.id.pay_detail_buy);
        payMoney = findViewById(R.id.pay_detail_money);
        wxSelected = findViewById(R.id.pay_detail_wx_checked);
        baoSelected = findViewById(R.id.pay_detail_bao_checked);
        wxView = findViewById(R.id.pay_detail_wx);
        baoView = findViewById(R.id.pay_detail_bao);
        paySure = findViewById(R.id.pay_detail_sure);
    }

    @Override
    public void setListener() {
        super.setListener();
        wxView.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                showWxSelect();
            }
        });
        baoView.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                showBaoSelect();
            }
        });
        View.OnClickListener payListener = new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                pay();
            }
        };
        toolbarOper.setOnClickListener(payListener);
        paySure.setOnClickListener(payListener);
    }

    private void pay() {
        if (wxSelected.getVisibility() == View.VISIBLE) {
            if (msgApi.isWXAppInstalled()) {
                wechatPay();
            } else {
                CustomToast.getInstance().showToast(R.string.pay_detail_no_wechat);
            }
        } else {
            aliPay();
        }
    }

    private void showWxSelect() {
        wxSelected.setVisibility(View.VISIBLE);
        wxSelected.setColorFilter(GetAppColor.getInstance().getAppColor());
        baoSelected.setVisibility(View.GONE);
    }

    private void showBaoSelect() {
        wxSelected.setVisibility(View.GONE);
        baoSelected.setVisibility(View.VISIBLE);
        baoSelected.setColorFilter(GetAppColor.getInstance().getAppColor());
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText(R.string.pay_detail_title);
        enableToolbarOper(R.string.pay_detail_oper);
        username.setText(AccountManager.getInstance().getUserInfo().getUsername());
        payDetail.setText(payDetailString);
        payMoney.setText(getString(R.string.pay_detail_money_content, payMoneyString));
        waitingDialog = WaitingDialog.create(context, context.getString(R.string.pay_detail_going));
        msgApi = WXAPIFactory.createWXAPI(context, ConstantManager.WXID, false);
        showBaoSelect();
    }

    private void wechatPay() {
        waitingDialog.show();
        RequestClient.requestAsync(new WxPay(payMoneyString, payGoods, payType), new SimpleRequestCallBack<BaseApiEntity<PayReq>>() {
            @Override
            public void onSuccess(BaseApiEntity<PayReq> payReqBaseApiEntity) {
                waitingDialog.dismiss();
                msgApi.sendReq(payReqBaseApiEntity.getData());
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
                waitingDialog.dismiss();
            }
        });
    }

    private void aliPay() {
        waitingDialog.show();
        String subject = ParameterUrl.encode(payDetail.getText().toString());
        String body = ParameterUrl.encode(payMoney.getText().toString());
        RequestClient.requestAsync(new AliPay(subject, body, payMoneyString, payGoods, payType), new SimpleRequestCallBack<BaseApiEntity<String>>() {
            @Override
            public void onSuccess(BaseApiEntity<String> baseApiEntity) {
                waitingDialog.dismiss();
                final String payInfo = baseApiEntity.getData() + "&sign=\"" + baseApiEntity.getValue()
                        + "\"&" + "sign_type=\"RSA\"";
                Runnable payRunnable = new Runnable() {

                    @Override
                    public void run() {
                        // 构造PayTask 对象
                        PayTask alipay = new PayTask(PayActivity.this);
                        // 调用支付接口，获取支付结果
                        String result = alipay.pay(payInfo, true);
                        handler.obtainMessage(0, result).sendToTarget();
                    }
                };
                ThreadPoolUtil.getInstance().execute(payRunnable);
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfo) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfo));
                waitingDialog.dismiss();
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
                        UserInfo userInfo = AccountManager.getInstance().getUserInfo();
                        userInfo.setVipStatus("1");
                        AccountManager.getInstance().setUserInfo(userInfo);
                        final MyMaterialDialog dialog = new MyMaterialDialog(activity.context);
                        dialog.setTitle(R.string.app_name).setMessage(R.string.pay_detail_success);
                        dialog.setPositiveButton(R.string.app_accept, new INoDoubleClick() {
                            @Override
                            public void activeClick(View view) {
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
                            CustomToast.getInstance().showToast("支付结果确认中");
                        } else if (TextUtils.equals(resultStatus, "6001")) {
                            CustomToast.getInstance().showToast(R.string.pay_detail_cancel);
                        } else if (TextUtils.equals(resultStatus, "6002")) {
                            CustomToast.getInstance().showToast("网络连接出错");
                        } else {
                            // 其他值就可以判断为支付失败，或者系统返回的错误
                            CustomToast.getInstance().showToast(R.string.pay_detail_fail);
                        }
                    }
                    break;
            }
        }
    }
}
