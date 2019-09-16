/**
 *
 */
package com.iyuba.music.file;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buaa.ct.core.adapter.CoreRecyclerViewAdapter;
import com.iyuba.music.R;


/**
 * @author zhuch
 *         <p/>
 *         文件浏览器相关类
 */
public class FileAdapter extends CoreRecyclerViewAdapter<FileInfo, FileAdapter.FileViewHolder> {
    private OnItemLongClickListener onItemLongClickListener;

    public FileAdapter(Context context, @Nullable OnItemLongClickListener listener) {
        super(context);
        onItemLongClickListener = listener;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FileViewHolder(LayoutInflater.from(context).inflate(R.layout.item_file, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final FileViewHolder fileViewHolder, int position) {
        super.onBindViewHolder(fileViewHolder, position);
        if (onItemLongClickListener != null) {
            fileViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemLongClickListener.onClick(fileViewHolder.getAdapterPosition());
                    return false;
                }
            });
        }
        final FileInfo fileInfo = getDatas().get(position);
        fileViewHolder.name.setText(fileInfo.getName());
        if (fileInfo.isDirectory()) {
            fileViewHolder.content.setText(fileInfo.getLastModify());
        } else {
            fileViewHolder.content.setText(FileUtil.formetFileSize(fileInfo.getSize()) + "   "
                    + fileInfo.getLastModify());
        }
        fileViewHolder.icon.setBackgroundResource(fileInfo.getIconResourceId());
    }

    public interface OnItemLongClickListener {
        void onClick(int pos);
    }

    static class FileViewHolder extends CoreRecyclerViewAdapter.MyViewHolder {

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
