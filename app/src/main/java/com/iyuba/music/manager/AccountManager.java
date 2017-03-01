package com.iyuba.music.manager;

import android.support.annotation.IntDef;
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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by 10202 on 2015/11/18.
 */
public class AccountManager {
    private static class SingleInstanceHelper {
        private static AccountManager instance = new AccountManager();
    }

    private UserInfo userInfo;
    @LoginState
    private int loginState;
    private String userId; // 用户ID
    private String userName; // 用户姓名
    private String userPwd; // 用户密码

    private AccountManager() {
        loginState = SIGN_OUT;
    }

    public static AccountManager getInstance() {
        return SingleInstanceHelper.instance;
    }

    public boolean checkUserLogin() {
        return loginState == SIGN_IN;
    }

    public boolean loginOut() {
        new UserInfoOp().delete(userId);
        loginState = SIGN_OUT;
        userId = ""; // 用户ID
        userName = ""; // 用户姓名
        userPwd = ""; // 用户密码
        userInfo = new UserInfo();
        saveUserNameAndPwd();
        SettingConfigManager.getInstance().setAutoLogin(false);
        return true;
    }

    private void saveUserNameAndPwd() {
        ConfigManager.getInstance().putString("userName", userName);
        ConfigManager.getInstance().putString("userPwd", userPwd);
        ConfigManager.getInstance().putString("userId", userId);
        if (!TextUtils.isEmpty(userId)) {
            HistoryLogin login = new HistoryLogin();
            login.setUserid(Integer.parseInt(userId));
            login.setUserName(userName);
            login.setUserPwd(userPwd);
            login.setLoginTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date()));
            new HistoryLoginOp().saveData(login);
        }
    }

    private String[] getUserNameAndPwd() {
        return new String[]{
                ConfigManager.getInstance().loadString("userName"),
                ConfigManager.getInstance().loadString("userPwd")};
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
                    if (BaseApiEntity.isSuccess(apiEntity)) {
                        userInfo = (UserInfo) apiEntity.getData();
                        new UserInfoOp().saveData(userInfo);
                        loginState = SIGN_IN;
                        userId = userInfo.getUid();
                        CustomToast.getInstance().showToast(RuntimeManager.getContext().getString(
                                R.string.login_success, userInfo.getUsername()));
                        saveUserNameAndPwd();
                        rc.success(apiEntity.getMessage());
                    } else if (BaseApiEntity.isFail(apiEntity)) {
                        loginState = SIGN_OUT;
                        SettingConfigManager.getInstance().setAutoLogin(false);
                        rc.fail(RuntimeManager.getString(R.string.login_fail));
                    } else {
                        loginState = SIGN_OUT;
                        SettingConfigManager.getInstance().setAutoLogin(false);
                        rc.fail(RuntimeManager.getString(R.string.login_error));
                    }
                }
            });
        } else if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            rc.fail(RuntimeManager.getString(R.string.net_speed_slow));
        } else {
            rc.fail(RuntimeManager.getString(R.string.no_internet));
        }
    }

    public void getPersonalInfo(final IOperationResult result) {
        PersonalInfoRequest.exeRequest(PersonalInfoRequest.generateUrl(userId, userId), userInfo, new IProtocolResponse() {
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
                if (BaseApiEntity.isSuccess(baseApiEntity)) {
                    userInfo = (UserInfo) baseApiEntity.getData();
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

    public void refreshVipStatus() {
        String[] paras = new String[]{userName, userPwd, String.valueOf(LocationUtil.getInstance().getLongitude())
                , String.valueOf(LocationUtil.getInstance().getLatitude())};
        LoginRequest.exeRequest(LoginRequest.generateUrl(paras), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {

            }

            @Override
            public void onServerError(String msg) {

            }

            @Override
            public void response(Object object) {
                BaseApiEntity apiEntity = (BaseApiEntity) object;
                if (BaseApiEntity.isSuccess(apiEntity)) {
                    UserInfo temp = (UserInfo) apiEntity.getData();
                    userInfo.setIyubi(temp.getIyubi());
                    userInfo.setVipStatus(temp.getVipStatus());
                    userInfo.setDeadline(temp.getDeadline());
                    new UserInfoOp().saveData(userInfo);
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

    @LoginState
    public int getLoginState() {
        return loginState;
    }

    public void setLoginState(@LoginState int state) {
        loginState = state;
        if (loginState == SIGN_IN) {
            userId = ConfigManager.getInstance().loadString("userId");
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

    public static final int SIGN_OUT = 0x01;
    public static final int SIGN_IN = 0x02;

    @IntDef({SIGN_OUT, SIGN_IN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoginState {
    }
}
