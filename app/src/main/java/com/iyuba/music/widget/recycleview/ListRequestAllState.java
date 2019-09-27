package com.iyuba.music.widget.recycleview;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.network.NetWorkState;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.util.ThreadUtils;
import com.iyuba.music.R;

public class ListRequestAllState extends FrameLayout {
    private TextView errorContent;
    private TextView retry;
    private ImageView errorImg;
    private View errContainer;
    private View list;
    private View loading;
    private @StringRes
    int emptyShowContent;

    public ListRequestAllState(@NonNull Context context) {
        this(context, null);
    }

    public ListRequestAllState(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListRequestAllState(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        measure(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        inflate(getContext(), R.layout.list_request_all_state, this);
        errorImg = findViewById(R.id.request_error_img);
        errorContent = findViewById(R.id.request_error_content);
        retry = findViewById(R.id.request_error_retry);
        errContainer = findViewById(R.id.request_error_container);
        loading = findViewById(R.id.request_loading);
        dismiss();
    }

    public void setList(View list) {
        this.list = list;
    }

    public void startLoad() {
        ThreadUtils.postOnUiThreadDelay(new Runnable() {
            @Override
            public void run() {
                list.setVisibility(INVISIBLE);
                errContainer.setVisibility(INVISIBLE);
                loading.setVisibility(VISIBLE);
            }
        }, 100);
    }

    public void setEmptyShowContent(@StringRes int emptyShowContent) {
        this.emptyShowContent = emptyShowContent;
    }

    public void setLoadingShowContent(@StringRes int loadingContent) {
        ((TextView) this.loading.findViewById(R.id.waitting_text)).setText(loadingContent);
    }

    public void show(ErrorInfoWrapper err, @Nullable final ListRequestListener listRequestListener) {
        list.setVisibility(GONE);
        hideLoading();
        switch (err.type) {
            case ErrorInfoWrapper.DATA_ERROR:
                errorContent.setText(R.string.data_error);
                errorImg.setImageResource(R.drawable.request_data_error);
                retry.setVisibility(VISIBLE);
                retry.setOnClickListener(new INoDoubleClick() {
                    @Override
                    public void activeClick(View view) {
                        showLoading();
                        if (listRequestListener != null) {
                            listRequestListener.retryClick();
                        }
                    }
                });
                break;
            case ErrorInfoWrapper.EMPTY_ERROR:
                if (emptyShowContent != 0) {
                    errorContent.setText(emptyShowContent);
                }
                errorImg.setImageResource(R.drawable.request_empty_data);
                retry.setVisibility(GONE);
                break;
            case ErrorInfoWrapper.NET_ERROR:
                errorContent.setText(R.string.net_no_net);
                errorImg.setImageResource(R.drawable.request_network_error);
                retry.setVisibility(VISIBLE);
                retry.setOnClickListener(new INoDoubleClick() {
                    @Override
                    public void activeClick(View view) {
                        if (NetWorkState.getInstance().getNetWorkState().equals(NetWorkState.WIFI_NONET)) {
                            try {
                                Intent intent = new Intent();
                                intent.setAction("android.intent.action.VIEW");
                                Uri content_url = Uri.parse("http://m.baidu.com");
                                intent.setData(content_url);
                                retry.getContext().startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            retry.getContext().startActivity(intent);
                        }
                    }
                });
                break;
        }
    }

    public void loadSuccess() {
        this.list.setVisibility(VISIBLE);
        this.loading.setVisibility(GONE);
        this.errorContent.setVisibility(GONE);
    }

    public void showLoading() {
        errContainer.setVisibility(GONE);
        loading.setVisibility(VISIBLE);
    }

    public void hideLoading() {
        loading.setVisibility(View.GONE);
        errContainer.setVisibility(VISIBLE);
    }

    public void dismiss() {
        errContainer.setVisibility(GONE);
        loading.setVisibility(GONE);
        if (list != null) {
            list.setVisibility(VISIBLE);
        }
    }

    public interface ListRequestListener {
        void retryClick();
    }
}
