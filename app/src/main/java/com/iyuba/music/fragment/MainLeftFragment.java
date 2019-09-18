package com.iyuba.music.fragment;

import android.app.Activity;
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

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.network.NetWorkState;
import com.buaa.ct.core.util.GetAppColor;
import com.buaa.ct.core.view.CustomToast;
import com.buaa.ct.core.view.MaterialRippleLayout;
import com.buaa.ct.core.view.image.DividerItemDecoration;
import com.iyuba.music.R;
import com.iyuba.music.activity.AboutActivity;
import com.iyuba.music.activity.LoginActivity;
import com.iyuba.music.activity.me.ChangePhotoActivity;
import com.iyuba.music.activity.me.CreditActivity;
import com.iyuba.music.activity.me.PersonalHomeActivity;
import com.iyuba.music.adapter.OperAdapter;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.entity.user.UserInfoOp;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.util.Utils;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomSnackBar;
import com.iyuba.music.widget.imageview.VipPhoto;

/**
 * Created by 10202 on 2015/12/29.
 */
public class MainLeftFragment extends BaseFragment {
    private static final int HANDLE_SLEEP_TIME = 0;
    private Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private Context context;
    private View root;
    //侧边栏
    private UserInfo userInfo;
    private MaterialRippleLayout login, noLogin;
    private RecyclerView menuList;
    private OperAdapter operAdapter;
    private VipPhoto personalPhoto;
    private TextView personalName, personalGrade, personalCredits, personalFollow, personalFan;
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
        menuList = root.findViewById(R.id.oper_list);
        login = root.findViewById(R.id.personal_login);
        noLogin = root.findViewById(R.id.personal_nologin);
        personalPhoto = root.findViewById(R.id.personal_photo);
        personalName = root.findViewById(R.id.personal_name);
        personalGrade = root.findViewById(R.id.personal_grade);
        personalCredits = root.findViewById(R.id.personal_credit);
        personalFollow = root.findViewById(R.id.personal_follow);
        personalFan = root.findViewById(R.id.personal_fan);
        about = root.findViewById(R.id.about);
        exit = root.findViewById(R.id.exit);
        operAdapter = new OperAdapter();
    }

    private void setOnClickListener() {
        login.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                SocialManager.getInstance().pushFriendId(AccountManager.getInstance().getUserId());
                Intent intent = new Intent(context, PersonalHomeActivity.class);
                startActivity(intent);
            }
        });
        noLogin.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                startActivityForResult(new Intent(context, LoginActivity.class), 101);
            }
        });
        personalPhoto.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                startActivity(new Intent(context, ChangePhotoActivity.class));
            }
        });
        about.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                startActivity(new Intent(context, AboutActivity.class));
            }
        });
        exit.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                Utils.getMusicApplication().exit();
            }
        });
        root.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                // 防止误触
            }
        });
        menuList.setAdapter(operAdapter);
        ((SimpleItemAnimator) menuList.getItemAnimator()).setSupportsChangeAnimations(false);
        // menuList.getItemAnimator().setChangeDuration(0); 或者采用这个方案
        menuList.setLayoutManager(new LinearLayoutManager(context));
        menuList.addItemDecoration(new DividerItemDecoration());
    }

    private void changeUIResumeByPara() {
        handler.removeCallbacksAndMessages(HANDLE_SLEEP_TIME);
        if (AccountManager.getInstance().getUserId().equals("0")) {
            login.setVisibility(View.GONE);
            noLogin.setVisibility(View.VISIBLE);
        } else {
            login.setVisibility(View.VISIBLE);
            noLogin.setVisibility(View.GONE);
            if (AccountManager.getInstance().checkUserLogin()) {
                personalPhoto.setVipStateVisible(AccountManager.getInstance().getUserInfo().getUid(), "1".equals(AccountManager.getInstance().getUserInfo().getVipStatus()));
                if (userInfo != null && !userInfo.getUid().equals(AccountManager.getInstance().getUserId())) {
                    updatePersonalInfoView();
                    getPersonalInfo();
                }
            } else {
                personalPhoto.setVisitor();
                String visitorId = AccountManager.getInstance().getUserId();
                userInfo = new UserInfo();
                userInfo.setUid(visitorId);
                userInfo.setUsername(visitorId);
                AccountManager.getInstance().setUserInfo(userInfo);
                getPersonalInfo();
            }
        }
        operAdapter.notifyDataSetChanged();
        int sleepSecond = Utils.getMusicApplication().getSleepSecond();
        if (sleepSecond != 0) {
            handler.sendEmptyMessage(HANDLE_SLEEP_TIME);
        }
    }

    private void autoLogin() {
        if (ConfigManager.getInstance().isAutoLogin()) { // 自动登录
            String[] userNameAndPwd = AccountManager.getInstance().getNameAndPwdFromSp();
            if (!TextUtils.isEmpty(userNameAndPwd[0]) && !TextUtils.isEmpty(userNameAndPwd[1])) {
                AccountManager.getInstance().setLoginState(AccountManager.SIGN_IN);
                userInfo = new UserInfoOp().selectData(AccountManager.getInstance().getUserId());
                AccountManager.getInstance().setUserInfo(userInfo);
                if (!TextUtils.isEmpty(userInfo.getFollower())) {
                    updatePersonalInfoView();
                }
                if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.EXCEPT_2G)) {
                    AccountManager.getInstance().login(userNameAndPwd[0], userNameAndPwd[1],
                            new IOperationResult() {
                                @Override
                                public void success(Object message) {
                                    Activity parent = getActivity();
                                    if ("add".equals(message.toString()) && parent != null && !parent.isDestroyed()) {
                                        CustomSnackBar.make(root, context.getString(R.string.personal_daily_login)).info(context.getString(R.string.credit_check), new INoDoubleClick() {
                                            @Override
                                            public void activeClick(View view) {
                                                startActivity(new Intent(context, CreditActivity.class));
                                            }
                                        });
                                    }
                                    updatePersonalInfoView();
                                    getPersonalInfo();
                                }

                                @Override
                                public void fail(Object message) {
                                    CustomToast.getInstance().showToast(message.toString());
                                }
                            });
                }
            } else {
                AccountManager.getInstance().setLoginState(AccountManager.SIGN_OUT);
            }
        }
    }

    private void getPersonalInfo() {
        AccountManager.getInstance().getPersonalInfo(new IOperationResult() {
            @Override
            public void success(Object object) {
                userInfo = AccountManager.getInstance().getUserInfo();
                updatePersonalInfoView();
            }

            @Override
            public void fail(Object object) {
                userInfo = new UserInfoOp().selectData(AccountManager.getInstance().getUserId());
                if (userInfo != null && !TextUtils.isEmpty(userInfo.getFollowing())) {     // 获取不到时采用历史数据
                    AccountManager.getInstance().setUserInfo(userInfo);
                } else {
                    userInfo = AccountManager.getInstance().getUserInfo();
                }
                updatePersonalInfoView();
            }
        });
    }

    private void setPersonalInfoContent() {
        personalName.setText(userInfo.getUsername());
        personalGrade.setText(context.getString(R.string.personal_grade,
                UserInfo.getLevelName(context, TextUtils.isEmpty(userInfo.getIcoins()) ? 0 : Integer.parseInt(userInfo.getIcoins()))));
        personalCredits.setText(context.getString(R.string.personal_credits, TextUtils.isEmpty(userInfo.getIcoins()) ? "0" : userInfo.getIcoins()));
        operAdapter.notifyItemChanged(1);
        int follow = TextUtils.isEmpty(userInfo.getFollowing()) ? 0 : Integer.parseInt(userInfo.getFollowing());
        if (follow > 1000) {
            personalFollow.setText(context.getString(R.string.personal_follow, follow / 1000 + "k"));
            personalFollow.setTextColor(GetAppColor.getInstance().getAppColor());
        } else {
            personalFollow.setText(context.getString(R.string.personal_follow, String.valueOf(follow)));
        }
        int follower = TextUtils.isEmpty(userInfo.getFollower()) ? 0 : Integer.parseInt(userInfo.getFollower());
        if (follower > 10000) {
            personalFan.setText(context.getString(R.string.personal_fan, follower / 10000 + "w"));
            personalFan.setTextColor(GetAppColor.getInstance().getAppColor());
        } else {
            personalFan.setText(context.getString(R.string.personal_fan, String.valueOf(follower)));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == 1) {// 登录的返回结果
            getPersonalInfo();
            CustomSnackBar.make(root, context.getString(R.string.personal_daily_login)).info(context.getString(R.string.credit_check), new INoDoubleClick() {
                @Override
                public void activeClick(View view) {
                    startActivity(new Intent(context, CreditActivity.class));
                }
            });
        } else if (requestCode == 101 && resultCode == 2) {// 登录+注册的返回结果
            getPersonalInfo();
            CustomSnackBar.make(root, context.getString(R.string.personal_change_photo)).info(context.getString(R.string.app_accept), new INoDoubleClick() {
                @Override
                public void activeClick(View view) {
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
        handler.removeMessages(HANDLE_SLEEP_TIME);
        login.setOnClickListener(null);
        noLogin.setOnClickListener(null);
        personalPhoto.setOnClickListener(null);
        about.setOnClickListener(null);
        exit.setOnClickListener(null);
    }

    @Override
    public boolean onBackPressed() {
        if (operAdapter.onBackPressed()) {
            return true;
        } else {
            return super.onBackPressed();
        }
    }

    private void updatePersonalInfoView() {
        Activity parent = getActivity();
        if (parent != null && !parent.isDestroyed()) {
            personalPhoto.setVipStateVisible(userInfo.getUid(), "1".equals(userInfo.getVipStatus()));
            setPersonalInfoContent();
        }
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<MainLeftFragment> {
        @Override
        public void handleMessageByRef(final MainLeftFragment fragment, Message msg) {
            fragment.operAdapter.notifyItemChanged(OperAdapter.SLEEP_POS);
            fragment.handler.sendEmptyMessageDelayed(HANDLE_SLEEP_TIME, 1000);
        }
    }
}
