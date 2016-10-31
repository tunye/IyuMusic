package com.iyuba.music.entity.original;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 10202 on 2015/10/8.
 */
public class Original implements Comparable<Original> {
    @SerializedName("SongId")
    private int articleID;
    @SerializedName("ParaId")
    private int paraID;//段落ID
    @SerializedName("IdIndex")
    private int sentenceID;//段内ID
    @SerializedName("Timing")
    private float startTime;
    @SerializedName("EndTiming")
    private float endTime;
    @SerializedName("Sentence")
    private String sentence = "";
    @SerializedName("SentenceCn")
    private String sentence_cn = "";
    @SerializedName("sentence_cn")
    private String sentence_cn_backup = "";
    private float totalTime;

    public static Original createRows(String lrcLine) {
        if (!lrcLine.startsWith("^[")) {
            return null;
        }
        // 最后一个"]"
        int lastIndexOfRightBracket = lrcLine.lastIndexOf("]^");
        // 歌词内容
        String content = lrcLine.substring(lastIndexOfRightBracket + 2,
                lrcLine.length());
        Original original = new Original();
        String[] contentArray = content.split("@@@");
        if (contentArray.length == 2) {
            original.setSentence(contentArray[0]);
            original.setSentence_cn(contentArray[1]);
        } else {
            original.setSentence(contentArray[0]);
        }
        // -03:33.02--00:36.37-
        String times = lrcLine.substring(0, lastIndexOfRightBracket + 2)
                .replace("^[", "-").replace("]^", "-");
        times = "-" + times + "-";
        String[] timesArray = times.split("--");
        original.setStartTime(Float.parseFloat(timesArray[1]));
        original.setEndTime(Float.parseFloat(timesArray[2]));
        original.setArticleID(Integer.parseInt(timesArray[3]));
        original.setParaID(Integer.parseInt(timesArray[4]));
        original.setSentenceID(Integer.parseInt(timesArray[5]));
        return original;
    }

    public int getArticleID() {
        return articleID;
    }

    public void setArticleID(int articleID) {
        this.articleID = articleID;
    }

    public int getParaID() {
        return paraID;
    }

    public void setParaID(int paraID) {
        this.paraID = paraID;
    }

    public int getSentenceID() {
        return sentenceID;
    }

    public void setSentenceID(int sentenceID) {
        this.sentenceID = sentenceID;
    }

    public float getStartTime() {
        return startTime;
    }

    public void setStartTime(float startTime) {
        this.startTime = startTime;
        if (startTime != 0 && endTime != 0) {
            totalTime = endTime - startTime;
        }
    }

    public float getEndTime() {
        return endTime;
    }

    public void setEndTime(float endTime) {
        this.endTime = endTime;
        if (startTime != 0 && endTime != 0) {
            totalTime = endTime - startTime;
        }
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

    public float getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(float totalTime) {
        this.totalTime = totalTime;
    }

    public String getSentence_cn_backup() {
        return sentence_cn_backup;
    }

    public void setSentence_cn_backup(String sentence_cn_backup) {
        this.sentence_cn_backup = sentence_cn_backup;
    }

    @Override
    public int compareTo(Original anotherOriginalRow) {
        if (this.getStartTime() > anotherOriginalRow.getStartTime()) {
            return 1;
        } else {
            return -1;
        }
    }
}
