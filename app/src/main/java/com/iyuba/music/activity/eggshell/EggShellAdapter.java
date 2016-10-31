package com.iyuba.music.activity.eggshell;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/10.
 */
public class EggShellAdapter extends RecyclerView.Adapter<EggShellAdapter.MyViewHolder> {
    private ArrayList<String> menuTextList;
    private ArrayList<Integer> menuIconList;
    private Context context;
    private OnRecycleViewItemClickListener itemClickListener;

    public EggShellAdapter(Context context) {
        this.context = context;
        menuIconList = new ArrayList<>();
        menuTextList = new ArrayList<>();
        initData();
    }

    private void initData() {
        menuIconList = new ArrayList<>();
        menuTextList = new ArrayList<>();

        menuIconList.add(R.mipmap.ic_launcher);
        menuIconList.add(R.mipmap.ic_launcher);
        menuIconList.add(R.mipmap.ic_launcher);
        menuTextList.add("动画");
        menuTextList.add("material输入框");
        menuTextList.add("福利");
    }

    public void setItemClickListener(OnRecycleViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_eggshell, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getLayoutPosition();
                itemClickListener.onItemClick(holder.itemView, pos);
            }
        });
        holder.menuText.setText(menuTextList.get(position));
        holder.menuIcon.setImageResource(menuIconList.get(position));
    }

    @Override
    public int getItemCount() {
        return menuTextList.size();
    }

    static class MyViewHolder extends RecycleViewHolder {

        TextView menuText;
        ImageView menuIcon;

        public MyViewHolder(View view) {
            super(view);
            menuText = (TextView) view.findViewById(R.id.oper_text);
            menuIcon = (ImageView) view.findViewById(R.id.oper_icon);
        }
    }
}
