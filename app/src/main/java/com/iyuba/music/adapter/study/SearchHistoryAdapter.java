package com.iyuba.music.adapter.study;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.MaterialMenuView;
import com.iyuba.music.R;
import com.iyuba.music.entity.artical.SearchHistory;
import com.iyuba.music.entity.artical.SearchHistoryOp;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/10.
 */
public class SearchHistoryAdapter extends BaseAdapter {
    private ArrayList<SearchHistory> historys;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;
    private Context context;
    private SearchHistoryOp searchHistoryOp;

    public SearchHistoryAdapter(Context context) {
        this.context = context;
        searchHistoryOp = new SearchHistoryOp();
        historys = searchHistoryOp.findDataTop();
    }

    public void setItemClickLitener(OnRecycleViewItemClickListener onItemClickLitener) {
        onRecycleViewItemClickListener = onItemClickLitener;
    }

    public void setList(String s) {
        if (TextUtils.isEmpty(s)) {
            historys = searchHistoryOp.findDataTop();
        } else {
            this.historys = searchHistoryOp.findDataByLike(s);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return historys.size();
    }

    @Override
    public SearchHistory getItem(int position) {
        return historys.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_search_history, parent, false);
            holder = new ViewHolder();
            holder.historyText = (TextView) convertView.findViewById(R.id.history_text);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onRecycleViewItemClickListener != null) {
                        onRecycleViewItemClickListener.onItemClick(v, position);
                    }
                }
            });
            holder.delete = (MaterialMenuView) convertView.findViewById(R.id.clear_history);
            holder.delete.setState(MaterialMenuDrawable.IconState.X);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchHistoryOp.deleteData(historys.get(position).getId());
                historys.remove(position);
                notifyDataSetChanged();
            }
        });
        holder.historyText.setText(historys.get(position).getContent());
        return convertView;
    }


    private static class ViewHolder {
        TextView historyText;
        MaterialMenuView delete;
    }

}
