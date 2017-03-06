package com.iyuba.music.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.CampaignActivity;
import com.iyuba.music.activity.WebViewActivity;
import com.iyuba.music.activity.main.DownloadSongActivity;
import com.iyuba.music.activity.main.FavorSongActivity;
import com.iyuba.music.activity.main.ListenSongActivity;
import com.iyuba.music.adapter.me.MeAdapter;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.util.MD5;
import com.iyuba.music.widget.dialog.CustomDialog;

/**
 * Created by 10202 on 2015/12/2.
 */
public class MeActivity extends BaseActivity {
    private MeAdapter meAdapter;

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
        meAdapter = new MeAdapter(context);
        discover.setAdapter(meAdapter);
    }

    @Override
    protected void setListener() {
        super.setListener();
        meAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 1:
                        if (AccountManager.getInstance().checkUserLogin()) {
                            SocialManager.getInstance().pushFriendId(AccountManager.getInstance().getUserId());
                            Intent intent = new Intent(context, FriendCenter.class);
                            intent.putExtra("type", "0");
                            intent.putExtra("needPop", true);
                            startActivity(intent);
                        } else {
                            CustomDialog.showLoginDialog(context);
                        }
                        break;
                    case 2:
                        if (AccountManager.getInstance().checkUserLogin()) {
                            SocialManager.getInstance().pushFriendId(AccountManager.getInstance().getUserId());
                            Intent intent = new Intent(context, FriendCenter.class);
                            intent.putExtra("type", "1");
                            intent.putExtra("needPop", true);
                            startActivity(intent);
                        } else {
                            CustomDialog.showLoginDialog(context);
                        }
                        break;
                    case 3:
                        if (AccountManager.getInstance().checkUserLogin()) {
                            startActivity(new Intent(context, FindFriendActivity.class));
                        } else {
                            CustomDialog.showLoginDialog(context);
                        }
                        break;
                    case 5:
                        startActivity(new Intent(context, DownloadSongActivity.class));
                        break;
                    case 6:
                        startActivity(new Intent(context, FavorSongActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(context, ListenSongActivity.class));
                        break;
                    case 9:
                        if (AccountManager.getInstance().checkUserLogin()) {
                            startActivity(new Intent(context, CreditActivity.class));
                        } else {
                            CustomDialog.showLoginDialog(context);
                        }
                        break;
                    case 10:
                        if (AccountManager.getInstance().checkUserLogin()) {
                            StringBuilder url = new StringBuilder();
                            url.append("http://m.iyuba.com/i/getRanking.jsp?appId=")
                                    .append(ConstantManager.getInstance().getAppId()).append("&uid=")
                                    .append(AccountManager.getInstance().getUserId()).append("&sign=")
                                    .append(MD5.getMD5ofStr(AccountManager.getInstance().getUserId()
                                            + "ranking" + ConstantManager.getInstance().getAppId()));
                            Intent intent = new Intent();
                            intent.setClass(context, WebViewActivity.class);
                            intent.putExtra("url", url.toString());
                            intent.putExtra("title", context.getString(R.string.oper_rank));
                            startActivity(intent);
                        } else {
                            CustomDialog.showLoginDialog(context);
                        }
                        break;
                    case 11:
                        if (AccountManager.getInstance().checkUserLogin()) {
                            StringBuilder url = new StringBuilder("http://m.iyuba.com/i/index.jsp?");
                            url.append("uid=").append(AccountManager.getInstance().getUserId()).append('&');
                            url.append("username=").append(AccountManager.getInstance().getUserInfo().getUsername()).append('&');
                            url.append("sign=").append(MD5.getMD5ofStr("iyuba" + AccountManager.getInstance().getUserId() + "camstory"));
                            Intent intent = new Intent();
                            intent.setClass(context, WebViewActivity.class);
                            intent.putExtra("url", url.toString());
                            intent.putExtra("title", context.getString(R.string.oper_bigdata));
                            startActivity(intent);
                        } else {
                            CustomDialog.showLoginDialog(context);
                        }
                        break;
                    case 12:
                        if (AccountManager.getInstance().checkUserLogin()) {
                            startActivity(new Intent(context, CampaignActivity.class));
                        } else {
                            CustomDialog.showLoginDialog(context);
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
        title.setText(R.string.oper_me);
    }
}
