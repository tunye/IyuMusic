package com.iyuba.music.activity.study;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.entity.original.Original;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.original.OriginalView;
import com.iyuba.music.widget.seekbar.DiscreteSlider;

import java.util.ArrayList;

/**
 * Created by 10202 on 2016/3/7.
 */
public class OriginalSizeActivity extends BaseActivity {
    private static final String[] example = {"^[8.2]^^[10.9]^^[1500]^^[1]^^[1]^Yellow diamonds in the light@@@黄色钻石闪耀",
            "^[11.8]^^[15.5]^^[1500]^^[2]^^[1]^And we’re standing side by side@@@你就在我身边",
            "^[15.6]^^[18.4]^^[1500]^^[3]^^[1]^As your shadow crosses mine@@@你我的影子缠绵交织",
            "^[19.3]^^[22.8]^^[1500]^^[4]^^[1]^What it takes to come alive@@@让我充满活力",
            "^[26.2]^^[33.2]^^[1500]^^[5]^^[1]^It’s the way I’m feeling I just can’t deny@@@我不能拒绝这种感觉",
            "^[34.0]^^[36.4]^^[1500]^^[6]^^[1]^But I’ve gotta let it go@@@但是我将释怀",
            "^[37.7]^^[41.0]^^[1500]^^[7]^^[1]^We Found Love in a hopeless place@@@我们在绝望之境中遇见了爱",
            "^[41.7]^^[45.4]^^[1500]^^[8]^^[1]^We Found Love in a hopeless place@@@我们在绝望之境中遇见了爱",
            "^[45.5]^^[48.5]^^[1500]^^[9]^^[1]^We Found Love in a hopeless place@@@我们在绝望之境中遇见了爱",
            "^[49.2]^^[53.4]^^[1500]^^[10]^^[1]^We Found Love in a hopeless place@@@我们在绝望之境中遇见了爱",
            "^[123.3]^^[126.1]^^[1500]^^[11]^^[1]^Shine a light through an open door@@@光芒穿过敞开的门口",
            "^[127.0]^^[130.6]^^[1500]^^[12]^^[1]^Love and life I will divide@@@我将把爱情与生活分离",
            "^[130.8]^^[133.5]^^[1500]^^[13]^^[1]^Turn away cause I need you more@@@因为我仍想见你而转身",
            "^[134.6]^^[140.4]^^[1500]^^[14]^^[1]^Feel the heartbeat in my mind@@@感受我灵魂的跃动",
            "^[141.4]^^[148.2]^^[1500]^^[15]^^[1]^It’s the way I’m feeling I just can’t deny@@@我不能拒绝这种感觉",
            "^[149.0]^^[152.8]^^[1500]^^[16]^^[1]^But I’ve gotta let it go@@@但是我将释怀",
            "^[153.0]^^[156.0]^^[1500]^^[17]^^[1]^We Found Love in a hopeless place@@@我们在绝望之境中遇见了爱",
            "^[156.8]^^[200.3]^^[1500]^^[18]^^[1]^We Found Love in a hopeless place@@@我们在绝望之境中遇见了爱",
            "^[200.5]^^[203.5]^^[1500]^^[19]^^[1]^We Found Love in a hopeless place@@@我们在绝望之境中遇见了爱",
            "^[204.3]^^[208.5]^^[1500]^^[20]^^[1]^We Found Love in a hopeless place@@@我们在绝望之境中遇见了爱",
            "^[215.8]^^[218.5]^^[1500]^^[21]^^[1]^Yellow diamonds in the light@@@黄色钻石闪耀",
            "^[219.5]^^[223.0]^^[1500]^^[22]^^[1]^And we’re standing side by side@@@你就在我身边",
            "^[223.2]^^[228.1]^^[1500]^^[23]^^[1]^As your shadow crosses mine@@@你我的影子缠绵交织",
            "^[230.6]^^[233.5]^^[1500]^^[24]^^[1]^We Found Love in a hopeless place@@@我们在绝望之境中遇见了爱",
            "^[234.4]^^[237.9]^^[1500]^^[25]^^[1]^We Found Love in a hopeless place@@@我们在绝望之境中遇见了爱",
            "^[238.0]^^[241.0]^^[1500]^^[26]^^[1]^We Found Love in a hopeless place@@@我们在绝望之境中遇见了爱",
            "^[241.9]^^[245.7]^^[1500]^^[27]^^[1]^We Found Love in a hopeless place@@@我们在绝望之境中遇见了爱",
            "^[315.6]^^[318.5]^^[1500]^^[28]^^[1]^We Found Love in a hopeless place@@@我们在绝望之境中遇见了爱",
            "^[319.3]^^[322.9]^^[1500]^^[29]^^[1]^We Found Love in a hopeless place@@@我们在绝望之境中遇见了爱",
            "^[323.0]^^[326.0]^^[1500]^^[30]^^[1]^We Found Love in a hopeless place@@@我们在绝望之境中遇见了爱",
            "^[327.0]^^[331.6]^^[1500]^^[31]^^[1]^We Found Love in a hopeless place@@@我们在绝望之境中遇见了爱",
            "^[332.0]^^[332.5]^^[1500]^^[32]^^[1]^End@@@完"};
    private ArrayList<Original> originalRows;
    private OriginalView original;
    private DiscreteSlider discreteSlider;
    private LinearLayout tickMarkLabels;
    private int sizePos, initPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.original_size);
        originalRows = new ArrayList<>();
        for (String lrcLine : example) {
            Original row = Original.createRows(lrcLine);
            originalRows.add(row);
        }
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = findViewById(R.id.toolbar_oper);
        original = findViewById(R.id.original);
        discreteSlider = findViewById(R.id.discrete_slider);
        tickMarkLabels = findViewById(R.id.tick_mark_labels);
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
                if (initPos != sizePos) {
                    ConfigManager.getInstance().setOriginalSize(posToSize(sizePos));
                    finish();
                } else {
                    CustomToast.getInstance().showToast(R.string.app_no_change);
                }
            }
        });
        discreteSlider.setOnDiscreteSliderChangeListener(new DiscreteSlider.OnDiscreteSliderChangeListener() {
            @Override
            public void onPositionChanged(int position) {
                if (sizePos != position) {
                    ((TextView) tickMarkLabels.getChildAt(sizePos)).setTextColor(getResources().getColor(R.color.text_color));
                    sizePos = position;
                    ((TextView) tickMarkLabels.getChildAt(sizePos)).setTextColor(GetAppColor.getInstance().getAppColor());
                    original.setTextSize(posToSize(position));
                    original.setOriginalList(originalRows);
                }
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.original_size_title);
        toolbarOper.setText(R.string.dialog_save);
        initPos = sizePos = sizeToPos(ConfigManager.getInstance().getOriginalSize());
        original.setTextSize(ConfigManager.getInstance().getOriginalSize());
        original.setOriginalList(originalRows);
        discreteSlider.setSelected(sizePos);
        tickMarkLabels.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tickMarkLabels.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                addTickMarkTextLabels(sizePos);
            }
        });

    }

    private int sizeToPos(int size) {
        switch (size) {
            case 14:
                return 0;
            case 16:
                return 1;
            case 18:
                return 2;
            case 20:
                return 3;
            case 24:
                return 4;
        }
        return 1;
    }

    private int posToSize(int pos) {
        switch (pos) {
            case 0:
                return 14;
            case 1:
                return 16;
            case 2:
                return 18;
            case 3:
                return 20;
            case 4:
                return 24;
            default:
                return 16;
        }
    }

    private void addTickMarkTextLabels(int initPos) {
        final int tickMarkCount = discreteSlider.getTickMarkCount();
        String[] tickMarkText = context.getResources().getStringArray(R.array.original_size);
        int tickMarkLabelWidth = RuntimeManager.getWindowWidth() / tickMarkCount;
        TextView tv;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                tickMarkLabelWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < tickMarkCount; i++) {
            tv = new TextView(context);
            tv.setText(tickMarkText[i]);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(14 + i * 2);
            if (i == initPos) {
                tv.setTextColor(GetAppColor.getInstance().getAppColor());
            } else {
                tv.setTextColor(getResources().getColor(R.color.text_color));
            }
            final int intervalPos = i;
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    discreteSlider.setSelected(intervalPos);
                }
            });
            tv.setLayoutParams(layoutParams);
            tickMarkLabels.addView(tv);
        }
    }

    @Override
    public void onBackPressed() {
        if (initPos != sizePos) {
            showSaveChangeDialog();
        } else {
            finish();
        }
    }

    private void showSaveChangeDialog() {
        CustomDialog.saveChangeDialog(context, new IOperationResult() {
            @Override
            public void success(Object object) {
                ConfigManager.getInstance().setOriginalSize(posToSize(sizePos));
                finish();
            }

            @Override
            public void fail(Object object) {
                finish();
            }
        });
    }
}
