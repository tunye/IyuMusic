package com.iyuba.music.file;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.util.AddRippleEffect;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.widget.roundview.RoundLinearLayout;
import com.iyuba.music.widget.roundview.RoundTextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class FilePosActivity extends BaseActivity {
    private String currentPath;
    private RecyclerView fileListView;
    private FileAdapter adapter;
    private RoundLinearLayout position;
    private TextView filePath;
    private RoundTextView sure;

    @Override
    public int getLayoutId() {
        return R.layout.file_browser;
    }

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        context = this;
        currentPath = "/";
    }

    @Override
    public void initWidget() {
        super.initWidget();
        fileListView = findViewById(R.id.file_recyclerview);
        adapter = new FileAdapter(this, null);
        fileListView.setAdapter(adapter);
        setRecyclerViewProperty(fileListView);
        filePath = findViewById(R.id.file_path);
        position = findViewById(R.id.file_parent);
        sure = findViewById(R.id.select_file_finish);
        AddRippleEffect.addRippleEffect(sure);
        sure.setVisibility(View.VISIBLE);
    }

    public void setListener() {
        back.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                finish();
            }
        });
        position.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                backToParent();
            }
        });
        sure.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                Intent intent = new Intent();
                if (currentPath.equals(getPath2())) {
                    intent.putExtra("path", currentPath.substring(currentPath.lastIndexOf(File.separator) + 1, currentPath.length()));
                } else {
                    intent.putExtra("path", currentPath);
                }
                setResult(1, intent);
                FilePosActivity.this.finish();
            }
        });
        adapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FileInfo f = adapter.getDatas().get(position);
                if (f.isDirectory()) {
                    currentPath = f.getPath();
                    viewFiles();
                }
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        backIcon.setBackgroundResource(R.drawable.close);
        title.setText(R.string.file_pos_title);
        getStartPos();
    }

    public void onActivityResumed() {
        filePath.setText(currentPath);
    }

    @Override
    public void onBackPressed() {
        backToParent();
    }

    private void getStartPos() {
        ArrayList<FileInfo> tmp = new ArrayList<>();
        String extSdCard = getPath2();
        if (!TextUtils.isEmpty(extSdCard)) {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setName("外置SD卡");
            fileInfo.setIsDirectory(true);
            fileInfo.setPath(extSdCard);
            fileInfo.setLastModify(DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));
            tmp.add(fileInfo);
        }
        tmp.add(FileUtil.getFileInfo(Environment.getExternalStorageDirectory()));
        adapter.setDataSet(tmp);
        filePath.setText(currentPath);
    }

    private void viewFiles() {//点进
        List<FileInfo> tmp = FileActivityHelper.getFiles(currentPath);
        if (tmp != null) {
            adapter.setDataSet(tmp);
            filePath.setText(currentPath);
        }
    }

    private void viewFiles(String filePath, String lastFilePath) {//退出
        List<FileInfo> tmp = FileActivityHelper.getFiles(filePath);
        if (tmp != null) {
            currentPath = filePath;
            adapter.setDataSet(tmp);
            this.filePath.setText(currentPath);
            for (int i = 0; i < tmp.size(); i++) {
                if (tmp.get(i).getPath().equals(lastFilePath)) {
                    fileListView.scrollToPosition(i + 5);
                    break;
                }
            }
        }
    }

    private void backToParent() {
        String parentPath = currentPath.substring(0, currentPath.lastIndexOf(File.separator));
        if (!TextUtils.isEmpty(parentPath)) {
            viewFiles(parentPath, currentPath);
        } else {
            finish();
        }
    }

    private String getPath2() {
        String sdcard_path = null;
        String sd_default = Environment.getExternalStorageDirectory()
                .getAbsolutePath();
        if (sd_default.endsWith("/")) {
            sd_default = sd_default.substring(0, sd_default.length() - 1);
        }
        // 得到路径
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                if (line.contains("fat") && line.contains("/mnt/")) {
                    String columns[] = line.split(" ");
                    if (columns.length > 1) {
                        if (sd_default.trim().equals(columns[1].trim())) {
                            continue;
                        }
                        sdcard_path = columns[1];
                    }
                } else if (line.contains("fuse") && line.contains("/mnt/")) {
                    String columns[] = line.split(" ");
                    if (columns.length > 1) {
                        if (sd_default.trim().equals(columns[1].trim())) {
                            continue;
                        }
                        sdcard_path = columns[1];
                    }
                }
            }
            br.close();
            isr.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sdcard_path;
    }
}
