package com.iyuba.music.activity.eggshell.material_edittext;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;

/**
 * Created by 10202 on 2016/2/23.
 */
public class MaterialEdittextMainActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.eggshell_material_main;
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText("Material EditText");
        initEnableBt();
        initSingleLineEllipsisEt();
        initSetErrorEt();
        initValidationEt();
    }

    private void initEnableBt() {
        final EditText basicEt = findViewById(R.id.basicEt);
        final Button enableBt = findViewById(R.id.enableBt);
        enableBt.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                basicEt.setEnabled(!basicEt.isEnabled());
                enableBt.setText(basicEt.isEnabled() ? "DISABLE" : "ENABLE");
            }
        });
    }

    private void initSingleLineEllipsisEt() {
        EditText singleLineEllipsisEt = findViewById(R.id.singleLineEllipsisEt);
        singleLineEllipsisEt.setSelection(singleLineEllipsisEt.getText().length());
    }

    private void initSetErrorEt() {
        final EditText bottomTextEt = findViewById(R.id.bottomTextEt);
        final Button setErrorBt = findViewById(R.id.setErrorBt);
        setErrorBt.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                bottomTextEt.setError("1-line Error!");
            }
        });
        final Button setError2Bt = findViewById(R.id.setError2Bt);
        setError2Bt.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                bottomTextEt.setError("2-line\nError!");
            }
        });
        final Button setError3Bt = findViewById(R.id.setError3Bt);
        setError3Bt.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                bottomTextEt.setError("So Many Errors! So Many Errors! So Many Errors! So Many Errors! So Many Errors! So Many Errors! So Many Errors! So Many Errors!");
            }
        });
    }

    private void initValidationEt() {
        final MaterialEditText validationEt = findViewById(R.id.validationEt);
        validationEt.addValidator(new RegexpValidator("Only Integer Valid!", "\\d+"));
        final Button validateBt = findViewById(R.id.validateBt);
        validateBt.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                // validate
                validationEt.validate();
            }
        });
    }
}
