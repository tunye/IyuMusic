/**
 *
 */
package com.iyuba.music.file;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.iyuba.music.R;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

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

    @Override
    public RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FileViewHolder(LayoutInflater.from(RuntimeManager.getContext()).inflate(R.layout.item_file, parent, false));
    }

    @Override
    public void onBindViewHolder(RecycleViewHolder holder, int position) {
        final FileViewHolder fileViewHolder = (FileViewHolder) holder;
        final FileInfo fileInfo = getItem(position);
        if (onRecycleViewItemClickListener != null) {
            final MaterialRippleLayout rippleView = (MaterialRippleLayout) fileViewHolder.itemView;
            rippleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecycleViewItemClickListener.onItemClick(fileViewHolder.itemView, fileViewHolder.getLayoutPosition());
                }
            });
            rippleView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onRecycleViewItemClickListener.onItemLongClick(fileViewHolder.itemView, fileViewHolder.getLayoutPosition());
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
            name = (TextView) view.findViewById(R.id.file_name);
            content = (TextView) view.findViewById(R.id.file_info);
            icon = (ImageView) view.findViewById(R.id.file_icon);
        }
    }
}
