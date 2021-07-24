package com.iyuba.music.file;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.manager.RuntimeManager;
import com.buaa.ct.core.util.ThreadPoolUtil;
import com.buaa.ct.core.util.ThreadUtils;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.widget.roundview.RoundLinearLayout;

import java.io.File;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;


/**
 * 文件浏览器相关类
 */
public class PasteFileActivity extends BaseActivity {
    private RoundLinearLayout position;
    private TextView filePath;
    private FileAdapter adapter;
    private RecyclerView fileListView;
    private String currentPath;
    private String currentPasteFilePath;
    private String action;
    private ProgressDialog progressDialog;

    @Override
    public int getLayoutId() {
        return R.layout.file_paste;
    }

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        currentPath = FileUtil.getSDPath();
        Bundle bundle = getIntent().getExtras();
        currentPasteFilePath = bundle.getString("CURRENTPASTEFILEPATH");
        action = bundle.getString("ACTION");
    }

    @Override
    public void initWidget() {
        super.initWidget();
        filePath = findViewById(R.id.file_path);
        position = findViewById(R.id.file_parent);
        adapter = new FileAdapter(this, null);
        fileListView = findViewById(R.id.file_recyclerview);
        fileListView.setAdapter(adapter);
        fileListView.setItemAnimator(new SlideInLeftAnimator(new OvershootInterpolator(1f)));
        setRecyclerViewProperty(fileListView);
    }

    @Override
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
        findViewById(R.id.file_createdir).setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                FileActivityHelper.createDir(PasteFileActivity.this, currentPath, new IOperationFinish() {
                    @Override
                    public void finish() {
                        viewFiles();
                    }
                });
            }
        });
        findViewById(R.id.paste).setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
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

                progressDialog = ProgressDialog.show(PasteFileActivity.this, RuntimeManager.getInstance().getString(R.string.file_remove),
                        RuntimeManager.getInstance().getString(R.string.file_remove_info), true, false);

                ThreadPoolUtil.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        if ("MOVE".equals(action)) {
                            if (FileUtil.copyFile(src, tar)) {
                                FileUtil.deleteFile(src);
                            }
                        } else {
                            FileUtil.copyFile(src, tar);
                        }
                        ThreadUtils.postOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putString("CURRENTPATH", currentPath);
                                intent.putExtras(bundle);
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            }
                        });
                    }
                });
            }
        });
        findViewById(R.id.cancel).setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
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
        if ("MOVE".equals(action)) {
            title.setText(R.string.file_move_title);
        } else {
            title.setText(R.string.file_paste_title);
        }
        viewFiles();
    }

    @Override
    public void onActivityResumed() {
        filePath.setText(currentPath);
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
        List<FileInfo> tmp = FileActivityHelper.getFiles(currentPath);
        if (tmp != null) {
            adapter.setDataSet(tmp);
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
}
