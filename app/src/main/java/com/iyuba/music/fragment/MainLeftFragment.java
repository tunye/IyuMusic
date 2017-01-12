package com.iyuba.music.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.AboutActivity;
import com.iyuba.music.activity.LoginActivity;
import com.iyuba.music.activity.SettingActivity;
import com.iyuba.music.activity.WebViewActivity;
import com.iyuba.music.activity.discover.DiscoverActivity;
import com.iyuba.music.activity.me.ChangePhotoActivity;
import com.iyuba.music.activity.me.CreditActivity;
import com.iyuba.music.activity.me.MessageActivity;
import com.iyuba.music.activity.me.PersonalHomeActivity;
import com.iyuba.music.activity.me.VipCenterActivity;
import com.iyuba.music.activity.me.WriteStateActivity;
import com.iyuba.music.adapter.OperAdapter;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.entity.user.UserInfoOp;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.local_music.LocalMusicActivity;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.CustomDialog;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 10202 on 2015/12/29.
 */
public class MainLeftFragment extends BaseFragment {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private Context context;
    private Snackbar snackbar;
    private View root;
    //侧边栏
    private UserInfo userInfo;
    private MaterialRippleLayout login, noLogin, sign;
    private ListView menuList;
    private OperAdapter operAdapter;
    private CircleImageView personalPhoto;
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
        menuList = (ListView) root.findViewById(R.id.oper_list);
        login = (MaterialRippleLayout) root.findViewById(R.id.personal_login);
        noLogin = (MaterialRippleLayout) root.findViewById(R.id.personal_nologin);
        sign = (MaterialRippleLayout) root.findViewById(R.id.sign_layout);
        menuList = (ListView) root.findViewById(R.id.oper_list);
        personalPhoto = (CircleImageView) root.findViewById(R.id.personal_photo);
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
                SocialManager.instance.pushFriendId(AccountManager.instance.getUserId());
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
                ((MusicApplication) RuntimeManager.getApplication()).exit();
            }
        });
        menuList.setAdapter(operAdapter);
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if (AccountManager.instance.getLoginState().equals(AccountManager.LoginState.LOGIN)) {
                            startActivity(new Intent(context, VipCenterActivity.class));
                        } else {
                            CustomDialog.showLoginDialog(context);
                        }
                        break;
                    case 1:
                        if (AccountManager.instance.checkUserLogin()) {
                            StringBuilder url = new StringBuilder();
                            url.append("http://m.iyuba.com/i/getLeaderBoard.jsp?appId=")
                                    .append(ConstantManager.instance.getAppId()).append("&uid=")
                                    .append(AccountManager.instance.getUserId()).append("&sign=")
                                    .append(MD5.getMD5ofStr(AccountManager.instance.getUserId()
                                            + "leaderBoard" + ConstantManager.instance.getAppId()));
                            Intent intent = new Intent();
                            intent.setClass(context, WebViewActivity.class);
                            intent.putExtra("url", url.toString());
                            intent.putExtra("title", context.getString(R.string.oper_rank));
                            startActivity(intent);
                        } else {
                            CustomDialog.showLoginDialog(context);
                        }
                        break;
                    case 2:
                        if (AccountManager.instance.getLoginState().equals(AccountManager.LoginState.LOGIN)) {
                            startActivity(new Intent(context, CreditActivity.class));
                        } else {
                            CustomDialog.showLoginDialog(context);
                        }
                        break;
                    case 3:
                        startActivity(new Intent(context, DiscoverActivity.class));
                        break;
                    case 4:
                        if (AccountManager.instance.checkUserLogin()) {
                            startActivity(new Intent(context, MessageActivity.class));
                        } else {
                            CustomDialog.showLoginDialog(context);
                        }
                        break;
                    case 5:
                        startActivity(new Intent(context, LocalMusicActivity.class));
                        break;
                    case 6:
                        if (AccountManager.instance.checkUserLogin()) {
                            StringBuilder url = new StringBuilder("http://m.iyuba.com/i/index.jsp?");
                            url.append("uid=").append(AccountManager.instance.getUserId()).append('&');
                            url.append("username=").append(AccountManager.instance.getUserName()).append('&');
                            url.append("sign=").append(MD5.getMD5ofStr("iyuba" + AccountManager.instance.getUserId() + "camstory"));
                            Intent intent = new Intent();
                            intent.setClass(context, WebViewActivity.class);
                            intent.putExtra("url", url.toString());
                            intent.putExtra("title", context.getString(R.string.oper_bigdata));
                            startActivity(intent);
                        } else {
                            CustomDialog.showLoginDialog(context);
                        }
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
        if (AccountManager.instance.checkUserLogin()) {
            login.setVisibility(View.VISIBLE);
            noLogin.setVisibility(View.GONE);
            sign.setVisibility(View.VISIBLE);
            getPersonalInfo();
        } else {
            login.setVisibility(View.GONE);
            noLogin.setVisibility(View.VISIBLE);
            sign.setVisibility(View.GONE);
        }
    }

    private void autoLogin() {
        if (SettingConfigManager.instance.isAutoLogin()) { // 自动登录
            AccountManager.instance.setLoginState(AccountManager.LoginState.LOGIN);
            if (!TextUtils.isEmpty(AccountManager.instance.getUserName()) && !TextUtils.isEmpty(AccountManager.instance.getUserPwd())) {
                if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.EXCEPT_2G)) {
                    userInfo = new UserInfoOp().selectData(AccountManager.instance.getUserId());
                    AccountManager.instance.setUserInfo(userInfo);
                    AccountManager.instance.login(AccountManager.instance.getUserName(), AccountManager.instance.getUserPwd(),
                            new IOperationResult() {
                                @Override
                                public void success(Object message) {
                                    if ("add".equals(message.toString())) {
                                        snackbar = Snackbar.make(root, R.string.personal_daily_login, Snackbar.LENGTH_LONG).setAction(R.string.credit_check, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(context, CreditActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                                        ((TextView) snackbar.getView().findViewById(R.id.snackbar_text)).setTextColor(Color.WHITE);
                                        snackbar.show();
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
                AccountManager.instance.setLoginState(AccountManager.LoginState.UNLOGIN);
            }
        }
    }

    private void localLogin() {
        AccountManager.instance.setLoginState(AccountManager.LoginState.LOGIN);
        userInfo = new UserInfoOp().selectData(AccountManager.instance.getUserId());
        AccountManager.instance.setUserInfo(userInfo);
        handler.sendEmptyMessage(0);
    }

    private void getPersonalInfo() {
        AccountManager.instance.getPersonalInfo(new IOperationResult() {
            @Override
            public void success(Object object) {
                userInfo = AccountManager.instance.getUserInfo();
                handler.sendEmptyMessage(0);
            }

            @Override
            public void fail(Object object) {
                userInfo = new UserInfoOp().selectData(AccountManager.instance.getUserId());
                if (userInfo != null && !TextUtils.isEmpty(userInfo.getFollowing())) {
                    AccountManager.instance.setUserInfo(userInfo);
                    handler.sendEmptyMessage(0);
                } else {
                    AccountManager.instance.setLoginState(AccountManager.LoginState.UNLOGIN);
                    changeUIResumeByPara();
                    CustomToast.INSTANCE.showToast("您的账号信息异常，请重新登录");
                }
            }
        });
    }

    private void setPersonalInfoContent() {
        personalName.setText(userInfo.getUsername());
        personalGrade.setText(context.getString(R.string.personal_grade,
                UserInfo.getLevelName(context, Integer.parseInt(userInfo.getIcoins()))));
        personalCredits.setText(context.getString(R.string.personal_credits, userInfo.getIcoins()));
        if (TextUtils.isEmpty(userInfo.getText()) || "null".equals(userInfo.getText())) {
            personalSign.setText(R.string.personal_nosign);
        } else {
            personalSign.setText(userInfo.getText());
        }
        int follow = Integer.parseInt(userInfo.getFollowing());
        if (follow > 1000) {
            personalFollow.setText(context.getString(R.string.personal_follow, follow / 1000 + "k"));
            personalFollow.setTextColor(GetAppColor.instance.getAppColor(context));
        } else {
            personalFollow.setText(context.getString(R.string.personal_follow, userInfo.getFollowing()));
        }
        int follower = Integer.parseInt(userInfo.getFollower());
        if (follower > 10000) {
            personalFan.setText(context.getString(R.string.personal_fan, follower / 10000 + "w"));
            personalFan.setTextColor(GetAppColor.instance.getAppColor(context));
        } else {
            personalFan.setText(context.getString(R.string.personal_fan, userInfo.getFollower()));
        }
        //personalMessage.setText(context.getString(R.string.personal_message, userInfo.getNotification()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == 1) {// 登录的返回结果
            getPersonalInfo();
            snackbar = Snackbar.make(root, R.string.personal_daily_login, Snackbar.LENGTH_LONG).setAction(R.string.credit_check, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CreditActivity.class);
                    startActivity(intent);
                }
            });
            ((TextView) snackbar.getView().findViewById(R.id.snackbar_text)).setTextColor(Color.WHITE);
            snackbar.show();
        } else if (requestCode == 101 && resultCode == 2) {// 登录+注册的返回结果
            getPersonalInfo();
            snackbar = Snackbar.make(root, R.string.personal_change_photo, Snackbar.LENGTH_LONG).setAction(R.string.app_accept, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AboutActivity.class);
                    startActivity(intent);
                }
            });
            ((TextView) snackbar.getView().findViewById(R.id.snackbar_text)).setTextColor(Color.WHITE);
            snackbar.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        changeUIResumeByPara();
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<MainLeftFragment> {
        @Override
        public void handleMessageByRef(final MainLeftFragment fragment, Message msg) {
            switch (msg.what) {
                case 0:
                    if (!TextUtils.isEmpty(fragment.userInfo.getUid())) {
                        ImageUtil.loadAvatar(fragment.userInfo.getUid(), fragment.personalPhoto);
                        fragment.setPersonalInfoContent();
                        fragment.login.setVisibility(View.VISIBLE);
                        fragment.noLogin.setVisibility(View.GONE);
                        fragment.sign.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    }
}
