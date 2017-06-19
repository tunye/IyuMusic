package com.iyuba.music;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iyuba.music.manager.ConstantManager;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * <p/>
 * Created by 10202 on 2015/12/8.
 */
public class CrashHandler implements UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private MusicApplication application;
    private Map<String, Object> infos = new ArrayMap<>();
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * create
     */
    public CrashHandler(MusicApplication application) {
        this.application = application;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    public void createLogFile(String logMsg) {
        collectDeviceInfo(application.getApplicationContext());
        saveLogInfo2File(logMsg);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            application.exit();
        }
    }

    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(application.getApplicationContext(), R.string.bug_exit, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        //收集设备参数信息
        collectDeviceInfo(application.getApplicationContext());
        //保存日志文件
        String filePath = saveCrashInfo2File(ex);
        //上传服务器
        //uploadFile(filePath,"www.iyuba.com");
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                infos.put("versionName", versionName);
                infos.put("versionCode", pi.versionCode);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : infos.entrySet()) {
            String key = entry.getKey();
            sb.append(key).append("=").append(new Gson().toJson(entry.getValue())).append("\n");
        }
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        FileOutputStream fos = null;
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash_" + time + "_" + timestamp + ".log";
            File dir = new File(ConstantManager.crashFolder);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            fos = new FileOutputStream(dir.getPath() + File.separator + fileName);
            fos.write(sb.toString().getBytes());

            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 保存错误信息到文件中
     *
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveLogInfo2File(String logMsg) {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : infos.entrySet()) {
            String key = entry.getKey();
            sb.append(key).append("=").append(new Gson().toJson(entry.getValue())).append("\n");
        }
        sb.append(logMsg);
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "log_" + time + "_" + timestamp + ".log";
            File dir = new File(ConstantManager.crashFolder);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(dir.getPath() + File.separator + fileName);
            fos.write(sb.toString().getBytes());
            fos.close();
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void uploadFile(String position, String webUrl) {
        String end = "/r/n";
        String Hyphens = "--";
        String boundary = "*****";
        FileInputStream fStream = null;
        try {
            URL url = new URL(webUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            /* 设定传送的method=POST */
            con.setRequestMethod("POST");
            /* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            /* 设定DataOutputStream */
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            ds.writeBytes(Hyphens + boundary + end);
            /* 取得文件的FileInputStream */
            fStream = new FileInputStream(new File(position));
            /* 设定每次写入1024bytes */
            int bufferSize = 4096;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
            /* 从文件读取数据到缓冲区 */
            while ((length = fStream.read(buffer)) != -1) {
                /* 将数据写入DataOutputStream中 */
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);
            ds.writeBytes(Hyphens + boundary + Hyphens + end);
            fStream.close();
            ds.flush();
            /* 取得Response内容 */
            InputStream is = con.getInputStream();
            int ch;
            StringBuilder b = new StringBuilder();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            Log.d("aaa", b.toString());
            ds.close();
        } catch (Exception e) {
            e.printStackTrace();
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
