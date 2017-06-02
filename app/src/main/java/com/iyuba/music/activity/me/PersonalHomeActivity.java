package com.iyuba.music.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.MainActivity;
import com.iyuba.music.activity.eggshell.meizhi.MeizhiPhotoActivity;
import com.iyuba.music.adapter.me.DoingAdapter;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.doings.Doing;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.merequest.AddAttentionRequest;
import com.iyuba.music.request.merequest.CancelAttentionRequest;
import com.iyuba.music.request.merequest.DoingRequest;
import com.iyuba.music.request.merequest.PersonalInfoRequest;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.imageview.VipPhoto;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by 10202 on 2016/2/29.
 */
public class PersonalHomeActivity extends BaseActivity implements MySwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private ArrayList<Doing> doings;
    private DoingAdapter doingAdapter;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private int doingPage;
    private boolean isLastPage = false;
    private UserInfo userinfo;
    //上部
    private VipPhoto personPhoto;
    private ImageView personSex;
    private Button otherDetail, message, attent, detail, fix, credits;
    private View myControl, otherControl;
    private TextView personName, personAttention, personFans, personViews;
    private boolean needPop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_home);
        context = this;
        isLastPage = false;
        needPop = getIntent().getBooleanExtra("needpop", false);
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    public void onResume() {
        super.onResume();
        changeUIResumeByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        myControl = findViewById(R.id.my_oper);
        otherControl = findViewById(R.id.other_oper);
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        personPhoto = (VipPhoto) findViewById(R.id.personal_img);
        personSex = (ImageView) findViewById(R.id.name_sex);
        detail = (Button) findViewById(R.id.personal_detail);
        otherDetail = (Button) findViewById(R.id.personal_other_detail);
        message = (Button) findViewById(R.id.personal_message);
        fix = (Button) findViewById(R.id.personal_fix);
        credits = (Button) findViewById(R.id.personal_credit);
        personViews = (TextView) findViewById(R.id.personal_view);
        attent = (Button) findViewById(R.id.personal_attent);
        personName = (TextView) findViewById(R.id.name_text);
        personFans = (TextView) findViewById(R.id.fans_fans);
        personAttention = (TextView) findViewById(R.id.fans_attention);
        RecyclerView doingRecycleView = (RecyclerView) findViewById(R.id.personal_doingslist);
        swipeRefreshLayout = (MySwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setColorSchemeColors(0xff259CF7, 0xff2ABB51, 0xffE10000, 0xfffaaa3c);
        swipeRefreshLayout.setFirstIndex(0);
        swipeRefreshLayout.setOnRefreshListener(this);
        doingRecycleView.setLayoutManager(new LinearLayoutManager(context));
        doingAdapter = new DoingAdapter(context);
        doingAdapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SocialManager.getInstance().pushDoing(doings.get(position));
                Intent intent = new Intent(context, ReplyDoingActivity.class);
                intent.putExtra(ReplyDoingActivity.VIP_FLG, "1".equals(userinfo.getVipStatus()));
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        doingRecycleView.setAdapter(doingAdapter);
        doingRecycleView.addItemDecoration(new DividerItemDecoration());
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(this);
        personAttention.setOnClickListener(this);
        personFans.setOnClickListener(this);
        personPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SocialManager.getInstance().getFriendId().equals(AccountManager.getInstance().getUserId())) {
                    startActivity(new Intent(context, ChangePhotoActivity.class));
                } else {
                    Intent intent = new Intent(context, MeizhiPhotoActivity.class);
                    intent.putExtra("url", "http://api.iyuba.com.cn/v2/api.iyuba?protocol=10005&size=big&uid=" + SocialManager.getInstance().getFriendId());
                    context.startActivity(intent);
                }
            }
        });
        detail.setOnClickListener(this);
        message.setOnClickListener(this);
        attent.setOnClickListener(this);
        fix.setOnClickListener(this);
        otherDetail.setOnClickListener(this);
        credits.setOnClickListener(this);
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        toolbarOper.setText(R.string.personal_logout);
        title.setText(R.string.person_title);
    }

    protected void changeUIResumeByPara() {
        String tempUid = SocialManager.getInstance().getFriendId();
        if (tempUid.equals(AccountManager.getInstance().getUserId())) {//himself
            myControl.setVisibility(View.VISIBLE);
            otherControl.setVisibility(View.GONE);
            toolbarOper.setVisibility(View.VISIBLE);
            userinfo = AccountManager.getInstance().getUserInfo();
            setContent();
        } else {//other
            myControl.setVisibility(View.GONE);
            otherControl.setVisibility(View.VISIBLE);
            toolbarOper.setVisibility(View.GONE);
            userinfo = new UserInfo();
            userinfo.setUid(tempUid);
            PersonalInfoRequest.exeRequest(PersonalInfoRequest.generateUrl(tempUid, AccountManager.getInstance().getUserId()), userinfo
                    , new IProtocolResponse() {
                        @Override
                        public void onNetError(String msg) {
                            CustomToast.getInstance().showToast(msg);
                        }

                        @Override
                        public void onServerError(String msg) {
                            CustomToast.getInstance().showToast(msg);
                        }

                        @Override
                        public void response(Object object) {
                            BaseApiEntity baseApiEntity = (BaseApiEntity) object;
                            if (BaseApiEntity.isSuccess(baseApiEntity)) {
                                userinfo = (UserInfo) baseApiEntity.getData();
                                doingAdapter.setVip("1".equals(userinfo.getVipStatus()));
                                doingAdapter.notifyDataSetChanged();
                                setContent();
                                setRelationShip();
                            }
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

    /**
     * 下拉刷新
     *
     * @param index 当前分页索引
     */
    @Override
    public void onRefresh(int index) {
        doingPage = 1;
        doings = new ArrayList<>();
        isLastPage = false;
        getDoingData();
    }

    /**
     * 加载更多
     *
     * @param index 当前分页索引
     */
    @Override
    public void onLoad(int index) {
        if (doings.size() == 0) {

        } else if (!isLastPage) {
            doingPage++;
            getDoingData();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            CustomToast.getInstance().showToast(R.string.person_doings_load_all);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_oper:
                final MyMaterialDialog mMaterialDialog = new MyMaterialDialog(context);
                mMaterialDialog.setTitle(R.string.app_name)
                        .setMessage(R.string.personal_logout_textmore)
                        .setPositiveButton(R.string.personal_logout_exit, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();
                                AccountManager.getInstance().loginOut();
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mMaterialDialog.dismiss();
                            }
                        });
                mMaterialDialog.show();
                break;
            case R.id.personal_attent:
                String state = attent.getText().toString();
                if (state.equals(context.getString(R.string.person_attention))) {
                    addAttention();
                } else {
                    final MyMaterialDialog cancleAttentionDialog = new MyMaterialDialog(context);
                    cancleAttentionDialog.setTitle(R.string.app_name)
                            .setMessage(R.string.person_attention_cancel_hint)
                            .setPositiveButton(R.string.app_accept, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cancleAttentionDialog.dismiss();
                                    cancelAttention();
                                }
                            })
                            .setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    cancleAttentionDialog.dismiss();
                                }
                            });
                    cancleAttentionDialog.show();
                }
                break;
            case R.id.fans_attention:
                SocialManager.getInstance().pushFriendId(SocialManager.getInstance().getFriendId());
                Intent intent = new Intent(context, FriendCenter.class);
                intent.putExtra("type", "0");
                intent.putExtra("needPop", true);
                startActivity(intent);
                break;
            case R.id.fans_fans:
                SocialManager.getInstance().pushFriendId(SocialManager.getInstance().getFriendId());
                intent = new Intent(context, FriendCenter.class);
                intent.putExtra("type", "1");
                intent.putExtra("needPop", true);
                startActivity(intent);
                break;
            case R.id.personal_message:
                SocialManager.getInstance().pushFriendId(userinfo.getUid());
                SocialManager.getInstance().pushFriendName(userinfo.getUsername());
                intent = new Intent(context, ChattingActivity.class);
                intent.putExtra("needpop", true);
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

    private void getDoingData() {
        DoingRequest.exeRequest(DoingRequest.generateUrl(userinfo.getUid(), doingPage), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {

            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg);
            }

            @Override
            public void response(Object object) {
                BaseListEntity listEntity = (BaseListEntity) object;
                isLastPage = listEntity.isLastPage();
                if (isLastPage) {
                    if (doingPage == 1) {
                        findViewById(R.id.no_doing).setVisibility(View.VISIBLE);
                    } else {
                        CustomToast.getInstance().showToast(R.string.person_doings_load_all);
                    }
                } else {
                    findViewById(R.id.no_doing).setVisibility(View.GONE);
                    doings.addAll((ArrayList<Doing>) listEntity.getData());
                    doingAdapter.setDoingList(doings);
                    doingAdapter.setVip("1".equals(userinfo.getVipStatus()));
//                    if (doingPage == 1) {
//
//                    } else {
//                        CustomToast.getInstance().showToast(doingPage + "/" + listEntity.getTotalPage(), 800);
//                    }
                }
                swipeRefreshLayout.setRefreshing(false);
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
        AddAttentionRequest.exeRequest(AddAttentionRequest.generateUrl(AccountManager.getInstance().getUserId(), userinfo.getUid()), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {

            }

            @Override
            public void onServerError(String msg) {

            }

            @Override
            public void response(Object object) {
                if (object.toString().equals("500")) {
                    attent.setText(R.string.person_attention_already);
                    CustomToast.getInstance().showToast(R.string.person_attention_success);
                } else {
                    CustomToast.getInstance().showToast(R.string.person_attention_fail);
                }
            }
        });
    }

    private void cancelAttention() {
        CancelAttentionRequest.exeRequest(CancelAttentionRequest.generateUrl(AccountManager.getInstance().getUserId(), userinfo.getUid()), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {

            }

            @Override
            public void onServerError(String msg) {

            }

            @Override
            public void response(Object object) {
                if (object.toString().equals("510")) {
                    attent.setText(R.string.person_attention);
                    CustomToast.getInstance().showToast(R.string.person_attention_cancel_success);
                } else {
                    CustomToast.getInstance().showToast(R.string.person_attention_cancel_fail);
                }
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
