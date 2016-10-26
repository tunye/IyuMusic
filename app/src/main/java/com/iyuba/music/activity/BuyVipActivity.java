package com.iyuba.music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.entity.user.UserInfoOp;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.account.PayForAppRequest;
import com.iyuba.music.request.account.PayRequest;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.CustomToast;

import de.hdodenhof.circleimageview.CircleImageView;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 10202 on 2015/12/29.
 */
public class BuyVipActivity extends BaseActivity {
    private final static int[] price = {200, 600, 1000, 2000, 1100};
    private UserInfo userInfo;
    private MaterialRippleLayout month, threeMonth, halfYear, year, app, iyubiRecharge;
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
        iyubiRecharge = (MaterialRippleLayout) findViewById(R.id.iyubi_recharge);

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
                final MaterialDialog materialDialog = new MaterialDialog(context);
                materialDialog.setTitle(R.string.vip_title).setMessage(context.getString(R.string.vip_buy_alert, price[0]));
                materialDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                    }
                });
                materialDialog.setPositiveButton(R.string.buy, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                        buy(1);
                    }
                });
                materialDialog.show();
            }
        });
        threeMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog materialDialog = new MaterialDialog(context);
                materialDialog.setTitle(R.string.vip_title).setMessage(context.getString(R.string.vip_buy_alert, price[1]));
                materialDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                    }
                });
                materialDialog.setPositiveButton(R.string.buy, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                        buy(3);
                    }
                });
                materialDialog.show();
            }
        });
        halfYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog materialDialog = new MaterialDialog(context);
                materialDialog.setTitle(R.string.vip_title).setMessage(context.getString(R.string.vip_buy_alert, price[2]));
                materialDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                    }
                });
                materialDialog.setPositiveButton(R.string.buy, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                        buy(6);
                    }
                });
                materialDialog.show();
            }
        });
        year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog materialDialog = new MaterialDialog(context);
                materialDialog.setTitle(R.string.vip_title).setMessage(context.getString(R.string.vip_buy_alert, price[3]));
                materialDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                    }
                });
                materialDialog.setPositiveButton(R.string.buy, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                        buy(12);
                    }
                });
                materialDialog.show();
            }
        });
        iyubiRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recharge();
            }
        });
        app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog materialDialog = new MaterialDialog(context);
                materialDialog.setTitle(R.string.vip_title).setMessage(context.getString(R.string.vip_buy_alert, price[4]));
                materialDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                    }
                });
                materialDialog.setPositiveButton(R.string.buy, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDialog.dismiss();
                        buy(0);
                    }
                });
                materialDialog.show();
            }
        });
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recharge();
            }
        });
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

    private void buy(int month) {
        int type = 0;
        switch (month) {
            case 0:
                type = 4;
                break;
            case 1:
                type = 0;
                break;
            case 3:
                type = 1;
                break;
            case 6:
                type = 2;
                break;
            case 12:
                type = 3;
                break;
        }
        if (Integer.parseInt(AccountManager.instance.getUserInfo().getIyubi()) < price[type]) {
            final MaterialDialog materialDialog = new MaterialDialog(context);
            materialDialog.setTitle(R.string.vip_huge).setMessage(R.string.vip_iyubi_not_enough);
            materialDialog.setPositiveButton(R.string.buy, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recharge();
                    materialDialog.dismiss();
                }
            });
            materialDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    materialDialog.dismiss();
                }
            });
            materialDialog.show();
        } else if (type != 4) {
            PayRequest.getInstance().exeRequest(PayRequest.getInstance().generateUrl(new String[]
                    {AccountManager.instance.getUserId(), String.valueOf(price[type]),
                            String.valueOf(month)}), new IProtocolResponse() {
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
}
