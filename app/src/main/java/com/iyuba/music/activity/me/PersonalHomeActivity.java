package com.iyuba.music.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.view.CustomToast;
import com.buaa.ct.imageselector.view.OnlyPreviewActivity;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseListActivity;
import com.iyuba.music.activity.MainActivity;
import com.iyuba.music.adapter.me.DoingAdapter;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.doings.Doing;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.merequest.AddAttentionRequest;
import com.iyuba.music.request.merequest.CancelAttentionRequest;
import com.iyuba.music.request.merequest.DoingRequest;
import com.iyuba.music.request.merequest.PersonalInfoRequest;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.imageview.VipPhoto;

import java.util.List;

/**
 * Created by 10202 on 2016/2/29.
 */
public class PersonalHomeActivity extends BaseListActivity<Doing> implements View.OnClickListener {
    public static final String NEED_POP = "need_pop";
    private UserInfo userinfo;
    //上部
    private VipPhoto personPhoto;
    private ImageView personSex;
    private Button otherDetail, message, attent, detail, fix, credits;
    private View myControl, otherControl;
    private TextView personName, personAttention, personFans, personViews;
    private boolean needPop;

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        needPop = getIntent().getBooleanExtra(NEED_POP, true);
    }

    @Override
    public int getLayoutId() {
        return R.layout.personal_home;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        myControl = findViewById(R.id.my_oper);
        otherControl = findViewById(R.id.other_oper);
        toolbarOper = findViewById(R.id.toolbar_oper);
        personPhoto = findViewById(R.id.personal_img);
        personSex = findViewById(R.id.name_sex);
        detail = findViewById(R.id.personal_detail);
        otherDetail = findViewById(R.id.personal_other_detail);
        message = findViewById(R.id.personal_message);
        fix = findViewById(R.id.personal_fix);
        credits = findViewById(R.id.personal_credit);
        personViews = findViewById(R.id.personal_view);
        attent = findViewById(R.id.personal_attent);
        personName = findViewById(R.id.name_text);
        personFans = findViewById(R.id.fans_fans);
        personAttention = findViewById(R.id.fans_attention);
        owner = findViewById(R.id.personal_doingslist);
        ownerAdapter = new DoingAdapter(context);
        ownerAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SocialManager.getInstance().pushDoing(getData().get(position));
                Intent intent = new Intent(context, ReplyDoingActivity.class);
                intent.putExtra(ReplyDoingActivity.VIP_FLG, "1".equals(userinfo.getVipStatus()));
                startActivity(intent);
            }
        });
        assembleRecyclerView();
    }

    @Override
    public void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(this);
        personAttention.setOnClickListener(this);
        personFans.setOnClickListener(this);
        personPhoto.setOnClickListener(this);
        detail.setOnClickListener(this);
        message.setOnClickListener(this);
        attent.setOnClickListener(this);
        fix.setOnClickListener(this);
        otherDetail.setOnClickListener(this);
        credits.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        enableToolbarOper(R.string.personal_logout);
        title.setText(R.string.person_title);
    }

    @Override
    public void onActivityResumed() {
        String tempUid = SocialManager.getInstance().getFriendId();
        if (tempUid.equals(AccountManager.getInstance().getUserId())) {//himself
            myControl.setVisibility(View.VISIBLE);
            otherControl.setVisibility(View.GONE);
            if (AccountManager.getInstance().checkUserLogin()) {
                toolbarOper.setVisibility(View.VISIBLE);
            } else {
                toolbarOper.setVisibility(View.GONE);
            }
            userinfo = AccountManager.getInstance().getUserInfo();
            setContent();
        } else {//other
            myControl.setVisibility(View.GONE);
            otherControl.setVisibility(View.VISIBLE);
            toolbarOper.setVisibility(View.GONE);
            userinfo = new UserInfo();
            userinfo.setUid(tempUid);
            RequestClient.requestAsync(new PersonalInfoRequest(tempUid, AccountManager.getInstance().getUserId(), userinfo), new SimpleRequestCallBack<BaseApiEntity<UserInfo>>() {
                @Override
                public void onSuccess(BaseApiEntity<UserInfo> apiEntity) {
                    if (BaseApiEntity.isSuccess(apiEntity)) {
                        userinfo = apiEntity.getData();
                        ((DoingAdapter) ownerAdapter).setVip("1".equals(userinfo.getVipStatus()));
                        ownerAdapter.notifyDataSetChanged();
                        setContent();
                        setRelationShip();
                    }
                }

                @Override
                public void onError(ErrorInfoWrapper errorInfoWrapper) {
                    CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
                }
            });
        }
        onRefresh(0);
    }

    @Override
    public void onBackPressed() {//isAppointExist
        if (mipush) {
            startActivity(new Intent(context, MainActivity.class));
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public int getToastResource() {
        return R.string.person_doings_load_all;
    }

    @Override
    public void onClick(View v) {
        if (INoDoubleClick.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.toolbar_oper:
                final MyMaterialDialog mMaterialDialog = new MyMaterialDialog(context);
                mMaterialDialog.setTitle(R.string.app_name)
                        .setMessage(R.string.personal_logout_textmore)
                        .setPositiveButton(R.string.personal_logout_exit, new INoDoubleClick() {
                            @Override
                            public void activeClick(View view) {
                                mMaterialDialog.dismiss();
                                AccountManager.getInstance().loginOut();
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.app_cancel, new INoDoubleClick() {
                            @Override
                            public void activeClick(View view) {
                                mMaterialDialog.dismiss();
                            }
                        });
                mMaterialDialog.show();
                break;
            case R.id.personal_img:
                if (SocialManager.getInstance().getFriendId().equals(AccountManager.getInstance().getUserId())) {
                    startActivity(new Intent(context, ChangePhotoActivity.class));
                } else {
                    OnlyPreviewActivity.startPreview(context, "http://api.iyuba.com.cn/v2/api.iyuba?protocol=10005&size=big&uid=" + SocialManager.getInstance().getFriendId());
                }
                break;
            case R.id.personal_attent:
                String state = attent.getText().toString();
                if (state.equals(context.getString(R.string.person_attention))) {
                    addAttention();
                } else {
                    final MyMaterialDialog cancleAttentionDialog = new MyMaterialDialog(context);
                    cancleAttentionDialog.setTitle(R.string.app_name)
                            .setMessage(R.string.person_attention_cancel_hint)
                            .setPositiveButton(R.string.app_accept, new INoDoubleClick() {
                                @Override
                                public void activeClick(View view) {
                                    cancleAttentionDialog.dismiss();
                                    cancelAttention();
                                }
                            })
                            .setNegativeButton(R.string.app_cancel, new INoDoubleClick() {
                                @Override
                                public void activeClick(View view) {
                                    cancleAttentionDialog.dismiss();
                                }
                            });
                    cancleAttentionDialog.show();
                }
                break;
            case R.id.fans_attention:
                SocialManager.getInstance().pushFriendId(SocialManager.getInstance().getFriendId());
                Intent intent = new Intent(context, FriendCenter.class);
                intent.putExtra(FriendCenter.NEED_POP, true);
                startActivity(intent);
                break;
            case R.id.fans_fans:
                SocialManager.getInstance().pushFriendId(SocialManager.getInstance().getFriendId());
                intent = new Intent(context, FriendCenter.class);
                intent.putExtra(FriendCenter.START_POS, 1);
                intent.putExtra(FriendCenter.NEED_POP, true);
                startActivity(intent);
                break;
            case R.id.personal_message:
                SocialManager.getInstance().pushFriendId(userinfo.getUid());
                SocialManager.getInstance().pushFriendName(userinfo.getUsername());
                intent = new Intent(context, ChattingActivity.class);
                startActivity(intent);
                break;
            case R.id.personal_other_detail:
            case R.id.personal_detail:
                SocialManager.getInstance().pushFriendId(userinfo.getUid());
                SocialManager.getInstance().pushFriendName(userinfo.getUsername());
                intent = new Intent(context, UserDetailInfoActivity.class);
                intent.putExtra("needpop", true);
                startActivity(intent);
                break;
            case R.id.personal_fix:
                startActivity(new Intent(context, EditUserDetailInfoActivity.class));
                break;
            case R.id.personal_credit:
                startActivity(new Intent(context, CreditActivity.class));
                break;
        }
    }

    @Override
    public void getNetData() {
        RequestClient.requestAsync(new DoingRequest(userinfo.getUid(), curPage), new SimpleRequestCallBack<BaseListEntity<List<Doing>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<Doing>> listEntity) {
                isLastPage = listEntity.isLastPage();
                if (isLastPage) {
                    if (curPage == 1) {
                        findViewById(R.id.no_doing).setVisibility(View.VISIBLE);
                    } else {
                        CustomToast.getInstance().showToast(R.string.person_doings_load_all);
                    }
                } else {
                    findViewById(R.id.no_doing).setVisibility(View.GONE);
                    ownerAdapter.addDatas(listEntity.getData());
//                    if (doingPage == 1) {
//
//                    } else {
//                        CustomToast.getInstance().showToast(doingPage + "/" + listEntity.getTotalPage(), 800);
//                    }
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                swipeRefreshLayout.setRefreshing(false);
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
            }
        });
    }

    private void setContent() {
        personViews.setText(context.getString(R.string.person_view, userinfo.getViews()));
        personName.setText(userinfo.getUsername());
        personFans.setText(context.getString(R.string.person_fans_count, userinfo.getFollower()));
        personAttention.setText(context.getString(R.string.person_attention_count, userinfo.getFollowing()));
        if (!TextUtils.isEmpty(userinfo.getGender())) {
            if (userinfo.getGender().equals("2")) {
                personSex.setBackgroundResource(R.drawable.user_info_female);
            } else {
                personSex.setBackgroundResource(R.drawable.user_info_male);
            }
        } else {
            personSex.setBackgroundResource(R.drawable.user_info_male);
        }
        personPhoto.setVipStateVisible(userinfo.getUid(), "1".equals(userinfo.getVipStatus()));
    }

    private void setRelationShip() {
        if (userinfo.getRelation().equals("0")) {
            // 我没关注了这个人
            attent.setText(R.string.person_attention);
        } else if (userinfo.getRelation().equals("1")) {
            // 他关注我
            attent.setText(R.string.person_attention);
        } else {
            char[] relation = {userinfo.getRelation().charAt(0),
                    userinfo.getRelation().charAt(1),
                    userinfo.getRelation().charAt(2)};
            if (relation[0] == '1' && relation[2] == '1') {
                // 相互关注
                attent.setText(R.string.person_attention_mutually);
            } else if (relation[0] == '1' && relation[2] == '0') {
                // 我关注了这个人
                attent.setText(R.string.person_attention_already);
            }
        }
    }

    private void addAttention() {
        RequestClient.requestAsync(new AddAttentionRequest(AccountManager.getInstance().getUserId(), userinfo.getUid()), new SimpleRequestCallBack<BaseApiEntity<String>>() {
            @Override
            public void onSuccess(BaseApiEntity<String> resultCode) {
                attent.setText(R.string.person_attention_already);
                CustomToast.getInstance().showToast(R.string.person_attention_success);
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
            }
        });
    }

    private void cancelAttention() {
        RequestClient.requestAsync(new CancelAttentionRequest(AccountManager.getInstance().getUserId(), userinfo.getUid()), new SimpleRequestCallBack<BaseApiEntity<String>>() {
            @Override
            public void onSuccess(BaseApiEntity<String> resultCode) {
                attent.setText(R.string.person_attention);
                CustomToast.getInstance().showToast(R.string.person_attention_cancel_success);
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (needPop) {
            SocialManager.getInstance().popFriendId();
        }
    }
}
