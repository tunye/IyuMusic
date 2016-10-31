package com.iyuba.music.entity.word;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/8.
 */
public class Word {
    private String user;
    private String createDate;
    private String word;
    private String pron;
    private String pronMP3;
    private String def;
    private String viewCount;
    private ArrayList<ExampleSentence> sentences;
    private String exampleSentence;
    private String isdelete;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPron() {
        return pron;
    }

    public void setPron(String pron) {
        this.pron = pron;
    }

    public String getPronMP3() {
        return pronMP3;
    }

    public void setPronMP3(String pronMP3) {
        this.pronMP3 = pronMP3;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public ArrayList<ExampleSentence> getSentences() {
        return sentences;
    }

    public void setSentences(ArrayList<ExampleSentence> sentences) {
        this.sentences = sentences;
        StringBuilder sb = new StringBuilder();
        for (ExampleSentence exampleSentence : sentences) {
            sb.append(exampleSentence.toString()).append("<br/>");
        }
        exampleSentence = sb.toString();
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getExampleSentence() {
        return exampleSentence;
    }

    public void setExampleSentence(String exampleSentence) {
        this.exampleSentence = exampleSentence;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(String isdelete) {
        this.isdelete = isdelete;
    }

    @Override
    public String toString() {
        return "Word{" +
                "user='" + user + '\'' +
                ", createDate='" + createDate + '\'' +
                ", word='" + word + '\'' +
                ", pron='" + pron + '\'' +
                ", pronMP3='" + pronMP3 + '\'' +
                ", def='" + def + '\'' +
                ", sentences=" + exampleSentence +
                ", isdelete=" + isdelete +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Word) {
            Word compare = (Word) o;
            return this.getWord().equals(compare.getWord()) && this.getUser().equals(compare.getUser());
        }
        return false;
    }
}
