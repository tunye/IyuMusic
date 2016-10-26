package com.iyuba.music.activity.eggshell;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.eggshell.material_edittext.MaterialEdittextMainActivity;
import com.iyuba.music.activity.eggshell.meizhi.MeizhiActivity;
import com.iyuba.music.activity.eggshell.view_animations.MyActivity;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by 10202 on 2015/12/2.
 */
public class EggShellActivity extends BaseActivity {
    private EggShellAdapter eggShellAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.egg_shell);
        context = this;
        SettingConfigManager.instance.setEggShell(true);
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        RecyclerView eggShellList = (RecyclerView) findViewById(R.id.eggshell_list);
        eggShellList.setLayoutManager(new LinearLayoutManager(this));
        eggShellAdapter = new EggShellAdapter(context);
        eggShellList.setAdapter(eggShellAdapter);
        eggShellList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        eggShellList.setItemAnimator(new SlideInLeftAnimator(new OvershootInterpolator(1f)));
    }

    @Override
    protected void setListener() {
        super.setListener();
        eggShellAdapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(context, MyActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(context, MaterialEdittextMainActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(context, MeizhiActivity.class));
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.oper_eggshell);
    }
}
