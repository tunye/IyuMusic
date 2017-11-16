package com.iyuba.music.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.iyuba.music.R;
import com.iyuba.music.activity.WebViewActivity;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.newsrequest.StudyTimeRequest;
import com.iyuba.music.widget.CustomToast;

/**
 * Created by 10202 on 2017-03-17.
 */

public class SignInDialog {
    private View root;
    private Context context;
    private IyubaDialog iyubaDialog;
    private boolean shown;

    private WebView webView;

    public SignInDialog(Context context) {
        this.context = context;
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = vi.inflate(R.layout.sign_in, null);
    }

    public void show() {
        StudyTimeRequest.exeRequest(StudyTimeRequest.generateUrl(), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg);
            }

            @Override
            public void response(Object object) {
                BaseApiEntity result = (BaseApiEntity) object;
                if (result.getState() == BaseApiEntity.SUCCESS) {
                    if (result.getData().toString().compareTo("180") >= 0) {
                        init();
                    } else {
                        CustomToast.getInstance().showToast("您当日学习时长不足3分钟，赶快去听几首歌曲再来签到吧~");
                    }
                } else {
                    CustomToast.getInstance().showToast("信息拉取失败");
                }
            }
        });
    }

    private void init() {
        View cancel = root.findViewById(R.id.sign_in_close);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        webView = (WebView) root.findViewById(R.id.sign_in_web);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JSHook(), "iyuba");
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        iyubaDialog = new IyubaDialog(context, root, false, 20, new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                shown = false;
            }
        });
        iyubaDialog.show();
        cancel.postDelayed(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("http://api.iyuba.com/credits/qiandao.jsp?uid=" + AccountManager.getInstance().getUserId() + "&appid=" + ConstantManager.appId);
            }
        }, 400);
        shown = true;
    }

    public void dismiss() {
        iyubaDialog.dismissAnim();
    }

    public boolean isShown() {
        return shown;
    }

    private class JSHook {
        @JavascriptInterface
        public void test(String p) {
            Log.e(getClass().getName(), "JSHook called! + " + p);
        }

        @JavascriptInterface
        public void onAdSpanClick(String url) {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("url", url);
            context.startActivity(intent);
        }

        @JavascriptInterface
        public void onFootImgClick(String url) {
            Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("url", url);
            context.startActivity(intent);
        }
    }
}
