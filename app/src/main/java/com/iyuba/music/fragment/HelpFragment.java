package com.iyuba.music.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.iyuba.music.R;
import com.iyuba.music.activity.HelpUseActivity;
import com.iyuba.music.listener.IOperationResultInt;
import com.iyuba.music.widget.bitmap.BitmapUtils;
import com.iyuba.music.widget.view.PullDoorView;


public class HelpFragment extends BaseFragment {
    private static final String KEY_CONTENT = "HelpFragment:Content";
    private int mContent;
    private boolean usePullDown;

    public static HelpFragment newInstance(int content, boolean usePullDown) {
        HelpFragment fragment = new HelpFragment();
        fragment.mContent = content;
        fragment.usePullDown = usePullDown;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ((savedInstanceState != null)
                && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getInt(KEY_CONTENT);
        }
        View root = inflater.inflate(R.layout.help_fragment, container, false);
        ImageView iv =  root.findViewById(R.id.iv);
        iv.setImageBitmap(BitmapUtils.readBitmap(root.getContext(), root.getContext().getResources().getIdentifier("help" + (mContent + 1), "raw", root.getContext().getPackageName())));
        PullDoorView pullDoorView = root.findViewById(R.id.root);
        if (usePullDown) {
            pullDoorView.setEnable(true);
            pullDoorView.setIOperationFinish(new IOperationResultInt() {
                @Override
                public void performance(int index) {
                    switch (index) {
                        case -1:
                            ((HelpUseActivity) getActivity()).pi.setVisibility(View.VISIBLE);
                            break;
                        case 0:
                            ((HelpUseActivity) getActivity()).pi.setVisibility(View.GONE);
                            break;
                        case 1:
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                            getActivity().finish();
                            break;
                    }
                }
            });
        } else {
            pullDoorView.setEnable(false);
        }
        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CONTENT, mContent);
    }
}
