package com.iyuba.music.adapter.me;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.MaterialMenuView;
import com.iyuba.music.R;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/10.
 */
public class AutoCompleteAdapter extends BaseAdapter implements Filterable {
    private ArrayList<String> historyFilter;
    private ArrayList<String> historyAll;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;
    private ArrayFilter filter;
    private Context context;

    public AutoCompleteAdapter(Context context, ArrayList<String> history) {
        this.context = context;
        this.historyAll = history;
        this.historyFilter = new ArrayList<>();
    }

    public void setOnItemClickLitener(OnRecycleViewItemClickListener onItemClickLitener) {
        onRecycleViewItemClickListener = onItemClickLitener;
    }

    @Override
    public int getCount() {
        return historyFilter.size();
    }

    @Override
    public Object getItem(int position) {
        return historyFilter.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_autocomplete, parent, false);
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
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onRecycleViewItemClickListener != null) {
                        onRecycleViewItemClickListener.onItemLongClick(v, position);
                    }
                    historyFilter.remove(position);
                    notifyDataSetChanged();
                }
            });
            holder.delete.setState(MaterialMenuDrawable.IconState.X);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.historyText.setText(historyFilter.get(position));
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ArrayFilter();
        }
        return filter;
    }

    private class ViewHolder {
        TextView historyText;
        MaterialMenuView delete;
    }

    private class ArrayFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {

            FilterResults results = new FilterResults();
            if (TextUtils.isEmpty(prefix)) {
                ArrayList<String> list = new ArrayList<>();
                list.addAll(historyAll);
                results.values = list;
                results.count = list.size();
                return results;
            } else {
                String prefixString = prefix.toString().toLowerCase();
                final ArrayList<String> newValues = new ArrayList<>();
                for (int i = 0; i < historyAll.size(); i++) {
                    final String value = historyAll.get(i);
                    final String valueText = value.toLowerCase();
                    if (valueText.startsWith(prefixString)) {
                        if (newValues.size() > 5)
                            break;
                        newValues.add(valueText);
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
                return results;
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            historyFilter = (ArrayList<String>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
