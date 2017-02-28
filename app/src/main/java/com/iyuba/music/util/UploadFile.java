package com.iyuba.music.util;

import android.util.Log;

import com.iyuba.music.listener.IOperationResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 文件上传
 *
 * @author 陈彤
 */
public class UploadFile {
    private static String success;

    public static void postImg(String actionUrl, File img, IOperationResult result) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
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
            FileInputStream fStream = new FileInputStream(img);
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
            success = b.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonObject = new JSONObject(success.substring(
                    success.indexOf("{"), success.lastIndexOf("}") + 1));
            if (jsonObject.getString("status").equals("0")) {
                result.success(jsonObject.getString("bigUrl"));
            } else {
                result.fail(null);
            }
        } catch (JSONException e) {
            result.fail(null);
        }
    }

    public static void postSound(String actionUrl, File sound, IOperationResult result) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
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
            FileInputStream fStream = new FileInputStream(sound);
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
            success = b.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonObjectRoot = new JSONObject(success);
            if (jsonObjectRoot.getInt("ResultCode") == 1) {
                result.success(null);
            } else {
                result.fail(null);
            }
        } catch (JSONException e) {
            result.fail(null);
        }
    }
}
