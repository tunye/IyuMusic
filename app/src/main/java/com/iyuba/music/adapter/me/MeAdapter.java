package com.iyuba.music.adapter.me;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.iyuba.music.R;
import com.iyuba.music.entity.mainpanel.Discover;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/10.
 */
public class MeAdapter extends RecyclerView.Adapter<RecycleViewHolder> {
    private static final int TYPE_BLANK = 0;
    private static final int TYPE_ITEM = 1;
    private static final ArrayList<Discover> discoverList;


    static {
        ArrayList<Integer> menuTextList = new ArrayList<>(15);
        ArrayList<Integer> menuIconList = new ArrayList<>(15);

        Discover blank = new Discover();
        blank.setType(TYPE_BLANK);

        menuIconList.add(R.drawable.friend_icon);
        menuIconList.add(R.drawable.friend_icon);
        menuIconList.add(R.drawable.friend_icon);
        menuIconList.add(R.drawable.friend_icon);
        menuIconList.add(R.drawable.friend_icon);
        menuIconList.add(R.drawable.friend_icon);
        menuIconList.add(R.drawable.credits_icon);
        menuIconList.add(R.drawable.rank_icon);
        menuIconList.add(R.drawable.bigdata_icon);
        menuIconList.add(R.drawable.activity_icon);

        menuTextList.add(R.string.oper_follow);
        menuTextList.add(R.string.oper_fan);
        menuTextList.add(R.string.oper_friends);
        menuTextList.add(R.string.oper_download);
        menuTextList.add(R.string.oper_favor);
        menuTextList.add(R.string.oper_listen);
        menuTextList.add(R.string.oper_credits);
        menuTextList.add(R.string.oper_rank);
        menuTextList.add(R.string.oper_bigdata);
        menuTextList.add(R.string.oper_activity);

        discoverList = new ArrayList<>(15);
        discoverList.add(blank);
        Discover item;
        for (int i = 0; i < menuIconList.size(); i++) {
            item = new Discover();
            item.setText(menuTextList.get(i));
            item.setDrawable(menuIconList.get(i));
            item.setType(TYPE_ITEM);
            discoverList.add(item);
            if (i == 2 || i == 5) {
                discoverList.add(blank);
            }
        }
        discoverList.add(blank);
    }

    private Context context;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;

    public MeAdapter(Context context) {
        this.context = context;
    }

    public void setOnItemClickLitener(OnRecycleViewItemClickListener onItemClickLitener) {
        onRecycleViewItemClickListener = onItemClickLitener;
    }

    @Override
    public int getItemCount() {
        return discoverList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return discoverList.get(position).getType();
    }

    private Discover getItem(int position) {
        return discoverList.get(position);
    }

    @Override
    public RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            return new DiscoverViewHolder(LayoutInflater.from(context).inflate(R.layout.item_discover, parent, false));
        } else if (viewType == TYPE_BLANK) {
            //inflate your layout and pass it to view holder
            return new BlankViewHolder(LayoutInflater.from(context).inflate(R.layout.item_blank, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecycleViewHolder holder, int position) {
        if (holder instanceof DiscoverViewHolder) {
            final DiscoverViewHolder viewHolder = (DiscoverViewHolder) holder;
            final Discover discover = getItem(position);
            if (onRecycleViewItemClickListener != null) {
                viewHolder.rippleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRecycleViewItemClickListener.onItemClick(viewHolder.rippleView, holder.getAdapterPosition());
                    }
                });
            }
            viewHolder.text.setText(context.getString(discover.getText()));
            viewHolder.icon.setImageResource(discover.getDrawable());
        }
    }

    private static class DiscoverViewHolder extends RecycleViewHolder {
        MaterialRippleLayout rippleView;
        TextView text;
        ImageView icon;


        DiscoverViewHolder(View view) {
            super(view);
            rippleView = (MaterialRippleLayout) view.findViewById(R.id.discover_ripple);
            text = (TextView) view.findViewById(R.id.discover_text);
            icon = (ImageView) view.findViewById(R.id.discover_icon);
        }
    }

    private static class BlankViewHolder extends RecycleViewHolder {

        BlankViewHolder(View view) {
            super(view);
        }
    }
}

