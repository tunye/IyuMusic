package com.iyuba.music.activity.eggshell.weight_monitor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseInputActivity;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by chentong1 on 2017/6/6.
 */

public class WeightMonitorActivity extends BaseInputActivity {
    private RecyclerView recyclerView;
    private ArrayList<WeightMonitorEntity> weights;
    private final static String token = "weight_monitor";
    private Gson transfer;
    private WeightMonitorAdapter adapter;
    private TextView toolbarOperSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weight_monitor);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        toolbarOperSub = (TextView) findViewById(R.id.toolbar_oper_sub);
        recyclerView = (RecyclerView) findViewById(R.id.listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration());
        adapter = new WeightMonitorAdapter(context, new ArrayList<WeightMonitorEntity>());
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolbarOperSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, WeightSetActivity.class));
            }
        });
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MyMaterialDialog dialog = new MyMaterialDialog(context);
                dialog.setTitle("今日体重");
                final View contentView = LayoutInflater.from(context).inflate(R.layout.file_create, null);
                dialog.setContentView(contentView);
                final MaterialEditText fileName = (MaterialEditText) contentView.findViewById(R.id.file_name);
                fileName.setHint("请输入今日体重，公斤计");
                dialog.setPositiveButton(R.string.file_create, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        }, 100);
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(fileName.getApplicationWindowToken(), 0);
                        WeightMonitorEntity entity = new WeightMonitorEntity();
                        entity.setTime(Calendar.getInstance().getTime());
                        entity.setWeight(Double.parseDouble(fileName.getText().toString()));
                        if (weights.size() != 0) {
                            entity.setChange(entity.getWeight() - weights.get(0).getWeight());
                        } else {
                            entity.setChange(0.0);
                        }
                        weights.add(0, entity);
                        adapter.notifyDataSetChanged();
                        ConfigManager.getInstance().putString(token, transfer.toJson(weights));

                    }
                });
                dialog.setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        }, 100);
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(fileName.getApplicationWindowToken(), 0);
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText("体重管家");
        toolbarOper.setText("添加");
        toolbarOperSub.setText("设置");
        Type listType = new TypeToken<ArrayList<WeightMonitorEntity>>() {
        }.getType();
        String historyData = ConfigManager.getInstance().loadString(token);
        if (TextUtils.isEmpty(historyData)) {
            weights = new ArrayList<>();
        } else {
            weights = transfer.fromJson(historyData, listType);
        }
        adapter.notifyDataSetChanged();
    }
}
