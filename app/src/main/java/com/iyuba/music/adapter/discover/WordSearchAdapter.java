package com.iyuba.music.adapter.discover;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.buaa.ct.core.adapter.CoreRecyclerViewAdapter;
import com.iyuba.music.R;
import com.iyuba.music.entity.word.Word;
import com.iyuba.music.manager.ConfigManager;

/**
 * Created by 10202 on 2015/10/10.
 */
public class WordSearchAdapter extends CoreRecyclerViewAdapter<Word, WordSearchAdapter.WordViewHolder> {
    public WordSearchAdapter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WordViewHolder(LayoutInflater.from(context).inflate(R.layout.item_word, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final WordViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.key.setText(getDatas().get(position).getWord());
        holder.def.setText(getDatas().get(position).getDef());
        if (ConfigManager.getInstance().isWordDefShow()) {
            holder.def.setVisibility(View.VISIBLE);
        } else {
            holder.def.setVisibility(View.GONE);
        }
    }

    static class WordViewHolder extends CoreRecyclerViewAdapter.MyViewHolder {
        private TextView key, def;

        WordViewHolder(View view) {
            super(view);
            key = itemView.findViewById(R.id.word_key);
            def = itemView.findViewById(R.id.word_def);
        }
    }
}
