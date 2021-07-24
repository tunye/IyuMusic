package com.iyuba.music.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.entity.user.MostDetailInfo;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.merequest.UserInfoDetailRequest;
import com.iyuba.music.util.Utils;


public class UserDetailInfoActivity extends BaseActivity {
    private TextView tvUserName, tvGender, tvResideLocation, tvBirthday,
            tvConstellation, tvZodiac, tvGraduatesSchool, tvCompany,
            tvAffectivestatus, tvLookingfor, tvIntro, tvInterest;
    private boolean needPop;
    private MostDetailInfo userDetailInfo;

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        needPop = getIntent().getBooleanExtra("needpop", false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.userinfo_detail;
    }

    private void setText() {

        tvUserName.setText(SocialManager.getInstance().getFriendName());
        switch (userDetailInfo.gender) {
            case "0":
                tvGender.setText(context.getString(R.string.person_detail_sex_undefined));
                break;
            case "1":
                tvGender.setText(context.getString(R.string.person_detail_sex_man));
                break;
            case "2":
                tvGender.setText(context.getString(R.string.person_detail_sex_woman));
                break;
        }
        tvResideLocation.setText(userDetailInfo.getResideLocation());
        tvBirthday.setText(userDetailInfo.getBirthday());
        tvConstellation.setText(userDetailInfo.getConstellation());
        tvZodiac.setText(userDetailInfo.getZodiac());
        tvGraduatesSchool.setText(userDetailInfo.getGraduateschool());
        tvCompany.setText(userDetailInfo.getCompany());
        tvAffectivestatus.setText(userDetailInfo.getAffectivestatus());
        tvLookingfor.setText(userDetailInfo.getLookingfor());
        tvIntro.setText(userDetailInfo.getBio());
        tvInterest.setText(userDetailInfo.getInterest());
    }

    @Override
    public void initWidget() {
        super.initWidget();
        toolbarOper = findViewById(R.id.toolbar_oper);
        tvUserName = findViewById(R.id.tvUserName);
        tvGender = findViewById(R.id.tvGender);
        tvResideLocation = findViewById(R.id.tvResideLocation);
        tvBirthday = findViewById(R.id.tvBirthday);
        tvConstellation = findViewById(R.id.tvConstellation);
        tvZodiac = findViewById(R.id.tvZodiac);
        tvGraduatesSchool = findViewById(R.id.tvGraduatesSchool);
        tvCompany = findViewById(R.id.tvCompany);
        tvAffectivestatus = findViewById(R.id.tvAffectivestatus);
        tvLookingfor = findViewById(R.id.tvLookingfor);
        tvIntro = findViewById(R.id.tvBio);
        tvInterest = findViewById(R.id.tvInterest);
    }

    @Override
    public void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                startActivity(new Intent(context, EditUserDetailInfoActivity.class));
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText(R.string.person_detail_title);
        enableToolbarOper(R.string.person_detail_edit);
        if (!AccountManager.getInstance().getUserId().equals(SocialManager.getInstance().getFriendId())) {
            toolbarOper.setVisibility(View.GONE);
        }
        RequestClient.requestAsync(new UserInfoDetailRequest(SocialManager.getInstance().getFriendId()), new SimpleRequestCallBack<MostDetailInfo>() {
            @Override
            public void onSuccess(MostDetailInfo mostDetailInfo) {
                userDetailInfo = mostDetailInfo;
                userDetailInfo.format(context, userDetailInfo);
                setText();
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
                finish();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (needPop) {
            SocialManager.getInstance().popFriendId();
            SocialManager.getInstance().popFriendName();
        }
    }
}
