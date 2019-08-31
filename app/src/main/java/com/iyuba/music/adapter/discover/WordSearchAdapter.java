package com.iyuba.music.adapter.discover;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.entity.word.Word;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.widget.recycleview.RecycleViewHolder;
import com.iyuba.music.widget.view.MaterialRippleLayout;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/10.
 */
public class WordSearchAdapter extends RecyclerView.Adapter<WordSearchAdapter.MyViewHolder> {
    private ArrayList<Word> wordList;
    private Context context;
    private OnRecycleViewItemClickListener onRecycleViewItemClickListener;

    public WordSearchAdapter(Context context, ArrayList<Word> words) {
        this.context = context;
        wordList = words;
    }

    public void setOnItemClickLitener(OnRecycleViewItemClickListener onItemClickListener) {
        onRecycleViewItemClickListener = onItemClickListener;
    }

    public void setDataSet(ArrayList<Word> words) {
        wordList = words;
        notifyDataSetChanged();
    }

    public void removeData(int position) {
        wordList.remove(position);
        notifyItemRemoved(position);
    }

    public void removeData(int[] position) {
        for (int i : position) {
            wordList.remove(i);
            notifyItemRemoved(i);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_word, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final int pos = position;
        if (onRecycleViewItemClickListener != null) {
            holder.rippleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecycleViewItemClickListener.onItemClick(holder.itemView, pos);
                }
            });
        }
        holder.key.setText(wordList.get(position).getWord());
        holder.def.setText(wordList.get(position).getDef());
        if (ConfigManager.getInstance().isWordDefShow()) {
            holder.def.setVisibility(View.VISIBLE);
        } else {
            holder.def.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    static class MyViewHolder extends RecycleViewHolder {
        private TextView key, def;
        private MaterialRippleLayout rippleView;

        MyViewHolder(View view) {
            super(view);
            key = itemView.findViewById(R.id.word_key);
            def = itemView.findViewById(R.id.word_def);
            rippleView = itemView.findViewById(R.id.word_ripple);
        }
    }
}
