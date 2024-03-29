package com.iyuba.music.activity.eggshell.meizhi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buaa.ct.core.adapter.CoreRecyclerViewAdapter;
import com.iyuba.music.R;
import com.iyuba.music.util.AppImageUtil;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.imageview.RatioImageView;

/**
 * Created by 10202 on 2015/10/10.
 */
public class MeizhiAdapter extends CoreRecyclerViewAdapter<Meizhi, MeizhiAdapter.MeizhiViewHolder> {
    public MeizhiAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public MeizhiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MeizhiViewHolder(LayoutInflater.from(context).inflate(R.layout.item_meizhi, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MeizhiViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                removeData(holder.getAdapterPosition());
                return true;
            }
        });
        holder.desc.setText(getDatas().get(position).getDesc());
        AppImageUtil.loadImage(getDatas().get(position).getUrl(), holder.pic);
    }

    static class MeizhiViewHolder extends CoreRecyclerViewAdapter.MyViewHolder {

        static final float[] ratio = {16f / 9, 17f / 9, 15f / 9};
        TextView desc;
        RatioImageView pic;

        MeizhiViewHolder(View view) {
            super(view);
            desc = view.findViewById(R.id.tv_title);
            pic = view.findViewById(R.id.iv_girl);
            pic.setRatio(ratio[Utils.getRandomInt(100) % 3]);
        }
    }
}
