package com.iyuba.music.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.iyuba.music.R;
import com.iyuba.music.adapter.study.ShareAdapter;
import com.iyuba.music.download.DownloadService;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.apprequest.ShareRequest;
import com.iyuba.music.widget.CustomToast;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMusic;

/**
 * Created by 10202 on 2015/10/28.
 */
public class ShareDialog {
    private View root;
    private Activity activity;
    private Context context;
    private boolean shown;
    private Article article;
    private Dialog dialog;
    private UMShareListener shareMorePeopleListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA share_media) {
            if (AccountManager.instance.checkUserLogin()) {
                getScore(2);
            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            CustomToast.INSTANCE.showToast(share_media.toSnsPlatform().mKeyword + "分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            CustomToast.INSTANCE.showToast(share_media.toSnsPlatform().mKeyword + "分享取消");
        }
    };
    private UMShareListener shareSinglePeopleListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA share_media) {
            if (AccountManager.instance.checkUserLogin()) {
                getScore(1);
            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            CustomToast.INSTANCE.showToast(share_media.toSnsPlatform().mKeyword + "分享失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            CustomToast.INSTANCE.showToast(share_media.toSnsPlatform().mKeyword + "分享取消");
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
        GridView moreGrid = (GridView) root.findViewById(R.id.study_menu);
        moreGrid.setNumColumns(3);
        int[] menuDrawable = new int[]{R.drawable.umeng_socialize_wxcircle, R.drawable.umeng_socialize_wechat,
                R.drawable.umeng_socialize_sina_on, R.drawable.umeng_socialize_qq_on,
                R.drawable.umeng_socialize_qzone_on, R.drawable.umeng_socialize_sms_on,};
        String[] menuText = context.getResources().getStringArray(R.array.share);
        ShareAdapter shareAdapter = new ShareAdapter(context);
        shareAdapter.setDataSet(menuText, menuDrawable);
        moreGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismiss();
                UMusic music;
                if (StudyManager.instance.getApp().equals("209") && article.getSimple() == 0) {
                    music = new UMusic(DownloadService.getAnnouncerUrl(article.getId(), article.getSoundUrl()));
                } else {
                    music = new UMusic(DownloadService.getSongUrl(article.getApp(), article.getMusicUrl()));
                }
                switch (position) {
                    case 0:
                        music.setAuthor(article.getSinger());
                        music.setTitle(article.getTitle());
                        music.setTargetUrl(getShareUrl());
                        music.setThumb(new UMImage(activity, getPicUrl()));
                        new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                                .withText(article.getSinger())
                                .withMedia(music).setCallback(shareMorePeopleListener)
                                .share();
                        break;
                    case 1:
                        music.setAuthor(article.getSinger());
                        music.setTitle(article.getTitle());
                        music.setTargetUrl(getShareUrl());
                        music.setThumb(getPicUrl());
                        new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN)
                                .withText(article.getSinger())
                                .withMedia(music).setCallback(shareSinglePeopleListener)
                                .share();
                        break;
                    case 2:
                        new ShareAction(activity).setPlatform(SHARE_MEDIA.SINA)
                                .withText("#听歌学英语# " + article.getTitle() + article.getContent())
                                .withTargetUrl(getShareUrl())
                                .withMedia(music).setCallback(shareMorePeopleListener)
                                .share();
                        break;
                    case 3:
                        music.setAuthor(article.getSinger());
                        music.setTitle(article.getTitle());
                        music.setTargetUrl(getShareUrl());
                        music.setThumb(getPicUrl());
                        new ShareAction(activity).setPlatform(SHARE_MEDIA.QQ)
                                .withText(article.getSinger())
                                .withTargetUrl(getShareUrl())
                                .withMedia(music).setCallback(shareSinglePeopleListener)
                                .share();
                        break;
                    case 4:
                        music.setAuthor(article.getSinger());
                        music.setTitle(article.getTitle());
                        music.setTargetUrl(getShareUrl());
                        music.setThumb(getPicUrl());
                        new ShareAction(activity).setPlatform(SHARE_MEDIA.QZONE)
                                .withText(article.getSinger())
                                .withTargetUrl(getShareUrl())
                                .withMedia(music).setCallback(shareMorePeopleListener)
                                .share();
                        break;
                    case 5:
                        new ShareAction(activity).setPlatform(SHARE_MEDIA.SMS)
                                .withText(article.getTitle() + article.getContent() + getShareUrl())
                                .setCallback(shareSinglePeopleListener)
                                .share();
                        break;
                    default:
                        break;
                }

            }
        });
        moreGrid.setAdapter(shareAdapter);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dialog = new Dialog(context, root, true, 0);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                shown = false;
            }
        });
    }

    public void show() {
        dialog.showAnim(R.anim.bottom_in);
        shown = true;
    }

    public void dismiss() {
        dialog.dismissAnim(R.anim.bottom_out);
    }

    private void getScore(int type) {
        ShareRequest.exeRequest(ShareRequest.generateUrl(AccountManager.instance.getUserId(), article.getId(), type), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {

            }

            @Override
            public void onServerError(String msg) {

            }

            @Override
            public void response(Object object) {
                BaseApiEntity apiEntity = (BaseApiEntity) object;
                switch (apiEntity.getState()) {
                    case SUCCESS:
                        CustomToast.INSTANCE.showToast(context.getString(R.string.article_share_success, apiEntity.getMessage(), apiEntity.getValue()));
                        break;
                    case FAIL:
                        CustomToast.INSTANCE.showToast(apiEntity.getMessage());
                        break;
                    case ERROR:
                        break;
                }
            }
        });
    }

    private String getShareUrl() {
        String url;
        switch (StudyManager.instance.getApp()) {
            case "201":
            case "218":
                url = "http://m.iyuba.com/voaS/play.jsp?id=" + article.getId();
                break;
            case "209":
                url = "http://m.iyuba.com/MSEn/play.jsp?id=" + article.getId();
                break;
            case "212":
                url = "http://m.iyuba.com/voaS/playC.jsp.jsp?id=" + article.getId();
                break;
            case "213":
                url = "http://m.iyuba.com/voaS/playAM.jsp?id=" + article.getId();
                break;
            case "217":
                url = "http://m.iyuba.com/voaS/playCV.jsp?id=" + article.getId();
                break;
            case "215":
            case "221":
            case "231":
                url = "http://m.iyuba.com/bbcwap/play.jsp?id=" + article.getId();
                break;
            case "229":
                url = "http://m.iyuba.com/ted/play.jsp?id=" + article.getId();
                break;
            default:
                url = "";
                break;
        }
        return url;
    }

    private String getPicUrl() {
        switch (StudyManager.instance.getApp()) {
            case "209":
                return "http://static.iyuba.com/images/song/" + article.getPicUrl();
            default:
                return article.getPicUrl();
        }
    }

    public boolean isShown() {
        return shown;
    }
}
