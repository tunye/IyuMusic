package com.iyuba.music.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.util.MD5;

/**
 * Created by 10202 on 2015/12/1.
 */
public class CampaignActivity extends BaseActivity {
    private LinearLayout root;
    private WebView web;
    private String url;
    private ProgressBar loadProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.campaign);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        root = (LinearLayout) findViewById(R.id.root);
        web = (WebView) findViewById(R.id.webview);
        loadProgress = (ProgressBar) findViewById(R.id.load_progress);
    }

    @Override
    public void onResume() {
        super.onResume();
        changeUIResumeByPara();
    }

    @Override
    protected void setListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        web.setWebChromeClient(
                new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        if (newProgress == 100) {
                            loadProgress.setVisibility(View.GONE);
                        } else {
                            if (loadProgress.getVisibility() == View.GONE) {
                                loadProgress.setVisibility(View.VISIBLE);
                            }
                            loadProgress.setProgress(newProgress);
                        }
                        super.onProgressChanged(view, newProgress);
                    }

                    @Override
                    public void onReceivedTitle(WebView view, String titleContent) {
                        super.onReceivedTitle(view, titleContent);
                    }
                }
        );
        web.setDownloadListener(new DownloadListener() {
            @Override

            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        web.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setSupportZoom(true);
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toolbarOper.getText().equals(context.getString(R.string.campaign_exchange))) {
                    toolbarOper.setText(R.string.campaign_recharge);
                    title.setText(R.string.campaign_exchange);
                    url = getExchangeUrl();
                } else {
                    toolbarOper.setText(R.string.campaign_exchange);
                    title.setText(R.string.campaign_recharge);
                    url = getRechargeUrl();
                }
                web.loadUrl(url);
                loadProgress.setProgress(0);
            }
        });
    }

    protected void changeUIResumeByPara() {
        toolbarOper.setText(R.string.campaign_recharge);
        title.setText(R.string.campaign_exchange);
        url = getExchangeUrl();
        web.loadUrl(url);
        loadProgress.setProgress(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        root.removeView(web);
        web.destroy();
    }

    private String getRechargeUrl() {
        String userId = AccountManager.getInstance().getUserId();
        String token = MD5.getMD5ofStr(userId + "iyuba");
        return "http://m.iyuba.com/m_login/present.jsp?uid=" + userId + "&token=" + token;
    }


    private String getExchangeUrl() {
        return "http://m.iyuba.com/mall/index.jsp?uid=" + AccountManager.getInstance().getUserId()
                + "&appid=" + ConstantManager.getInstance().getAppId() + "&username="
                + AccountManager.getInstance().getUserInfo().getUsername() + "&sign=" +
                MD5.getMD5ofStr("iyuba" + AccountManager.getInstance().getUserId() + "camstory");
    }
}
