package com.iyuba.music.activity;

import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import com.buaa.ct.appskin.SkinManager;
import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.manager.ImmersiveManager;
import com.buaa.ct.core.util.GetAppColor;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.adapter.FlavorAdapter;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.receiver.ChangePropertyBroadcast;
import com.iyuba.music.widget.dialog.CustomDialog;

import java.util.Arrays;


public class SkinActivity extends BaseActivity implements FlavorAdapter.OnItemClickListener {
    private String initSkin;

    @Override
    public int getLayoutId() {
        return R.layout.skin;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        toolbarOper = findViewById(R.id.toolbar_oper);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        FlavorAdapter mAdapter = new FlavorAdapter(this);
        mAdapter.setItemClickListener(this);
        mAdapter.addAll(Arrays.asList(context.getResources().getStringArray(R.array.flavors)), Arrays.asList(context.getResources().getStringArray(R.array.flavors_def)));
        mAdapter.setCurrentFlavor(GetAppColor.getInstance().getSkinFlg(SkinManager.getInstance().getCurrSkin()));
        recyclerView.setAdapter(mAdapter);
        setRecyclerViewProperty(recyclerView);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @Override
    public void setListener() {
        back.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                onBackPressed();
            }
        });
        toolbarOper.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                if (!initSkin.equals(SkinManager.getInstance().getCurrSkin())) {
                    Intent intent = new Intent(ChangePropertyBroadcast.FLAG);
                    sendBroadcast(intent);
                } else {
                    CustomToast.getInstance().showToast(R.string.app_no_change);
                }
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText(R.string.oper_skin);
        enableToolbarOper(R.string.dialog_save);
        initSkin = SkinManager.getInstance().getCurrSkin();
    }

    @Override
    public void onItemClicked(View view, String item, int position) {
        SkinManager.getInstance().changeSkin(item);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (position == 0) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.skin_app_color));
                toolBarLayout.setBackgroundColor(getResources().getColor(R.color.skin_app_color));
                ImmersiveManager.getInstance().updateImmersiveStatus(this, true);
            } else {
                getWindow().setStatusBarColor(getResources().getColor(getResource("skin_app_color_" + item)));
                ImmersiveManager.getInstance().updateImmersiveStatus(this, ImmersiveManager.getInstance().isBrightTheme(getResource("skin_app_color_" + item)));
            }
        }
    }

    private int getResource(String colorName) {
        return getResources().getIdentifier(colorName, "color", getPackageName());
    }

    @Override
    public void onBackPressed() {
        if (!initSkin.equals(SkinManager.getInstance().getCurrSkin())) {
            showSaveChangeDialog();
        } else {
            finish();
        }
    }

    private void showSaveChangeDialog() {
        CustomDialog.saveChangeDialog(context, new IOperationResult() {
            @Override
            public void success(Object object) {
                Intent intent = new Intent(ChangePropertyBroadcast.FLAG);
                sendBroadcast(intent);
            }

            @Override
            public void fail(Object object) {
                SkinManager.getInstance().changeSkin(initSkin);
                finish();
            }
        });
    }
}
