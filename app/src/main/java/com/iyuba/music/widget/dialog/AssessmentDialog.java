package com.iyuba.music.widget.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.listener.IOperationResultInt;
import com.iyuba.music.widget.bitmap.ReadBitmap;
import com.iyuba.music.widget.boundnumber.RiseNumberTextView;

import tyrantgit.explosionfield.ExplosionField;

/**
 * Created by 10202 on 2017-03-17.
 */

public class AssessmentDialog implements View.OnClickListener {
    private View root;
    private Context context;
    private IOperationResultInt operationResultInt;
    private IyubaDialog iyubaDialog;
    private boolean shown;

    private RiseNumberTextView scoreView;
    private TextView scoreComment;

    public AssessmentDialog(Context context) {
        this.context = context;
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = vi.inflate(R.layout.assessment_result, null);
    }

    public void setListener(IOperationResultInt menuResultListener) {
        operationResultInt = menuResultListener;
        init();
    }

    private void init() {
        View cancel = root.findViewById(R.id.assessment_close);
        scoreView = (RiseNumberTextView) root.findViewById(R.id.assessment_score);
        View send, listen, retry;
        send = root.findViewById(R.id.assessment_send);
        listen = root.findViewById(R.id.assessment_listen);
        retry = root.findViewById(R.id.assessment_retry);
        scoreComment = (TextView) root.findViewById(R.id.assessment_comment);
        cancel.setOnClickListener(this);
        send.setOnClickListener(this);
        retry.setOnClickListener(this);
        listen.setOnClickListener(this);
        iyubaDialog = new IyubaDialog(context, root, false, 36, new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                shown = false;
            }
        });
    }

    public void show(final float score) {
        iyubaDialog.show();
        scoreView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scoreView.withNumber(score).start();

            }
        }, 1000);
        ExplosionField explosionView = new ExplosionField(context);
        explosionView.explode(ReadBitmap.readBitmap(context, R.drawable.assessment_flower), new Rect(0, 0, 200, 200), 500, 500);
        int level = (int) score / 20;
        String comment;
        switch (level) {
            case 0:
            case 1:
                comment = context.getString(R.string.read_grade_d);
                break;
            case 2:
                comment = context.getString(R.string.read_grade_c);
                break;
            case 3:
                comment = context.getString(R.string.read_grade_b);
                break;
            case 4:
                comment = context.getString(R.string.read_grade_a);
                break;
            default:
                comment = context.getString(R.string.read_grade_c);
                break;
        }
        scoreComment.setText(comment);
        shown = true;
    }

    public void dismiss() {
        iyubaDialog.dismissAnim();
    }

    public boolean isShown() {
        return shown;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.assessment_send:
                operationResultInt.performance(0);
                break;
            case R.id.assessment_listen:
                operationResultInt.performance(1);
                break;
            case R.id.assessment_retry:
                operationResultInt.performance(2);
                break;
            case R.id.assessment_close:
                dismiss();
                break;
        }
    }
}
