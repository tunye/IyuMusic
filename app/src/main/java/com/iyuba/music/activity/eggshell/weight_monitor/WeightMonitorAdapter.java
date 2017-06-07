package com.iyuba.music.activity.eggshell.weight_monitor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.util.DateFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeightMonitorAdapter extends RecyclerView.Adapter<WeightMonitorAdapter.WeightViewHolder> {
    private List<WeightMonitorEntity> datas;
    private Context context;

    public WeightMonitorAdapter(Context context, ArrayList<WeightMonitorEntity> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public WeightViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WeightViewHolder(LayoutInflater.from(context).inflate(R.layout.item_weight_monitor, parent, false));
    }

    @Override
    public void onBindViewHolder(final WeightViewHolder holder, int position) {
        final WeightMonitorEntity item = datas.get(position);
        holder.time.setText(DateFormat.formatYear(item.getTime()));
        holder.weight.setText("体重为: " + item.getWeight());
        double change = item.getChange();
        holder.change.setText(String.format(Locale.CHINA, "%.2f", change));
        if (change > 0) {
            holder.change.setTextColor(context.getResources().getColor(R.color.skin_app_color_red));
        } else if (change < 0) {
            holder.change.setTextColor(context.getResources().getColor(R.color.skin_app_color_lgreen));
        } else {
            holder.change.setTextColor(context.getResources().getColor(R.color.skin_app_color_gray));
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    static class WeightViewHolder extends RecyclerView.ViewHolder {

        TextView time, weight, change;

        WeightViewHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.weight_time);
            weight = (TextView) itemView.findViewById(R.id.weight);
            change = (TextView) itemView.findViewById(R.id.weight_change);
        }
    }
}
