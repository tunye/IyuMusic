package com.iyuba.music.file;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.flyco.roundview.RoundLinearLayout;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import java.io.File;
import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;


/**
 * 文件浏览器相关类
 */
public class PasteFileActivity extends BaseActivity {
    private RoundLinearLayout position;
    private TextView filePath;
    private FileAdapter adapter;
    private RecyclerView fileListView;
    private ArrayList<FileInfo> files;
    private String currentPath;
    private String currentPasteFilePath;
    private String action;
    private ProgressDialog progressDialog;
    private Handler progressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("CURRENTPATH", currentPath);
            intent.putExtras(bundle);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_paste);
        context = this;
        currentPath = FileUtil.getSDPath();
        files = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        currentPasteFilePath = bundle.getString("CURRENTPASTEFILEPATH");
        action = bundle.getString("ACTION");
        initWidget();
        setListener();
        changeUIByPara();
        viewFiles();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        filePath = (TextView) findViewById(R.id.file_path);
        position = (RoundLinearLayout) findViewById(R.id.file_parent);
        adapter = new FileAdapter();
        fileListView = (RecyclerView) findViewById(R.id.file_recyclerview);
        fileListView.setLayoutManager(new LinearLayoutManager(this));
        fileListView.setAdapter(adapter);
        fileListView.addItemDecoration(new DividerItemDecoration());
        fileListView.setItemAnimator(new SlideInLeftAnimator(new OvershootInterpolator(1f)));
    }

    @Override
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
        findViewById(R.id.file_createdir).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileActivityHelper.createDir(PasteFileActivity.this, currentPath, new IOperationFinish() {
                    @Override
                    public void finish() {
                        viewFiles();
                    }
                });
            }
        });
        findViewById(R.id.paste).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final File src = new File(currentPasteFilePath);
                if (!src.exists()) {
                    CustomToast.getInstance().showToast(R.string.file_noexists);
                    return;
                }
                String newPath = FileUtil.combinPath(currentPath, src.getName());
                final File tar = new File(newPath);
                if (tar.exists()) {
                    CustomToast.getInstance().showToast(R.string.file_exists);
                    return;
                }

                progressDialog = ProgressDialog.show(PasteFileActivity.this, RuntimeManager.getString(R.string.file_remove),
                        RuntimeManager.getString(R.string.file_remove_info), true, false);

                new Thread() {
                    @Override
                    public void run() {
                        if ("MOVE".equals(action)) {
                            if (FileUtil.copyFile(src, tar)) {
                                FileUtil.deleteFile(src);
                            }
                        } else {
                            FileUtil.copyFile(src, tar);
                        }
                        progressHandler.sendEmptyMessage(0);
                    }
                }.start();
            }
        });
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
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
        if ("MOVE".equals(action)) {
            title.setText(R.string.file_move_title);
        } else {
            title.setText(R.string.file_paste_title);
        }
    }

    protected void changeUIResumeByPara() {
        filePath.setText(currentPath);
    }

    @Override
    public void onResume() {
        super.onResume();
        changeUIResumeByPara();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backToParent();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void viewFiles() {
        ArrayList<FileInfo> tmp = FileActivityHelper.getFiles(currentPath);
        if (tmp != null) {
            files.clear();
            files.addAll(tmp);
            adapter.setDataSet(files);
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
}
