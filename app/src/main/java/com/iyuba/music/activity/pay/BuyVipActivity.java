package com.iyuba.music.activity.pay;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.view.CustomToast;
import com.buaa.ct.core.view.MaterialRippleLayout;
import com.buaa.ct.core.view.image.CircleImageView;
import com.buaa.ct.core.view.image.DividerItemDecoration;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.adapter.MaterialDialogAdapter;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.entity.user.UserInfoOp;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.request.account.PayForAppRequest;
import com.iyuba.music.request.account.PayRequest;
import com.iyuba.music.util.AppImageUtil;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.recycleview.MyLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10202 on 2015/12/29.
 */
public class BuyVipActivity extends BaseActivity {
    private final static int ONLINE_PAY_CODE = 101;
    private final static int[] PAY_GOODS = {200, 600, 1000, 2000, 1100};
    private final static int[] PAY_MONTH = {1, 3, 6, 12, 0};
    private static final String[] PAY_MONEY = {"19.9", "59.9", "99.9", "199", "99.9"};
    private int payType;
    private UserInfo userInfo;
    private MaterialRippleLayout month, threeMonth, halfYear, year, app, payWay;
    private TextView vipUpdateText, vipIyubi, vipDeadline, vipName;
    private ImageView vipStatus;
    private CircleImageView vipPhoto;

    @Override
    public int getLayoutId() {
        return R.layout.buy_vip;
    }

    @Override
    public void onResume() {
        userInfo = AccountManager.getInstance().getUserInfo();
        super.onResume();
    }

    @Override
    public void initWidget() {
        super.initWidget();
        month = findViewById(R.id.vip_month);
        threeMonth = findViewById(R.id.vip_three_month);
        halfYear = findViewById(R.id.vip_half_year);
        year = findViewById(R.id.vip_year);
        app = findViewById(R.id.vip_app);
        payWay = findViewById(R.id.vip_buy_way);

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
        month.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                pay(0);
            }
        });
        threeMonth.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                pay(1);
            }
        });
        halfYear.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                pay(2);
            }
        });
        year.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                pay(3);
            }
        });
        app.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                pay(4);
            }
        });
        payWay.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                payWayDialog();
            }
        });
        toolbarOper.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                startActivity(new Intent(context, BuyIyubiActivity.class));
            }
        });
    }

    private void pay(int type) {
        if (payType == 1) {
            payByIyubiDialog(type);
        } else {
            String goodsDetail;
            switch (type) {
                case 0:
                    goodsDetail = getString(R.string.vip_month);
                    break;
                case 1:
                    goodsDetail = getString(R.string.vip_three_month);
                    break;
                case 2:
                    goodsDetail = getString(R.string.vip_half_year);
                    break;
                case 3:
                    goodsDetail = getString(R.string.vip_year);
                    break;
                case 4:
                    goodsDetail = getString(R.string.vip_app);
                    break;
                default:
                    goodsDetail = getString(R.string.vip_month);
                    break;
            }
            PayActivity.launch(this, goodsDetail.split("-")[0], PAY_MONEY[type],
                    String.valueOf(PAY_MONTH[type]), getProductId(type), ONLINE_PAY_CODE);
        }
    }

    @NonNull
    private String getProductId(int type) {
        String productId = "0";
        switch (type) {
            case 4:
                productId = "10";
                break;
            default:
                break;
        }
        return productId;
    }

    private void payByIyubiDialog(final int pos) {
        final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
        materialDialog.setTitle(R.string.vip_title).setMessage(context.getString(R.string.vip_buy_alert, PAY_GOODS[pos]));
        materialDialog.setNegativeButton(R.string.app_cancel, new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                materialDialog.dismiss();
            }
        });
        materialDialog.setPositiveButton(R.string.app_buy, new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                materialDialog.dismiss();
                buy(pos);
            }
        });
        materialDialog.show();
    }

    private void payWayDialog() {
        final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
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
        });
        adapter.setSelected(payType);
        payWayList.setAdapter(adapter);
        payWayList.setLayoutManager(new MyLinearLayoutManager(context));
        payWayList.addItemDecoration(new DividerItemDecoration());
        materialDialog.setContentView(root);
        materialDialog.setPositiveButton(R.string.app_cancel, new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText(R.string.vip_title);
        enableToolbarOper(R.string.vip_recharge);
    }

    @Override
    public void onActivityResumed() {
        super.onActivityResumed();
        AppImageUtil.loadAvatar(AccountManager.getInstance().getUserId(), vipPhoto);
        if (userInfo.getVipStatus().equals("1")) {
            vipStatus.setImageResource(R.drawable.vip);
            vipUpdateText.setText(R.string.vip_update);
            vipDeadline.setText(context.getString(R.string.vip_deadline, userInfo.getDeadline()));
        } else {
            vipStatus.setImageResource(R.drawable.unvip);
            vipUpdateText.setText(R.string.vip_buy);
            vipDeadline.setText(context.getString(R.string.vip_undeadline));
        }
        vipName.setText(userInfo.getUsername());
        vipIyubi.setText(context.getString(R.string.vip_iyubi, userInfo.getIyubi()));
    }

    private void buy(int type) {
        if (Integer.parseInt(AccountManager.getInstance().getUserInfo().getIyubi()) < PAY_GOODS[type]) {
            final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
            materialDialog.setTitle(R.string.vip_huge).setMessage(R.string.vip_iyubi_not_enough);
            materialDialog.setPositiveButton(R.string.app_buy, new INoDoubleClick() {
                @Override
                public void activeClick(View view) {
                    context.startActivity(new Intent(context, BuyIyubiActivity.class));
                    materialDialog.dismiss();
                }
            });
            materialDialog.setNegativeButton(R.string.app_cancel, new INoDoubleClick() {
                @Override
                public void activeClick(View view) {
                    materialDialog.dismiss();
                }
            });
            materialDialog.show();
        } else if (type != 4) {
            RequestClient.requestAsync(new PayRequest(new String[]{AccountManager.getInstance().getUserId(),
                    String.valueOf(PAY_GOODS[type]), String.valueOf(PAY_MONTH[type])}), new SimpleRequestCallBack<BaseApiEntity<UserInfo>>() {
                @Override
                public void onSuccess(BaseApiEntity<UserInfo> apiEntity) {
                    if (BaseApiEntity.isSuccess(apiEntity)) {
                        userInfo = apiEntity.getData();
                        new UserInfoOp().saveData(userInfo);
                        AccountManager.getInstance().setUserInfo(userInfo);
                        CustomToast.getInstance().showToast(context.getString(R.string.vip_buy_success,
                                userInfo.getIyubi()));
                        onActivityResumed();
                    } else {
                        CustomToast.getInstance().showToast(context.getString(R.string.vip_buy_fail,
                                apiEntity.getMessage()));
                    }
                }

                @Override
                public void onError(ErrorInfoWrapper errorInfoWrapper) {
                    CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
                }
            });
        } else {
            RequestClient.requestAsync(new PayForAppRequest(new String[]
                    {AccountManager.getInstance().getUserId(), String.valueOf(PAY_GOODS[type])}), new SimpleRequestCallBack<BaseApiEntity<UserInfo>>() {
                @Override
                public void onSuccess(BaseApiEntity<UserInfo> apiEntity) {
                    if (BaseApiEntity.isSuccess(apiEntity)) {
                        userInfo = apiEntity.getData();
                        new UserInfoOp().saveData(userInfo);
                        AccountManager.getInstance().setUserInfo(userInfo);
                        CustomToast.getInstance().showToast(context.getString(R.string.vip_buy_success,
                                userInfo.getIyubi()));
                        onActivityResumed();
                    } else {
                        CustomToast.getInstance().showToast(context.getString(R.string.vip_buy_fail, apiEntity.getMessage()));
                    }
                }

                @Override
                public void onError(ErrorInfoWrapper errorInfoWrapper) {
                    CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ONLINE_PAY_CODE && resultCode == RESULT_OK) {              // 刷新vip时长
            AccountManager.getInstance().refreshVipStatus();
        }
    }
}
