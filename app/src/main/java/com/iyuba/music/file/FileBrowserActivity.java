package com.iyuba.music.file;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.util.PermissionPool;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IOperationResultInt;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.widget.dialog.ContextMenu;
import com.iyuba.music.widget.roundview.RoundLinearLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FileBrowserActivity extends BaseActivity {
    private String currentPath;
    private RecyclerView fileListView;
    private FileAdapter adapter;
    private ContextMenu contextMenu;
    private RoundLinearLayout position;
    private TextView filePath;
    private File currentFile;

    @Override
    public int getLayoutId() {
        return R.layout.file_browser;
    }

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        currentPath = ConstantManager.envir;
    }

    @Override
    public void afterSetLayout() {
        super.afterSetLayout();
        permissionDispose(PermissionPool.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        fileListView = findViewById(R.id.file_recyclerview);
        adapter = new FileAdapter(this, new FileAdapter.OnItemLongClickListener() {
            @Override
            public void onClick(int pos) {
                currentFile = new File(adapter.getDatas().get(pos).getPath());
                contextMenu.show();
            }
        });
        fileListView.setAdapter(adapter);
        setRecyclerViewProperty(fileListView);
        filePath = findViewById(R.id.file_path);
        position = findViewById(R.id.file_parent);
        initContextMenu();
    }

    private void initContextMenu() {
        contextMenu = new ContextMenu(context);
        ArrayList<String> operText = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(
                R.array.file_op)));
        contextMenu.setInfo(operText, new IOperationResultInt() {
            @Override
            public void performance(int index) {
                contextMenu.dismiss();
                operCurrentFile(index);
            }
        });
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
        adapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FileInfo f = adapter.getDatas().get(position);
                if (f.isDirectory()) {
                    currentPath = f.getPath();
                    viewFiles();
                } else {
                    openFile(f.getPath());
                }
            }
        });
    }

    @Override
    public void onActivityCreated() {
        backIcon.setBackgroundResource(R.drawable.close);
        title.setText(R.string.file_title);
        viewFiles();
    }

    @Override
    public void onActivityResumed() {
        filePath.setText(currentPath);
    }

    @Override
    public void onBackPressed() {
        backToParent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            Bundle bundle = data.getExtras();
            if (bundle != null && bundle.containsKey("CURRENTPATH")) {
                currentPath = bundle.getString("CURRENTPATH");
                viewFiles();
            }
        }
    }

    private void operCurrentFile(int index) {
        switch (index) {
            case 0:
                FileActivityHelper.renameFile(context, currentFile, new IOperationFinish() {
                            @Override
                            public void finish() {
                                viewFiles();
                            }
                        }
                );
                break;
            case 1:
                pasteFile(currentFile.getPath(), "COPY");
                break;
            case 2:
                pasteFile(currentFile.getPath(), "MOVE");
                break;
            case 3:
                FileUtil.deleteFile(currentFile);
                viewFiles();
                break;
            case 4:
                FileActivityHelper.viewFileInfo(context, FileUtil.getFileInfo(currentFile));
                break;
            default:
                break;
        }
    }

    private void viewFiles() {//点进
        List<FileInfo> tmp = FileActivityHelper.getFiles(currentPath);
        if (tmp != null) {
            adapter.setDataSet(tmp);
            filePath.setText(currentPath);
        }
    }

    private boolean viewFiles(String filePath, String lastFilePath) {//退出
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
            return true;
        } else {
            return false;
        }
    }

    private void backToParent() {
        String parentPath = currentPath.substring(0, currentPath.lastIndexOf(File.separator));
        if (TextUtils.isEmpty(parentPath) || !viewFiles(parentPath, currentPath)) {
            finish();
        }
    }

    private void openFile(String path) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

        File f = new File(path);
        String type = FileUtil.getMIMEType(f.getName());
        // android N获取uri的新方式，更方便的方式是通过在application中改变strictmode
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            intent.setDataAndType(FileProvider.getUriForFile(this, getApplication().getPackageName(), f), type);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        } else {
//            intent.setDataAndType(Uri.fromFile(f), type);
//        }
        intent.setDataAndType(Uri.fromFile(f), type);
        startActivity(intent);
    }

    private void pasteFile(String path, String action) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("CURRENTPASTEFILEPATH", path);
        bundle.putString("ACTION", action);
        intent.putExtras(bundle);
        intent.setClass(context, PasteFileActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onAccreditSucceed(int requestCode) {
        super.onAccreditSucceed(requestCode);
        File file = new File(currentPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public void onAccreditFailure(int requestCode) {
        super.onAccreditFailure(requestCode);
        onRequestPermissionDenied(context.getString(R.string.storage_permission_content),
                new int[]{PermissionPool.WRITE_EXTERNAL_STORAGE},
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }
}
