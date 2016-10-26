package com.iyuba.music.entity.word;

/**
 * Created by 10202 on 2015/10/8.
 */
public class ExampleSentence {
    private int index;
    private String sentence;
    private String sentence_cn;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getSentence_cn() {
        return sentence_cn;
    }

    public void setSentence_cn(String sentence_cn) {
        this.sentence_cn = sentence_cn;
    }

    @Override
    public String toString() {
        StringBuilder StringBuilder = new StringBuilder();
        StringBuilder.append(index).append(".").append(sentence).append("<br/>").append(sentence_cn).append("<br/>");
        return StringBuilder.toString();
    }
}