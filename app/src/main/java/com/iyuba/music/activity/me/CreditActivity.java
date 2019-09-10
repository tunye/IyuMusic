package com.iyuba.music.activity.me;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.view.CustomToast;
import com.buaa.ct.core.view.swiperefresh.CustomSwipeToRefresh;
import com.buaa.ct.core.view.swiperefresh.MySwipeRefreshLayout;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.WebViewActivity;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.merequest.GradeRequest;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.boundnumber.RiseNumberTextView;

/**
 * Created by 10202 on 2016/3/31.
 */
public class CreditActivity extends BaseActivity implements MySwipeRefreshLayout.OnRefreshListener {
    private RiseNumberTextView counts, rank;
    private View creditDetail, creditExchange;
    private CustomSwipeToRefresh swipeRefreshLayout;
    private TextView creditDuration;

    @Override
    public int getLayoutId() {
        return R.layout.credits;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        toolbarOper = findViewById(R.id.toolbar_oper);
        counts = findViewById(R.id.credit_counts);
        rank = findViewById(R.id.credit_rank);
        creditDetail = findViewById(R.id.credit_detail);
        creditExchange = findViewById(R.id.credit_exchange);
        creditDuration = findViewById(R.id.credit_duration);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setColorSchemeColors(0xff259CF7, 0xff2ABB51, 0xffE10000, 0xfffaaa3c);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                Intent intent = new Intent();
                intent.setClass(context, WebViewActivity.class);
                intent.putExtra("url", "http://m.iyuba.cn/mall/ruleOfintegral.jsp");
                intent.putExtra("title", context.getString(R.string.credits_helper));
                startActivity(intent);
            }
        });
        creditDetail.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                Intent intent = new Intent();
                intent.setClass(context, WebViewActivity.class);
                intent.putExtra("url", "http://api.iyuba.cn/credits/useractionrecordmobileList1.jsp?uid=" + AccountManager.getInstance().getUserId());
                intent.putExtra("title", context.getString(R.string.credits_details));
                startActivity(intent);
            }
        });
        creditExchange.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                Intent intent = new Intent();
                intent.setClass(context, WebViewActivity.class);
                intent.putExtra("url", "http://m.iyuba.cn/mall/index.jsp?uid=" + AccountManager.getInstance().getUserId()
                        + "&appid=" + ConstantManager.appId + "&username="
                        + AccountManager.getInstance().getUserInfo().getUsername() + "&sign=" +
                        MD5.getMD5ofStr("iyuba" + AccountManager.getInstance().getUserId() + "camstory"));
                intent.putExtra("title", context.getString(R.string.campaign_exchange));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText(R.string.credits_title);
        toolbarOper.setText(R.string.credits_helper);
        title.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (AccountManager.getInstance().getUserInfo().getIcoins() != null) {
                    counts.withNumber(Integer.parseInt(AccountManager.getInstance().getUserInfo().getIcoins())).start();
                } else {
                    counts.setText("0");
                }
                rank.setText(R.string.credits_loading);
                onRefresh(0);
            }
        }, 700);
    }

    /**
     * 下拉刷新
     *
     * @param index 当前分页索引
     */
    @Override
    public void onRefresh(int index) {
        getData();
    }

    /**
     * 加载更多
     *
     * @param index 当前分页索引
     */
    @Override
    public void onLoad(int index) {

    }

    private void getData() {
        RequestClient.requestAsync(new GradeRequest(AccountManager.getInstance().getUserId()), new SimpleRequestCallBack<String>() {
            @Override
            public void onSuccess(String result) {
                final String[] para = result.split("@@@");
                if (AccountManager.getInstance().getUserInfo().getIcoins() != null) {
                    counts.withNumber(Integer.parseInt(AccountManager.getInstance().getUserInfo().getIcoins())).start();
                }
                rank.withNumber(Integer.parseInt(para[0])).start();
                creditDuration.setText(getString(R.string.credits_study_time, exeStudyTime(Integer.parseInt(para[1]))));
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
            }
        });
    }

    private String exeStudyTime(int time) {
        StringBuilder sb = new StringBuilder();
        int minus = time % 60;
        int minute = time / 60 % 60;
        int hour = time / 60 / 60;
        if (hour > 0) {
            sb.append(hour).append(':');
        }
        if (minute > 0 || hour > 0) {
            sb.append(minute).append(':');
        }
        sb.append(minus);
        if (sb.toString().contains(":")) {
            return sb.toString();
        } else {
            return "0:" + sb.toString();
        }
    }
}
