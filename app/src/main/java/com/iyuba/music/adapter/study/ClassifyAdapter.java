package com.iyuba.music.adapter.study;

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
public class ClassifyAdapter extends BaseAdapter {
    private ArrayList<Integer> contents;
    private ArrayList<Integer> imgs;
    private Context context;

    public ClassifyAdapter(Context context, int mode) {
        this.context = context;
        init(mode);
    }

    private void init(int mode) {
        imgs = new ArrayList<>();
        contents = new ArrayList<>();
        if (mode == 0) {
            imgs.add(R.drawable.local_list);
            imgs.add(R.drawable.favor_list);
            imgs.add(R.drawable.history_list);
            contents.add(R.string.classify_local);
            contents.add(R.string.classify_favor);
            contents.add(R.string.classify_history);
        } else {
            imgs.add(R.drawable.pop_list);
            imgs.add(R.drawable.rural_list);
            imgs.add(R.drawable.jazz_list);
            imgs.add(R.drawable.fork_list);
            imgs.add(R.drawable.classic_list);
            imgs.add(R.drawable.rock_list);
            imgs.add(R.drawable.dance_list);
            imgs.add(R.drawable.video_list);
            imgs.add(R.drawable.grammy_list);
            contents.add(R.string.classify_pop);
            contents.add(R.string.classify_rural);
            contents.add(R.string.classify_jazz);
            contents.add(R.string.classify_fork);
            contents.add(R.string.classify_classic);
            contents.add(R.string.classify_rock);
            contents.add(R.string.classify_dance);
            contents.add(R.string.classify_video);
            contents.add(R.string.classify_grammy);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_classify, parent, false);
            holder = new ViewHolder();
            holder.classifyText = (TextView) convertView.findViewById(R.id.classify_text);
            holder.classifyImg = (ImageView) convertView.findViewById(R.id.classify_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.classifyText.setText(contents.get(position));
        holder.classifyImg.setImageResource(imgs.get(position));
        return convertView;
    }

    @Override
    public String getItem(int pos) {
        return context.getString(contents.get(pos));
    }

    @Override
    public int getCount() {
        return contents.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {

        TextView classifyText;
        ImageView classifyImg;
    }
}
