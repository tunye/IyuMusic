package com.iyuba.music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.buaa.ct.skin.SkinManager;
import com.flyco.roundview.RoundRelativeLayout;
import com.flyco.roundview.RoundTextView;
import com.iyuba.music.R;
import com.iyuba.music.activity.discover.WordSetActivity;
import com.iyuba.music.activity.study.StudySetActivity;
import com.iyuba.music.adapter.MaterialDialogAdapter;
import com.iyuba.music.file.FileUtil;
import com.iyuba.music.fragment.StartFragment;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.iyuba.music.widget.recycleview.MyLinearLayoutManager;
import com.iyuba.music.widget.view.AddRippleEffect;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.io.File;
import java.util.Arrays;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 10202 on 2015/11/26.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    IyubaDialog waittingDialog;
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private RoundRelativeLayout feedback, helpUse, wordSet, studySet, share, skin, versionFeature, moreApp;
    private RoundRelativeLayout language, push, clear;
    private TextView currLanguage, currClear, currSkin;
    private CheckBox currPush;
    private RoundTextView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        context = this;
        waittingDialog = WaitingDialog.create(context, context.getString(R.string.setting_clearing));
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        share = (RoundRelativeLayout) findViewById(R.id.setting_share);
        AddRippleEffect.addRippleEffect(share);
        skin = (RoundRelativeLayout) findViewById(R.id.setting_skin);
        AddRippleEffect.addRippleEffect(skin);
        versionFeature = (RoundRelativeLayout) findViewById(R.id.setting_version_feature);
        AddRippleEffect.addRippleEffect(versionFeature);
        feedback = (RoundRelativeLayout) findViewById(R.id.setting_feedback);
        AddRippleEffect.addRippleEffect(feedback);
        moreApp = (RoundRelativeLayout) findViewById(R.id.setting_more_app);
        AddRippleEffect.addRippleEffect(moreApp);
        helpUse = (RoundRelativeLayout) findViewById(R.id.setting_help_use);
        AddRippleEffect.addRippleEffect(helpUse);
        wordSet = (RoundRelativeLayout) findViewById(R.id.setting_word_set);
        AddRippleEffect.addRippleEffect(wordSet);
        studySet = (RoundRelativeLayout) findViewById(R.id.setting_study_set);
        AddRippleEffect.addRippleEffect(studySet);
        language = (RoundRelativeLayout) findViewById(R.id.setting_language);
        AddRippleEffect.addRippleEffect(language);
        push = (RoundRelativeLayout) findViewById(R.id.setting_push);
        AddRippleEffect.addRippleEffect(push);
        clear = (RoundRelativeLayout) findViewById(R.id.setting_clear);
        AddRippleEffect.addRippleEffect(clear);
        currClear = (TextView) findViewById(R.id.setting_curr_clear);
        currLanguage = (TextView) findViewById(R.id.setting_curr_language);
        currSkin = (TextView) findViewById(R.id.setting_curr_skin);
        currPush = (CheckBox) findViewById(R.id.setting_curr_push);
        logout = (RoundTextView) findViewById(R.id.setting_logout);
        AddRippleEffect.addRippleEffect(logout);
    }

    @Override
    protected void setListener() {
        super.setListener();
        skin.setOnClickListener(this);
        versionFeature.setOnClickListener(this);
        feedback.setOnClickListener(this);
        helpUse.setOnClickListener(this);
        wordSet.setOnClickListener(this);
        studySet.setOnClickListener(this);
        language.setOnClickListener(this);
        currLanguage.setOnClickListener(this);
        push.setOnClickListener(this);
        clear.setOnClickListener(this);
        currPush.setOnClickListener(this);
        currSkin.setOnClickListener(this);
        logout.setOnClickListener(this);
        share.setOnClickListener(this);
        moreApp.setOnClickListener(this);
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.setting_title);
    }

    protected void changeUIResumeByPara() {
        currPush.setChecked(SettingConfigManager.getInstance().isPush());
        currLanguage.setText(getLanguage(SettingConfigManager.getInstance().getLanguage()));
        currSkin.setText(getSkin(SkinManager.getInstance().getCurrSkin()));
        handler.sendEmptyMessage(1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        changeUIResumeByPara();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_version_feature:
                StartFragment.showVersionFeature(context);
                break;
            case R.id.setting_skin:
            case R.id.setting_curr_skin:
                startActivity(new Intent(context, SkinActivity.class));
                break;
            case R.id.setting_share:
                String text = getResources().getString(R.string.setting_share_content,
                        ConstantManager.getInstance().getAppName())
                        + "：http://app.iyuba.com/android/androidDetail.jsp?id="
                        + ConstantManager.getInstance().getAppId();
                Intent shareInt = new Intent(Intent.ACTION_SEND);
                shareInt.setType("text/*");
                shareInt.putExtra(Intent.EXTRA_TEXT, text);
                shareInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareInt.putExtra("sms_body", text);
                startActivity(Intent.createChooser(shareInt, context.getString(R.string.setting_share_ways)));
                break;
            case R.id.setting_feedback:
                startActivity(new Intent(context, FeedbackActivity.class));
                break;
            case R.id.setting_help_use:
                startActivity(new Intent(context, HelpUseActivity.class));
                break;
            case R.id.setting_language:
            case R.id.setting_curr_language:
                popLanguageDialog();
                break;
            case R.id.setting_push:
                currPush.setChecked(!currPush.isChecked());
                setPushState();
                break;
            case R.id.setting_clear:
                waittingDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileUtil.clearAllCache(context);
                        FileUtil.clearFileDir(new File(ConstantManager.getInstance().getCrashFolder()));
                        FileUtil.clearFileDir(new File(ConstantManager.getInstance().getUpdateFolder()));
                        FileUtil.clearFileDir(new File(ConstantManager.getInstance().getImgFile()));
                        handler.sendEmptyMessage(2);
                    }
                }).start();
                break;
            case R.id.setting_curr_push:
                setPushState();
                break;
            case R.id.setting_word_set:
                startActivity(new Intent(context, WordSetActivity.class));
                break;
            case R.id.setting_study_set:
                startActivity(new Intent(context, StudySetActivity.class));
                break;
            case R.id.setting_more_app:
                Intent intent = new Intent();
                intent.setClass(context, WebViewActivity.class);
                intent.putExtra("url", "http://app.iyuba.com/android");
                intent.putExtra("title", context.getString(R.string.setting_moreapp));
                startActivity(intent);
                break;
            case R.id.setting_logout:
                logout();
                break;
        }
    }

    private void setPushState() {
        SettingConfigManager.getInstance().setPush(!SettingConfigManager.getInstance().isPush());
        if (SettingConfigManager.getInstance().isPush()) {
            MiPushClient.enablePush(context);
        } else {
            MiPushClient.disablePush(context);
        }
    }

    private void popLanguageDialog() {
        final MaterialDialog languageDialog = new MaterialDialog(context);
        languageDialog.setTitle(R.string.setting_language_hint);
        View root = View.inflate(context, R.layout.recycleview, null);
        RecyclerView languageList = (RecyclerView) root.findViewById(R.id.listview);
        MaterialDialogAdapter adapter = new MaterialDialogAdapter(context, Arrays.asList(context.getResources().getStringArray(R.array.language)));
        adapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                onLanguageChanged(position);
                languageDialog.dismiss();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        adapter.setSelected(SettingConfigManager.getInstance().getLanguage());
        languageList.setAdapter(adapter);
        languageList.setLayoutManager(new MyLinearLayoutManager(context));
        languageList.addItemDecoration(new DividerItemDecoration());
        languageDialog.setContentView(root);
        languageDialog.setPositiveButton(R.string.app_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                languageDialog.dismiss();
            }
        });
        languageDialog.show();
    }

    private void onLanguageChanged(int language) {
        SettingConfigManager.getInstance().setLanguage(language);
        ChangePropery.updateLanguageMode(language);
        LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(new Intent("changeProperty"));
    }

    private void onNightChanged() {
        SettingConfigManager.getInstance().setNight(!SettingConfigManager.getInstance().isNight());
        ChangePropery.updateNightMode(SettingConfigManager.getInstance().isNight());
        LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(new Intent("changeProperty"));
    }

    private String getLanguage(int language) {
        String[] languages = context.getResources().getStringArray(R.array.language);
        return languages[language];
    }

    private String getSkin(int skin) {
        String[] languages = context.getResources().getStringArray(R.array.flavors);
        return languages[skin];
    }

    private void logout() {
        final MaterialDialog mMaterialDialog = new MaterialDialog(context);
        mMaterialDialog.setTitle(R.string.app_name);
        if (AccountManager.getInstance().checkUserLogin()) {
            mMaterialDialog.setMessage(R.string.personal_logout_textmore)
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

        } else {
            mMaterialDialog.setMessage(R.string.personal_no_login)
                    .setPositiveButton(R.string.app_accept, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();
                        }
                    });
        }
        mMaterialDialog.show();
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<SettingActivity> {
        @Override
        public void handleMessageByRef(final SettingActivity activity, Message msg) {
            switch (msg.what) {
                case 1:
                    long size = 0;
                    size = size + FileUtil.getTotalCacheSize(activity);
                    size = size + FileUtil.getFolderSize(new File(
                            ConstantManager.getInstance().getCrashFolder()));
                    size = size + FileUtil.getFolderSize(new File(
                            ConstantManager.getInstance().getUpdateFolder()));
                    size = size + FileUtil.getFolderSize(new File(
                            ConstantManager.getInstance().getImgFile()));
                    activity.currClear.setText(FileUtil.formetFileSize(size));
                    break;
                case 2:
                    activity.waittingDialog.dismiss();
                    activity.handler.sendEmptyMessage(1);
                    break;
            }
        }
    }
}
