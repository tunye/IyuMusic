package com.iyuba.music.activity.study;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.fragment.BaseFragment;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.textview.JustifyTextView;

/**
 * Created by 10202 on 2015/12/17.
 */
public class StudyInfoFragment extends BaseFragment {
    private ImageView img;
    private TextView title, singer, announcer;
    private JustifyTextView content;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.study_info, null);
        img = view.findViewById(R.id.article_img);
        title = view.findViewById(R.id.article_title);
        announcer = view.findViewById(R.id.article_announcer);
        singer = view.findViewById(R.id.article_singer);
        content = view.findViewById(R.id.article_abstract);
        content.setMovementMethod(ScrollingMovementMethod.getInstance());
        refresh();
        return view;
    }

    public void refresh() {
        Article curArticle = StudyManager.getInstance().getCurArticle();
        if (StudyManager.getInstance().getApp().equals("209")) {
            ImageUtil.loadImage("http://static.iyuba.cn/images/song/" + curArticle.getPicUrl(), img, R.drawable.default_music);
            announcer.setText(context.getString(R.string.article_announcer, curArticle.getBroadcaster()));
            singer.setText(context.getString(R.string.article_singer, curArticle.getSinger()));
        } else {
            ImageUtil.loadImage(curArticle.getPicUrl(), img, R.drawable.default_music);
            singer.setText(curArticle.getTitle_cn());
        }
        title.setText(curArticle.getTitle());
        content.setText(curArticle.getContent());
    }
}
