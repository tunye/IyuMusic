package com.iyuba.music.entity.original;


import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.util.FileOperation;

import java.io.File;
import java.util.ArrayList;

/**
 * 默认的歌词解析器
 *
 * @author Ligang 2014/8/19
 */
public class LrcMaker {
    private static LrcMaker instance;

    private LrcMaker() {
        File file = new File(ConstantManager.lrcFolder);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static LrcMaker getInstance() {
        if (instance == null) {
            instance = new LrcMaker();
        }
        return instance;
    }

    /***
     * 将歌词文件里面的字符串 解析成一个List<LrcRow>
     */
    public void makeOriginal(int para, ArrayList<Original> original) {
        String fileUrl;
        if (StudyManager.getInstance().getApp().equals("209")) {
            fileUrl = ConstantManager.lrcFolder + File.separator + para + ".lrc";
        } else {
            fileUrl = ConstantManager.lrcFolder + File.separator + StudyManager.getInstance().getApp() + "-" + para + ".lrc";
        }
        StringBuilder sb = new StringBuilder();
        for (Original lrc : original) {
            sb.append("^[").append(lrc.getStartTime()).append("]^");
            sb.append("^[").append(lrc.getEndTime()).append("]^");
            sb.append("^[").append(lrc.getArticleID()).append("]^");
            sb.append("^[").append(lrc.getParaID()).append("]^");
            sb.append("^[").append(lrc.getSentenceID()).append("]^");
            sb.append(lrc.getSentence()).append("@@@").append(lrc.getSentence_cn());
            sb.append("\n");
        }
        FileOperation.writeFileSDFileBuffer(fileUrl, sb.toString(), false);
    }
}
