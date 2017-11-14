package com.iyuba.music.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.adapter.study.DownloadNewsAdapter;
import com.iyuba.music.download.DownloadFile;
import com.iyuba.music.download.DownloadManager;
import com.iyuba.music.download.DownloadTask;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.file.FileUtil;
import com.iyuba.music.fragment.StartFragment;
import com.iyuba.music.ground.VideoPlayerActivity;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.util.ThreadPoolUtil;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by 10202 on 2016/3/7.
 */
public class DownloadSongActivity extends BaseActivity implements IOnClickListener {
    private ArrayList<Article> downloaded, downloading;
    private DownloadNewsAdapter downloadedAdapter, downloadingAdapter;
    private LocalInfoOp localInfoOp;
    private ArticleOp articleOp;
    private View downloadingBar;
    private ScrollView downloadScroll;
    private Map<String, Integer> fileMap;
    private TextView toolBarOperSub, downloadingDel, downloadingContinue, downloadedDel,
            downloadedStatic, downloadingStatic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_song);
        context = this;
        localInfoOp = new LocalInfoOp();
        articleOp = new ArticleOp();
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        getFileMap();
        downloadScroll = (ScrollView) findViewById(R.id.download_scroll);
        toolBarOperSub = (TextView) findViewById(R.id.toolbar_oper_sub);
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        RecyclerView downloadedRecycleView = (RecyclerView) findViewById(R.id.downloaded_recyclerview);
        downloadedRecycleView.setLayoutManager(new LinearLayoutManager(context));
        downloadedRecycleView.addItemDecoration(new DividerItemDecoration());
        downloadedDel = (TextView) findViewById(R.id.downloaded_delete);
        downloadedStatic = (TextView) findViewById(R.id.downloaded_statistic);
        final RecyclerView downloadingRecycleView = (RecyclerView) findViewById(R.id.downloading_recyclerview);
        downloadingRecycleView.setLayoutManager(new LinearLayoutManager(context));
        downloadingRecycleView.addItemDecoration(new DividerItemDecoration());
        downloadingStatic = (TextView) findViewById(R.id.downloading_statistic);
        downloadingDel = (TextView) findViewById(R.id.downloading_delete);
        downloadingContinue = (TextView) findViewById(R.id.downloading_start);
        ((SimpleItemAnimator) downloadingRecycleView.getItemAnimator()).setSupportsChangeAnimations(false);
        downloadingBar = findViewById(R.id.downloading_bar);
        downloadedAdapter = new DownloadNewsAdapter(context,fileMap);
        downloadedAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String app = downloaded.get(position).getApp();
                if (app.equals("229") || app.equals("217") || app.equals("213")) {
                    ArrayList<Article> temp = new ArrayList<>();
                    temp.add(downloaded.get(position));
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra("articleList", temp);
                    context.startActivity(intent);
                } else {
                    StudyManager.getInstance().setStartPlaying(true);
                    StudyManager.getInstance().setListFragmentPos(DownloadSongActivity.this.getClass().getName());
                    StudyManager.getInstance().setSourceArticleList(downloaded);
                    StudyManager.getInstance().setLesson("music");
                    StudyManager.getInstance().setCurArticle(downloaded.get(position));
                    context.startActivity(new Intent(context, StudyActivity.class));
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        downloadedRecycleView.setAdapter(downloadedAdapter);

        downloadingAdapter = new DownloadNewsAdapter(context,fileMap);
        downloadingAdapter.setDownloadCompleteClickLitener(new IOperationFinish() {
            @Override
            public void finish() {
                getFileMap();
                downloadedAdapter.setFileMap(fileMap);
                getData();
            }
        });
        downloadingAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CustomToast.getInstance().showToast(R.string.article_downloading);
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        downloadingRecycleView.setAdapter(downloadingAdapter);
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolBarLayout.setOnTouchListener(new IOnDoubleClick(this, context.getString(R.string.list_double)));
        toolBarOperSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toolBarOperSub.getText().equals(getString(R.string.select_all))) {
                    downloadingAdapter.setDeleteAll();
                    downloadedAdapter.setDeleteAll();
                } else {
                    CustomDialog.clearDownload(context, R.string.article_clear_download_hint, new IOperationResult() {
                        @Override
                        public void success(Object object) {
                            final File file = new File(ConstantManager.musicFolder);
                            if (file.exists()) {
                                downloadedAdapter.setDataSet(new ArrayList<Article>());
                                downloadingAdapter.setDataSet(new ArrayList<Article>());
                                localInfoOp.clearAllDownload();
                                ThreadPoolUtil.getInstance().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        FileUtil.deleteFile(file);
                                    }
                                });
                            }
                            getData();
                        }

                        @Override
                        public void fail(Object object) {

                        }
                    });
                }
            }
        });
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toolbarOper.getText().equals(context.getString(R.string.article_edit))) {
                    downloadedAdapter.setDelete(true);
                    downloadingAdapter.setDelete(true);
                    toolBarOperSub.setText(R.string.article_select_all);
                    toolbarOper.setText(R.string.app_del);
                } else {
                    downloadingAdapter.setDelete(false);
                    downloadedAdapter.setDelete(false);
                    toolbarOper.setText(R.string.article_edit);
                    toolBarOperSub.setText(R.string.article_clear);
                    downloading = downloadingAdapter.getDataSet();
                    for (Article temp : downloading) {
                        if (temp.isDelete()) {
                            deleteFile(temp.getId(), temp.getApp(), "2");
                            localInfoOp.updateDownload(temp.getId(), temp.getApp(), 0);
                        }
                    }

                    downloaded = downloadedAdapter.getDataSet();
                    for (Article temp : downloaded) {
                        if (temp.isDelete()) {
                            deleteFile(temp.getId(), temp.getApp(), "1");
                            localInfoOp.updateDownload(temp.getId(), temp.getApp(), 0);
                        }
                    }
                    if (DownloadSongActivity.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                        StudyManager.getInstance().setSourceArticleList(downloaded);
                    }
                    getData();
                }
            }
        });
        downloadedDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomDialog.clearDownload(context, R.string.article_clear_download_hint, new IOperationResult() {
                    @Override
                    public void success(Object object) {
                        for (Article temp : downloaded) {
                            deleteFile(temp.getId(), temp.getApp(), "1");
                        }
                        downloadedAdapter.setDataSet(new ArrayList<Article>());
                        localInfoOp.clearDownloaded();
                        downloadedStatic.setText(context.getString(R.string.article_downloaded_static, 0));
                    }

                    @Override
                    public void fail(Object object) {

                    }
                });
            }
        });
        downloadingDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadManager.getInstance().fileList.clear();
                StartFragment.checkTmpFile();
                localInfoOp.clearDownloading();
                downloadingBar.setVisibility(View.GONE);
                downloadingAdapter.setDataSet(new ArrayList<Article>());
            }
        });
        downloadingContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<LocalInfo> downloadingContinue = localInfoOp.findDataByShouldContinue();
                DownloadFile downloadFile;
                for (LocalInfo localInfo : downloadingContinue) {
                    if (localInfo.getDownload() == 3) {
                        localInfoOp.updateDownload(localInfo.getId(), localInfo.getApp(), 2);
                        downloadFile = new DownloadFile();
                        downloadFile.id = localInfo.getId();
                        downloadFile.downloadState = "start";
                        DownloadManager.getInstance().fileList.add(downloadFile);
                        for (Article article : downloading) {
                            if (article.getId() == localInfo.getId()) {
                                new DownloadTask(article).start();
                                break;
                            }
                        }
                    }
                }
                downloadingAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.classify_local);
        toolbarOper.setText(R.string.article_edit);
        toolBarOperSub.setText(R.string.article_clear);
    }

    @Override
    public void onClick(View view, Object message) {
        downloadScroll.scrollTo(0, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        getDownloadedData();
        downloading = new ArrayList<>();
        ArrayList<LocalInfo> temp = localInfoOp.findDataByDownloading();
        if (temp.size() == 0) {
            downloadingBar.setVisibility(View.GONE);
            downloadingAdapter.setDataSet(new ArrayList<Article>());
        } else {
            downloadingBar.setVisibility(View.VISIBLE);
            Article article;
            for (LocalInfo local : temp) {
                article = articleOp.findById(local.getApp(), local.getId());
                article.setExpireContent(local.getSeeTime());
                article.setId(local.getId());
                article.setApp(local.getApp());
                downloading.add(article);
            }
            downloadingAdapter.setDataSet(downloading);
            downloadingStatic.setText(context.getString(R.string.article_downloading_static, downloading.size()));
        }

        downloadScroll.scrollTo(0, 0);
    }

    private void getDownloadedData() {
        downloaded = new ArrayList<>();
        ArrayList<LocalInfo> temp = localInfoOp.findDataByDownloaded();
        Article article;
        for (LocalInfo local : temp) {
            article = articleOp.findById(local.getApp(), local.getId());
            article.setExpireContent(local.getSeeTime());
            article.setId(local.getId());
            article.setApp(local.getApp());
            downloaded.add(article);
        }
        downloadedAdapter.setDataSet(downloaded);
        downloadedStatic.setText(context.getString(R.string.article_downloaded_static, downloaded.size()));
        if (DownloadSongActivity.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
            StudyManager.getInstance().setSourceArticleList(downloaded);
        }
    }

    private void deleteFile(int id, String app, String state) {
        String baseUrl = ConstantManager.musicFolder + File.separator;
        File deleteFile;
        switch (app) {
            case "209": {
                deleteFile = new File(baseUrl + id + (state.equals("1") ? ".mp3" : ".tmp"));
                if (deleteFile.exists()) {
                    deleteFile.delete();
                }
                deleteFile = new File(baseUrl + id + (state.equals("1") ? "s.mp3" : "s.tmp"));
                if (deleteFile.exists()) {
                    deleteFile.delete();
                }
                break;
            }
            case "229":
            case "217":
            case "213": {
                deleteFile = new File(baseUrl + app + "-" + id + (state.equals("1") ? ".mp4" : ".tmp"));
                if (deleteFile.exists()) {
                    deleteFile.delete();
                }
                break;
            }
            default: {
                deleteFile = new File(baseUrl + app + "-" + id + (state.equals("1") ? ".mp3" : ".tmp"));
                if (deleteFile.exists()) {
                    deleteFile.delete();
                }
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (downloadedAdapter.isDelete() || downloadingAdapter.isDelete()) {
            downloadedAdapter.setDelete(false);
            downloadingAdapter.setDelete(false);
            toolbarOper.setText(R.string.article_edit);
            toolBarOperSub.setText(R.string.article_clear);
        } else {
            super.onBackPressed();
        }
    }

    public void getFileMap() {
        fileMap = new ArrayMap<>();
        File packageFile = new File(ConstantManager.musicFolder);
        if (packageFile.exists() && packageFile.list() != null) {
            String id;
            for (String fileName : packageFile.list()) {
                if (fileName.endsWith(".tmp")) {
                    continue;
                }
                fileName = fileName.split("\\.")[0];
                if (!fileName.contains("-")) {
                    if (fileName.endsWith("s")) {
                        id = fileName.substring(0, fileName.length() - 1);
                        if (fileMap.containsKey(id)) {
                            fileMap.put(id, 2);
                        } else {
                            fileMap.put(id, -1);
                        }
                    } else {
                        id = fileName;
                        if (fileMap.containsKey(id)) {
                            fileMap.put(id, 2);
                        } else {
                            fileMap.put(id, 1);
                        }
                    }
                } else {
                    String[] temp = fileName.split("-");
                    fileMap.put(temp[1], Integer.parseInt(temp[0]));
                }
            }
        }
    }
}
