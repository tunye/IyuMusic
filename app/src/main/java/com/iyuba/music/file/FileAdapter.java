/**
 *
 */
package com.iyuba.music.file;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;
import com.iyuba.music.widget.view.MaterialRippleLayout;

import java.util.ArrayList;


/**
 * @author zhuch
 *         <p/>
 *         文件浏览器相关类
 */
public class FileAdapter extends RecyclerView.Adapter<RecycleViewHolder> {
    private ArrayList<FileInfo> fileInfos;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;

    public FileAdapter() {
        fileInfos = new ArrayList<>();
    }

    public void setOnItemClickLitener(OnRecycleViewItemClickListener onItemClickLitener) {
        onRecycleViewItemClickListener = onItemClickLitener;
    }

    public void setDataSet(ArrayList<FileInfo> fileInfos) {
        this.fileInfos = fileInfos;
        notifyDataSetChanged();
    }

    public void removeData(int position) {
        fileInfos.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return fileInfos.size();
    }

    private FileInfo getItem(int position) {
        return fileInfos.get(position);
    }

    @NonNull
    @Override
    public RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FileViewHolder(LayoutInflater.from(RuntimeManager.getInstance().getContext()).inflate(R.layout.item_file, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewHolder holder, int position) {
        final FileViewHolder fileViewHolder = (FileViewHolder) holder;
        final FileInfo fileInfo = getItem(position);
        final int pos = position;
        if (onRecycleViewItemClickListener != null) {
            final MaterialRippleLayout rippleView = (MaterialRippleLayout) fileViewHolder.itemView;
            rippleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecycleViewItemClickListener.onItemClick(fileViewHolder.itemView, pos);
                }
            });
            rippleView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onRecycleViewItemClickListener.onItemLongClick(fileViewHolder.itemView, pos);
                    return true;
                }
            });
        }
        fileViewHolder.name.setText(fileInfo.getName());
        if (fileInfo.isDirectory()) {
            fileViewHolder.content.setText(fileInfo.getLastModify());
        } else {
            fileViewHolder.content.setText(FileUtil.formetFileSize(fileInfo.getSize()) + "   "
                    + fileInfo.getLastModify());
        }
        fileViewHolder.icon.setBackgroundResource(fileInfo.getIconResourceId());
    }

    private static class FileViewHolder extends RecycleViewHolder {

        TextView name;
        ImageView icon;
        TextView content;


        FileViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.file_name);
            content = view.findViewById(R.id.file_info);
            icon = view.findViewById(R.id.file_icon);
        }
    }
}
