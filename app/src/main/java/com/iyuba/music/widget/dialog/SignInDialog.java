package com.iyuba.music.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.activity.WebViewActivity;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.newsrequest.StudyTimeRequest;
import com.iyuba.music.util.Utils;

/**
 * Created by 10202 on 2017-03-17.
 */

public class SignInDialog {
    private View root;
    private Context context;
    private IyubaDialog iyubaDialog;
    private boolean shown;

    private WebView webView;
    private View loading;

    public SignInDialog(Context context) {
        this.context = context;
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = vi.inflate(R.layout.sign_in, null);
    }

    public void show() {
        RequestClient.requestAsync(new StudyTimeRequest(), new SimpleRequestCallBack<BaseApiEntity<Integer>>() {
            @Override
            public void onSuccess(BaseApiEntity<Integer> result) {
                if (result.getState() == BaseApiEntity.SUCCESS) {
                    if (Integer.parseInt(result.getData().toString()) >= 180) {
                        init();
                    } else {
                        CustomToast.getInstance().showToast("您当日学习时长不足3分钟，赶快去听几首歌曲再来签到吧~");
                    }
                } else {
                    CustomToast.getInstance().showToast("信息拉取失败");
                }
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
            }
        });
    }

    private void init() {
        final View cancel = root.findViewById(R.id.sign_in_close);
        cancel.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                dismiss();
            }
        });
        loading = root.findViewById(R.id.sign_in_loading_animation);
        iyubaDialog = new IyubaDialog(context, root, false, 20, new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                shown = false;
            }
        });
        iyubaDialog.show();
        shown = true;
        loadingWebView();
    }

    private void loadingWebView() {
        webView = root.findViewById(R.id.sign_in_web);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        webView.setWebChromeClient(
                new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        super.onProgressChanged(view, newProgress);
                        if (newProgress == 100) {
                            loading.setVisibility(View.GONE);
                            webView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onReceivedTitle(WebView view, String titleContent) {
                        super.onReceivedTitle(view, titleContent);
                    }
                }
        );
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JSHook(), "iyuba");
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.loadUrl("http://api.iyuba.cn/credits/qiandao.jsp?uid=" + AccountManager.getInstance().getUserId() + "&appid=" + ConstantManager.appId);
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
