package com.iyuba.music.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.util.AddRippleEffect;
import com.iyuba.music.R;
import com.iyuba.music.adapter.MenuAdapter;
import com.iyuba.music.listener.IOperationResultInt;

import java.util.List;

/**
 * Created by 10202 on 2015/10/28.
 */
public class ContextMenu {
    private View root;
    private List<String> operText;
    private Context context;
    private IOperationResultInt operationResultInt;
    private IyubaDialog iyubaDialog;
    private boolean shown;

    public ContextMenu(Context context) {
        this.context = context;
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = vi.inflate(R.layout.context_menu, null);
    }

    public void setInfo(List<String> list, IOperationResultInt menuResultListener) {
        operText = list;
        operationResultInt = menuResultListener;
        init();
    }

    private void init() {
        View cancel = root.findViewById(R.id.cancel);
        AddRippleEffect.addRippleEffect(cancel, 300);
        cancel.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                dismiss();
            }
        });
        ListView oper = root.findViewById(R.id.operList);
        MenuAdapter adapter = new MenuAdapter(context, operText);
        adapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                dismiss();
                operationResultInt.performance(position);
            }
        });
        oper.setAdapter(adapter);
        root.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                dismiss();
            }
        });
        iyubaDialog = new IyubaDialog(context, root, true, Gravity.BOTTOM);
        iyubaDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                shown = false;
            }
        });
    }

    public void show() {
        iyubaDialog.show();
        shown = true;
    }

    public void dismiss() {
        iyubaDialog.dismissAnim();
    }

    public boolean isShown() {
        return shown;
    }
}
