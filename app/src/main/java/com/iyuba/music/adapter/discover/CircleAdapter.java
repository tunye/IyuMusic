package com.iyuba.music.adapter.discover;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.eggshell.meizhi.MeizhiPhotoActivity;
import com.iyuba.music.activity.me.PersonalHomeActivity;
import com.iyuba.music.entity.doings.Circle;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.widget.imageview.VipPhoto;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;
import com.iyuba.music.widget.textview.JustifyTextView;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by 10202 on 2015/10/10.
 */
public class CircleAdapter extends RecyclerView.Adapter<CircleAdapter.MyViewHolder> {
    private ArrayList<Circle> circles;
    private Context context;
    private OnRecycleViewItemClickListener itemClickListener;

    public CircleAdapter(Context context) {
        this.context = context;
        circles = new ArrayList<>();
    }

    public void setCircleList(ArrayList<Circle> circles) {
        this.circles = circles;
        notifyDataSetChanged();
    }

    public void setItemClickListener(OnRecycleViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_circle, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final int pos = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(holder.itemView, pos);
            }
        });
        final Circle circle = circles.get(position);
        holder.circleUserName.setText(circle.getUsername());
        holder.circleCounts.setText(context.getString(R.string.circle_pnum, circle.getReplynum()));
        holder.circleContent.setText(getContent(circle));
        switch (circle.getIdtype()) {
            case "doid":
                holder.circleType.setText(R.string.circle_do);
                holder.circleImg.setVisibility(View.GONE);
                break;
            case "picid":
                holder.circleType.setText(R.string.circle_pic);
                holder.circleImg.setVisibility(View.VISIBLE);
                ImageUtil.loadImage("http://static1.iyuba.cn/data/attachment/album/" + getSmallImageUrl(circle.getImage())
                        , holder.circleImg, R.drawable.default_music);
                holder.circleImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, MeizhiPhotoActivity.class);
                        intent.putExtra("url", "http://static1.iyuba.cn/data/attachment/album/" + circle.getImage());
                        context.startActivity(intent);
                    }
                });
                if (!TextUtils.isEmpty(circle.getBody())) {
                    holder.circleContent.setText(ParameterUrl.decode(circle.getBody()));
                }
                break;
            case "blogid":
                holder.circleType.setText(R.string.circle_blog);
                holder.circleImg.setVisibility(View.VISIBLE);
                ImageUtil.loadImage(circle.getImage(), holder.circleImg, R.drawable.default_music);
                holder.circleImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, MeizhiPhotoActivity.class);
                        intent.putExtra("url", circle.getImage());
                        context.startActivity(intent);
                    }
                });
                holder.circleContent.setText(Html.fromHtml(circle.getBody()));
                break;
        }
        holder.circleTime.setText(DateFormat.showTime(context, new Date(circle.getDateline() * 1000)));
        holder.circlePhoto.setVipStateVisible(circle.getUid(), circle.getVip() == 1);
        holder.circlePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialManager.getInstance().pushFriendId(circle.getUid());
                Intent intent = new Intent(context, PersonalHomeActivity.class);
                intent.putExtra("needpop", true);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return circles.size();
    }

    private String getContent(Circle circle) {
        String title = circle.getTitle();
        String username = circle.getUsername();
        title = title.replace(username, "");
        return ParameterUrl.decode(title.substring(1, title.length()));
    }

    private String getSmallImageUrl(String url) {
        String[] paths = url.split("\\.");
        return paths[0] + "-s." + paths[1];
    }

    static class MyViewHolder extends RecycleViewHolder {

        TextView circleUserName, circleTime, circleCounts, circleType;
        JustifyTextView circleContent;
        VipPhoto circlePhoto;
        ImageView circleImg;

        public MyViewHolder(View view) {
            super(view);
            circleUserName = (TextView) view.findViewById(R.id.circle_username);
            circleType = (TextView) view.findViewById(R.id.circle_type);
            circleCounts = (TextView) view.findViewById(R.id.circle_pmnum);
            circleContent = (JustifyTextView) view.findViewById(R.id.circle_content);
            circleTime = (TextView) view.findViewById(R.id.circle_dateline);
            circlePhoto = (VipPhoto) view.findViewById(R.id.circle_photo);
            circleImg = (ImageView) view.findViewById(R.id.circle_img);
        }
    }
}
