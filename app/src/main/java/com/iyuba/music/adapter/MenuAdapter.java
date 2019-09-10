package com.iyuba.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.view.MaterialRippleLayout;
import com.iyuba.music.R;

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
            holder.menuText = convertView.findViewById(R.id.menu_text);
            holder.rippleView = convertView.findViewById(R.id.menu_ripple);
            if (onRecycleViewItemClickListener != null) {
                holder.rippleView.setOnClickListener(new INoDoubleClick() {
                    @Override
                    public void onClick(View view) {
                        super.onClick(view);
                        onRecycleViewItemClickListener.onItemClick(view, position);
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
