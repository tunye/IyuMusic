package com.iyuba.music.manager;

import android.content.Context;
import android.text.TextUtils;

import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.HistoryLogin;
import com.iyuba.music.entity.user.HistoryLoginOp;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.entity.user.UserInfoOp;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.request.account.LoginRequest;
import com.iyuba.music.request.merequest.PersonalInfoRequest;
import com.iyuba.music.util.LocationUtil;
import com.iyuba.music.widget.CustomToast;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by 10202 on 2015/11/18.
 */
public enum AccountManager {
    instance;
    private Context context;
    private UserInfo userInfo;
    private LoginState loginState;
    private String userId; // 用户ID
    private String userName; // 用户姓名
    private String userPwd; // 用户密码

    AccountManager() {
        context = RuntimeManager.getContext();
        loginState = LoginState.UNLOGIN;
    }

    public boolean checkUserLogin() {
        return loginState.equals(LoginState.LOGIN);
    }

    public boolean loginOut() {
        new UserInfoOp().delete(userId);
        loginState = LoginState.UNLOGIN;
        userId = ""; // 用户ID
        userName = ""; // 用户姓名
        userPwd = ""; // 用户密码
        userInfo = new UserInfo();
        saveUserNameAndPwd();
        SettingConfigManager.instance.setAutoLogin(false);
        return true;
    }

    public void saveUserNameAndPwd() {
        ConfigManager.instance.putString("userName", userName);
        ConfigManager.instance.putString("userPwd", userPwd);
        ConfigManager.instance.putString("userId", userId);
        if (!TextUtils.isEmpty(userId)) {
            HistoryLogin login = new HistoryLogin();
            login.setUserid(Integer.parseInt(userId));
            login.setUserName(userName);
            login.setUserPwd(userPwd);
            login.setLoginTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            new HistoryLoginOp().saveData(login);
        }
    }

    public String[] getUserNameAndPwd() {
        String[] nameAndPwd = new String[]{
                ConfigManager.instance.loadString("userName"),
                ConfigManager.instance.loadString("userPwd")};
        return nameAndPwd;
    }

    public void login(final String userName, String userPwd,
                      final IOperationResult rc) {
        this.userName = userName;
        this.userPwd = userPwd;
        String[] paras = new String[]{userName, userPwd, String.valueOf(LocationUtil.getInstance().getLongitude())
                , String.valueOf(LocationUtil.getInstance().getLatitude())};
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.EXCEPT_2G)) {
            LoginRequest.exeRequest(LoginRequest.generateUrl(paras), new IProtocolResponse() {
                @Override
                public void onNetError(String msg) {
                    rc.fail(msg);
                }

                @Override
                public void onServerError(String msg) {
                    rc.fail(msg);
                }

                @Override
                public void response(Object object) {
                    BaseApiEntity apiEntity = (BaseApiEntity) object;
                    if (apiEntity.getState().equals(BaseApiEntity.State.SUCCESS)) {
                        userInfo = (UserInfo) apiEntity.getData();
                        new UserInfoOp().saveData(userInfo);
                        loginState = LoginState.LOGIN;
                        userId = userInfo.getUid();
                        CustomToast.INSTANCE.showToast(context.getString(
                                R.string.login_success, userInfo.getUsername()));
                        saveUserNameAndPwd();
                        rc.success(apiEntity.getMessage());
                    } else if (apiEntity.getState().equals(BaseApiEntity.State.FAIL)) {
                        loginState = LoginState.UNLOGIN;
                        SettingConfigManager.instance.setAutoLogin(false);
                        rc.fail(context.getString(R.string.login_fail));
                    } else {
                        loginState = LoginState.UNLOGIN;
                        SettingConfigManager.instance.setAutoLogin(false);
                        rc.fail(context.getString(R.string.login_error));
                    }
                }
            });
        } else if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            rc.fail(context.getString(R.string.net_speed_slow));
        } else {
            rc.fail(context.getString(R.string.no_internet));
        }
    }

    public void getPersonalInfo(final IOperationResult result) {
        PersonalInfoRequest.exeRequest(PersonalInfoRequest.generateUrl(userId, userId), new IProtocolResponse() {
                    @Override
                    public void onNetError(String msg) {
                        if (result != null) {
                            result.fail(null);
                        }
                    }

                    @Override
                    public void onServerError(String msg) {
                        if (result != null) {
                            result.fail(null);
                        }
                    }

                    @Override
                    public void response(Object object) {
                        BaseApiEntity baseApiEntity = (BaseApiEntity) object;
                        if (baseApiEntity.getState().equals(BaseApiEntity.State.SUCCESS)) {
                            new UserInfoOp().saveData(userInfo);
                            if (result != null) {
                                result.success(null);
                            }
                        } else {
                            if (result != null) {
                                result.fail(null);
                            }
                        }
                    }
                });
    }

    public String getUserPwd() {
        return userPwd;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public LoginState getLoginState() {
        return loginState;
    }

    public void setLoginState(LoginState state) {
        loginState = state;
        if (loginState.equals(LoginState.LOGIN)) {
            userId = ConfigManager.instance.loadString("userId");
            String[] nameAndPwd = getUserNameAndPwd();
            userName = nameAndPwd[0];
            userPwd = nameAndPwd[1];
        }
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public enum LoginState {UNLOGIN, LOGIN}
}
