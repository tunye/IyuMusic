package com.iyuba.music.adapter.study;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.me.PersonalHomeActivity;
import com.iyuba.music.entity.comment.Comment;
import com.iyuba.music.entity.comment.CommentAgreeOp;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.newsrequest.CommentAgreeRequest;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.player.SimplePlayer;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.iyuba.music.manager.RuntimeManager.getApplication;

/**
 * Created by 10202 on 2015/10/10.
 */
public class CommentAdapter extends RecyclerView.Adapter<RecycleViewHolder> {
    private ArrayList<Comment> comments;
    private Context context;
    private SimplePlayer player;
    private int playingComment;
    private int voiceState;
    private ImageView playingVoiceImg;
    private TextView playingVoiceText;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (playingVoiceImg != null) {
                        if (voiceState % 3 == 1) {
                            playingVoiceImg.setBackgroundResource(R.drawable.comment_voice_p1);
                        } else if (voiceState % 3 == 2) {
                            playingVoiceImg.setBackgroundResource(R.drawable.comment_voice_p2);
                        } else if (voiceState % 3 == 0) {
                            playingVoiceImg.setBackgroundResource(R.drawable.comment_voice_p3);
                        }
                        voiceState++;
                        handler.sendEmptyMessageDelayed(0, 500);
                    }
                    break;
                case 1:
                    if (playingVoiceText != null) {
                        playingVoiceText.setText((player.getDuration() - player.getCurrentPosition()) / 1000 + "s");
                        handler.sendEmptyMessageDelayed(1, 1000);
                    }
                    break;
            }
            return false;
        }
    });
    private ProgressBar playingVoiceLoading;
    private CommentAgreeOp commentAgreeOp;
    private String uid;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;

    public CommentAdapter(Context context) {
        this.context = context;
        commentAgreeOp = new CommentAgreeOp();
        if (AccountManager.instance.checkUserLogin()) {
            uid = AccountManager.instance.getUserId();
        } else {
            uid = "0";
        }
        comments = new ArrayList<>();
    }

    public void setOnItemClickLitener(OnRecycleViewItemClickListener onItemClickLitener) {
        onRecycleViewItemClickListener = onItemClickLitener;
    }

    public void setDataSet(ArrayList<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public void removeData(int position) {
        comments.remove(position);
        notifyItemRemoved(position);
    }

    public void removeData(int[] position) {
        for (int i : position) {
            comments.remove(i);
            notifyItemRemoved(i);
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    private Comment getItem(int position) {
        return comments.get(position);
    }

    @Override
    public RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        player = new SimplePlayer(context);
        playingComment = -1;
        return new CommentViewHolder(LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(RecycleViewHolder holder, final int position) {
        final CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
        final Comment comment = getItem(position);
        if (onRecycleViewItemClickListener != null) {
            commentViewHolder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = commentViewHolder.getLayoutPosition();
                    onRecycleViewItemClickListener.onItemClick(commentViewHolder.root, pos);
                }
            });
        }
        commentViewHolder.name.setText(comment.getUserName());
        commentViewHolder.time.setText(comment.getCreateDate());
        commentViewHolder.agreeCount.setText(String.valueOf(comment.getAgreeCount()));
        commentViewHolder.againstCount.setText(String.valueOf(comment.getAgainstCount()));
        if (comment.getShuoshuoType() == 0) {
            commentViewHolder.voice.setVisibility(View.GONE);
            commentViewHolder.content.setVisibility(View.VISIBLE);
            commentViewHolder.content.setText(comment.getShuoshuo());
        } else {
            commentViewHolder.content.setVisibility(View.GONE);
            commentViewHolder.voice.setVisibility(View.VISIBLE);
            commentViewHolder.voiceImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (player.isPlaying()) {
                        player.pause();
                        player.reset();
                        handler.removeMessages(0);
                        if (comment.getId() == playingComment) {
                            handler.removeMessages(1);
                            notifyItemChanged(position);
                        } else {
                            playingComment = -1;
                            notifyDataSetChanged();
                            playingComment = comment.getId();
                            playingVoiceLoading = commentViewHolder.loading;
                            playingVoiceImg = commentViewHolder.voiceImg;
                            playingVoiceText = commentViewHolder.voiceTime;
                            playVoice("http://daxue.iyuba.com/appApi/" + comment.getShuoshuo(), position);// 播放
                        }
                    } else {
                        if (((MusicApplication) getApplication()).getPlayerService().getPlayer().isPlaying()) {
                            context.sendBroadcast(new Intent("iyumusic.pause"));
                        }
                        playingComment = comment.getId();
                        playingVoiceLoading = commentViewHolder.loading;
                        playingVoiceImg = commentViewHolder.voiceImg;
                        playingVoiceText = commentViewHolder.voiceTime;
                        playVoice("http://daxue.iyuba.com/appApi/" + comment.getShuoshuo(), position);// 播放
                    }
                }
            });
        }
        ImageUtil.loadAvatar(comment.getUserid(), commentViewHolder.pic);
        int repeat = commentAgreeOp.findDataByAll(String.valueOf(comment.getId()), uid);
        if (repeat == 0) {
            commentViewHolder.agreeView.setBackgroundResource(R.drawable.agree);
            commentViewHolder.againstView.setBackgroundResource(R.drawable.against);
        } else if (repeat == 1) {
            commentViewHolder.agreeView.setBackgroundResource(R.drawable.agree_press);
            commentViewHolder.againstView.setBackgroundResource(R.drawable.against);
        } else if (repeat == 2) {
            commentViewHolder.agreeView.setBackgroundResource(R.drawable.agree);
            commentViewHolder.againstView.setBackgroundResource(R.drawable.against_press);
        }
        // 是在播放，显示动画
        if (comment.getId() == playingComment) {
            playingVoiceImg = commentViewHolder.voiceImg;
            playingVoiceText = commentViewHolder.voiceTime;
        } else if (!player.isPlaying()) {// 否则停止
            commentViewHolder.voiceTime.setText("");
            commentViewHolder.voiceImg.setBackgroundResource(R.drawable.comment_voice_p3);
        }
        commentViewHolder.agreeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentAgreeOp.findDataByAll(String.valueOf(comment.getId()), uid) == 0) {
                    CommentAgreeRequest.getInstance().exeRequest(CommentAgreeRequest.getInstance().generateUrl(61001, comment.getId()), new IProtocolResponse() {
                        @Override
                        public void onNetError(String msg) {
                            CustomToast.INSTANCE.showToast(msg);
                        }

                        @Override
                        public void onServerError(String msg) {
                            CustomToast.INSTANCE.showToast(msg);
                        }

                        @Override
                        public void response(Object object) {
                            if (object.toString().equals("001")) {
                                commentAgreeOp.saveData(String.valueOf(comment.getId()), uid, "agree");
                                comment.setAgreeCount(comment.getAgreeCount() + 1);
                                YoYo.with(Techniques.FadeIn).duration(250).playOn(commentViewHolder.agreeCount);
                                notifyItemChanged(position);
                            } else if (object.toString().equals("000")) {
                                CustomToast.INSTANCE.showToast(R.string.comment_agree_fail);
                            }
                        }
                    });
                } else {
                    CustomToast.INSTANCE.showToast(R.string.comment_already);
                }
            }
        });
        commentViewHolder.againstView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commentAgreeOp.findDataByAll(String.valueOf(comment.getId()), uid) == 0) {
                    CommentAgreeRequest.getInstance().exeRequest(CommentAgreeRequest.getInstance().generateUrl(61002, comment.getId()), new IProtocolResponse() {
                        @Override
                        public void onNetError(String msg) {
                            CustomToast.INSTANCE.showToast(msg);
                        }

                        @Override
                        public void onServerError(String msg) {
                            CustomToast.INSTANCE.showToast(msg);
                        }

                        @Override
                        public void response(Object object) {
                            if (object.toString().equals("001")) {
                                commentAgreeOp.saveData(String.valueOf(comment.getId()), uid, "against");
                                comment.setAgainstCount(comment.getAgainstCount() + 1);
                                YoYo.with(Techniques.FadeIn).duration(250).playOn(commentViewHolder.againstCount);
                                notifyItemChanged(position);
                            } else if (object.toString().equals("000")) {
                                CustomToast.INSTANCE.showToast(R.string.comment_agree_fail);
                            }
                        }
                    });
                } else {
                    CustomToast.INSTANCE.showToast(R.string.comment_already);
                }
            }
        });
        commentViewHolder.pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialManager.instance.pushFriendId(comments.get(position).getUserid());
                Intent intent = new Intent(context, PersonalHomeActivity.class);
                intent.putExtra("needpop", true);
                context.startActivity(intent);
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
                if (!((MusicApplication) getApplication()).getPlayerService().getPlayer().isPlaying()) {
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
                if (!((MusicApplication) getApplication()).getPlayerService().getPlayer().isPlaying()) {
                    context.sendBroadcast(new Intent("iyumusic.pause"));
                }
                playingComment = -1;
                handler.removeMessages(0);
                handler.removeMessages(1);
                notifyItemChanged(position);
                playingVoiceLoading.setVisibility(View.GONE);
                CustomToast.INSTANCE.showToast(R.string.comment_play_fail);
                return false;
            }
        });
    }

    public void onDestroy() {
        if (player != null) {
            if (player.isPlaying()) {
                player.pause();
                if (!((MusicApplication) getApplication()).getPlayerService().getPlayer().isPlaying()) {
                    context.sendBroadcast(new Intent("iyumusic.pause"));
                }
                handler.removeMessages(0);
                handler.removeMessages(1);
            }
            player.stopPlayback();

        }
    }

    static class CommentViewHolder extends RecycleViewHolder {

        TextView name, time, content, agreeCount, againstCount;
        ImageView voiceImg, agreeView, againstView;
        View voice;
        CircleImageView pic;
        TextView voiceTime;
        MaterialRippleLayout root;
        ProgressBar loading;

        public CommentViewHolder(View view) {
            super(view);
            root = (MaterialRippleLayout) view.findViewById(R.id.root);
            name = (TextView) view.findViewById(R.id.comment_name);
            content = (TextView) view.findViewById(R.id.comment_content);
            voiceTime = (TextView) view.findViewById(R.id.comment_voice_time);
            time = (TextView) view.findViewById(R.id.comment_time);
            agreeCount = (TextView) view.findViewById(R.id.comment_agree_text);
            againstCount = (TextView) view.findViewById(R.id.comment_against_text);
            pic = (CircleImageView) view.findViewById(R.id.comment_image);
            agreeView = (ImageView) view.findViewById(R.id.comment_agree_img);
            againstView = (ImageView) view.findViewById(R.id.comment_against_img);
            voice = view.findViewById(R.id.comment_voice);
            voiceImg = (ImageView) view.findViewById(R.id.comment_voice_img);
            loading = (ProgressBar) view.findViewById(R.id.comment_voice_loading);
        }
    }
}
