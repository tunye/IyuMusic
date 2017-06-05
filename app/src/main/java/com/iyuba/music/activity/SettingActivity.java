package com.iyuba.music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.buaa.ct.skin.SkinManager;
import com.iyuba.music.R;
import com.iyuba.music.activity.discover.WordSetActivity;
import com.iyuba.music.activity.study.StudySetActivity;
import com.iyuba.music.adapter.MaterialDialogAdapter;
import com.iyuba.music.file.FileUtil;
import com.iyuba.music.fragment.StartFragment;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.receiver.ChangePropertyBroadcast;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.ThreadPoolUtil;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.iyuba.music.widget.recycleview.MyLinearLayoutManager;
import com.iyuba.music.widget.roundview.RoundRelativeLayout;
import com.iyuba.music.widget.roundview.RoundTextView;
import com.iyuba.music.widget.view.AddRippleEffect;
import com.kyleduo.switchbutton.SwitchButton;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.io.File;
import java.util.Arrays;

/**
 * Created by 10202 on 2015/11/26.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private RoundRelativeLayout feedback, helpUse, wordSet, studySet, share, skin, versionFeature, moreApp;
    private RoundRelativeLayout language, push, clearPic, clearAudio;
    private TextView currLanguage, picCache, audioCache, currSkin;
    private SwitchButton currPush;
    private RoundTextView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
        context = this;
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
        clearPic = (RoundRelativeLayout) findViewById(R.id.setting_pic_clear);
        AddRippleEffect.addRippleEffect(clearPic);
        clearAudio = (RoundRelativeLayout) findViewById(R.id.setting_audio_clear);
        AddRippleEffect.addRippleEffect(clearAudio);
        picCache = (TextView) findViewById(R.id.setting_pic_cache);
        audioCache = (TextView) findViewById(R.id.setting_audio_cache);
        currLanguage = (TextView) findViewById(R.id.setting_curr_language);
        currSkin = (TextView) findViewById(R.id.setting_curr_skin);
        currPush = (SwitchButton) findViewById(R.id.setting_curr_push);
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
        clearPic.setOnClickListener(this);
        clearAudio.setOnClickListener(this);
        currSkin.setOnClickListener(this);
        logout.setOnClickListener(this);
        share.setOnClickListener(this);
        moreApp.setOnClickListener(this);
        currPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setPushState();
                if (isChecked) {
                    currPush.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
                    CustomToast.getInstance().showToast(R.string.setting_push_on);
                } else {
                    currPush.setBackColorRes(R.color.background_light);
                    CustomToast.getInstance().showToast(R.string.setting_push_off);
                }
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.setting_title);
        if (ConfigManager.getInstance().isPush()) {
            currPush.setCheckedImmediatelyNoEvent(true);
            currPush.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
        } else {
            currPush.setCheckedImmediatelyNoEvent(false);
            currPush.setBackColorRes(R.color.background_light);
        }
        handler.sendEmptyMessage(1);
    }

    protected void changeUIResumeByPara() {
        currLanguage.setText(getLanguage(ConfigManager.getInstance().getLanguage()));
        currSkin.setText(getSkin(SkinManager.getInstance().getCurrSkin()));
    }

    @Override
    public void onBackPressed() {
        if (changeProperty) {
            startActivity(new Intent(context, MainActivity.class));
        } else {
            super.onBackPressed();
        }
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
                        ConstantManager.appName)
                        + "：http://app.iyuba.com/android/androidDetail.jsp?id="
                        + ConstantManager.appId;
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
                Intent intent = new Intent(context, HelpUseActivity.class);
                intent.putExtra("UsePullDown", true);
                startActivity(intent);
                break;
            case R.id.setting_language:
            case R.id.setting_curr_language:
                popLanguageDialog();
                break;
            case R.id.setting_push:
                currPush.setChecked(!currPush.isChecked());
                setPushState();
                break;
            case R.id.setting_audio_clear:
                CustomDialog.clearDownload(context, R.string.article_clear_cache_hint, new IOperationResult() {
                    @Override
                    public void success(Object object) {
                        ThreadPoolUtil.getInstance().execute(new Runnable() {
                            @Override
                            public void run() {
                                FileUtil.clearFileDir(RuntimeManager.getApplication().getProxy().getCacheFolder());
                            }
                        });
                        handler.sendEmptyMessage(3);
                    }

                    @Override
                    public void fail(Object object) {

                    }
                });
                break;
            case R.id.setting_pic_clear:
                ImageUtil.clearImageAllCache(this);
                ThreadPoolUtil.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        FileUtil.clearFileDir(new File(ConstantManager.crashFolder));
                        FileUtil.clearFileDir(new File(ConstantManager.updateFolder));
                        FileUtil.clearFileDir(new File(ConstantManager.imgFile));
                    }
                });
                handler.sendEmptyMessage(2);
                break;
            case R.id.setting_word_set:
                startActivity(new Intent(context, WordSetActivity.class));
                break;
            case R.id.setting_study_set:
                startActivity(new Intent(context, StudySetActivity.class));
                break;
            case R.id.setting_more_app:
                intent = new Intent(context, WebViewActivity.class);
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
        ConfigManager.getInstance().setPush(!ConfigManager.getInstance().isPush());
        if (ConfigManager.getInstance().isPush()) {
            MiPushClient.enablePush(context);
        } else {
            MiPushClient.disablePush(context);
        }
    }

    private void popLanguageDialog() {
        final MyMaterialDialog languageDialog = new MyMaterialDialog(context);
        languageDialog.setTitle(R.string.setting_language_hint);
        View root = View.inflate(context, R.layout.recycleview, null);
        RecyclerView languageList = (RecyclerView) root.findViewById(R.id.listview);
        MaterialDialogAdapter adapter = new MaterialDialogAdapter(context, Arrays.asList(context.getResources().getStringArray(R.array.language)));
        adapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (ConfigManager.getInstance().getLanguage() != position) {
                    onLanguageChanged(position);
                }
                languageDialog.dismiss();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        adapter.setSelected(ConfigManager.getInstance().getLanguage());
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
        ConfigManager.getInstance().setLanguage(language);
        ChangePropery.updateLanguageMode(language);
        Intent intent = new Intent(ChangePropertyBroadcast.FLAG);
        intent.putExtra(ChangePropertyBroadcast.SOURCE, this.getClass().getSimpleName());
        sendBroadcast(intent);
    }

    private String getLanguage(int language) {
        String[] languages = context.getResources().getStringArray(R.array.language);
        return languages[language];
    }

    private String getSkin(String skin) {
        int pos = GetAppColor.getInstance().getSkinFlg(skin);
        String[] skins = context.getResources().getStringArray(R.array.flavors);
        return skins[pos];
    }

    private void logout() {
        final MyMaterialDialog mMaterialDialog = new MyMaterialDialog(context);
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
                    activity.audioCache.setText(FileUtil.formetFileSize(FileUtil.getFolderSize(RuntimeManager.getApplication().getProxy().getCacheFolder())));
                    long size = 0;
                    size += FileUtil.getGlideCacheSize(activity);
                    size += FileUtil.getFolderSize(new File(ConstantManager.crashFolder));
                    size += FileUtil.getFolderSize(new File(ConstantManager.updateFolder));
                    size += FileUtil.getFolderSize(new File(ConstantManager.imgFile));
                    activity.picCache.setText(FileUtil.formetFileSize(size));
                    break;
                case 2:
                    activity.picCache.setText("0B");
                    break;
                case 3:
                    activity.audioCache.setText("0B");
                    break;
            }
        }
    }
}
