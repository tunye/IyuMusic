package com.iyuba.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.iyuba.music.R;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/10.
 */
public class MenuAdapter extends BaseAdapter {
    private ArrayList<String> menuList;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;
    private Context context;

    public MenuAdapter(Context context, ArrayList<String> menuList) {
        this.context = context;
        this.menuList = menuList;
    }

    public void setOnItemClickLitener(OnRecycleViewItemClickListener onItemClickLitener) {
        onRecycleViewItemClickListener = onItemClickLitener;
    }

    public void setDataSet(ArrayList<String> menuList) {
        this.menuList = menuList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Override
    public Object getItem(int position) {
        return menuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate
                    (R.layout.item_context_menu, parent, false);
            holder = new ViewHolder();
            holder.menuText = (TextView) convertView.findViewById(R.id.menu_text);
            holder.rippleView = (MaterialRippleLayout) convertView.findViewById(R.id.menu_ripple);
            if (onRecycleViewItemClickListener != null) {
                holder.rippleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRecycleViewItemClickListener.onItemClick(v, position);
                    }
                });
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.menuText.setText(menuList.get(position));
        return convertView;
    }

    private static class ViewHolder {
        TextView menuText;
        MaterialRippleLayout rippleView;
    }
}
