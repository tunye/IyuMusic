package com.iyuba.music.entity.original;


import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 默认的歌词解析器
 *
 * @author Ligang 2014/8/19
 */
public class OriginalParser {
    private static OriginalParser instance;

    private OriginalParser() {
    }

    public static final OriginalParser getInstance() {
        if (instance == null) {
            instance = new OriginalParser();
        }
        return instance;
    }

    public boolean fileExist(int para) {
        String fileUrl;
        if (StudyManager.getInstance().getApp().equals("209")) {
            fileUrl = ConstantManager.getInstance().getOriginalFolder() + File.separator + para + ".lrc";
        } else {
            fileUrl = ConstantManager.getInstance().getOriginalFolder() + File.separator + StudyManager.getInstance().getApp() + "-" + para + ".lrc";
        }
        File file = new File(fileUrl);
        return file.exists();
    }

    /***
     * 将歌词文件里面的字符串 解析成一个List<LrcRow>
     */
    public void getOriginal(int para, IOperationResult result) {
        String fileUrl;
        if (StudyManager.getInstance().getApp().equals("209")) {
            fileUrl = ConstantManager.getInstance().getOriginalFolder() + File.separator + para + ".lrc";
        } else {
            fileUrl = ConstantManager.getInstance().getOriginalFolder() + File.separator + StudyManager.getInstance().getApp() + "-" + para + ".lrc";
        }
        File file = new File(fileUrl);
        if (!file.exists())
            return;
        ArrayList<Original> originalRows = new ArrayList<>();
        String lrcLine;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((lrcLine = br.readLine()) != null) {
                Original row = Original.createRows(lrcLine);
                originalRows.add(row);
            }
            br.close();
            if (originalRows.size() == 0) {
                result.fail("");
            } else if (originalRows.size() == 1) {
                result.success(originalRows);
            } else {
                Collections.sort(originalRows);
                result.success(originalRows);
            }
        } catch (IOException e) {
            e.printStackTrace();
            result.fail("");
        }
    }
}
