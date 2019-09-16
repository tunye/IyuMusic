package com.iyuba.music.activity.eggshell.loading_indicator;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.manager.RuntimeManager;
import com.buaa.ct.core.util.GetAppColor;
import com.buaa.ct.core.view.image.DividerItemDecoration;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.wang.avi.AVLoadingIndicatorView;


/**
 * Created by 10202 on 2016/12/19.
 */

public class LoadingIndicatorList extends BaseActivity {
    private static final String[] INDICATORS = new String[]{
            "BallPulseIndicator",
            "BallGridPulseIndicator",
            "BallClipRotateIndicator",
            "BallClipRotatePulseIndicator",
            "SquareSpinIndicator",
            "BallClipRotateMultipleIndicator",
            "BallPulseRiseIndicator",
            "BallRotateIndicator",
            "CubeTransitionIndicator",
            "BallZigZagIndicator",
            "BallZigZagDeflectIndicator",
            "BallTrianglePathIndicator",
            "BallScaleIndicator",
            "LineScaleIndicator",
            "LineScalePartyIndicator",
            "BallScaleMultipleIndicator",
            "BallPulseSyncIndicator",
            "BallBeatIndicator",
            "LineScalePulseOutIndicator",
            "LineScalePulseOutRapidIndicator",
            "BallScaleRippleIndicator",
            "BallScaleRippleMultipleIndicator",
            "BallSpinFadeLoaderIndicator",
            "LineSpinFadeLoaderIndicator",
            "TriangleSkewSpinIndicator",
            "PacmanIndicator",
            "BallGridBeatIndicator",
            "SemiCircleSpinIndicator",
    };

    @Override
    public int getLayoutId() {
        return R.layout.loading_indicator_list;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        RecyclerView mRecycler = findViewById(R.id.recycler);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 4);
        mRecycler.setLayoutManager(layoutManager);
        mRecycler.addItemDecoration(new DividerItemDecoration(
                (int) RuntimeManager.getInstance().getContext().getResources().getDimension(R.dimen.line_thin),
                context.getResources().getColor(R.color.background_light),
                DividerItemDecoration.TYPE_WITHOUT_BORDER));
        mRecycler.setAdapter(new RecyclerView.Adapter<IndicatorHolder>() {
            @NonNull
            @Override
            public IndicatorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = getLayoutInflater().inflate(R.layout.item_loading_indicator, parent, false);
                return new IndicatorHolder(itemView);
            }

            @Override
            public void onBindViewHolder(final IndicatorHolder holder, int position) {
                holder.indicatorView.setIndicator(INDICATORS[holder.getAdapterPosition()]);
                holder.indicatorView.setIndicatorColor(GetAppColor.getInstance().getAppColor());
                holder.itemView.setOnClickListener(new INoDoubleClick() {
                    @Override
                    public void activeClick(View view) {
                        Intent intent = new Intent(LoadingIndicatorList.this, LoadingIndicator.class);
                        intent.putExtra("indicator", INDICATORS[holder.getAdapterPosition()]);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public int getItemCount() {
                return INDICATORS.length;
            }
        });
    }

    @Override
    public void setListener() {
        super.setListener();
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText("加载小动画");
    }

    static class IndicatorHolder extends RecyclerView.ViewHolder {

        AVLoadingIndicatorView indicatorView;

        IndicatorHolder(View itemView) {
            super(itemView);
            indicatorView = itemView.findViewById(R.id.indicator);
        }
    }
}