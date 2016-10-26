package com.iyuba.music.adapter.discover;

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
public class DiscoverAdapter extends RecyclerView.Adapter<RecycleViewHolder> {
    private static final int TYPE_BLANK = 0;
    private static final int TYPE_ITEM = 1;
    private static final ArrayList<Discover> discoverList;

    static {
        Discover discover = new Discover();
        discoverList = new ArrayList<>();
        discover.setType(TYPE_BLANK);
        discoverList.add(discover);
        discover = new Discover();
        discover.setDrawable(R.drawable.circle_icon);
        discover.setText(R.string.oper_circle);
        discover.setType(TYPE_ITEM);
        discoverList.add(discover);
        discover = new Discover();
        discover.setDrawable(R.drawable.friend_icon);
        discover.setText(R.string.oper_friends);
        discover.setType(TYPE_ITEM);
        discoverList.add(discover);
        discover = new Discover();
        discover.setType(TYPE_BLANK);
        discoverList.add(discover);

        discover = new Discover();
        discover.setDrawable(R.drawable.ground_icon);
        discover.setText(R.string.oper_ground);
        discover.setType(TYPE_ITEM);
        discoverList.add(discover);
        discover = new Discover();
        discover.setDrawable(R.drawable.activity_icon);
        discover.setText(R.string.oper_activity);
        discover.setType(TYPE_ITEM);
        discoverList.add(discover);
        discover = new Discover();
        discover.setDrawable(R.drawable.moreapp_icon);
        discover.setText(R.string.oper_moreapp);
        discover.setType(TYPE_ITEM);
        discoverList.add(discover);
        discover = new Discover();
        discover.setType(TYPE_BLANK);
        discoverList.add(discover);

        discover = new Discover();
        discover.setDrawable(R.drawable.word_search_icon);
        discover.setText(R.string.word_search_title);
        discover.setType(TYPE_ITEM);
        discoverList.add(discover);
        discover = new Discover();
        discover.setDrawable(R.drawable.word_saying_icon);
        discover.setText(R.string.word_saying_title);
        discover.setType(TYPE_ITEM);
        discoverList.add(discover);
        discover = new Discover();
        discover.setDrawable(R.drawable.word_list_icon);
        discover.setText(R.string.word_list_title);
        discover.setType(TYPE_ITEM);
        discoverList.add(discover);
        discover = new Discover();
        discover.setType(TYPE_BLANK);
        discoverList.add(discover);

        discover = new Discover();
        discover.setDrawable(R.drawable.file_oper_icon);
        discover.setText(R.string.oper_file);
        discover.setType(TYPE_ITEM);
        discoverList.add(discover);
        if (SettingConfigManager.instance.isEggShell()) {
            discover = new Discover();
            discover.setDrawable(R.drawable.eggshell_icon);
            discover.setText(R.string.oper_eggshell);
            discover.setType(TYPE_ITEM);
            discoverList.add(discover);
        }
        discover = new Discover();
        discover.setType(TYPE_BLANK);
        discoverList.add(discover);
    }

    private Context context;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;

    public DiscoverAdapter(Context context) {
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
    public void onBindViewHolder(RecycleViewHolder holder, final int position) {
        if (holder instanceof DiscoverViewHolder) {
            final DiscoverViewHolder viewHolder = (DiscoverViewHolder) holder;
            final Discover discover = getItem(position);
            if (onRecycleViewItemClickListener != null) {
                viewHolder.rippleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRecycleViewItemClickListener.onItemClick(viewHolder.rippleView, position);
                    }
                });
            }
            viewHolder.text.setText(context.getString(discover.getText()));
            viewHolder.icon.setImageResource(discover.getDrawable());
        }
    }

    class DiscoverViewHolder extends RecycleViewHolder {
        MaterialRippleLayout rippleView;
        TextView text;
        ImageView icon;


        public DiscoverViewHolder(View view) {
            super(view);
            rippleView = (MaterialRippleLayout) view.findViewById(R.id.discover_ripple);
            text = (TextView) view.findViewById(R.id.discover_text);
            icon = (ImageView) view.findViewById(R.id.discover_icon);
        }
    }

    class BlankViewHolder extends RecycleViewHolder {

        public BlankViewHolder(View view) {
            super(view);
        }
    }
}

