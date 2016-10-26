package com.iyuba.music.entity.word;

/**
 * Created by 10202 on 2015/12/2.
 */
public class Saying {
    private int id;
    private String chinese;
    private String english;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChinese() {
        return chinese;
    }

    public void setChinese(String chinese) {
        this.chinese = chinese;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    @Override
    public String toString() {
        return "Saying{" +
                "id=" + id +
                ", chinese='" + chinese + '\'' +
                ", english='" + english + '\'' +
                '}';
    }
}
