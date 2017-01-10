package com.iyuba.music.activity.pay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.WebViewActivity;
import com.iyuba.music.adapter.MaterialDialogAdapter;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.entity.user.UserInfoOp;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.account.PayForAppRequest;
import com.iyuba.music.request.account.PayRequest;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.iyuba.music.widget.recycleview.MyLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 10202 on 2015/12/29.
 */
public class BuyVipActivity extends BaseActivity {
    private final static int ONLINE_PAY_CODE = 101;
    private final static int[] price = {200, 600, 1000, 2000, 1100};
    private final static int[] priceMonth = {1, 3, 6, 12, 0};
    private int payType;
    private UserInfo userInfo;
    private MaterialRippleLayout month, threeMonth, halfYear, year, app, payWay;
    private TextView vipUpdateText, vipIyubi, vipDeadline, vipName;
    private ImageView vipStatus;
    private CircleImageView vipPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_vip);
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
        month = (MaterialRippleLayout) findViewById(R.id.vip_month);
        threeMonth = (MaterialRippleLayout) findViewById(R.id.vip_three_month);
        halfYear = (MaterialRippleLayout) findViewById(R.id.vip_half_year);
        year = (MaterialRippleLayout) findViewById(R.id.vip_year);
        app = (MaterialRippleLayout) findViewById(R.id.vip_app);
        payWay = (MaterialRippleLayout) findViewById(R.id.vip_buy_way);

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
        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(0);
            }
        });
        threeMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(1);
            }
        });
        halfYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(2);
            }
        });
        year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(3);
            }
        });
        app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(4);
            }
        });
        payWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payWayDialog();
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
        if (payType == 1) {
            payByIyubiDialog(type);
        } else {
            PayActivity.launch(this, type, ONLINE_PAY_CODE);
        }
    }

    private void payByIyubiDialog(final int pos) {
        final MaterialDialog materialDialog = new MaterialDialog(context);
        materialDialog.setTitle(R.string.vip_title).setMessage(context.getString(R.string.vip_buy_alert, price[pos]));
        materialDialog.setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.setPositiveButton(R.string.app_buy, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
                buy(pos);
            }
        });
        materialDialog.show();
    }

    private void payWayDialog() {
        final MaterialDialog materialDialog = new MaterialDialog(context);
        materialDialog.setTitle(R.string.vip_buy_way);
        View root = View.inflate(context, R.layout.recycleview, null);
        RecyclerView payWayList = (RecyclerView) root.findViewById(R.id.listview);
        List<String> payWays = new ArrayList<>();
        payWays.add(context.getString(R.string.vip_buy_money));
        payWays.add(context.getString(R.string.vip_buy_iyubi));
        MaterialDialogAdapter adapter = new MaterialDialogAdapter(context, payWays);
        adapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                payType = position;
                materialDialog.dismiss();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        adapter.setSelected(0);
        payWayList.setAdapter(adapter);
        payWayList.setLayoutManager(new MyLinearLayoutManager(context));
        payWayList.addItemDecoration(new DividerItemDecoration());
        materialDialog.setContentView(root);
        materialDialog.setPositiveButton(R.string.app_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.vip_title);
        toolbarOper.setText(R.string.vip_recharge);
    }

    private void changeUIResumeByPara() {
        ImageUtil.loadAvatar(AccountManager.instance.getUserId(), vipPhoto);
        if (userInfo.getVipStatus().equals("1")) {
            vipStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.vip));
            vipUpdateText.setText(R.string.vip_update);
            vipDeadline.setText(context.getString(R.string.vip_deadline, userInfo.getDeadline()));
        } else {
            vipStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.unvip));
            vipUpdateText.setText(R.string.vip_buy);
            vipDeadline.setText(context.getString(R.string.vip_undeadline));
        }
        vipName.setText(userInfo.getUsername());
        vipIyubi.setText(context.getString(R.string.vip_iyubi, userInfo.getIyubi()));
    }

    private void buy(int type) {
        if (Integer.parseInt(AccountManager.instance.getUserInfo().getIyubi()) < price[type]) {
            final MaterialDialog materialDialog = new MaterialDialog(context);
            materialDialog.setTitle(R.string.vip_huge).setMessage(R.string.vip_iyubi_not_enough);
            materialDialog.setPositiveButton(R.string.app_buy, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recharge();
                    materialDialog.dismiss();
                }
            });
            materialDialog.setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    materialDialog.dismiss();
                }
            });
            materialDialog.show();
        } else if (type != 4) {
            PayRequest.getInstance().exeRequest(PayRequest.getInstance().generateUrl(new String[]
                    {AccountManager.instance.getUserId(), String.valueOf(price[type]),
                            String.valueOf(priceMonth[type])}), new IProtocolResponse() {
                @Override
                public void onNetError(String msg) {
                    CustomToast.INSTANCE.showToast(msg);
                }

                @Override
                public void onServerError(String msg) {
                    CustomToast.INSTANCE.showToast(msg);
                }

                @Override
                public void response(Object object) {
                    BaseApiEntity apiEntity = (BaseApiEntity) object;
                    if (apiEntity.getState().equals(BaseApiEntity.State.SUCCESS)) {
                        userInfo = (UserInfo) apiEntity.getData();
                        new UserInfoOp().saveData(userInfo);
                        AccountManager.instance.setUserInfo(userInfo);
                        CustomToast.INSTANCE.showToast(context.getString(R.string.vip_buy_success,
                                userInfo.getIyubi()));
                        changeUIResumeByPara();
                    } else {
                        CustomToast.INSTANCE.showToast(context.getString(R.string.vip_buy_fail,
                                apiEntity.getMessage()));
                    }
                }
            });
        } else {
            PayForAppRequest.getInstance().exeRequest(PayForAppRequest.getInstance().generateUrl(new String[]
                    {AccountManager.instance.getUserId(), String.valueOf(price[type])}), new IProtocolResponse() {
                @Override
                public void onNetError(String msg) {
                    CustomToast.INSTANCE.showToast(msg);
                }

                @Override
                public void onServerError(String msg) {
                    CustomToast.INSTANCE.showToast(msg);
                }

                @Override
                public void response(Object object) {
                    BaseApiEntity baseApiEntity = (BaseApiEntity) object;
                    if (baseApiEntity.getState().equals(BaseApiEntity.State.SUCCESS)) {
                        userInfo = (UserInfo) baseApiEntity.getData();
                        new UserInfoOp().saveData(userInfo);
                        AccountManager.instance.setUserInfo(userInfo);
                        CustomToast.INSTANCE.showToast(context.getString(R.string.vip_buy_success,
                                userInfo.getIyubi()));
                        changeUIResumeByPara();
                    } else {
                        CustomToast.INSTANCE.showToast(context.getString(R.string.vip_buy_fail,
                                baseApiEntity.getMessage()));
                    }
                }
            });
        }
    }

    private void recharge() {
        Intent intent = new Intent();
        intent.setClass(context, WebViewActivity.class);
        intent.putExtra("url", "http://app.iyuba.com/wap/index.jsp?uid="
                + AccountManager.instance.getUserId() + "&appid="
                + ConstantManager.instance.getAppId());
        intent.putExtra("title", context.getString(R.string.vip_recharge));
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ONLINE_PAY_CODE && resultCode == RESULT_OK) {              // 刷新vip时长
            AccountManager.instance.getPersonalInfo(null);
        }
    }
}
