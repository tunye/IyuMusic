package com.iyuba.music.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.util.GetAppColor;

import java.util.ArrayList;
import java.util.List;

public class FlavorAdapter extends RecyclerView.Adapter<FlavorAdapter.FlavorViewHolder> {
    private List<String> showItems;
    private List<String> defItems;
    private OnItemClickListener mItemClickListener;
    private int currFlavor;
    private Context context;

    public FlavorAdapter(Context context) {
        this.context = context;
        showItems = new ArrayList<>();
        defItems = new ArrayList<>();
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setCurrentFlavor(int currentFlavor) {
        int previousIndex = currFlavor;
        notifyItemChanged(previousIndex);
        currFlavor = currentFlavor;
        notifyItemChanged(currentFlavor);
    }

    public void addAll(List<String> showItems, List<String> defItems) {
        this.showItems = showItems;
        this.defItems = defItems;
        notifyDataSetChanged();
    }

    @Override
    public FlavorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FlavorViewHolder(LayoutInflater.from(context).inflate(R.layout.item_layout_flavor, parent, false));
    }

    @Override
    public void onBindViewHolder(final FlavorViewHolder holder, int position) {
        final String item = defItems.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrentFlavor(holder.getAdapterPosition());
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClicked(v, item, holder.getAdapterPosition());
                }
            }
        });
        holder.title.setText(showItems.get(position));
        int primary, primaryDark, accent;
        if (position == 0) {
            primary = context.getResources().getColor(R.color.skin_app_color);
            primaryDark = context.getResources().getColor(R.color.skin_app_color_light);
            accent = context.getResources().getColor(R.color.skin_color_accent);
        } else {
            primary = context.getResources().getColor(GetAppColor.getResource("skin_app_color_" + item));
            primaryDark = context.getResources().getColor(GetAppColor.getResource("skin_app_color_light_" + item));
            accent = context.getResources().getColor(GetAppColor.getResource("skin_color_accent_" + item));
        }
        holder.primary.setBackgroundDrawable(generateDrawable(primary));
        holder.dark.setBackgroundDrawable(generateDrawable(primaryDark));
        holder.accent.setBackgroundDrawable(generateDrawable(accent));
        holder.indicator.setVisibility(position == currFlavor ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return showItems.size();
    }

    private ShapeDrawable generateDrawable(@ColorInt int color) {
        ShapeDrawable d = new ShapeDrawable(new OvalShape());
        d.setIntrinsicWidth(dipToPx(context, 24));
        d.setIntrinsicHeight(dipToPx(context, 24));
        d.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        return d;
    }

    private int dipToPx(Context ctx, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, ctx.getResources().getDisplayMetrics());
    }

    public interface OnItemClickListener {
        void onItemClicked(View view, String item, int position);
    }

    static class FlavorViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView primary, dark, accent;
        ImageView indicator;

        FlavorViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            primary = (ImageView) itemView.findViewById(R.id.primaryColor);
            dark = (ImageView) itemView.findViewById(R.id.primaryColorDark);
            accent = (ImageView) itemView.findViewById(R.id.accentColor);
            indicator = (ImageView) itemView.findViewById(R.id.indicator);
        }
    }
}
