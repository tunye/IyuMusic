package com.iyuba.music.adapter.discover;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buaa.ct.core.adapter.CoreRecyclerViewAdapter;
import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.imageselector.view.OnlyPreviewActivity;
import com.iyuba.music.R;
import com.iyuba.music.activity.me.PersonalHomeActivity;
import com.iyuba.music.entity.doings.Circle;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.util.AppImageUtil;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.widget.imageview.VipPhoto;
import com.iyuba.music.widget.textview.JustifyTextView;

import java.util.Date;

/**
 * Created by 10202 on 2015/10/10.
 */
public class CircleAdapter extends CoreRecyclerViewAdapter<Circle, CircleAdapter.FriendCircleViewHolder> {
    public CircleAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public FriendCircleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FriendCircleViewHolder(LayoutInflater.from(context).inflate(R.layout.item_circle, parent, false));
    }

    @Override
    public void onBindViewHolder(final FriendCircleViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final Circle circle = getDatas().get(position);
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
                AppImageUtil.loadImage("http://static1.iyuba.cn/data/attachment/album/" + getSmallImageUrl(circle.getImage())
                        , holder.circleImg, R.drawable.default_music);
                holder.circleImg.setOnClickListener(new INoDoubleClick() {
                    @Override
                    public void activeClick(View view) {
                        OnlyPreviewActivity.startPreview(context, "http://static1.iyuba.cn/data/attachment/album/" + circle.getImage());
                    }
                });
                if (!TextUtils.isEmpty(circle.getBody())) {
                    holder.circleContent.setText(ParameterUrl.decode(circle.getBody()));
                }
                break;
            case "blogid":
                holder.circleType.setText(R.string.circle_blog);
                holder.circleImg.setVisibility(View.VISIBLE);
                AppImageUtil.loadImage(circle.getImage(), holder.circleImg, R.drawable.default_music);
                holder.circleImg.setOnClickListener(new INoDoubleClick() {
                    @Override
                    public void activeClick(View view) {
                        OnlyPreviewActivity.startPreview(context, circle.getImage());
                    }
                });
                holder.circleContent.setText(Html.fromHtml(circle.getBody()));
                break;
        }
        holder.circleTime.setText(DateFormat.showTime(context, new Date(circle.getDateline() * 1000)));
        holder.circlePhoto.setVipStateVisible(circle.getUid(), circle.getVip() == 1);
        holder.circlePhoto.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                SocialManager.getInstance().pushFriendId(circle.getUid());
                Intent intent = new Intent(context, PersonalHomeActivity.class);
                intent.putExtra("needpop", true);
                context.startActivity(intent);
            }
        });
    }

    private String getContent(Circle circle) {
        String title = circle.getTitle();
        String username = circle.getUsername();
        title = title.replace(username, "");
        return ParameterUrl.decode(title.substring(1));
    }

    private String getSmallImageUrl(String url) {
        String[] paths = url.split("\\.");
        return paths[0] + "-s." + paths[1];
    }

    static class FriendCircleViewHolder extends CoreRecyclerViewAdapter.MyViewHolder {

        TextView circleUserName, circleTime, circleCounts, circleType;
        JustifyTextView circleContent;
        VipPhoto circlePhoto;
        ImageView circleImg;

        FriendCircleViewHolder(View view) {
            super(view);
            circleUserName = view.findViewById(R.id.circle_username);
            circleType = view.findViewById(R.id.circle_type);
            circleCounts = view.findViewById(R.id.circle_pmnum);
            circleContent = view.findViewById(R.id.circle_content);
            circleTime = view.findViewById(R.id.circle_dateline);
            circlePhoto = view.findViewById(R.id.circle_photo);
            circleImg = view.findViewById(R.id.circle_img);
        }
    }
}
