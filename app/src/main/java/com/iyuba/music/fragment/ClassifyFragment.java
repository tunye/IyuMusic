package com.iyuba.music.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.iyuba.music.R;
import com.iyuba.music.activity.main.ClassifyNewsList;
import com.iyuba.music.activity.main.DownloadSongActivity;
import com.iyuba.music.activity.main.FavorSongActivity;
import com.iyuba.music.activity.main.ListenSongActivity;
import com.iyuba.music.adapter.study.ClassifyAdapter;
import com.iyuba.music.widget.recycleview.StationaryGridview;

/**
 * Created by 10202 on 2016/3/4.
 */
public class ClassifyFragment extends BaseFragment {
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.classify, null);
        StationaryGridview person = (StationaryGridview) view.findViewById(R.id.person_view);
        StationaryGridview system = (StationaryGridview) view.findViewById(R.id.system_view);
        ClassifyAdapter personAdapter = new ClassifyAdapter(context, 0);
        final ClassifyAdapter systemAdapter = new ClassifyAdapter(context, 1);
        person.setAdapter(personAdapter);
        system.setAdapter(systemAdapter);
        person.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        system.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, ClassifyNewsList.class);
                intent.putExtra("classify", position + 1);
                intent.putExtra("classifyName", systemAdapter.getItem(position));
                startActivity(intent);
            }
        });
        person.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(context, DownloadSongActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(context, FavorSongActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(context, ListenSongActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });
        return view;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
