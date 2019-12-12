package com.iyuba.music.manager;

import com.iyuba.music.entity.article.Article;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 10202 on 2015/12/17.
 */
public class StudyManager {
    private Article curArticle;
    private List<Article> sourceArticleList;
    private List<Article> curArticleList;
    private String app;
    private String listFragmentPos;
    private String lesson;
    private String startTime;
    private boolean isStartPlaying;

    private StudyManager() {
        app = ConstantManager.appId;
        sourceArticleList = new ArrayList<>();
    }

    public static StudyManager getInstance() {
        return SingleInstanceHelper.instance;
    }

    public void next() {
        int pos = curArticleList.indexOf(curArticle);
        pos = (pos + 1) % curArticleList.size();
        curArticle = curArticleList.get(pos);
        app = curArticle.getApp();
    }

    public void before() {
        int pos = curArticleList.indexOf(curArticle);
        pos = (pos - 1 + curArticleList.size()) % curArticleList.size();
        curArticle = curArticleList.get(pos);
        app = curArticle.getApp();
    }

    public void setSourceArticleList(List<Article> sourceArticleList) {
        this.sourceArticleList = sourceArticleList;
        generateArticleList();
    }

    public List<Article> getCurArticleList() {
        return curArticleList;
    }

    public Article getCurArticle() {
        return curArticle;
    }

    public void setCurArticle(Article curArticle) {
        this.curArticle = curArticle;
        this.app = curArticle.getApp();
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson = lesson;
    }

    public String getListFragmentPos() {
        return listFragmentPos;
    }

    public void setListFragmentPos(String listFragmentPos) {
        this.listFragmentPos = listFragmentPos;
    }

    public boolean isStartPlaying() {
        return isStartPlaying;
    }

    public void setStartPlaying(boolean startPlaying) {
        isStartPlaying = startPlaying;
    }

    public int getMusicType() {
        if (!app.equals("209")) {
            return 0;
        } else if (curArticle.getSimple() == 1) {
            return 0;
        } else {
            return ConfigManager.getInstance().getStudyMode();
        }
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void generateArticleList() {
        curArticleList = new ArrayList<>();
        switch (ConfigManager.getInstance().getStudyPlayMode()) {
            case 0:
            case 1:
                curArticleList.addAll(sourceArticleList);
                break;
            case 2:
                ArrayList<Article> temp = new ArrayList<>();
                temp.addAll(sourceArticleList);
                Collections.shuffle(temp);
                curArticleList.addAll(temp);
                break;
        }
    }

    private static class SingleInstanceHelper {
        private static StudyManager instance = new StudyManager();
    }
}
