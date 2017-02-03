package com.iyuba.music.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.iyuba.music.R;
import com.iyuba.music.adapter.MenuAdapter;
import com.iyuba.music.listener.IOperationResultInt;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.widget.view.AddRippleEffect;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/28.
 */
public class ContextMenu {
    private View root;
    private ArrayList<String> operText;
    private Context context;
    private IOperationResultInt operationResultInt;
    private Dialog dialog;
    private boolean shown;

    public ContextMenu(Context context) {
        this.context = context;
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = vi.inflate(R.layout.context_menu, null);
    }

    public void setInfo(ArrayList<String> list, IOperationResultInt menuResultListener) {
        operText = list;
        operationResultInt = menuResultListener;
        init();
    }

    private void init() {
        View cancel = root.findViewById(R.id.cancle);
        AddRippleEffect.addRippleEffect(cancel);
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dismiss();
            }
        });
        ListView oper = (ListView) root.findViewById(R.id.operList);
        MenuAdapter adapter = new MenuAdapter(context, operText);
        adapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                dismiss();
                operationResultInt.performance(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        oper.setAdapter(adapter);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dialog = new Dialog(context, root, true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                shown = false;
            }
        });
    }

    public void show() {
        dialog.show();
        shown = true;
    }

    public void dismiss() {
        dialog.dismissAnim();
    }

    public boolean isShown() {
        return shown;
    }
}
