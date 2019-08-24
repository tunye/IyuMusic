package com.iyuba.music.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buaa.ct.appskin.SkinManager;
import com.iyuba.music.R;
import com.iyuba.music.activity.LoginActivity;
import com.iyuba.music.activity.MainActivity;
import com.iyuba.music.activity.SettingActivity;
import com.iyuba.music.activity.SkinActivity;
import com.iyuba.music.activity.SleepActivity;
import com.iyuba.music.activity.discover.DiscoverActivity;
import com.iyuba.music.activity.me.MeActivity;
import com.iyuba.music.activity.me.WriteStateActivity;
import com.iyuba.music.ground.AppGroundActivity;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.receiver.ChangePropertyBroadcast;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.Mathematics;
import com.iyuba.music.widget.dialog.SignInDialog;
import com.iyuba.music.widget.imageview.GoImageView;

import java.util.ArrayList;


/**
 * Created by 10202 on 2015/10/10.
 */
public class OperAdapter extends RecyclerView.Adapter<OperAdapter.OperViewHolder> {
    private static final ArrayList<Integer> menuTextList;
    private static final ArrayList<Integer> menuIconList;
    private SignInDialog signInDialog;

    static {
        menuIconList = new ArrayList<>(12);
        menuTextList = new ArrayList<>(12);

        menuIconList.add(R.drawable.sign_in_icon);
        menuIconList.add(R.drawable.personal_state);
        menuIconList.add(R.drawable.ground_icon);
        menuIconList.add(R.drawable.discover_icon);
        menuIconList.add(R.drawable.me_icon);
        menuIconList.add(R.drawable.night_icon);
        menuIconList.add(R.drawable.sleep_icon);
        menuIconList.add(R.drawable.skin_icon);
        menuIconList.add(R.drawable.setting_icon);

        menuTextList.add(R.string.oper_sign_in);
        menuTextList.add(R.string.oper_personal_state);
        menuTextList.add(R.string.oper_ground);
        menuTextList.add(R.string.oper_discover);
        menuTextList.add(R.string.oper_me);
        menuTextList.add(R.string.oper_night);
        menuTextList.add(R.string.oper_sleep);
        menuTextList.add(R.string.oper_skin);
        menuTextList.add(R.string.oper_setting);
    }

    public OperAdapter() {
    }

    @Override
    public int getItemCount() {
        return menuIconList.size();
    }

    @Override
    public OperAdapter.OperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OperAdapter.OperViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_operlist, parent, false));
    }

    @Override
    public void onBindViewHolder(final OperViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClicked(v.getContext(), holder.getAdapterPosition());
            }
        });
        holder.menuText.setText(RuntimeManager.getString(menuTextList.get(position)));
        holder.menuIcon.setImageResource(menuIconList.get(position));
        if (holder.getAdapterPosition() == 0) {
            holder.menuResult.setVisibility(View.VISIBLE);
            if (!AccountManager.getInstance().checkUserLogin()) {
                holder.go.setVisibility(View.GONE);
                holder.menuText.setText(R.string.oper_visitor);
                holder.menuResult.setText(R.string.oper_visitor_handle);
            } else {
                holder.go.setVisibility(View.VISIBLE);
            }
        }
        if (holder.getAdapterPosition() == 1) {
            holder.go.setVisibility(View.GONE);
            String state = AccountManager.getInstance().getUserInfo().getText();
            if (TextUtils.isEmpty(state) || "null".equals(state)) {
                holder.menuText.setText(R.string.personal_nosign);
            } else {
                holder.menuText.setText(state);
            }
        }
        if (holder.menuText.getText().equals(RuntimeManager.getString(R.string.oper_night))) {
            holder.go.setVisibility(View.GONE);
            holder.menuResult.setVisibility(View.VISIBLE);
            holder.menuResult.setText(ConfigManager.getInstance().isNight() ? R.string.oper_night_on : R.string.oper_night_off);
        }
        if (holder.menuText.getText().equals(RuntimeManager.getString(R.string.oper_sleep))) {
            holder.go.setVisibility(View.GONE);
            holder.menuResult.setVisibility(View.VISIBLE);
            int sleepSecond = RuntimeManager.getApplication().getSleepSecond();
            if (sleepSecond == 0) {
                holder.menuResult.setText(R.string.sleep_no_set);
            } else {
                holder.menuResult.setText(Mathematics.formatTime(sleepSecond));
            }
        }
        if (holder.menuText.getText().equals(RuntimeManager.getString(R.string.oper_skin))) {
            holder.go.setVisibility(View.GONE);
            holder.menuResult.setVisibility(View.VISIBLE);
            holder.menuResult.setText(getSkin(holder.menuResult.getContext(), SkinManager.getInstance().getCurrSkin()));
        }
    }

    private void onItemClicked(final Context context, int position) {
        switch (position) {
            case 0:
                if (AccountManager.getInstance().checkUserLogin()) {
                    signInDialog = new SignInDialog(context);
                    signInDialog.show();
                } else {
                    ((Activity) context).startActivityForResult(new Intent(context, LoginActivity.class), 101);
                }
                break;
            case 1:
                if (AccountManager.getInstance().checkUserLogin()) {
                    context.startActivity(new Intent(context, WriteStateActivity.class));
                } else {
                    ((Activity) context).startActivityForResult(new Intent(context, LoginActivity.class), 101);
                }
                break;
            case 2:
                context.startActivity(new Intent(context, AppGroundActivity.class));
                break;
            case 3:
                context.startActivity(new Intent(context, DiscoverActivity.class));
                break;
            case 4:
                context.startActivity(new Intent(context, MeActivity.class));
                break;
            case 5:
                ConfigManager.getInstance().setNight(!ConfigManager.getInstance().isNight());
                ChangePropery.updateNightMode(ConfigManager.getInstance().isNight());
                Intent intent = new Intent(ChangePropertyBroadcast.FLAG);
                intent.putExtra(ChangePropertyBroadcast.SOURCE, MainActivity.class.getSimpleName());
                context.sendBroadcast(intent);
                break;
            case 6:
                context.startActivity(new Intent(context, SleepActivity.class));
                break;
            case 7:
                context.startActivity(new Intent(context, SkinActivity.class));
                break;
            case 8:
                context.startActivity(new Intent(context, SettingActivity.class));
                break;
            default:
                break;
        }
    }

    private String getSkin(Context context, String skin) {
        int pos = GetAppColor.getInstance().getSkinFlg(skin);
        String[] skins = context.getResources().getStringArray(R.array.flavors);
        return skins[pos];
    }

    public boolean onBackPressed() {
        if (signInDialog.isShown()) {
            signInDialog.dismiss();
            return true;
        }
        return false;
    }

    static class OperViewHolder extends RecyclerView.ViewHolder {

        TextView menuText, menuResult;
        ImageView menuIcon;
        GoImageView go;

        OperViewHolder(View itemView) {
            super(itemView);
            menuText = (TextView) itemView.findViewById(R.id.oper_text);
            menuIcon = (ImageView) itemView.findViewById(R.id.oper_icon);
            menuResult = (TextView) itemView.findViewById(R.id.oper_result);
            go = (GoImageView) itemView.findViewById(R.id.oper_go);
        }
    }
}
