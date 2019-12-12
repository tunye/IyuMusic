package com.iyuba.music.activity.study;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buaa.ct.core.listener.IOnDoubleClick;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.util.ThreadPoolUtil;
import com.buaa.ct.core.util.ThreadUtils;
import com.iyuba.music.R;
import com.iyuba.music.adapter.study.ReadAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.original.LrcParser;
import com.iyuba.music.entity.original.Original;
import com.iyuba.music.fragment.BaseRecyclerViewFragment;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.LrcRequest;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10202 on 2015/12/17.
 */
public class ReadFragment extends BaseRecyclerViewFragment<Original> {
    private Article curArticle;
    private IyubaDialog waittingDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        enableSwipeWidget = false;
        curArticle = StudyManager.getInstance().getCurArticle();
        waittingDialog = WaitingDialog.create(context, context.getString(R.string.read_loading));
        ownerAdapter = new ReadAdapter(context);
        assembleRecyclerView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getOriginal();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((ReadAdapter) ownerAdapter).onDestroy();
    }

    private void getOriginal() {
        if (LrcParser.getInstance().fileExist(curArticle.getId())) {
            ThreadPoolUtil.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    LrcParser.getInstance().getOriginal(curArticle.getId(), new IOperationResult() {
                        @Override
                        public void success(final Object object) {
                            ThreadUtils.postOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ownerAdapter.setDataSet((ArrayList<Original>) object);
                                }
                            });
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
        RequestClient.requestAsync(new LrcRequest(id, 2), new SimpleRequestCallBack<BaseListEntity<List<Original>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<Original>> listEntity) {
                List<Original> originalList = listEntity.getData();
                for (Original original : originalList) {
                    original.setArticleID(id);
                }
                ownerAdapter.setDataSet(originalList);
                finish.finish();
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {

            }
        });
    }
}
