package com.iyuba.music.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.iyuba.music.R;
import com.iyuba.music.widget.bitmap.ReadBitmap;


public class HelpFragment extends BaseFragment {
    private static final String KEY_CONTENT = "HelpFragment:Content";
    private int mContent;

    public static HelpFragment newInstance(int content) {
        HelpFragment fragment = new HelpFragment();
        fragment.mContent = content;
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
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CONTENT, mContent);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
