package com.iyuba.music.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.AboutActivity;
import com.iyuba.music.activity.LoginActivity;
import com.iyuba.music.activity.SettingActivity;
import com.iyuba.music.activity.SleepActivity;
import com.iyuba.music.activity.WxOfficialAccountActivity;
import com.iyuba.music.activity.discover.DiscoverActivity;
import com.iyuba.music.activity.me.ChangePhotoActivity;
import com.iyuba.music.activity.me.CreditActivity;
import com.iyuba.music.activity.me.MeActivity;
import com.iyuba.music.activity.me.PersonalHomeActivity;
import com.iyuba.music.activity.me.VipCenterActivity;
import com.iyuba.music.activity.me.WriteStateActivity;
import com.iyuba.music.adapter.OperAdapter;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.entity.user.UserInfoOp;
import com.iyuba.music.ground.AppGroundActivity;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.receiver.ChangePropertyBroadcast;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomSnackBar;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.imageview.VipPhoto;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

/**
 * Created by 10202 on 2015/12/29.
 */
public class MainLeftFragment extends BaseFragment {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private Context context;
    private View root;
    //侧边栏
    private UserInfo userInfo;
    private MaterialRippleLayout login, noLogin, sign;
    private RecyclerView menuList;
    private OperAdapter operAdapter;
    private VipPhoto personalPhoto;
    private TextView personalName, personalGrade, personalCredits, personalFollow, personalFan;
    private TextView personalSign;
    //底部
    private View about, exit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.main_left, null);
        initWidget();
        setOnClickListener();
        autoLogin();
        return root;
    }

    private void initWidget() {
        menuList = (RecyclerView) root.findViewById(R.id.oper_list);
        login = (MaterialRippleLayout) root.findViewById(R.id.personal_login);
        noLogin = (MaterialRippleLayout) root.findViewById(R.id.personal_nologin);
        sign = (MaterialRippleLayout) root.findViewById(R.id.sign_layout);
        personalPhoto = (VipPhoto) root.findViewById(R.id.personal_photo);
        personalName = (TextView) root.findViewById(R.id.personal_name);
        personalGrade = (TextView) root.findViewById(R.id.personal_grade);
        personalCredits = (TextView) root.findViewById(R.id.personal_credit);
        personalFollow = (TextView) root.findViewById(R.id.personal_follow);
        personalFan = (TextView) root.findViewById(R.id.personal_fan);
        personalSign = (TextView) root.findViewById(R.id.personal_sign);
        about = root.findViewById(R.id.about);
        exit = root.findViewById(R.id.exit);
        operAdapter = new OperAdapter(context);
    }

    private void setOnClickListener() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialManager.getInstance().pushFriendId(AccountManager.getInstance().getUserId());
                Intent intent = new Intent(context, PersonalHomeActivity.class);
                intent.putExtra("needpop", true);
                startActivity(intent);
            }
        });
        noLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.launchForResult(getActivity());
            }
        });
        personalPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, ChangePhotoActivity.class));
            }
        });
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, WriteStateActivity.class));
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AboutActivity.class));
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RuntimeManager.getApplication().exit();
            }
        });
        menuList.setAdapter(operAdapter);
        ((SimpleItemAnimator) menuList.getItemAnimator()).setSupportsChangeAnimations(false);
        // menuList.getItemAnimator().setChangeDuration(0); 或者采用这个方案
        menuList.setLayoutManager(new LinearLayoutManager(context));
        menuList.addItemDecoration(new DividerItemDecoration());
        operAdapter.setItemClickListener(new OperAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                switch (position) {
                    case 0:
                        if (AccountManager.getInstance().checkUserLogin()) {
                            startActivity(new Intent(context, VipCenterActivity.class));
                        } else {
                            CustomDialog.showLoginDialog(context, new IOperationFinish() {
                                @Override
                                public void finish() {
                                    startActivity(new Intent(context, VipCenterActivity.class));
                                }
                            });
                        }
                        break;
                    case 1:
//                        HeadlinesRuntimeManager.setApplicationContext(RuntimeManager.getContext());
//                        String userid = "0";
//                        if (AccountManager.getInstance().checkUserLogin()) {
//                            userid = AccountManager.getInstance().getUserId();
//                        }
//                        startActivity(MainHeadlinesActivity.getIntent2Me(context, userid, "209", "music", (DownloadService.checkVip() ? "1" : "0")));
                        startActivity(new Intent(context, AppGroundActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(context, DiscoverActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(context, MeActivity.class));
                        break;
                    case 4:
                        SettingConfigManager.getInstance().setNight(!SettingConfigManager.getInstance().isNight());
                        ChangePropery.updateNightMode(SettingConfigManager.getInstance().isNight());
                        Intent intent = new Intent(ChangePropertyBroadcast.FLAG);
                        intent.putExtra(ChangePropertyBroadcast.SOURCE, getActivity().getClass().getSimpleName());
                        context.sendBroadcast(intent);
                        break;
                    case 5:
                        startActivity(new Intent(context, SleepActivity.class));
                        break;
                    case 6:
                        startActivity(new Intent(context, WxOfficialAccountActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(context, SettingActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void changeUIResumeByPara() {
        handler.removeCallbacksAndMessages(null);
        if (AccountManager.getInstance().checkUserLogin()) {
            login.setVisibility(View.VISIBLE);
            noLogin.setVisibility(View.GONE);
            sign.setVisibility(View.VISIBLE);
            personalPhoto.setVipStateVisible(AccountManager.getInstance().getUserInfo().getUid(), "1".equals(AccountManager.getInstance().getUserInfo().getVipStatus()));
            if (TextUtils.isEmpty(AccountManager.getInstance().getUserInfo().getFollower())) {
                getPersonalInfo();
            }
        } else {
            login.setVisibility(View.GONE);
            noLogin.setVisibility(View.VISIBLE);
            sign.setVisibility(View.GONE);
        }
        int sleepSecond = ((MusicApplication) getActivity().getApplication()).getSleepSecond();
        if (sleepSecond != 0) {
            handler.sendEmptyMessage(1);
        }
    }

    private void autoLogin() {
        if (SettingConfigManager.getInstance().isAutoLogin()) { // 自动登录
            AccountManager.getInstance().setLoginState(AccountManager.SIGN_IN);
            String[] userNameAndPwd = AccountManager.getInstance().getUserNameAndPwd();
            if (!TextUtils.isEmpty(userNameAndPwd[0]) && !TextUtils.isEmpty(userNameAndPwd[1])) {
                if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.EXCEPT_2G)) {
                    userInfo = new UserInfoOp().selectData(AccountManager.getInstance().getUserId());
                    AccountManager.getInstance().setUserInfo(userInfo);
                    AccountManager.getInstance().login(userNameAndPwd[0], userNameAndPwd[1],
                            new IOperationResult() {
                                @Override
                                public void success(Object message) {
                                    if ("add".equals(message.toString())) {
                                        CustomSnackBar.make(root, context.getString(R.string.personal_daily_login)).info(context.getString(R.string.credit_check), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                startActivity(new Intent(context, CreditActivity.class));
                                            }
                                        });
                                    }
                                    getPersonalInfo();
                                }

                                @Override
                                public void fail(Object message) {
                                    localLogin();
                                }
                            });
                } else {
                    localLogin();
                }
            } else {
                AccountManager.getInstance().setLoginState(AccountManager.SIGN_OUT);
            }
        }
    }

    private void localLogin() {
        AccountManager.getInstance().setLoginState(AccountManager.SIGN_IN);
        userInfo = new UserInfoOp().selectData(AccountManager.getInstance().getUserId());
        AccountManager.getInstance().setUserInfo(userInfo);
        handler.sendEmptyMessage(0);
    }

    private void getPersonalInfo() {
        AccountManager.getInstance().getPersonalInfo(new IOperationResult() {
            @Override
            public void success(Object object) {
                userInfo = AccountManager.getInstance().getUserInfo();
                handler.sendEmptyMessage(0);
            }

            @Override
            public void fail(Object object) {
                userInfo = new UserInfoOp().selectData(AccountManager.getInstance().getUserId());
                if (userInfo != null && !TextUtils.isEmpty(userInfo.getFollowing())) {     // 获取不到时采用历史数据
                    AccountManager.getInstance().setUserInfo(userInfo);
                } else {
                    userInfo = AccountManager.getInstance().getUserInfo();
                }
                handler.sendEmptyMessage(0);
            }
        });
    }

    private void setPersonalInfoContent() {
        personalName.setText(userInfo.getUsername());
        personalGrade.setText(context.getString(R.string.personal_grade,
                UserInfo.getLevelName(context, TextUtils.isEmpty(userInfo.getIcoins()) ? 0 : Integer.parseInt(userInfo.getIcoins()))));
        personalCredits.setText(context.getString(R.string.personal_credits, TextUtils.isEmpty(userInfo.getIcoins()) ? "0" : userInfo.getIcoins()));
        if (TextUtils.isEmpty(userInfo.getText()) || "null".equals(userInfo.getText())) {
            personalSign.setText(R.string.personal_nosign);
        } else {
            personalSign.setText(userInfo.getText());
        }
        int follow = TextUtils.isEmpty(userInfo.getFollowing()) ? 0 : Integer.parseInt(userInfo.getFollowing());
        if (follow > 1000) {
            personalFollow.setText(context.getString(R.string.personal_follow, follow / 1000 + "k"));
            personalFollow.setTextColor(GetAppColor.getInstance().getAppColor(context));
        } else {
            personalFollow.setText(context.getString(R.string.personal_follow, String.valueOf(follow)));
        }
        int follower = TextUtils.isEmpty(userInfo.getFollower()) ? 0 : Integer.parseInt(userInfo.getFollower());
        if (follower > 10000) {
            personalFan.setText(context.getString(R.string.personal_fan, follower / 10000 + "w"));
            personalFan.setTextColor(GetAppColor.getInstance().getAppColor(context));
        } else {
            personalFan.setText(context.getString(R.string.personal_fan, String.valueOf(follower)));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == 1) {// 登录的返回结果
            getPersonalInfo();
            CustomSnackBar.make(root, context.getString(R.string.personal_daily_login)).info(context.getString(R.string.credit_check), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, CreditActivity.class));
                }
            });
        } else if (requestCode == 101 && resultCode == 2) {// 登录+注册的返回结果
            getPersonalInfo();
            CustomSnackBar.make(root, context.getString(R.string.personal_change_photo)).info(context.getString(R.string.app_accept), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, ChangePhotoActivity.class));
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        changeUIResumeByPara();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        operAdapter.setItemClickListener(null);
        login.setOnClickListener(null);
        noLogin.setOnClickListener(null);
        personalPhoto.setOnClickListener(null);
        sign.setOnClickListener(null);
        about.setOnClickListener(null);
        exit.setOnClickListener(null);
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<MainLeftFragment> {
        @Override
        public void handleMessageByRef(final MainLeftFragment fragment, Message msg) {
            switch (msg.what) {
                case 0:
                    if (!TextUtils.isEmpty(fragment.userInfo.getUid())) {
                        fragment.personalPhoto.setVipStateVisible(fragment.userInfo.getUid(), "1".equals(fragment.userInfo.getVipStatus()));
                        fragment.setPersonalInfoContent();
                        fragment.login.setVisibility(View.VISIBLE);
                        fragment.noLogin.setVisibility(View.GONE);
                        fragment.sign.setVisibility(View.VISIBLE);
                    }
                    break;
                case 1:
                    fragment.operAdapter.notifyItemChanged(5);
                    fragment.handler.sendEmptyMessageDelayed(1, 1000);
                    break;
            }
        }
    }
}
