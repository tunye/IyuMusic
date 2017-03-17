package com.iyuba.music.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.iyuba.music.R;
import com.iyuba.music.activity.HelpUseActivity;
import com.iyuba.music.listener.IOperationResultInt;
import com.iyuba.music.widget.bitmap.ReadBitmap;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ((savedInstanceState != null)
                && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getInt(KEY_CONTENT);
        }
        View root = inflater.inflate(R.layout.help_fragment, container, false);
        ImageView iv = (ImageView) root.findViewById(R.id.iv);
        iv.setImageBitmap(ReadBitmap.readBitmap(getContext(), getContext().getResources().getIdentifier("help" + mContent, "raw", getContext().getPackageName())));
        PullDoorView pullDoorView = (PullDoorView) root.findViewById(R.id.root);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CONTENT, mContent);
    }
}
