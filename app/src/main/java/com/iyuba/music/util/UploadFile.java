package com.iyuba.music.util;

import com.buaa.ct.core.util.ThreadUtils;
import com.iyuba.music.listener.IOperationResult;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 文件上传
 *
 * @author 陈彤
 */
public class UploadFile {

    public static void postImg(String actionUrl, File img, final IOperationResult result) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        FileInputStream fStream = null;
        try {
            URL url = new URL(actionUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            /* 设定传送的method=POST */
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            /* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            /* 设定DataOutputStream */
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; " + "name=\""
                    + img + "\"" + ";filename=\""
                    + System.currentTimeMillis() + ".jpg\"" + end);
            ds.writeBytes(end);
            fStream = new FileInputStream(img);
            /* 设定每次写入1024bytes */
            int bufferSize = 4 * 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
            /* 从文件读取数据到缓冲区 */
            while ((length = fStream.read(buffer)) != -1) {
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            fStream.close();
            ds.flush();
            /* 取得Response内容 */
            InputStream is = con.getInputStream();
            int ch;
            StringBuilder b = new StringBuilder();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            ds.close();
            String returnContent = b.toString().trim();
            JSONObject jsonObject = new JSONObject(returnContent.substring(
                    returnContent.indexOf("{"), returnContent.lastIndexOf("}") + 1));
            if (jsonObject.getString("status").equals("0")) {
                final String imgUrl = jsonObject.getString("bigUrl");
                ThreadUtils.postOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        result.success(imgUrl);
                    }
                });
            } else {
                ThreadUtils.postOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        result.fail(null);
                    }
                });
            }
        } catch (Exception e) {
            ThreadUtils.postOnUiThread(new Runnable() {
                @Override
                public void run() {
                    result.fail(null);
                }
            });
        } finally {
            if (fStream != null) {
                try {
                    fStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void postSound(String actionUrl, File sound, final IOperationResult result) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        FileInputStream fStream = null;
        try {
            URL url = new URL(actionUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            /* 设定传送的method=POST */
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            /* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            /* 设定DataOutputStream */
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; "
                    + "name=sound" + ";filename=\""
                    + System.currentTimeMillis() + ".amr\"" + end);
            ds.writeBytes(end);
            fStream = new FileInputStream(sound);
            int bufferSize = 4 * 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
            /* 从文件读取数据到缓冲区 */
            while ((length = fStream.read(buffer)) != -1) {
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            /* close streams */
            fStream.close();
            ds.flush();
            /* 取得Response内容 */
            InputStream is = con.getInputStream();
            int ch;
            StringBuilder b = new StringBuilder();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            ds.close();
            JSONObject jsonObjectRoot = new JSONObject(b.toString().trim());
            final int resultCode = jsonObjectRoot.getInt("ResultCode");
            ThreadUtils.postOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (resultCode == 1) {
                        result.success(null);
                    } else {
                        result.fail(null);
                    }
                }
            });
        } catch (Exception e) {
            ThreadUtils.postOnUiThread(new Runnable() {
                @Override
                public void run() {
                    result.fail(null);
                }
            });
        } finally {
            if (fStream != null) {
                try {
                    fStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
