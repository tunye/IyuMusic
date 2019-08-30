package com.iyuba.music.activity.eggshell.meizhi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.iyuba.music.R;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.imageview.RatioImageView;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/10.
 */
public class MeizhiAdapter extends RecyclerView.Adapter<MeizhiAdapter.MyViewHolder> {
    private ArrayList<Meizhi> meizhiArrayList;
    private Context context;
    private OnRecycleViewItemClickListener itemClickListener;

    public MeizhiAdapter(Context context) {
        this.context = context;
        meizhiArrayList = new ArrayList<>();
    }

    public void removeData(int position) {
        meizhiArrayList.remove(position);
        notifyItemRemoved(position);
    }

    public void setDataSet(ArrayList<Meizhi> meizhis) {
        this.meizhiArrayList = meizhis;
        notifyDataSetChanged();
    }

    public void setItemClickListener(OnRecycleViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_meizhi,
                parent, false));
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
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemClickListener.onItemLongClick(holder.itemView, pos);
                return true;
            }
        });
        Meizhi meizhi = meizhiArrayList.get(position);
        holder.desc.setText(meizhi.getDesc());
        Glide.with(context)
                .load(meizhi.getUrl())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .into(holder.pic);
    }

    @Override
    public int getItemCount() {
        return meizhiArrayList.size();
    }

    static class MyViewHolder extends RecycleViewHolder {

        TextView desc;
        RatioImageView pic;

        public MyViewHolder(View view) {
            super(view);
            desc = (TextView) view.findViewById(R.id.tv_title);
            pic = (RatioImageView) view.findViewById(R.id.iv_girl);
            int temp = Utils.getRandomInt(6);
            int originalHeight;
            if (temp % 3 == 0) {
                originalHeight = 50 - temp;
            } else {
                originalHeight = 50 + temp;
            }
            pic.setOriginalSize(50, originalHeight);
        }
    }
}
