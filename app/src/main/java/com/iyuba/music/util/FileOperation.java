package com.iyuba.music.util;

import android.content.Context;

import com.iyuba.music.manager.RuntimeManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 10202 on 2015/10/16.
 */
public class FileOperation {
    public static String readRawFile(int rawid, String encode) {
        String res = "";
        try { //得到资源中的Raw数据流
            InputStream in = RuntimeManager.getContext().getResources().openRawResource(rawid);
            //得到数据的大小
            int length = in.available();
            byte[] buffer = new byte[length];
            //读取数据
            in.read(buffer);
            //依test.txt的编码类型选择合适的编码，如果不调整会乱码
            res = new String(buffer, encode);
            //关闭
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String readAssetsFile(String fileName, String encode) {
        String res = "";
        try {
            //得到资源中的asset数据流
            InputStream in = RuntimeManager.getContext().getResources().getAssets().open(fileName);
            int length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);
            in.close();
            res = new String(buffer, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    //写数据
    public static void writeDataFile(String fileName, String writestr) throws IOException {
        try {
            FileOutputStream fout = RuntimeManager.getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
            byte[] bytes = writestr.getBytes();
            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //读数据
    public static String readDataFile(String fileName, String encode) throws IOException {
        String res = "";
        try {
            FileInputStream fin = RuntimeManager.getContext().openFileInput(fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = new String(buffer, encode);
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    //写数据到SD中的文件
    public static void writeFileSdcardFile(String fileName, String write_str) throws IOException {
        try {
            FileOutputStream fout = new FileOutputStream(fileName);
            byte[] bytes = write_str.getBytes();
            fout.write(bytes);
            fout.flush();
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeFileSDFileBuffer(String filename, String context, boolean cover) {
        try {
            File file = new File(filename);
            //第二个参数意义是说是否以append方式添加内容
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, cover));
            bw.write(context);
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //读SD中的文件
    public String readFileSdcardFile(String fileName, String encode) throws IOException {
        String res = "";
        try {
            FileInputStream fin = new FileInputStream(fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = new String(buffer, encode);
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
