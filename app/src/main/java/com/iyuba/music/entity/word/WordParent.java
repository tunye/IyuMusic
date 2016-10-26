package com.iyuba.music.entity.word;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.iyuba.music.manager.SettingConfigManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10202 on 2015/12/3.
 */
public class WordParent implements ParentListItem {
    private ArrayList<Word> wordArrayList;
    private String parentContent;
    private int parentIndex;
    private boolean initiallyExpanded;

    public static ArrayList<WordParent> generateWordParent(ArrayList<Word> wordArrayList) {
        ArrayList<WordParent> wordParents = new ArrayList<>();
        int j = 0;
        WordParent wordParent = new WordParent();
        wordParent.setParentContent(getContentByPara(SettingConfigManager.instance.getWordOrder(), wordArrayList.get(0)));
        wordParent.setInitiallyExpanded(true);
        wordParent.setParentIndex(j++);
        ArrayList<Word> temp = new ArrayList<>();
        temp.add(wordArrayList.get(0));
        for (int i = 0; i < wordArrayList.size() - 1; i++) {
            if (getCompareResultByPara(SettingConfigManager.instance.getWordOrder(),
                    wordArrayList.get(i), wordArrayList.get(i + 1))) {
                temp.add(wordArrayList.get(i + 1));
            } else {
                wordParent.setWordArrayList(temp);
                wordParents.add(wordParent);
                temp = new ArrayList<>();
                temp.add(wordArrayList.get(i + 1));
                wordParent = new WordParent();
                wordParent.setInitiallyExpanded(true);
                wordParent.setParentIndex(j++);
                wordParent.setParentContent(getContentByPara(SettingConfigManager.instance.getWordOrder(), wordArrayList.get(i + 1)));
            }
        }
        wordParent.setWordArrayList(temp);
        wordParents.add(wordParent);
        return wordParents;
    }

    private static String getContentByPara(int para, Word word) {
        if (para == 1) {
            String time = word.getCreateDate();
            if (time.contains(" ")) {
                return time.split(" ")[0];
            } else {
                return word.getCreateDate();
            }
        } else {
            return String.valueOf(word.getWord().charAt(0)).toUpperCase();
        }
    }

    private static boolean getCompareResultByPara(int para, Word former, Word latter) {
        if (para == 1) {
            String formertime = former.getCreateDate();
            String lattertime = latter.getCreateDate();
            if (formertime.contains(" ")) {
                formertime = formertime.split(" ")[0];
            }
            if (lattertime.contains(" ")) {
                lattertime = lattertime.split(" ")[0];
            }
            return formertime.equals(lattertime);
        } else {
            return String.valueOf(former.getWord().charAt(0)).toUpperCase().equals(
                    String.valueOf(latter.getWord().charAt(0)).toUpperCase());
        }
    }

    public ArrayList<Word> getWordArrayList() {
        return wordArrayList;
    }

    public void setWordArrayList(ArrayList<Word> wordArrayList) {
        this.wordArrayList = wordArrayList;
    }

    public String getParentContent() {
        return parentContent;
    }

    public void setParentContent(String parentContent) {
        this.parentContent = parentContent;
    }

    public int getParentIndex() {
        return parentIndex;
    }

    public void setParentIndex(int parentIndex) {
        this.parentIndex = parentIndex;
    }

    public boolean isInitiallyExpanded() {
        return initiallyExpanded;
    }

    public void setInitiallyExpanded(boolean initiallyExpanded) {
        this.initiallyExpanded = initiallyExpanded;
    }

    @Override
    public List<Word> getChildItemList() {
        return wordArrayList;
    }
}
