package com.iyuba.music.activity.discover;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.buaa.ct.core.manager.RuntimeManager;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.util.ThreadPoolUtil;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.request.discoverrequest.BlogRequest;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.Utils;
import com.iyuba.music.util.WeakReferenceHandler;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * Created by 10202 on 2016/4/22.
 */
public class BlogActivity extends BaseActivity {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private TextView blogAuthor, blogTime, blogTitle, blogContent;
    private Spanned sp;
    private String message;
    private int id;

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        id = getIntent().getIntExtra("blogid", 0);
    }

    @Override
    public int getLayoutId() {
        return R.layout.blog;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        blogContent = findViewById(R.id.blog_content);
        blogAuthor = findViewById(R.id.blog_author);
        blogTime = findViewById(R.id.blog_time);
        blogTitle = findViewById(R.id.blog_title);
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        getData();
    }

    private void getData() {
        RequestClient.requestAsync(new BlogRequest(id), new SimpleRequestCallBack<BaseApiEntity<String>>() {
            @Override
            public void onSuccess(BaseApiEntity<String> baseApiEntity) {
                message = baseApiEntity.getMessage();
                blogTitle.setText(baseApiEntity.getData());
                String temp = baseApiEntity.getValue();
                String[] contents = temp.split("@@");
                title.setText(context.getString(R.string.circle_blog) + " - " + contents[0]);
                blogAuthor.setText(context.getString(R.string.circle_blog_author, contents[0], contents[1]));
                blogTime.setText(context.getString(R.string.circle_blog_time, DateFormat.showTime(context, new Date(Long.parseLong(contents[2]) * 1000))));
                ThreadPoolUtil.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        message = message.replaceAll("<br/>", "");
                        sp = Html.fromHtml(message + "<br/><br/><br/>", new Html.ImageGetter() {
                            @Override
                            public Drawable getDrawable(String source) {
                                InputStream is;
                                try {
                                    is = (InputStream) new URL(source).getContent();
                                    Drawable d = Drawable.createFromStream(is, "src");
                                    float ratio = d.getIntrinsicWidth() * 1f / d.getIntrinsicHeight();
                                    float modiHeight = (RuntimeManager.getInstance().getWindowWidth() - RuntimeManager.getInstance().dip2px(32)) / ratio;
                                    d.setBounds(0, 0, RuntimeManager.getInstance().getWindowWidth()
                                            - RuntimeManager.getInstance().dip2px(32), (int) modiHeight);
                                    is.close();
                                    return d;
                                } catch (Exception e) {
                                    return null;
                                }
                            }
                        }, null);
                        handler.sendEmptyMessage(0);
                    }
                });
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
            }
        });
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<BlogActivity> {
        @Override
        public void handleMessageByRef(final BlogActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    activity.blogContent.setText(activity.sp);
                    activity.blogContent.setMovementMethod(LinkMovementMethod.getInstance());
                    break;
            }
        }
    }
}
