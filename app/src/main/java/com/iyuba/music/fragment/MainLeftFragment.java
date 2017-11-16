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
import android.widget.ImageView;
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
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.receiver.ChangePropertyBroadcast;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomSnackBar;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.dialog.SignInDialog;
import com.iyuba.music.widget.imageview.VipPhoto;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.iyuba.music.widget.roundview.RoundTextView;

/**
 * Created by 10202 on 2015/12/29.
 */
public class MainLeftFragment extends BaseFragment {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private Context context;
    private View root;
    //侧边栏
    private UserInfo userInfo;
    private MaterialRippleLayout login, noLogin, sign, signIn;
    private RecyclerView menuList;
    private OperAdapter operAdapter;
    private VipPhoto personalPhoto;
    private TextView personalName, personalGrade, personalCredits, personalFollow, personalFan;
    private TextView personalSign;
    private TextView signInHint;
    private RoundTextView signInHandle;
    private ImageView signInIcon;
    private SignInDialog signInDialog;
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
        signIn = (MaterialRippleLayout) root.findViewById(R.id.sign_in_layout);
        personalPhoto = (VipPhoto) root.findViewById(R.id.personal_photo);
        personalName = (TextView) root.findViewById(R.id.personal_name);
        personalGrade = (TextView) root.findViewById(R.id.personal_grade);
        personalCredits = (TextView) root.findViewById(R.id.personal_credit);
        personalFollow = (TextView) root.findViewById(R.id.personal_follow);
        personalFan = (TextView) root.findViewById(R.id.personal_fan);
        personalSign = (TextView) root.findViewById(R.id.personal_sign);
        about = root.findViewById(R.id.about);
        exit = root.findViewById(R.id.exit);
        signInIcon = (ImageView) root.findViewById(R.id.sign_in_icon);
        signInHint = (TextView) root.findViewById(R.id.sign_in_hint);
        signInHandle = (RoundTextView) root.findViewById(R.id.sign_in_handle);
        operAdapter = new OperAdapter();
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
                startActivityForResult(new Intent(context, LoginActivity.class), 101);
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
        signInHandle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountManager.getInstance().checkUserLogin()) {
                    signInDialog = new SignInDialog(context);
                } else {
                    startActivityForResult(new Intent(context, LoginActivity.class), 101);
                }
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
                            CustomDialog.showLoginDialog(context, false, new IOperationFinish() {
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
                        ConfigManager.getInstance().setNight(!ConfigManager.getInstance().isNight());
                        ChangePropery.updateNightMode(ConfigManager.getInstance().isNight());
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
        if (AccountManager.getInstance().getUserId().equals("0")) {
            login.setVisibility(View.GONE);
            noLogin.setVisibility(View.VISIBLE);
            sign.setVisibility(View.GONE);
            signIn.setVisibility(View.GONE);
        } else {
            login.setVisibility(View.VISIBLE);
            noLogin.setVisibility(View.GONE);
            sign.setVisibility(View.VISIBLE);
            signIn.setVisibility(View.VISIBLE);
            if (AccountManager.getInstance().checkUserLogin()) {
                signInIcon.setImageResource(R.drawable.sign_in_icon);
                signInHandle.setText(R.string.oper_sign_in_handle);
                signInHint.setText(R.string.oper_sign_in);
                personalPhoto.setVipStateVisible(AccountManager.getInstance().getUserInfo().getUid(), "1".equals(AccountManager.getInstance().getUserInfo().getVipStatus()));
                if (userInfo != null && !userInfo.getUid().equals(AccountManager.getInstance().getUserId())) {
                    getPersonalInfo();
                }
            } else {
                signInIcon.setImageResource(R.drawable.sign_in_icon);
                signInHandle.setText(R.string.oper_visitor_handle);
                signInHint.setText(R.string.oper_visitor);
                personalPhoto.setVisitor();
                String visitorId = AccountManager.getInstance().getUserId();
                userInfo = new UserInfo();
                userInfo.setUid(visitorId);
                AccountManager.getInstance().setUserInfo(userInfo);
                getPersonalInfo();
            }
        }
        int sleepSecond = ((MusicApplication) getActivity().getApplication()).getSleepSecond();
        if (sleepSecond != 0) {
            handler.sendEmptyMessage(1);
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
                                        CustomSnackBar.make(root, context.getString(R.string.personal_daily_login)).info(context.getString(R.string.credit_check), new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                startActivity(new Intent(context, CreditActivity.class));
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void fail(Object message) {

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
        if (TextUtils.isEmpty(userInfo.getText()) || "null".equals(userInfo.getText())) {
            personalSign.setText(R.string.personal_nosign);
        } else {
            personalSign.setText(userInfo.getText());
        }
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

    @Override
    public boolean onBackPressed() {
        if (signInDialog != null && signInDialog.isShown()) {
            signInDialog.dismiss();
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
            switch (msg.what) {
                case 1:
                    fragment.operAdapter.notifyItemChanged(5);
                    fragment.handler.sendEmptyMessageDelayed(1, 1000);
                    break;
            }
        }
    }
}
