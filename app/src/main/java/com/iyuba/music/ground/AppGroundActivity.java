package com.iyuba.music.ground;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;

import me.drakeet.materialdialog.MaterialDialog;


public class AppGroundActivity extends BaseActivity {
    private GridView gridview;
    private static final int WRITE_EXTERNAL_TASK_CODE = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_ground);
        context = this;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_TASK_CODE);
        }
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        gridview = (GridView) findViewById(R.id.app_shelf);
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

    /**
     * 根据类别初始化 后续可添加新内容
     */
    private void initGrid() {
        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<>();
        int[] text = new int[]{R.string.voa_speical, R.string.voa_bbc6,
                R.string.voa_video, R.string.voa_cs, R.string.voa_bbc,
                R.string.voa_ae, R.string.voa_word, R.string.voa_bbcnews, R.string.voa_ted};
        int[] drawable = new int[]{R.drawable.voa, R.drawable.bbc6,
                R.drawable.voavideo, R.drawable.csvoa, R.drawable.bbc,
                R.drawable.meiyu, R.drawable.voa_word, R.drawable.bbc_news, R.drawable.ted};
        HashMap<String, Object> map;
        for (int i = 0; i < text.length; i++) {
            map = new HashMap<>();
            map.put("ItemImage", drawable[i]);// 添加图像资源的ID
            map.put("ItemText", context.getString(text[i]));// 按序号做ItemText
            lstImageItem.add(map);
        }
        SimpleAdapter saImageItems = new SimpleAdapter(this, lstImageItem,
                R.layout.item_app, new String[]{"ItemImage", "ItemText"},
                new int[]{R.id.item_image, R.id.item_text});
        gridview.setAdapter(saImageItems);
        gridview.setOnItemClickListener(new ItemClickListener());
    }

    class ItemClickListener implements OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            HashMap<String, Object> item = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
            Intent intent = new Intent(context, SimpleNewsActivity.class);
            intent.putExtra("type", item.get("ItemText").toString());
            startActivity(intent);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_TASK_CODE && grantResults.length == permissions.length
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

        } else {
            final MaterialDialog materialDialog = new MaterialDialog(context);
            materialDialog.setTitle(R.string.storage_permission);
            materialDialog.setMessage(R.string.storage_permission_content);
            materialDialog.setPositiveButton(R.string.sure, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(AppGroundActivity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_TASK_CODE);
                    materialDialog.dismiss();
                }
            });
            materialDialog.show();
        }
    }
}
