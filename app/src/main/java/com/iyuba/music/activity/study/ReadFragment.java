package com.iyuba.music.activity.study;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyuba.music.R;
import com.iyuba.music.adapter.study.ReadAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.artical.Article;
import com.iyuba.music.entity.original.LrcParser;
import com.iyuba.music.entity.original.Original;
import com.iyuba.music.fragment.BaseFragment;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.LrcRequest;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.Dialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/12/17.
 */
public class ReadFragment extends BaseFragment implements IOnClickListener {
    private RecyclerView recyclerView;
    private ReadAdapter readAdapter;
    private Context context;
    private Article curArticle;
    private Dialog waittingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.recycleview, null);
        curArticle = StudyManager.instance.getCurArticle();
        waittingDialog = new WaitingDialog.Builder(context).setMessage(context.getString(R.string.read_loading)).create();
        getActivity().findViewById(R.id.toolbar).setOnTouchListener(new IOnDoubleClick(this, context.getString(R.string.list_double)));
        recyclerView = (RecyclerView) view.findViewById(R.id.listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        readAdapter = new ReadAdapter(context);
        recyclerView.setAdapter(readAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
        getOriginal();
        return view;
    }

    @Override
    public void onClick(View view, Object message) {
        recyclerView.scrollToPosition(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        readAdapter.onDestroy();
    }

    private void getOriginal() {
        if (LrcParser.getInstance().fileExist(curArticle.getId())) {
            LrcParser.getInstance().getOriginal(curArticle.getId(), new IOperationResult() {
                @Override
                public void success(Object object) {
                    readAdapter.setDataSet((ArrayList<Original>) object);
                }

                @Override
                public void fail(Object object) {

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
        LrcRequest.getInstance().exeRequest(LrcRequest.getInstance().generateUrl(id, 2), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.INSTANCE.showToast(msg);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.INSTANCE.showToast(msg);
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
}
