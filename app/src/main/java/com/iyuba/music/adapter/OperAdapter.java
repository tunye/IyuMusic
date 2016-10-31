package com.iyuba.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.music.R;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/10.
 */
public class OperAdapter extends BaseAdapter {
    private static final ArrayList<Integer> menuTextList;
    private static final ArrayList<Integer> menuIconList;

    static {
        menuIconList = new ArrayList<>();
        menuTextList = new ArrayList<>();

        menuIconList.add(R.drawable.vip_icon);
        menuIconList.add(R.drawable.credits_icon);
        menuIconList.add(R.drawable.discover_icon);
        menuIconList.add(R.drawable.message_icon);
        menuIconList.add(R.drawable.local_music_icon);
        menuIconList.add(R.drawable.bigdata_icon);
        menuIconList.add(R.drawable.setting_icon);

        menuTextList.add(R.string.oper_vip);
        menuTextList.add(R.string.oper_credits);
        menuTextList.add(R.string.oper_discover);
        menuTextList.add(R.string.oper_message);
        menuTextList.add(R.string.oper_local_music);
        menuTextList.add(R.string.oper_bigdata);
        menuTextList.add(R.string.oper_setting);
    }

    private Context context;

    public OperAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return menuIconList.size();
    }

    @Override
    public Object getItem(int position) {

        return menuIconList.get(position);
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
                    (R.layout.item_operlist, parent, false);
            holder = new ViewHolder();
            holder.menuText = (TextView) convertView.findViewById(R.id.oper_text);
            holder.menuIcon = (ImageView) convertView.findViewById(R.id.oper_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.menuText.setText(context.getString(menuTextList.get(position)));
        holder.menuIcon.setImageResource(menuIconList.get(position));
        return convertView;
    }

    private static class ViewHolder {
        TextView menuText;
        ImageView menuIcon;
    }
}
