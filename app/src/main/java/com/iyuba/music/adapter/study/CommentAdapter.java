package com.iyuba.music.adapter.study;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.buaa.ct.core.adapter.CoreRecyclerViewAdapter;
import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.view.CustomToast;
import com.buaa.ct.core.view.MaterialRippleLayout;
import com.buaa.ct.core.view.recyclerview.RecycleViewHolder;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iyuba.music.R;
import com.iyuba.music.activity.me.PersonalHomeActivity;
import com.iyuba.music.entity.comment.Comment;
import com.iyuba.music.entity.comment.CommentAgreeOp;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.newsrequest.CommentAgreeRequest;
import com.iyuba.music.util.Utils;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.imageview.VipPhoto;
import com.iyuba.music.widget.player.SimplePlayer;

/**
 * Created by 10202 on 2015/10/10.
 */
public class CommentAdapter extends CoreRecyclerViewAdapter<Comment, CommentAdapter.CommentViewHolder> {
    private Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private boolean shouldAutoPlayMainPlayer;
    private SimplePlayer player;
    private int playingComment;
    private int voiceState;
    private ImageView playingVoiceImg;
    private TextView playingVoiceText;
    private ProgressBar playingVoiceLoading;
    private CommentAgreeOp commentAgreeOp;
    private String uid;

    public CommentAdapter(Context context, boolean autoPlay) {
        super(context);
        this.shouldAutoPlayMainPlayer = autoPlay;
        commentAgreeOp = new CommentAgreeOp();
        uid = AccountManager.getInstance().getUserId();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentViewHolder(LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        player = new SimplePlayer(context);
        playingComment = -1;
        final Comment comment = getDatas().get(viewHolder.getAdapterPosition());
        viewHolder.name.setText(comment.getUserName());
        viewHolder.time.setText(comment.getCreateDate());
        viewHolder.agreeCount.setText(String.valueOf(comment.getAgreeCount()));
        viewHolder.againstCount.setText(String.valueOf(comment.getAgainstCount()));
        if (comment.getShuoshuoType() == 0) {
            viewHolder.voice.setVisibility(View.GONE);
            viewHolder.content.setVisibility(View.VISIBLE);
            viewHolder.content.setText(comment.getShuoshuo());
        } else {
            viewHolder.content.setVisibility(View.GONE);
            viewHolder.voice.setVisibility(View.VISIBLE);
            viewHolder.voiceImg.setOnClickListener(new INoDoubleClick() {
                @Override
                public void onClick(View view) {
                    super.onClick(view);
                    if (player.isPlaying()) {
                        player.pause();
                        player.reset();
                        handler.removeMessages(0);
                        if (comment.getId() == playingComment) {
                            handler.removeMessages(1);
                            notifyItemChanged(viewHolder.getAdapterPosition());
                        } else {
                            playingComment = -1;
                            notifyDataSetChanged();
                            playingComment = comment.getId();
                            playingVoiceLoading = viewHolder.loading;
                            playingVoiceImg = viewHolder.voiceImg;
                            playingVoiceText = viewHolder.voiceTime;
                            playVoice("http://daxue.iyuba.cn/appApi/" + comment.getShuoshuo(), viewHolder.getAdapterPosition());// 播放
                        }
                    } else {
                        if (Utils.getMusicApplication().getPlayerService().getPlayer().isPlaying() && shouldAutoPlayMainPlayer) {
                            context.sendBroadcast(new Intent("iyumusic.pause"));
                        }
                        playingComment = comment.getId();
                        playingVoiceLoading = viewHolder.loading;
                        playingVoiceImg = viewHolder.voiceImg;
                        playingVoiceText = viewHolder.voiceTime;
                        playVoice("http://daxue.iyuba.cn/appApi/" + comment.getShuoshuo(), viewHolder.getAdapterPosition());// 播放
                    }
                }
            });
        }
        viewHolder.pic.setVipStateVisible(comment.getUserid(), comment.getVip() == 1);
        int repeat = commentAgreeOp.findDataByAll(String.valueOf(comment.getId()), uid);
        if (repeat == 0) {
            viewHolder.agreeView.setBackgroundResource(R.drawable.agree);
            viewHolder.againstView.setBackgroundResource(R.drawable.against);
        } else if (repeat == 1) {
            viewHolder.agreeView.setBackgroundResource(R.drawable.agree_press);
            viewHolder.againstView.setBackgroundResource(R.drawable.against);
        } else if (repeat == 2) {
            viewHolder.agreeView.setBackgroundResource(R.drawable.agree);
            viewHolder.againstView.setBackgroundResource(R.drawable.against_press);
        }
        // 是在播放，显示动画
        if (comment.getId() == playingComment) {
            playingVoiceImg = viewHolder.voiceImg;
            playingVoiceText = viewHolder.voiceTime;
        } else if (!player.isPlaying()) {// 否则停止
            viewHolder.voiceTime.setText("");
            viewHolder.voiceImg.setBackgroundResource(R.drawable.comment_voice_p3);
        }
        viewHolder.agreeView.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                if (commentAgreeOp.findDataByAll(String.valueOf(comment.getId()), uid) == 0) {
                    RequestClient.requestAsync(new CommentAgreeRequest(61001, comment.getId()), new SimpleRequestCallBack<String>() {
                        @Override
                        public void onSuccess(String resultCode) {
                            if (resultCode.equals("001")) {
                                commentAgreeOp.saveData(String.valueOf(comment.getId()), uid, "agree");
                                comment.setAgreeCount(comment.getAgreeCount() + 1);
                                YoYo.with(Techniques.FadeIn).duration(250).playOn(viewHolder.agreeCount);
                                notifyItemChanged(viewHolder.getAdapterPosition());
                            } else if (resultCode.equals("000")) {
                                CustomToast.getInstance().showToast(R.string.comment_agree_fail);
                            }
                        }

                        @Override
                        public void onError(ErrorInfoWrapper errorInfoWrapper) {
                            CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
                        }
                    });
                } else {
                    CustomToast.getInstance().showToast(R.string.comment_already);
                }
            }
        });
        viewHolder.againstView.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                if (commentAgreeOp.findDataByAll(String.valueOf(comment.getId()), uid) == 0) {
                    RequestClient.requestAsync(new CommentAgreeRequest(61002, comment.getId()), new SimpleRequestCallBack<String>() {
                        @Override
                        public void onSuccess(String resultCode) {
                            if (resultCode.equals("001")) {
                                commentAgreeOp.saveData(String.valueOf(comment.getId()), uid, "against");
                                comment.setAgainstCount(comment.getAgainstCount() + 1);
                                YoYo.with(Techniques.FadeIn).duration(250).playOn(viewHolder.againstCount);
                                notifyItemChanged(viewHolder.getAdapterPosition());
                            } else if (resultCode.equals("000")) {
                                CustomToast.getInstance().showToast(R.string.comment_against_fail);
                            }
                        }

                        @Override
                        public void onError(ErrorInfoWrapper errorInfoWrapper) {
                            CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
                        }
                    });
                } else {
                    CustomToast.getInstance().showToast(R.string.comment_already);
                }
            }
        });
        viewHolder.pic.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                if (AccountManager.getInstance().checkUserLogin()) {
                    SocialManager.getInstance().pushFriendId(comment.getUserid());
                    Intent intent = new Intent(context, PersonalHomeActivity.class);
                    intent.putExtra("needpop", true);
                    context.startActivity(intent);
                } else {
                    CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                        @Override
                        public void finish() {
                            SocialManager.getInstance().pushFriendId(comment.getUserid());
                            Intent intent = new Intent(context, PersonalHomeActivity.class);
                            intent.putExtra("needpop", true);
                            context.startActivity(intent);
                        }
                    });
                }
            }
        });
    }

    private void playVoice(String url, final int position) {
        player.setVideoPath(url);

        if (player.isPrepared()) {
            player.start();
            voiceState = 0;
            handler.sendEmptyMessage(0);
            handler.sendEmptyMessage(1);
        } else {
            playingVoiceLoading.setVisibility(View.VISIBLE);
            playingVoiceText.setVisibility(View.GONE);
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    player.start();
                    voiceState = 0;
                    playingVoiceText.setVisibility(View.VISIBLE);
                    playingVoiceLoading.setVisibility(View.GONE);
                    handler.sendEmptyMessage(0);
                    handler.sendEmptyMessage(1);
                }
            });
        }
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (!Utils.getMusicApplication().getPlayerService().getPlayer().isPlaying() && shouldAutoPlayMainPlayer) {
                    context.sendBroadcast(new Intent("iyumusic.pause"));
                }
                player.reset();
                playingComment = -1;
                handler.removeMessages(0);
                handler.removeMessages(1);
                notifyItemChanged(position);
            }
        });
        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                player.reset();
                if (!Utils.getMusicApplication().getPlayerService().getPlayer().isPlaying() && shouldAutoPlayMainPlayer) {
                    context.sendBroadcast(new Intent("iyumusic.pause"));
                }
                playingComment = -1;
                handler.removeMessages(0);
                handler.removeMessages(1);
                notifyItemChanged(position);
                playingVoiceLoading.setVisibility(View.GONE);
                CustomToast.getInstance().showToast(R.string.comment_play_fail);
                return false;
            }
        });
    }

    public void onDestroy() {
        if (player != null) {
            if (player.isPlaying()) {
                player.pause();
                if (!(Utils.getMusicApplication().getPlayerService().getPlayer().isPlaying() && shouldAutoPlayMainPlayer)) {
                    context.sendBroadcast(new Intent("iyumusic.pause"));
                }
            }
            player.stopPlayback();
        }
        handler.removeCallbacksAndMessages(null);
    }

    static class CommentViewHolder extends CoreRecyclerViewAdapter.MyViewHolder {

        TextView name, time, content, agreeCount, againstCount;
        ImageView voiceImg, agreeView, againstView;
        View voice;
        VipPhoto pic;
        TextView voiceTime;
        MaterialRippleLayout root;
        ProgressBar loading;

        CommentViewHolder(View view) {
            super(view);
            root = view.findViewById(R.id.root);
            name = view.findViewById(R.id.comment_name);
            content = view.findViewById(R.id.comment_content);
            voiceTime = view.findViewById(R.id.comment_voice_time);
            time = view.findViewById(R.id.comment_time);
            agreeCount = view.findViewById(R.id.comment_agree_text);
            againstCount = view.findViewById(R.id.comment_against_text);
            pic = view.findViewById(R.id.comment_image);
            agreeView = view.findViewById(R.id.comment_agree_img);
            againstView = view.findViewById(R.id.comment_against_img);
            voice = view.findViewById(R.id.comment_voice);
            voiceImg = view.findViewById(R.id.comment_voice_img);
            loading = view.findViewById(R.id.comment_voice_loading);
        }
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<CommentAdapter> {
        @Override
        public void handleMessageByRef(final CommentAdapter adapter, Message msg) {
            switch (msg.what) {
                case 0:
                    if (adapter.playingVoiceImg != null) {
                        if (adapter.voiceState % 3 == 1) {
                            adapter.playingVoiceImg.setBackgroundResource(R.drawable.comment_voice_p1);
                        } else if (adapter.voiceState % 3 == 2) {
                            adapter.playingVoiceImg.setBackgroundResource(R.drawable.comment_voice_p2);
                        } else if (adapter.voiceState % 3 == 0) {
                            adapter.playingVoiceImg.setBackgroundResource(R.drawable.comment_voice_p3);
                        }
                        adapter.voiceState++;
                        adapter.handler.sendEmptyMessageDelayed(0, 500);
                    }
                    break;
                case 1:
                    if (adapter.playingVoiceText != null) {
                        adapter.playingVoiceText.setText((adapter.player.getDuration() - adapter.player.getCurrentPosition()) / 1000 + "s");
                        adapter.handler.sendEmptyMessageDelayed(1, 1000);
                    }
                    break;
            }
        }
    }
}
