package com.iyuba.music.activity.me;

/**
 * 编辑个人信息界面
 *
 * @author chentong
 * @version 1.0
 */

import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.util.LocationUtil;
import com.buaa.ct.core.view.CustomToast;
import com.buaa.ct.core.view.image.CircleImageView;
import com.buaa.ct.core.view.image.DividerItemDecoration;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.adapter.MaterialDialogAdapter;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.MostDetailInfo;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.request.merequest.EditUserInfoRequest;
import com.iyuba.music.request.merequest.UserInfoDetailRequest;
import com.iyuba.music.util.AppImageUtil;
import com.iyuba.music.util.JudgeZodicaAndConstellation;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.iyuba.music.widget.recycleview.MyLinearLayoutManager;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class EditUserDetailInfoActivity extends BaseActivity {
    private TextView gender, birthday, zodiac, constellation;
    private MaterialEditText location, school, company, affectiveStatus, lookingFor, bio, interest;
    private View changeImageLayout;
    private CircleImageView userImage;
    private MostDetailInfo editUserInfo;
    private IyubaDialog waitingDialog;

    @Override
    public int getLayoutId() {
        return R.layout.edit_user_info;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        toolbarOper = findViewById(R.id.toolbar_oper);
        userImage = findViewById(R.id.iveditPortrait);
        gender = findViewById(R.id.editGender);
        birthday = findViewById(R.id.editBirthday);
        location = findViewById(R.id.editResideLocation);
        zodiac = findViewById(R.id.editZodiac);
        constellation = findViewById(R.id.editConstellation);
        changeImageLayout = findViewById(R.id.editPortrait);
        school = findViewById(R.id.editSchool);
        company = findViewById(R.id.editCompany);
        affectiveStatus = findViewById(R.id.editAffectiveStatus);
        lookingFor = findViewById(R.id.editLookingFor);
        bio = findViewById(R.id.editBio);
        interest = findViewById(R.id.editInterest);

        waitingDialog = WaitingDialog.create(context, context.getString(R.string.person_detail_loading));
    }

    private void setText() {
        if (!TextUtils.isEmpty(editUserInfo.getGender())) {
            switch (editUserInfo.getGender()) {
                case "1":
                    gender.setText(R.string.person_detail_sex_man);
                    break;
                case "2":
                    gender.setText(R.string.person_detail_sex_woman);
                    break;
                default:
                    gender.setText(R.string.person_detail_sex_undefined);
                    break;
            }
        } else {
            gender.setText(R.string.person_detail_sex_undefined);
        }
        birthday.setText(editUserInfo.getBirthday());
        zodiac.setText(editUserInfo.getZodiac());
        constellation.setText(editUserInfo.getConstellation());
        if (!TextUtils.isEmpty(editUserInfo.getResideLocation())) {
            location.setText(editUserInfo.getResideLocation());
        }
        school.setText(editUserInfo.getGraduateschool());
        company.setText(editUserInfo.getCompany());
        affectiveStatus.setText(editUserInfo.getAffectivestatus());
        lookingFor.setText(editUserInfo.getLookingfor());
        bio.setText(editUserInfo.getBio());
        interest.setText(editUserInfo.getInterest());
        AppImageUtil.loadAvatar(AccountManager.getInstance().getUserId(), userImage);
    }

    @Override
    public void setListener() {
        super.setListener();
        changeImageLayout.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {

                Intent intent = new Intent(context, ChangePhotoActivity.class);
                startActivity(intent);
            }
        });

        gender.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                popGenderDialog();
            }
        });
        birthday.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                popBirthDialog();
            }
        });
        toolbarOper.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                toolbarOper.setClickable(false);
                String city = location.getText().toString();
                city = city.trim();
                String value, key;
                StringBuilder sb = new StringBuilder();
                int i;
                String[] dates = editUserInfo.getBirthday().split("-");
                if (city.contains(" ")) {
                    String[] area = city.split(" ");
                    sb.append(editUserInfo.getGender()).append(",");
                    sb.append(dates[0]).append(",");
                    sb.append(dates[1]).append(",");
                    sb.append(dates[2]).append(",");
                    sb.append(editUserInfo.getConstellation()).append(",");
                    sb.append(editUserInfo.getZodiac()).append(",");
                    sb.append(school.getText()).append(",");
                    for (i = 0; i < area.length; i++) {
                        sb.append(area[i]).append(",");
                    }
                    sb.append(affectiveStatus.getText()).append(",");
                    sb.append(lookingFor.getText()).append(",");
                    sb.append(bio.getText()).append(",");
                    sb.append(interest.getText()).append(",");
                    sb.append(company.getText());
                    value = sb.toString();
                    if (i == 3) {
                        key = "gender,birthyear,birthmonth,birthday,constellation,zodiac,graduateschool,resideprovince,residecity,residedist,affectivestatus,lookingfor,bio,interest,company";
                    } else {
                        key = "gender,birthyear,birthmonth,birthday,constellation,zodiac,graduateschool,resideprovince,residecity,affectivestatus,lookingfor,bio,interest,company";
                    }
                } else {
                    sb.append(editUserInfo.getGender()).append(",");
                    sb.append(dates[0]).append(",");
                    sb.append(dates[1]).append(",");
                    sb.append(dates[2]).append(",");
                    sb.append(editUserInfo.getConstellation()).append(",");
                    sb.append(editUserInfo.getZodiac()).append(",");
                    sb.append(school.getText()).append(",");
                    sb.append(city).append(",");
                    sb.append(affectiveStatus.getText()).append(",");
                    sb.append(lookingFor.getText()).append(",");
                    sb.append(bio.getText()).append(",");
                    sb.append(interest.getText()).append(",");
                    sb.append(company.getText());
                    value = sb.toString();
                    key = "gender,birthyear,birthmonth,birthday,constellation,zodiac,graduateschool,residecity,affectivestatus,lookingfor,bio,interest,company";
                }
                RequestClient.requestAsync(new EditUserInfoRequest(AccountManager.getInstance().getUserId(), key, value), new SimpleRequestCallBack<BaseApiEntity<String>>() {
                    @Override
                    public void onSuccess(BaseApiEntity<String> result) {
                        toolbarOper.setClickable(true);
                        CustomToast.getInstance().showToast(R.string.person_detail_success);
                        EditUserDetailInfoActivity.this.finish();
                    }

                    @Override
                    public void onError(ErrorInfoWrapper errorInfoWrapper) {
                        toolbarOper.setClickable(true);
                        CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
                    }
                });
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText(R.string.person_detail_title);
        enableToolbarOper(R.string.person_detail_submit);
        waitingDialog.show();
        getAddr();
    }

    private void getAddr() {
        double latitude = AccountManager.getInstance().getLatitude();
        double longitude = AccountManager.getInstance().getLongitude();
        if (latitude == 0.0 && longitude == 0.0) {
            getDetaiInfo();
        } else {
            LocationUtil.getCurLocation(new LocationUtil.OnLocationListener() {
                @Override
                public void getlocation(Location result) {
                    Address curAddress = LocationUtil.getCurRegion(result);
                    location.setText(curAddress.getAdminArea() + " " + curAddress.getSubAdminArea() + " " + curAddress.getLocality());
                    getDetaiInfo();
                }
            });
        }
    }

    private void getDetaiInfo() {
        RequestClient.requestAsync(new UserInfoDetailRequest(AccountManager.getInstance().getUserId()), new SimpleRequestCallBack<MostDetailInfo>() {
            @Override
            public void onSuccess(MostDetailInfo mostDetailInfo) {
                editUserInfo = mostDetailInfo;
                waitingDialog.dismiss();
                setText();
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                waitingDialog.dismiss();
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
            }
        });
    }

    private void popGenderDialog() {
        final MyMaterialDialog genderDialog = new MyMaterialDialog(context);
        genderDialog.setTitle(R.string.person_detail_sex);
        View root = View.inflate(context, R.layout.recycleview, null);
        RecyclerView languageList = (RecyclerView) root.findViewById(R.id.listview);
        MaterialDialogAdapter adapter = new MaterialDialogAdapter(context, Arrays.asList(context.getResources().getStringArray(R.array.gender)));
        adapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == 1) {
                    editUserInfo.setGender("2");
                    gender.setText(context.getString(R.string.person_detail_sex_woman));
                } else if (position == 0) {
                    editUserInfo.setGender("1");
                    gender.setText(context.getString(R.string.person_detail_sex_man));
                }
                genderDialog.dismiss();
            }
        });
        adapter.setSelected(0);
        languageList.setAdapter(adapter);
        languageList.setLayoutManager(new MyLinearLayoutManager(context));
        languageList.addItemDecoration(new DividerItemDecoration());
        genderDialog.setContentView(root);
        genderDialog.setPositiveButton(R.string.app_cancel, new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                genderDialog.dismiss();
            }
        });
        genderDialog.show();
    }

    private void popBirthDialog() {
        Calendar calendar = Calendar.getInstance();
        String[] dates;
        if (TextUtils.isEmpty(editUserInfo.getBirthday()) || !editUserInfo.getBirthday().contains("-")) {
            dates = new String[]{String.valueOf(calendar.get(Calendar.YEAR)), String.valueOf(calendar.get(Calendar.MONTH) + 1), String.valueOf(calendar.get(Calendar.DATE))};
        } else {
            dates = editUserInfo.getBirthday().split("-");
        }
        DatePickerDialog dialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker dp, int year,
                                          int month, int dayOfMonth) {
                        editUserInfo.setBirthday(year + "-" + (month + 1) + "-" + dayOfMonth);
                        Calendar cal = new GregorianCalendar(year, month,
                                dayOfMonth);
                        String constellation = JudgeZodicaAndConstellation
                                .date2Constellation(cal);
                        String zodiac = JudgeZodicaAndConstellation
                                .date2Zodica(cal);
                        editUserInfo.setZodiac(zodiac);
                        editUserInfo.setConstellation(constellation);
                        editUserInfo.setBirthday(year + "-" + (month + 1)
                                + "-" + dayOfMonth);
                        setText();
                    }
                }, Integer.parseInt(dates[0]), // 传入年份
                Integer.parseInt(dates[1]) - 1, // 传入月份
                Integer.parseInt(dates[2]) // 传入天数
        );
        dialog.setTitle(R.string.person_detail_birth);
        dialog.show();
    }
}
