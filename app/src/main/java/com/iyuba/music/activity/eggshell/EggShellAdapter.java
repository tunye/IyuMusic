package com.iyuba.music.activity.eggshell;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buaa.ct.core.adapter.CoreRecyclerViewAdapter;
import com.iyuba.music.R;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/10.
 */
public class EggShellAdapter extends CoreRecyclerViewAdapter<String, EggShellAdapter.MyViewHolder> {
    private ArrayList<Integer> menuIconList;

    public EggShellAdapter(Context context) {
        super(context);
        initData();
    }

    private void initData() {
        menuIconList = new ArrayList<>();

        menuIconList.add(R.mipmap.ic_launcher);
        menuIconList.add(R.mipmap.ic_launcher2);
        menuIconList.add(R.mipmap.ic_launcher);
        menuIconList.add(R.mipmap.ic_launcher2);
        getDatas().add("动画");
        getDatas().add("material输入框");
        getDatas().add("加载小动画");
        getDatas().add("福利");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_eggshell, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.menuText.setText(getDatas().get(position));
        holder.menuIcon.setImageResource(menuIconList.get(position));
    }

    static class MyViewHolder extends CoreRecyclerViewAdapter.MyViewHolder {

        TextView menuText;
        ImageView menuIcon;

        MyViewHolder(View view) {
            super(view);
            menuText = view.findViewById(R.id.oper_text);
            menuIcon = view.findViewById(R.id.oper_icon);
        }
    }
}
