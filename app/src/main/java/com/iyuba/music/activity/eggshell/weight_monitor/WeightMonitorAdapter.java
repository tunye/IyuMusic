package com.iyuba.music.activity.eggshell.weight_monitor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.util.DateFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeightMonitorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private double initWeight;
    private double targetWeight;
    private List<WeightMonitorEntity> datas;
    private Context context;

    public WeightMonitorAdapter(Context context, ArrayList<WeightMonitorEntity> datas) {
        this.context = context;
        this.datas = datas;
        initWeight = ConfigManager.getInstance().getInitWeight();
        targetWeight = ConfigManager.getInstance().getTargetWeight();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return new WeightViewHolder(LayoutInflater.from(context).inflate(R.layout.item_weight_monitor, parent, false));
        } else if (viewType == TYPE_HEADER) {
            return new WeightViewTopHolder(LayoutInflater.from(context).inflate(R.layout.item_weight_monitor_top, parent, false));
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return datas.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof WeightViewHolder) {
            WeightViewHolder itemView = (WeightViewHolder) holder;
            final WeightMonitorEntity item = datas.get(position - 1);
            itemView.time.setText(DateFormat.formatYear(item.getTime()));
            itemView.weight.setText("体重为: " + item.getWeight());
            double change = item.getChange();
            itemView.change.setText(String.format(Locale.CHINA, "%.2f", change));
            if (change > 0) {
                itemView.change.setTextColor(context.getResources().getColor(R.color.skin_app_color_red));
            } else if (change < 0) {
                itemView.change.setTextColor(context.getResources().getColor(R.color.skin_app_color_lgreen));
            } else {
                itemView.change.setTextColor(context.getResources().getColor(R.color.skin_app_color_gray));
            }
        } else {
            double currWeight;
            if (datas.size() != 0) {
                currWeight = datas.get(0).getWeight();
            } else {
                currWeight = initWeight;
            }
            WeightViewTopHolder itemView = (WeightViewTopHolder) holder;
            itemView.minus.setText("初始体重" + String.format(Locale.CHINA, "%.2f", initWeight) + "公斤，已减重" + String.format(Locale.CHINA, "%.2f", (initWeight - currWeight)) + "公斤");
            itemView.target.setText("距离迎娶琪儿还有" + String.format(Locale.CHINA, "%.2f", (currWeight - targetWeight)) + "公斤");
            if (!ConfigManager.getInstance().isShowTarget()) {
                itemView.target.setVisibility(View.GONE);
            } else {
                itemView.target.setVisibility(View.VISIBLE);
            }
        }
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

    static class WeightViewTopHolder extends RecyclerView.ViewHolder {

        TextView minus, target;

        WeightViewTopHolder(View itemView) {
            super(itemView);
            minus = (TextView) itemView.findViewById(R.id.weight_minus);
            target = (TextView) itemView.findViewById(R.id.weight_target);
        }
    }
}
