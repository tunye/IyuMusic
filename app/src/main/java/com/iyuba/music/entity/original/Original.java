package com.iyuba.music.entity.original;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by 10202 on 2015/10/8.
 */
public class Original implements Comparable<Original> {
    @JSONField(name = "SongId")
    private int articleID;
    @JSONField(name = "ParaId")
    private int paraID;//段落ID
    @JSONField(name = "IdIndex")
    private int sentenceID;//段内ID
    @JSONField(name = "Timing")
    private float startTime;
    @JSONField(name = "EndTiming")
    private float endTime;
    @JSONField(name = "Sentence")
    private String sentence = "";
    @JSONField(name = "SentenceCn")
    private String sentence_cn = "";
    @JSONField(name = "sentence_cn")
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Original) {
            Original equalPre = (Original) obj;
            return equalPre.articleID == this.articleID && equalPre.getSentence().equals(this.sentence) && equalPre.getStartTime() == this.startTime;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = articleID;
        result = 31 * result + paraID;
        result = 31 * result + sentenceID;
        result = 31 * result + (startTime != +0.0f ? Float.floatToIntBits(startTime) : 0);
        result = 31 * result + (endTime != +0.0f ? Float.floatToIntBits(endTime) : 0);
        result = 31 * result + (sentence != null ? sentence.hashCode() : 0);
        result = 31 * result + (sentence_cn != null ? sentence_cn.hashCode() : 0);
        result = 31 * result + (sentence_cn_backup != null ? sentence_cn_backup.hashCode() : 0);
        result = 31 * result + (totalTime != +0.0f ? Float.floatToIntBits(totalTime) : 0);
        return result;
    }
}
