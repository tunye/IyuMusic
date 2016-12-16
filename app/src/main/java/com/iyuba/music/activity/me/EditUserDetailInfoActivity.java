package com.iyuba.music.activity.me;

/**
 * 编辑个人信息界面
 *
 * @author chentong
 * @version 1.0
 */

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenu;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialripple.MaterialRippleLayout;
import com.buaa.ct.skin.BaseSkinActivity;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.adapter.MaterialDialogAdapter;
import com.iyuba.music.entity.user.MostDetailInfo;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.request.merequest.EditUserInfoRequest;
import com.iyuba.music.request.merequest.LocateRequest;
import com.iyuba.music.request.merequest.UserInfoDetailRequest;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.JudgeZodicaAndConstellation;
import com.iyuba.music.util.LocationUtil;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.Dialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.iyuba.music.widget.recycleview.MyLinearLayoutManager;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.hdodenhof.circleimageview.CircleImageView;
import me.drakeet.materialdialog.MaterialDialog;

public class EditUserDetailInfoActivity extends BaseSkinActivity {
    protected Context context;
    protected MaterialRippleLayout back;
    protected MaterialMenu backIcon;
    protected TextView title, toolbarOper;
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private TextView gender, birthday, zodiac, constellation;
    private MaterialEditText location, school, company, affectiveStatus, lookingFor, bio, interest;
    private View changeImageLayout;
    private CircleImageView userImage;
    private MostDetailInfo editUserInfo;
    private Dialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(GetAppColor.instance.getAppColor(this));
            getWindow().setNavigationBarColor(GetAppColor.instance.getAppColor(this));
        }
        setContentView(R.layout.edit_user_info);
        context = this;
        waitingDialog = new WaitingDialog.Builder(context).setMessage(context.getString(R.string.person_detail_loading)).create();
        initWidget();
        setListener();
        changeUIByPara();
        ((MusicApplication) getApplication()).pushActivity(this);
    }

    protected void initWidget() {
        back = (MaterialRippleLayout) findViewById(R.id.back);
        backIcon = (MaterialMenu) findViewById(R.id.back_material);
        title = (TextView) findViewById(R.id.toolbar_title);
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        userImage = (CircleImageView) findViewById(R.id.iveditPortrait);
        gender = (TextView) findViewById(R.id.editGender);
        birthday = (TextView) findViewById(R.id.editBirthday);
        location = (MaterialEditText) findViewById(R.id.editResideLocation);
        zodiac = (TextView) findViewById(R.id.editZodiac);
        constellation = (TextView) findViewById(R.id.editConstellation);
        changeImageLayout = findViewById(R.id.editPortrait);
        school = (MaterialEditText) findViewById(R.id.editSchool);
        company = (MaterialEditText) findViewById(R.id.editCompany);
        affectiveStatus = (MaterialEditText) findViewById(R.id.editAffectiveStatus);
        lookingFor = (MaterialEditText) findViewById(R.id.editLookingFor);
        bio = (MaterialEditText) findViewById(R.id.editBio);
        interest = (MaterialEditText) findViewById(R.id.editInterest);
    }

    private void setText() {

        if (!TextUtils.isEmpty(editUserInfo.getGender())) {
            if (editUserInfo.getGender().equals("1")) {
                gender.setText(R.string.person_detail_sex_man);
            } else if (editUserInfo.getGender().equals("2")) {
                gender.setText(R.string.person_detail_sex_woman);
            } else {
                gender.setText(R.string.person_detail_sex_undefined);
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
        ImageUtil.loadAvatar(AccountManager.instance.getUserId(), userImage);
    }

    protected void setListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
                EditUserInfoRequest.getInstance().exeRequest(EditUserInfoRequest.getInstance().
                        generateUrl(AccountManager.instance.getUserId(), key, value), new IProtocolResponse() {
                    @Override
                    public void onNetError(String msg) {
                        CustomToast.INSTANCE.showToast(msg);
                    }

                    @Override
                    public void onServerError(String msg) {
                        CustomToast.INSTANCE.showToast(msg);
                    }

                    @Override
                    public void response(Object object) {
                        String result = object.toString();
                        toolbarOper.setClickable(true);
                        if (result.equals("221")) {
                            CustomToast.INSTANCE.showToast(R.string.person_detail_success);
                            EditUserDetailInfoActivity.this.finish();
                        } else {
                            CustomToast.INSTANCE.showToast(R.string.person_detail_fail);
                        }
                    }
                });
            }
        });
    }

    protected void changeUIByPara() {
        backIcon.setState(MaterialMenuDrawable.IconState.ARROW);
        title.setText(R.string.person_detail_title);
        toolbarOper.setText(R.string.person_detail_submit);
        handler.sendEmptyMessage(0);
    }

    private void getAddr() {
        double latitude = LocationUtil.getInstance().getLatitude();
        double longitude = LocationUtil.getInstance().getLongitude();
        if (latitude == 0.0 && longitude == 0.0) {
            getDetaiInfo();
        } else {
            LocateRequest.getInstance().exeRequest(LocateRequest.getInstance().generateUrl(latitude, longitude), new IProtocolResponse() {
                @Override
                public void onNetError(String msg) {
                    CustomToast.INSTANCE.showToast(msg);
                    waitingDialog.dismiss();
                }

                @Override
                public void onServerError(String msg) {
                    CustomToast.INSTANCE.showToast(msg);
                    waitingDialog.dismiss();
                }

                @Override
                public void response(Object object) {
                    location.setText(object.toString().trim());
                    getDetaiInfo();
                }
            });
        }
    }

    private void getDetaiInfo() {
        UserInfoDetailRequest.getInstance().exeRequest(UserInfoDetailRequest.getInstance().
                generateUrl(AccountManager.instance.getUserId()), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.INSTANCE.showToast(msg);
                waitingDialog.dismiss();
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.INSTANCE.showToast(msg);
                waitingDialog.dismiss();
            }

            @Override
            public void response(Object object) {
                editUserInfo = (MostDetailInfo) object;
                handler.sendEmptyMessage(1);
            }
        });
    }

    private void popGenderDialog() {
        final MaterialDialog genderDialog = new MaterialDialog(context);
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
        adapter.setSelected(SettingConfigManager.instance.getLanguage());
        languageList.setAdapter(adapter);
        languageList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
        languageList.setLayoutManager(new MyLinearLayoutManager(context));
        genderDialog.setContentView(root);
        genderDialog.setPositiveButton(R.string.cancel, new View.OnClickListener() {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((MusicApplication) getApplication()).popActivity(this);
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
