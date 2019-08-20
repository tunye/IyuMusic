package com.iyuba.music.adapter.study;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.study.StudySetActivity;
import com.iyuba.music.download.DownloadFile;
import com.iyuba.music.download.DownloadManager;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.RoundProgressBar;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by 10202 on 2015/10/10.
 */
public class DownloadNewsAdapter extends RecyclerView.Adapter<DownloadNewsAdapter.MyViewHolder> {
    private ArrayList<Article> newsList;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;
    private IOperationFinish finish;
    private Context context;
    private LocalInfoOp localInfoOp;
    private boolean delete;
    private boolean deleteAll;

    private Map<String, Integer> fileMap;

    public DownloadNewsAdapter(Context context, Map<String, Integer> fileMap) {
        this.context = context;
        this.delete = false;
        newsList = new ArrayList<>();
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
            for (Article article : newsList) {
                article.setDelete(false);
            }
            deleteAll = false;
        } else {
            for (Article article : newsList) {
                article.setDelete(true);
            }
            deleteAll = true;
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickLitener(OnRecycleViewItemClickListener onItemClickLitener) {
        onRecycleViewItemClickListener = onItemClickLitener;
    }

    public void setDownloadCompleteClickLitener(IOperationFinish iOperationFinish) {
        finish = iOperationFinish;
    }

    public ArrayList<Article> getDataSet() {
        return newsList;
    }

    public void setDataSet(ArrayList<Article> newses) {
        newsList = newses;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_download_newslist, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Article article = newsList.get(position);
        final int pos = position;
        if (onRecycleViewItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                                    notifyItemChanged(pos);
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
                                onRecycleViewItemClickListener.onItemClick(holder.itemView, pos);
                            }
                        });
                        materialDialog.show();
                    } else {
                        onRecycleViewItemClickListener.onItemClick(holder.itemView, pos);
                    }
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onRecycleViewItemClickListener.onItemLongClick(holder.itemView, pos);
                    return true;
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
                    notifyItemChanged(pos);
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
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newsList.get(holder.getAdapterPosition()).setDelete(holder.delete.isChecked());
            }
        });
        holder.delete.setChecked(article.isDelete());
        if (article.getApp().equals("209")) {
            ImageUtil.loadImage("http://static.iyuba.cn/images/song/" + article.getPicUrl(),
                    holder.pic, R.drawable.default_music);
        } else {
            ImageUtil.loadImage(article.getPicUrl(), holder.pic, R.drawable.default_music);
        }
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    static class MyViewHolder extends RecycleViewHolder {

        TextView title, singer, broadcaster;
        ImageView pic, noExist;
        RoundProgressBar download;
        CheckBox delete;


        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.article_title);
            singer = (TextView) view.findViewById(R.id.article_singer);
            broadcaster = (TextView) view.findViewById(R.id.article_announcer);
            download = (RoundProgressBar) view.findViewById(R.id.roundProgressBar);
            pic = (ImageView) view.findViewById(R.id.article_image);
            delete = (CheckBox) view.findViewById(R.id.item_delete);
            noExist = (ImageView) view.findViewById(R.id.file_notexist);
        }
    }
}
