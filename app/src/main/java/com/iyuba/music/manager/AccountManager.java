package com.iyuba.music.manager;

import android.location.Location;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.buaa.ct.core.manager.RuntimeManager;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.util.LocationUtil;
import com.buaa.ct.core.util.SPUtils;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.HistoryLogin;
import com.iyuba.music.entity.user.HistoryLoginOp;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.entity.user.UserInfoOp;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.request.account.LoginRequest;
import com.iyuba.music.request.merequest.PersonalInfoRequest;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.Utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;


/**
 * Created by 10202 on 2015/11/18.
 */
public class AccountManager {
    public static final int SIGN_OUT = 0x01;
    public static final int SIGN_IN = 0x02;
    @LoginState
    private int loginState;
    private UserInfo userInfo;
    private String userId; // 用户ID
    private String visitorId;
    private String userName; // 用户姓名
    private String userPwd; // 用户密码

    private boolean isGetPosition = false;
    private double latitude = 39.9;
    private double longitude = 116.3;

    private AccountManager() {
        loginState = SIGN_OUT;
        visitorId = SPUtils.loadString(ConfigManager.getInstance().getPreferences(), "visitorId");
    }

    public static AccountManager getInstance() {
        return SingleInstanceHelper.instance;
    }

    public boolean checkUserLogin() {
        return loginState == SIGN_IN;
    }

    public void loginOut() {
        new UserInfoOp().delete(userId);
        loginState = SIGN_OUT;
        userId = ""; // 用户ID
        visitorId = "";
        userName = ""; // 用户姓名
        userPwd = ""; // 用户密码
        userInfo = new UserInfo();
        saveUserNameAndPwd();
        SPUtils.putString(ConfigManager.getInstance().getPreferences(), "visitorId", "");
        ConfigManager.getInstance().setAutoLogin(false);
    }

    private void saveUserNameAndPwd() {
        SPUtils.putString(ConfigManager.getInstance().getPreferences(), "userName", userName);
        SPUtils.putString(ConfigManager.getInstance().getPreferences(), "userPwd", userPwd);
        SPUtils.putString(ConfigManager.getInstance().getPreferences(), "userId", userId);
        if (!TextUtils.isEmpty(userId)) {
            HistoryLogin login = new HistoryLogin();
            login.setUserid(Integer.parseInt(userId));
            login.setUserName(userName);
            login.setUserPwd(userPwd);
            login.setLoginTime(DateFormat.formatTime(new Date()));
            new HistoryLoginOp().saveData(login);
        }
    }

    public String[] getNameAndPwdFromSp() {
        return new String[]{
                SPUtils.loadString(ConfigManager.getInstance().getPreferences(), "userName"),
                SPUtils.loadString(ConfigManager.getInstance().getPreferences(), "userPwd")};
    }

    public void login(String userName, String userPwd, final IOperationResult rc) {
        this.userName = userName;
        this.userPwd = userPwd;
        String[] paras = new String[]{userName, userPwd, String.valueOf(getLongitude())
                , String.valueOf(getLatitude())};
        RequestClient.requestAsync(new LoginRequest(paras), new SimpleRequestCallBack<BaseApiEntity<UserInfo>>() {
            @Override
            public void onSuccess(BaseApiEntity<UserInfo> apiEntity) {
                loginState = SIGN_IN;

                UserInfoOp userInfoOp = new UserInfoOp();
                UserInfo tempResult = apiEntity.getData();
                userId = tempResult.getUid();
                if (userInfoOp.selectDataByName(getInstance().userName) == null) {
                    saveUserNameAndPwd();
                }
                if (userInfo == null) {
                    userInfo = tempResult;
                } else {
                    userInfo.update(tempResult);
                }
                userInfoOp.saveData(userInfo);

                if (Utils.getMusicApplication().isShowSignInToast()) {
                    Utils.getMusicApplication().setShowSignInToast(false);
                    CustomToast.getInstance().showToast(RuntimeManager.getInstance().getContext().getString(
                            R.string.login_success, userInfo.getUsername()));
                }

                if (rc != null) {
                    rc.success(apiEntity.getMessage());
                }
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                loginState = SIGN_OUT;
                ConfigManager.getInstance().setAutoLogin(false);
                if (rc != null) {
                    rc.fail(Utils.getRequestErrorMeg(errorInfoWrapper));
                }
            }
        });
    }

    public void getPersonalInfo(final IOperationResult result) {
        RequestClient.requestAsync(new PersonalInfoRequest(getUserId(), getUserId(), userInfo), new SimpleRequestCallBack<BaseApiEntity<UserInfo>>() {
            @Override
            public void onSuccess(BaseApiEntity<UserInfo> baseApiEntity) {
                if (BaseApiEntity.isSuccess(baseApiEntity)) {
                    userInfo = baseApiEntity.getData();
                    if (result != null) {
                        result.success(null);
                    }
                    new UserInfoOp().saveData(userInfo);
                } else {
                    if (result != null) {
                        result.fail(null);
                    }
                }
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                if (result != null) {
                    result.fail(null);
                }
            }
        });
    }

    public void refreshVipStatus() {
        String[] paras = new String[]{userName, userPwd, String.valueOf(getLongitude())
                , String.valueOf(getLatitude())};
        RequestClient.requestAsync(new LoginRequest(paras), new SimpleRequestCallBack<BaseApiEntity<UserInfo>>() {
            @Override
            public void onSuccess(BaseApiEntity<UserInfo> apiEntity) {
                UserInfo temp = apiEntity.getData();
                userInfo.setIyubi(temp.getIyubi());
                userInfo.setVipStatus(temp.getVipStatus());
                userInfo.setDeadline(temp.getDeadline());
                new UserInfoOp().saveData(userInfo);
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {

            }
        });
    }

    public String getUserId() {
        if (loginState == SIGN_IN) {
            return userId;
        } else if (!TextUtils.isEmpty(visitorId)) {
            return visitorId;
        } else {
            return "0";
        }
    }

    public boolean needGetVisitorID() {
        return TextUtils.isEmpty(visitorId) && SPUtils.loadBoolean(ConfigManager.getInstance().getPreferences(), "gotVisitor", true);
    }

    public void setLoginState(@LoginState int state) {
        loginState = state;
        if (loginState == SIGN_IN) {
            userId = SPUtils.loadString(ConfigManager.getInstance().getPreferences(), "userId");
        }
    }

    public void getGPS() {
        LocationUtil.getCurLocation(new LocationUtil.OnLocationListener() {
            @Override
            public void getlocation(Location location) {
                setLocation(location);
            }
        });
    }

    private void setLocation(@Nullable Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            isGetPosition = true;
        } else {
            latitude = 39.9;
            longitude = 116.3;
            isGetPosition = false;
        }
    }

    public double getLatitude() {
        if (!isGetPosition) {
            getGPS();
        }
        return latitude;
    }

    public double getLongitude() {
        if (!isGetPosition) {
            getGPS();
        }
        return longitude;
    }

    public String getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @IntDef({SIGN_OUT, SIGN_IN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoginState {
    }

    private static class SingleInstanceHelper {
        private static AccountManager instance = new AccountManager();
    }
}
