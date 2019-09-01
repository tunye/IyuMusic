package com.iyuba.music.activity.me;

/**
 * 编辑个人信息界面
 *
 * @author chentong
 * @version 1.0
 */

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.adapter.MaterialDialogAdapter;
import com.iyuba.music.entity.user.MostDetailInfo;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.request.apprequest.LocateRequest;
import com.iyuba.music.request.merequest.EditUserInfoRequest;
import com.iyuba.music.request.merequest.UserInfoDetailRequest;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.JudgeZodicaAndConstellation;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.iyuba.music.widget.recycleview.MyLinearLayoutManager;
import com.iyuba.music.widget.imageview.CircleImageView;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class EditUserDetailInfoActivity extends BaseActivity {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private TextView gender, birthday, zodiac, constellation;
    private MaterialEditText location, school, company, affectiveStatus, lookingFor, bio, interest;
    private View changeImageLayout;
    private CircleImageView userImage;
    private MostDetailInfo editUserInfo;
    private IyubaDialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_info);
        waitingDialog = WaitingDialog.create(context, context.getString(R.string.person_detail_loading));
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
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
        ImageUtil.loadAvatar(AccountManager.getInstance().getUserId(), userImage);
    }

    @Override
    protected void setListener() {
        super.setListener();
        changeImageLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, ChangePhotoActivity.class);
                startActivity(intent);
            }
        });

        gender.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popGenderDialog();
            }
        });
        birthday.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                popBirthDialog();
            }
        });
        toolbarOper.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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
                EditUserInfoRequest.exeRequest(EditUserInfoRequest.generateUrl(AccountManager.getInstance().getUserId(), key, value), new IProtocolResponse<String>() {
                    @Override
                    public void onNetError(String msg) {
                        CustomToast.getInstance().showToast(msg);
                    }

                    @Override
                    public void onServerError(String msg) {
                        CustomToast.getInstance().showToast(msg);
                    }

                    @Override
                    public void response(String result) {
                        toolbarOper.setClickable(true);
                        if (result.equals("221")) {
                            CustomToast.getInstance().showToast(R.string.person_detail_success);
                            EditUserDetailInfoActivity.this.finish();
                        } else {
                            CustomToast.getInstance().showToast(R.string.person_detail_fail);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.person_detail_title);
        toolbarOper.setText(R.string.person_detail_submit);
        handler.sendEmptyMessage(0);
    }

    private void getAddr() {
        double latitude = AccountManager.getInstance().getLatitude();
        double longitude = AccountManager.getInstance().getLongitude();
        if (latitude == 0.0 && longitude == 0.0) {
            getDetaiInfo();
        } else {
            LocateRequest.exeRequest(LocateRequest.generateUrl(latitude, longitude), new IProtocolResponse<String>() {
                @Override
                public void onNetError(String msg) {
                    CustomToast.getInstance().showToast(msg);
                    waitingDialog.dismiss();
                }

                @Override
                public void onServerError(String msg) {
                    CustomToast.getInstance().showToast(msg);
                    waitingDialog.dismiss();
                }

                @Override
                public void response(String result) {
                    location.setText(result.trim());
                    getDetaiInfo();
                }
            });
        }
    }

    private void getDetaiInfo() {
        UserInfoDetailRequest.exeRequest(UserInfoDetailRequest.generateUrl(AccountManager.getInstance().getUserId()), new IProtocolResponse<MostDetailInfo>() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg);
                waitingDialog.dismiss();
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg);
                waitingDialog.dismiss();
            }

            @Override
            public void response(MostDetailInfo result) {
                editUserInfo = result;
                handler.sendEmptyMessage(1);
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

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        adapter.setSelected(0);
        languageList.setAdapter(adapter);
        languageList.setLayoutManager(new MyLinearLayoutManager(context));
        languageList.addItemDecoration(new DividerItemDecoration());
        genderDialog.setContentView(root);
        genderDialog.setPositiveButton(R.string.app_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<EditUserDetailInfoActivity> {
        @Override
        public void handleMessageByRef(final EditUserDetailInfoActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    activity.waitingDialog.show();
                    activity.getAddr();
                    break;
                case 1:
                    activity.waitingDialog.dismiss();
                    activity.setText();
                    break;
            }
        }
    }
}
