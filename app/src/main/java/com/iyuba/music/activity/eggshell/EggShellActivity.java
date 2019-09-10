package com.iyuba.music.activity.eggshell;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseListActivity;
import com.iyuba.music.activity.eggshell.loading_indicator.LoadingIndicatorList;
import com.iyuba.music.activity.eggshell.material_edittext.MaterialEdittextMainActivity;
import com.iyuba.music.activity.eggshell.meizhi.MeizhiActivity;
import com.iyuba.music.activity.eggshell.view_animations.AnimationShowActivity;
import com.iyuba.music.manager.ConfigManager;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

/**
 * Created by 10202 on 2015/12/2.
 */
public class EggShellActivity extends BaseListActivity<String> {
    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        ConfigManager.getInstance().setEggShell(true);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        owner = findViewById(R.id.recyclerview_widget);
        ownerAdapter = new EggShellAdapter(context);
        assembleRecyclerView();
        owner.setItemAnimator(new SlideInLeftAnimator(new OvershootInterpolator(1f)));
    }

    @Override
    public void setListener() {
        super.setListener();
        ownerAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(context, AnimationShowActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(context, MaterialEdittextMainActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(context, LoadingIndicatorList.class));
                        break;
                    case 3:
                        startActivity(new Intent(context, MeizhiActivity.class));
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText(R.string.oper_eggshell);
        swipeRefreshLayout.setEnabled(false);
    }
}
