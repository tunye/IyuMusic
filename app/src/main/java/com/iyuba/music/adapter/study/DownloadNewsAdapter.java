package com.iyuba.music.adapter.study;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.buaa.ct.core.adapter.CoreRecyclerViewAdapter;
import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.util.GetAppColor;
import com.buaa.ct.core.view.CustomToast;
import com.buaa.ct.core.view.recyclerview.RecycleViewHolder;
import com.iyuba.music.R;
import com.iyuba.music.activity.study.StudySetActivity;
import com.iyuba.music.download.DownloadFile;
import com.iyuba.music.download.DownloadManager;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.util.AppImageUtil;
import com.iyuba.music.widget.RoundProgressBar;
import com.iyuba.music.widget.dialog.MyMaterialDialog;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by 10202 on 2015/10/10.
 */
public class DownloadNewsAdapter extends CoreRecyclerViewAdapter<Article,DownloadNewsAdapter.MyViewHolder> {
    private IOperationFinish finish;
    private LocalInfoOp localInfoOp;
    private boolean delete;
    private boolean deleteAll;

    private Map<String, Integer> fileMap;

    public DownloadNewsAdapter(Context context, Map<String, Integer> fileMap) {
        super(context);
        this.delete = false;
        localInfoOp = new LocalInfoOp();
        this.fileMap = fileMap;
    }

    public void setFileMap(Map<String, Integer> map) {
        fileMap = map;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
        notifyDataSetChanged();
    }

    public void setDeleteAll() {
        if (deleteAll) {
            for (Article article : getDatas()) {
                article.setDelete(false);
            }
            deleteAll = false;
        } else {
            for (Article article : getDatas()) {
                article.setDelete(true);
            }
            deleteAll = true;
        }
        notifyDataSetChanged();
    }

    public void setDownloadCompleteClickLitener(IOperationFinish iOperationFinish) {
        finish = iOperationFinish;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_download_newslist, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        super.onBindViewHolder(holder,position);
        final Article article = getDatas().get(position);
        if (onRecycleViewItemClickListener != null) {
            holder.itemView.setOnClickListener(new INoDoubleClick() {
                @Override
                public void onClick(View view) {
                    super.onClick(view);
                    if (delete) {
                        holder.delete.setChecked(!holder.delete.isChecked());
                        article.setDelete(holder.delete.isChecked());
                    } else if (holder.noExist.getVisibility() == View.VISIBLE) {
                        final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
                        materialDialog.setTitle(R.string.app_name);
                        int messageStringId = Integer.parseInt(article.getTextFind());
                        materialDialog.setMessage(context.getString(messageStringId));
                        if (messageStringId == R.string.article_download_file_dismiss) {
                            materialDialog.setPositiveButton(context.getString(R.string.app_del), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    localInfoOp.updateDownload(article.getId(), article.getApp(), 0);
                                    notifyItemChanged(holder.getAdapterPosition());
                                    materialDialog.dismiss();
                                }
                            });
                        } else {
                            materialDialog.setPositiveButton(context.getString(R.string.article_download_set), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    context.startActivity(new Intent(context, StudySetActivity.class));
                                    materialDialog.dismiss();
                                }
                            });
                        }

                        materialDialog.setNegativeButton(context.getString(R.string.article_download_open_still), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                materialDialog.dismiss();
                                onRecycleViewItemClickListener.onItemClick(holder.itemView, holder.getAdapterPosition());
                            }
                        });
                        materialDialog.show();
                    } else {
                        onRecycleViewItemClickListener.onItemClick(holder.itemView, holder.getAdapterPosition());
                    }
                }
            });
        }

        holder.title.setText(article.getTitle());
        if ("209".equals(article.getApp())) {
            holder.singer.setText(context.getString(R.string.article_singer, article.getSinger()));
            holder.broadcaster.setText(context.getString(R.string.article_announcer, article.getBroadcaster()));
        } else {
            holder.singer.setText(article.getContent());
            holder.broadcaster.setVisibility(View.GONE);
        }
        if (fileMap.containsKey(String.valueOf(article.getId()))) {
            if ("209".equals(article.getApp()) && article.getSimple() == 0) {
                switch (fileMap.get(String.valueOf(article.getId()))) {
                    case 2:                                               // 文件整齐
                        holder.noExist.setVisibility(View.GONE);
                        break;
                    case 1:                                               // 存在原唱文件
                        if (ConfigManager.getInstance().getStudyMode() != 0) {
                            article.setTextFind(String.valueOf(R.string.article_download_only_song));
                            holder.noExist.setVisibility(View.VISIBLE);
                        } else {
                            holder.noExist.setVisibility(View.GONE);
                        }
                        break;
                    case -1:                                              // 存在解说文件
                        if (ConfigManager.getInstance().getStudyMode() != 1) {
                            article.setTextFind(String.valueOf(R.string.article_download_only_sound));
                            holder.noExist.setVisibility(View.VISIBLE);
                        } else {
                            holder.noExist.setVisibility(View.GONE);
                        }
                        break;
                }
            } else {
                holder.noExist.setVisibility(View.GONE);
            }
        } else {
            if (localInfoOp.findDataById(article.getApp(), article.getId()).getDownload() == 2) {
                holder.noExist.setVisibility(View.GONE);
            } else {
                article.setTextFind(String.valueOf(R.string.article_download_file_dismiss));
                holder.noExist.setVisibility(View.VISIBLE);
            }
        }
        if (localInfoOp.findDataById(article.getApp(), article.getId()).getDownload() == 2) {
            final int id = article.getId();
            holder.download.setVisibility(View.VISIBLE);
            holder.pic.setVisibility(View.GONE);
            DownloadFile file;
            Runnable refreshItem = new Runnable() {
                @Override
                public void run() {
                    notifyItemChanged(holder.getAdapterPosition());
                }
            };
            for (int i = 0; i < DownloadManager.getInstance().fileList.size(); i++) {
                file = DownloadManager.getInstance().fileList.get(i);
                if (file.id == id) {
                    switch (file.downloadState) {
                        case "start":
                            holder.download.setCricleProgressColor(GetAppColor.getInstance().getAppColor());
                            holder.download.setTextColor(GetAppColor.getInstance().getAppColor());
                            if (file.fileSize != 0 && file.downloadSize != 0) {
                                holder.download.setMax(file.fileSize);
                                holder.download.setProgress(file.downloadSize);
                            } else {
                                holder.download.setMax(1);
                                holder.download.setProgress(0);
                            }
                            holder.itemView.postDelayed(refreshItem, 500);
                            break;
                        case "half_finish":
                            holder.download.setCricleProgressColor(GetAppColor.getInstance().getAppColorAccent());
                            holder.download.setTextColor(GetAppColor.getInstance().getAppColor());
                            if (file.fileSize != 0 && file.downloadSize != 0) {
                                holder.download.setMax(file.fileSize);
                                holder.download.setProgress(file.downloadSize);
                            } else {
                                holder.download.setMax(1);
                                holder.download.setProgress(0);
                            }
                            holder.itemView.postDelayed(refreshItem, 500);
                            break;
                        case "finish":
                            localInfoOp.updateDownload(file.id, article.getApp(), 1);
                            CustomToast.getInstance().showToast(R.string.article_download_success);
                            DownloadManager.getInstance().fileList.remove(file);
                            holder.itemView.removeCallbacks(refreshItem);
                            holder.itemView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish.finish();
                                }
                            }, 500);
                            break;
                        case "fail":
                            localInfoOp.updateDownload(file.id, article.getApp(), 3);
                            CustomToast.getInstance().showToast(R.string.article_download_fail);
                            DownloadManager.getInstance().fileList.remove(file);
                            holder.itemView.removeCallbacks(refreshItem);
                            holder.itemView.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish.finish();
                                }
                            }, 500);
                            break;
                    }

                    break;
                }
            }
        } else {
            holder.download.setVisibility(View.GONE);
            holder.pic.setVisibility(View.VISIBLE);
        }
        if (delete) {
            holder.delete.setVisibility(View.VISIBLE);
        } else {
            holder.delete.setVisibility(View.GONE);
        }
        holder.delete.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                getDatas().get(holder.getAdapterPosition()).setDelete(holder.delete.isChecked());
            }
        });
        holder.delete.setChecked(article.isDelete());
        if (article.getApp().equals("209")) {
            AppImageUtil.loadImage("http://static.iyuba.cn/images/song/" + article.getPicUrl(),
                    holder.pic, R.drawable.default_music);
        } else {
            AppImageUtil.loadImage(article.getPicUrl(), holder.pic, R.drawable.default_music);
        }
    }

    static class MyViewHolder extends CoreRecyclerViewAdapter.MyViewHolder {

        TextView title, singer, broadcaster;
        ImageView pic, noExist;
        RoundProgressBar download;
        CheckBox delete;


        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.article_title);
            singer = view.findViewById(R.id.article_singer);
            broadcaster = view.findViewById(R.id.article_announcer);
            download = view.findViewById(R.id.roundProgressBar);
            pic = view.findViewById(R.id.article_image);
            delete = view.findViewById(R.id.item_delete);
            noExist = view.findViewById(R.id.file_notexist);
        }
    }
}
