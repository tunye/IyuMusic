package com.iyuba.music.adapter.study;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.download.DownloadService;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.original.Original;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.util.Mathematics;
import com.iyuba.music.util.UploadFile;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.player.SimplePlayer;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/10.
 */
public class ReadAdapter extends RecyclerView.Adapter<ReadAdapter.MyViewHolder> {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private ArrayList<Original> originals;
    private Context context;
    private int curItem = -1;
    private SimplePlayer player, simplePlayer;
    private TextView curText;
    private boolean curRecord;
    private boolean isRecord;
    private Article curArticle;
    private MediaRecorder mediaRecorder;

    public ReadAdapter(Context context) {
        this.context = context;
        simplePlayer = new SimplePlayer(context);
        curItem = 0;
        isRecord = false;
        originals = new ArrayList<>();
        player = new SimplePlayer(context);
        curArticle = StudyManager.instance.getCurArticle();
        player.setVideoPath(getPath());
        mediaRecorder = new MediaRecorder();
    }

    public void setDataSet(ArrayList<Original> originals) {
        this.originals = originals;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_read, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Original original = originals.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curItem != holder.getLayoutPosition()) {
                    int oldPos = curItem;
                    curItem = holder.getLayoutPosition();
                    player.seekTo((int) original.getStartTime() * 1000);
                    player.start();
                    Message message = new Message();
                    message.what = 0;
                    message.arg1 = (int) original.getEndTime() * 1000;
                    if (message.arg1 == 0) {
                        if (position + 1 == originals.size()) {
                            message.arg1 = player.getDuration() - 500;
                        } else {
                            message.arg1 = (int) originals.get(position + 1).getStartTime() * 1000;
                        }
                    }
                    notifyItemChanged(oldPos);
                    notifyItemChanged(curItem);
                    handler.sendMessage(message);
                }
            }
        });
        holder.num.setText(String.valueOf(position + 1));
        holder.english.setText(original.getSentence());
        holder.chinese.setText(original.getSentence_cn());
        if (position == curItem) {
            curText = holder.recordTime;
            holder.readControl.setVisibility(View.VISIBLE);
            if (curRecord) {
                holder.recordPlay.setVisibility(View.VISIBLE);
                holder.recordSend.setVisibility(View.VISIBLE);
            } else {
                holder.recordPlay.setVisibility(View.INVISIBLE);
                holder.recordSend.setVisibility(View.INVISIBLE);
            }
        } else {
            holder.readControl.setVisibility(View.GONE);
            holder.recordPlay.setVisibility(View.INVISIBLE);
            holder.recordSend.setVisibility(View.INVISIBLE);
            curRecord = false;
        }
        if (player.isPlaying()) {
            holder.play.setImageResource(R.drawable.read_play);
        } else {
            holder.play.setImageResource(R.drawable.read_pause);
        }
        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.seekTo((int) original.getStartTime() * 1000);
                player.start();
                Message message = new Message();
                message.what = 0;
                message.arg1 = (int) original.getEndTime() * 1000;
                if (position + 1 == originals.size()) {
                    message.arg1 = player.getDuration() - 500;
                } else {
                    message.arg1 = (int) originals.get(position + 1).getStartTime() * 1000;
                }
                notifyItemChanged(curItem);
                handler.sendMessage(message);
            }
        });
        holder.record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                curRecord = true;
                if (isRecord) {
                    isRecord = false;
                    holder.record.setImageResource(R.drawable.record);
                    mediaRecorder.stop();
                    mediaRecorder.reset();
                    handler.sendEmptyMessage(3);
                    holder.recordPlay.setVisibility(View.VISIBLE);
                    holder.recordSend.setVisibility(View.VISIBLE);
                } else {
                    if (player.isPlaying()) {
                        player.pause();
                        holder.play.setImageResource(R.drawable.read_pause);
                        handler.removeMessages(0);
                    }
                    holder.record.setImageResource(R.drawable.recording);
                    isRecord = true;
                    mediaRecorder.reset();
                    initRecorder();
                }
            }
        });
        holder.recordPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simplePlayer.setVideoPath(ConstantManager.instance.getRecordFile());
                simplePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        simplePlayer.start();
                        Message message = new Message();
                        message.what = 4;
                        message.arg1 = simplePlayer.getDuration();
                        handler.sendMessage(message);
                    }
                });
                simplePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        simplePlayer.reset();
                        handler.sendEmptyMessage(5);
                    }
                });
            }
        });
        holder.recordSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountManager.instance.checkUserLogin()) {
                    new UploadVoice().start();
                } else {
                    CustomDialog.showLoginDialog(context);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return originals.size();
    }

    private String getPath() {
        String url = DownloadService.getSongUrl(curArticle.getApp(), curArticle.getMusicUrl());
        StringBuilder localUrl = new StringBuilder();
        localUrl.append(ConstantManager.instance.getMusicFolder()).append(File.separator).append(curArticle.getId()).append(".mp3");
        File localFile = new File(localUrl.toString());
        if (localFile.exists()) {
            return localUrl.toString();
        } else {
            return url.toString();
        }
    }

    private void initRecorder() {
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder
                .setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder
                .setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(ConstantManager.instance.getRecordFile());
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            Message message = new Message();
            message.arg1 = 0;
            message.what = 2;
            handler.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        if (player.isPlaying()) {
            player.pause();
        }
        if (simplePlayer.isPlaying()) {
            simplePlayer.pause();
        }
        player.stopPlayback();
        simplePlayer.stopPlayback();
        handler.removeMessages(0);
        handler.removeMessages(2);
        handler.removeMessages(4);
    }

    static class MyViewHolder extends RecycleViewHolder {

        TextView num, english, chinese, recordTime;
        View readControl;
        ImageView play, record, recordPlay, recordSend;

        public MyViewHolder(View view) {
            super(view);
            num = (TextView) view.findViewById(R.id.read_num);
            english = (TextView) view.findViewById(R.id.read_text);
            chinese = (TextView) view.findViewById(R.id.read_text_zh);
            readControl = view.findViewById(R.id.read_control);
            play = (ImageView) view.findViewById(R.id.read_play);
            record = (ImageView) view.findViewById(R.id.read_record);
            recordPlay = (ImageView) view.findViewById(R.id.read_record_play);
            recordSend = (ImageView) view.findViewById(R.id.read_record_send);
            recordTime = (TextView) view.findViewById(R.id.record_time);
        }
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<ReadAdapter> {
        @Override
        public void handleMessageByRef(final ReadAdapter adapter, Message msg) {
            Message message;
            switch (msg.what) {
                case 0:
                    if (adapter.player.getCurrentPosition() < msg.arg1) {
                        adapter.curText.setText(Mathematics.formatTime((msg.arg1 - adapter.player.getCurrentPosition()) / 1000));
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
                    adapter.curText.setText(Mathematics.formatTime(msg.arg1 / 1000));
                    message = new Message();
                    message.what = 2;
                    message.arg1 = msg.arg1 + 1000;
                    adapter.handler.sendMessageDelayed(message, 1000);
                    break;
                case 3:
                    adapter.handler.removeMessages(2);
                    adapter.curText.setText("");
                    break;
                case 4:
                    adapter.curText.setText(Mathematics.formatTime(msg.arg1 / 1000));
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

    public class UploadVoice extends Thread {

        public UploadVoice() {
        }

        @Override
        public void run() {

            super.run();
            StringBuilder sb = new StringBuilder(
                    "http://daxue.iyuba.com/appApi/UnicomApi?protocol=60003&platform=android&appName=music&format=json");
            sb.append("&userid=").append(
                    AccountManager.instance.getUserId());
            sb.append("&shuoshuotype=").append(2);
            sb.append("&voaid=").append(curArticle.getId());
            final File file = new File(ConstantManager.instance.getRecordFile());
            UploadFile.postSound(sb.toString(), file, new IOperationResult() {
                @Override
                public void success(Object object) {
                    file.delete();
                    CustomToast.INSTANCE.showToast(R.string.read_send_success);
                }

                @Override
                public void fail(Object object) {
                    CustomToast.INSTANCE.showToast(R.string.read_send_fail);
                }
            });
        }
    }
}
