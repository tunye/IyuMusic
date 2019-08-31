package com.iyuba.music.ground;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.MainActivity;

import java.util.ArrayList;


public class AppGroundActivity extends BaseActivity implements OnItemClickListener {
    private GridView gridview;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_ground);
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        gridview = findViewById(R.id.app_shelf);
        initGrid();
    }

    @Override
    protected void setListener() {
        super.setListener();
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.oper_ground);
    }

    @Override
    public void onBackPressed() {
        if (!mipush) {
            super.onBackPressed();
        } else {
            startActivity(new Intent(context, MainActivity.class));
        }
    }

    /**
     * 根据类别初始化 后续可添加新内容
     */
    private void initGrid() {
        ArrayList<ArrayMap<String, Object>> lstImageItem = new ArrayList<>();
        int[] text = new int[]{R.string.voa_speical, R.string.voa_bbc6,
                R.string.voa_video, R.string.voa_cs, R.string.voa_bbc,
                R.string.voa_ae, R.string.voa_word, R.string.voa_bbcnews, R.string.voa_ted};
        int[] drawable = new int[]{R.drawable.voa, R.drawable.bbc6,
                R.drawable.voavideo, R.drawable.csvoa, R.drawable.bbc,
                R.drawable.meiyu, R.drawable.voa_word, R.drawable.bbc_news, R.drawable.ted};
        ArrayMap<String, Object> map;
        for (int i = 0; i < text.length; i++) {
            map = new ArrayMap<>();
            map.put("ItemImage", drawable[i]);// 添加图像资源的ID
            map.put("ItemText", context.getString(text[i]));// 按序号做ItemText
            lstImageItem.add(map);
        }
        SimpleAdapter saImageItems = new SimpleAdapter(this, lstImageItem,
                R.layout.item_app, new String[]{"ItemImage", "ItemText"},
                new int[]{R.id.item_image, R.id.item_text});
        gridview.setAdapter(saImageItems);
        gridview.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        ArrayMap<String, Object> item = (ArrayMap<String, Object>) arg0.getItemAtPosition(arg2);
        Intent intent = new Intent(context, GroundNewsActivity.class);
        intent.putExtra("type", item.get("ItemText").toString());
        startActivity(intent);
    }
}
