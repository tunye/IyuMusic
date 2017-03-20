package com.iyuba.music.file;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.iyuba.music.widget.roundview.RoundLinearLayout;
import com.iyuba.music.widget.roundview.RoundTextView;
import com.iyuba.music.widget.view.AddRippleEffect;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class FilePosActivity extends BaseActivity {
    private ArrayList<FileInfo> files;
    private String currentPath;
    private RecyclerView fileListView;
    private FileAdapter adapter;
    private RoundLinearLayout position;
    private TextView filePath;
    private RoundTextView sure;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_browser);
        context = this;
        files = new ArrayList<>();
        currentPath = "/";
        initWidget();
        setListener();
        changeUIByPara();
        getStartPos();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        fileListView = (RecyclerView) findViewById(R.id.file_recyclerview);
        fileListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FileAdapter();
        adapter.setDataSet(files);
        fileListView.setAdapter(adapter);
        fileListView.addItemDecoration(new DividerItemDecoration());
        filePath = (TextView) findViewById(R.id.file_path);
        position = (RoundLinearLayout) findViewById(R.id.file_parent);
        sure = (RoundTextView) findViewById(R.id.select_file_finish);
        AddRippleEffect.addRippleEffect(sure);
        sure.setVisibility(View.VISIBLE);
    }

    protected void setListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToParent();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        adapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FileInfo f = files.get(position);
                if (f.isDirectory()) {
                    currentPath = f.getPath();
                    viewFiles();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.file_pos_title);
    }

    protected void changeUIResumeByPara() {
        filePath.setText(currentPath);
    }

    @Override
    public void onBackPressed() {
        backToParent();
    }

    @Override
    public void onResume() {
        super.onResume();
        changeUIResumeByPara();
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
        files.clear();
        files.addAll(tmp);
        adapter.setDataSet(files);
        filePath.setText(currentPath);
    }

    private void viewFiles() {//点进
        ArrayList<FileInfo> tmp = FileActivityHelper.getFiles(currentPath);
        if (tmp != null) {
            files.clear();
            files.addAll(tmp);
            adapter.setDataSet(files);
            filePath.setText(currentPath);
        }
    }

    private void viewFiles(String filePath, String lastFilePath) {//退出
        ArrayList<FileInfo> tmp = FileActivityHelper.getFiles(filePath);
        if (tmp != null) {
            files.clear();
            files.addAll(tmp);
            currentPath = filePath;
            adapter.setDataSet(files);
            this.filePath.setText(currentPath);
            for (int i = 0; i < files.size(); i++) {
                if (files.get(i).getPath().equals(lastFilePath)) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sdcard_path;
    }
}
