package com.iyuba.music.file;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.iyuba.music.widget.view.MaterialRippleLayout;
import com.iyuba.music.R;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 文件浏览器相关类
 */
public class FileActivityHelper {
    public static ArrayList<FileInfo> getFiles(String path) {
        File f = new File(path);
        File[] files = f.listFiles();
        if (files == null) {
            CustomToast.getInstance().showToast(String.format(
                    RuntimeManager.getString(R.string.file_cannotopen), path));
            return null;
        }
        ArrayList<FileInfo> fileList = new ArrayList<>();
        for (File temp : files) {
            fileList.add(FileUtil.getFileInfo(temp));
        }
        Collections.sort(fileList, new FileComparator());
        return fileList;
    }

    public static void createDir(final Context context, final String path, final IOperationFinish finish) {
        final MyMaterialDialog dialog = new MyMaterialDialog(context);
        dialog.setTitle(R.string.file_create_dialogtitle);
        View contentView = LayoutInflater.from(context).inflate(R.layout.file_create, null);
        dialog.setContentView(contentView);
        final MaterialEditText fileName = (MaterialEditText) contentView.findViewById(R.id.file_name);
        fileName.setHint("请输入文件名:");
        fileName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    fileName.setError(context.getResources().getString(R.string.file_name_empty));
                } else {
                    fileName.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dialog.setPositiveButton(R.string.file_create, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                String newName = fileName.getText().toString();
                if (TextUtils.isEmpty(newName)) {
                    CustomToast.getInstance().showToast(R.string.file_create_fail);
                    return;
                }
                String fullFileName = FileUtil.combinPath(path, newName);
                File newFile = new File(fullFileName);
                if (newFile.exists()) {
                    CustomToast.getInstance().showToast(R.string.file_exists);
                } else {
                    if (newFile.mkdir()) {
                        finish.finish();
                    } else {
                        CustomToast.getInstance().showToast(R.string.file_create_fail);
                    }
                }
            }
        });
        dialog.setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(fileName.getApplicationWindowToken(), 0);
            }
        });
        dialog.show();
    }

    public static void renameFile(final Context context, final File f, final IOperationFinish finish) {
        final MyMaterialDialog dialog = new MyMaterialDialog(context);
        dialog.setTitle(R.string.file_rename_dialogtitle);
        View contentView = LayoutInflater.from(context).inflate(R.layout.file_create, null);
        dialog.setContentView(contentView);
        final MaterialEditText fileName = (MaterialEditText) contentView.findViewById(R.id.file_name);
        fileName.setHint("请输入修改的文件名:");
        final String oldName = f.getName();
        fileName.setText(oldName);
        fileName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    fileName.setError(context.getResources().getString(R.string.file_name_empty));
                } else if (s.toString().equals(oldName)) {
                    fileName.setError(context.getResources().getString(R.string.file_name_same));
                } else {
                    fileName.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dialog.setPositiveButton(R.string.file_rename, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 100);
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(fileName.getApplicationWindowToken(), 0);
                String newName = fileName.getText().toString();
                if (TextUtils.isEmpty(newName)) {
                    CustomToast.getInstance().showToast(R.string.file_create_fail);
                    return;
                }
                String fullFileName = FileUtil.combinPath(f.getParent(), newName);
                File newFile = new File(fullFileName);
                if (newFile.exists()) {
                    CustomToast.getInstance().showToast(R.string.file_exists);
                } else {
                    if (f.renameTo(newFile)) {
                        finish.finish();
                    } else {
                        CustomToast.getInstance().showToast(R.string.file_rename_fail);
                    }
                }
            }
        });
        dialog.setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 100);
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(fileName.getApplicationWindowToken(), 0);
            }
        });
        dialog.show();
    }

    public static void viewFileInfo(Context context, FileInfo info) {
        View layout = LayoutInflater.from(context).inflate(R.layout.file_info, null);
        ((TextView) layout.findViewById(R.id.file_name)).setText(info.getName());
        ((TextView) layout.findViewById(R.id.file_lastmodified))
                .setText(info.getLastModify());
        ((TextView) layout.findViewById(R.id.file_size)).setText(FileUtil
                .formetFileSize(info.getSize()));
        if (info.isDirectory()) {
            ((TextView) layout.findViewById(R.id.file_contents))
                    .setText("文件夹数量:" + info.getFolderCount() + ", 文件数量:" + info.getFileCount());
        } else {
            layout.findViewById(R.id.file_contents_info).setVisibility(View.GONE);
        }
        MaterialRippleLayout sure = (MaterialRippleLayout) layout.findViewById(R.id.button_accept);
        final IyubaDialog iyubaDialog = new IyubaDialog(context, layout, true, 24);
        iyubaDialog.show();
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iyubaDialog.dismiss();
            }
        });
    }
}