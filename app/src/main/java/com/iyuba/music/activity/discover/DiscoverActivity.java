package com.iyuba.music.activity.discover;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.CampaignActivity;
import com.iyuba.music.activity.WebViewActivity;
import com.iyuba.music.activity.eggshell.EggShellActivity;
import com.iyuba.music.activity.me.FriendCenter;
import com.iyuba.music.activity.me.MessageActivity;
import com.iyuba.music.adapter.discover.DiscoverAdapter;
import com.iyuba.music.file.FileBrowserActivity;
import com.iyuba.music.ground.AppGroundActivity;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.local_music.LocalMusicActivity;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.widget.dialog.CustomDialog;

/**
 * Created by 10202 on 2015/12/2.
 */
public class DiscoverActivity extends BaseActivity {
    private DiscoverAdapter discoverAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discover);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        RecyclerView discover = (RecyclerView) findViewById(R.id.discover_list);
        discover.setLayoutManager(new LinearLayoutManager(context));
        discoverAdapter = new DiscoverAdapter(context);
        discover.setAdapter(discoverAdapter);
    }

    @Override
    protected void setListener() {
        super.setListener();
        discoverAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 1:
                        if (AccountManager.INSTANCE.checkUserLogin()) {
                            startActivity(new Intent(context, CircleActivity.class));
                        } else {
                            CustomDialog.showLoginDialog(context);
                        }
                        break;
                    case 2:
                        if (AccountManager.INSTANCE.checkUserLogin()) {
                            startActivity(new Intent(context, MessageActivity.class));
                        } else {
                            CustomDialog.showLoginDialog(context);
                        }
                        break;
                    case 3:
                        if (AccountManager.INSTANCE.checkUserLogin()) {
                            SocialManager.instance.pushFriendId(AccountManager.INSTANCE.getUserId());
                            Intent intent = new Intent(context, FriendCenter.class);
                            intent.putExtra("type", "2");
                            intent.putExtra("needPop", true);
                            startActivity(intent);
                        } else {
                            CustomDialog.showLoginDialog(context);
                        }
                        break;
                    case 4:
                        startActivity(new Intent(context, AppGroundActivity.class));
                        break;
                    case -6:
                        Intent intent = new Intent();
                        intent.setClass(context, WebViewActivity.class);
                        intent.putExtra("url", "http://app.iyuba.com/android");
                        intent.putExtra("title", context.getString(R.string.oper_moreapp));
                        startActivity(intent);
                        break;
                    case 5:
                        startActivity(new Intent(context, WordSearchActivity.class));
                        break;
                    case 6:
                        startActivity(new Intent(context, SayingActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(context, WordListActivity.class));
                        break;
                    case 9:
                        startActivity(new Intent(context, FileBrowserActivity.class));
                        break;
                    case 10:
                        startActivity(new Intent(context, LocalMusicActivity.class));
                        break;
                    case 11:
                        if (SettingConfigManager.instance.isEggShell()) {
                            startActivity(new Intent(context, EggShellActivity.class));
                        }
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.oper_discover);
    }
}
