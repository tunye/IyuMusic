package com.iyuba.music.file;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.flyco.roundview.RoundLinearLayout;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IOperationResultInt;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.widget.dialog.ContextMenu;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


public class FileBrowserActivity extends BaseActivity {
    private ArrayList<FileInfo> files;
    private String currentPath;
    private RecyclerView fileListView;
    private FileAdapter adapter;
    private ContextMenu contextMenu;
    private RoundLinearLayout position;
    private TextView filePath;
    private File currentFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_browser);
        context = this;
        files = new ArrayList<>();
        currentPath = ConstantManager.instance.getEnvir();
        File file = new File(currentPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        initWidget();
        setListener();
        changeUIByPara();
        viewFiles();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        fileListView = (RecyclerView) findViewById(R.id.file_recyclerview);
        fileListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FileAdapter();
        adapter.setDataSet(files);
        fileListView.setAdapter(adapter);
        fileListView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        filePath = (TextView) findViewById(R.id.file_path);
        position = (RoundLinearLayout) findViewById(R.id.file_parent);
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
        adapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                FileInfo f = files.get(position);
                if (f.isDirectory()) {
                    currentPath = f.getPath();
                    viewFiles();
                } else {
                    openFile(f.getPath());
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                currentFile = new File(files.get(position).getPath());
                contextMenu.show();
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        backIcon.setState(MaterialMenuDrawable.IconState.X);
        title.setText(R.string.file_title);
    }

    protected void changeUIResumeByPara() {
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

    @Override
    public void onResume() {
        super.onResume();
        changeUIResumeByPara();
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
        ArrayList<FileInfo> tmp = FileActivityHelper.getFiles(currentPath);
        if (tmp != null) {
            files.clear();
            files.addAll(tmp);
            adapter.setDataSet(files);
            filePath.setText(currentPath);
        }
    }

    private boolean viewFiles(String filePath, String lastFilePath) {//退出
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
}
