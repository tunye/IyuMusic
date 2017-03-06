package com.iyuba.music.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.util.Mathematics;
import com.iyuba.music.widget.imageview.GoImageView;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/10.
 */
public class OperAdapter extends RecyclerView.Adapter<OperAdapter.OperViewHolder> {
    private static final ArrayList<Integer> menuTextList;
    private static final ArrayList<Integer> menuIconList;

    static {
        menuIconList = new ArrayList<>(12);
        menuTextList = new ArrayList<>(12);

        menuIconList.add(R.drawable.vip_icon);
        menuIconList.add(R.drawable.ground_icon);
        menuIconList.add(R.drawable.discover_icon);
        menuIconList.add(R.drawable.me_icon);
        menuIconList.add(R.drawable.night_icon);
        menuIconList.add(R.drawable.sleep_icon);
        menuIconList.add(R.drawable.official_accounts_icon);
        menuIconList.add(R.drawable.setting_icon);

        menuTextList.add(R.string.oper_vip);
        menuTextList.add(R.string.oper_ground);
        menuTextList.add(R.string.oper_discover);
        menuTextList.add(R.string.oper_me);
        menuTextList.add(R.string.oper_night);
        menuTextList.add(R.string.oper_sleep);
        menuTextList.add(R.string.oper_wx);
        menuTextList.add(R.string.oper_setting);
    }

    private Context context;
    private OperAdapter.OnItemClickListener mItemClickListener;

    public OperAdapter(Context context) {
        this.context = context;
    }

    public void setItemClickListener(OperAdapter.OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return menuIconList.size();
    }

    @Override
    public OperAdapter.OperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OperAdapter.OperViewHolder(LayoutInflater.from(context).inflate(R.layout.item_operlist, parent, false));
    }

    @Override
    public void onBindViewHolder(final OperViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClicked(v, holder.getAdapterPosition());
                }
            }
        });
        holder.menuText.setText(context.getString(menuTextList.get(position)));
        holder.menuIcon.setImageResource(menuIconList.get(position));
        if (holder.menuText.getText().equals(context.getString(R.string.oper_night))) {
            holder.go.setVisibility(View.GONE);
            holder.menuResult.setVisibility(View.VISIBLE);
            holder.menuResult.setText(SettingConfigManager.getInstance().isNight() ? R.string.oper_night_on : R.string.oper_night_off);
        }
        if (holder.menuText.getText().equals(context.getString(R.string.oper_sleep))) {
            holder.go.setVisibility(View.GONE);
            holder.menuResult.setVisibility(View.VISIBLE);
            int sleepSecond = ((MusicApplication) ((Activity) context).getApplication()).getSleepSecond();
            if (sleepSecond == 0) {
                holder.menuResult.setText(R.string.sleep_no_set);
            } else {
                holder.menuResult.setText(Mathematics.formatTime(sleepSecond));
            }
        }
        if (!holder.menuText.getText().equals(context.getString(R.string.oper_night)) &&
                !holder.menuText.getText().equals(context.getString(R.string.oper_sleep))) {
            holder.menuResult.setVisibility(View.GONE);
            holder.go.setVisibility(View.VISIBLE);
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(View view, int position);
    }

    static class OperViewHolder extends RecyclerView.ViewHolder {

        TextView menuText, menuResult;
        ImageView menuIcon;
        GoImageView go;

        OperViewHolder(View itemView) {
            super(itemView);
            menuText = (TextView) itemView.findViewById(R.id.oper_text);
            menuIcon = (ImageView) itemView.findViewById(R.id.oper_icon);
            menuResult = (TextView) itemView.findViewById(R.id.oper_result);
            go = (GoImageView) itemView.findViewById(R.id.oper_go);
        }
    }
}
