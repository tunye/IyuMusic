package com.iyuba.music.activity.study;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyuba.music.R;
import com.iyuba.music.adapter.study.ReadAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.original.LrcParser;
import com.iyuba.music.entity.original.Original;
import com.iyuba.music.fragment.BaseRecyclerViewFragment;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.LrcRequest;
import com.iyuba.music.util.ThreadPoolUtil;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/12/17.
 */
public class ReadFragment extends BaseRecyclerViewFragment {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private ReadAdapter readAdapter;
    private Article curArticle;
    private IyubaDialog waittingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        curArticle = StudyManager.getInstance().getCurArticle();
        waittingDialog = WaitingDialog.create(context, context.getString(R.string.read_loading));
        readAdapter = new ReadAdapter(context);
        recyclerView.setAdapter(readAdapter);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        setUserVisibleHint(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        disableSwipeLayout();
        getOriginal();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        readAdapter.onDestroy();
    }

    private void getOriginal() {
        if (LrcParser.getInstance().fileExist(curArticle.getId())) {
            ThreadPoolUtil.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    LrcParser.getInstance().getOriginal(curArticle.getId(), new IOperationResult() {
                        @Override
                        public void success(Object object) {
                            handler.obtainMessage(0, object).sendToTarget();
                        }

                        @Override
                        public void fail(Object object) {

                        }
                    });
                }
            });
        } else {
            waittingDialog.show();
            getWebLrc(curArticle.getId(), new IOperationFinish() {
                @Override
                public void finish() {
                    waittingDialog.dismiss();
                }
            });
        }
    }

    private void getWebLrc(final int id, final IOperationFinish finish) {
        LrcRequest.exeRequest(LrcRequest.generateUrl(id, 2), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg);
            }

            @Override
            public void response(Object object) {
                BaseListEntity listEntity = (BaseListEntity) object;
                ArrayList<Original> originalList = (ArrayList<Original>) listEntity.getData();
                for (Original original : originalList) {
                    original.setArticleID(id);
                }
                readAdapter.setDataSet(originalList);
                finish.finish();
            }
        });
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<ReadFragment> {
        @Override
        public void handleMessageByRef(final ReadFragment fragment, Message msg) {
            switch (msg.what) {
                case 0:
                    fragment.readAdapter.setDataSet((ArrayList<Original>) msg.obj);
                    break;
            }
        }
    }
}
