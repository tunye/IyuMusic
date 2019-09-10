package com.iyuba.music.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.adapter.study.ShareAdapter;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.account.ScoreOperRequest;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

/**
 * Created by 10202 on 2015/10/28.
 */
public class ShareDialog {
    private View root;
    private Activity activity;
    private Context context;
    private boolean shown;
    private Article article;
    private IyubaDialog iyubaDialog;
    private UMShareListener shareMorePeopleListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            if (AccountManager.getInstance().checkUserLogin()) {
                getScore(39);
            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            CustomToast.getInstance().showToast(share_media.toSnsPlatform().mShowWord + "分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            CustomToast.getInstance().showToast(share_media.toSnsPlatform().mShowWord + "分享取消");
        }
    };
    private UMShareListener shareSinglePeopleListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            if (AccountManager.getInstance().checkUserLogin()) {
                getScore(38);
            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            CustomToast.getInstance().showToast(share_media.toSnsPlatform().mKeyword + "分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            CustomToast.getInstance().showToast(share_media.toSnsPlatform().mKeyword + "分享取消");
        }
    };

    public ShareDialog(Activity activity, Article article) {
        this.context = activity;
        this.activity = activity;
        this.article = article;
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = vi.inflate(R.layout.study_more, null);
        init();
    }

    private void init() {
        GridView moreGrid = root.findViewById(R.id.study_menu);
        moreGrid.setNumColumns(3);
        ShareAdapter shareAdapter = new ShareAdapter(context);
        moreGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismiss();
                UMWeb web = new UMWeb(getShareUrl());
                web.setTitle(article.getTitle());
                web.setDescription(article.getContent());
                web.setThumb(new UMImage(activity, getPicUrl()));
                switch (position) {
                    case 0:
                        new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                                .withMedia(web).setCallback(shareMorePeopleListener)
                                .share();
                        break;
                    case 1:
                        new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN)
                                .withMedia(web).setCallback(shareSinglePeopleListener)
                                .share();
                        break;
                    case 2:
                        new ShareAction(activity).setPlatform(SHARE_MEDIA.SINA)
                                .withText("#听歌学英语# " + article.getTitle() + " — " + article.getSinger())
                                .withMedia(web).setCallback(shareMorePeopleListener)
                                .share();
                        break;
                    case 3:
                        new ShareAction(activity).setPlatform(SHARE_MEDIA.QQ)
                                .withMedia(web).setCallback(shareSinglePeopleListener)
                                .share();
                        break;
                    case 4:
                        new ShareAction(activity).setPlatform(SHARE_MEDIA.QZONE)
                                .withText(article.getTitle() + " — " + article.getSinger())
                                .withMedia(web).setCallback(shareMorePeopleListener)
                                .share();
                        break;
                    case 5:
                        new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN_FAVORITE)
                                .withText("听歌学英语 " + article.getTitle() + " — " + article.getSinger())
                                .withMedia(web)
                                .setCallback(shareSinglePeopleListener)
                                .share();
                        break;
                    default:
                        break;
                }

            }
        });
        moreGrid.setAdapter(shareAdapter);
        root.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                dismiss();
            }
        });
        iyubaDialog = new IyubaDialog(context, root, true, 0);
        iyubaDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                shown = false;
            }
        });
    }

    public void show() {
        iyubaDialog.showAnim(R.anim.bottom_in);
        shown = true;
    }

    public void dismiss() {
        iyubaDialog.dismissAnim(R.anim.bottom_out);
    }

    private void getScore(int type) {
        RequestClient.requestAsync(new ScoreOperRequest(AccountManager.getInstance().getUserId(), article.getId(), type), new SimpleRequestCallBack<BaseApiEntity<String>>() {
            @Override
            public void onSuccess(BaseApiEntity<String> apiEntity) {
                switch (apiEntity.getState()) {
                    case BaseApiEntity.SUCCESS:
                        CustomToast.getInstance().showToast(context.getString(R.string.article_share_success, apiEntity.getMessage(), apiEntity.getValue()));
                        break;
                    case BaseApiEntity.FAIL:
                        CustomToast.getInstance().showToast(apiEntity.getMessage());
                        break;
                    case BaseApiEntity.ERROR:
                        break;
                }
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {

            }
        });
    }

    private String getShareUrl() {
        String url;
        switch (StudyManager.getInstance().getApp()) {
            case "201":
            case "218":
                url = "http://m.iyuba.cn/news.html?type=voa&id=" + article.getId();
                break;
            case "209":
                if ("401".equals(article.getCategory())) {
                    url = "http://m.iyuba.cn/news.html?type=topvideos&id=" + article.getId();
                } else {
                    url = "http://m.iyuba.cn/news.html?type=song&id=" + article.getId();
                }
                break;
            case "212":
                url = "http://m.iyuba.cn/news.html?type=csvoa&id=" + article.getId();
                break;
            case "213":
                url = "http://m.iyuba.cn/news.html?type=meiyu&id=" + article.getId();
                break;
            case "217":
                url = "http://m.iyuba.cn/news.html?type=voavideo&id=" + article.getId();
                break;
            case "215":
            case "221":
            case "231":
                url = "http://m.iyuba.cn/news.html?type=bbc&id=" + article.getId();
                break;
            case "229":
                url = "http://m.iyuba.cn/news.html?type=ted&id=" + article.getId();
                break;
            default:
                url = "";
                break;
        }
        return url;
    }

    private String getPicUrl() {
        switch (StudyManager.getInstance().getApp()) {
            case "209":
                return "http://static.iyuba.cn/images/song/" + article.getPicUrl();
            default:
                return article.getPicUrl();
        }
    }

    public boolean isShown() {
        return shown;
    }
}
