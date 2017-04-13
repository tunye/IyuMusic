package com.iyuba.music.activity.discover;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.request.discoverrequest.BlogRequest;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.ThreadPoolUtil;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog);
        context = this;
        id = getIntent().getIntExtra("blogid", 0);
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        blogContent = (TextView) findViewById(R.id.blog_content);
        blogAuthor = (TextView) findViewById(R.id.blog_author);
        blogTime = (TextView) findViewById(R.id.blog_time);
        blogTitle = (TextView) findViewById(R.id.blog_title);
    }

    @Override
    protected void setListener() {
        super.setListener();
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        getData();
    }

    private void getData() {
        BlogRequest.exeRequest(BlogRequest.generateUrl(id), new IProtocolResponse() {
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
                BaseApiEntity baseApiEntity = (BaseApiEntity) object;
                message = baseApiEntity.getMessage();
                blogTitle.setText(baseApiEntity.getData().toString());
                String temp = baseApiEntity.getValue();
                String[] contents = temp.split("@@");
                title.setText(context.getString(R.string.circle_blog) + " - " + contents[0]);
                blogAuthor.setText(context.getString(R.string.circle_blog_author, contents[0], contents[1]));
                blogTime.setText(context.getString(R.string.circle_blog_time, DateFormat.showTime(context, new Date(Long.parseLong(contents[2]) * 1000))));
                ThreadPoolUtil.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        message.replaceAll("<br/>", "");
                        sp = Html.fromHtml(message + "<br/><br/><br/>", new Html.ImageGetter() {
                            @Override
                            public Drawable getDrawable(String source) {
                                InputStream is;
                                try {
                                    is = (InputStream) new URL(source).getContent();
                                    Drawable d = Drawable.createFromStream(is, "src");
                                    float ratio = d.getIntrinsicWidth() * 1f / d.getIntrinsicHeight();
                                    float modiHeight = (RuntimeManager.getWindowWidth() - RuntimeManager.dip2px(32)) / ratio;
                                    d.setBounds(0, 0, RuntimeManager.getWindowWidth()
                                            - RuntimeManager.dip2px(32), (int) modiHeight);
                                    is.close();
                                    return d;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }
                        }, null);
                        handler.sendEmptyMessage(0);
                    }
                });
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
