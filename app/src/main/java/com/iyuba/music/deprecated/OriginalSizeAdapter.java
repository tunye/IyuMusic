package com.iyuba.music.deprecated;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 10202 on 2015/11/30.
 */
public class OriginalSizeAdapter extends RecyclerView.Adapter<OriginalSizeAdapter.MyViewHolder> {
    private static int selected = 0;
    private List<String> sizeTextList;
    private int[] size = {12, 14, 16, 18, 20};
    private Context context;
    private OnRecycleViewItemClickListener itemClickListener;

    public OriginalSizeAdapter(Context context) {
        this.context = context;
        sizeTextList = Arrays.asList(context.getResources().getStringArray(R.array.original_size));
    }

    public void setItemClickListener(OnRecycleViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_original_size,
//                parent, false));
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_ad,
                parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (itemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(null, holder.getLayoutPosition());
                }
            });
            holder.sizeSelector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(null, holder.getLayoutPosition());
                }
            });
            holder.sizeText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(null, holder.getLayoutPosition());
                }
            });
        }
        holder.sizeText.setText(sizeTextList.get(position));
        holder.sizeText.setTextSize(size[position]);
        holder.sizeSelector.setChecked(position == selected);
    }

    @Override
    public int getItemCount() {
        return sizeTextList.size();
    }

    public void setSelected(int selected) {
        OriginalSizeAdapter.selected = selected;
        notifyDataSetChanged();
    }

    private void onItemClick(View v, int position) {
        notifyItemChanged(selected);
        selected = position;
        notifyItemChanged(selected);
        itemClickListener.onItemClick(v, selected);
    }

    class MyViewHolder extends RecycleViewHolder {

        TextView sizeText;
        RadioButton sizeSelector;

        public MyViewHolder(View view) {
            super(view);
//            sizeText = (TextView) view.findViewById(R.id.size);
//            sizeSelector = (RadioButton) view.findViewById(R.id.size_checked);
        }
    }
}
