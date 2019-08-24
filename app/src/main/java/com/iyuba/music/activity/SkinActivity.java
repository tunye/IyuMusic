package com.iyuba.music.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.TextView;

import com.buaa.ct.appskin.SkinManager;
import com.iyuba.music.R;
import com.iyuba.music.adapter.FlavorAdapter;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.receiver.ChangePropertyBroadcast;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.ImmersiveManager;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import java.util.Arrays;


public class SkinActivity extends BaseActivity implements FlavorAdapter.OnItemClickListener {
    private String initSkin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.skin);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        FlavorAdapter mAdapter = new FlavorAdapter(this);
        mAdapter.setItemClickListener(this);
        mAdapter.addAll(Arrays.asList(context.getResources().getStringArray(R.array.flavors)), Arrays.asList(context.getResources().getStringArray(R.array.flavors_def)));
        mAdapter.setCurrentFlavor(GetAppColor.getInstance().getSkinFlg(SkinManager.getInstance().getCurrSkin()));
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration());
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @Override
    protected void setListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.oper_skin);
        toolbarOper.setText(R.string.dialog_save);
        initSkin = SkinManager.getInstance().getCurrSkin();
    }

    @Override
    public void onItemClicked(View view, String item, int position) {
        SkinManager.getInstance().changeSkin(item);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (position == 0) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.skin_app_color));
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
