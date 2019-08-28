package com.iyuba.music.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.entity.user.MostDetailInfo;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.merequest.UserInfoDetailRequest;
import com.iyuba.music.widget.CustomToast;


public class UserDetailInfoActivity extends BaseActivity {
    private TextView tvUserName, tvGender, tvResideLocation, tvBirthday,
            tvConstellation, tvZodiac, tvGraduatesSchool, tvCompany,
            tvAffectivestatus, tvLookingfor, tvIntro, tvInterest;
    private boolean needPop;
    private MostDetailInfo userDetailInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_detail);
        context = this;
        needPop = getIntent().getBooleanExtra("needpop", false);
        initWidget();
        setListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        changeUIByPara();
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
    protected void initWidget() {
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
    protected void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, EditUserDetailInfoActivity.class));
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.person_detail_title);
        toolbarOper.setText(R.string.person_detail_edit);
        if (!AccountManager.getInstance().getUserId().equals(SocialManager.getInstance().getFriendId())) {
            toolbarOper.setVisibility(View.GONE);
        }
        UserInfoDetailRequest.exeRequest(UserInfoDetailRequest.generateUrl(SocialManager.getInstance().getFriendId()), new IProtocolResponse<MostDetailInfo>() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg);
                finish();
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg);
                finish();
            }

            @Override
            public void response(MostDetailInfo result) {
                userDetailInfo = result;
                userDetailInfo.format(context, userDetailInfo);
                setText();
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
