package com.iyuba.music.adapter.discover;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.music.widget.view.MaterialRippleLayout;
import com.iyuba.music.adapter.expandable.Adapter.ExpandableRecyclerAdapter;
import com.iyuba.music.adapter.expandable.Model.ParentListItem;
import com.iyuba.music.adapter.expandable.ViewHolder.ChildViewHolder;
import com.iyuba.music.adapter.expandable.ViewHolder.ParentViewHolder;
import com.iyuba.music.R;
import com.iyuba.music.entity.word.Word;
import com.iyuba.music.entity.word.WordParent;
import com.iyuba.music.listener.OnExpandableRecycleViewClickListener;
import com.iyuba.music.manager.ConfigManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10202 on 2015/12/3.
 */
public class WordExpandableAdapter extends ExpandableRecyclerAdapter<WordExpandableAdapter.WordParentViewHolder, WordExpandableAdapter.WordChildViewHolder> {
    private LayoutInflater mInflater;
    private boolean deleteMode;
    private ArrayList<Word> tryTodeleteList;
    private OnExpandableRecycleViewClickListener itemClickListener;

    public WordExpandableAdapter(Context context, List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        this.deleteMode = false;
        tryTodeleteList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
    }

    public WordExpandableAdapter(Context context, boolean deleteMode, List<? extends ParentListItem> parentItemList) {
        super(parentItemList);
        this.deleteMode = deleteMode;
        tryTodeleteList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
    }

    public void setItemClickListener(OnExpandableRecycleViewClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public WordParentViewHolder onCreateParentViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item_word_parent, parent, false);
        return new WordParentViewHolder(view);
    }

    @Override
    public WordChildViewHolder onCreateChildViewHolder(ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item_word, parent, false);
        return new WordChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(final WordParentViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        final WordParent verticalParent = (WordParent) parentListItem;
        parentViewHolder.bind(verticalParent);
    }

    @Override
    public void onBindChildViewHolder(WordChildViewHolder childViewHolder, int position, Object childListItem) {
        Word verticalChild = (Word) childListItem;
        childViewHolder.bind(verticalChild);
    }

    private void deleteWord(Word word, CheckBox checkBox, boolean delete) {
        if (delete) {
            tryTodeleteList.add(word);
        } else {
            tryTodeleteList.remove(word);
        }
        checkBox.setChecked(delete);
    }

    public ArrayList<Word> getTryTodeleteList() {
        return tryTodeleteList;
    }

    class WordChildViewHolder extends ChildViewHolder {
        private TextView key, def;
        private CheckBox delete;
        private MaterialRippleLayout rippleView;

        WordChildViewHolder(View itemView) {
            super(itemView);
            key = (TextView) itemView.findViewById(R.id.word_key);
            def = (TextView) itemView.findViewById(R.id.word_def);
            delete = (CheckBox) itemView.findViewById(R.id.item_delete);
            rippleView = (MaterialRippleLayout) itemView.findViewById(R.id.word_ripple);
        }

        void bind(final Word word) {
            key.setText(word.getWord());
            def.setText(word.getDef());
            delete.setChecked(tryTodeleteList.contains(word));
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteWord(word, delete, delete.isChecked());
                }
            });
            if (ConfigManager.getInstance().isWordDefShow()) {
                def.setVisibility(View.VISIBLE);
            } else {
                def.setVisibility(View.GONE);
            }
            if (deleteMode) {
                delete.setVisibility(View.VISIBLE);
            } else {
                delete.setVisibility(View.GONE);
            }
            rippleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (deleteMode) {
                        deleteWord(word, delete, !delete.isChecked());
                    } else {
                        itemClickListener.onItemClick(rippleView, word.getWord());
                    }
                }
            });
        }
    }

    class WordParentViewHolder extends ParentViewHolder {
        private static final float INITIAL_POSITION = 0.0f;
        private static final float ROTATED_POSITION = 180f;
        private static final float PIVOT_VALUE = 0.5f;
        private static final long DEFAULT_ROTATE_DURATION_MS = 200;
        TextView parentContent;
        ImageView expandableStatus;

        /**
         * Public constructor for the CustomViewHolder.
         *
         * @param itemView the view of the parent item. Find/modify views using this.
         */
        WordParentViewHolder(View itemView) {
            super(itemView);
            parentContent = (TextView) itemView.findViewById(R.id.parent_content);
            expandableStatus = (ImageView) itemView.findViewById(R.id.expandable_status);
            expandableStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isExpanded()) {
                        collapseView();
                    } else {
                        expandView();
                    }
                }
            });

            expandableStatus.setBackgroundResource(R.drawable.expandable_state);
        }

        void bind(final WordParent parent) {
            parentContent.setText(parent.getParentContent());
        }

        @Override
        public void setExpanded(boolean expanded) {
            super.setExpanded(expanded);
            if (expanded) {
                expandableStatus.setRotation(ROTATED_POSITION);
            } else {
                expandableStatus.setRotation(INITIAL_POSITION);
            }
        }

        @Override
        public void onExpansionToggled(boolean expanded) {
            super.onExpansionToggled(expanded);
            RotateAnimation rotateAnimation = new RotateAnimation(ROTATED_POSITION,
                    INITIAL_POSITION,
                    RotateAnimation.RELATIVE_TO_SELF, PIVOT_VALUE,
                    RotateAnimation.RELATIVE_TO_SELF, PIVOT_VALUE);
            rotateAnimation.setDuration(DEFAULT_ROTATE_DURATION_MS);
            rotateAnimation.setFillAfter(true);
            expandableStatus.startAnimation(rotateAnimation);
        }
    }
}
