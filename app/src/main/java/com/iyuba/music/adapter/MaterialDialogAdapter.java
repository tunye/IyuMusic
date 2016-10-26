package com.iyuba.music.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.iyuba.music.R;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10202 on 2015/11/30.
 */
public class MaterialDialogAdapter extends RecyclerView.Adapter<MaterialDialogAdapter.MyViewHolder> {
    private static int selected = 0;
    private ArrayList<String> sleepTextList;
    private Context context;
    private OnRecycleViewItemClickListener itemClickListener;

    public MaterialDialogAdapter(Context context, List<String> data) {
        this.context = context;
        sleepTextList = new ArrayList<>();
        sleepTextList.addAll(data);
    }

    public void setItemClickListener(OnRecycleViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_material_dialog, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (itemClickListener != null) {
            holder.rippleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.getLayoutPosition() != selected) {
                        onItemClick(holder.rippleView, holder.getLayoutPosition());
                    }
                }
            });
            holder.sleepSelector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.getLayoutPosition() != selected) {
                        onItemClick(holder.rippleView, holder.getLayoutPosition());
                    }
                }
            });
        }
        holder.sleepText.setText(sleepTextList.get(position));
        holder.sleepSelector.setChecked(position == selected);
    }

    @Override
    public int getItemCount() {
        return sleepTextList.size();
    }

    public void setSelected(int selected) {
        MaterialDialogAdapter.selected = selected;
        notifyDataSetChanged();
    }

    private void onItemClick(View v, int position) {
        selected = position;
        notifyItemChanged(selected);
        itemClickListener.onItemClick(v, selected);
    }

    class MyViewHolder extends RecycleViewHolder {

        TextView sleepText;
        RadioButton sleepSelector;
        MaterialRippleLayout rippleView;


        public MyViewHolder(View view) {
            super(view);
            sleepText = (TextView) view.findViewById(R.id.sleep_time);
            sleepSelector = (RadioButton) view.findViewById(R.id.sleep_selector);
            rippleView = (MaterialRippleLayout) view.findViewById(R.id.sleep_ripple);
        }
    }
}
