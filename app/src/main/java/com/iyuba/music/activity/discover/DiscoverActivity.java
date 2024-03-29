package com.iyuba.music.activity.discover;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.WxOfficialAccountActivity;
import com.iyuba.music.activity.eggshell.EggShellActivity;
import com.iyuba.music.activity.me.MessageActivity;
import com.iyuba.music.activity.me.SearchFriendActivity;
import com.iyuba.music.adapter.discover.DiscoverAdapter;
import com.iyuba.music.file.FileBrowserActivity;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.local_music.LocalMusicActivity;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.widget.dialog.CustomDialog;

/**
 * Created by 10202 on 2015/12/2.
 */
public class DiscoverActivity extends BaseActivity {
    private DiscoverAdapter discoverAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.discover;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        RecyclerView discover = findViewById(R.id.discover_list);
        discover.setLayoutManager(new LinearLayoutManager(context));
        discoverAdapter = new DiscoverAdapter(context);
        discover.setAdapter(discoverAdapter);
    }

    @Override
    public void setListener() {
        super.setListener();
        discoverAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position - 1) {
                    case 0:
                        if (AccountManager.getInstance().checkUserLogin()) {
                            startActivity(new Intent(context, CircleActivity.class));
                        } else {
                            CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                                @Override
                                public void finish() {
                                    startActivity(new Intent(context, CircleActivity.class));
                                }
                            });
                        }
                        break;
                    case 1:
                        if (AccountManager.getInstance().checkUserLogin()) {
                            startActivity(new Intent(context, MessageActivity.class));
                        } else {
                            CustomDialog.showLoginDialog(context, false, new IOperationFinish() {
                                @Override
                                public void finish() {
                                    startActivity(new Intent(context, MessageActivity.class));
                                }
                            });
                        }
                        break;
                    case 2:
                        if (AccountManager.getInstance().checkUserLogin()) {
                            startActivity(new Intent(context, SearchFriendActivity.class));
                        } else {
                            CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                                @Override
                                public void finish() {
                                    startActivity(new Intent(context, SearchFriendActivity.class));
                                }
                            });
                        }
                        break;
                    case 3:
                        startActivity(new Intent(context, WxOfficialAccountActivity.class));
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
                        if (ConfigManager.getInstance().isEggShell()) {
                            startActivity(new Intent(context, EggShellActivity.class));
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText(R.string.oper_discover);
    }
}
