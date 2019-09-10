package com.iyuba.music.adapter.study;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buaa.ct.core.adapter.CoreRecyclerViewAdapter;
import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.util.ThreadPoolUtil;
import com.buaa.ct.core.view.CustomToast;
import com.buaa.ct.core.view.recyclerview.RecycleViewHolder;
import com.iflytek.cloud.EvaluatorListener;
import com.iflytek.cloud.EvaluatorResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.ise.result.Result;
import com.iflytek.ise.result.xml.XmlResultParser;
import com.iyuba.assessment.IseManager;
import com.iyuba.music.R;
import com.iyuba.music.download.DownloadUtil;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.original.Original;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IOperationResultInt;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.UploadFile;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.dialog.AssessmentDialog;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.iyuba.music.widget.player.SimplePlayer;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/10.
 */
public class ReadAdapter extends CoreRecyclerViewAdapter<Original,ReadAdapter.MyViewHolder> implements IOperationResultInt {
    private Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private int curItem = -1;
    private SimplePlayer player, simplePlayer;
    private TextView curText;
    private boolean isRecord;
    private Article curArticle;
    private IyubaDialog waittingDialog;
    private AssessmentDialog assessmentDialog;
    private EvaluatorListener evaluatorListener = new EvaluatorListener() {
        private static final String TAG = "assessment";

        @Override
        public void onResult(EvaluatorResult result, boolean isLast) {
            if (isLast) {
                waittingDialog.dismiss();
                XmlResultParser resultParser = new XmlResultParser();
                Result resultEva = resultParser.parse(result.getResultString());
                if (resultEva.is_rejected) {
                    CustomToast.getInstance().showToast(R.string.read_refused);
                } else {
                    assessmentDialog.show(resultEva.total_score * 20);
                }
                IseManager.getInstance(context).transformPcmToAmr();
            }
        }

        @Override
        public void onError(SpeechError error) {
            waittingDialog.dismiss();
            if (error != null) {
                CustomToast.getInstance().showToast("error:" + error.getErrorCode() + "," + error.getErrorDescription());
            }
        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {
            handler.sendEmptyMessage(3);
            waittingDialog.show();
        }

        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }

        @Override
        public void onVolumeChanged(int volume, byte[] arg1) {

        }
    };

    public ReadAdapter(Context context) {
        super(context);
        simplePlayer = new SimplePlayer(context);
        curItem = 0;
        isRecord = false;
        waittingDialog = WaitingDialog.create(context, context.getString(R.string.read_assessment));
        assessmentDialog = new AssessmentDialog(context);
        player = new SimplePlayer(context);
        curArticle = StudyManager.getInstance().getCurArticle();
        player.setVideoPath(getPath());
        assessmentDialog.setListener(this);
    }
    
    @Override
    public void performance(int index) {
        switch (index) {
            case 0:
                if (AccountManager.getInstance().checkUserLogin()) {
                    sendVoice();
                } else {
                    CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                        @Override
                        public void finish() {
                            sendVoice();
                        }
                    });
                }
                break;
            case 1:
                simplePlayer.setVideoPath(ConstantManager.recordFile + IseManager.AMR_SUFFIX);
                simplePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        simplePlayer.start();
                        handler.obtainMessage(4, simplePlayer.getDuration(), 0).sendToTarget();
                    }
                });
                simplePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        simplePlayer.reset();
                        handler.sendEmptyMessage(5);
                    }
                });
                break;
            case 2:
                resetFunction();
                isRecord = true;
                initRecorder(getDatas().get(curItem).getSentence());
                break;
        }
    }

    private void sendVoice() {
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                String sb = "http://daxue.iyuba.cn/appApi/UnicomApi?protocol=60003&platform=android&appName=music&format=json" + "&userid=" +
                        AccountManager.getInstance().getUserId() +
                        "&shuoshuotype=" + 2 +
                        "&voaid=" + curArticle.getId();
                final File file = new File(ConstantManager.recordFile + IseManager.AMR_SUFFIX);
                UploadFile.postSound(sb, file, new IOperationResult() {
                    @Override
                    public void success(Object object) {
                        file.delete();
                        CustomToast.getInstance().showToast(R.string.read_send_success);
                    }

                    @Override
                    public void fail(Object object) {
                        CustomToast.getInstance().showToast(R.string.read_send_fail);
                    }
                });
            }
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_read, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        super.onBindViewHolder(holder,position);
        final Original original = getDatas().get(position);
        holder.itemView.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                if (curItem != holder.getAdapterPosition()) {
                    int oldPos = curItem;
                    curItem = holder.getAdapterPosition();
                    notifyItemChanged(oldPos);
                    resetFunction();
                    player.seekTo((int) original.getStartTime() * 1000);
                    player.start();
                    int arg1 = (int) original.getEndTime() * 1000;
                    if (arg1 == 0) {
                        if (holder.getAdapterPosition() + 1 == getDatas().size()) {
                            arg1 = player.getDuration() - 500;
                        } else {
                            arg1 = (int) getDatas().get(holder.getAdapterPosition() + 1).getStartTime() * 1000;
                        }
                    }
                    handler.obtainMessage(0, arg1, 0).sendToTarget();
                }
            }
        });
        holder.num.setText(String.valueOf(position + 1));
        holder.english.setText(original.getSentence());
        if (TextUtils.isEmpty(original.getSentence_cn())) {
            holder.chinese.setVisibility(View.GONE);
        } else {
            holder.chinese.setText(original.getSentence_cn());
            holder.chinese.setVisibility(View.VISIBLE);
        }
        if (position == curItem) {
            curText = holder.recordTime;
            holder.readControl.setVisibility(View.VISIBLE);
        } else {
            holder.readControl.setVisibility(View.GONE);
        }
        if (player.isPlaying()) {
            holder.play.setImageResource(R.drawable.read_play);
        } else {
            holder.play.setImageResource(R.drawable.read_pause);
        }
        if (isRecord) {
            holder.record.setImageResource(R.drawable.recording);
        } else {
            holder.record.setImageResource(R.drawable.record);
        }
        holder.play.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                resetFunction();
                if (!player.isPlaying()) {
                    player.seekTo((int) (original.getStartTime() * 1000));
                    player.start();
                    int arg1;
                    if (holder.getAdapterPosition() + 1 == getDatas().size()) {
                        arg1 = player.getDuration() - 500;
                    } else {
                        arg1 = (int) (getDatas().get(holder.getAdapterPosition() + 1).getStartTime() * 1000);
                    }
                    handler.obtainMessage(0, arg1, 0).sendToTarget();
                }
            }
        });
        holder.record.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                if (isRecord) {
                    IseManager.getInstance(context).stopEvaluate();
                    handler.sendEmptyMessage(3);
                    waittingDialog.show();
                } else {
                    resetFunction();
                    isRecord = true;
                    initRecorder(original.getSentence());
                }
            }
        });
    }

    private String getPath() {
        String url = DownloadUtil.getSongUrl(curArticle.getApp(), curArticle.getMusicUrl());
        StringBuilder localUrl = new StringBuilder();
        localUrl.append(ConstantManager.musicFolder).append(File.separator).append(curArticle.getId()).append(".mp3");
        File localFile = new File(localUrl.toString());
        if (localFile.exists()) {
            return localUrl.toString();
        } else {
            return url;
        }
    }

    private void initRecorder(String sentence) {
        IseManager.getInstance(context).startEvaluate(sentence, ConstantManager.recordFile, evaluatorListener);
        handler.obtainMessage(2, 0, 0).sendToTarget();
    }

    private void resetFunction() {
        if (player.isPrepared() && player.isPlaying()) {
            player.pause();
        }
        if (isRecord) {
            isRecord = false;
            IseManager.getInstance(context).cancelEvaluate();
        }
        if (simplePlayer.isPrepared() && simplePlayer.isPlaying()) {
            simplePlayer.pause();
            simplePlayer.reset();
        }
        handler.removeCallbacksAndMessages(null);
        notifyItemChanged(curItem);
    }

    public void onDestroy() {
        player.stopPlayback();
        simplePlayer.stopPlayback();
        if (isRecord) {
            IseManager.getInstance(context).cancelEvaluate();
        }
        IseManager.getInstance(context).releaseResource();
        handler.removeCallbacksAndMessages(null);
    }

    static class MyViewHolder extends CoreRecyclerViewAdapter.MyViewHolder {

        TextView num, english, chinese, recordTime;
        View readControl;
        ImageView play, record;

        public MyViewHolder(View view) {
            super(view);
            num = view.findViewById(R.id.read_num);
            english = view.findViewById(R.id.read_text);
            chinese = view.findViewById(R.id.read_text_zh);
            readControl = view.findViewById(R.id.read_control);
            play = view.findViewById(R.id.read_play);
            record = view.findViewById(R.id.read_record);
            recordTime = view.findViewById(R.id.record_time);
        }
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<ReadAdapter> {
        @Override
        public void handleMessageByRef(final ReadAdapter adapter, Message msg) {
            Message message;
            switch (msg.what) {
                case 0:
                    if (adapter.player.getCurrentPosition() < msg.arg1) {
                        adapter.curText.setText(DateFormat.formatTime((msg.arg1 - adapter.player.getCurrentPosition()) / 1000));
                        message = new Message();
                        message.what = 0;
                        message.arg1 = msg.arg1;
                        adapter.handler.sendMessageDelayed(message, 300);
                    } else {
                        adapter.handler.sendEmptyMessage(1);
                    }
                    break;
                case 1:
                    adapter.player.pause();
                    adapter.curText.setText("");
                    if (adapter.curItem != -1) {
                        adapter.notifyItemChanged(adapter.curItem);
                    }
                    adapter.handler.removeMessages(0);
                    break;
                case 2:
                    adapter.curText.setText(DateFormat.formatTime(msg.arg1 / 1000));
                    message = new Message();
                    message.what = 2;
                    message.arg1 = msg.arg1 + 1000;
                    adapter.handler.sendMessageDelayed(message, 1000);
                    break;
                case 3:
                    adapter.isRecord = false;
                    adapter.handler.removeMessages(2);
                    adapter.curText.setText("");
                    adapter.notifyItemChanged(adapter.curItem);
                    break;
                case 4:
                    adapter.curText.setText(DateFormat.formatTime(msg.arg1 / 1000));
                    message = new Message();
                    message.what = 4;
                    message.arg1 = msg.arg1 - 1000;
                    adapter.handler.sendMessageDelayed(message, 1000);
                    break;
                case 5:
                    adapter.handler.removeMessages(4);
                    adapter.curText.setText("");
                    break;
            }
        }
    }
}
