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
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.imageview.PickerView;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 10202 on 2015/11/30.
 */
public class SleepAdapter extends RecyclerView.Adapter<SleepAdapter.MyViewHolder> {
    private final static int[] minutes = new int[]{0, 10, 15, 20, 30, 45, 60, 90};
    private static int selected = 0;
    private ArrayList<String> sleepTextList;
    private Context context;
    private int minute;
    private OnRecycleViewItemClickListener itemClickListener;
    private MaterialDialog materialDialog;

    public SleepAdapter(Context context) {
        this.context = context;
        initData();
    }

    public void setItemClickListener(OnRecycleViewItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private void initData() {
        sleepTextList = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.sleep_time)));
        generateMaterialDialog();
    }

    private void generateMaterialDialog() {
        materialDialog = new MaterialDialog(context);
        View pickerView = View.inflate(context, R.layout.custom_sleeptime, null);
        final PickerView minute_pv = (PickerView) pickerView.findViewById(R.id.minute_pv);
        final PickerView hour_pv = (PickerView) pickerView.findViewById(R.id.hour_pv);
        List<String> hours = new ArrayList<>();
        List<String> minutes = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            hours.add(String.format(Locale.CHINA, "%02d", i));
        }
        for (int i = 0; i < 60; i++) {
            minutes.add(String.format(Locale.CHINA, "%02d", i));
        }
        minute_pv.setData(minutes);
        hour_pv.setData(hours);
        minute_pv.setSelected(0);
        hour_pv.setSelected(0);
        materialDialog.setContentView(pickerView);
        materialDialog.setTitle(sleepTextList.get(sleepTextList.size() - 1));
        final String customText = sleepTextList.get(sleepTextList.size() - 1);
        materialDialog.setPositiveButton(R.string.app_accept, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
                String hour = hour_pv.getSelected();
                String second = minute_pv.getSelected();
                minute = Integer.parseInt(hour) * 60 + Integer.parseInt(second);
                if (minute != 0) {
                    itemClickListener.onItemClick(v, selected);
                    sleepTextList.set(sleepTextList.size() - 1, customText + "-" + context.getString(R.string.sleep_custom, hour, second));
                    CustomToast.INSTANCE.showToast(context.getString(R.string.sleep_hint, minute));
                    notifyItemChanged(selected);
                }
            }
        });
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_sleep, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (itemClickListener != null) {
            holder.rippleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(holder.rippleView, holder.getLayoutPosition());
                }
            });
            holder.sleepSelector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(holder.rippleView, holder.getLayoutPosition());
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


    public int getMinute() {
        return minute;
    }

    private void onItemClick(View v, int position) {
        int lastPos = selected;
        selected = position;
        if (position != sleepTextList.size() - 1) {
            minute = minutes[position];
            itemClickListener.onItemClick(v, selected);
            if (lastPos == sleepTextList.size() - 1) {
                sleepTextList.set(sleepTextList.size() - 1, sleepTextList.get(sleepTextList.size() - 1).split("-")[0]);
            }
            if (position != 0) {
                CustomToast.INSTANCE.showToast(context.getString(R.string.sleep_hint, minute));
            }
            notifyItemChanged(lastPos);
            notifyItemChanged(selected);
        } else {
            materialDialog.show();
            notifyItemChanged(lastPos);
            notifyItemChanged(selected);
        }
    }

    static class MyViewHolder extends RecycleViewHolder {

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
