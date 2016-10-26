package com.iyuba.music.activity.eggshell.meizhi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.iyuba.music.R;
import com.iyuba.music.listener.IOperationResultInt;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.ContextMenu;
import com.umeng.analytics.MobclickAgent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Administrator on 2016/3/30.
 */
public class MeizhiPhotoFragment extends DialogFragment {
    protected Context context;
    private PhotoView photoView;
    private ContextMenu menu;
    private String url;

    public MeizhiPhotoFragment() {
    }

    public static MeizhiPhotoFragment newInstance(String photoUrl) {
        MeizhiPhotoFragment fragment = new MeizhiPhotoFragment();
        Bundle args = new Bundle();
        args.putString("photoUrl", photoUrl);
        fragment.setArguments(args);
        return fragment;
    }

    private void saveFile(Bitmap bm, String fileName) throws IOException {
        File foder = new File(ConstantManager.instance.getImgFile());
        if (!foder.exists()) {
            foder.mkdirs();
        }
        File myCaptureFile = new File(ConstantManager.instance.getImgFile(), fileName);
        if (!myCaptureFile.exists()) {
            myCaptureFile.createNewFile();

        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(myCaptureFile);
        intent.setData(uri);
        context.sendBroadcast(intent);
        CustomToast.INSTANCE.showToast(R.string.photo_downloaded);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meizhi_photo_fragment, container);
        photoView = (PhotoView) view.findViewById(R.id.iv_fr_girl);
        context = getContext();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        url = getArguments().getString("photoUrl");
        menu = new ContextMenu(context);
        Glide.with(this).load(url).crossFade().into(photoView);
        setupPhotoEvent();
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupPhotoEvent() {
        photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                dismiss();
            }
        });
        photoView.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ArrayList<String> list = new ArrayList<>();
                        list.add(context.getString(R.string.photo_download));
                        menu.setInfo(list, new IOperationResultInt() {
                            @Override
                            public void performance(int index) {
                                if (index == 0) {
                                    Glide.with(context).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                            try {
                                                saveFile(resource, Calendar.getInstance().getTimeInMillis() + ".jpg");
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        menu.show();
                        return false;
                    }
                });
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        MobclickAgent.onPageStart(getClass().getName());
        super.onResume();
    }

    @Override
    public void onPause() {
        if (menu.isShown()) {
            menu.dismiss();
        }
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getName());
    }
}
